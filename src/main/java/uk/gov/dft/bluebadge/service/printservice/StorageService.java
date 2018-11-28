package uk.gov.dft.bluebadge.service.printservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.Mimetypes;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dft.bluebadge.service.printservice.config.S3Config;

@Service
@Slf4j
public class StorageService {

	public static final String ENCODING_CHAR_SET = "UTF-8";
	private final AmazonS3 amazonS3;
	private final S3Config s3Config;
	private final TransferManager transferManager;

	public StorageService(AmazonS3 amazonS3, S3Config s3Config, TransferManager transferManager) {
		this.amazonS3 = amazonS3;
		this.s3Config = s3Config;
		this.transferManager = transferManager;
	}

	public boolean upload(File file) throws IOException, InterruptedException {
		Objects.requireNonNull(file, "File is null");
		if (file.length() == 0) {
			throw new IllegalArgumentException("Upload failed. Batch file is empty");
		}
		
		log.info("Uploading document to S3. {}, size:{}", file.getName(), file.length());
		
		String keyName = UUID.randomUUID().toString() + "-" + file.getCanonicalPath();
		keyName = URLEncoder.encode(keyName, ENCODING_CHAR_SET);
		
		Upload upload = transferManager.upload(s3Config.getS3Bucket(), keyName, new FileInputStream(file), setMetaData(file));
		UploadResult uploadResult = upload.waitForUploadResult();
		URL url = amazonS3.getUrl(uploadResult.getBucketName(), uploadResult.getKey());
		
		return !StringUtils.isEmpty(url);
	}

	private ObjectMetadata setMetaData(File file) {
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(file.length());
		String mimetype = Mimetypes.getInstance().getMimetype(file.getName());
		meta.setContentType(mimetype);
		return meta;
	}
	

}
