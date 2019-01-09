package uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.dft.bluebadge.common.api.model.CommonResponse;

@Data
@EqualsAndHashCode(callSuper = false)
public class ReferenceDataResponse extends CommonResponse {
  @JsonProperty("data")
  private List<ReferenceData> data = null;
}
