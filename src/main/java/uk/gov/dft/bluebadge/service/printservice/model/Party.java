package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

/** Party */
@Validated
public class Party {
  @JsonProperty("typeCode")
  private String typeCode = null;

  @JsonProperty("contact")
  private Contact contact = null;

  @JsonProperty("person")
  private Person person = null;

  @JsonProperty("organisation")
  private Organisation organisation = null;

  public Party typeCode(String typeCode) {
    this.typeCode = typeCode;
    return this;
  }

  /**
   * A short code from the PARTY group of reference data. e.g. PERSON or ORG.
   *
   * @return typeCode
   */
  @ApiModelProperty(
    example = "PERSON",
    required = true,
    value = "A short code from the PARTY group of reference data. e.g. PERSON or ORG."
  )
  @NotNull
  @Size(max = 10)
  public String getTypeCode() {
    return typeCode;
  }

  public void setTypeCode(String typeCode) {
    this.typeCode = typeCode;
  }

  public Party contact(Contact contact) {
    this.contact = contact;
    return this;
  }

  /**
   * Get contact
   *
   * @return contact
   */
  @ApiModelProperty(required = true, value = "")
  @NotNull
  @Valid
  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
  }

  public Party person(Person person) {
    this.person = person;
    return this;
  }

  /**
   * Get person
   *
   * @return person
   */
  @ApiModelProperty(value = "")
  @Valid
  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Party organisation(Organisation organisation) {
    this.organisation = organisation;
    return this;
  }

  /**
   * Get organisation
   *
   * @return organisation
   */
  @ApiModelProperty(value = "")
  @Valid
  public Organisation getOrganisation() {
    return organisation;
  }

  public void setOrganisation(Organisation organisation) {
    this.organisation = organisation;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Party party = (Party) o;
    return Objects.equals(this.typeCode, party.typeCode)
        && Objects.equals(this.contact, party.contact)
        && Objects.equals(this.person, party.person)
        && Objects.equals(this.organisation, party.organisation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeCode, contact, person, organisation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Party {\n");

    sb.append("    typeCode: ").append(toIndentedString(typeCode)).append("\n");
    sb.append("    contact: ").append(toIndentedString(contact)).append("\n");
    sb.append("    person: ").append(toIndentedString(person)).append("\n");
    sb.append("    organisation: ").append(toIndentedString(organisation)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
