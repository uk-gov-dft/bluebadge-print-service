package uk.gov.dft.bluebadge.service.printservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uk.gov.dft.bluebadge.common.api.common.ServiceConfiguration;
import uk.gov.dft.bluebadge.common.security.TokenForwardingClientContext;

@Configuration
public class ApiConfig {

  @Validated
  @ConfigurationProperties("blue-badge.reference-data-service.servicehost")
  @Bean
  public ServiceConfiguration referenceDataServiceConfiguration() {
    return new ServiceConfiguration();
  }

  /**
   * OAuth rest template configured with a token forwarding context. So if a bearer token is found
   * on the security context, then it is used for the rest template.
   */
  @Bean("referenceDataServiceRestTemplate")
  RestTemplate referenceDataServiceRestTemplate(
      ClientCredentialsResourceDetails clientCredentialsResourceDetails,
      ServiceConfiguration referenceDataServiceConfiguration) {
    OAuth2RestTemplate result =
        new OAuth2RestTemplate(
            clientCredentialsResourceDetails, new TokenForwardingClientContext());
    HttpComponentsClientHttpRequestFactory requestFactory =
        new HttpComponentsClientHttpRequestFactory();
    result.setRequestFactory(requestFactory);
    result.setUriTemplateHandler(
        new DefaultUriBuilderFactory(referenceDataServiceConfiguration.getUrlPrefix()));
    return result;
  }
}
