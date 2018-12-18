package uk.gov.service.printservice.test.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

public class SFTPUtils {

  private String host =
      System.getenv("sftp_host") == null ? "localhost" : System.getenv("sftp_host");
  private int port =
      System.getenv("sftp_port") == null ? 2222 : Integer.parseInt(System.getenv("sftp_port"));
  private String user = System.getenv("sftp_user") == null ? "foo" : System.getenv("sftp_user");
  private String password =
      System.getenv("sftp_pass") == null ? "pass" : System.getenv("sftp_pass");
  private String dropbox =
      System.getenv("sftp_folder") == null ? "/upload" : System.getenv("sftp_folder");

  private String knownhosts =
      System.getenv("sftp_knownhosts") == null
          ? "~/.ssh/sftp_known_hosts"
          : System.getenv("sftp_knownhosts");

  class SftpChannelManager implements AutoCloseable {

    JSch jsch = new JSch();
    Session session;
    ChannelSftp channel;

    SftpChannelManager() throws JSchException {
      jsch.setKnownHosts(knownhosts);
      session = jsch.getSession(user, host, port);
      //      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword(password);
      session.connect();

      Channel channel = session.openChannel("sftp");
      channel.connect();
      this.channel = (ChannelSftp) channel;
    }

    @Override
    public void close() {
      try {
        channel.exit();
        session.disconnect();
      } catch (Exception e) {
        // No-op
      }
    }
  }

  @SuppressWarnings("unused")
  public void clean() throws Exception {
    try (SftpChannelManager channelManager = new SftpChannelManager()) {
      //noinspection unchecked
      for (ChannelSftp.LsEntry item :
          (Vector<ChannelSftp.LsEntry>) channelManager.channel.ls(dropbox)) {
        System.out.println("file: " + item.getFilename());
        if (!item.getAttrs().isDir()) {
          channelManager.channel.rm(dropbox + "/" + item.getFilename()); // Remove file.
        }
      }
    }
  }

  @SuppressWarnings("unused")
  public int getFileCount() throws JSchException, SftpException {
    int count;

    try (SftpChannelManager channelManager = new SftpChannelManager()) {
      channelManager.channel.cd(dropbox);
      count = channelManager.channel.ls(dropbox).size();
    }

    return count;
  }

  public boolean putFile(String resourcePath) {
    try (SftpChannelManager channelManager = new SftpChannelManager()) {
      channelManager.channel.cd(dropbox);
      File f = new File(this.getClass().getResource(resourcePath).toURI());
      channelManager.channel.put(new FileInputStream(f), f.getName(), ChannelSftp.OVERWRITE);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
