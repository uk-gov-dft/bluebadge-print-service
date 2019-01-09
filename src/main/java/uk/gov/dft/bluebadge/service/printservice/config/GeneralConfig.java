package uk.gov.dft.bluebadge.service.printservice.config;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class GeneralConfig {

  @Value("${general.organisation.photo.resourceuri.england}")
  @NotNull
  private String organisationPhotoUriEngland;

  @Value("${general.organisation.photo.resourceuri.wales}")
  @NotNull
  private String organisationPhotoUriWales;
}
