package uk.gov.dft.bluebadge.service.printservice;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Stream;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.LocalAuthorityRefData;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.LocalAuthorityRefData.LocalAuthorityMetaData;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.Nation;
import uk.gov.dft.bluebadge.service.printservice.model.Badge;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.Batches;
import uk.gov.dft.bluebadge.service.printservice.model.Contact;
import uk.gov.dft.bluebadge.service.printservice.model.Organisation;
import uk.gov.dft.bluebadge.service.printservice.model.Party;
import uk.gov.dft.bluebadge.service.printservice.model.Person;

public class TestDataFixtures {

  public static Batches batchesPayload() {
    Batches xmlBatches = new Batches();
    xmlBatches.add(batch1());
    xmlBatches.add(batch2());

    return xmlBatches;
  }

  public static Batch standardBatchPayload() {
    return batch1();
  }

  public static Batch fasttrackBatchPayload() {
    return batch2();
  }

  public static Stream<Batch> payloads() {
    return Stream.of(standardBatchPayload(), fasttrackBatchPayload());
  }

  public static LocalAuthorityRefData welshLocalAuthority() {
    LocalAuthorityRefData la = new LocalAuthorityRefData();

    la.setShortCode("ANGL");
    la.setDescription("LA description");
    la.setLocalAuthorityMetaData(laMetaData("ANGL", Nation.WLS));

    return la;
  }

  public static LocalAuthorityRefData englishLocalAuthority() {
    LocalAuthorityRefData la = new LocalAuthorityRefData();

    la.setShortCode("LBKC");
    la.setDescription("LA description");
    la.setLocalAuthorityMetaData(laMetaData("LBKC", Nation.ENG));

    return la;
  }

  private static LocalAuthorityMetaData laMetaData(String shortCode, Nation nation) {
    LocalAuthorityMetaData meta = new LocalAuthorityMetaData();
    meta.setIssuingAuthorityShortCode(shortCode);
    meta.setIssuingAuthorityName("LA name");
    meta.setNation(nation);
    meta.setContactUrl("http://contact_url");
    meta.setNameLine2("name line 2");
    meta.setAddressLine1("address 1");
    meta.setAddressLine2("address 2");
    meta.setAddressLine3("address 3");
    meta.setAddressLine4("address 4");
    meta.setTown("town");
    meta.setCounty("county");
    meta.setCountry("United Kingdom");
    meta.setPostcode("SW1A 1AA");
    meta.setContactNumber("02070140800");
    meta.setEmailAddress("email@mail.com");

    return meta;
  }

  private static Batch batch1() {
    Batch batch = new Batch();
    batch.setFilename("filename1");
    batch.setBatchType("STANDARD");
    batch.setBadges(Arrays.asList(badge1(), badge2(), badge3()));
    return batch;
  }

  private static Batch batch2() {
    Batch batch = new Batch();
    batch.setFilename("filename2");
    batch.setBatchType("FASTTRACK");
    batch.setBadges(Arrays.asList(badge2(), badge3(), badge4()));

    return batch;
  }

  private static Badge badge1() {
    Badge details = new Badge();
    details.setBadgeNumber("AA12BB");
    details.setLocalAuthorityShortCode("ANGL");
    details.setStartDate(LocalDate.of(2019, 01, 02));
    details.setExpiryDate(LocalDate.of(2021, 1, 1));
    details.setImageLink("http://url_to_s3_bucket_photo1");
    details.setParty(
        personParty(
            createContact("John First", "20", "Main str.", "London", "SW1 1AA", "john@email.com"),
            person1()));
    details.setDeliverToCode("HOME");
    details.setDeliveryOptionCode("STAND");

    return details;
  }

  private static Badge badge2() {

    Badge details = new Badge();
    details.setBadgeNumber("AA34BB");
    details.setLocalAuthorityShortCode("ANGL");
    details.setStartDate(LocalDate.of(2019, 01, 02));
    details.setExpiryDate(LocalDate.of(2021, 1, 1));
    details.setImageLink("http://url_to_s3_bucket_photo2");
    details.setParty(
        personParty(
            createContact(
                "Jane Second", "Council", "government road", "London", "EC1 2Z", "jane@email.com"),
            person2()));
    details.setDeliverToCode("HOME");
    details.setDeliveryOptionCode("FAST");

    return details;
  }

  private static Badge badge3() {

    Badge details = new Badge();
    details.setBadgeNumber("CC12DD");
    details.setLocalAuthorityShortCode("LBKC");
    details.setStartDate(LocalDate.of(2019, 01, 02));
    details.setExpiryDate(LocalDate.of(2021, 1, 1));
    details.setImageLink("http://url_to_s3_bucket_photo3");
    details.setParty(
        organisationParty(
            createContact(
                "XmlName Last", "88", "pleasant walk", "Manchester", "M4 3AS", "xml@email.com"),
            organisation()));
    details.setDeliverToCode("COUNCIL");
    details.setDeliveryOptionCode("STAND");

    return details;
  }

  private static Badge badge4() {

    Badge details = new Badge();
    details.setBadgeNumber("CC34DD");
    details.setLocalAuthorityShortCode("LBKC");
    details.setStartDate(LocalDate.of(2019, 01, 02));
    details.setExpiryDate(LocalDate.of(2021, 1, 1));
    details.setImageLink("http://url_to_s3_bucket_photo4");
    details.setParty(
        personParty(
            createContact(
                "Michael Third",
                "flat 5",
                "century building",
                "Leeds",
                "LS1 3XX",
                "mike@email.com"),
            person3()));
    details.setDeliverToCode("HOME");
    details.setDeliveryOptionCode("STAND");

    return details;
  }

  private static Party personParty(Contact contact, Person person) {
    Party party = new Party();
    party.setContact(contact);
    party.setPerson(person);
    party.setTypeCode("PERSON");

    return party;
  }

  private static Party organisationParty(Contact contact, Organisation organisation) {
    Party party = new Party();
    party.setContact(contact);
    party.setOrganisation(organisation);
    party.setTypeCode("ORG");
    return party;
  }

  private static Organisation organisation() {
    Organisation o = new Organisation();
    o.setBadgeHolderName("Organisation for disabled people");

    return o;
  }

  private static Person person1() {
    Person p = new Person();
    p.setBadgeHolderName("Michelangelo Lodovico Buonarroti Simoni");
    p.setDob(LocalDate.of(1977, 3, 4));
    p.setGenderCode("MALE");
    return p;
  }

  private static Person person2() {
    Person p = new Person();
    p.setBadgeHolderName("Jane Second");
    p.setDob(LocalDate.of(1987, 12, 8));
    p.setGenderCode("FEMALE");
    return p;
  }

  private static Person person3() {
    Person p = new Person();
    p.setBadgeHolderName("Michael Third");
    p.setDob(LocalDate.of(1934, 2, 5));
    p.setGenderCode("MALE");
    return p;
  }

  private static Person person4() {
    Person p = new Person();
    p.setBadgeHolderName("XmlName Last");
    p.setDob(LocalDate.of(1964, 12, 15));
    p.setGenderCode("UNSPECIFIED");
    return p;
  }

  private static Contact createContact(
      String name, String address1, String address2, String town, String postcode, String email) {
    Contact address = new Contact();
    address.setFullName(name);
    address.setLine2(address1);
    address.setBuildingStreet(address2);
    address.setTownCity(town);
    address.setPostCode(postcode);
    address.setPrimaryPhoneNumber(""); // Is this needed
    address.setEmailAddress(email);

    return address;
  }
}
