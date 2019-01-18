package uk.gov.dft.bluebadge.service.printservice.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class S3Config {
  @Value("${amazon.bb-printer-bucket}")
  @NotNull
  private String s3PrinterBucket;

  @Value("${amazon.bb-badge-bucket}")
  @NotNull
  private String s3BadgeBucket;

  @Value("${amazon.bb-processed-badge-bucket}")
  @NotNull
  private String s3InBucket;

  @Value("${amazon.signed-url-duration-ms:5000}")
  @NotNull
  private Integer signedUrlDurationMs;

  @Bean
  AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder.defaultClient();
  }

  @Bean
  TransferManager transferManager() {
    return TransferManagerBuilder.standard().withS3Client(amazonS3()).build();
  }
}
