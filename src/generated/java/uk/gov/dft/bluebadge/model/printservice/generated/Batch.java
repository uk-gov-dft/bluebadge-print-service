package uk.gov.dft.bluebadge.model.printservice.generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

/** Batch */
@Validated
public class Batch {
  @JsonProperty("filename")
  private String filename = null;

  @JsonProperty("batchType")
  private String batchType = null;

  @JsonProperty("localAuthorities")
  @Valid
  private List<LocalAuthority> localAuthorities = null;

  public Batch filename(String filename) {
    this.filename = filename;
    return this;
  }

  /**
   * Get filename
   *
   * @return filename
   */
  @ApiModelProperty(value = "")
  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Batch batchType(String batchType) {
    this.batchType = batchType;
    return this;
  }

  /**
   * Get batchType
   *
   * @return batchType
   */
  @ApiModelProperty(example = "FASTTRACK, STANDARD or LA", value = "")
  public String getBatchType() {
    return batchType;
  }

  public void setBatchType(String batchType) {
    this.batchType = batchType;
  }

  public Batch localAuthorities(List<LocalAuthority> localAuthorities) {
    this.localAuthorities = localAuthorities;
    return this;
  }

  public Batch addLocalAuthoritiesItem(LocalAuthority localAuthoritiesItem) {
    if (this.localAuthorities == null) {
      this.localAuthorities = new ArrayList<>();
    }
    this.localAuthorities.add(localAuthoritiesItem);
    return this;
  }

  /**
   * Get localAuthorities
   *
   * @return localAuthorities
   */
  @ApiModelProperty(value = "")
  @Valid
  public List<LocalAuthority> getLocalAuthorities() {
    return localAuthorities;
  }

  public void setLocalAuthorities(List<LocalAuthority> localAuthorities) {
    this.localAuthorities = localAuthorities;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Batch batch = (Batch) o;
    return Objects.equals(this.filename, batch.filename)
        && Objects.equals(this.batchType, batch.batchType)
        && Objects.equals(this.localAuthorities, batch.localAuthorities);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename, batchType, localAuthorities);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Batch {\n");

    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    batchType: ").append(toIndentedString(batchType)).append("\n");
    sb.append("    localAuthorities: ").append(toIndentedString(localAuthorities)).append("\n");
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
