package uk.gov.dft.bluebadge.service.printservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.xml.stream.XMLStreamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import uk.gov.dft.bluebadge.service.printservice.config.S3Config;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.utils.ModelToXmlConverter;

@Service
@Slf4j
public class PrintService {

  private static final Path XML_DIR =
      Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");

  private final StorageService s3;
  private final FTPService ftp;
  private final ModelToXmlConverter xmlConverter;

  @Autowired private S3Config s3Config;

  PrintService(StorageService s3, FTPService ftp, ModelToXmlConverter xmlConverter) {
    this.s3 = s3;
    this.ftp = ftp;
    this.xmlConverter = xmlConverter;
  }

  public void print(Batch batch) {

    boolean uploaded = false;
    try {
      uploaded = uploadToS3(batch);
    } catch (IOException e) {
      log.error("Can't upload file to s3", e);
    }

    if (uploaded) {
      log.info("Batch uploaded to S3, processing.");
      processBatches();
    }
  }

  private boolean uploadToS3(Batch batch) throws IOException {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    mapper.registerModule(new JavaTimeModule());

    String json = mapper.writeValueAsString(batch);

    String filename =
        batch.getBatchType()
            + "_"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"))
            + ".json";

    boolean uploaded = s3.uploadToPrinterBucket(json, filename);
    log.debug("Json payload {} has been uploaded: {}", json, uploaded);

    return uploaded;
  }

  private boolean processBatches() {
    boolean success = true;
    try {
      List<String> files = s3.listPrinterBucketFiles();
      log.info("Processing {} files", files.size());
      for (String file : files) {
        log.debug("Downloading file: {} from s3 printer bucket", file);
        success &= processBatch(file);
      }
    } catch (Exception e) {
      log.error("Error while processing badges:" + e.getMessage(), e);
      return false;
    }

    return success;
  }

  @Async("batchExecutor")
  private boolean processBatch(String key) {

    Optional<String> json;
    try {
      json = s3.downloadPrinterFileAsString(key);
    } catch (Exception e) {
      log.error("Can't download file: {} from s3 bucket: {}", key, s3Config.getS3PrinterBucket());
      log.error("Error while downloading: {}", e);
      return false;
    }

    String xmlFileName;
    if (json.isPresent()) {
      try {
        xmlFileName = prepareXml(json.get());
      } catch (IOException | XMLStreamException e) {
        log.error("Can't process json string: {} into valid xml", json.get());
        log.error("Error while converting into xml: {}", e);
        return false;
      }
    } else {
      log.error("Can't download file: {} from s3", key);
      return false;
    }

    boolean transferred = false;
    try {
      transferred = ftp.send(xmlFileName);
    } catch (Exception e) {
      log.error("Can't send file: {} ftp", xmlFileName);
      log.error("Error while sending file to ftp: {}", e.getMessage());
    }

    if (transferred) {
      s3.deletePrinterBucketFile(key);
    }
    cleanTempResources();

    return transferred;
  }

  private String prepareXml(String json) throws IOException, XMLStreamException {
    log.debug("Prepare xml file");

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    Batch batch = objectMapper.readValue(json, Batch.class);

    return xmlConverter.toXml(batch, XML_DIR);
  }

  private void cleanTempResources() {
    try {
      FileSystemUtils.deleteRecursively(XML_DIR);
    } catch (IOException e) {
      log.error("Error while deleting local temporary resources: {}", e.getMessage());
    }
  }
}
