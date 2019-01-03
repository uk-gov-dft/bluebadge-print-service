package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

/** Person */
@Validated
@EqualsAndHashCode
@ToString
@Data
public class Person {
  @JsonProperty("badgeHolderName")
  @ApiModelProperty(example = "Dwight Appleman", required = true, value = "")
  @NotEmpty
  @Size(max = 100)
  private String badgeHolderName = null;

  @JsonProperty("dob")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  //  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  @ApiModelProperty(example = "1972-09-13", value = "Date of birth YYYY-MM-DD")
  @NotNull
  @Valid
  private LocalDate dob = null;

  @JsonProperty("genderCode")
  @ApiModelProperty(
    example = "MALE",
    value =
        "A short code from the GENDER group of reference data. e.g. MALE, FEMALE or UNSPECIFIED."
  )
  private String genderCode = null;
}
