package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/** Organisation */
@Validated
@EqualsAndHashCode
@ToString
@Data
public class Organisation {
  @JsonProperty("badgeHolderName")
  @ApiModelProperty(required = true, value = "")
  @NotEmpty
  private String badgeHolderName = null;
}
