package uk.gov.dft.bluebadge.service.printservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.standardBatchPayload;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.testJson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.xml.stream.XMLStreamException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import uk.gov.dft.bluebadge.service.printservice.utils.ModelToXmlConverter;

@RunWith(JUnitPlatform.class)
@Slf4j
public class PrintServiceTest {

  private StorageService s3 = mock(StorageService.class);
  private FTPService ftp = mock(FTPService.class);
  private ModelToXmlConverter xmlConverter = mock(ModelToXmlConverter.class);
  private PrintService service = new PrintService(s3, ftp, xmlConverter);

  private String originalTmpDir = System.getProperty("java.io.tmpdir");

  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));

    String testTmpDir = Paths.get("src", "test", "resources", "tmp").toString();
    System.setProperty("java.io.tmpdir", testTmpDir);
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));

    System.setProperty("java.io.tmpdir", originalTmpDir);
  }

  @Test
  @DisplayName("Should convert received payload into json content and send to save on s3")
  @SneakyThrows
  public void printSuccess() {
    setup();

    service.print(standardBatchPayload());

    verify(s3, times(1)).upload(any(), any());
    verify(s3, times(1)).listFiles();
    verify(s3, times(2)).getBucketName();
    verify(s3, times(1)).downloadFile(any(), any());
    verify(s3, times(1)).deleteFile(any());

    verify(ftp, times(1)).send(any());

    verify(xmlConverter, times(1)).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if json content can't be uploaded on s3")
  @SneakyThrows
  public void printFailureToUploadToS3() {
    setup();
    when(s3.upload(any(), any())).thenReturn(false);

    service.print(standardBatchPayload());

    verify(s3, times(1)).upload(any(), any());
    verify(s3, never()).listFiles();
    verify(s3, never()).getBucketName();
    verify(s3, never()).downloadFile(any(), any());
    verify(s3, never()).deleteFile(any());

    verify(ftp, never()).send(any());

    verify(xmlConverter, never()).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if file can't be downloaded from s3")
  @SneakyThrows
  public void printFailureToDownloadFromS3() {
    setup();
    when(s3.downloadFile(any(), any())).thenReturn(Optional.empty());

    service.print(standardBatchPayload());

    verify(s3, times(1)).upload(any(), any());
    verify(s3, times(1)).listFiles();
    verify(s3, times(2)).getBucketName();
    verify(s3, times(1)).downloadFile(any(), any());
    verify(s3, never()).deleteFile(any());

    verify(ftp, never()).send(any());

    verify(xmlConverter, never()).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if file can't be transferred to FTP")
  @SneakyThrows
  public void printFailureToSendFileToFTP() {
    setup();
    when(ftp.send(any())).thenReturn(false);

    service.print(standardBatchPayload());

    verify(s3, times(1)).upload(any(), any());
    verify(s3, times(1)).listFiles();
    verify(s3, times(2)).getBucketName();
    verify(s3, times(1)).downloadFile(any(), any());
    verify(s3, never()).deleteFile(any());

    verify(ftp, times(1)).send(any());

    verify(xmlConverter, times(1)).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if file can't be deserealized to XML")
  @SneakyThrows
  public void printFailureToConvertToXML() {
    setup();
    doThrow(IOException.class).when(xmlConverter).toXml(any(), any());

    service.print(standardBatchPayload());

    verify(s3, times(1)).upload(any(), any());
    verify(s3, times(1)).listFiles();
    verify(s3, times(2)).getBucketName();
    verify(s3, times(1)).downloadFile(any(), any());
    verify(s3, never()).deleteFile(any());

    verify(ftp, never()).send(any());

    verify(xmlConverter, times(1)).toXml(any(), any());
  }

  private void setup()
      throws MalformedURLException, IOException, InterruptedException, Exception,
          XMLStreamException {
    when(s3.upload(any(), any())).thenReturn(true);

    when(ftp.send(any())).thenReturn(true);

    List<String> files = Arrays.asList("printbatch_1.json");
    when(s3.listFiles()).thenReturn(files);

    when(s3.getBucketName()).thenReturn("bucket");
    when(s3.downloadFile(any(), any())).thenReturn(Optional.of(testJson));

    String xml =
        Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml", "BADGEEXTRACT_1.xml")
            .toString();
    when(xmlConverter.toXml(any(), any())).thenReturn(xml);

    when(ftp.send(any())).thenReturn(true);
    doNothing().when(s3).deleteFile(any());
  }
}
