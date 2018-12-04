package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

/** Badge */
@Validated
public class Badge {
  @JsonProperty("localAuthorityShortCode")
  private String localAuthorityShortCode = null;

  @JsonProperty("badgeNumber")
  private String badgeNumber = null;

  @JsonProperty("party")
  private Party party = null;

  @JsonProperty("startDate")
  private LocalDate startDate = null;

  @JsonProperty("expiryDate")
  private LocalDate expiryDate = null;

  @JsonProperty("deliverToCode")
  private String deliverToCode = null;

  @JsonProperty("deliveryOptionCode")
  private String deliveryOptionCode = null;

  @JsonProperty("imageLink")
  private String imageLink = null;

  public Badge localAuthorityShortCode(String localAuthorityShortCode) {
    this.localAuthorityShortCode = localAuthorityShortCode;
    return this;
  }

  /**
   * Short code of local authority.
   *
   * @return localAuthorityShortCode
   */
  @ApiModelProperty(example = "ABERD", value = "Short code of local authority.")
  @Pattern(regexp = "^[A-Z]+$")
  public String getLocalAuthorityShortCode() {
    return localAuthorityShortCode;
  }

  public void setLocalAuthorityShortCode(String localAuthorityShortCode) {
    this.localAuthorityShortCode = localAuthorityShortCode;
  }

  public Badge badgeNumber(String badgeNumber) {
    this.badgeNumber = badgeNumber;
    return this;
  }

  /**
   * The unique badge number for this badge.
   *
   * @return badgeNumber
   */
  @ApiModelProperty(example = "091215", value = "The unique badge number for this badge.")
  public String getBadgeNumber() {
    return badgeNumber;
  }

  public void setBadgeNumber(String badgeNumber) {
    this.badgeNumber = badgeNumber;
  }

  public Badge party(Party party) {
    this.party = party;
    return this;
  }

  /**
   * Get party
   *
   * @return party
   */
  @ApiModelProperty(value = "")
  @Valid
  public Party getParty() {
    return party;
  }

  public void setParty(Party party) {
    this.party = party;
  }

  public Badge startDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * The date that the badge comes into effect.
   *
   * @return startDate
   */
  @ApiModelProperty(example = "2018-07-07", value = "The date that the badge comes into effect.")
  @Valid
  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public Badge expiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }

  /**
   * The date the badge expires.
   *
   * @return expiryDate
   */
  @ApiModelProperty(example = "2019-06-31", value = "The date the badge expires.")
  @Valid
  public LocalDate getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDate expiryDate) {
    this.expiryDate = expiryDate;
  }

  public Badge deliverToCode(String deliverToCode) {
    this.deliverToCode = deliverToCode;
    return this;
  }

  /**
   * A short code from the DELIVER group of reference data.
   *
   * @return deliverToCode
   */
  @ApiModelProperty(
    example = "HOME",
    value = "A short code from the DELIVER group of reference data."
  )
  @Size(max = 10)
  public String getDeliverToCode() {
    return deliverToCode;
  }

  public void setDeliverToCode(String deliverToCode) {
    this.deliverToCode = deliverToCode;
  }

  public Badge deliveryOptionCode(String deliveryOptionCode) {
    this.deliveryOptionCode = deliveryOptionCode;
    return this;
  }

  /**
   * A short code from the DELOP group of reference data. e.g. STAND or FAST
   *
   * @return deliveryOptionCode
   */
  @ApiModelProperty(
    example = "STAND",
    value = "A short code from the DELOP group of reference data. e.g. STAND or FAST"
  )
  @Size(max = 10)
  public String getDeliveryOptionCode() {
    return deliveryOptionCode;
  }

  public void setDeliveryOptionCode(String deliveryOptionCode) {
    this.deliveryOptionCode = deliveryOptionCode;
  }

  public Badge imageLink(String imageLink) {
    this.imageLink = imageLink;
    return this;
  }

  /**
   * A URL for the badge photo.
   *
   * @return imageLink
   */
  @ApiModelProperty(example = "http://tiny.url?q=ab63fg", value = "A URL for the badge photo.")
  @Size(max = 255)
  public String getImageLink() {
    return imageLink;
  }

  public void setImageLink(String imageLink) {
    this.imageLink = imageLink;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Badge badge = (Badge) o;
    return Objects.equals(this.localAuthorityShortCode, badge.localAuthorityShortCode)
        && Objects.equals(this.badgeNumber, badge.badgeNumber)
        && Objects.equals(this.party, badge.party)
        && Objects.equals(this.startDate, badge.startDate)
        && Objects.equals(this.expiryDate, badge.expiryDate)
        && Objects.equals(this.deliverToCode, badge.deliverToCode)
        && Objects.equals(this.deliveryOptionCode, badge.deliveryOptionCode)
        && Objects.equals(this.imageLink, badge.imageLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        localAuthorityShortCode,
        badgeNumber,
        party,
        startDate,
        expiryDate,
        deliverToCode,
        deliveryOptionCode,
        imageLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Badge {\n");

    sb.append("    localAuthorityShortCode: ")
        .append(toIndentedString(localAuthorityShortCode))
        .append("\n");
    sb.append("    badgeNumber: ").append(toIndentedString(badgeNumber)).append("\n");
    sb.append("    party: ").append(toIndentedString(party)).append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
    sb.append("    deliverToCode: ").append(toIndentedString(deliverToCode)).append("\n");
    sb.append("    deliveryOptionCode: ").append(toIndentedString(deliveryOptionCode)).append("\n");
    sb.append("    imageLink: ").append(toIndentedString(imageLink)).append("\n");
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
