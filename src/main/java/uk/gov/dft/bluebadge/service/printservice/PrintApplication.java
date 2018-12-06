package uk.gov.dft.bluebadge.service.printservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = "uk.gov.dft.bluebadge")
@EnableSwagger2
public class PrintApplication {

  public static void main(String[] args) {
    SpringApplication.run(PrintApplication.class, args);
  }
}
