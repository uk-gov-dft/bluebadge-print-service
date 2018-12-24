package uk.gov.dft.bluebadge.service.printservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.standardBatchPayload;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.testJson;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.validXml;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.rejectedXml;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.successBatch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import uk.gov.dft.bluebadge.service.printservice.config.S3Config;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatch;
import uk.gov.dft.bluebadge.service.printservice.utils.ModelToXmlConverter;
import uk.gov.dft.bluebadge.service.printservice.utils.XmlToProcessedBatch;

@RunWith(JUnitPlatform.class)
@Slf4j
public class PrintServiceTest {

	private S3Config s3Config = mock(S3Config.class);
  private StorageService s3 = mock(StorageService.class);
  private FTPService ftp = mock(FTPService.class);
  private ModelToXmlConverter xmlConverter = mock(ModelToXmlConverter.class);
  private XmlToProcessedBatch xmlProcessor = mock(XmlToProcessedBatch.class);
  
  @Autowired
  private PrintService service = new PrintService(s3, ftp, xmlConverter, xmlProcessor);

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

    verify(s3, times(1)).uploadToPrinterBucket(any(), any());
    verify(s3, times(1)).listPrinterBucketFiles();
    verify(s3, times(1)).downloadPrinterFileAsString(any());
    verify(s3, times(1)).deletePrinterBucketFile(any());

    verify(ftp, times(1)).send(any());

    verify(xmlConverter, times(1)).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if json content can't be uploaded on s3")
  @SneakyThrows
  public void printFailureToUploadToS3() {
    setup();
    when(s3.uploadToPrinterBucket(any(), any())).thenReturn(false);

    service.print(standardBatchPayload());

    verify(s3, times(1)).uploadToPrinterBucket(any(), any());
    verify(s3, never()).listPrinterBucketFiles();
    verify(s3, never()).downloadPrinterFileAsString(any());
    verify(s3, never()).deletePrinterBucketFile(any());

    verify(ftp, never()).send(any());

    verify(xmlConverter, never()).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if file can't be downloaded from s3")
  @SneakyThrows
  public void printFailureToDownloadFromS3() {
    setup();
    when(s3.downloadPrinterFileAsString(any())).thenReturn(Optional.empty());

    service.print(standardBatchPayload());

    verify(s3, times(1)).uploadToPrinterBucket(any(), any());
    verify(s3, times(1)).listPrinterBucketFiles();
    verify(s3, times(1)).downloadPrinterFileAsString(any());
    verify(s3, never()).deletePrinterBucketFile(any());

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

    verify(s3, times(1)).uploadToPrinterBucket(any(), any());
    verify(s3, times(1)).listPrinterBucketFiles();
    verify(s3, times(1)).downloadPrinterFileAsString(any());
    verify(s3, never()).deletePrinterBucketFile(any());

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

    verify(s3, times(1)).uploadToPrinterBucket(any(), any());
    verify(s3, times(1)).listPrinterBucketFiles();
    verify(s3, times(1)).downloadPrinterFileAsString(any());
    verify(s3, never()).deletePrinterBucketFile(any());

    verify(ftp, never()).send(any());

    verify(xmlConverter, times(1)).toXml(any(), any());
  }

  @Test
  @DisplayName("Should return successfully processed batches")
  @SneakyThrows
  public void getProcessedBatchesSuccess() {
    setup();

    when(s3.listInBucketXmlFiles()).thenReturn(Arrays.asList("processed_batch.xml"));
    List<ProcessedBatch> batches = service.getProcessedBatches();

    verify(s3, times(1)).listInBucketXmlFiles();
    verify(xmlProcessor, times(1)).readProcessedBatchFile(any(), eq("processed_batch.xml"));
    assertEquals(1, batches.size());
  }

  
  private void setup()
      throws MalformedURLException, IOException, InterruptedException, Exception,
          XMLStreamException {
    when(s3.uploadToPrinterBucket(any(), any())).thenReturn(true);

    when(ftp.send(any())).thenReturn(true);

    when(s3.listPrinterBucketFiles()).thenReturn(Arrays.asList("printbatch_1.json"));
    when(s3.downloadPrinterFileAsString(any())).thenReturn(Optional.of(testJson));
    
    InputStream isProcessed = new ByteArrayInputStream(validXml.getBytes(Charset.forName("UTF-8")));
    when(s3.downloadS3File(any(), eq("processed_batch.xml"))).thenReturn(isProcessed);
        
    InputStream isRejected = new ByteArrayInputStream(rejectedXml.getBytes(Charset.forName("UTF-8")));
    when(s3.downloadS3File(any(), eq("rejected_batch.xml"))).thenReturn(isRejected);
 
    when(xmlProcessor.readProcessedBatchFile(any(), any())).thenReturn(successBatch); 
    
    when(s3.getInBucket()).thenReturn("inBucket");
    
    String xml =
        Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml", "BADGEEXTRACT_1.xml")
            .toString();
    when(xmlConverter.toXml(any(), any())).thenReturn(xml);

    when(ftp.send(any())).thenReturn(true);
    doNothing().when(s3).deletePrinterBucketFile(any());
  }
}
