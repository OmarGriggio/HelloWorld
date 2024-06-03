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
  private static final String QUERY_DELETE_BY_ID = "DELETE FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_UPDATE = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?";
  private static final String QUERY_FIND_BY_AVS = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NO_AVS=?";
  private static final String QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO = "SELECT * FROM VERSEMENTS WHERE NUMERO_ALLOCATAIRE=?";

  public List<Allocataire> findAll(String likeNom) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        preparedStatement = connection.prepareStatement(QUERY_FIND_ALL);
      } else {
        preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      List<Allocataire> allocataires = new ArrayList<>();
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          allocataires.add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2), resultSet.getString(1)));
        }
      }
      return allocataires;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Allocataire findById(long id) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return new Allocataire(new NoAVS(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3));
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Allocataire findByAvsNumber(String avsNumber) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_BY_AVS);
      preparedStatement.setString(1, avsNumber);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return new Allocataire(new NoAVS(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3));
      }
      return null;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void deleteById(long id) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DELETE_BY_ID);
      preparedStatement.setLong(1, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(Allocataire allocataire) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE);
      preparedStatement.setString(1, allocataire.getNom());
      preparedStatement.setString(2, allocataire.getPrenom());
      preparedStatement.setString(3, allocataire.getNoAVS().toString());
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean hasPayments(String avsNumber) {
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement checkVersementStatement = connection.prepareStatement(QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO);
      checkVersementStatement.setString(1, avsNumber);
      ResultSet resultSetVersement = checkVersementStatement.executeQuery();
      return resultSetVersement.next();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
