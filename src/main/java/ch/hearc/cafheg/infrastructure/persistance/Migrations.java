package ch.hearc.cafheg.infrastructure.persistance;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Migrations {

  private static final Logger logger = LoggerFactory.getLogger(Migrations.class);

  private final Database database;
  private final boolean isTestEnv;

  public Migrations(Database database, boolean isTestEnv) {
    this.database = database;
    this.isTestEnv = isTestEnv;
  }

  public void start() {
    logger.debug("Doing migrations");
    String location = "classpath:db/ddl";

    Flyway flyway = Flyway.configure()
            .dataSource(database.dataSource())
            .locations(location)
            .load();

    flyway.migrate();
    logger.info("Migrations done");

    if (isTestEnv) {
      logger.debug("Test environment detected, DDL migrations done");
    }
  }

  public void stop() {
    // No need to close the connection here
  }
}