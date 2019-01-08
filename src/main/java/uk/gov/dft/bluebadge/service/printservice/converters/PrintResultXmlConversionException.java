package uk.gov.dft.bluebadge.service.printservice.converters;

import javax.xml.stream.XMLStreamReader;

public class PrintResultXmlConversionException extends Exception {

  private String detailedError;

  PrintResultXmlConversionException(String message, XMLStreamReader reader, String fileName) {
    super(message);
    try {
      detailedError =
          message
              + " While processing file:"
              + fileName
              + " Location: Line-"
              + reader.getLocation().getLineNumber()
              + " Column-"
              + reader.getLocation().getColumnNumber();
    } catch (Exception e) {
      detailedError = message;
    }
  }

  public PrintResultXmlConversionException(String detailedError) {
    super(detailedError);
    this.detailedError = detailedError;
  }

  public String getDetailedError() {
    return detailedError;
  }
}
