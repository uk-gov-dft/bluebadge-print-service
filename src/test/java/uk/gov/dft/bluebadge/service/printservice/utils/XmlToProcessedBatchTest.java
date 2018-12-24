package uk.gov.dft.bluebadge.service.printservice.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.rejectedXml;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.validXml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatch;

class XmlToProcessedBatchTest {

  private XmlToProcessedBatch processor = new XmlToProcessedBatch();

  @Test
  void processValidConfirmationFile() throws BatchConfirmationXmlException {

    InputStream is = new ByteArrayInputStream(validXml.getBytes());
    ProcessedBatch batch = processor.readProcessedBatchFile(is, "MyTest.xml");

    assertThat(batch.getProcessedBadges().size()).isEqualTo(6);
  }

  @Test
  void processRejectFile() throws BatchConfirmationXmlException {
    InputStream is = new ByteArrayInputStream(rejectedXml.getBytes());
    ProcessedBatch batch = processor.readProcessedBatchFile(is, "MyTest.xml");

    assertThat(batch.getProcessedBadges().size()).isEqualTo(2);
  }
}
