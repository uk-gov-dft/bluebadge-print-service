package uk.gov.dft.bluebadge.service.printservice;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.testJson;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import java.nio.file.Paths;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import uk.gov.dft.bluebadge.service.printservice.config.S3Config;

@RunWith(JUnitPlatform.class)
@Slf4j
public class StorageServiceTest {
  public static final int URL_DURATION_MS = 60000;

  private StorageService service;
  @Mock private AmazonS3 s3;

  private String originalTmpDir = System.getProperty("java.io.tmpdir");

  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));
    setup();

    String testTmpDir = Paths.get("src", "test", "resources", "tmp").toString();
    System.setProperty("java.io.tmpdir", testTmpDir);
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
    System.setProperty("java.io.tmpdir", originalTmpDir);
  }

  @SneakyThrows
  public void setup() {
    initMocks(this);

    S3Config s3Config = new S3Config();
    s3Config.setS3Bucket("test_bucket");
    s3Config.setSignedUrlDurationMs(URL_DURATION_MS);

    service = new StorageService(s3Config, s3);
  }

  @Test
  @DisplayName("Should upload json file to s3")
  @SneakyThrows
  public void upload() {
    PutObjectResult result = mock(PutObjectResult.class);
    when(s3.putObject(any(String.class), any(String.class), any(String.class))).thenReturn(result);
    when(s3.doesObjectExist(any(String.class), any(String.class))).thenReturn(true);

    String fileName = "test.json";
    boolean uploaded = service.upload(testJson, fileName);

    assertTrue(uploaded);
    verify(s3, times(1)).putObject(eq("test_bucket"), endsWith("test.json"), eq(testJson));
    verify(s3, times(1)).doesObjectExist(eq("test_bucket"), endsWith("test.json"));
  }

  @Test
  @DisplayName("Should download a json string form a bucket")
  @SneakyThrows
  public void downloadJson() {

    when(s3.doesObjectExist(any(String.class), any(String.class))).thenReturn(true);
    when(s3.getObjectAsString(any(String.class), any(String.class))).thenReturn(testJson);

    Optional<String> actual = service.downloadFile("test_bucker", "test.json");

    assertNotNull(actual.get());
  }

  @Test
  @DisplayName("Should verify delete method has been invoked")
  @SneakyThrows
  public void deleteFile() {
    doNothing().when(s3).deleteObject(any(), eq("test.json"));

    service.deleteFile("test.json");

    verify(s3, times(1)).deleteObject(any(), eq("test.json"));
  }

  @Test
  @DisplayName("Should verify list all objects has been invoked")
  @SneakyThrows
  public void listFiles() {
    ObjectListing result = mock(ObjectListing.class);
    when(s3.listObjects(any(String.class))).thenReturn(result);

    service.listFiles();

    verify(s3, times(1)).listObjects(any(String.class));
  }
}
