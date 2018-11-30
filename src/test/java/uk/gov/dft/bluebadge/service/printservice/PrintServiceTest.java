package uk.gov.dft.bluebadge.service.printservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.payload;

import java.net.URL;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@Slf4j
public class PrintServiceTest {

  private StorageService s3 = mock(StorageService.class);
  private PrintService service = new PrintService(s3);

  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));

    service = new PrintService(s3);
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
  }

  @Test
  @DisplayName("Should convert received payload into json file and send to save on s3")
  @SneakyThrows
  public void print() {
    URL s3Url = new URL("http://path_to_printbatch.json");
    when(s3.upload(any())).thenReturn(s3Url);
    service.print(payload());
    verify(s3, times(2)).upload(any());
  }
}
