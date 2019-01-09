package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

/** Badge */
@Validated
@EqualsAndHashCode
@ToString
@Data
public class Badge {
  @JsonProperty("localAuthorityShortCode")
  @ApiModelProperty(example = "ABERD", value = "Short code of local authority.")
  @Pattern(regexp = "^[A-Z]+$")
  @NotEmpty
  @Valid
  private String localAuthorityShortCode = null;

  @JsonProperty("badgeNumber")
  @ApiModelProperty(example = "091215", value = "The unique badge number for this badge.")
  @NotEmpty
  private String badgeNumber = null;

  @JsonProperty("party")
  @NotNull
  private Party party = null;

  @JsonProperty("startDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @ApiModelProperty(example = "2018-07-07", value = "The date that the badge comes into effect.")
  @NotNull
  @Valid
  private LocalDate startDate = null;

  @JsonProperty("expiryDate")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @ApiModelProperty(example = "2019-06-31", value = "The date the badge expires.")
  @NotNull
  @Valid
  private LocalDate expiryDate = null;

  @JsonProperty("deliverToCode")
  @ApiModelProperty(
    example = "HOME",
    value = "A short code from the DELIVER group of reference data."
  )
  @Size(max = 10)
  @NotEmpty
  private String deliverToCode = null;

  @JsonProperty("deliveryOptionCode")
  @NotEmpty
  @ApiModelProperty(
    example = "STAND",
    value = "A short code from the DELOP group of reference data. e.g. STAND or FAST"
  )
  @Size(max = 10)
  private String deliveryOptionCode = null;

  @JsonProperty("imageLink")
  @ApiModelProperty(example = "http://tiny.url?q=ab63fg", value = "A URL for the badge photo.")
  @Size(max = 255)
  @NotEmpty
  private String imageLink = null;
}
