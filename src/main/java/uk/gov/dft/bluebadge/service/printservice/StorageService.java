package uk.gov.dft.bluebadge.service.printservice;

import static uk.gov.dft.bluebadge.service.printservice.utils.S3Utils.getBucket;
import static uk.gov.dft.bluebadge.service.printservice.utils.S3Utils.getKey;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
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

  public boolean upload(String src, String fileName) throws IOException, InterruptedException {

    log.info("Uploading document to S3. Payload: {}", src);

    String keyName = UUID.randomUUID().toString() + "-" + fileName;
    keyName = URLEncoder.encode(keyName, ENCODING_CHAR_SET);

    amazonS3.putObject(s3Config.getS3Bucket(), keyName, src);

    return amazonS3.doesObjectExist(s3Config.getS3Bucket(), keyName);
  }

  public List<String> listFiles() {
    ObjectListing result = amazonS3.listObjects(s3Config.getS3Bucket());
    List<S3ObjectSummary> summaries = result.getObjectSummaries();
    List<String> files =
        summaries
            .stream()
            .filter(f -> f.getKey().endsWith(".json"))
            .map(f -> f.getKey())
            .collect(Collectors.toList());

    return files;
  }

  public Optional<String> downloadFile(String bucket, String key) {
    if (amazonS3.doesObjectExist(bucket, key)) {
      log.debug("json file: {} exists in s3 bucket {}", key, bucket);
      return Optional.of(amazonS3.getObjectAsString(bucket, key));
    }
    return Optional.empty();
  }

  public Optional<byte[]> downloadFile(String url) throws IOException {

    Optional<String> bucket = getBucket(url);
    Optional<String> key = getKey(url);
    if (bucket.isPresent() && key.isPresent()) {
      InputStream is = null;
      try {
        is = amazonS3.getObject(bucket.get(), key.get()).getObjectContent();
        return Optional.of(IOUtils.toByteArray(is));
      } finally {
        if (null != is) {
          is.close();
        }
      }
    }

    return Optional.empty();
  }

  public void deleteFile(String key) {
    amazonS3.deleteObject(s3Config.getS3Bucket(), key);
  }

  private URL generateSignedS3Url(String link) {
    if (null == link) {
      return null;
    }
    long expTimeMillis = System.currentTimeMillis() + s3Config.getSignedUrlDurationMs();
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(s3Config.getS3Bucket(), link)
            .withMethod(HttpMethod.GET)
            .withExpiration(new Date(expTimeMillis));
    return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
  }

  public String getBucketName() {
    return s3Config.getS3Bucket();
  }
}
