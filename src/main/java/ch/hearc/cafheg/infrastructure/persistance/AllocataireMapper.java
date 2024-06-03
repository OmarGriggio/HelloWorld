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

  private static final String QUERY_FIND_ALL = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_DELETE_BY_AVS = "DELETE FROM ALLOCATAIRES WHERE NO_AVS=?";
  private static final String QUERY_UPDATE = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?";
  private static final String QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO = "SELECT * FROM VERSEMENTS WHERE NUMERO_ALLOCATAIRE=?";
  private static final String QUERY_FIND_BY_AVS = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NO_AVS=?";

  public List<Allocataire> findAll(String likeNom) {
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

  public Allocataire findById(long id) {
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
    System.out.println("findByAvsNumber() " + avsNumber);
    Connection connection = activeJDBCConnection();
    try {
      System.out.println("SQL:" + QUERY_FIND_BY_AVS);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_BY_AVS);
      preparedStatement.setString(1, avsNumber);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        System.out.println("Allocataire mapping");
        return new Allocataire(new NoAVS(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3));
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean hasPayments(String avsNumber) {
    Connection connection = activeJDBCConnection();
    try {
      System.out.println("SQL: " + QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO);
      PreparedStatement checkVersementStatement = connection.prepareStatement(QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO);
      checkVersementStatement.setString(1, avsNumber);
      ResultSet resultSetVersement = checkVersementStatement.executeQuery();
      return resultSetVersement.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteByAvsNumber(String avsNumber) {
    System.out.println("deleteByAvsNumber() " + avsNumber);
    if (hasPayments(avsNumber)) {
      throw new IllegalArgumentException("Allocataire with AVS number " + avsNumber + " has linked payments, can't delete.");
    }
    Connection connection = activeJDBCConnection();
    try {
      System.out.println("SQL:" + QUERY_DELETE_BY_AVS);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DELETE_BY_AVS);
      preparedStatement.setString(1, avsNumber);
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected == 0) {
        throw new IllegalArgumentException("Allocataire with AVS number " + avsNumber + " not found.");
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public String update(Allocataire allocataire) {
    System.out.println("update() " + allocataire.getNoAVS());
    Allocataire existingAllocataire = findByAvsNumber(allocataire.getNoAVS().toString());
    if (existingAllocataire == null) {
      return "Allocataire with AVS number " + allocataire.getNoAVS() + " not found.";
    }
    if (existingAllocataire.getNom().equals(allocataire.getNom()) && existingAllocataire.getPrenom().equals(allocataire.getPrenom())) {
      return "No changes detected for Allocataire with AVS number " + allocataire.getNoAVS() + ". Update not needed.";
    }
    Connection connection = activeJDBCConnection();
    try {
      System.out.println("SQL:" + QUERY_UPDATE);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE);
      preparedStatement.setString(1, allocataire.getNom());
      preparedStatement.setString(2, allocataire.getPrenom());
      preparedStatement.setString(3, allocataire.getNoAVS().toString());
      preparedStatement.executeUpdate();
      return "Allocataire with AVS number " + allocataire.getNoAVS() + " updated successfully.";
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
