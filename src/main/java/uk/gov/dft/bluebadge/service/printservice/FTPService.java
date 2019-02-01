package uk.gov.dft.bluebadge.service.printservice;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.dft.bluebadge.service.printservice.config.FTPClientConfig;

@Service
@Slf4j
public class FTPService {

  private final FTPClientConfig ftpConfig;

  public FTPService(FTPClientConfig ftpConfig) {
    this.ftpConfig = ftpConfig;
  }

  boolean send(String filename) {
    JSch jsch = new JSch();
    Session session = null;
    ChannelSftp sftpChannel = null;
    File file = new File(filename);
    try (FileInputStream fileInputStream = new FileInputStream(file)) {
      jsch.setKnownHosts(ftpConfig.getKnownhosts());
      session = jsch.getSession(ftpConfig.getUser(), ftpConfig.getHost(), ftpConfig.getPort());

      session.setPassword(ftpConfig.getPassword());
      session.connect();

      Channel channel = session.openChannel("sftp");
      channel.connect();
      sftpChannel = (ChannelSftp) channel;
      //sftpChannel.cd(ftpConfig.getDropbox());

      sftpChannel.put(fileInputStream, file.getName(), ChannelSftp.OVERWRITE);

    } catch (Exception e) {
      log.error("Error happened while sending file to sftp:" + e.getMessage(), e);
      return false;
    } finally {
      if (null != sftpChannel) {
        sftpChannel.exit();
      }
      if (null != session) {
        session.disconnect();
      }
    }

    return true;
  }
}
