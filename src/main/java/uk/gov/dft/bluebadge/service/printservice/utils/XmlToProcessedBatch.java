package uk.gov.dft.bluebadge.service.printservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.ValidationUtils;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBadge;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatch;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XmlToProcessedBatch {

  private static final String BADGE_PRINT_CONFIRMATION = "BadgePrintConfirmations";
  private static final String BADGE_PRINT_REJECTIONS = "BadgePrintRejections";
  private static final String CONFIRMATION = "Confirmation";
  private static final String REJECTION = "Rejection";
  private static final String BADGE_ID = "BadgeIdentifier";
  private static final String CANCELLATION = "Cancellation";
  private static final String DISPATCHED_DATE = "DispatchedDate";
  private static final String ERROR_MESSAGE = "ErrorMessage";

  /**
   * Read and parse confirmation/rejection file.
   *
   * @param xmlStream InputStream for file.
   * @param filename  File name for logging and populating.
   * @return Populated Batch results object.
   * @throws BatchConfirmationXmlException Holds info on location in file where error occurred.
   */
  public ProcessedBatch readProcessedBatchFile(InputStream xmlStream, String filename)
      throws BatchConfirmationXmlException {
    ProcessedBatch.ProcessedBatchBuilder batchBuilder = ProcessedBatch.builder().filename(filename);
    List<ProcessedBadge> processedBadges = new ArrayList<>();

    XMLStreamReader reader = null;
    try {
      reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlStream);
      while (reader.nextTag() == XMLStreamConstants.START_ELEMENT) {
        String elementName = reader.getLocalName();

        if (CONFIRMATION.equalsIgnoreCase(elementName)) {
          // Parse confirmation and add.
          processedBadges.add(readConfirmation(reader, filename));
        } else if (REJECTION.equalsIgnoreCase(elementName)) {
          // Parse rejection and add.
          processedBadges.add(readRejection(reader, filename));
        } else if (BADGE_PRINT_CONFIRMATION.equalsIgnoreCase(elementName)) {
          // Start element of Confirmation.
          batchBuilder.fileType(ProcessedBatch.FileTypeEnum.CONFIRMATION);
        } else if (BADGE_PRINT_REJECTIONS.equalsIgnoreCase(elementName)) {
          // Start element of Rejection.
          batchBuilder.fileType(ProcessedBatch.FileTypeEnum.REJECTION);
        } else {
          // Unexpected element
          throw buildAndLogException("Unexpected element:" + elementName, reader, filename);
        }
      }
    } catch (XMLStreamException e) {
      log.error(e.getMessage(), e);
      throw new BatchConfirmationXmlException(
          "XMLStreamException reading " + filename + "." + e.getMessage());
    } finally {
      try {
        if (null != reader) {
          reader.close();
        }
      } catch (XMLStreamException e) {
        log.error("Could not close XMLStreamReader", e);
      }
    }
    return batchBuilder.processedBadges(processedBadges).build();
  }

  private ProcessedBadge readConfirmation(XMLStreamReader reader, String filename)
      throws XMLStreamException, BatchConfirmationXmlException {
    ProcessedBadge.ProcessedBadgeBuilder badgeBuilder = ProcessedBadge.builder();

    while (reader.hasNext()) {
      int eventType = reader.next();
      String elementName;
      if (eventType == XMLStreamReader.START_ELEMENT) {
        elementName = reader.getLocalName();
        if (BADGE_ID.equalsIgnoreCase(elementName)) {
          badgeBuilder.badgeNumber(reader.getElementText());
        } else if (CANCELLATION.equalsIgnoreCase(elementName)) {
          badgeBuilder.cancellation(
              ProcessedBadge.CancellationEnum.fromValue(reader.getElementText()));
        } else if (DISPATCHED_DATE.equalsIgnoreCase(elementName)) {
          badgeBuilder.dispatchedDate(parseIsoDateTime(reader.getElementText()));
        } else {
          throw buildAndLogException(
              "Unexpected xml element whilst parsing confirmation:" + elementName + ".",
              reader,
              filename);
        }
      } else if (eventType == XMLStreamReader.END_ELEMENT) {
        elementName = reader.getLocalName();
        if (CONFIRMATION.equals(elementName)) {
          ProcessedBadge badge = badgeBuilder.build();
          // Check required elements
          if (null == badge.getBadgeNumber()
              || null == badge.getCancellation()
              || null == badge.getDispatchedDate()) {
            throw buildAndLogException(
                "Missing element processing confirmation." + badge + ".", reader, filename);
          }
          return badge;
        }
      }
    }
    throw buildAndLogException("Premature end of file parsing confirmation.", reader, filename);
  }

  private ProcessedBadge readRejection(XMLStreamReader reader, String filename)
      throws XMLStreamException, BatchConfirmationXmlException {
    ProcessedBadge.ProcessedBadgeBuilder badgeBuilder = ProcessedBadge.builder();

    while (reader.hasNext()) {
      int eventType = reader.next();
      String elementName;
      if (eventType == XMLStreamReader.START_ELEMENT) {
        elementName = reader.getLocalName();
        if (BADGE_ID.equalsIgnoreCase(elementName)) {
          badgeBuilder.badgeNumber(reader.getElementText());
        } else if (ERROR_MESSAGE.equalsIgnoreCase(elementName)) {
          badgeBuilder.errorMessage(reader.getElementText());
        } else {
          throw buildAndLogException(
              "Unexpected xml element whilst parsing rejection:" + elementName, reader, filename);
        }
      } else if (eventType == XMLStreamReader.END_ELEMENT) {
        elementName = reader.getLocalName();
        if (REJECTION.equalsIgnoreCase(elementName)) {
          ProcessedBadge badge = badgeBuilder.build();
          // Check required elements
          if (null == badge.getBadgeNumber() || null == badge.getErrorMessage()) {
            throw buildAndLogException(
                "Missing element processing rejection." + badge + ".", reader, filename);
          }
          return badgeBuilder.build();
        }
      }
    }
    throw buildAndLogException("Premature end of file processing rejection.", reader, filename);
  }

  OffsetDateTime parseIsoDateTime(String stringValue) {
    if (StringUtils.isNotEmpty(stringValue)) {
      if (!stringValue.endsWith("Z")) {
        stringValue = stringValue + "Z";
      }

      return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(stringValue, OffsetDateTime::from);
    }
    return null;
  }

  private BatchConfirmationXmlException buildAndLogException(
      String message, XMLStreamReader reader, String filename) {
    BatchConfirmationXmlException e = new BatchConfirmationXmlException(message, reader, filename);
    log.error(e.getDetailedError());
    return e;
  }
}
