package uk.gov.dft.bluebadge.service.printservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.xml.stream.XMLStreamException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.utils.ModelToXmlConverter;

@Service
@Slf4j
public class SFTPService {

	private final StorageService s3;

	private final ModelToXmlConverter xmlConverter;
	
	public SFTPService(StorageService s3, ModelToXmlConverter xmlConverter) {
		this.s3 = s3;
		this.xmlConverter = xmlConverter;
	}

	@Async("sftpTaskExecutor")
	public void send() throws IOException, XMLStreamException {
		log.debug("Starting sending xml file to sftp");

		ObjectMapper objectMapper = new ObjectMapper();

		File dir = s3.downloadBucket();
		for (File file : dir.listFiles()) {
			String json = readFile(file);

			Batch batch = objectMapper.readValue(json, Batch.class);
			
//			String xmlFileName = xmlConverter.toXml(batch);
		}

		// for each file in files
		// create valid xml (with pictures)
		// send it to sftp
		// clean the bucket

	}


	private String readFile(File file) throws IOException {
		return new String(Files.readAllBytes(file.toPath()));
	}


}
