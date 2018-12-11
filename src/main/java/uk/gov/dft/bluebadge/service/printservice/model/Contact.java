package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/** Contact */
@Validated
@EqualsAndHashCode
@ToString
@Data
public class Contact {
  @JsonProperty("fullName")
  @ApiModelProperty(
    example = "June Whitfield",
    value = "The administrative contact for the badge(s)."
  )
  @Size(max = 100)
  private String fullName = null;

  @JsonProperty("buildingStreet")
  @ApiModelProperty(example = "65 Basil Chambers", required = true, value = "")
  @NotEmpty
  @Size(max = 100)
  private String buildingStreet = null;

  @JsonProperty("line2")
  @ApiModelProperty(example = "Northern Quarter", value = "")
  @Size(max = 100)
  private String line2 = null;

  @JsonProperty("townCity")
  @ApiModelProperty(example = "Manchester", required = true, value = "")
  @NotEmpty
  @Size(max = 100)
  private String townCity = null;

  @JsonProperty("postCode")
  @ApiModelProperty(example = "SK6 8GH", required = true, value = "")
  @NotEmpty
  @Pattern(regexp = "^[A-Za-z]{1,2}[0-9][0-9A-Za-z]?\\s?[0-9][A-Za-z]{2}$")
  private String postCode = null;

  @JsonProperty("primaryPhoneNumber")
  @ApiModelProperty(example = "01616548765", required = true, value = "")
  @NotEmpty
  @Size(max = 20)
  private String primaryPhoneNumber = null;

  @JsonProperty("secondaryPhoneNumber")
  @ApiModelProperty(example = "01616548765", value = "")
  @Size(max = 20)
  private String secondaryPhoneNumber = null;

  @JsonProperty("emailAddress")
  @ApiModelProperty(example = "june@bigbrainknitting.com", value = "")
  @Pattern(regexp = "^\\S+\\@\\S+")
  @Size(max = 100)
  private String emailAddress = null;
}
