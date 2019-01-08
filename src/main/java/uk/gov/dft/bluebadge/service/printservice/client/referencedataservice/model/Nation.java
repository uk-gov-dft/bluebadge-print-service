package uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model;

import lombok.Getter;

@Getter
public enum Nation {
  ENG("E", "E", "Standard"),
  WLS("W", "EW", "Wallet"),
  SCO("S", "E", "Standard"),
  NIR("N", "E", "Standard");

  private String xmlPrintFileIssuingCountry;
  private final String xmlPrintFileLanguageCode;
  private final String xmlPrintFileClockType;

  Nation(
      String xmlPrintFileIssuingCountry,
      String xmlPrintFileLanguageCode,
      String xmlPrintFileClockType) {
    this.xmlPrintFileIssuingCountry = xmlPrintFileIssuingCountry;
    this.xmlPrintFileLanguageCode = xmlPrintFileLanguageCode;
    this.xmlPrintFileClockType = xmlPrintFileClockType;
  }
}
