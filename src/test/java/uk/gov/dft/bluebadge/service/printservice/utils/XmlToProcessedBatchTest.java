package uk.gov.dft.bluebadge.service.printservice.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatch;

class XmlToProcessedBatchTest {

  private XmlToProcessedBatch processor = new XmlToProcessedBatch();

  @Test
  void processValidConfirmationFile() throws BatchConfirmationXmlException {
    String validXml =
        "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>\n"
            + "<BadgePrintConfirmations xmlns=\"http://YourSite.com/BadgePrintConfirmations\">\n"
            + "<Confirmation>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<Cancellation>no</Cancellation>\n"
            + "<DispatchedDate>2018-08-04T10:21:21</DispatchedDate>\n"
            + "</Confirmation>\n"
            + "<Confirmation>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<Cancellation>no</Cancellation>\n"
            + "<DispatchedDate>2018-08-04T10:15:49</DispatchedDate>\n"
            + "</Confirmation>\n"
            + "<Confirmation>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<Cancellation>no</Cancellation>\n"
            + "<DispatchedDate>2018-08-04T10:17:08</DispatchedDate>\n"
            + "</Confirmation>\n"
            + "<Confirmation>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<Cancellation>no</Cancellation>\n"
            + "<DispatchedDate>2018-08-04T10:21:24</DispatchedDate>\n"
            + "</Confirmation>\n"
            + "<Confirmation>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<Cancellation>no</Cancellation>\n"
            + "<DispatchedDate>2018-08-04T10:17:22</DispatchedDate>\n"
            + "</Confirmation>\n"
            + "<Confirmation>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<Cancellation>no</Cancellation>\n"
            + "<DispatchedDate>2018-08-04T10:15:54</DispatchedDate>\n"
            + "</Confirmation>\n"
            + "</BadgePrintConfirmations>";

    InputStream is = new ByteArrayInputStream(validXml.getBytes());
    ProcessedBatch batch = processor.readProcessedBatchFile(is, "MyTest.xml");

    assertThat(batch.getProcessedBadges().size()).isEqualTo(6);
  }

  @Test
  void processRejectFile() throws BatchConfirmationXmlException {
    String file =
        "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>\n"
            + "<BadgePrintRejections xmlns=\"http://YourSite.com/BadgePrintRejections\">\n"
            + "<Rejection>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<ErrorMessage>: Photograph not found</ErrorMessage>\n"
            + "</Rejection>\n"
            + "<Rejection>\n"
            + "<BadgeIdentifier>xxxxxx</BadgeIdentifier>\n"
            + "<ErrorMessage>: Photograph not found</ErrorMessage>\n"
            + "</Rejection>\n"
            + "</BadgePrintRejections>";
    InputStream is = new ByteArrayInputStream(file.getBytes());
    ProcessedBatch batch = processor.readProcessedBatchFile(is, "MyTest.xml");

    assertThat(batch.getProcessedBadges().size()).isEqualTo(2);
  }
}
