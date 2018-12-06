package uk.gov.dft.bluebadge.service.printservice;

import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.dft.bluebadge.common.api.model.Error;
import uk.gov.dft.bluebadge.common.controller.AbstractController;
import uk.gov.dft.bluebadge.common.service.exception.InternalServerException;
import uk.gov.dft.bluebadge.service.printservice.generated.controller.PrintBatchApi;
import uk.gov.dft.bluebadge.service.printservice.model.xml.Batch;

@RestController
@Slf4j
public class PrintController extends AbstractController implements PrintBatchApi {

  private PrintService service;

  public PrintController(PrintService service) {
    this.service = service;
  }

  @Override
  // todo: add permissions something like @PreAuthorize("hasAuthority('PERM_PRINT')")
  public ResponseEntity<Void> printBatch(@ApiParam() @Valid @RequestBody Batch batch) {
    try {
      service.print(batch);
    } catch (Exception e) {
      log.error(e.getMessage());
      Error error = new Error();
      error.setMessage(e.getMessage());
      throw new InternalServerException(error);
    }
    return ResponseEntity.ok().build();
  }
}
