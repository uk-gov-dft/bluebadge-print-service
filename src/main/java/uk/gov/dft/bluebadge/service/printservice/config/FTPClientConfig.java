package uk.gov.dft.bluebadge.service.printservice.config;

import java.io.PrintWriter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class FTPClientConfig {

  @Value("${ftp.host}")
  private String host;

  @Value("${ftp.user}")
  private String user;

  @Value("${ftp.password}")
  private String password;

  @Value("${ftp.dropbox}")
  private String dropbox;

  @Bean
  public FTPClient ftpClient() throws Exception {
    FTPClient ftp = new FTPClient();
    ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    ftp.setFileType(FTP.BINARY_FILE_TYPE);

    return ftp;
  }
}
