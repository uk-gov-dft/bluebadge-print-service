package uk.gov.dft.bluebadge.service.printservice.model;

import java.util.ArrayList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

/** Batches */
@Validated
@EqualsAndHashCode
@ToString
@Data
public class Batches extends ArrayList<Batch> {}
