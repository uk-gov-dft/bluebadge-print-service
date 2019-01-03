package uk.gov.dft.bluebadge.service.printservice.utils;

import static java.util.stream.Collectors.groupingBy;
import static uk.gov.dft.bluebadge.service.printservice.model.Batch.BatchTypeEnum.FASTTRACK;

import com.amazonaws.util.IOUtils;
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
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.dft.bluebadge.service.printservice.StorageService;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.LocalAuthorityRefData;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.LocalAuthorityRefData.LocalAuthorityMetaData;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.Nation;
import uk.gov.dft.bluebadge.service.printservice.model.Badge;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.Contact;
import uk.gov.dft.bluebadge.service.printservice.referencedata.ReferenceDataService;

@Component
@Slf4j
public class ModelToXmlConverter {
  private static final String PERSON = "PERSON";
  private final StorageService s3;
  private final ReferenceDataService referenceData;

  ModelToXmlConverter(StorageService s3, ReferenceDataService referenceData) {
    this.s3 = s3;
    this.referenceData = referenceData;
  }

  public String toXml(Batch batch, Path xmlDir) throws XMLStreamException, IOException {

    XMLOutputFactory factory = XMLOutputFactory.newInstance();

    Path xmlFileName = createXmlFile(batch.getBatchType().equals(FASTTRACK), xmlDir);

    XMLStreamWriter writer =
        factory.createXMLStreamWriter(new FileOutputStream(xmlFileName.toString()), "Cp1252");

    writer.writeStartDocument();
    writer.writeStartElement("BadgePrintExtract");
    writer.writeStartElement("Batch");

    writer.writeStartElement("Filename");
    writer.writeCharacters(xmlFileName.getFileName().toString());
    writer.writeEndElement();

    writer.writeStartElement("ReExtract");
    writer.writeCharacters("no");
    writer.writeEndElement();
    writer.writeEndElement();

    Map<String, List<Badge>> ordered = groupByLA(batch);

    writer.writeStartElement("LocalAuthorities");
    for (Map.Entry<String, List<Badge>> entry : ordered.entrySet()) {
      writer.writeStartElement("LocalAuthority");
      writeLocalAuthority(writer, entry.getKey());
      writer.writeStartElement("Badges");
      for (Badge badge : entry.getValue()) {
        writeBadgeDetails(writer, badge, entry.getKey());
      }
      writer.writeEndElement();
      writer.writeEndElement();
    }
    writer.writeEndElement();
    writer.writeEndElement();
    writer.writeEndDocument();

    writer.flush();
    writer.close();

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

  private void writeBadgeDetails(XMLStreamWriter writer, Badge badge, String laCode)
      throws XMLStreamException, IOException {
    writer.writeStartElement("BadgeDetails");

    writer.writeStartElement("BadgeIdentifier");
    writer.writeCharacters(badge.getBadgeNumber());
    writer.writeEndElement();

    writer.writeStartElement("PrintedBadgeReference");
    String reference = getPrintedBadgeReference(badge);
    writer.writeCharacters(reference);
    writer.writeEndElement();

    writer.writeStartElement("StartDate");
    writer.writeCharacters(badge.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    writer.writeEndElement();

    writer.writeStartElement("ExpiryDate");
    writer.writeCharacters(badge.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    writer.writeEndElement();

    writer.writeStartElement("DispatchMethodCode");
    writer.writeCharacters(mapDispatchMethodCode(badge.getDeliverToCode()));
    writer.writeEndElement();

    writer.writeStartElement("FastTrackCode");
    writer.writeCharacters(mapFastTrackCode(badge.getDeliveryOptionCode()));
    writer.writeEndElement();

    writer.writeStartElement("PostageCode");
    writer.writeCharacters(mapPostageCode(badge.getDeliveryOptionCode()));
    writer.writeEndElement();

    writer.writeStartElement("Photo");
    Optional<byte[]> imageFile = s3.downloadBadgeFile(badge.getImageLink());
    if (imageFile.isPresent()) {
      String image = toBase64(imageFile.get());
      writer.writeCharacters(image);
    } else {
      boolean isOrganisation = badge.getParty().getTypeCode().equals("ORG");
      if (isOrganisation) {
        LocalAuthorityRefData la = referenceData.retrieveLocalAuthority(laCode);

        String imageFileName;
        if (la.getLocalAuthorityMetaData().getNation() == Nation.WLS) {
          imageFileName = "/pictures/org_W.jpg";
        } else {
          imageFileName = "/pictures/org_E.jpg";
        }
        writer.writeCharacters(
            toBase64(IOUtils.toByteArray(getClass().getResourceAsStream(imageFileName))));
      }
    }
    writer.writeEndElement();

    writer.writeStartElement("BarCodeData");
    writer.writeCharacters(getBarCode(badge, reference));
    writer.writeEndElement();

    writeName(writer, getHolder(badge));

    writeLetterAddress(writer, badge, laCode);

    writer.writeEndElement();
  }

  private void writeLetterAddress(XMLStreamWriter writer, Badge badge, String laCode)
      throws XMLStreamException {
    String deliverTo = badge.getDeliverToCode();

    if (deliverTo.equalsIgnoreCase("HOME")) {
      writeHomeLetterAddress(writer, badge);

    } else {
      writeCouncilLetterAddress(writer, badge, laCode);
    }
  }

  private void writeCouncilLetterAddress(XMLStreamWriter writer, Badge badge, String laCode)
      throws XMLStreamException {
    LocalAuthorityMetaData la =
        referenceData.retrieveLocalAuthority(laCode).getLocalAuthorityMetaData();

    writer.writeStartElement("LetterAddress");

    writer.writeStartElement("NameLine1");
    writer.writeCharacters(getHolder(badge));
    writer.writeEndElement();

    writer.writeStartElement("AddressLine1");
    writer.writeCharacters(la.getAddressLine1());
    writer.writeEndElement();

    writer.writeStartElement("AddressLine2");
    writer.writeCharacters(la.getAddressLine2());
    writer.writeEndElement();

    writer.writeStartElement("Town");
    writer.writeCharacters(la.getTown());
    writer.writeEndElement();

    writer.writeStartElement("Country");
    writer.writeCharacters("United Kingdom");
    writer.writeEndElement();

    writer.writeStartElement("Postcode");
    writer.writeCharacters(la.getPostcode());
    writer.writeEndElement();

    writer.writeEndElement();
  }

  private void writeHomeLetterAddress(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException {
    Contact contact = badge.getParty().getContact();
    writer.writeStartElement("LetterAddress");

    writer.writeStartElement("NameLine1");
    writer.writeCharacters(contact.getFullName());
    writer.writeEndElement();

    writer.writeStartElement("AddressLine1");
    writer.writeCharacters(contact.getBuildingStreet());
    writer.writeEndElement();

    writer.writeStartElement("AddressLine2");
    writer.writeCharacters(contact.getLine2());
    writer.writeEndElement();

    writer.writeStartElement("Town");
    writer.writeCharacters(contact.getTownCity());
    writer.writeEndElement();

    writer.writeStartElement("Country");
    writer.writeCharacters("United Kingdom");
    writer.writeEndElement();

    writer.writeStartElement("Postcode");
    writer.writeCharacters(contact.getPostCode());
    writer.writeEndElement();

    writer.writeEndElement();
  }

  private void writeName(XMLStreamWriter writer, String holder) throws XMLStreamException {
    String name = holder;
    String surname = "";

    if (holder.length() > 28) {
      int idx = holder.indexOf(' ');
      name = holder.substring(0, idx).trim();
      surname = holder.substring(idx).trim();
    }
    writer.writeStartElement("Name");
    writer.writeCharacters(name);
    writer.writeEndElement();

    writer.writeStartElement("Surname");
    writer.writeCharacters(surname);
    writer.writeEndElement();
  }

  private void writeLocalAuthority(XMLStreamWriter writer, String laCode)
      throws XMLStreamException {
    LocalAuthorityRefData la = referenceData.retrieveLocalAuthority(laCode);

    Nation nation = la.getLocalAuthorityMetaData().getNation();

    writer.writeStartElement("LACode");
    writer.writeCharacters(la.getShortCode());
    writer.writeEndElement();

    writer.writeStartElement("LAName");
    writer.writeCharacters(la.getDescription());
    writer.writeEndElement();

    writer.writeStartElement("IssuingCountry");
    writer.writeCharacters(nation.getXmlPrintFileCode());
    writer.writeEndElement();

    writer.writeStartElement("LanguageCode");
    String language = Nation.WLS == nation ? "EW" : "E";
    writer.writeCharacters(language);
    writer.writeEndElement();

    writer.writeStartElement("ClockType");
    String clock = Nation.WLS == nation ? "Wallet" : "Standard";
    writer.writeCharacters(clock);
    writer.writeEndElement();

    writer.writeStartElement("PhoneNumber");
    writer.writeCharacters(la.getLocalAuthorityMetaData().getContactNumber());
    writer.writeEndElement();

    writer.writeStartElement("EmailAddress");
    writer.writeCharacters(la.getLocalAuthorityMetaData().getEmailAddress());
    writer.writeEndElement();
  }

  private String toBase64(byte[] src) {
    return Base64.getEncoder().encodeToString(src);
  }

  private Map<String, List<Badge>> groupByLA(Batch batch) {

    return batch.getBadges().stream().collect(groupingBy(Badge::getLocalAuthorityShortCode));
  }

  private String mapGender(String code) {
    switch (code) {
      case "MALE":
        return "X";
      case "FEMALE":
        return "Y";
      default:
        return "Z";
    }
  }

  private String mapDispatchMethodCode(String code) {
    return code.equalsIgnoreCase("HOME") ? "M" : "C";
  }

  private String mapFastTrackCode(String code) {
    return code.equalsIgnoreCase("FAST") ? "Y" : "N";
  }

  private String mapPostageCode(String code) {
    return code.equalsIgnoreCase("FAST") ? "SD1" : "SC";
  }

  private String getPrintedBadgeReference(Badge badge) {
    boolean isPerson = badge.getParty().getTypeCode().equals(PERSON);
    String dob =
        isPerson
            ? badge.getParty().getPerson().getDob().format(DateTimeFormatter.ofPattern("MMyy"))
            : "";
    String gender = isPerson ? mapGender(badge.getParty().getPerson().getGenderCode()) : "O";
    String expiry = badge.getExpiryDate().format(DateTimeFormatter.ofPattern("MMyy"));

    return badge.getBadgeNumber() + " 9 " + dob + gender + expiry;
  }

  private String getBarCode(Badge badge, String reference) {
    boolean isPerson = badge.getParty().getTypeCode().equals(PERSON);

    return isPerson ? StringUtils.right(reference, 7) : StringUtils.right(reference, 5);
  }

  private String getHolder(Badge badge) {
    boolean isPerson = badge.getParty().getTypeCode().equals(PERSON);

    return isPerson
        ? badge.getParty().getPerson().getBadgeHolderName()
        : badge.getParty().getOrganisation().getBadgeHolderName();
  }
}
