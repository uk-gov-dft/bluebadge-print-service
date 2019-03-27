package uk.gov.dft.bluebadge.service.printservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amazonaws.util.IOUtils;
import java.util.ArrayList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.dft.bluebadge.service.printservice.controller.CommonResponseControllerAdvice;

@Slf4j
class PrintControllerTest {

  private MockMvc mvc;

  private PrintService service = mock(PrintService.class);

  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    mvc =
        MockMvcBuilders.standaloneSetup(new PrintController(service))
            .setControllerAdvice(new CommonResponseControllerAdvice())
            .build();
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
  }

  @Test
  @DisplayName("Accepts payload for printBatch and returns HTTP 200 ")
  @SneakyThrows
  void request_print_batch_success() {
    String body =
        IOUtils.toString(getClass().getResourceAsStream("/printbatch_20181127122345.json"));
    RequestBuilder builder =
        MockMvcRequestBuilders.post("/printBatch")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON);
    doNothing().when(service).storePrintBatchInS3(any());
    doNothing().when(service).processPrintBatches();
    mvc.perform(builder).andExpect(status().isOk());
    verify(service, times(1)).storePrintBatchInS3(any());
    verify(service, times(1)).processPrintBatches();
  }

  @Test
  @DisplayName("Accepts payload for printBatch and returns HTTP 500 ")
  @SneakyThrows
  void request_print_batch_throws_exception() {
    String body =
        IOUtils.toString(getClass().getResourceAsStream("/printbatch_20181127122345.json"));
    RequestBuilder builder =
        MockMvcRequestBuilders.post("/printBatch")
            .content(body)
            .contentType(MediaType.APPLICATION_JSON);
    doThrow(new RuntimeException("Some underlying problems"))
        .when(service)
        .storePrintBatchInS3(any());
    mvc.perform(builder).andExpect(status().is5xxServerError());
    verify(service, times(1)).storePrintBatchInS3(any());
    // And still process other batches.
    verify(service, times(1)).processPrintBatches();
  }

  @Test
  @DisplayName("Returns processed batches and HTTP 200")
  @SneakyThrows
  void get_processed_batches_success() {
    RequestBuilder builder =
        MockMvcRequestBuilders.get("/processed-batches").contentType(MediaType.APPLICATION_JSON);
    when(service.getProcessedBatches()).thenReturn(new ArrayList<>());
    mvc.perform(builder).andExpect(status().isOk());
    verify(service, times(1)).getProcessedBatches();
  }

  @Test
  @DisplayName("Delete batch returns 200 if ok and 404 if batch not exists.")
  @SneakyThrows
  void deleteBatch() {
    RequestBuilder builder =
        MockMvcRequestBuilders.delete("/processed-batches/{id}", "ABatchName")
            .contentType(MediaType.APPLICATION_JSON);
    when(service.deleteBatchConfirmation(any())).thenReturn(true);
    mvc.perform(builder).andExpect(status().isOk());
    verify(service, times(1)).deleteBatchConfirmation("ABatchName");

    when(service.deleteBatchConfirmation(any())).thenReturn(false);
    mvc.perform(builder).andExpect(status().isNotFound());
    verify(service, times(2)).deleteBatchConfirmation("ABatchName");
  }
}
