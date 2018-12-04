package uk.gov.dft.bluebadge.service.printservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/** Batch */
@Validated
public class Batch {
  @JsonProperty("filename")
  private String filename = null;

  @JsonProperty("batchType")
  private String batchType = null;

  @JsonProperty("Badges")
  @Valid
  private List<Badge> badges = null;

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

  public Batch badges(List<Badge> badges) {
    this.badges = badges;
    return this;
  }

  public Batch addBadgesItem(Badge badgesItem) {
    if (this.badges == null) {
      this.badges = new ArrayList<>();
    }
    this.badges.add(badgesItem);
    return this;
  }

  /**
   * Get badges
   *
   * @return badges
   */
  @ApiModelProperty(value = "")
  @Valid
  public List<Badge> getBadges() {
    return badges;
  }

  public void setBadges(List<Badge> badges) {
    this.badges = badges;
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
        && Objects.equals(this.badges, batch.badges);
  }

  @Override
  public int hashCode() {
    return Objects.hash(filename, batchType, badges);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Batch {\n");

    sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
    sb.append("    batchType: ").append(toIndentedString(batchType)).append("\n");
    sb.append("    badges: ").append(toIndentedString(badges)).append("\n");
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
