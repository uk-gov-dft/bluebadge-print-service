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

  private static final String ENCODING_CHAR_SET = "UTF-8";
  private final AmazonS3 amazonS3;
  private final S3Config s3Config;

  StorageService(S3Config s3Config, AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
    this.s3Config = s3Config;
  }

  boolean uploadToPrinterBucket(String src, String fileName) throws IOException {

    log.info("Uploading document to S3.  FileName:{}, Payload: {}", fileName, src);

    String keyName = UUID.randomUUID().toString() + "-" + fileName;
    keyName = URLEncoder.encode(keyName, ENCODING_CHAR_SET);

    amazonS3.putObject(s3Config.getS3PrinterBucket(), keyName, src);

    return amazonS3.doesObjectExist(s3Config.getS3PrinterBucket(), keyName);
  }

  List<String> listPrinterBucketFiles() {
    ObjectListing result = amazonS3.listObjects(s3Config.getS3PrinterBucket());
    List<S3ObjectSummary> summaries = result.getObjectSummaries();

    return summaries
        .stream()
        .filter(f -> f.getKey().endsWith(".json"))
        .map(S3ObjectSummary::getKey)
        .collect(Collectors.toList());
  }

  List<String> listInBucketXmlFiles() {
    ObjectListing result = amazonS3.listObjects(s3Config.getS3InBucket());
    List<S3ObjectSummary> summaries = result.getObjectSummaries();

    return summaries
        .stream()
        .filter(f -> f.getKey().endsWith(".xml"))
        .map(S3ObjectSummary::getKey)
        .collect(Collectors.toList());
  }

  Optional<String> downloadPrinterFileAsString(String key) {
    if (amazonS3.doesObjectExist(s3Config.getS3PrinterBucket(), key)) {
      log.debug("json file: {} exists in s3 bucket {}", key, s3Config.getS3PrinterBucket());
      return Optional.of(amazonS3.getObjectAsString(s3Config.getS3PrinterBucket(), key));
    }
    return Optional.empty();
  }

  InputStream downloadS3File(String bucket, String key) {
    return amazonS3.getObject(bucket, key).getObjectContent();
  }

  public Optional<byte[]> downloadBadgeFile(String key) throws IOException {

    try (InputStream is = downloadS3File(s3Config.getS3BadgeBucket(), key)) {
      return Optional.of(IOUtils.toByteArray(is));
    }
  }

  void deletePrinterBucketFile(String key) {
    amazonS3.deleteObject(s3Config.getS3PrinterBucket(), key);
  }
  
  public String getPrinterBucket() {
  		return s3Config.getS3PrinterBucket();
  }

  public String getInBucket() {
		return s3Config.getS3InBucket();
}
}
