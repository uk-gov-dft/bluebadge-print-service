package uk.gov.dft.bluebadge.service.printservice;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dft.bluebadge.service.printservice.config.S3Config;

@Service
@Slf4j
public class StorageService {

  public static final String ENCODING_CHAR_SET = "UTF-8";
  private final AmazonS3 amazonS3;
  private final S3Config s3Config;

  public StorageService(S3Config s3Config, AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
    this.s3Config = s3Config;
  }

  public boolean uploadToPrinterBucket(String src, String fileName)
      throws IOException, InterruptedException {

    log.info("Uploading document to S3. Payload: {}", src);

    String keyName = UUID.randomUUID().toString() + "-" + fileName;
    keyName = URLEncoder.encode(keyName, ENCODING_CHAR_SET);

    amazonS3.putObject(s3Config.getS3PrinterBucket(), keyName, src);

    return amazonS3.doesObjectExist(s3Config.getS3PrinterBucket(), keyName);
  }

  public List<String> listPrinterBucketFiles() {
    ObjectListing result = amazonS3.listObjects(s3Config.getS3PrinterBucket());
    List<S3ObjectSummary> summaries = result.getObjectSummaries();
    List<String> files =
        summaries
            .stream()
            .filter(f -> f.getKey().endsWith(".json"))
            .map(f -> f.getKey())
            .collect(Collectors.toList());

    return files;
  }

  public Optional<String> downloadPrinterFileAsString(String key) {
    if (amazonS3.doesObjectExist(s3Config.getS3PrinterBucket(), key)) {
      log.debug("json file: {} exists in s3 bucket {}", key, s3Config.getS3PrinterBucket());
      return Optional.of(amazonS3.getObjectAsString(s3Config.getS3PrinterBucket(), key));
    }
    return Optional.empty();
  }

  public Optional<byte[]> downloadBadgeFile(String key) throws IOException {

    try (InputStream is = amazonS3.getObject(s3Config.getS3BadgeBucket(), key).getObjectContent()) {
      return Optional.of(IOUtils.toByteArray(is));
    }
  }

  void deletePrinterBucketFile(String key) {
    amazonS3.deleteObject(s3Config.getS3PrinterBucket(), key);
  }
}
