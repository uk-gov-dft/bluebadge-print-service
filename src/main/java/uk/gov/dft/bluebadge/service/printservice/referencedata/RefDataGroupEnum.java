package uk.gov.dft.bluebadge.service.printservice.referencedata;

public enum RefDataGroupEnum {
  PARTY("PARTY"),
  DELIVER_TO("DELIVER"),
  DELIVERY_OPTIONS("DELOP"),
  GENDER("GENDER"),
  LA("LA");

  public String getGroupKey() {
    return groupKey;
  }

  private final String groupKey;

  RefDataGroupEnum(String groupKey) {

    this.groupKey = groupKey;
  }
}
