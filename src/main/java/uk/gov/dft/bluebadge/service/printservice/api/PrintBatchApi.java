package uk.gov.dft.bluebadge.service.printservice.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.ProcessedBatchesResponse;

@Api(value = "PrintService")
public interface PrintBatchApi {

  @ApiOperation(
    value = "Creates a Batch of badges to be send for printing",
    nickname = "printBatchPost",
    tags = {
      "print Batch",
    }
  )
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK."),
      @ApiResponse(code = 400, message = "Invalid request", response = CommonResponse.class)
    }
  )
  @PostMapping(
    value = "/printBatch",
    produces = {"application/json"},
    consumes = {"application/json"}
  )
  ResponseEntity<Void> printBatch(
      @ApiParam(value = "Batch badges to be send for printing") @Valid @RequestBody Batch batch);

  @ApiOperation(value = "", nickname = "processedBatchesBatchNameDelete")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK.")})
  @DeleteMapping(
    value = "/processed-batches/{batchName}",
    produces = {"application/json"},
    consumes = {"application/json"}
  )
  ResponseEntity<Void> deleteBatch(
      @ApiParam(required = true) @PathVariable("batchName") String batchName);

  @ApiOperation(
    value = "",
    nickname = "processedBatchesGet",
    response = ProcessedBatchesResponse.class,
    tags = {}
  )
  @ApiResponses(
    value = {@ApiResponse(code = 200, message = "OK.", response = ProcessedBatchesResponse.class)}
  )
  @GetMapping(
    value = "/processed-batches",
    produces = {"application/json"},
    consumes = {"application/json"}
  )
  ResponseEntity<ProcessedBatchesResponse> processedBatches();
}
