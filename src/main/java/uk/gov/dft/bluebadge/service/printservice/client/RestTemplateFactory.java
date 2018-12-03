package uk.gov.dft.bluebadge.service.printservice.client;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("unused")
@Component
public class RestTemplateFactory {

  private RestTemplate restTemplate;

  public RestTemplateFactory() {
    this.restTemplate = new RestTemplate();
    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();
    CloseableHttpClient httpClient =
        HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
    requestFactory.setHttpClient(httpClient);
    this.restTemplate.setRequestFactory(requestFactory);
  }

  public RestTemplate getInstance() {
    return restTemplate;
  }
}
