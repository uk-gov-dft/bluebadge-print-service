package uk.gov.service.bluebadge.test.utils;

import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineUtils {
  private static final Logger log = LoggerFactory.getLogger(CommandLineUtils.class);

  public CommandLineUtils(Map<String, String> config) {

    /*
    String url = config.get("url");
    String username = config.get("username");
    String  ***REMOVED***);
    String driver = config.get("driverClassName");
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driver);
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    jdbc = new JdbcTemplate(dataSource);
    log.info("init jdbc template: {}", url);*/
  }

  public boolean runScript(String script) throws IOException, InterruptedException {
    //log.info("Karate Command Line Utils. Running script: {}", script);
    /*
        //ProcessBuilder pb = new ProcessBuilder("myshellScript.sh", "myArg1", "myArg2");
        ProcessBuilder pb = new ProcessBuilder(script);

        //    pb.directory(new File("myDir"));
        Process p = pb.start();
        p.wait(5000);
        System.out.println("-------exitValue=" + p.exitValue());
        System.out.println("-------outputStream=" + p.getOutputStream());
        System.out.println("-------errorStream=" + p.getErrorStream());
    */
    return true;
  }
}
