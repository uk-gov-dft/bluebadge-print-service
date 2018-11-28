package uk.gov.dft.bluebadge.service.printservice;

import static java.io.File.separator;
import static java.nio.file.Files.readAllLines;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class ResourceLoader {

  public static String loadTestResource(String filename) throws IOException {
    String testDir = "src" + separator + "test" + separator + "resources" + separator;
    String testPayload =
        FileSystems.getDefault()
            .getPath(testDir + filename)
            .normalize()
            .toAbsolutePath()
            .toString();

    Path path = Paths.get(testPayload);
    String payload = readAllLines(path).stream().map(s -> s.trim()).collect(Collectors.joining(""));

    return payload;
  }
}
