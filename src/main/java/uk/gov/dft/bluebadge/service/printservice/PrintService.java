package uk.gov.dft.bluebadge.service.printservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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

  private static final Path SRC_DIR =
      Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_src");
  private static final Path JSON_DIR =
      Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_json");
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
    File jsonFile = null;
    try {
      jsonFile = convertAndSave(batch);
    } catch (IOException e) {
      log.error("Can't process and save payload");
      log.error("Error while processing payload: {}", e.getMessage());
    }

    boolean uploaded = false;
    try {
      uploaded = uploadJsonToS3(jsonFile);
    } catch (IOException | InterruptedException e) {
      log.error("Can't upload file to s3: {}", e.getMessage());
    }

    if (uploaded) {
      processBatches();
    }
  }

  private boolean uploadJsonToS3(File jsonFile) throws IOException, InterruptedException {
    URL s3URL = null;
    try {
      s3URL = s3.upload(jsonFile);
      log.debug("Json file {} has been uploaded, URL: {}", jsonFile.getName(), s3URL);
    } finally {
      boolean deleted = jsonFile.delete();
      log.debug(
          "Json file {} {} been deleted from temporary folder",
          jsonFile.getName(),
          deleted ? "has" : "hasn't");
    }

    return s3URL != null;
  }

  private File convertAndSave(Batch batch) throws IOException {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    Path path =
        SRC_DIR.resolve(
            batch.getBatchType()
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"))
                + ".json");

    log.debug("Convert and save batches payload to temporary json file {}", path.toString());
    Files.createDirectories(path.getParent());
    File jsonFile = Files.createFile(path).toFile();

    mapper.writeValue(jsonFile, batch);
    return jsonFile;
  }

  private boolean processBatches() {
    boolean success = true;
    try {
      ftp.connect();
      List<String> files = s3.listFiles();
      for (String file : files) {
        success &= processBatch(file);
      }
    } catch (Exception e) {
      log.error("Error while processing badges", e.getMessage());
    } finally {
      try {
        ftp.disconnect();
      } catch (Exception e) {
        log.error("Error while disconnecting from ftp: {}", e.getMessage());
      }
    }

    return success;
  }

  @Async("batchExecutor")
  private boolean processBatch(String key) {
    String bucket = s3.getBucketName();
    Optional<File> file;
    try {
      file = s3.downloadFile(bucket, key, JSON_DIR);
    } catch (IOException e) {
      log.error("Can't download file: {} from s3 bucket: {}", key, bucket);
      log.error("Error while downloading: {}", e.getMessage());
      return false;
    }

    String xmlFileName = null;
    if (file.isPresent()) {
      try {
        xmlFileName = prepareXml(file.get());
      } catch (IOException | XMLStreamException e) {
        log.error("Can't process file: {} into valid xml", file.get().getAbsolutePath());
        log.error("Error while converting into xml: {}", e.getMessage());
        return false;
      }
    } else {
      log.error("Can't download file: {} from s3", file.get().getAbsolutePath());
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

  private String prepareXml(File file) throws IOException, XMLStreamException {
    log.debug("Starting sending xml file to sftp");

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    String json = readFile(file);
    Batch batch = objectMapper.readValue(json, Batch.class);

    return xmlConverter.toXml(batch, XML_DIR);
  }

  private String readFile(File file) throws IOException {
    return new String(Files.readAllBytes(file.toPath()));
  }

  private void cleanTempResources() {
    try {
      FileSystemUtils.deleteRecursively(XML_DIR);
      FileSystemUtils.deleteRecursively(JSON_DIR);
      FileSystemUtils.deleteRecursively(SRC_DIR);
    } catch (IOException e) {
      log.error("Error while deleting local temporary resources: {}", e.getMessage());
    }
  }
}
