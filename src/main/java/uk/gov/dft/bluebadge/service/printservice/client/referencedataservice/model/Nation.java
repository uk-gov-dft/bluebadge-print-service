package uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.StringUtils;

public enum Nation {
  ENG("E"),
  WLS("W"),
  SCO("S"),
  NIR("N");

	private String code;
	
	Nation(String code) {
		this.code = code;
	}
	
  public String getCode() {
		return code;
	}

	@SuppressWarnings("unused")
  @JsonCreator
  public static Nation forValue(String value) {
    // Expect a valid nation if a value given
    if (StringUtils.isNotEmpty(value)) {
      return Nation.valueOf(value);
    }
    return null;
  }
}
