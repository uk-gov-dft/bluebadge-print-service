package uk.gov.dft.bluebadge.service.printservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dft.bluebadge.common.service.exception.BadRequestException;
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

		String keyName = UUID.randomUUID().toString() + "-" + file.getCanonicalPath();
		keyName = URLEncoder.encode(keyName, ENCODING_CHAR_SET);

		Upload upload = transferManager.upload(s3Config.getS3Bucket(), keyName, new FileInputStream(file),
		    setMetaData(file));
		UploadResult uploadResult = upload.waitForUploadResult();
		// URL url = amazonS3.getUrl(uploadResult.getBucketName(),
		// uploadResult.getKey());

		return generateSignedS3Url(uploadResult.getKey());
	}

	public File downloadBucket() {
		File tempDir = Paths.get(System.getProperty("java.io.tmpdir"),
		    											"printbatch_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss")))
												.toFile();
		tempDir.mkdirs();

		transferManager.downloadDirectory(s3Config.getS3Bucket(), null, tempDir, true);
		return tempDir;
	}

	public File downloadFile(String picUrl) {
		File tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_pics").toFile();
		tempDir.mkdirs();

		transferManager.download(getBucket(picUrl), getKey(picUrl), tempDir);
		return tempDir;
	}
	
	private Optional<String> getBucket(String url) {
		AmazonS3URI amazonS3URI = null;
    try {
      amazonS3URI = new AmazonS3URI(url, false);
    } catch (Exception e) {
      log.info("Failed to extract S3 URI. Link:{}", url);
      return Optional.empty();
    }
    if (null == amazonS3URI.getBucket()) {
      log.info("Failed to extract S3 object bucket from url: {}", amazonS3URI.toString());
    }
 		
    return Optional.of(amazonS3URI.getBucket());
	}
	
	private Optional<String> getKey(String url) {
		AmazonS3URI amazonS3URI = null;
    try {
      amazonS3URI = new AmazonS3URI(url, false);
    } catch (Exception e) {
      log.info("Failed to extract S3 URI. Link:{}", url);
      return Optional.empty();
    }
    if (null == amazonS3URI.getKey()) {
      log.info("Failed to extract S3 object key from url: {}", amazonS3URI.toString());
    }
 		
    return Optional.of(amazonS3URI.getKey());
	}

	
	
	private void checkURL(String url) {
    AmazonS3URI amazonS3URI;
    try {
      amazonS3URI = new AmazonS3URI(url, false);
    } catch (Exception e) {
      log.info("Failed to extract S3 URI. Link:{}", url, e);
      Error error =
          new Error()
              .message("Failed to extract S3 bucket and key from url: " + url)
              .reason(ARTIFACT_LINK_ERR_FIELD);
      throw new BadRequestException(error);
    }
    if (null == amazonS3URI.getBucket()) {
      Error error =
          new Error()
              .message("Failed to extract S3 object bucket from url: " + url)
              .reason(ARTIFACT_LINK_ERR_FIELD);
      throw new BadRequestException(error);
    }
    if (null == amazonS3URI.getKey()) {
      Error error =
          new Error()
              .message("Failed to extract S3 object key from url: " + url)
              .reason(ARTIFACT_LINK_ERR_FIELD);
      throw new BadRequestException(error);
    }
    if (!amazonS3.doesObjectExist(amazonS3URI.getBucket(), amazonS3URI.getKey())) {
      String message =
          String.format(S3_NOT_FOUND_ERR_MSG, amazonS3URI.getBucket(), amazonS3URI.getKey(), url);
      log.info(message);
      Error error = new Error().message(message).reason(ARTIFACT_LINK_ERR_FIELD);
      throw new BadRequestException(error);
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
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(s3Config.getS3Bucket(),
		    link).withMethod(HttpMethod.GET).withExpiration(new Date(expTimeMillis));
		return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
	}
}
