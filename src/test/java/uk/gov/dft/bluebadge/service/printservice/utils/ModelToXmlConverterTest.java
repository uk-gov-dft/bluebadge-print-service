package uk.gov.dft.bluebadge.service.printservice.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.englishLocalAuthority;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.fasttrackBatchPayload;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.standardBatchPayload;
import static uk.gov.dft.bluebadge.service.printservice.TestDataFixtures.welshLocalAuthority;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.dft.bluebadge.service.printservice.StorageService;
import uk.gov.dft.bluebadge.service.printservice.referencedata.ReferenceDataService;

@Slf4j
class ModelToXmlConverterTest {

  private StorageService s3 = mock(StorageService.class);
  private final ReferenceDataService referenceData = mock(ReferenceDataService.class);

  private ModelToXmlConverter converter = new ModelToXmlConverter(s3, referenceData);

  private String originalTmpDir = System.getProperty("java.io.tmpdir");

  private String s3PictureFilePath =
      Paths.get("src", "test", "resources", "tmp", "printbatch_pics", "smile.jpg").toString();

  @BeforeEach
  void beforeEachTest(TestInfo testInfo) {
    log.info(String.format("About to execute [%s]", testInfo.getDisplayName()));

    String testTmpDir = Paths.get("src", "test", "resources", "tmp").toString();
    System.setProperty("java.io.tmpdir", testTmpDir);
  }

  @AfterEach
  void afterEachTest(TestInfo testInfo) {
    log.info(String.format("Finished executing [%s]", testInfo.getDisplayName()));
    System.setProperty("java.io.tmpdir", originalTmpDir);
  }

  @DisplayName("Should convert model and save standard xml file in temp folder")
  @SneakyThrows
  @Test
  void convertStandardBatchAndSave() {
    Path xmlDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");
    File picture = new File(s3PictureFilePath);
    when(s3.downloadBadgeFile(any())).thenReturn(Optional.of(Files.readAllBytes(picture.toPath())));
    when(referenceData.retrieveLocalAuthority("ANGL")).thenReturn(welshLocalAuthority());
    when(referenceData.retrieveLocalAuthority("GLOCC")).thenReturn(englishLocalAuthority());

    String file = converter.toXml(standardBatchPayload(), xmlDir);
    boolean expected = Files.exists(Paths.get(file));
    assertTrue(expected);
    deleteXmlFile(file);
  }

  @DisplayName("Should convert model and save FastTrack xml file in temp folder")
  @SneakyThrows
  @Test
  void convertFastTrackBatchAndSave() {
    Path xmlDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");
    File picture = new File(s3PictureFilePath);
    when(s3.downloadBadgeFile(any())).thenReturn(Optional.of(Files.readAllBytes(picture.toPath())));
    when(referenceData.retrieveLocalAuthority("ANGL")).thenReturn(welshLocalAuthority());
    when(referenceData.retrieveLocalAuthority("GLOCC")).thenReturn(englishLocalAuthority());

    String file = converter.toXml(fasttrackBatchPayload(), xmlDir);
    boolean expected = Files.exists(Paths.get(file));
    assertTrue(expected);
    deleteXmlFile(file);
  }

  @DisplayName("Should return IssuingCountry = `W` for LACode=ANGL")
  @SneakyThrows
  @Test
  void testIssuingCountryWales() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='ANGL']/IssuingCountry";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("W", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return LanguageCode = `EW` for LACode=ANGL")
  @SneakyThrows
  @Test
  void testLanguageCodeWales() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='ANGL']/LanguageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("EW", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return ClockType = `Wallet` for LACode=ANGL")
  @SneakyThrows
  @Test
  void testClockTypeWales() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='ANGL']/ClockType";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("Wallet", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return IssuingCountry = `E` for LACode=GLOCC")
  @SneakyThrows
  @Test
  void testIssuingCountryEngland() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='GLOCC']/IssuingCountry";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("E", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return LanguageCode = `E` for LACode=GLOCC")
  @SneakyThrows
  @Test
  void testLanguageCodeEngland() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='GLOCC']/LanguageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("E", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return ClockType = `Standard` for LACode=GLOCC")
  @SneakyThrows
  @Test
  void testClockTypeEngland() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority[LACode='GLOCC']/ClockType";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("Standard", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName(
      "Should return PrintedBadgeReference = `AA12BB 9 0377X0121` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testBadgeIdentifierForPerson() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/PrintedBadgeReference";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("AA12BB 9 0377X0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return PrintedBadgeReference = `CC12DD 9 O0121` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testBadgeIdentifierForOrganisation() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/PrintedBadgeReference";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("CC12DD 9 O0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return organisation stock photo for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testStockPhotoForOrganisation() {
    Path xmlDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");
    when(s3.downloadBadgeFile(any())).thenReturn(Optional.empty());
    when(referenceData.retrieveLocalAuthority("ANGL")).thenReturn(welshLocalAuthority());
    when(referenceData.retrieveLocalAuthority("GLOCC")).thenReturn(englishLocalAuthority());

    String file = converter.toXml(standardBatchPayload(), xmlDir);

    assertTrue(Files.exists(Paths.get(file)));

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(file);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/Photo";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    String expected =
        new Scanner(getClass().getResourceAsStream("/orgpictures/england_base64.txt"), "UTF-8")
            .next();
    assertEquals(expected, node.getTextContent());

    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='WALESO']/Photo";
    node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    expected =
        new Scanner(getClass().getResourceAsStream("/orgpictures/wales_base64.txt"), "UTF-8")
            .next();
    assertEquals(expected, node.getTextContent());

    deleteXmlFile(file);
  }

  @DisplayName("Should return DispatchMethodCode = `M` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testDispatchMethodCodeToHome() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/DispatchMethodCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("M", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return DispatchMethodCode = `C` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testDispatchMethodCodeToCouncil() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/DispatchMethodCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("C", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return FastTrackCode = `N` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testFastTrackCodeNo() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/FastTrackCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("N", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return FastTrackCode = `Y` for BadgeIdentifier=AA34BB")
  @SneakyThrows
  @Test
  void testFastTrackCodeYes() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/FastTrackCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("Y", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return PostageCode = `SC` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testPostageCodeSC() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/PostageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("SC", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return PostageCode = `SD1` for BadgeIdentifier=AA34BB")
  @SneakyThrows
  @Test
  void testPostageCodeSD1() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/PostageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("SD1", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return BarCodeData = `77X0121` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testBarCodeDataForPerson() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/BarCodeData";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("77X0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return BarCodeData = `O0121` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testBarCodeDataForOrganisation() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/BarCodeData";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("O0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName(
      "Should return populate only Name=`Jane Second` and leave Surname blank for BadgeIdentifier=AA34BB")
  @SneakyThrows
  @Test
  void testShortName() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/Name";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("Jane Second", node.getTextContent());

    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/Surname";
    node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("", node.getTextContent());

    deleteXmlFile(xmlPath);
  }

  @DisplayName(
      "Should return populate Name=`Michelangelo` and Surname=`Lodovico Buonarroti Simoni` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  void testLongName() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/Name";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("Michelangelo", node.getTextContent());

    expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/Surname";
    node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("Lodovico Buonarroti Simoni", node.getTextContent());

    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should deliver to council address for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  void testDeliveryToCouncilAddress() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/LetterAddress";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    NodeList addrLines = node.getChildNodes();

    assertEquals("Organisation for disabled people", addrLines.item(0).getTextContent());
    assertEquals("address 1", addrLines.item(1).getTextContent());
    assertEquals("address 2", addrLines.item(2).getTextContent());
    assertEquals("town", addrLines.item(3).getTextContent());
    assertEquals("United Kingdom", addrLines.item(4).getTextContent());
    assertEquals("SW1A 1AA", addrLines.item(5).getTextContent());

    deleteXmlFile(xmlPath);
  }

  private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true); // never forget this!
    return factory.newDocumentBuilder();
  }

  private String saveXmlFile() throws IOException, XMLStreamException {
    String s3PictureFilePath =
        Paths.get("src", "test", "resources", "tmp", "printbatch_pics", "smile_small.jpg")
            .toString();
    Path xmlDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");
    File picture = new File(s3PictureFilePath);
    when(s3.downloadBadgeFile(any())).thenReturn(Optional.of(Files.readAllBytes(picture.toPath())));
    when(referenceData.retrieveLocalAuthority("ANGL")).thenReturn(welshLocalAuthority());
    when(referenceData.retrieveLocalAuthority("GLOCC")).thenReturn(englishLocalAuthority());

    String file = converter.toXml(standardBatchPayload(), xmlDir);
    boolean expected = Files.exists(Paths.get(file));
    assertTrue(expected);

    return file;
  }

  @SneakyThrows
  private void deleteXmlFile(String xmlPath) {
    Files.delete(Paths.get(xmlPath));
  }
}
