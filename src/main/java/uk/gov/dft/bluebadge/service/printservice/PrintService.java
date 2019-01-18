package uk.gov.dft.bluebadge.service.printservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import uk.gov.dft.bluebadge.service.printservice.converters.PrintRequestToPrintXml;
import uk.gov.dft.bluebadge.service.printservice.converters.PrintResultXmlConversionException;
import uk.gov.dft.bluebadge.service.printservice.converters.PrintResultXmlToProcessedBatchResponse;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatch;
import uk.gov.dft.bluebadge.service.printservice.referencedata.ReferenceDataService;

@Service
@Slf4j
class PrintService {

  private static final Path XML_DIR =
      Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");

  private final StorageService s3;
  private final FTPService ftp;
  private final PrintRequestToPrintXml xmlConverter;
  private final PrintResultXmlToProcessedBatchResponse xmlToProcessedBatch;
  private ObjectMapper mapper;
  private ReferenceDataService referenceDataService;

  PrintService(
      StorageService s3,
      FTPService ftp,
      PrintRequestToPrintXml xmlConverter,
      PrintResultXmlToProcessedBatchResponse xmlToProcessedBatch,
      ObjectMapper mapper,
      ReferenceDataService referenceDataService) {
    this.s3 = s3;
    this.ftp = ftp;
    this.xmlConverter = xmlConverter;
    this.xmlToProcessedBatch = xmlToProcessedBatch;
    this.mapper = mapper;
    this.referenceDataService = referenceDataService;
  }

  /**
   * Returns printing results. Either successful, CONFIRMATION type batches or REJECTIONs. Results
   * received as XML files from S3 and parsed into List of ProcessedBatch pojos.
   *
   * @return List of processing results (from printing company) for previously submitted print
   *     requests.
   */
  List<ProcessedBatch> getProcessedBatches() {
    log.info("Returning processed batches.");
    List<ProcessedBatch> processedBatches = new ArrayList<>();
    List<String> files = s3.listInBucketXmlFiles();
    int successCount = 0;

    for (String file : files) {
      log.info("Parsing {}", file);
      try (InputStream is = s3.downloadS3File(s3.getInBucket(), file)) {
        processedBatches.add(xmlToProcessedBatch.readProcessedBatchFile(is, file));
        successCount++;
      } catch (PrintResultXmlConversionException e) {
        processedBatches.add(
            ProcessedBatch.builder().filename(file).errorMessage(e.getDetailedError()).build());
      } catch (Exception e) {
        // Catch, log and create a response for any unexpected exceptions and then carry on processing.
        // Possible causes: Invalid date format, S3 problem.
        log.error("Unexpected exception parsing file:" + file + ":" + e.getMessage(), e);
        processedBatches.add(
            ProcessedBatch.builder().filename(file).errorMessage(e.getMessage()).build());
      }
    }
    log.info(
        "Processed {} batch file(s), {} successful, {} failed.",
        files.size(),
        successCount,
        files.size() - successCount);
    return processedBatches;
  }

  void storePrintBatchInS3(Batch batch) throws IOException {
    String json = mapper.writeValueAsString(batch);

    boolean uploaded = s3.uploadToPrinterBucket(json, batch.getFilename() + ".json");
    log.debug("Json payload for batch {}, has been uploaded: {}", batch.getFilename(), uploaded);
  }

  @Async("batchExecutor")
  synchronized void processPrintBatches() {
    // Get list of batches to process.
    List<String> s3Keys;
    try {
      s3Keys = s3.listPrinterBucketFiles();
    } catch (Exception e) {
      // Can't continue processing.
      log.error("Failed listing s3 files while processing print batchs ", e);
      return;
    }

    // Process each batch, if 1 fails, continue with the rest.
    log.info("Processing {} batches", s3Keys.size());
    for (String file : s3Keys) {
      try {
        processPrintBatch(file);
      } catch (Exception e) {
        // Any exception, carry on and try next batch.
        log.error("Failed processing " + file, e);
      }
    }
  }

  private void processPrintBatch(String key) {

    log.info("Processing print batch {}", key);
    Optional<String> json;
    try {
      json = s3.downloadPrinterFileAsString(key);
      if (!json.isPresent()) {
        log.error("Can't download file: {} from s3", key);
        return;
      }
    } catch (Exception e) {
      log.error("Error while downloading from s3 for: " + key, e);
      return;
    }

    String xmlFileName;
    try {
      Batch batch = mapper.readValue(json.get(), Batch.class);
      xmlFileName = xmlConverter.toXml(batch, XML_DIR, referenceDataService);
    } catch (Exception e) {
      log.error("Error while converting into xml:" + e.getMessage() + ", File:" + key, e);
      return;
    }
    log.info("Xml created for {}, beginning sftp.", xmlFileName);

    if (ftp.send(xmlFileName)) {
      try {
        log.info("Sftp complete for {}, removing batch from S3.", xmlFileName);
        s3.deletePrinterBucketFile(key);
        log.info("Processing of batch {} completed successfully.", key);
      } catch (Exception e) {
        // File sent to printers, but batch not deleted.
        // This could result in it being resent.
        log.error(
            "File "
                + xmlFileName
                + " successfully sftp'd to printer, but removal from s3 of "
                + key
                + " failed. This could result in the file being resent to printer.");
      }
    }
    cleanTempResources();
  }

  private void cleanTempResources() {
    try {
      FileSystemUtils.deleteRecursively(XML_DIR);
    } catch (IOException e) {
      log.warn("Error while deleting local temporary resources: {}", e.getMessage());
    }
  }

  boolean deleteBatchConfirmation(String batchName) {
    log.info("Deleting batch {}", batchName);
    return s3.deleteS3FileByKey(s3.getInBucket(), batchName);
  }

  void initReferenceData() {
    // Get a populated reference data instance whilst request is active and auth token available.
    // Required for async processing, where no request.
    // Also refresh the ref data.
    referenceDataService.init(true);
  }
}
