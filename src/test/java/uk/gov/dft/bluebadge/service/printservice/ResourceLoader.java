package uk.gov.dft.bluebadge.service.printservice;

import static java.nio.file.Files.readAllLines;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

class ResourceLoader {

  static String loadTestResource(String filename) throws IOException {
    Path testPath = Paths.get("src", "test", "resources", filename);
    String testPayload =
        FileSystems.getDefault()
            .getPath(testPath.toString())
            .normalize()
            .toAbsolutePath()
            .toString();

    Path path = Paths.get(testPayload);

    return readAllLines(path).stream().map(String::trim).collect(Collectors.joining(""));
  }
}
