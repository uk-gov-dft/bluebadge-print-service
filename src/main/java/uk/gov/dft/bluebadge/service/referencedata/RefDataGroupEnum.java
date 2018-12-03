package uk.gov.dft.bluebadge.service.referencedata;

@SuppressWarnings("SpellCheckingInspection")
public enum RefDataGroupEnum {
  ELIGIBILITY("ELIGIBILIT"),
  APP_SOURCE("APPSOURCE"),
  PARTY("PARTY"),
  STATUS("STATUS"),
  DELIVER_TO("DELIVER"),
  DELIVERY_OPTIONS("DELOP"),
  GENDER("GENDER"),
  CANCEL("CANCEL"),
  LA("LA"),
  REPLACE("REPLACE");

  public String getGroupKey() {
    return groupKey;
  }

  private final String groupKey;

  RefDataGroupEnum(String groupKey) {

    this.groupKey = groupKey;
  }
}
