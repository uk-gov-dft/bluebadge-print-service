package uk.gov.service.printservice.test.utils;

import java.io.PrintWriter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class SFTPUtils {

  private String host = "localhost:2222";

  private String user = "foo";

  private String  ***REMOVED***;

  private String dropbox = "/host/upload";

  private FTPClient ftpClient;

  public SFTPUtils() throws Exception {
    ftpClient = ftpClient();
  }

  public FTPClient ftpClient() throws Exception {
    FTPClient ftp = new FTPClient();
    ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
    ftp.setFileType(FTP.BINARY_FILE_TYPE);

    return ftp;
  }

  public void clean() throws Exception {
    connect();
    cleanDirectory(dropbox);
    disconnect();
  }

  public int getFileCount() throws Exception {
    connect();
    int count = ftpClient.listFiles(dropbox).length;
    disconnect();

    return count;
  }

  private void cleanDirectory(String path) throws Exception {
    FTPFile[] files = ftpClient.listFiles(path);
    if (files.length > 0) {
      for (FTPFile ftpFile : files) {
        if (ftpFile.isDirectory()) {
          cleanDirectory(path + "/" + ftpFile.getName());
        } else {
          String deleteFilePath = path + "/" + ftpFile.getName();
          ftpClient.deleteFile(deleteFilePath);
        }
      }
    }
  }

  public void connect() throws Exception {
    ftpClient.connect(host);

    int reply = ftpClient.getReplyCode();
    if (!FTPReply.isPositiveCompletion(reply)) {
      ftpClient.disconnect();
      throw new Exception("Exception in connecting to FTP Server");
    }
    ftpClient.login(user, password);
    ftpClient.enterLocalPassiveMode();
  }

  public void disconnect() throws Exception {
    if (ftpClient.isConnected()) {
      ftpClient.logout();
      ftpClient.disconnect();
    }
  }
}
