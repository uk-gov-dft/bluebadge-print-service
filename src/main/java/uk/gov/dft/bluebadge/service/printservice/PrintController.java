package uk.gov.dft.bluebadge.service.printservice;

import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dft.bluebadge.common.api.model.Error;
import uk.gov.dft.bluebadge.common.controller.AbstractController;
import uk.gov.dft.bluebadge.common.service.exception.InternalServerException;
import uk.gov.dft.bluebadge.service.printservice.api.PrintBatchApi;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatchesResponse;

@RestController
@Slf4j
public class PrintController extends AbstractController implements PrintBatchApi {

  private PrintService service;

  PrintController(PrintService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Void> printBatch(@ApiParam() @Valid @RequestBody Batch batch) {
    try {
      log.info("Beginning print batch.");
      service.print(batch);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      Error error = new Error();
      error.setMessage(e.getMessage());
      throw new InternalServerException(error);
    }
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> processedBatchesBatchNameDelete(
      @ApiParam(required = true) @PathVariable("batchName") String batchName) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @Override
  public ResponseEntity<ProcessedBatchesResponse> processedBatches() {
    return ResponseEntity.ok(
        ProcessedBatchesResponse.builder().data(service.getProcessedBatches()).build());
  }
}
