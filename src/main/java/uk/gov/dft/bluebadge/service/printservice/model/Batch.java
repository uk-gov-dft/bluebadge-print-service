package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/** Batch */
@Validated
@EqualsAndHashCode
@ToString
@Data
public class Batch {
  public enum BatchTypeEnum {
    FASTTRACK,
    STANDARD,
    LA;
  }

  @JsonProperty("filename")
  @ApiModelProperty()
  @NotEmpty
  private String filename = null;

  @JsonProperty("batchType")
  @ApiModelProperty()
  @NotNull
  private BatchTypeEnum batchType = null;

  @JsonProperty("badges")
  @ApiModelProperty()
  @Valid
  private List<Badge> badges = null;
}
