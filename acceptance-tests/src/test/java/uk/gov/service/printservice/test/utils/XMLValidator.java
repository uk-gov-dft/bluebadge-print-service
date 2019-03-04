package uk.gov.service.printservice.test.utils;

import java.io.File;
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
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(XMLValidator.class);

  @SuppressWarnings("unused")
  public boolean validate(String xmlFile, String schemaFile) {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      Schema schema = schemaFactory.newSchema(new File(getResource(schemaFile)));

      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(new File(getResource(xmlFile))));
      return true;
    } catch (SAXException | IOException ex) {
      File directory = new File("./");
      log.error(
          "Error validating file [{}] with schema [{}] on directory [{}], exception message: [{}], exception: [{}]",
          xmlFile,
          schemaFile,
          directory.getAbsolutePath(),
          ex.getMessage(),
          ex);
      return false;
    }
  }

  private String getResource(String filename) {
    URL resource = getClass().getClassLoader().getResource(filename);
    Objects.requireNonNull(resource);
    return resource.getFile();
  }
}
