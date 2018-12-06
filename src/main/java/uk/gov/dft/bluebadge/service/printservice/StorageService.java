package uk.gov.dft.bluebadge.service.printservice;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
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

    log.info(
        "Uploading document to S3 bucket{}. {}, size:{}",
        s3Config.getS3Bucket(),
        file.getName(),
        file.length());

    String keyName = UUID.randomUUID().toString() + "-" + file.getCanonicalPath();
    keyName = URLEncoder.encode(keyName, ENCODING_CHAR_SET);

    try (FileInputStream fis = new FileInputStream(file)) {
      Upload upload =
          transferManager.upload(s3Config.getS3Bucket(), keyName, fis, setMetaData(file));
      UploadResult uploadResult = upload.waitForUploadResult();
      //		URL url = amazonS3.getUrl(uploadResult.getBucketName(), uploadResult.getKey());
      return generateSignedS3Url(uploadResult.getKey());
    }
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
}
