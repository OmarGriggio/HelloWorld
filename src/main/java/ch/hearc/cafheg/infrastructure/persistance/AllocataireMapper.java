package ch.hearc.cafheg.infrastructure.persistance;

import ch.hearc.cafheg.business.allocations.Allocataire;
import ch.hearc.cafheg.business.allocations.NoAVS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllocataireMapper extends Mapper {

  private static final String QUERY_FIND_ALL = "SELECT NOM, PRENOM, NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM, PRENOM, NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_DELETE_BY_AVS = "DELETE FROM ALLOCATAIRES WHERE NO_AVS=?";
  private static final String QUERY_UPDATE = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?";
  private static final String QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO = "SELECT * FROM VERSEMENTS WHERE NUMERO_ALLOCATAIRE=?";
  private static final String QUERY_FIND_BY_AVS = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NO_AVS=?";

  public List<Allocataire> findAll(String likeNom) {
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = prepareFindAllStatement(connection, likeNom);
         ResultSet resultSet = preparedStatement.executeQuery()) {

      List<Allocataire> allocataires = new ArrayList<>();
      while (resultSet.next()) {
        allocataires.add(mapAllocataire(resultSet));
      }
      return allocataires;
    } catch (SQLException e) {
      throw new RuntimeException("Error finding all allocataires", e);
    }
  }

  private PreparedStatement prepareFindAllStatement(Connection connection, String likeNom) throws SQLException {
    if (likeNom == null) {
      return connection.prepareStatement(QUERY_FIND_ALL);
    } else {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
      preparedStatement.setString(1, likeNom + "%");
      return preparedStatement;
    }
  }

  public Allocataire findById(long id) {
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO)) {

      preparedStatement.setLong(1, id);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return mapAllocataire(resultSet);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error finding allocataire by ID", e);
    }
  }

  public Allocataire findByAvsNumber(String avsNumber) {
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_BY_AVS)) {

      preparedStatement.setString(1, avsNumber);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          return mapAllocataire(resultSet);
        } else {
          return null;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error finding allocataire by AVS number", e);
    }
  }

  public boolean hasPayments(String avsNumber) {
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO)) {

      preparedStatement.setString(1, avsNumber);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        return resultSet.next();
      }
    } catch (SQLException e) {
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
    if (existingAllocataire.getNom().equals(allocataire.getNom()) && existingAllocataire.getPrenom().equals(allocataire.getPrenom())) {
      return "No changes detected for Allocataire with AVS number " + allocataire.getNoAVS() + ". Update not needed.";
    }
    try (Connection connection = activeJDBCConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE)) {

      preparedStatement.setString(1, allocataire.getNom());
      preparedStatement.setString(2, allocataire.getPrenom());
      preparedStatement.setString(3, allocataire.getNoAVS().toString());
      preparedStatement.executeUpdate();
      return "Allocataire with AVS number " + allocataire.getNoAVS() + " updated successfully.";
    } catch (SQLException e) {
      throw new RuntimeException("Error updating allocataire", e);
    }
  }

  private Allocataire mapAllocataire(ResultSet resultSet) throws SQLException {
    return new Allocataire(new NoAVS(resultSet.getString("NO_AVS")), resultSet.getString("NOM"), resultSet.getString("PRENOM"));
  }
}
