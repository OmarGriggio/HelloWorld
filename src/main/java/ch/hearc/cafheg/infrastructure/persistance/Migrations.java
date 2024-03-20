package ch.hearc.cafheg.infrastructure.persistance;

import org.flywaydb.core.Flyway;

/**
 * Gestion des scripts de migration sur la base de données.
 */
public class Migrations {

  private final Database database;

  public Migrations(Database database) {
    this.database = database;
  }

  /**
   * Exécution des migrations
   */
  public void start(String location) {
    System.out.println("Doing migrations");

    Flyway flyway = Flyway.configure()
            .dataSource(database.dataSource())
            .locations(location)
            .load();

    flyway.migrate();
    System.out.println("Migrations done");
  }
}
