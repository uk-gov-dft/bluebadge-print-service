package uk.gov.dft.bluebadge.service.printservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
  @Mock private TransferManager transferManager;
  private URL s3ObjectURL;
  private URL s3SignedUrl;

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

    service = new StorageService(s3Config, s3, transferManager);
  }

  @Test
  @DisplayName("Should upload json file to s3")
  @SneakyThrows
  public void upload() {
    s3ObjectURL = new URL("http://test");
    s3SignedUrl = new URL("http://testSigned");

    Upload upload = mock(Upload.class);
    when(transferManager.upload(any(), any(), any(), any())).thenReturn(upload);

    UploadResult uploadResult = mock(UploadResult.class);
    when(upload.waitForUploadResult()).thenReturn(uploadResult);
    when(uploadResult.getBucketName()).thenReturn("resultBucket");
    when(uploadResult.getKey()).thenReturn("resultKey");

    when(s3.getUrl("resultBucket", "resultKey")).thenReturn(s3ObjectURL);
    when(s3.generatePresignedUrl(any())).thenReturn(s3SignedUrl);

    File file = new File("src/test/resources/printbatch_20181127122345.json");
    URL s3Json = service.upload(file);

    assertNotNull(s3Json);
    assertEquals(s3SignedUrl, s3Json);
    verify(transferManager, times(1))
        .upload(eq("test_bucket"), endsWith("printbatch_20181127122345.json"), any(), any());
  }

  @Test
  @DisplayName("Should download all files from the bucket")
  public void download() {

    MultipleFileDownload download = mock(MultipleFileDownload.class);
    File tmpDir =
        Paths.get(
                System.getProperty("java.io.tmpdir"), "printbatch_src", "printbatch_20181129123055")
            .toFile();

    when(transferManager.downloadDirectory(any(), eq(null), eq(tmpDir), eq(true)))
        .thenReturn(download);

    File dir = service.downloadBucket();

    assertNotNull(dir);
    assertTrue(dir.exists());
  }

  @Test
  @DisplayName("Should download a file form a bucket")
  @SneakyThrows
  public void downloadFile() {

    Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_src");
    File testJson = tmpDir.resolve("test.json").toFile();
    Download download = mock(Download.class);
    when(transferManager.download(any(), eq("test.json"), eq(testJson))).thenReturn(download);

    service.downloadFile("", "test.json", tmpDir);

    assertNotNull(testJson);
    assertTrue(testJson.exists());
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
    doNothing().when(s3).listObjects(any(String.class));

    service.listFiles();

    verify(s3, times(1)).listObjects(any(String.class));
  }
}
