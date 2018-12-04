package uk.gov.dft.bluebadge.service.printservice.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.stereotype.Component;

import uk.gov.dft.bluebadge.model.printservice.generated.BadgeDetails;
import uk.gov.dft.bluebadge.model.printservice.generated.Batch;
import uk.gov.dft.bluebadge.model.printservice.generated.LetterAddress;
import uk.gov.dft.bluebadge.model.printservice.generated.LocalAuthority;
import uk.gov.dft.bluebadge.model.printservice.generated.Name;
import uk.gov.dft.bluebadge.service.printservice.StorageService;

@Component
public class ModelToXmlConverter {
	
	private final StorageService s3;

	public ModelToXmlConverter(StorageService s3) {
		this.s3 = s3;
	}
	
	public String toXml(Batch batch) throws XMLStreamException, IOException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();

		String xmlFileName = getXmlFileName(batch.getBatchType().equals("FASTTRACK"));
		
		XMLStreamWriter writer = factory.createXMLStreamWriter(new FileWriter(xmlFileName));

		writer.writeStartDocument();
		writer.writeStartElement("BadgePrintExtract");
		writer.writeStartElement("Batch");

		writer.writeStartElement("Filename");
		writer.writeCharacters(xmlFileName);
		writer.writeEndElement();

		writer.writeStartElement("ReExtract");
		writer.writeCharacters("no");
		writer.writeEndElement();

		writer.writeStartElement("LocalAuthorities");
		for (LocalAuthority la : batch.getLocalAuthorities()) {
			writer.writeStartElement("LocalAuthority");
			writeLocalAuthority(writer, la);				
			writer.writeStartElement("Badges");				
			for (BadgeDetails badge : la.getBadges()) {
				writeBadgeDetails(writer, badge);
			}				
			writer.writeEndElement();
			writer.writeEndElement();
		}
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndElement();
		writer.writeEndDocument();

		writer.flush();
		writer.close();
		
		return xmlFileName;
	}
	
	private String getXmlFileName(boolean isFasttrack) {		
		String suffix = isFasttrack ? "-FastTrack" : "";
		String xmlFile = Paths.get(System.getProperty("java.io.tmpdir"),
				"printbatch_xml", "BADGEEXTRACT_" + 
													LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss")) + 
													suffix +
													".xml").toString();

		return xmlFile;
	}

	private void writeBadgeDetails(XMLStreamWriter writer, BadgeDetails badge) throws XMLStreamException, IOException {
		writer.writeStartElement("BadgeDetails");

		writer.writeStartElement("BadgeIdentifier");
		writer.writeCharacters(badge.getBadgeIdentifier());
		writer.writeEndElement();

		writer.writeStartElement("PrintedBadgeReference");
		writer.writeCharacters(badge.getPrintedBadgeReference());
		writer.writeEndElement();

		writer.writeStartElement("StartDate");
		writer.writeCharacters(badge.getStartDate());
		writer.writeEndElement();

		writer.writeStartElement("ExpiryDate");
		writer.writeCharacters(badge.getExpiryDate());
		writer.writeEndElement();

		writer.writeStartElement("DispatchMethodCode");
		writer.writeCharacters(badge.getDispatchMethodCode());
		writer.writeEndElement();

		writer.writeStartElement("FastTrackCode");
		writer.writeCharacters(badge.getDispatchMethodCode());
		writer.writeEndElement();

		writer.writeStartElement("PostageCode");
		writer.writeCharacters(badge.getPostageCode());
		writer.writeEndElement();

		writer.writeStartElement("photo");
		Optional<File> imageFile = s3.downloadFile(badge.getPhoto());
		if (imageFile.isPresent()) {
			String image = toBase64(imageFile.get());
			writer.writeCharacters(image);
		}
		
		writer.writeEndElement();

		writer.writeStartElement("BarCodeData");
		writer.writeCharacters(badge.getBarCodeData());
		writer.writeEndElement();
		
		writeName(writer, badge.getName());
						
		writeLetterAddress(writer, badge.getLetterAddress());
		
		writer.writeEndElement();
	}

	private void writeLetterAddress(XMLStreamWriter writer, LetterAddress address) throws XMLStreamException {
		writer.writeStartElement("LetterAddress");
			
		writer.writeStartElement("NameLine1");
		writer.writeCharacters(address.getNameLine());
		writer.writeEndElement();
		
		writer.writeStartElement("AddressLine1");
		writer.writeCharacters(address.getAddressLine1());
		writer.writeEndElement();
		
		writer.writeStartElement("AddressLine2");
		writer.writeCharacters(address.getAddressLine2());
		writer.writeEndElement();
		
		writer.writeStartElement("Town");
		writer.writeCharacters(address.getTown());
		writer.writeEndElement();
		
		writer.writeStartElement("Country");
		writer.writeCharacters(address.getCountry());
		writer.writeEndElement();
		
		writer.writeStartElement("Postcode");
		writer.writeCharacters(address.getPostcode());
		writer.writeEndElement();
		
		writer.writeEndElement();
	}

	private void writeName(XMLStreamWriter writer, Name name) throws XMLStreamException {
		writer.writeStartElement("Name");

		writer.writeStartElement("Forename");
		writer.writeCharacters(name.getForename());
		writer.writeEndElement();

		writer.writeStartElement("Surname");
		writer.writeCharacters(name.getSurname());
		writer.writeEndElement();

		writer.writeEndElement();
	}

	private void writeLocalAuthority(XMLStreamWriter writer, LocalAuthority la) throws XMLStreamException {
		writer.writeStartElement("LACode");
		writer.writeCharacters(la.getLaCode());
		writer.writeEndElement();

		writer.writeStartElement("LAName");
		writer.writeCharacters(la.getLaName());
		writer.writeEndElement();

		writer.writeStartElement("IssuingCountry");
		writer.writeCharacters(la.getIssuingCountry());
		writer.writeEndElement();

		writer.writeStartElement("LanguageCode");
		writer.writeCharacters(la.getLanguageCode());
		writer.writeEndElement();

		writer.writeStartElement("ClockType");
		writer.writeCharacters(la.getClockType());
		writer.writeEndElement();
		
		writer.writeStartElement("PhoneNumber");
		writer.writeCharacters(la.getPhoneNumber());
		writer.writeEndElement();

		writer.writeStartElement("EmailAddress");
		writer.writeCharacters(la.getEmailAddress());
		writer.writeEndElement();
	}

	private String toBase64(File file) throws IOException {
		return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
	}
}
