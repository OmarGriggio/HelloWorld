package ch.hearc.cafheg.infrastructure.persistance;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestion des scripts de migration sur la base de données.
 */
public class Migrations {

  private static final Logger logger = LoggerFactory.getLogger(Migrations.class);

  private final Database database;

  public Migrations(Database database) {
    this.database = database;
  }

  /**
   * Exécution des migrations
   */
  public void start(String location) {
    logger.info("Starting migrations from location: {}", location);

    Flyway flyway = Flyway.configure()
        .dataSource(database.dataSource())
        .locations(location)
        .load();

    flyway.migrate();
    logger.info("Migrations completed successfully");
  }
}
