package ch.hearc.cafheg.infrastructure.persistance;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe abstraite permettant à chaque implémentation de Mapper
 * de recupérer la connection JDBC active.
 */
public class Mapper {
  protected Connection activeJDBCConnection() throws SQLException {
    return Database.activeJDBCConnection();
  }
}
