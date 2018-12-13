package uk.gov.service.printservice.test.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.Vector;

public class SFTPUtils {

  private String host =
      System.getenv("sftp_host") == null ? "127.0.0.1" : System.getenv("sftp_host");
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

  public void clean() throws Exception {
    JSch jsch = new JSch();
    Session session = null;
    ChannelSftp sftpChannel = null;
    try {
      jsch.setKnownHosts(knownhosts);
      session = jsch.getSession(user, host, port);
      //      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword(password);
      session.connect();

      Channel channel = session.openChannel("sftp");
      channel.connect();
      sftpChannel = (ChannelSftp) channel;

      sftpChannel.cd(dropbox);

      Vector<ChannelSftp.LsEntry> fileAndFolderList = sftpChannel.ls(dropbox);
      for (ChannelSftp.LsEntry item : fileAndFolderList) {
        System.out.println("file: " + item.getFilename());
        if (!item.getAttrs().isDir()) {
          sftpChannel.rm(dropbox + "/" + item.getFilename()); // Remove file.
        }
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    } finally {
      sftpChannel.exit();
      session.disconnect();
    }
  }

  public int getFileCount() throws Exception {
    int count = -1;
    JSch jsch = new JSch();
    Session session = null;
    ChannelSftp sftpChannel = null;
    try {
      jsch.setKnownHosts(knownhosts);
      session = jsch.getSession(user, host, port);
      //      session.setConfig("StrictHostKeyChecking", "no");
      session.setPassword(password);
      session.connect();

      Channel channel = session.openChannel("sftp");
      channel.connect();
      sftpChannel = (ChannelSftp) channel;
      sftpChannel.cd(dropbox);
      count = sftpChannel.ls(dropbox).size();
    } catch (Exception e) {
      System.err.println(e.getMessage());
    } finally {
      sftpChannel.exit();
      session.disconnect();
    }

    return count;
  }
}
