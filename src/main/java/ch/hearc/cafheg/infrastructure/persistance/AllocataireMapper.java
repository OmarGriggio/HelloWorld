package ch.hearc.cafheg.infrastructure.persistance;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.NoAVS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocataireMapper extends Mapper {

  private static final Logger log = LoggerFactory.getLogger(AllocataireMapper.class);

  private static final String QUERY_FIND_ALL = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_DELETE_BY_AVS = "DELETE FROM ALLOCATAIRES WHERE NO_AVS=?";
  private static final String QUERY_UPDATE = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?";
  private static final String QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO = "SELECT * FROM VERSEMENTS WHERE FK_ALLOCATAIRES=?";
  private static final String QUERY_FIND_BY_AVS = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NO_AVS=?";

  public List<Allocataire> findAll(String likeNom) throws SQLException {
    System.out.println("findAll() " + likeNom);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        System.out.println("SQL: " + QUERY_FIND_ALL);
        preparedStatement = connection
                .prepareStatement(QUERY_FIND_ALL);
      } else {

        System.out.println("SQL: " + QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement = connection
                .prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      System.out.println("Allocation d'un nouveau tableau");
      List<Allocataire> allocataires = new ArrayList<>();

      System.out.println("Exécution de la requête");
      try (ResultSet resultSet = preparedStatement.executeQuery()) {

        System.out.println("Allocataire mapping");
        while (resultSet.next()) {
          System.out.println("ResultSet#next");
          allocataires
                  .add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2),
                          resultSet.getString(1)));
        }
      }
      System.out.println("Allocataires trouvés " + allocataires.size());
      return allocataires;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Allocataire findById(long id) throws SQLException {
    System.out.println("findById() " + id);
    Connection connection = activeJDBCConnection();
    try {
      System.out.println("SQL:" + QUERY_FIND_WHERE_NUMERO);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      System.out.println("ResultSet#next");
      resultSet.next();
      System.out.println("Allocataire mapping");
      return new Allocataire(new NoAVS(resultSet.getString(1)),
              resultSet.getString(2), resultSet.getString(3));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Allocataire findByAvsNumber(String avsNumber) {
    log.debug("Finding allocataire by AVS number: {}", avsNumber);
    try {
      Connection connection = activeJDBCConnection();
      try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_BY_AVS)) {

        preparedStatement.setString(1, avsNumber);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          if (resultSet.next()) {
            log.debug("Allocataire found: {}", avsNumber);
            return new Allocataire(
                    new NoAVS(resultSet.getString(1)),
                    resultSet.getString(2),
                    resultSet.getString(3)
            );
          } else {
            log.debug("Allocataire not found: {}", avsNumber);
            return null;
          }
        }
      }
    } catch (SQLException e) {
      log.error("Error finding allocataire by AVS number", e);
      throw new RuntimeException("Error finding allocataire by AVS number", e);
    } catch (RuntimeException e) {
      log.error("Connection is closed", e);
      // Handle the closed connection error here
      // You can throw a new exception, return a default value, or take any other appropriate action
      throw new RuntimeException("Connection is closed", e);
    }
  }

  public boolean hasPayments(String avsNumber) {
    log.debug("Checking payments for AVS number: {}", avsNumber);
    try (Connection connection = activeJDBCConnection()) {
      if (connection == null || connection.isClosed()) {
        log.error("Connection is closed or null before query execution");
        throw new SQLException("Connection is closed or null");
      }
      try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO)) {
        preparedStatement.setString(1, avsNumber);
        log.debug("Executing query: {}", preparedStatement);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
          boolean hasPayments = resultSet.next();
          log.debug("Has payments: {}", hasPayments);
          return hasPayments;
        }
      }
    } catch (SQLException e) {
      log.error("Error checking for payments", e);
      throw new RuntimeException("Error checking for payments", e);
    }
  }


  public void deleteByAvsNumber(String avsNumber) {
    if (hasPayments(avsNumber)) {
      throw new IllegalArgumentException("Allocataire with AVS number " + avsNumber + " has linked payments, can't delete.");
    }
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DELETE_BY_AVS)) {

      preparedStatement.setString(1, avsNumber);
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected == 0) {
        throw new IllegalArgumentException("Allocataire with AVS number " + avsNumber + " not found.");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting allocataire by AVS number", e);
    }
  }

  public String update(Allocataire allocataire) {
    Allocataire existingAllocataire = findByAvsNumber(allocataire.getNoAVS().toString());
    if (existingAllocataire == null) {
      return "Allocataire with AVS number " + allocataire.getNoAVS() + " not found.";
    }

    boolean isNomChanged = !existingAllocataire.getNom().equals(allocataire.getNom());
    boolean isPrenomChanged = !existingAllocataire.getPrenom().equals(allocataire.getPrenom());

    if (!isNomChanged && !isPrenomChanged) {
      return "No changes detected for Allocataire with AVS number " + allocataire.getNoAVS() + ". Update not needed.";
    }

    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE)) {

      preparedStatement.setString(1, allocataire.getNom());
      preparedStatement.setString(2, allocataire.getPrenom());
      preparedStatement.setString(3, allocataire.getNoAVS().toString());

      log.debug("Executing update statement: {}", preparedStatement);

      int rowsAffected = preparedStatement.executeUpdate();
      connection.commit(); // Ensure commit if autocommit is false

      if (rowsAffected == 0) {
        return "No rows updated for Allocataire with AVS number " + allocataire.getNoAVS() + ". Update failed.";
      }
      return "Allocataire with AVS number " + allocataire.getNoAVS() + " updated successfully.";
    } catch (SQLException e) {
      log.error("Error updating allocataire", e);
      throw new RuntimeException("Error updating allocataire", e);
    }
  }
}
