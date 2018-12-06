/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.1).
 * https://github.com/swagger-api/swagger-codegen Do not edit the class manually.
 */
package uk.gov.dft.bluebadge.service.printservice.generated.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;
import uk.gov.dft.bluebadge.service.printservice.model.xml.Batch;

@Api(value = "PrintBatch", description = "the PrintBatch API")
public interface PrintBatchApi {

  Logger log = LoggerFactory.getLogger(PrintBatchApi.class);

  default Optional<ObjectMapper> getObjectMapper() {
    return Optional.empty();
  }

  default Optional<HttpServletRequest> getRequest() {
    return Optional.empty();
  }

  default Optional<String> getAcceptHeader() {
    return getRequest().map(r -> r.getHeader("Accept"));
  }

  @ApiOperation(
    value = "Creates a batch of badges to be send for printing",
    nickname = "printBatchPost",
    notes = "",
    tags = {
      "print batch",
    }
  )
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK."),
      @ApiResponse(code = 400, message = "Invalid request", response = CommonResponse.class)
    }
  )
  @RequestMapping(
    value = "/printBatch",
    produces = {"application/json"},
    consumes = {"application/json"},
    method = RequestMethod.POST
  )
  default ResponseEntity<Void> printBatch(
      @ApiParam(value = "Batch badges to be send for printing") @Valid @RequestBody Batch batch) {
    if (getObjectMapper().isPresent() && getAcceptHeader().isPresent()) {
    } else {
      log.warn(
          "ObjectMapper or HttpServletRequest not configured in default PrintBatchApi interface so no example is generated");
    }
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}