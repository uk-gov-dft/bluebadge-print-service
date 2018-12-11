package uk.gov.dft.bluebadge.service.printservice.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import java.util.stream.Stream;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.gov.dft.bluebadge.service.printservice.StorageService;
import uk.gov.dft.bluebadge.service.printservice.model.Batch;
import uk.gov.dft.bluebadge.service.printservice.referencedata.ReferenceDataService;

@RunWith(JUnitPlatform.class)
@Slf4j
public class ModelToXmlConverterTest {

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

  @DisplayName("Should convert model and save xml file in temp folder")
  @SneakyThrows
  @ParameterizedTest
  @MethodSource("payloads")
  public void convertStandardBatchAndSave(Batch payload) {
    Path xmlDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");
    File picture = new File(s3PictureFilePath);
    when(s3.downloadFile(any(), eq(xmlDir.resolve("pictures")))).thenReturn(Optional.of(picture));
    when(referenceData.retrieveLocalAuthority("ANGL")).thenReturn(welshLocalAuthority());
    when(referenceData.retrieveLocalAuthority("LBKC")).thenReturn(englishLocalAuthority());

    String file = converter.toXml(payload, xmlDir);
    boolean expected = Files.exists(Paths.get(file));
    assertTrue(expected);
  }

  private static Stream<Batch> payloads() {
    return Stream.of(standardBatchPayload(), fasttrackBatchPayload());
  }

  @DisplayName("Should return IssuingCountry = `W` for LACode=ANGL")
  @SneakyThrows
  @Test
  public void testIssuingCountryWales() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority[LACode='ANGL']/IssuingCountry";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("W", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return LanguageCode = `EW` for LACode=ANGL")
  @SneakyThrows
  @Test
  public void testLanguageCodeWales() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority[LACode='ANGL']/LanguageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("EW", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return ClockType = `WALLET` for LACode=ANGL")
  @SneakyThrows
  @Test
  public void testClockTypeWales() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority[LACode='ANGL']/ClockType";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("WALLET", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return IssuingCountry = `E` for LACode=LBKC")
  @SneakyThrows
  @Test
  public void testIssuingCountryEngland() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority[LACode='LBKC']/IssuingCountry";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("E", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return LanguageCode = `E` for LACode=LBKC")
  @SneakyThrows
  @Test
  public void testLanguageCodeEngland() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority[LACode='LBKC']/LanguageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("E", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return ClockType = `STANDARD` for LACode=LBKC")
  @SneakyThrows
  @Test
  public void testClockTypeEngland() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority[LACode='LBKC']/ClockType";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("STANDARD", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName(
      "Should return PrintedBadgeReference = `AA12BB 9 0377X0121` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  public void testBadgeIdentifierForPerson() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/PrintedBadgeReference";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("AA12BB 9 0377X0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return PrintedBadgeReference = `CC12DD 9 O0121` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  public void testBadgeIdentifierForOrganisation() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/PrintedBadgeReference";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("CC12DD 9 O0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return DispatchMethodCode = `M` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  public void testDispatchMethodCodeToHome() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/DispatchMethodCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("M", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return DispatchMethodCode = `C` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  public void testDispatchMethodCodeToCouncil() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/DispatchMethodCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("C", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return FastTrackCode = `N` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  public void testFastTrackCodeNo() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/FastTrackCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("N", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return FastTrackCode = `Y` for BadgeIdentifier=AA34BB")
  @SneakyThrows
  @Test
  public void testFastTrackCodeYes() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/FastTrackCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("Y", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return PostageCode = `SC` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  public void testPostageCodeSC() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/PostageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("SC", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return PostageCode = `SD1` for BadgeIdentifier=AA34BB")
  @SneakyThrows
  @Test
  public void testPostageCodeSD1() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/PostageCode";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("SD1", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return BarCodeData = `77X0121` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  public void testBarCodeDataForPerson() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/BarCodeData";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("77X0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should return BarCodeData = `O0121` for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  public void testBarCodeDataForOrganisation() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/BarCodeData";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

    assertEquals("O0121", node.getTextContent());
    deleteXmlFile(xmlPath);
  }

  @DisplayName(
      "Should return populate only Forename=`Jane Second` and leave Surname blank for BadgeIdentifier=AA34BB")
  @SneakyThrows
  @Test
  public void testShortName() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/Name/Forename";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("Jane Second", node.getTextContent());

    expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA34BB']/Name/Surname";
    node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("", node.getTextContent());

    deleteXmlFile(xmlPath);
  }

  @DisplayName(
      "Should return populate Forename=`Michelangelo` and Surname=`Lodovico Buonarroti Simoni` for BadgeIdentifier=AA12BB")
  @SneakyThrows
  @Test
  public void testLongName() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/Name/Forename";
    Node node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("Michelangelo", node.getTextContent());

    expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='AA12BB']/Name/Surname";
    node = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);
    assertEquals("Lodovico Buonarroti Simoni", node.getTextContent());

    deleteXmlFile(xmlPath);
  }

  @DisplayName("Should deliver to council address for BadgeIdentifier=CC12DD")
  @SneakyThrows
  @Test
  public void testDeliveryToCouncilAddress() {
    String xmlPath = saveXmlFile();

    DocumentBuilder builder = getDocumentBuilder();
    Document doc = builder.parse(xmlPath);

    XPathFactory xpathfactory = XPathFactory.newInstance();
    XPath xpath = xpathfactory.newXPath();

    String expression =
        "/BadgePrintExtract/Batch/LocalAuthorities/LocalAuthority/Badges/BadgeDetails[BadgeIdentifier='CC12DD']/LetterAddress";
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
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder;
  }

  private String saveXmlFile() throws IOException, XMLStreamException {
    String s3PictureFilePath =
        Paths.get("src", "test", "resources", "tmp", "printbatch_pics", "smile_small.jpg")
            .toString();
    Path xmlDir = Paths.get(System.getProperty("java.io.tmpdir"), "printbatch_xml");
    File picture = new File(s3PictureFilePath);
    when(s3.downloadFile(any(), eq(xmlDir.resolve("pictures")))).thenReturn(Optional.of(picture));
    when(referenceData.retrieveLocalAuthority("ANGL")).thenReturn(welshLocalAuthority());
    when(referenceData.retrieveLocalAuthority("LBKC")).thenReturn(englishLocalAuthority());

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
