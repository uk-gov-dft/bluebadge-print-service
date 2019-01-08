package uk.gov.dft.bluebadge.service.printservice.model;

import java.util.List;
import javax.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;

@Validated
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class ProcessedBatchesResponse extends CommonResponse {

  @Valid private List<ProcessedBatch> data;
}
