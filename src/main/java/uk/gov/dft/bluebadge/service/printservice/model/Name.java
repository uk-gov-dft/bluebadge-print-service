package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

import org.springframework.validation.annotation.Validated;

/** Name */
@Validated
public class Name {
  @JsonProperty("forename")
  private String forename = null;

  @JsonProperty("surname")
  private String surname = null;

  public Name forename(String forename) {
    this.forename = forename;
    return this;
  }

  /**
   * Get forename
   *
   * @return forename
   */
  @ApiModelProperty(example = "John Smith (badge_holder)", value = "")
  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public Name surname(String surname) {
    this.surname = surname;
    return this;
  }

  /**
   * Get surname
   *
   * @return surname
   */
  @ApiModelProperty(
    example =
        "If holder_name is greater than 27 characters, at the first space delimited of the string, split onto 'surname'",
    value = ""
  )
  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Name name = (Name) o;
    return Objects.equals(this.forename, name.forename)
        && Objects.equals(this.surname, name.surname);
  }

  @Override
  public int hashCode() {
    return Objects.hash(forename, surname);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Name {\n");

    sb.append("    forename: ").append(toIndentedString(forename)).append("\n");
    sb.append("    surname: ").append(toIndentedString(surname)).append("\n");
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
