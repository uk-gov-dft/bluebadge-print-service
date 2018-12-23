package uk.gov.dft.bluebadge.service.printservice.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OffsetDateTimeXmlAdapter extends XmlAdapter<String, OffsetDateTime> {

  public OffsetDateTime unmarshal(String stringValue) {
    return stringValue != null
        ? DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(stringValue, OffsetDateTime::from)
        : null;
  }

  public String marshal(OffsetDateTime value) {
    return value != null ? DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value) : null;
  }
}
