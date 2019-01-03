package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
  @JsonProperty("typeCode")
  @ApiModelProperty(
    example = "PERSON",
    required = true,
    value = "A short code from the PARTY group of reference data. e.g. PERSON or ORG."
  )
  @NotEmpty
  @Size(max = 10)
  private String typeCode = null;

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
