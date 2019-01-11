package uk.gov.dft.bluebadge.service.printservice;

import static org.assertj.core.api.Assertions.assertThat;
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
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.successBatch;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.testJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import uk.gov.dft.bluebadge.service.printservice.converters.PrintRequestToPrintXml;
import uk.gov.dft.bluebadge.service.printservice.converters.PrintResultXmlConversionException;
import uk.gov.dft.bluebadge.service.printservice.converters.PrintResultXmlToProcessedBatchResponse;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatch;

@Slf4j
class PrintServiceTest {

  private StorageService mockS3 = mock(StorageService.class);
  private FTPService mockFtpService = mock(FTPService.class);
  private PrintRequestToPrintXml mockXmlConverter = mock(PrintRequestToPrintXml.class);
  private PrintResultXmlToProcessedBatchResponse mockXmlToProcessedBatch =
      mock(PrintResultXmlToProcessedBatchResponse.class);

  private PrintService service =
      new PrintService(
          mockS3, mockFtpService, mockXmlConverter, mockXmlToProcessedBatch, new ObjectMapper());

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
  @DisplayName("Should convert received payload into json content and send to save on mockS3")
  @SneakyThrows
  void printSuccess() {
    setup();

    service.print(standardBatchPayload());

    verify(mockS3, times(1)).uploadToPrinterBucket(any(), any());
    verify(mockS3, times(1)).listPrinterBucketFiles();
    verify(mockS3, times(1)).downloadPrinterFileAsString(any());
    verify(mockS3, times(1)).deletePrinterBucketFile(any());

    verify(mockFtpService, times(1)).send(any());

    verify(mockXmlConverter, times(1)).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if json content can't be uploaded on mockS3")
  @SneakyThrows
  void printFailureToUploadToS3() {
    setup();
    when(mockS3.uploadToPrinterBucket(any(), any())).thenReturn(false);

    service.print(standardBatchPayload());

    verify(mockS3, times(1)).uploadToPrinterBucket(any(), any());
    verify(mockS3, never()).listPrinterBucketFiles();
    verify(mockS3, never()).downloadPrinterFileAsString(any());
    verify(mockS3, never()).deletePrinterBucketFile(any());

    verify(mockFtpService, never()).send(any());

    verify(mockXmlConverter, never()).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if file can't be downloaded from mockS3")
  @SneakyThrows
  void printFailureToDownloadFromS3() {
    setup();
    when(mockS3.downloadPrinterFileAsString(any())).thenReturn(Optional.empty());

    service.print(standardBatchPayload());

    verify(mockS3, times(1)).uploadToPrinterBucket(any(), any());
    verify(mockS3, times(1)).listPrinterBucketFiles();
    verify(mockS3, times(1)).downloadPrinterFileAsString(any());
    verify(mockS3, never()).deletePrinterBucketFile(any());

    verify(mockFtpService, never()).send(any());

    verify(mockXmlConverter, never()).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if file can't be transferred to FTP")
  @SneakyThrows
  void printFailureToSendFileToFTP() {
    setup();
    when(mockFtpService.send(any())).thenReturn(false);

    service.print(standardBatchPayload());

    verify(mockS3, times(1)).uploadToPrinterBucket(any(), any());
    verify(mockS3, times(1)).listPrinterBucketFiles();
    verify(mockS3, times(1)).downloadPrinterFileAsString(any());
    verify(mockS3, never()).deletePrinterBucketFile(any());

    verify(mockFtpService, times(1)).send(any());

    verify(mockXmlConverter, times(1)).toXml(any(), any());
  }

  @Test
  @DisplayName("Should fail if file can't be deserealized to XML")
  @SneakyThrows
  void printFailureToConvertToXML() {
    setup();
    doThrow(IOException.class).when(mockXmlConverter).toXml(any(), any());

    service.print(standardBatchPayload());

    verify(mockS3, times(1)).uploadToPrinterBucket(any(), any());
    verify(mockS3, times(1)).listPrinterBucketFiles();
    verify(mockS3, times(1)).downloadPrinterFileAsString(any());
    verify(mockS3, never()).deletePrinterBucketFile(any());

    verify(mockFtpService, never()).send(any());

    verify(mockXmlConverter, times(1)).toXml(any(), any());
  }

  @Test
  @DisplayName("Should return successfully processed batches")
  @SneakyThrows
  void getProcessedBatchesSuccess() {

    when(mockS3.listInBucketXmlFiles())
        .thenReturn(
            Arrays.asList(
                "validProcessedBatch1.xml",
                "invalidProcessedBatch1.xml",
                "validProcessedBatch2.xml"));
    when(mockS3.downloadS3File(any(), eq("validProcessedBatch1.xml")))
        .thenReturn(
            getClass().getResourceAsStream("/processedBatchXml/ValidConfirmationSingleBadge.xml"));
    when(mockS3.downloadS3File(any(), eq("validProcessedBatch2.xml")))
        .thenReturn(
            getClass().getResourceAsStream("/processedBatchXml/ValidConfirmationSingleBadge2.xml"));
    when(mockS3.downloadS3File(any(), eq("invalidProcessedBatch1.xml")))
        .thenReturn(
            getClass()
                .getResourceAsStream(
                    "/processedBatchXml/InvalidConfirmationUnexpectedElement.xml"));
    when(mockXmlToProcessedBatch.readProcessedBatchFile(any(), eq("invalidProcessedBatch1.xml")))
        .thenThrow(new PrintResultXmlConversionException("Unexpected"));
    when(mockXmlToProcessedBatch.readProcessedBatchFile(any(), eq("validProcessedBatch1.xml")))
        .thenReturn(ProcessedBatch.builder().filename("validProcessedBatch1.xml").build());
    when(mockXmlToProcessedBatch.readProcessedBatchFile(any(), eq("validProcessedBatch2.xml")))
        .thenReturn(ProcessedBatch.builder().filename("validProcessedBatch2.xml").build());
    // When 2 valid files and one unparsable...
    List<ProcessedBatch> batches = service.getProcessedBatches();

    verify(mockS3, times(1)).listInBucketXmlFiles();
    verify(mockXmlToProcessedBatch, times(1))
        .readProcessedBatchFile(any(), eq("validProcessedBatch1.xml"));
    verify(mockXmlToProcessedBatch, times(1))
        .readProcessedBatchFile(any(), eq("validProcessedBatch2.xml"));
    verify(mockXmlToProcessedBatch, times(1))
        .readProcessedBatchFile(any(), eq("invalidProcessedBatch1.xml"));

    // Then all 3 get processed
    assertThat(batches.size()).isEqualTo(3);
    assertThat(batches)
        .contains(
            ProcessedBatch.builder()
                .errorMessage("Unexpected")
                .filename("invalidProcessedBatch1.xml")
                .build());
    assertThat(batches)
        .contains(ProcessedBatch.builder().filename("validProcessedBatch1.xml").build());
    assertThat(batches)
        .contains(ProcessedBatch.builder().filename("validProcessedBatch2.xml").build());
  }

  private void setup() throws Exception {
    when(mockS3.uploadToPrinterBucket(any(), any())).thenReturn(true);

    when(mockFtpService.send(any())).thenReturn(true);

    when(mockS3.listPrinterBucketFiles())
        .thenReturn(Collections.singletonList("printbatch_1.json"));
    when(mockS3.downloadPrinterFileAsString(any())).thenReturn(Optional.of(testJson));

    when(mockXmlToProcessedBatch.readProcessedBatchFile(any(), any())).thenReturn(successBatch);

    when(mockS3.getInBucket()).thenReturn("inBucket");

    String xml =
        Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml", "BADGEEXTRACT_1.xml")
            .toString();
    when(mockXmlConverter.toXml(any(), any())).thenReturn(xml);

    when(mockFtpService.send(any())).thenReturn(true);
    doNothing().when(mockS3).deletePrinterBucketFile(any());
  }
}
