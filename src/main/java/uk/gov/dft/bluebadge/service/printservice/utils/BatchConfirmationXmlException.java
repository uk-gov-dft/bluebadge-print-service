package uk.gov.dft.bluebadge.service.printservice.utils;

import javax.xml.stream.XMLStreamReader;

public class BatchConfirmationXmlException extends Exception {

  private final String detailedError;

  BatchConfirmationXmlException(String message, XMLStreamReader reader, String fileName) {
    super(message);
    detailedError =
        message
            + " While processing file:"
            + fileName
            + " Location: Line-"
            + reader.getLocation().getLineNumber()
            + " Column-"
            + reader.getLocation().getColumnNumber();
  }

  public BatchConfirmationXmlException(String detailedError) {
    super(detailedError);
    this.detailedError = detailedError;
  }

  public String getDetailedError() {
    return detailedError;
  }
}
