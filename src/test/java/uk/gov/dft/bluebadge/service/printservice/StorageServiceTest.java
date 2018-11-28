package uk.gov.dft.bluebadge.service.printservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.endsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import java.io.File;
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
import org.mockito.Mock;
import uk.gov.dft.bluebadge.service.printservice.config.S3Config;

@RunWith(JUnitPlatform.class)
@Slf4j
public class StorageServiceTest {
  public static final int URL_DURATION_MS = 60000;

  private StorageService storageService;
  @Mock private AmazonS3 s3;
  @Mock private TransferManager transferManager;
  private URL s3ObjectURL;
  private URL s3SignedUrl;

  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));
    setup();
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
  }

  @SneakyThrows
  public void setup() {
    initMocks(this);

    S3Config s3Config = new S3Config();
    s3Config.setS3Bucket("test_bucket");
    s3Config.setSignedUrlDurationMs(URL_DURATION_MS);

    storageService = new StorageService(s3Config, s3, transferManager);

    s3ObjectURL = new URL("http://test");
    s3SignedUrl = new URL("http://testSigned");
  }

  @Test
  @DisplayName("Should upload json file to s3")
  public void upload() throws Exception {
    Upload upload = mock(Upload.class);
    when(transferManager.upload(any(), any(), any(), any())).thenReturn(upload);

    UploadResult uploadResult = mock(UploadResult.class);
    when(upload.waitForUploadResult()).thenReturn(uploadResult);
    when(uploadResult.getBucketName()).thenReturn("resultBucket");
    when(uploadResult.getKey()).thenReturn("resultKey");

    when(s3.getUrl("resultBucket", "resultKey")).thenReturn(s3ObjectURL);
    when(s3.generatePresignedUrl(any())).thenReturn(s3SignedUrl);

    File file = new File("src/test/resources/printbatch_20181127122345.json");
    URL s3Json = storageService.upload(file);

    assertNotNull(s3Json);
    assertEquals(s3SignedUrl, s3Json);
    verify(transferManager, times(1))
        .upload(eq("test_bucket"), endsWith("printbatch_20181127122345.json"), any(), any());
  }
}
