package uk.gov.dft.bluebadge.service.printservice.converters;

import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.dft.bluebadge.service.printservice.StorageService;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.LocalAuthorityRefData;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.LocalAuthorityRefData.LocalAuthorityMetaData;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.Nation;
import uk.gov.dft.bluebadge.service.printservice.config.GeneralConfig;
import uk.gov.dft.bluebadge.service.printservice.model.Badge;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.Contact;
import uk.gov.dft.bluebadge.service.printservice.referencedata.ReferenceDataService;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.Common.DATE_PATTERN;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.Common.XML_ENCODING;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.Common.XML_VERSION;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_COUNTRY;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_COUNTRY_VALUE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_LINE1;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_LINE2;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_LINE3;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_LINE4;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_NAME1;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_NAME2;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_POSTCODE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ADDRESS_TOWN;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.BADGES;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.BADGE_DETAIL;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.BADGE_IDENTIFIER;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.BADGE_REFERENCE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.BAR_CODE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.BATCH;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.CLOCK_TYPE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.COLLECTION_ADDRESS;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.DISPATCH_METHOD;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.EXPIRY_DATE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.FASTTRACK_CODE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.FILENAME;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.FORENAME;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ISSUING_COUNTRY;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LANGUAGE_CODE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LA_CODE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LA_EMAIL;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LA_NAME;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LA_PHONE_NO;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LETTER_ADDRESS;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LOCAL_AUTHORITIES;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.LOCAL_AUTHORITY;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.NAME;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ORG_NAME;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.PHOTO;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.POSTAGE_CODE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.RE_EXTRACT;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.RE_EXTRACT_VALUE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.ROOT;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.START_DATE;
import static uk.gov.dft.bluebadge.service.printservice.converters.XmlSchemaConstants.PrintRequestElements.SURNAME;
import static uk.gov.dft.bluebadge.service.printservice.model.Batch.BatchTypeEnum.FASTTRACK;

@Component
@Slf4j
public class PrintRequestToPrintXml {
  private final StorageService s3;
  private final ReferenceDataService referenceData;
  private GeneralConfig generalConfig;

  PrintRequestToPrintXml(
      StorageService s3, ReferenceDataService referenceData, GeneralConfig generalConfig) {
    this.s3 = s3;
    this.referenceData = referenceData;
    this.generalConfig = generalConfig;
  }

  public String toXml(Batch batch, Path xmlDir) throws XMLStreamException, IOException {

    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    Path xmlFileName = createXmlFile(batch.getBatchType().equals(FASTTRACK), xmlDir);
    XMLStreamWriter writer = null;

    try (FileOutputStream fos = new FileOutputStream(xmlFileName.toString())) {
      writer =
          factory.createXMLStreamWriter(fos, "Cp1252");

      writer.writeStartDocument(XML_ENCODING, XML_VERSION);
      writer.writeStartElement(ROOT);
      writer.writeStartElement(BATCH);

      writeAndCloseElement(writer, FILENAME, xmlFileName.getFileName().toString());
      writeAndCloseElement(writer, RE_EXTRACT, RE_EXTRACT_VALUE);

      writer.writeEndElement(); // End Batch

      Map<String, List<Badge>> ordered = groupByLA(batch);

      writer.writeStartElement(LOCAL_AUTHORITIES);
      for (Map.Entry<String, List<Badge>> entry : ordered.entrySet()) {
        writer.writeStartElement(LOCAL_AUTHORITY);
        writeLocalAuthority(writer, entry.getKey());

        writer.writeStartElement(BADGES);
        for (Badge badge : entry.getValue()) {
          writeAndCloseBadgeDetailsElement(writer, badge);
        }
        writer.writeEndElement(); // End Badges
        writer.writeEndElement(); // End LocalAuthority
      }
      writer.writeEndElement(); // End LocalAuthorities
      writer.writeEndElement(); // End BadgePrintExtract (ROOT)
      writer.writeEndDocument();

      writer.flush();

    } finally {
      if (null != writer) {
        writer.close();
      }
    }
    return xmlFileName.toString();
  }

  private Path createXmlFile(boolean isFasttrack, Path xmlDir) throws IOException {
    String suffix = isFasttrack ? "-FastTrack" : "";
    Path xmlFile =
        xmlDir.resolve(
            "BADGEEXTRACT_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"))
                + suffix
                + ".xml");

    Files.createDirectories(xmlFile.getParent());
    Files.createFile(xmlFile);

    return xmlFile;
  }

  private void writeAndCloseBadgeDetailsElement(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException, IOException {
    writer.writeStartElement(BADGE_DETAIL);

    writeAndCloseElement(writer, BADGE_IDENTIFIER, badge.getBadgeNumber());
    writeAndCloseElement(writer, BADGE_REFERENCE, getPrintedBadgeReference(badge));
    writeAndCloseElement(
        writer, START_DATE, badge.getStartDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
    writeAndCloseElement(
        writer,
        EXPIRY_DATE,
        badge.getExpiryDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
    writeAndCloseElement(writer, DISPATCH_METHOD, badge.getDeliverToCode().getXmlPrintFileCode());
    writeAndCloseElement(
        writer, FASTTRACK_CODE, badge.getDeliveryOptionCode().getXmlFasttrackCode());
    writeAndCloseElement(writer, POSTAGE_CODE, badge.getDeliveryOptionCode().getXmlPostageCode());
    writeAndClosePhotoElement(writer, badge);
    writeAndCloseElement(writer, BAR_CODE, getBarCode(badge));

    writeAndCloseNameElement(writer, badge);
    writeLetterAddressElements(writer, badge);
    if (Badge.DeliverToCode.COUNCIL == badge.getDeliverToCode()) {
      writeCollectionAddressElements(writer, badge);
    }

    writer.writeEndElement();
  }

  private void writeAndClosePhotoElement(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException, IOException {
    writer.writeStartElement(PHOTO);

    if (badge.isPersonBadge() && StringUtils.isNotEmpty(badge.getImageLink())) {
      Optional<byte[]> imageFile = s3.downloadBadgeFile(badge.getImageLink());
      if (imageFile.isPresent()) {
        String image = toBase64(imageFile.get());
        writer.writeCharacters(image);
      }
    } else {
      LocalAuthorityRefData la =
          referenceData.retrieveLocalAuthority(badge.getLocalAuthorityShortCode());

      String imageFileName;
      if (la.getLocalAuthorityMetaData().getNation() == Nation.WLS) {
        imageFileName = generalConfig.getOrganisationPhotoUriWales();
      } else {
        imageFileName = generalConfig.getOrganisationPhotoUriEngland();
      }
      writer.writeCharacters(
          toBase64(IOUtils.toByteArray(getClass().getResourceAsStream(imageFileName))));
    }
    writer.writeEndElement();
  }

  private void writeCollectionAddressElements(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException {

    LocalAuthorityRefData la = referenceData.retrieveLocalAuthority(badge.getLocalAuthorityShortCode());
    LocalAuthorityMetaData laMeta = la.getLocalAuthorityMetaData();

    writer.writeStartElement(COLLECTION_ADDRESS);
    writeAndCloseElement(writer, ADDRESS_NAME1, la.getDescription());
    writeAndCloseElement(writer, ADDRESS_NAME2, laMeta.getNameLine2());
    writeAndCloseElement(writer, ADDRESS_LINE1, laMeta.getAddressLine1());
    writeAndCloseElement(writer, ADDRESS_LINE2, laMeta.getAddressLine2());
    writeAndCloseElement(writer, ADDRESS_LINE3, laMeta.getAddressLine3());
    writeAndCloseElement(writer, ADDRESS_LINE4, laMeta.getAddressLine4());
    writeAndCloseElement(writer, ADDRESS_TOWN, laMeta.getTown());
    writeAndCloseElement(writer, ADDRESS_COUNTRY, ADDRESS_COUNTRY_VALUE);
    writeAndCloseElement(writer, ADDRESS_POSTCODE, laMeta.getPostcode());
    writer.writeEndElement();
  }

  private void writeLetterAddressElements(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException {
    Contact contact = badge.getParty().getContact();

    writer.writeStartElement(LETTER_ADDRESS);
    writeAndCloseElement(writer, ADDRESS_NAME1, contact.getFullName());
    writeAndCloseElement(writer, ADDRESS_LINE1, contact.getBuildingStreet());
    writeAndCloseElement(writer, ADDRESS_LINE2, contact.getLine2());
    writeAndCloseElement(writer, ADDRESS_TOWN, contact.getTownCity());
    writeAndCloseElement(writer, ADDRESS_COUNTRY, ADDRESS_COUNTRY_VALUE);
    writeAndCloseElement(writer, ADDRESS_POSTCODE, contact.getPostCode());
    writer.writeEndElement();
  }

  private void writeAndCloseNameElement(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException {

    writer.writeStartElement(NAME);
    String name = getHolderName(badge);
    if (badge.isPersonBadge()) {
      Pair<String, String> names = getSurnameForenamePair(name);
      writeAndCloseElement(writer, SURNAME, names.getLeft());
      if (StringUtils.isNotEmpty(names.getRight())) {
        writeAndCloseElement(writer, FORENAME, names.getRight());
      }
    } else {
      writeAndCloseElement(writer, ORG_NAME, name);
    }
    writer.writeEndElement(); // End Name
  }

  /**
   * Splits name
   *
   * @param holder Name to split
   * @return Pair with surname as left and forename as right
   */
  Pair<String, String> getSurnameForenamePair(String holder) {
    String surname;
    String forename = null;
    if (holder.length() > 28) {
      int idx = holder.lastIndexOf(' ');
      forename = holder.substring(0, idx).trim();
      surname = holder.substring(idx).trim();
    } else {
      surname = holder;
    }

    return Pair.of(surname, forename);
  }

  private void writeLocalAuthority(XMLStreamWriter writer, String laCode)
      throws XMLStreamException {
    LocalAuthorityRefData la = referenceData.retrieveLocalAuthority(laCode);

    Nation nation = la.getLocalAuthorityMetaData().getNation();

    writeAndCloseElement(writer, LA_CODE, la.getShortCode());
    writeAndCloseElement(writer, LA_NAME, la.getDescription());
    writeAndCloseElement(writer, ISSUING_COUNTRY, nation.getXmlPrintFileIssuingCountry());
    writeAndCloseElement(writer, LANGUAGE_CODE, nation.getXmlPrintFileLanguageCode());
    writeAndCloseElement(writer, CLOCK_TYPE, nation.getXmlPrintFileClockType());
    writeAndCloseElement(writer, LA_PHONE_NO, la.getLocalAuthorityMetaData().getContactNumber());
    writeAndCloseElement(writer, LA_EMAIL, la.getLocalAuthorityMetaData().getEmailAddress());
  }

  private String toBase64(byte[] src) {
    return Base64.getEncoder().encodeToString(src);
  }

  private Map<String, List<Badge>> groupByLA(Batch batch) {

    return batch.getBadges().stream().collect(groupingBy(Badge::getLocalAuthorityShortCode));
  }

  private void writeAndCloseElement(XMLStreamWriter writer, String tag, String text)
      throws XMLStreamException {
    writer.writeStartElement(tag);
    writer.writeCharacters(text);
    writer.writeEndElement();
  }

  String getPrintedBadgeReference(Badge badge) {
    String dob = "";
    String gender;
    if (badge.isPersonBadge()) {
      // Party is notnull in bean
      // Dob is not null in person bean.
      // Check person is not null
      Assert.notNull(badge.getParty().getPerson(), "Person cannot be null if party is person");
      dob = badge.getParty().getPerson().getDob().format(DateTimeFormatter.ofPattern("MMyy"));
      gender = badge.getParty().getPerson().getGenderCode().getXmlPrintFileCode();
    } else {
      gender = "O";
    }
    // Expiry date is notnull in bean
    String expiry = badge.getExpiryDate().format(DateTimeFormatter.ofPattern("MMyy"));

    return badge.getBadgeNumber() + " 0 " + dob + gender + expiry;
  }

  String getBarCode(Badge badge) {
    if (badge.isPersonBadge()) {
      return StringUtils.right(getPrintedBadgeReference(badge), 7);
    } else {
      return StringUtils.right(getPrintedBadgeReference(badge), 5);
    }
  }

  private String getHolderName(Badge badge) {
    if (badge.isPersonBadge()) {
      return badge.getParty().getPerson().getBadgeHolderName();
    } else {
      return badge.getParty().getOrganisation().getBadgeHolderName();
    }
  }
}
