package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

/** Organisation */
@Validated
public class Organisation {
  @JsonProperty("badgeHolderName")
  private String badgeHolderName = null;

  public Organisation badgeHolderName(String badgeHolderName) {
    this.badgeHolderName = badgeHolderName;
    return this;
  }

  /**
   * Get badgeHolderName
   *
   * @return badgeHolderName
   */
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public String getBadgeHolderName() {
    return badgeHolderName;
  }

  public void setBadgeHolderName(String badgeHolderName) {
    this.badgeHolderName = badgeHolderName;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Organisation organisation = (Organisation) o;
    return Objects.equals(this.badgeHolderName, organisation.badgeHolderName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(badgeHolderName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Organisation {\n");

    sb.append("    badgeHolderName: ").append(toIndentedString(badgeHolderName)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
