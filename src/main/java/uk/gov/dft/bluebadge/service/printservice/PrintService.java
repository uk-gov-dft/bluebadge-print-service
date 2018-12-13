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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
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

  public PrintService(StorageService s3, FTPService ftp, ModelToXmlConverter xmlConverter) {
    this.s3 = s3;
    this.ftp = ftp;
    this.xmlConverter = xmlConverter;
  }

  public void print(Batch batch) {

    boolean uploaded = false;
    try {
      uploaded = uploadToS3(batch);
    } catch (IOException | InterruptedException e) {
      log.error("Can't upload file to s3: {}", e.getMessage());
    }

    if (uploaded) {
      processBatches();
    }
  }

  private boolean uploadToS3(Batch batch) throws IOException, InterruptedException {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    mapper.registerModule(new JavaTimeModule());

    String json = mapper.writeValueAsString(batch);

    String filename =
        batch.getBatchType()
            + "_"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"))
            + ".json";

    boolean uploaded = s3.upload(json, filename);
    log.debug("Json payload {} has been uploaded: {}", json, uploaded);

    return uploaded;
  }

  private boolean processBatches() {
    boolean success = true;
    try {
      List<String> files = s3.listFiles();
      for (String file : files) {
        log.debug("Downloading file: {} from s3 bucket: {}", file, s3.getBucketName());
        success &= processBatch(file);
      }
    } catch (Exception e) {
      log.error("Error while processing badges", e.getMessage());
    }

    return success;
  }

  @Async("batchExecutor")
  private boolean processBatch(String key) {
    String bucket = s3.getBucketName();
    Optional<String> json;
    try {
      json = s3.downloadFile(bucket, key);
    } catch (Exception e) {
      log.error("Can't download file: {} from s3 bucket: {}", key, bucket);
      log.error("Error while downloading: {}", e.getMessage());
      return false;
    }

    String xmlFileName = null;
    if (json.isPresent()) {
      try {
        xmlFileName = prepareXml(json.get());
      } catch (IOException | XMLStreamException e) {
        log.error("Can't process json string: {} into valid xml", json.get());
        log.error("Error while converting into xml: {}", e.getMessage());
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
      s3.deleteFile(key);
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
