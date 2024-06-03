package ch.hearc.cafheg.infrastructure.persistance;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class Database {
  private static DataSource dataSource;
  static Logger logger = LoggerFactory.getLogger(Database.class);
  private static final ThreadLocal<Connection> connection = new ThreadLocal<>();

  public static Connection activeJDBCConnection() {
    Connection conn = connection.get();
    if (conn == null) {
      RuntimeException e = new RuntimeException("No active JDBC connection");
      logger.error("No active JDBC connection", e);
      throw e;
    }
    try {
      if (conn.isClosed()) {
        logger.error("Connection is closed");
        throw new RuntimeException("Connection is closed");
      }
    } catch (SQLException e) {
      logger.error("Error checking connection status", e);
      throw new RuntimeException(e);
    }
    return conn;
  }

  public static <T> T inTransaction(Supplier<T> inTransaction) {
    logger.debug("Transaction start");
    try {
      if (connection.get() == null || connection.get().isClosed()) {
        logger.debug("Transaction: getConnection");
        connection.set(dataSource.getConnection());
      }
      return inTransaction.get();
    } catch (Exception e) {
      logger.error("Transaction error when getting connection", e);
      throw new RuntimeException(e);
    } finally {
      logger.debug("Transaction end");
    }
  }

  public void start() {
    logger.info("Initializing datasource");
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:h2:mem:sample");
    config.setMaximumPoolSize(20);
    config.setDriverClassName("org.h2.Driver");
    dataSource = new HikariDataSource(config);
    try {
      connection.set(dataSource.getConnection());
    } catch (SQLException e) {
      logger.error("Error getting connection", e);
      throw new RuntimeException(e);
    }
    logger.info("Datasource initialized");
  }

  public void stop() {
    try {
      if (connection.get() != null && !connection.get().isClosed()) {
        connection.get().close();
        connection.remove();
      }
    } catch (SQLException e) {
      logger.error("Error stopping the database", e);
    }
  }

  DataSource dataSource() {
    return dataSource;
  }
}
