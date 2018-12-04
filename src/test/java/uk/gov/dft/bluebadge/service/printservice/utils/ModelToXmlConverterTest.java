package uk.gov.dft.bluebadge.service.printservice.utils;

import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dft.bluebadge.service.printservice.StorageService;

@RunWith(JUnitPlatform.class)
@Slf4j
public class ModelToXmlConverterTest {

  private StorageService s3 = mock(StorageService.class);

  private ModelToXmlConverter converter = new ModelToXmlConverter(s3);
  
  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
  }

}
