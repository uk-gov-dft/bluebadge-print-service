package uk.gov.dft.bluebadge.service.printservice.model.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

/** LetterAddress */
@Validated
public class LetterAddress {
  @JsonProperty("nameLine")
  private String nameLine = null;

  @JsonProperty("addressLine1")
  private String addressLine1 = null;

  @JsonProperty("addressLine2")
  private String addressLine2 = null;

  @JsonProperty("town")
  private String town = null;

  @JsonProperty("country")
  private String country = null;

  @JsonProperty("postcode")
  private String postcode = null;

  public LetterAddress nameLine(String nameLine) {
    this.nameLine = nameLine;
    return this;
  }

  /**
   * Get nameLine
   *
   * @return nameLine
   */
  @ApiModelProperty(
    example = "John Smith (badge_holder) regardless if we deliver to home or council",
    value = ""
  )
  public String getNameLine() {
    return nameLine;
  }

  public void setNameLine(String nameLine) {
    this.nameLine = nameLine;
  }

  public LetterAddress addressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
    return this;
  }

  /**
   * Get addressLine1
   *
   * @return addressLine1
   */
  @ApiModelProperty(
    example = "person or council 'address line 1' depends on dispatchMethodCode",
    value = ""
  )
  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public LetterAddress addressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
    return this;
  }

  /**
   * Get addressLine2
   *
   * @return addressLine2
   */
  @ApiModelProperty(
    example = "person or council 'address line 2' depends on dispatchMethodCode",
    value = ""
  )
  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public LetterAddress town(String town) {
    this.town = town;
    return this;
  }

  /**
   * Get town
   *
   * @return town
   */
  @ApiModelProperty(example = "person or council 'town' depends on dispatchMethodCode", value = "")
  public String getTown() {
    return town;
  }

  public void setTown(String town) {
    this.town = town;
  }

  public LetterAddress country(String country) {
    this.country = country;
    return this;
  }

  /**
   * Get country
   *
   * @return country
   */
  @ApiModelProperty(example = "always 'United Kingdom'", value = "")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public LetterAddress postcode(String postcode) {
    this.postcode = postcode;
    return this;
  }

  /**
   * Get postcode
   *
   * @return postcode
   */
  @ApiModelProperty(
    example = "person or council 'postcode' depends on dispatchMethodCode",
    value = ""
  )
  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LetterAddress letterAddress = (LetterAddress) o;
    return Objects.equals(this.nameLine, letterAddress.nameLine)
        && Objects.equals(this.addressLine1, letterAddress.addressLine1)
        && Objects.equals(this.addressLine2, letterAddress.addressLine2)
        && Objects.equals(this.town, letterAddress.town)
        && Objects.equals(this.country, letterAddress.country)
        && Objects.equals(this.postcode, letterAddress.postcode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nameLine, addressLine1, addressLine2, town, country, postcode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LetterAddress {\n");

    sb.append("    nameLine: ").append(toIndentedString(nameLine)).append("\n");
    sb.append("    addressLine1: ").append(toIndentedString(addressLine1)).append("\n");
    sb.append("    addressLine2: ").append(toIndentedString(addressLine2)).append("\n");
    sb.append("    town: ").append(toIndentedString(town)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    postcode: ").append(toIndentedString(postcode)).append("\n");
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
