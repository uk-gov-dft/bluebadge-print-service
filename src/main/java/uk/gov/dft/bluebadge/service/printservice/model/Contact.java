package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

/** Contact */
@Validated
public class Contact {
  @JsonProperty("fullName")
  private String fullName = null;

  @JsonProperty("buildingStreet")
  private String buildingStreet = null;

  @JsonProperty("line2")
  private String line2 = null;

  @JsonProperty("townCity")
  private String townCity = null;

  @JsonProperty("postCode")
  private String postCode = null;

  @JsonProperty("primaryPhoneNumber")
  private String primaryPhoneNumber = null;

  @JsonProperty("secondaryPhoneNumber")
  private String secondaryPhoneNumber = null;

  @JsonProperty("emailAddress")
  private String emailAddress = null;

  public Contact fullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  /**
   * The administrative contact for the badge(s).
   *
   * @return fullName
   */
  @ApiModelProperty(
    example = "June Whitfield",
    value = "The administrative contact for the badge(s)."
  )
  @Size(max = 100)
  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Contact buildingStreet(String buildingStreet) {
    this.buildingStreet = buildingStreet;
    return this;
  }

  /**
   * Get buildingStreet
   *
   * @return buildingStreet
   */
  @ApiModelProperty(example = "65 Basil Chambers", required = true, value = "")
  @NotNull
  @Size(max = 100)
  public String getBuildingStreet() {
    return buildingStreet;
  }

  public void setBuildingStreet(String buildingStreet) {
    this.buildingStreet = buildingStreet;
  }

  public Contact line2(String line2) {
    this.line2 = line2;
    return this;
  }

  /**
   * Get line2
   *
   * @return line2
   */
  @ApiModelProperty(example = "Northern Quarter", value = "")
  @Size(max = 100)
  public String getLine2() {
    return line2;
  }

  public void setLine2(String line2) {
    this.line2 = line2;
  }

  public Contact townCity(String townCity) {
    this.townCity = townCity;
    return this;
  }

  /**
   * Get townCity
   *
   * @return townCity
   */
  @ApiModelProperty(example = "Manchester", required = true, value = "")
  @NotNull
  @Size(max = 100)
  public String getTownCity() {
    return townCity;
  }

  public void setTownCity(String townCity) {
    this.townCity = townCity;
  }

  public Contact postCode(String postCode) {
    this.postCode = postCode;
    return this;
  }

  /**
   * Get postCode
   *
   * @return postCode
   */
  @ApiModelProperty(example = "SK6 8GH", required = true, value = "")
  @NotNull
  @Pattern(regexp = "^[A-Za-z]{1,2}[0-9][0-9A-Za-z]?\\s?[0-9][A-Za-z]{2}$")
  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public Contact primaryPhoneNumber(String primaryPhoneNumber) {
    this.primaryPhoneNumber = primaryPhoneNumber;
    return this;
  }

  /**
   * Get primaryPhoneNumber
   *
   * @return primaryPhoneNumber
   */
  @ApiModelProperty(example = "01616548765", required = true, value = "")
  @NotNull
  @Size(max = 20)
  public String getPrimaryPhoneNumber() {
    return primaryPhoneNumber;
  }

  public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
    this.primaryPhoneNumber = primaryPhoneNumber;
  }

  public Contact secondaryPhoneNumber(String secondaryPhoneNumber) {
    this.secondaryPhoneNumber = secondaryPhoneNumber;
    return this;
  }

  /**
   * Get secondaryPhoneNumber
   *
   * @return secondaryPhoneNumber
   */
  @ApiModelProperty(example = "01616548765", value = "")
  @Size(max = 20)
  public String getSecondaryPhoneNumber() {
    return secondaryPhoneNumber;
  }

  public void setSecondaryPhoneNumber(String secondaryPhoneNumber) {
    this.secondaryPhoneNumber = secondaryPhoneNumber;
  }

  public Contact emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   *
   * @return emailAddress
   */
  @ApiModelProperty(example = "june@bigbrainknitting.com", value = "")
  @Pattern(regexp = "^\\S+\\@\\S+")
  @Size(max = 100)
  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Contact contact = (Contact) o;
    return Objects.equals(this.fullName, contact.fullName)
        && Objects.equals(this.buildingStreet, contact.buildingStreet)
        && Objects.equals(this.line2, contact.line2)
        && Objects.equals(this.townCity, contact.townCity)
        && Objects.equals(this.postCode, contact.postCode)
        && Objects.equals(this.primaryPhoneNumber, contact.primaryPhoneNumber)
        && Objects.equals(this.secondaryPhoneNumber, contact.secondaryPhoneNumber)
        && Objects.equals(this.emailAddress, contact.emailAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        fullName,
        buildingStreet,
        line2,
        townCity,
        postCode,
        primaryPhoneNumber,
        secondaryPhoneNumber,
        emailAddress);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Contact {\n");

    sb.append("    fullName: ").append(toIndentedString(fullName)).append("\n");
    sb.append("    buildingStreet: ").append(toIndentedString(buildingStreet)).append("\n");
    sb.append("    line2: ").append(toIndentedString(line2)).append("\n");
    sb.append("    townCity: ").append(toIndentedString(townCity)).append("\n");
    sb.append("    postCode: ").append(toIndentedString(postCode)).append("\n");
    sb.append("    primaryPhoneNumber: ").append(toIndentedString(primaryPhoneNumber)).append("\n");
    sb.append("    secondaryPhoneNumber: ")
        .append(toIndentedString(secondaryPhoneNumber))
        .append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
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
