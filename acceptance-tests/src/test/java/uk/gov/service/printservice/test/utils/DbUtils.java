package uk.gov.service.printservice.test.utils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class DbUtils {
  private static final Logger log = LoggerFactory.getLogger(DbUtils.class);
  private final JdbcTemplate jdbc;

  public DbUtils(Map<String, String> config) {
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
    log.info("init jdbc template: {}", url);
  }

  public boolean runScript(String script) throws SQLException {
    log.info("Karate DB. Running script: {}", script);
    ScriptUtils.executeSqlScript(
        jdbc.getDataSource().getConnection(), new ClassPathResource(script));
    return true;
  }

  public Object readValue(String query) {
    return jdbc.queryForObject(query, Object.class);
  }

  public Map<String, Object> readRow(String query) {
    log.debug("Karate DB query: {}", query);
    Map<String, Object> stringObjectMap = jdbc.queryForMap(query);
    log.debug("Karate DB result: {}", stringObjectMap);
    return stringObjectMap;
  }

  public List<Map<String, Object>> readRows(String query) {
    return jdbc.queryForList(query);
  }
}
