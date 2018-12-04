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
  }
}
