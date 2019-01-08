package uk.gov.dft.bluebadge.service.printservice.converters;

final class XmlSchemaConstants {

  private XmlSchemaConstants() {}

  static class Common {
    static final String XML_VERSION = "1.0";
    static final String XML_ENCODING = "windows-1252";
    static final String DATE_PATTERN = "yyyy-MM-dd";
  }

  static class PrintRequestElements {
    static final String ROOT = "BadgePrintExtract";
    static final String BATCH = "Batch";
    static final String FILENAME = "Filename";
    static final String RE_EXTRACT = "ReExtract";
    static final String RE_EXTRACT_VALUE = "no";
    static final String LOCAL_AUTHORITIES = "LocalAuthorities";
    static final String LOCAL_AUTHORITY = "LocalAuthority";
    static final String BADGES = "Badges";
    static final String BADGE_DETAIL = "BadgeDetails";
    static final String BADGE_IDENTIFIER = "BadgeIdentifier";
    static final String BADGE_REFERENCE = "PrintedBadgeReference";
    static final String START_DATE = "StartDate";
    static final String EXPIRY_DATE = "ExpiryDate";
    static final String DISPATCH_METHOD = "DispatchMethodCode";
    static final String FASTTRACK_CODE = "FastTrackCode";
    static final String POSTAGE_CODE = "PostageCode";
    static final String BAR_CODE = "BarCodeData";
    static final String PHOTO = "Photo";
    static final String LETTER_ADDRESS = "LetterAddress";
    static final String LETTER_ADDRESS_NAME = "NameLine1";
    static final String LETTER_ADDRESS_LINE1 = "AddressLine1";
    static final String LETTER_ADDRESS_LINE2 = "AddressLine2";
    static final String LETTER_ADDRESS_TOWN = "Town";
    static final String LETTER_ADDRESS_COUNTRY = "Country";
    static final String LETTER_ADDRESS_COUNTRY_VALUE = "United Kingdom";
    static final String LETTER_ADDRESS_POSTCODE = "Postcode";
    static final String NAME = "Name";
    static final String SURNAME = "Surname";
    static final String FORENAME = "Forename";
    static final String ORG_NAME = "OrganisationName";
    static final String LA_CODE = "LACode";
    static final String LA_NAME = "LAName";
    static final String ISSUING_COUNTRY = "IssuingCountry";
    static final String LANGUAGE_CODE = "LanguageCode";
    static final String CLOCK_TYPE = "ClockType";
    static final String LA_PHONE_NO = "PhoneNumber";
    static final String LA_EMAIL = "EmailAddress";
  }
}
