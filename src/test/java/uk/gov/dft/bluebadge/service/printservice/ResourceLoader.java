package uk.gov.dft.bluebadge.service.printservice;

import static java.nio.file.Files.readAllLines;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ResourceLoader {

  public static String loadTestResource(String filename) throws IOException {
    Path testPath = Paths.get("src", "test", "resources", filename);
    String testPayload =
        FileSystems.getDefault()
            .getPath(testPath.toString())
            .normalize()
            .toAbsolutePath()
            .toString();

    Path path = Paths.get(testPayload);
    String payload = readAllLines(path).stream().map(s -> s.trim()).collect(Collectors.joining(""));

    return payload;
  }
}
