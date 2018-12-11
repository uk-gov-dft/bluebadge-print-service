package uk.gov.dft.bluebadge.service.printservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;
import uk.gov.dft.bluebadge.service.printservice.config.FTPClientConfig;

@Service
@Slf4j
public class FTPService {

  private final FTPClientConfig ftpConfig;

  public FTPService(FTPClientConfig ftpConfig) {
    this.ftpConfig = ftpConfig;
  }

  public FTPClient connect() throws Exception {
    FTPClient ftp = ftpConfig.ftpClient();
    ftp.connect(ftpConfig.getHost());

    int reply = ftp.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      ftp.disconnect();
      log.error("Exception in connecting to FTP Server");
      throw new Exception("Exception in connecting to FTP Server");
    }
    ftp.login(ftpConfig.getUser(), ftpConfig.getPassword());
    ftp.enterLocalPassiveMode();

    return ftp;
  }

  public void disconnect() throws Exception {
    FTPClient ftp = ftpConfig.ftpClient();
    if (ftp.isConnected()) {
      ftp.logout();
      ftp.disconnect();
    }
  }

  public boolean send(String file) throws Exception {
    log.debug("Starting sending xml file to ftp");
    FTPClient ftp = ftpConfig.ftpClient();
    try (InputStream input = new FileInputStream(new File(file))) {
      return ftp.storeFile(ftpConfig.getDropbox(), input);
    }
  }
}
