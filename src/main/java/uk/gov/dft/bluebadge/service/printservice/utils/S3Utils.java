package uk.gov.dft.bluebadge.service.printservice.utils;

import java.util.Optional;

import com.amazonaws.services.s3.AmazonS3URI;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class S3Utils {

	public static Optional<String> getBucket(String url) {
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
	
	public static Optional<String> getKey(String url) {
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
}
