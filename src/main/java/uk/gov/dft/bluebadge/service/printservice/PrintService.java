package uk.gov.dft.bluebadge.service.printservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dft.bluebadge.model.printservice.generated.Batch;
import uk.gov.dft.bluebadge.model.printservice.generated.Batches;

@Service
@Slf4j
public class PrintService {

  private final StorageService s3;

  public PrintService(StorageService s3) {
    this.s3 = s3;
  }

  public void print(Batches batch) throws IOException, InterruptedException {
    List<File> jsonBatchFiles = convertAndSave(batch);

    for (File jsonFile : jsonBatchFiles) {
      try {
        URL s3URL = s3.upload(jsonFile);
        log.debug("Json file {} has been uploaded, URL: {}", jsonFile.getName(), s3URL.toString());
      } finally {
        boolean deleted = jsonFile.delete();
        log.debug(
            "Json file {} {} been deleted from temporary folder",
            jsonFile.getName(),
            deleted ? "has" : "hasn't");
      }
    }
  }

  private List<File> convertAndSave(Batches src) throws IOException {
    List<File> list = new ArrayList<>();
    src.stream()
        .forEach(
            batch -> {
              try {
                list.add(saveFile(batch));
              } catch (Exception e) {
                log.error("Couldn't convert/save batch {} into json file", batch.toString());
              }
            });

    return list;
  }

  private File saveFile(Batch batch) throws Exception {
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    String filename =
        System.getProperty("java.io.tmpdir")
            + "printbatch_"
            + batch.getBatchType()
            + "_"
            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"))
            + ".json";
    log.debug("Convert and save batches payload to temporary json file {}", filename);
    File jsonFile = new File(filename);
    mapper.writeValue(jsonFile, batch);
    return jsonFile;
  }
}
