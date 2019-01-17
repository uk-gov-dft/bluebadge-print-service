package uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LocalAuthorityRefData extends ReferenceData {

  @JsonProperty("metaData")
  private LocalAuthorityMetaData localAuthorityMetaData = null;

  @Data
  public static class LocalAuthorityMetaData {
    private String issuingAuthorityShortCode;
    private String issuingAuthorityName;
    private Nation nation;
    private String contactUrl;
    private String nameLine2;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressLine4;
    private String town;
    private String county;
    private String country;
    private String postcode;
    private String contactNumber;
    private String emailAddress;
    private String clockType;
  }
}
