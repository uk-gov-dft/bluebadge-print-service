package uk.gov.dft.bluebadge.service.printservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(JUnitPlatform.class)
@Slf4j
public class PrintControllerTest {

  private MockMvc mvc;

  private PrintService service = mock(PrintService.class);

  @BeforeEach
  public void beforeEachTest(TestInfo testInfo) {
    mvc = MockMvcBuilders.standaloneSetup(new PrintController(service)).build();
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
  }

  @Test
  @DisplayName("Accepts payload and returns HTTP 200 ")
  @SneakyThrows
  public void request_print_batch_success() {
    String body = ResourceLoader.loadTestResource("printbatch_20181127122345.json");
    RequestBuilder builder =
        MockMvcRequestBuilders.post("/printBatch")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON);
    doNothing().when(service).print(any());
    mvc.perform(builder).andExpect(status().isOk());
    verify(service, times(1)).print(any());
  }

  @Test
  @DisplayName("Accepts payload and returns HTTP 500 ")
  @SneakyThrows
  public void request_print_batch_throws_exception() {
    String body = ResourceLoader.loadTestResource("printbatch_20181127122345.json");
    RequestBuilder builder =
        MockMvcRequestBuilders.post("/printBatch")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON);
    doThrow(new IOException("Some underlying problems")).when(service).print(any());
    mvc.perform(builder).andExpect(status().is5xxServerError());
    verify(service, times(1)).print(any());
  }
}
