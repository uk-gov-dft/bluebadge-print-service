package uk.gov.dft.bluebadge.service.printservice;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dft.bluebadge.model.printservice.generated.Batches;

@Service
@Slf4j
public class PrintService {
	
	public void print(Batches batch) {
		// store to s3
	}

}
