package uk.gov.dft.bluebadge.service.printservice.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.DODGY_IMAGE_LINK;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.englishLocalAuthority;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.fasttrackBatchPayload;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.standardBatchPayload;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.standardDodgyBatchPayLoad;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.welshLocalAuthority;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.welshSwanseaLocalAuthority;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.gov.dft.bluebadge.service.printservice.StorageService;
import uk.gov.dft.bluebadge.service.printservice.config.GeneralConfig;
import uk.gov.dft.bluebadge.service.printservice.model.Badge;
import uk.gov.dft.bluebadge.service.printservice.model.Party;
import uk.gov.dft.bluebadge.service.printservice.model.Person;
import uk.gov.dft.bluebadge.service.printservice.referencedata.ReferenceDataService;

@Slf4j
class PrintRequestToPrintXmlTest {

  private XPathFactory xpathfactory = XPathFactory.newInstance();
  private XPath xpath = xpathfactory.newXPath();
  private static Path xmlDir = Paths.get("src", "test", "resources", "tmp", "printbatch_xml");
  private static StorageService s3Mock = mock(StorageService.class);
  private static final ReferenceDataService referenceDataMock = mock(ReferenceDataService.class);
  private static final GeneralConfig generalConfigMock = mock(GeneralConfig.class);
  private static Document parsedStandardXmlFile;
  // Path to converted file.  File deleted at end of test class.
  private static String standardXmlFile;
  private static PrintRequestToPrintXml converter =
      new PrintRequestToPrintXml(s3Mock, generalConfigMock);

  private String s3PictureFilePath = "/tmp/printbatch_pics/smile.jpg";

  @BeforeAll
  static void before()
      throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
    when(generalConfigMock.getOrganisationPhotoUriEngland()).thenReturn("/pictures/org_E.jpg");
    when(generalConfigMock.getOrganisationPhotoUriWales()).thenReturn("/pictures/org_W.jpg");
    when(referenceDataMock.retrieveLocalAuthority("ANGL")).thenReturn(welshLocalAuthority());
    when(referenceDataMock.retrieveLocalAuthority("SWAN")).thenReturn(welshSwanseaLocalAuthority());
    when(referenceDataMock.retrieveLocalAuthority("GLOCC")).thenReturn(englishLocalAuthority());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true); // never forget this!

    saveStandardXmlFile();
    parsedStandardXmlFile = factory.newDocumentBuilder().parse(standardXmlFile);
  }

  @AfterAll
  static void after() throws IOException {
    Files.delete(Paths.get(standardXmlFile));
  }

  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
  }

  @DisplayName("Should convert model and save standard xml file in temp folder")
  @SneakyThrows
  @Test
  void convertStandardBatchAndSave() {
    when(s3Mock.downloadBadgeFile(any()))
        .thenReturn(
            Optional.of(IOUtils.toByteArray(getClass().getResourceAsStream(s3PictureFilePath))));

    assertTrue(Files.exists(Paths.get(standardXmlFile)));
  }

  @DisplayName("Should convert model and save FastTrack xml file in temp folder")
  @SneakyThrows
  @Test
  void convertFastTrackBatchAndSave() {
    when(s3Mock.downloadBadgeFile(any()))
        .thenReturn(
            Optional.of(IOUtils.toByteArray(getClass().getResourceAsStream(s3PictureFilePath))));

    String file = converter.toXml(fasttrackBatchPayload(), xmlDir, referenceDataMock);
    boolean expected = Files.exists(Paths.get(file));
    assertTrue(expected);
    deleteXmlFile(file);
  }

  @DisplayName(
      "Should convert model and save Standard xml file without dodgy badges in temp folder")
  @SneakyThrows
  @Test
  void convertStandardWithDodgyBadgesBatchAndSave() {
    when(s3Mock.downloadBadgeFile("http://url_to_s3_bucket_photo1"))
        .thenReturn(
            Optional.of(IOUtils.toByteArray(getClass().getResourceAsStream(s3PictureFilePath))));
    when(s3Mock.downloadBadgeFile("http://url_to_s3_bucket_photo2"))
        .thenReturn(
            Optional.of(IOUtils.toByteArray(getClass().getResourceAsStream(s3PictureFilePath))));
    when(s3Mock.downloadBadgeFile(DODGY_IMAGE_LINK)).thenThrow(new AmazonS3Exception("fail"));
    when(generalConfigMock.getOrganisationPhotoUriEngland()).thenReturn(null);

    String file = converter.toXml(standardDodgyBatchPayLoad(), xmlDir, referenceDataMock);
    boolean expected = Files.exists(Paths.get(file));
    assertTrue(expected);

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true); // never forget this!
    Document parseDodgyStandardXmlFile = factory.newDocumentBuilder().parse(file);

    Node nodeDodgyBadge1 =
        getNodeTextInXmlFile(
            parseDodgyStandardXmlFile,
            "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']");
    assertThat(nodeDodgyBadge1).isNull();

    Node nodeBadge2 =
        getNodeTextInXmlFile(
            parseDodgyStandardXmlFile,
            "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']");
    assertThat(nodeBadge2).isNotNull();

    Node nodeBadge3 =
        getNodeTextInXmlFile(
            parseDodgyStandardXmlFile,
            "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']");
    assertThat(nodeBadge3).isNull();

    Node nodeBadgeWales =
        getNodeTextInXmlFile(
            parseDodgyStandardXmlFile,
            "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='WALESO']");
    assertThat(nodeBadgeWales).isNotNull();

    Node nodeDodgyBadge5 =
        getNodeTextInXmlFile(
            parseDodgyStandardXmlFile,
            "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BF']");
    assertThat(nodeDodgyBadge5).isNull();

    Node nodeDodgyBadge6 =
        getNodeTextInXmlFile(
            parseDodgyStandardXmlFile,
            "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DM']");
    assertThat(nodeDodgyBadge6).isNull();

    deleteXmlFile(file);
  }

  @DisplayName("Should return IssuingCountry = `W` for LACode=ANGL")
  @SneakyThrows
  @Test
  void testIssuingCountryWales() {
    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='ANGL']/IssuingCountry";

    assertEquals("W", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return LanguageCode = `EW` for LACode=ANGL")
  @SneakyThrows
  @Test
  void testLanguageCodeWales() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='ANGL']/LanguageCode";

    assertEquals("EW", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName(
      "Should return ClockType = `Standard` for LACode=ANGL, different from default for Nation")
  @SneakyThrows
  @Test
  void testOverriddenClockTypeWales() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='ANGL']/ClockType";

    assertEquals("Standard", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return ClockType = `Wallet` for LACode=SWAN")
  @SneakyThrows
  @Test
  void testClockTypeWales() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='SWAN']/ClockType";

    assertEquals("Wallet", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return IssuingCountry = `E` for LACode=GLOCC")
  @SneakyThrows
  @Test
  void testIssuingCountryEngland() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='GLOCC']/IssuingCountry";

    assertEquals("E", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return LanguageCode = `E` for LACode=GLOCC")
  @SneakyThrows
  @Test
  void testLanguageCodeEngland() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='GLOCC']/LanguageCode";

    assertEquals("E", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return ClockType = `Standard` for LACode=GLOCC")
  @SneakyThrows
  @Test
  void testClockTypeEngland() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='GLOCC']/ClockType";

    assertEquals("Standard", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName(
      "Should return PrintedBadgeReference = `AA12BB 9 0377X0121` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testBadgeIdentifierForPerson() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/PrintedBadgeReference";

    assertEquals("AA12BB 0 0377X0121", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return PrintedBadgeReference = `CC12DD 9 O0121` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testBadgeIdentifierForOrganisation() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/PrintedBadgeReference";

    assertEquals("CC12DD 0 O0121", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return organisation stock photo for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testStockPhotoForOrganisation() {
    when(s3Mock.downloadBadgeFile(any())).thenReturn(Optional.empty());

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/Photo";

    String expected =
        new Scanner(getClass().getResourceAsStream("/orgpictures/england_base64.txt"), "UTF-8")
            .next();
    assertEquals(expected, getNodeTextInStandardXmlFile(expression));

    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='WALESO']/Photo";

    expected =
        new Scanner(getClass().getResourceAsStream("/orgpictures/wales_base64.txt"), "UTF-8")
            .next();
    assertEquals(expected, getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return DispatchMethodCode = `M` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testDispatchMethodCodeToHome() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/DispatchMethodCode";

    assertEquals("M", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return DispatchMethodCode = `C` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testDispatchMethodCodeToCouncil() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/DispatchMethodCode";

    assertEquals("C", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return FastTrackCode = `N` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testFastTrackCodeNo() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/FastTrackCode";

    assertEquals("N", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return FastTrackCode = `Y` for BadgeIdentifier=AA34BB")
  @SneakyThrows
  @Test
  void testFastTrackCodeYes() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/FastTrackCode";

    assertEquals("Y", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return PostageCode = `SC` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testPostageCodeSC() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/PostageCode";

    assertEquals("SC", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return PostageCode = `RD` for BadgeIdentifier=AA34BB (FAST)")
  @SneakyThrows
  @Test
  void testPostageCodeSD1() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/PostageCode";

    assertEquals("RD", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return BarCodeData = `77X0121` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testBarCodeDataForPerson() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/BarCodeData";

    assertEquals("77X0121", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should return BarCodeData = `O0121` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testBarCodeDataForOrganisation() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/BarCodeData";

    assertEquals("O0121", getNodeTextInStandardXmlFile(expression));
  }

  @Test
  @SneakyThrows
  void emptyElementTest() {
    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='WALESO']/LetterAddress/AddressLine2";

    assertThat(xpath.compile(expression).evaluate(parsedStandardXmlFile, XPathConstants.NODE))
        .isNull();
  }

  @SneakyThrows
  @Test
  void testShortName() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/Name/Surname";

    assertEquals("Jane Second", getNodeTextInStandardXmlFile(expression));

    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/Name/Forename";

    assertThat(xpath.compile(expression).evaluate(parsedStandardXmlFile, XPathConstants.NODE))
        .isNull();

    // Organisation
    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='WALESO']/OrganisationName";

    assertEquals("Organisation for disabled people", getNodeTextInStandardXmlFile(expression));

    // For org, the contact name should be in name.
    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='WALESO']/Name/Surname";
    assertEquals("The contact name", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName(
      "Should return populate Name=`Michelangelo` and Surname=`Lodovico Buonarroti Simoni` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testLongName() {

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/Name/Forename";
    assertEquals("Michelangelo Lodovico Buonarroti", getNodeTextInStandardXmlFile(expression));

    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/Name/Surname";
    assertEquals("Simoni", getNodeTextInStandardXmlFile(expression));
  }

  @DisplayName("Should deliver to council address for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testDeliveryToCouncilCollectionAddress() {

    // When deliver to council
    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/CollectionAddress";
    Node node =
        (Node) xpath.compile(expression).evaluate(parsedStandardXmlFile, XPathConstants.NODE);
    NodeList addrLines = node.getChildNodes();

    // Then collection address is populated
    assertEquals("LA description", addrLines.item(0).getTextContent());
    assertEquals("name line 2", addrLines.item(1).getTextContent());
    assertEquals("address 1", addrLines.item(2).getTextContent());
    assertEquals("address 2", addrLines.item(3).getTextContent());
    assertEquals("address 3", addrLines.item(4).getTextContent());
    assertEquals("address 4", addrLines.item(5).getTextContent());
    assertEquals("town", addrLines.item(6).getTextContent());
    assertEquals("United Kingdom", addrLines.item(7).getTextContent());
    assertEquals("SW1A 1AA", addrLines.item(8).getTextContent());

    // When deliver to is home
    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/CollectionAddress";
    node = (Node) xpath.compile(expression).evaluate(parsedStandardXmlFile, XPathConstants.NODE);

    // Then no collection address
    assertThat(node).isNull();
  }

  private static void saveStandardXmlFile() throws IOException, XMLStreamException {
    when(s3Mock.downloadBadgeFile(any()))
        .thenReturn(
            Optional.of(
                IOUtils.toByteArray(
                    PrintRequestToPrintXmlTest.class
                        .getResourceAsStream("/tmp/printbatch_pics/smile_small.jpg"))));

    standardXmlFile = converter.toXml(standardBatchPayload(), xmlDir, referenceDataMock);
    boolean expected = Files.exists(Paths.get(standardXmlFile));
    assertTrue(expected);
  }

  @SneakyThrows
  private void deleteXmlFile(String xmlPath) {
    Files.delete(Paths.get(xmlPath));
  }

  @Test
  void getSurnameForenamePair_Test() {
    Pair<String, String> result;

    result = converter.getSurnameForenamePair("Less than 28 chars");
    assertThat(result.getLeft()).isEqualTo("Less than 28 chars");
    assertThat(result.getRight()).isNull();

    result = converter.getSurnameForenamePair("morethan28chars withasensiblespace");
    assertThat(result.getLeft()).isEqualTo("withasensiblespace");
    assertThat(result.getRight()).isEqualTo("morethan28chars");
  }

  @Test
  void getPrintedBadgeReference_andBarcode_Test() {
    Badge badge = new Badge();
    // For a person badge....
    badge.setBadgeNumber("ABCDEF");
    badge.setExpiryDate(LocalDate.of(2050, 1, 1));
    badge.setParty(new Party());
    badge.getParty().setTypeCode(Party.PartyType.PERSON);
    badge.getParty().setPerson(new Person());
    badge.getParty().getPerson().setDob(LocalDate.of(1970, 12, 31));
    badge.getParty().getPerson().setGenderCode(Person.GenderCode.MALE);

    String reference = converter.getPrintedBadgeReference(badge);
    assertThat(reference).isEqualTo("ABCDEF 0 1270X0150");
    assertThat(converter.getBarCode(badge)).isEqualTo("70X0150");

    // If female...
    badge.getParty().getPerson().setGenderCode(Person.GenderCode.FEMALE);
    reference = converter.getPrintedBadgeReference(badge);
    assertThat(reference).isEqualTo("ABCDEF 0 1270Y0150");
    assertThat(converter.getBarCode(badge)).isEqualTo("70Y0150");

    // If gender unspecified
    badge.getParty().getPerson().setGenderCode(Person.GenderCode.UNSPECIFIE);
    reference = converter.getPrintedBadgeReference(badge);
    assertThat(reference).isEqualTo("ABCDEF 0 1270Z0150");
    assertThat(converter.getBarCode(badge)).isEqualTo("70Z0150");

    // If Organisation
    badge.getParty().setPerson(null);
    badge.getParty().setTypeCode(Party.PartyType.ORG);
    reference = converter.getPrintedBadgeReference(badge);
    assertThat(reference).isEqualTo("ABCDEF 0 O0150");
    assertThat(converter.getBarCode(badge)).isEqualTo("O0150");
  }

  @Test
  public void escapeContent_shouldWork() {
    assertThat(converter.escapeContent("hello & bye")).isEqualTo("hello &amp; bye");
    assertThat(converter.escapeContent("hello > bye")).isEqualTo("hello &gt; bye");
    assertThat(converter.escapeContent("hello < bye")).isEqualTo("hello &lt; bye");
    assertThat(converter.escapeContent("hello & < > bye")).isEqualTo("hello &amp; &lt; &gt; bye");
    assertThat(converter.escapeContent("hello && << >> bye"))
        .isEqualTo("hello &amp;&amp; &lt;&lt; &gt;&gt; bye");
    assertThat(converter.escapeContent("hello && << >> >&< bye"))
        .isEqualTo("hello &amp;&amp; &lt;&lt; &gt;&gt; &gt;&amp;&lt; bye");
  }

  @SneakyThrows
  private Node getNodeTextInXmlFile(Document doc, String xPathExpression) {
    Node node = (Node) xpath.compile(xPathExpression).evaluate(doc, XPathConstants.NODE);
    return node;
  }

  @SneakyThrows
  private String getNodeTextInStandardXmlFile(String xPathExpression) {
    Node node =
        (Node) xpath.compile(xPathExpression).evaluate(parsedStandardXmlFile, XPathConstants.NODE);
    return node.getTextContent();
  }
}
