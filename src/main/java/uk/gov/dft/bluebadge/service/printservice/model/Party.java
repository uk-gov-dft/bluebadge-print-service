package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/** Party */
@Validated
@EqualsAndHashCode
@ToString
@Data
public class Party {

  public enum PartyType {
    PERSON,
    ORG
  }

  @NotNull private PartyType typeCode;

  @JsonProperty("contact")
  @ApiModelProperty(required = true, value = "")
  @NotNull
  @Valid
  private Contact contact = null;

  @JsonProperty("person")
  @ApiModelProperty(value = "")
  @Valid
  private Person person = null;

  @JsonProperty("organisation")
  @ApiModelProperty(value = "")
  @Valid
  private Organisation organisation = null;
}
