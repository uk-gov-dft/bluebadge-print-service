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
import uk.gov.dft.bluebadge.service.printservice.config.GeneralConfig;
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
  private GeneralConfig generalConfig;

  ModelToXmlConverter(
      StorageService s3, ReferenceDataService referenceData, GeneralConfig generalConfig) {
    this.s3 = s3;
    this.referenceData = referenceData;
    this.generalConfig = generalConfig;
  }

  public String toXml(Batch batch, Path xmlDir) throws XMLStreamException, IOException {

    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    Path xmlFileName = createXmlFile(batch.getBatchType().equals(FASTTRACK), xmlDir);
    XMLStreamWriter writer = null;

    try {
      writer =
          factory.createXMLStreamWriter(new FileOutputStream(xmlFileName.toString()), "Cp1252");

      writer.writeStartDocument();
      writer.writeStartElement("BadgePrintExtract");
      writer.writeStartElement("Batch");

      writeAndCloseElement(writer, "Filename", xmlFileName.getFileName().toString());
      writeAndCloseElement(writer, "ReExtract", "no");

      writer.writeEndElement(); // End Batch

      Map<String, List<Badge>> ordered = groupByLA(batch);

      writer.writeStartElement("LocalAuthorities");
      for (Map.Entry<String, List<Badge>> entry : ordered.entrySet()) {
        writer.writeStartElement("LocalAuthority");
        writeLocalAuthority(writer, entry.getKey());

        writer.writeStartElement("Badges");
        for (Badge badge : entry.getValue()) {
          writeAndCloseBadgeDetailsElement(writer, badge);
        }
        writer.writeEndElement(); // End Badges
        writer.writeEndElement(); // End LocalAuthority
      }
      writer.writeEndElement(); // End LocalAuthorities
      writer.writeEndElement(); // End BadgePrintExtract
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
    writer.writeStartElement("BadgeDetails");

    writeAndCloseElement(writer, "BadgeIdentifier", badge.getBadgeNumber());
    writeAndCloseElement(writer, "PrintedBadgeReference", getPrintedBadgeReference(badge));
    writeAndCloseElement(
        writer,
        "StartDate",
        badge.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    writeAndCloseElement(
        writer,
        "ExpiryDate",
        badge.getExpiryDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    writeAndCloseElement(
        writer, "DispatchMethodCode", mapDispatchMethodCode(badge.getDeliverToCode()));
    writeAndCloseElement(writer, "FastTrackCode", mapFastTrackCode(badge.getDeliveryOptionCode()));
    writeAndCloseElement(writer, "PostageCode", mapPostageCode(badge.getDeliveryOptionCode()));
    writeAndClosePhotoElement(writer, badge);
    writeAndCloseElement(writer, "BarCodeData", getBarCode(badge, getPrintedBadgeReference(badge)));
    writeAndCloseNameElements(writer, getHolder(badge));
    writeAndCloseLetterAddressElement(writer, badge);

    writer.writeEndElement();
  }

  private void writeAndClosePhotoElement(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException, IOException {
    writer.writeStartElement("Photo");

    if (isPerson(badge)) {
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

  private void writeAndCloseLetterAddressElement(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException {
    String deliverTo = badge.getDeliverToCode();

    writer.writeStartElement("LetterAddress");
    if (deliverTo.equalsIgnoreCase("HOME")) {
      writeHomeLetterAddressElements(writer, badge);

    } else {
      writeCouncilLetterAddressElements(writer, badge);
    }
    writer.writeEndElement();
  }

  private void writeCouncilLetterAddressElements(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException {
    LocalAuthorityMetaData la =
        referenceData
            .retrieveLocalAuthority(badge.getLocalAuthorityShortCode())
            .getLocalAuthorityMetaData();

    writeAndCloseElement(writer, "NameLine1", getHolder(badge));
    writeAndCloseElement(writer, "AddressLine1", la.getAddressLine1());
    writeAndCloseElement(writer, "AddressLine2", la.getAddressLine2());
    writeAndCloseElement(writer, "Town", la.getTown());
    writeAndCloseElement(writer, "Country", "United Kingdom");
    writeAndCloseElement(writer, "Postcode", la.getPostcode());
  }

  private void writeHomeLetterAddressElements(XMLStreamWriter writer, Badge badge)
      throws XMLStreamException {
    Contact contact = badge.getParty().getContact();

    writeAndCloseElement(writer, "NameLine1", contact.getFullName());
    writeAndCloseElement(writer, "AddressLine1", contact.getBuildingStreet());
    writeAndCloseElement(writer, "AddressLine2", contact.getLine2());
    writeAndCloseElement(writer, "Town", contact.getTownCity());
    writeAndCloseElement(writer, "Country", "United Kingdom");
    writeAndCloseElement(writer, "Postcode", contact.getPostCode());
  }

  private void writeAndCloseNameElements(XMLStreamWriter writer, String holder)
      throws XMLStreamException {
    String name = holder;
    String surname = "";

    if (holder.length() > 28) {
      int idx = holder.indexOf(' ');
      name = holder.substring(0, idx).trim();
      surname = holder.substring(idx).trim();
    }

    writeAndCloseElement(writer, "Name", name);
    writeAndCloseElement(writer, "Surname", surname);
  }

  private void writeLocalAuthority(XMLStreamWriter writer, String laCode)
      throws XMLStreamException {
    LocalAuthorityRefData la = referenceData.retrieveLocalAuthority(laCode);

    Nation nation = la.getLocalAuthorityMetaData().getNation();

    writeAndCloseElement(writer, "LACode", la.getShortCode());
    writeAndCloseElement(writer, "LAName", la.getDescription());
    writeAndCloseElement(writer, "IssuingCountry", nation.getXmlPrintFileCode());
    writeAndCloseElement(writer, "LanguageCode", Nation.WLS == nation ? "EW" : "E");
    writeAndCloseElement(writer, "ClockType", Nation.WLS == nation ? "Wallet" : "Standard");
    writeAndCloseElement(writer, "PhoneNumber", la.getLocalAuthorityMetaData().getContactNumber());
    writeAndCloseElement(writer, "EmailAddress", la.getLocalAuthorityMetaData().getEmailAddress());
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
    String dob =
        isPerson(badge)
            ? badge.getParty().getPerson().getDob().format(DateTimeFormatter.ofPattern("MMyy"))
            : "";
    String gender = isPerson(badge) ? mapGender(badge.getParty().getPerson().getGenderCode()) : "O";
    String expiry = badge.getExpiryDate().format(DateTimeFormatter.ofPattern("MMyy"));

    return badge.getBadgeNumber() + " 9 " + dob + gender + expiry;
  }

  private String getBarCode(Badge badge, String reference) {
    return isPerson(badge) ? StringUtils.right(reference, 7) : StringUtils.right(reference, 5);
  }

  private String getHolder(Badge badge) {

    return isPerson(badge)
        ? badge.getParty().getPerson().getBadgeHolderName()
        : badge.getParty().getOrganisation().getBadgeHolderName();
  }

  private boolean isPerson(Badge badge) {
    return badge.getParty().getTypeCode().equals(PERSON);
  }
}
