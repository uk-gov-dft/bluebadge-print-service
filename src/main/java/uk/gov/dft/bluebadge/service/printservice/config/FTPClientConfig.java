package uk.gov.dft.bluebadge.service.printservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class FTPClientConfig {

  @Value("${ftp.host}")
  private String host;

  @Value("${ftp.port}")
  private int port;

  @Value("${ftp.user}")
  private String user;

  @Value("${ftp.password}")
  private String password;

  @Value("${ftp.dropbox}")
  private String dropbox;
}
