package uk.gov.dft.bluebadge.service.printservice.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class FileUtils {

	public static String toBase64(File file) throws IOException {
	
		String encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
		
		return encodedString;
	}
}
