package uk.gov.dft.bluebadge.service.referencedata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.ReferenceDataApiClient;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.LocalAuthorityRefData;
import uk.gov.dft.bluebadge.service.printservice.client.referencedataservice.model.ReferenceData;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReferenceDataService {

  private final Set<String> validGroupKeys = new HashSet<>();
  private final ReferenceDataApiClient referenceDataApiClient;
  private Map<String, LocalAuthorityRefData> localAuthorityMap = new HashMap<>();
  private AtomicBoolean isLoaded = new AtomicBoolean(false);

  @Autowired
  public ReferenceDataService(ReferenceDataApiClient referenceDataApiClient) {
    this.referenceDataApiClient = referenceDataApiClient;
  }

  /**
   * Load the ref data first time required. Chose not to do PostConstruct so that can start service
   * if ref data service is still starting.
   */
  private void init() {
    if (!isLoaded.getAndSet(true)) {
      List<ReferenceData> referenceDataList = referenceDataApiClient.retrieveReferenceData();
      for (ReferenceData item : referenceDataList) {
        String key = item.getGroupShortCode() + "_" + item.getShortCode();
        validGroupKeys.add(key);

        if (item instanceof LocalAuthorityRefData) {
          localAuthorityMap.put(item.getShortCode(), (LocalAuthorityRefData) item);
        }
      }
      if (referenceDataList.isEmpty()) {
        isLoaded.set(false);
      }
    }
  }

  /**
   * Check a reference data group contains the key provided.
   *
   * @param group The group to check.
   * @param code The expected value.
   * @return true if present.
   */
  public boolean groupContainsKey(RefDataGroupEnum group, String code) {
    init();
    String key = group.getGroupKey() + "_" + code;
    return validGroupKeys.contains(key);
  }

  public LocalAuthorityRefData retrieveLocalAuthority(String localAuthorityShortCode) {
    init();
    return localAuthorityMap.get(localAuthorityShortCode);
  }
}
