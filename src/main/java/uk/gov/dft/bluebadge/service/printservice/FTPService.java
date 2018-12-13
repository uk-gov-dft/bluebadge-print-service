package uk.gov.dft.bluebadge.service.printservice;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.jcraft.jsch.SftpException;
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
    try {
      jsch.setKnownHosts(ftpConfig.getKnownhosts());
      session = jsch.getSession(ftpConfig.getUser(), ftpConfig.getHost(), ftpConfig.getPort());

      session.setPassword(ftpConfig.getPassword());
      session.connect();

      Channel channel = session.openChannel("sftp");
      channel.connect();
      sftpChannel = (ChannelSftp) channel;
      sftpChannel.cd(ftpConfig.getDropbox());
      File file = new File(filename);
      sftpChannel.put(new FileInputStream(file), file.getName(), ChannelSftp.OVERWRITE);
    }catch (JSchException | FileNotFoundException | SftpException e) {
      log.error("Error happened while sending file to sftp", e);
      return false;
    } finally {
      sftpChannel.exit();
      session.disconnect();
    }

    return true;
  }
}
