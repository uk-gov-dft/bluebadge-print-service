package uk.gov.dft.bluebadge.service.printservice.config;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class S3Config {
  @Value("${amazon.s3bucket}")
  @NotNull
  private String s3Bucket;
  
  @Value("${amazon.thumbnail-height-px:300}")
  @NotNull
  private Integer thumbnailHeight;
  
  @Value("${amazon.signed-url-duration-ms:5000}")
  @NotNull
  private Integer signedUrlDurationMs;
  
  @Bean
  AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder.defaultClient();
  }
  
  @Bean
  TransferManager transferManager() {
    return TransferManagerBuilder.standard()
        .withS3Client(amazonS3())
        .withMultipartUploadThreshold((long) (5 * 1024 * 1025))
        .build();
  }
}