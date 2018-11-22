package uk.gov.dft.bluebadge.service.printservice;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.dft.bluebadge.service.printservice.config.SecurityConfig;

@SpringBootTest(
  classes = PrintServiceApplication.class,
  properties = {"management.server.port=19991"}
)
@RunWith(SpringRunner.class)
@ActiveProfiles({"test", "dev"})
public class PrintServiceApplicationTests {

  @Autowired 
  private SecurityConfig securityConfig;

  @Test
  public void loadContext() {}

  @Test
  public void shouldInstantiateSecurityConfig() {
    assertThat(securityConfig, Matchers.notNullValue());
  }
}
