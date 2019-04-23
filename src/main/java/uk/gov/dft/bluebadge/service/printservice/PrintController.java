package uk.gov.dft.bluebadge.service.printservice;

import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dft.bluebadge.common.api.common.CommonResponseHandler;
import uk.gov.dft.bluebadge.common.api.model.Error;
import uk.gov.dft.bluebadge.common.service.exception.InternalServerException;
import uk.gov.dft.bluebadge.service.printservice.api.PrintBatchApi;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatchesResponse;

@RestController
@Slf4j
@CommonResponseHandler
public class PrintController implements PrintBatchApi {

  private PrintService service;

  PrintController(PrintService service) {
    this.service = service;
  }

  @Override
  public ResponseEntity<Void> printBatch(@ApiParam() @Valid @RequestBody Batch batch) {
    try {
      // Get reference data while request is active.
      service.initReferenceData();
      log.info("Received new print batch: {}", batch.getFilename());
      service.storePrintBatchInS3(batch);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      Error error = new Error();
      error.setMessage(e.getMessage());
      throw new InternalServerException(error);
    } finally {
      service.processPrintBatches();
    }

    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deleteBatchConfirmation(
      @ApiParam(required = true) @PathVariable("batchName") String batchName) {

    if (service.deleteBatchConfirmation(batchName)) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Override
  public ResponseEntity<ProcessedBatchesResponse> processedBatches() {
    return ResponseEntity.ok(
        ProcessedBatchesResponse.builder().data(service.getProcessedBatches()).build());
  }
}
