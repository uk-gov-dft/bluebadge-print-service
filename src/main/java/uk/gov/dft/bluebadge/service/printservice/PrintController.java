package uk.gov.dft.bluebadge.service.printservice;

import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dft.bluebadge.common.controller.AbstractController;
import uk.gov.dft.bluebadge.model.printservice.generated.Batches;
import uk.gov.dft.bluebadge.service.printservice.generated.controller.PrintBatchApi;

@RestController
public class PrintController extends AbstractController implements PrintBatchApi {

  private PrintService service;

  public PrintController(PrintService service) {
    this.service = service;
  }

  @Override
  // todo: add permissions
  // @PreAuthorize("hasAuthority('PERM_PRINT-BATCH')")
  public ResponseEntity<Void> printBatch(@ApiParam() @Valid @RequestBody Batches batch) {
    service.print(batch);
    return ResponseEntity.ok().build();
  }
}
