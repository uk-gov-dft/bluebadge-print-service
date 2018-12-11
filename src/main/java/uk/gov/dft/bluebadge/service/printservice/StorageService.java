package uk.gov.dft.bluebadge.service.printservice;

import static uk.gov.dft.bluebadge.service.printservice.utils.S3Utils.getBucket;
import static uk.gov.dft.bluebadge.service.printservice.utils.S3Utils.getKey;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
  private final TransferManager transferManager;

  public StorageService(S3Config s3Config, AmazonS3 amazonS3, TransferManager transferManager) {
    this.amazonS3 = amazonS3;
    this.s3Config = s3Config;
    this.transferManager = transferManager;
  }

  public URL upload(File file) throws IOException, InterruptedException {
    Objects.requireNonNull(file, "File is null");
    if (file.length() == 0) {
      throw new IllegalArgumentException("Upload failed. Batch file is empty");
    }

    log.info("Uploading document to S3. {}, size:{}", file.getName(), file.length());

    String keyName = UUID.randomUUID().toString() + "-" + file.getName();
    keyName = URLEncoder.encode(keyName, ENCODING_CHAR_SET);

    Upload upload =
        transferManager.upload(
            s3Config.getS3Bucket(), keyName, new FileInputStream(file), setMetaData(file));
    UploadResult uploadResult = upload.waitForUploadResult();
    // URL url = amazonS3.getUrl(uploadResult.getBucketName(),
    // uploadResult.getKey());

    return generateSignedS3Url(uploadResult.getKey());
  }

  public File downloadBucket() {
    File tempDir =
        Paths.get(
                System.getProperty("java.io.tmpdir"),
                "printbatch_"
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss")))
            .toFile();
    tempDir.mkdirs();

    transferManager.downloadDirectory(s3Config.getS3Bucket(), null, tempDir, true);
    return tempDir;
  }

  public List<String> listFiles() {
    ObjectListing result = amazonS3.listObjects(s3Config.getS3Bucket());
    List<S3ObjectSummary> summaries = result.getObjectSummaries();
    List<String> files = summaries.stream().map(f -> f.getKey()).collect(Collectors.toList());

    return files;
  }

  public Optional<File> downloadFile(String bucket, String key, Path dir) throws IOException {

    File tempFile = dir.resolve(key).toFile();
    if (tempFile.createNewFile()) {
      transferManager.download(bucket, key, tempFile);
      return Optional.of(tempFile);
    }

    return Optional.empty();
  }

  public Optional<File> downloadFile(String url, Path dir) throws IOException {

    Optional<String> bucket = getBucket(url);
    Optional<String> key = getKey(url);
    if (bucket.isPresent() && key.isPresent()) {
      File tempFile = dir.resolve(key.get()).toFile();
      if (tempFile.createNewFile()) {
        transferManager.download(bucket.get(), key.get(), tempFile);
        return Optional.of(tempFile);
      }
    }

    return Optional.empty();
  }

  public void deleteFile(String key) {
    amazonS3.deleteObject(s3Config.getS3Bucket(), key);
  }

  private ObjectMetadata setMetaData(File file) {
    ObjectMetadata meta = new ObjectMetadata();
    meta.setContentLength(file.length());
    String mimetype = Mimetypes.getInstance().getMimetype(file.getName());
    meta.setContentType(mimetype);
    return meta;
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
