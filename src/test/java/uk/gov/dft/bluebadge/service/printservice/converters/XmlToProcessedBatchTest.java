package uk.gov.dft.bluebadge.service.printservice.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBadge;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatch;

class XmlToProcessedBatchTest {

  private PrintResultXmlToProcessedBatchResponse processor =
      new PrintResultXmlToProcessedBatchResponse();

  @Test
  void processValidConfirmationFile() throws PrintResultXmlConversionException {

    InputStream is =
        getClass().getResourceAsStream("/processedBatchXml/ValidConfirmationMultipleBadges.xml");
    ProcessedBatch batch =
        processor.readProcessedBatchFile(is, "ValidConfirmationMultipleBadges.xml");

    // THEN...
    // Record count is 6
    assertThat(batch.getProcessedBadges().size()).isEqualTo(6);

    // Batch info correct
    assertThat(batch.getErrorMessage()).isNull();
    assertThat(batch.getFilename()).isEqualTo("ValidConfirmationMultipleBadges.xml");
    assertThat(batch.getFileType()).isEqualTo(ProcessedBatch.FileTypeEnum.CONFIRMATION);

    // Check a Confirmation with no as cancellation
    ProcessedBadge badge =
        ProcessedBadge.builder()
            .badgeNumber("222222")
            .errorMessage(null)
            .cancellation(ProcessedBadge.CancellationEnum.NO)
            .dispatchedDate(OffsetDateTime.of(2018, 2, 2, 14, 24, 34, 0, ZoneOffset.UTC))
            .build();
    assertThat(batch.getProcessedBadges()).contains(badge);

    // Check a Confirmation with yes as cancellation
    badge =
        ProcessedBadge.builder()
            .badgeNumber("222226")
            .errorMessage(null)
            .cancellation(ProcessedBadge.CancellationEnum.YES)
            .dispatchedDate(OffsetDateTime.of(2018, 1, 1, 13, 23, 33, 0, ZoneOffset.UTC))
            .build();
    assertThat(batch.getProcessedBadges()).contains(badge);
  }

  @Test
  void processValidRejectFile() throws PrintResultXmlConversionException {
    InputStream is =
        getClass().getResourceAsStream("/processedBatchXml/ValidRejectionMultipleBadges.xml");
    ProcessedBatch batch = processor.readProcessedBatchFile(is, "ValidRejectionMultipleBadges.xml");

    // THEN...
    // Record count is 2
    assertThat(batch.getProcessedBadges().size()).isEqualTo(2);

    // Batch info correct
    assertThat(batch.getErrorMessage()).isNull();
    assertThat(batch.getFilename()).isEqualTo("ValidRejectionMultipleBadges.xml");
    assertThat(batch.getFileType()).isEqualTo(ProcessedBatch.FileTypeEnum.REJECTION);

    // Check both badge responses
    ProcessedBadge badge =
        ProcessedBadge.builder()
            .badgeNumber("333333")
            .errorMessage(": Photograph not found")
            .cancellation(null)
            .dispatchedDate(null)
            .build();
    assertThat(batch.getProcessedBadges()).contains(badge);

    // Check a Confirmation with yes as cancellation
    badge =
        ProcessedBadge.builder()
            .badgeNumber("333334")
            .errorMessage(": Blah")
            .cancellation(null)
            .dispatchedDate(null)
            .build();
    assertThat(batch.getProcessedBadges()).contains(badge);
  }

  @Test
  void unexpectedElementInConfirmation() {
    InputStream is =
        getClass()
            .getResourceAsStream("/processedBatchXml/InvalidConfirmationUnexpectedElement.xml");
    try {
      processor.readProcessedBatchFile(is, "InvalidConfirmationUnexpectedElement.xml");
      failBecauseExceptionWasNotThrown(PrintResultXmlConversionException.class);
    } catch (PrintResultXmlConversionException e) {
      assertThat(e.getDetailedError()).contains("Unexpected xml element whilst parsing");
    }
  }

  @Test
  void unexpectedElementInRejection() {
    InputStream is =
        getClass().getResourceAsStream("/processedBatchXml/InvalidRejectionUnexpectedElement.xml");
    try {
      processor.readProcessedBatchFile(is, "InvalidRejectionUnexpectedElement.xml");
      failBecauseExceptionWasNotThrown(PrintResultXmlConversionException.class);
    } catch (PrintResultXmlConversionException e) {
      assertThat(e.getDetailedError()).contains("Unexpected xml element whilst parsing rejection");
    }
  }

  @Test
  void missingElementInConfirmation() {
    InputStream is =
        getClass().getResourceAsStream("/processedBatchXml/InvalidConfirmationMissingElement.xml");
    try {
      processor.readProcessedBatchFile(is, "InvalidConfirmationMissingElement.xml");
      failBecauseExceptionWasNotThrown(PrintResultXmlConversionException.class);
    } catch (PrintResultXmlConversionException e) {
      assertThat(e.getDetailedError()).contains("Missing element processing confirmation");
    }
  }

  @Test
  void missingElementInRejection() {
    InputStream is =
        getClass().getResourceAsStream("/processedBatchXml/InvalidRejectionMissingElement.xml");
    try {
      processor.readProcessedBatchFile(is, "InvalidConfirmationMissingElement.xml");
      failBecauseExceptionWasNotThrown(PrintResultXmlConversionException.class);
    } catch (PrintResultXmlConversionException e) {
      assertThat(e.getDetailedError()).contains("Missing element processing rejection");
    }
  }

  @Test
  void invalidRootElement() {
    InputStream is = getClass().getResourceAsStream("/processedBatchXml/InvalidRootElement.xml");
    try {
      processor.readProcessedBatchFile(is, "InvalidRootElement.xml");
      failBecauseExceptionWasNotThrown(PrintResultXmlConversionException.class);
    } catch (PrintResultXmlConversionException e) {
      assertThat(e.getDetailedError()).contains("Unexpected element:");
    }
  }

  @Test
  void invalidBadgeElement() {
    InputStream is = getClass().getResourceAsStream("/processedBatchXml/InvalidBadgeElement.xml");
    try {
      processor.readProcessedBatchFile(is, "InvalidBadgeElement.xml");
      failBecauseExceptionWasNotThrown(PrintResultXmlConversionException.class);
    } catch (PrintResultXmlConversionException e) {
      assertThat(e.getDetailedError()).contains("Unexpected element:");
    }
  }

  @Test
  void truncatedFile() {
    InputStream is = getClass().getResourceAsStream("/processedBatchXml/InvalidTruncatedFile.xml");
    try {
      processor.readProcessedBatchFile(is, "InvalidTruncatedFile.xml");
      failBecauseExceptionWasNotThrown(PrintResultXmlConversionException.class);
    } catch (PrintResultXmlConversionException e) {
      assertThat(e.getDetailedError()).contains("XMLStreamException reading");
    }
  }

  @Test
  void parseIsoDateTimeTest() {
    // Parses with no Z at format end
    OffsetDateTime result = processor.parseIsoDateTime("1980-01-01T14:00:00");
    assertThat(result.getYear()).isEqualTo(1980);

    // Parses with a Z
    result = processor.parseIsoDateTime("1981-01-01T14:00:00Z");
    assertThat(result.getYear()).isEqualTo(1981);

    // Null safe
    result = processor.parseIsoDateTime(null);
    assertThat(result).isNull();
    result = processor.parseIsoDateTime("");
    assertThat(result).isNull();
  }
}
