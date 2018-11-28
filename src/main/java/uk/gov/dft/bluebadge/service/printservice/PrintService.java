package uk.gov.dft.bluebadge.service.printservice;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dft.bluebadge.model.printservice.generated.Batches;

@Service
@Slf4j
public class PrintService {
	
	private final StorageService s3;
	
	public PrintService(StorageService s3) {
		this.s3 = s3;
	}

	public void print(Batches batch) throws IOException, InterruptedException {
		File jsonFile = convertAndSave(batch);

		try {
			boolean uploaded = s3.upload(jsonFile);
			log.debug("Json file {} has been uploaded: {}", jsonFile.getName(), uploaded ? "yes" : "no");			
		} finally {
			boolean deleted = jsonFile.delete();
			log.debug("Temporary json file {} has been deleted: {}", jsonFile.getName(), deleted ? "yes" : "no");			
		}
	}

	private File convertAndSave(Batches src) throws IOException {
		ObjectMapper mapper = new ObjectMapper();

		String filename = System.getProperty("java.io.tmpdir") + 
											"printbatch_" + 
											LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss")) + 
											".json";
		log.debug("Convert batches payload to temporary file {}", filename);
		File jsonFile = new File(filename);
		mapper.writeValue(jsonFile, src);

		return jsonFile;
	}
}
