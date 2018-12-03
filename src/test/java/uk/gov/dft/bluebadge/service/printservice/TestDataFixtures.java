package uk.gov.dft.bluebadge.service.printservice;

import java.util.Arrays;
import uk.gov.dft.bluebadge.service.printservice.model.BadgeDetails;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.model.Batches;
import uk.gov.dft.bluebadge.service.printservice.model.LetterAddress;
import uk.gov.dft.bluebadge.service.printservice.model.LocalAuthority;
import uk.gov.dft.bluebadge.service.printservice.model.Name;

public class TestDataFixtures {

  public static Batches batchesPayload() {
    Batches batches = new Batches();
    batches.add(batch1());
    batches.add(batch2());

    return batches;
  }

  public static Batch batchPayload() {
    return batch1();
  }

  private static Batch batch1() {
    Batch batch = new Batch();
    batch.setFilename("filename1");
    batch.batchType("STANDARD");
    batch.setLocalAuthorities(Arrays.asList(authority1(), authority2()));

    return batch;
  }

  private static Batch batch2() {
    Batch batch = new Batch();
    batch.setFilename("filename2");
    batch.batchType("FASTTRACK");
    batch.setLocalAuthorities(Arrays.asList(authority1(), authority2()));

    return batch;
  }

  private static LocalAuthority authority1() {
    LocalAuthority authority = new LocalAuthority();
    authority.setClockType("STANDARD");
    authority.setLaCode("ABERD");
    authority.setIssuingCountry("S");
    authority.setLaName("Aberdinshire council");
    authority.setLanguageCode("E");
    authority.setEmailAddress("bluebadge@aberdine.gov.uk");
    authority.setPhoneNumber("07875506745");
    authority.setBadges(Arrays.asList(badge1(), badge2()));

    return authority;
  }

  private static LocalAuthority authority2() {
    LocalAuthority authority = new LocalAuthority();
    authority.setClockType("WALET");
    authority.setLaCode("ANGL");
    authority.setIssuingCountry("W");
    authority.setLaName("Anglesey council");
    authority.setLanguageCode("EW");
    authority.setEmailAddress("bluebadge@anglesey.gov.uk");
    authority.setPhoneNumber("07875506746");
    authority.setBadges(Arrays.asList(badge3(), badge4()));

    return authority;
  }

  private static BadgeDetails badge1() {
    BadgeDetails details = new BadgeDetails();

    details.setBadgeIdentifier("AA12BB");
    details.setPrintedBadgeReference("AA12BB 0 0580X0121");
    details.setStartDate("2019-01-02");
    details.setExpiryDate("2021-01-01");
    details.setDispatchMethodCode("M");
    details.setFastTrackCode("Y");
    details.setPostageCode("SC");
    details.setPhoto("http://url_to_s3_bucket_photo1");
    details.setBarCodeData("80X1220");
    details.setName(getName("John", "First"));
    details.setLetterAddress(getAddress("John First", "20", "Main str.", "London", "SW1 1AA"));

    return details;
  }

  private static BadgeDetails badge2() {
    BadgeDetails details = new BadgeDetails();

    details.setBadgeIdentifier("AA34BB");
    details.setPrintedBadgeReference("AA34BB 0 0199Y0121");
    details.setStartDate("2019-01-02");
    details.setExpiryDate("2021-01-01");
    details.setDispatchMethodCode("C");
    details.setFastTrackCode("N");
    details.setPostageCode("SC");
    details.setPhoto("http://url_to_s3_bucket_photo2");
    details.setBarCodeData("Y1220");
    details.setName(getName("Jane", "Second"));
    details.setLetterAddress(
        getAddress("Jane Second", "Council", "government road", "London", "EC1 2Z"));

    return details;
  }

  private static BadgeDetails badge3() {
    BadgeDetails details = new BadgeDetails();

    details.setBadgeIdentifier("CC12DD");
    details.setPrintedBadgeReference("CC12DD 0 0152Y0121");
    details.setStartDate("2019-01-02");
    details.setExpiryDate("2021-01-01");
    details.setDispatchMethodCode("M");
    details.setFastTrackCode("Y");
    details.setPostageCode("SD1");
    details.setPhoto("http://url_to_s3_bucket_photo3");
    details.setBarCodeData("52Y1220");
    details.setName(getName("Michael", "Third"));
    details.setLetterAddress(
        getAddress("Michael Third", "flat 5", "century building", "Leeds", "LS1 3XX"));

    return details;
  }

  private static BadgeDetails badge4() {
    BadgeDetails details = new BadgeDetails();

    details.setBadgeIdentifier("CC34DD");
    details.setPrintedBadgeReference("CC34DD 0 1228Z0121");
    details.setStartDate("2019-01-02");
    details.setExpiryDate("2021-01-01");
    details.setDispatchMethodCode("M");
    details.setFastTrackCode("Y");
    details.setPostageCode("SD1");
    details.setPhoto("http://url_to_s3_bucket_photo4");
    details.setBarCodeData("Z0121");
    details.setName(getName("Name", "Last"));
    details.setLetterAddress(
        getAddress("Name Last", "88", "pleasant walk", "Manchester", "M4 3AS"));

    return details;
  }

  private static Name getName(String firstName, String lastName) {
    Name name = new Name();
    name.setForename(firstName);
    name.setSurname(lastName);
    return name;
  }

  private static LetterAddress getAddress(
      String name, String address1, String address2, String town, String postcode) {
    LetterAddress address = new LetterAddress();
    address.setNameLine(name);
    address.setAddressLine1(address1);
    address.setAddressLine2(address2);
    address.setCountry("United Kingdom");
    address.setTown(town);
    address.setPostcode(postcode);

    return address;
  }
}
