package uk.gov.dft.bluebadge.service.printservice;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SFTPService {

	private final StorageService s3;

	public SFTPService(StorageService s3) {
		this.s3 = s3;
	}

	@Async("sftpTaskExecutor")
	public void send() throws IOException {
		log.debug("Starting sending xml file to sftp");

		File dir = s3.download();
		for (File file : dir.listFiles()) {
			String json = readFile(file);
		}

		// for each file in files
		// create valid xml (with pictures)
		// send it to sftp
		// clean the bucket

	}

	private String readFile(File file) throws IOException {
		try {
			return new String(Files.readAllBytes(Paths.get(file.getPath())));
		} catch (IOException e) {
			log.error("Can't read file {}", file.getPath());
			throw e;
		}
	}

}
