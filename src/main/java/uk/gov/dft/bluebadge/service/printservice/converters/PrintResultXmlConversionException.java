package uk.gov.dft.bluebadge.service.printservice.converters;

import javax.xml.stream.XMLStreamReader;

public class PrintResultXmlConversionException extends Exception {

  private final String detailedError;

  PrintResultXmlConversionException(String message, XMLStreamReader reader, String fileName) {
    super(message);
    String error;
    try {
      error =
          message
              + " While processing file:"
              + fileName
              + " Location: Line-"
              + reader.getLocation().getLineNumber()
              + " Column-"
              + reader.getLocation().getColumnNumber();
    } catch (Exception e) {
      error = message;
    }
    detailedError = error;
  }

  public PrintResultXmlConversionException(String detailedError) {
    super(detailedError);
    this.detailedError = detailedError;
  }

  public String getDetailedError() {
    return detailedError;
  }
}
