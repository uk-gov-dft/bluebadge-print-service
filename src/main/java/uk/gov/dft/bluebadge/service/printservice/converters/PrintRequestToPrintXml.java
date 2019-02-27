package uk.gov.dft.bluebadge.service.printservice.converters;

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

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.util.IOUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
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

@Component
@Slf4j
public class PrintRequestToPrintXml {
  private final StorageService s3;
  private ReferenceDataService referenceData;
  private GeneralConfig generalConfig;

  PrintRequestToPrintXml(StorageService s3, GeneralConfig generalConfig) {
    this.s3 = s3;
    this.generalConfig = generalConfig;
  }

  public String toXml(Batch batch, Path xmlDir, ReferenceDataService referenceData)
      throws XMLStreamException, IOException {
    this.referenceData = referenceData;
    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    factory.setProperty("escapeCharacters", false); // To avoid escaping <, > and &
    Path xmlFileName = createXmlFile(batch, xmlDir);
    XMLStreamWriter writer;

    try (FileOutputStream fos = new FileOutputStream(xmlFileName.toString())) {
      log.info("Beginning xml write {}", batch.getFilename());
      writer = factory.createXMLStreamWriter(fos, "Cp1252");

      writer.writeStartDocument(XML_ENCODING, XML_VERSION);
      writer.writeStartElement(ROOT);
      writer.writeStartElement(BATCH);

      writeAndCloseElement(writer, FILENAME, xmlFileName.getFileName().toString());
      writeAndCloseElement(writer, RE_EXTRACT, RE_EXTRACT_VALUE);

      writer.writeEndElement(); // End Batch

      Map<String, List<Badge>> ordered = groupByLA(batch);

      writer.writeStartElement(LOCAL_AUTHORITIES);
      for (Map.Entry<String, List<Badge>> entry : ordered.entrySet()) {
        log.info(
            "Writing print batch xml content for la: {}, count:{}",
            entry.getKey(),
            entry.getValue().size());

        StringBuilder localAuthorityBufferString = new StringBuilder("<" + LOCAL_AUTHORITY + ">");
        localAuthorityBufferString.append(getLocalAuthorityXmlString(entry.getKey()));
        int count = 0;
        localAuthorityBufferString.append("<" + BADGES + ">");
        for (Badge badge : entry.getValue()) {
          try {
            localAuthorityBufferString.append(getBadgeDetailsXmlString(badge));
            count++;
            if (count % 100 == 0) {
              log.debug("Written {} badge xml records for {}...", count, entry.getKey());
            }
          } catch (Exception ex) {
            log.error(
                "Skipping badge [{}] in batch [{}], due to an exception [{}], exception stack trace:",
                badge.getBadgeNumber(),
                batch.getFilename(),
                ex.getMessage(),
                ex);
          }
        }
        localAuthorityBufferString.append("</" + BADGES + ">");
        localAuthorityBufferString.append("</" + LOCAL_AUTHORITY + ">");

        if (count > 0) {
          writer.writeCharacters(localAuthorityBufferString.toString());
        }
        log.info("Finished writing xml for {}", entry.getKey());
      }
      writer.writeEndElement(); // End LocalAuthorities
      writer.writeEndElement(); // End BadgePrintExtract (ROOT)
      writer.writeEndDocument();

      writer.flush();
      // Not in finally block as underlying stream gets closed (closable) before finally.
      // In the case of an exception, stream is closed and writer.close will again
      // attempt to close the stream and throw an exception (even though it is
      // documented not to do this).
      writer.close();
    }
    return xmlFileName.toString();
  }

  private Path createXmlFile(Batch batch, Path xmlDir) throws IOException {

    Path xmlFile = xmlDir.resolve(batch.getFilename() + ".xml");

    Files.createDirectories(xmlFile.getParent());
    Files.deleteIfExists(xmlFile);
    Files.createFile(xmlFile);

    return xmlFile;
  }

  private String getPhotoElementXmlString(Badge badge) throws IOException {
    StringBuilder xmlString = new StringBuilder();
    xmlString.append(String.format("<%s>", PHOTO));
    if (badge.isPersonBadge() && StringUtils.isNotEmpty(badge.getImageLink())) {
      Optional<byte[]> imageFile = Optional.empty();
      try {
        imageFile = s3.downloadBadgeFile(badge.getImageLink());
      } catch (AmazonS3Exception e) {
        log.error("Could not find s3 object for key " + badge.getImageLink(), e);
        throw new IOException("Could not find image for key " + badge.getImageLink());
      }
      if (imageFile.isPresent()) {
        String image = toBase64(imageFile.get());
        xmlString.append(image);
      } else {
        log.error("Could not find image for key [{}]" + badge.getImageLink());
        throw new IOException("Could not find image for key " + badge.getImageLink());
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
      xmlString.append(
          toBase64(IOUtils.toByteArray(getClass().getResourceAsStream(imageFileName))));
    }
    xmlString.append(String.format("</%s>", PHOTO));

    return xmlString.toString();
  }

  private String getCollectionAddressElementsXmlString(Badge badge) {
    StringBuilder xmlString = new StringBuilder();

    LocalAuthorityRefData la =
        referenceData.retrieveLocalAuthority(badge.getLocalAuthorityShortCode());
    LocalAuthorityMetaData laMeta = la.getLocalAuthorityMetaData();

    xmlString.append(String.format("<%s>", COLLECTION_ADDRESS));
    xmlString.append(getTagIfValueNotNull(ADDRESS_NAME1, la.getDescription()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_NAME2, laMeta.getNameLine2()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_LINE1, laMeta.getAddressLine1()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_LINE2, laMeta.getAddressLine2()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_LINE3, laMeta.getAddressLine3()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_LINE4, laMeta.getAddressLine4()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_TOWN, laMeta.getTown()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_COUNTRY, ADDRESS_COUNTRY_VALUE));
    xmlString.append(getTagIfValueNotNull(ADDRESS_POSTCODE, laMeta.getPostcode()));
    xmlString.append(String.format("</%s>", COLLECTION_ADDRESS));
    return xmlString.toString();
  }

  private String getNameElementXmlString(Badge badge) {
    StringBuilder xmlString = new StringBuilder();
    xmlString.append(String.format("<%s>", NAME));
    String name = getNameForNameElement(badge);
    Pair<String, String> names = getSurnameForenamePair(name);
    xmlString.append(getTagIfValueNotNull(SURNAME, names.getLeft()));
    if (StringUtils.isNotEmpty(names.getRight())) {
      xmlString.append(getTagIfValueNotNull(FORENAME, names.getRight()));
    }
    xmlString.append(String.format("</%s>", NAME));
    return xmlString.toString();
  }

  private String getLetterAddressElementsXmlString(Badge badge) {
    Contact contact = badge.getParty().getContact();
    StringBuilder xmlString = new StringBuilder();
    xmlString.append(String.format("<%s>", LETTER_ADDRESS));
    xmlString.append(getTagIfValueNotNull(ADDRESS_NAME1, contact.getFullName()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_LINE1, contact.getBuildingStreet()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_LINE2, contact.getLine2()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_TOWN, contact.getTownCity()));
    xmlString.append(getTagIfValueNotNull(ADDRESS_COUNTRY, ADDRESS_COUNTRY_VALUE));
    xmlString.append(getTagIfValueNotNull(ADDRESS_POSTCODE, contact.getPostCode()));
    xmlString.append(String.format("</%s>", LETTER_ADDRESS));
    return xmlString.toString();
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

  private String getLocalAuthorityXmlString(String laCode) {
    LocalAuthorityRefData la = referenceData.retrieveLocalAuthority(laCode);
    LocalAuthorityMetaData metadata = la.getLocalAuthorityMetaData();
    Nation nation = metadata.getNation();

    StringBuilder xmlString = new StringBuilder();
    xmlString.append(getTagIfValueNotNull(LA_CODE, la.getShortCode()));
    xmlString.append(getTagIfValueNotNull(LA_NAME, la.getDescription()));
    xmlString.append(getTagIfValueNotNull(ISSUING_COUNTRY, nation.getXmlPrintFileIssuingCountry()));
    xmlString.append(getTagIfValueNotNull(LANGUAGE_CODE, nation.getXmlPrintFileLanguageCode()));
    xmlString.append(getTagIfValueNotNull(CLOCK_TYPE, metadata.getClockType()));
    xmlString.append(getTagIfValueNotNull(LA_PHONE_NO, metadata.getContactNumber()));
    xmlString.append(getTagIfValueNotNull(LA_EMAIL, metadata.getEmailAddress()));

    return xmlString.toString();
  }

  private String getBadgeDetailsXmlString(Badge badge) throws IOException {
    StringBuilder xmlString = new StringBuilder();
    xmlString.append(String.format("<%s>", BADGE_DETAIL));

    xmlString.append(getTagIfValueNotNull(BADGE_IDENTIFIER, badge.getBadgeNumber()));
    xmlString.append(getTagIfValueNotNull(BADGE_REFERENCE, getPrintedBadgeReference(badge)));
    xmlString.append(
        getTagIfValueNotNull(
            START_DATE, badge.getStartDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN))));
    xmlString.append(
        getTagIfValueNotNull(
            EXPIRY_DATE, badge.getExpiryDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN))));
    xmlString.append(
        getTagIfValueNotNull(DISPATCH_METHOD, badge.getDeliverToCode().getXmlPrintFileCode()));
    xmlString.append(
        getTagIfValueNotNull(FASTTRACK_CODE, badge.getDeliveryOptionCode().getXmlFasttrackCode()));
    xmlString.append(
        getTagIfValueNotNull(POSTAGE_CODE, badge.getDeliveryOptionCode().getXmlPostageCode()));
    xmlString.append(getPhotoElementXmlString(badge));
    xmlString.append(getTagIfValueNotNull(BAR_CODE, getBarCode(badge)));

    if (badge.isOrganisationBadge()) {
      Assert.notNull(
          badge.getParty().getOrganisation(),
          "Organisation badge with no organisation:" + badge.getBadgeNumber());
      xmlString.append(
          getTagIfValueNotNull(ORG_NAME, badge.getParty().getOrganisation().getBadgeHolderName()));
    }
    xmlString.append(getNameElementXmlString(badge));
    xmlString.append(getLetterAddressElementsXmlString(badge));
    if (Badge.DeliverToCode.COUNCIL == badge.getDeliverToCode()) {
      xmlString.append(getCollectionAddressElementsXmlString(badge));
    }
    xmlString.append(String.format("</%s>", BADGE_DETAIL));
    return xmlString.toString();
  }

  private String toBase64(byte[] src) {
    return Base64.getEncoder().encodeToString(src);
  }

  private Map<String, List<Badge>> groupByLA(Batch batch) {

    return batch.getBadges().stream().collect(groupingBy(Badge::getLocalAuthorityShortCode));
  }

  private void writeAndCloseElement(XMLStreamWriter writer, String tag, String text)
      throws XMLStreamException {
    if (StringUtils.isNotEmpty(text)) {
      writer.writeStartElement(tag);
      writer.writeCharacters(text);
      writer.writeEndElement();
    }
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

  private String getNameForNameElement(Badge badge) {
    if (badge.isPersonBadge()) {
      return badge.getParty().getPerson().getBadgeHolderName();
    } else {
      return badge.getParty().getContact().getFullName();
    }
  }

  private String getTagIfValueNotNull(String tag, String value) {
    if (StringUtils.isNotEmpty(value)) {
      return (String.format("<%s>%s</%s>", tag, escapeContent(value), tag));
    }
    return "";
  }

  protected String escapeContent(String raw) {
    if (raw == null) {
      return "";
    }
    return raw.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }
}
