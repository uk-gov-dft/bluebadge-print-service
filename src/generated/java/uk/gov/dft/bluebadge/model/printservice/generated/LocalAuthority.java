package uk.gov.dft.bluebadge.model.printservice.generated;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import uk.gov.dft.bluebadge.model.printservice.generated.BadgeDetails;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * LocalAuthority
 */
@Validated

public class LocalAuthority   {
  @JsonProperty("laCode")
  private String laCode = null;

  @JsonProperty("laName")
  private String laName = null;

  @JsonProperty("issuingCountry")
  private String issuingCountry = null;

  @JsonProperty("languageCode")
  private String languageCode = null;

  @JsonProperty("clockType")
  private String clockType = null;

  @JsonProperty("phoneNumber")
  private String phoneNumber = null;

  @JsonProperty("emailAddress")
  private String emailAddress = null;

  @JsonProperty("badges")
  @Valid
  private List<BadgeDetails> badges = null;

  public LocalAuthority laCode(String laCode) {
    this.laCode = laCode;
    return this;
  }

  /**
   * Get laCode
   * @return laCode
  **/
  @ApiModelProperty(example = "LA short code 'ABERD'", value = "")


  public String getLaCode() {
    return laCode;
  }

  public void setLaCode(String laCode) {
    this.laCode = laCode;
  }

  public LocalAuthority laName(String laName) {
    this.laName = laName;
    return this;
  }

  /**
   * Get laName
   * @return laName
  **/
  @ApiModelProperty(example = "LA description 'Aberdinshire council'", value = "")


  public String getLaName() {
    return laName;
  }

  public void setLaName(String laName) {
    this.laName = laName;
  }

  public LocalAuthority issuingCountry(String issuingCountry) {
    this.issuingCountry = issuingCountry;
    return this;
  }

  /**
   * Get issuingCountry
   * @return issuingCountry
  **/
  @ApiModelProperty(example = "Country 'E' = English 'S' = Scottish 'W' = Welsh 'N' = Northern Ireland, convert this from LA metadata", value = "")


  public String getIssuingCountry() {
    return issuingCountry;
  }

  public void setIssuingCountry(String issuingCountry) {
    this.issuingCountry = issuingCountry;
  }

  public LocalAuthority languageCode(String languageCode) {
    this.languageCode = languageCode;
    return this;
  }

  /**
   * Get languageCode
   * @return languageCode
  **/
  @ApiModelProperty(example = "if LA Nation = Wales 'EW', If LA Nation = England, Northern Ireland or Scotland then 'E'", value = "")


  public String getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  public LocalAuthority clockType(String clockType) {
    this.clockType = clockType;
    return this;
  }

  /**
   * Get clockType
   * @return clockType
  **/
  @ApiModelProperty(example = "Northern Ireland = 'STANDARD', Wales = 'WALLET', England = 'STANDARD', Scotland = 'STANDARD'", value = "")


  public String getClockType() {
    return clockType;
  }

  public void setClockType(String clockType) {
    this.clockType = clockType;
  }

  public LocalAuthority phoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    return this;
  }

  /**
   * Get phoneNumber
   * @return phoneNumber
  **/
  @ApiModelProperty(example = "phone number '01234 567890' - from LA metadata", value = "")


  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public LocalAuthority emailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**
   * Get emailAddress
   * @return emailAddress
  **/
  @ApiModelProperty(example = "email 'enquiries@dcuc.gov.uk' - from LA metadata", value = "")


  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public LocalAuthority badges(List<BadgeDetails> badges) {
    this.badges = badges;
    return this;
  }

  public LocalAuthority addBadgesItem(BadgeDetails badgesItem) {
    if (this.badges == null) {
      this.badges = new ArrayList<>();
    }
    this.badges.add(badgesItem);
    return this;
  }

  /**
   * Get badges
   * @return badges
  **/
  @ApiModelProperty(value = "")

  @Valid

  public List<BadgeDetails> getBadges() {
    return badges;
  }

  public void setBadges(List<BadgeDetails> badges) {
    this.badges = badges;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocalAuthority localAuthority = (LocalAuthority) o;
    return Objects.equals(this.laCode, localAuthority.laCode) &&
        Objects.equals(this.laName, localAuthority.laName) &&
        Objects.equals(this.issuingCountry, localAuthority.issuingCountry) &&
        Objects.equals(this.languageCode, localAuthority.languageCode) &&
        Objects.equals(this.clockType, localAuthority.clockType) &&
        Objects.equals(this.phoneNumber, localAuthority.phoneNumber) &&
        Objects.equals(this.emailAddress, localAuthority.emailAddress) &&
        Objects.equals(this.badges, localAuthority.badges);
  }

  @Override
  public int hashCode() {
    return Objects.hash(laCode, laName, issuingCountry, languageCode, clockType, phoneNumber, emailAddress, badges);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocalAuthority {\n");
    
    sb.append("    laCode: ").append(toIndentedString(laCode)).append("\n");
    sb.append("    laName: ").append(toIndentedString(laName)).append("\n");
    sb.append("    issuingCountry: ").append(toIndentedString(issuingCountry)).append("\n");
    sb.append("    languageCode: ").append(toIndentedString(languageCode)).append("\n");
    sb.append("    clockType: ").append(toIndentedString(clockType)).append("\n");
    sb.append("    phoneNumber: ").append(toIndentedString(phoneNumber)).append("\n");
    sb.append("    emailAddress: ").append(toIndentedString(emailAddress)).append("\n");
    sb.append("    badges: ").append(toIndentedString(badges)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

