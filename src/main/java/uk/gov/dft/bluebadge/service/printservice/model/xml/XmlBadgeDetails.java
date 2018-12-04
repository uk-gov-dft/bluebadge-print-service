package uk.gov.dft.bluebadge.service.printservice.model.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/** XmlBadgeDetails */
@Validated
public class XmlBadgeDetails {
  @JsonProperty("badgeIdentifier")
  private String badgeIdentifier = null;

  @JsonProperty("printedBadgeReference")
  private String printedBadgeReference = null;

  @JsonProperty("startDate")
  private String startDate = null;

  @JsonProperty("expiryDate")
  private String expiryDate = null;

  @JsonProperty("dispatchMethodCode")
  private String dispatchMethodCode = null;

  @JsonProperty("fastTrackCode")
  private String fastTrackCode = null;

  @JsonProperty("postageCode")
  private String postageCode = null;

  @JsonProperty("photo")
  private String photo = null;

  @JsonProperty("barCodeData")
  private String barCodeData = null;

  @JsonProperty("xmlName")
  private XmlName xmlName = null;

  @JsonProperty("xmlLetterAddress")
  private XmlLetterAddress xmlLetterAddress = null;

  public XmlBadgeDetails badgeIdentifier(String badgeIdentifier) {
    this.badgeIdentifier = badgeIdentifier;
    return this;
  }

  /**
   * Get badgeIdentifier
   *
   * @return badgeIdentifier
   */
  @ApiModelProperty(example = "6 digit badge number in DfT system", value = "")
  public String getBadgeIdentifier() {
    return badgeIdentifier;
  }

  public void setBadgeIdentifier(String badgeIdentifier) {
    this.badgeIdentifier = badgeIdentifier;
  }

  public XmlBadgeDetails printedBadgeReference(String printedBadgeReference) {
    this.printedBadgeReference = printedBadgeReference;
    return this;
  }

  /**
   * Get printedBadgeReference
   *
   * @return printedBadgeReference
   */
  @ApiModelProperty(
    example =
        "first 6 characters is 'badgeIdentifier' 7th character is the issue number this will always be 0 DDBB – month/year of birth of badge_holder, G - Gender (X=Male, Y=Female, Z=Not Specified) MMYY - month/year of badge expiry e.g. 'AA12BB 0 0290X1220'",
    value = ""
  )
  public String getPrintedBadgeReference() {
    return printedBadgeReference;
  }

  public void setPrintedBadgeReference(String printedBadgeReference) {
    this.printedBadgeReference = printedBadgeReference;
  }

  public XmlBadgeDetails startDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  /**
   * Get startDate
   *
   * @return startDate
   */
  @ApiModelProperty(example = "badge start date '2018-08-03'", value = "")
  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public XmlBadgeDetails expiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
    return this;
  }

  /**
   * Get expiryDate
   *
   * @return expiryDate
   */
  @ApiModelProperty(example = "badge expiry date '2020-08-03'", value = "")
  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }

  public XmlBadgeDetails dispatchMethodCode(String dispatchMethodCode) {
    this.dispatchMethodCode = dispatchMethodCode;
    return this;
  }

  /**
   * Get dispatchMethodCode
   *
   * @return dispatchMethodCode
   */
  @ApiModelProperty(example = "deliver to LA - 'C', deliver to home - 'M'", value = "")
  public String getDispatchMethodCode() {
    return dispatchMethodCode;
  }

  public void setDispatchMethodCode(String dispatchMethodCode) {
    this.dispatchMethodCode = dispatchMethodCode;
  }

  public XmlBadgeDetails fastTrackCode(String fastTrackCode) {
    this.fastTrackCode = fastTrackCode;
    return this;
  }

  /**
   * Get fastTrackCode
   *
   * @return fastTrackCode
   */
  @ApiModelProperty(example = "based on deliver_option_code 'Y' or 'N'", value = "")
  public String getFastTrackCode() {
    return fastTrackCode;
  }

  public void setFastTrackCode(String fastTrackCode) {
    this.fastTrackCode = fastTrackCode;
  }

  public XmlBadgeDetails postageCode(String postageCode) {
    this.postageCode = postageCode;
    return this;
  }

  /**
   * Get postageCode
   *
   * @return postageCode
   */
  @ApiModelProperty(
    example =
        "example 'SC' if deliver_option_code = 'STAND', 'SD1' if deliver_option_code = 'FAST'",
    value = ""
  )
  public String getPostageCode() {
    return postageCode;
  }

  public void setPostageCode(String postageCode) {
    this.postageCode = postageCode;
  }

  public XmlBadgeDetails photo(String photo) {
    this.photo = photo;
    return this;
  }

  /**
   * Get photo
   *
   * @return photo
   */
  @ApiModelProperty(
    example =
        "aws-s3-url to image, in case if image not provided - url to default palceholder          image",
    value = ""
  )
  public String getPhoto() {
    return photo;
  }

  public void setPhoto(String photo) {
    this.photo = photo;
  }

  public XmlBadgeDetails barCodeData(String barCodeData) {
    this.barCodeData = barCodeData;
    return this;
  }

  /**
   * Get barCodeData
   *
   * @return barCodeData
   */
  @ApiModelProperty(
    example =
        "last 7 characters of the 'printedBadgeReference' for PERSON badge, last 5 characters of the 'printedBadgeReference' for ORGANISATION badge, e.g. '44X0621'",
    value = ""
  )
  public String getBarCodeData() {
    return barCodeData;
  }

  public void setBarCodeData(String barCodeData) {
    this.barCodeData = barCodeData;
  }

  public XmlBadgeDetails name(XmlName xmlName) {
    this.xmlName = xmlName;
    return this;
  }

  /**
   * Get xmlName
   *
   * @return xmlName
   */
  @ApiModelProperty(value = "")
  @Valid
  public XmlName getXmlName() {
    return xmlName;
  }

  public void setXmlName(XmlName xmlName) {
    this.xmlName = xmlName;
  }

  public XmlBadgeDetails letterAddress(XmlLetterAddress xmlLetterAddress) {
    this.xmlLetterAddress = xmlLetterAddress;
    return this;
  }

  /**
   * Get xmlLetterAddress
   *
   * @return xmlLetterAddress
   */
  @ApiModelProperty(value = "")
  @Valid
  public XmlLetterAddress getXmlLetterAddress() {
    return xmlLetterAddress;
  }

  public void setXmlLetterAddress(XmlLetterAddress xmlLetterAddress) {
    this.xmlLetterAddress = xmlLetterAddress;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    XmlBadgeDetails xmlBadgeDetails = (XmlBadgeDetails) o;
    return Objects.equals(this.badgeIdentifier, xmlBadgeDetails.badgeIdentifier)
        && Objects.equals(this.printedBadgeReference, xmlBadgeDetails.printedBadgeReference)
        && Objects.equals(this.startDate, xmlBadgeDetails.startDate)
        && Objects.equals(this.expiryDate, xmlBadgeDetails.expiryDate)
        && Objects.equals(this.dispatchMethodCode, xmlBadgeDetails.dispatchMethodCode)
        && Objects.equals(this.fastTrackCode, xmlBadgeDetails.fastTrackCode)
        && Objects.equals(this.postageCode, xmlBadgeDetails.postageCode)
        && Objects.equals(this.photo, xmlBadgeDetails.photo)
        && Objects.equals(this.barCodeData, xmlBadgeDetails.barCodeData)
        && Objects.equals(this.xmlName, xmlBadgeDetails.xmlName)
        && Objects.equals(this.xmlLetterAddress, xmlBadgeDetails.xmlLetterAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        badgeIdentifier,
        printedBadgeReference,
        startDate,
        expiryDate,
        dispatchMethodCode,
        fastTrackCode,
        postageCode,
        photo,
        barCodeData,
        xmlName,
        xmlLetterAddress);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class XmlBadgeDetails {\n");

    sb.append("    badgeIdentifier: ").append(toIndentedString(badgeIdentifier)).append("\n");
    sb.append("    printedBadgeReference: ")
        .append(toIndentedString(printedBadgeReference))
        .append("\n");
    sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
    sb.append("    expiryDate: ").append(toIndentedString(expiryDate)).append("\n");
    sb.append("    dispatchMethodCode: ").append(toIndentedString(dispatchMethodCode)).append("\n");
    sb.append("    fastTrackCode: ").append(toIndentedString(fastTrackCode)).append("\n");
    sb.append("    postageCode: ").append(toIndentedString(postageCode)).append("\n");
    sb.append("    photo: ").append(toIndentedString(photo)).append("\n");
    sb.append("    barCodeData: ").append(toIndentedString(barCodeData)).append("\n");
    sb.append("    xmlName: ").append(toIndentedString(xmlName)).append("\n");
    sb.append("    xmlLetterAddress: ").append(toIndentedString(xmlLetterAddress)).append("\n");
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
