package uk.gov.dft.bluebadge.service.printservice.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Builder
@EqualsAndHashCode
public class ProcessedBatch {

  public enum FileTypeEnum {
    CONFIRMATION,
    REJECTION
  }

  private String filename;
  private String errorMessage;
  private FileTypeEnum fileType;
  private List<ProcessedBadge> processedBadges;
}
