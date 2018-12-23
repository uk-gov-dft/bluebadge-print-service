package uk.gov.dft.bluebadge.service.printservice.utils;

import javax.xml.stream.XMLStreamReader;

public class BatchConfirmationXmlException extends Exception {

  private String detailedError;

  public BatchConfirmationXmlException(String message, XMLStreamReader reader, String fileName) {
    super(message);
    detailedError =
        message
            + "\n While processing file:"
            + fileName
            + "\n Location: Line-"
            + reader.getLocation().getLineNumber()
            + "Column-"
            + reader.getLocation().getColumnNumber();
  }

  public BatchConfirmationXmlException(String detailedError) {
    this.detailedError = detailedError;
  }

  public String getDetailedError() {
    return detailedError;
  }
}
