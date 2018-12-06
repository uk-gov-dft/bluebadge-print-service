package uk.gov.dft.bluebadge.service.printservice.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static uk.gov.dft.bluebadge.service.printservice.config.ActuatorSecurityConfig.*;


@Configuration
@Order(BEFORE_RESOURCE_SERVER_ORDER)
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {
  /** Order before the resource server (which is 3) */
  public static final int BEFORE_RESOURCE_SERVER_ORDER = 2;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.requestMatcher(EndpointRequest.toAnyEndpoint())
        .authorizeRequests()
        .anyRequest()
        .permitAll();
  }
}
