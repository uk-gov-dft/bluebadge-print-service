package uk.gov.service.printservice.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

public class XMLValidator {

  public boolean validate(String xmlFile, String schemaFile) {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      Schema schema = schemaFactory.newSchema(new File(getResource(schemaFile)));

      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(new File(getResource(xmlFile))));
      return true;
    } catch (SAXException | IOException e) {
      System.err.println(
          "--------------------------------------- error ---------------------------------------");
      System.err.println(e.getMessage());
      System.err.println(
          "-------------------------------------------------------------------------------------");
      return false;
    }
  }

  private String getResource(String filename) throws FileNotFoundException {
    URL resource = getClass().getClassLoader().getResource(filename);
    Objects.requireNonNull(resource);
    return resource.getFile();
  }
}
