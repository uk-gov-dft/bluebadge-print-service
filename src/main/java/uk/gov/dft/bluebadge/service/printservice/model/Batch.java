package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotEmpty;
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
  @JsonProperty("filename")
  @ApiModelProperty(value = "")
  private String filename = null;

  @JsonProperty("batchType")
  @ApiModelProperty(example = "FASTTRACK, STANDARD or LA", value = "")
  @NotEmpty
  private String batchType = null;

  @JsonProperty("badges")
  @ApiModelProperty(value = "")
  private List<Badge> badges = null;

  public Batch addBadgesItem(Badge badge) {
    if (this.badges == null) {
      this.badges = new ArrayList<>();
    }
    this.badges.add(badge);
    return this;
  }
}
