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

  private static final Logger logger = LoggerFactory.getLogger(AllocataireMapper.class);

  private static final String QUERY_FIND_ALL = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES";
  private static final String QUERY_FIND_WHERE_NOM_LIKE = "SELECT NOM,PRENOM,NO_AVS FROM ALLOCATAIRES WHERE NOM LIKE ?";
  private static final String QUERY_FIND_WHERE_NUMERO = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NUMERO=?";
  private static final String QUERY_DELETE_BY_AVS = "DELETE FROM ALLOCATAIRES WHERE NO_AVS=?";
  private static final String QUERY_UPDATE = "UPDATE ALLOCATAIRES SET NOM=?, PRENOM=? WHERE NO_AVS=?";
  private static final String QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO = "SELECT * FROM VERSEMENTS WHERE NUMERO_ALLOCATAIRE=?";
  private static final String QUERY_FIND_BY_AVS = "SELECT NO_AVS, NOM, PRENOM FROM ALLOCATAIRES WHERE NO_AVS=?";

  public List<Allocataire> findAll(String likeNom) {
    logger.info("findAll() {}", likeNom);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement;
      if (likeNom == null) {
        logger.debug("SQL: {}", QUERY_FIND_ALL);
        preparedStatement = connection.prepareStatement(QUERY_FIND_ALL);
      } else {
        logger.debug("SQL: {}", QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NOM_LIKE);
        preparedStatement.setString(1, likeNom + "%");
      }
      logger.debug("Allocation d'un nouveau tableau");
      List<Allocataire> allocataires = new ArrayList<>();

      logger.debug("Exécution de la requête");
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        logger.debug("Allocataire mapping");
        while (resultSet.next()) {
          logger.trace("ResultSet#next");
          allocataires
              .add(new Allocataire(new NoAVS(resultSet.getString(3)), resultSet.getString(2), resultSet.getString(1)));
        }
      }
      logger.info("Allocataires trouvés: {}", allocataires.size());
      return allocataires;
    } catch (SQLException e) {
      logger.error("Erreur lors de la recherche des allocataires", e);
      throw new RuntimeException(e);
    }
  }

  public Allocataire findById(long id) {
    logger.info("findById() {}", id);
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL: {}", QUERY_FIND_WHERE_NUMERO);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_WHERE_NUMERO);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      logger.trace("ResultSet#next");
      resultSet.next();
      logger.debug("Allocataire mapping");
      return new Allocataire(new NoAVS(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3));
    } catch (SQLException e) {
      logger.error("Erreur lors de la recherche de l'allocataire par ID", e);
      throw new RuntimeException(e);
    }
  }

  public Allocataire findByAvsNumber(String avsNumber) {
    logger.info("findByAvsNumber() {}", avsNumber);
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL: {}", QUERY_FIND_BY_AVS);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_BY_AVS);
      preparedStatement.setString(1, avsNumber);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        logger.debug("Allocataire mapping");
        return new Allocataire(new NoAVS(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3));
      }
      return null;
    } catch (SQLException e) {
      logger.error("Erreur lors de la recherche de l'allocataire par numéro AVS", e);
      throw new RuntimeException(e);
    }
  }

  private boolean hasPayments(String avsNumber) {
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL: {}", QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO);
      PreparedStatement checkVersementStatement = connection.prepareStatement(QUERY_SELECT_ALL_VERSEMENTS_WHERE_NUMERO);
      checkVersementStatement.setString(1, avsNumber);
      ResultSet resultSetVersement = checkVersementStatement.executeQuery();
      return resultSetVersement.next();
    } catch (SQLException e) {
      logger.error("Erreur lors de la vérification des versements pour le numéro AVS {}", avsNumber, e);
      throw new RuntimeException(e);
    }
  }

  public String deleteByAvsNumber(String avsNumber) {
    logger.info("deleteByAvsNumber() {}", avsNumber);
    if (hasPayments(avsNumber)) {
      return "Allocataire with AVS number " + avsNumber + " has linked payments, can't delete.";
    }
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL: {}", QUERY_DELETE_BY_AVS);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_DELETE_BY_AVS);
      preparedStatement.setString(1, avsNumber);
      int rowsAffected = preparedStatement.executeUpdate();
      if (rowsAffected > 0) {
        return "Allocataire with AVS number " + avsNumber + " deleted successfully.";
      } else {
        return "Allocataire with AVS number " + avsNumber + " not found.";
      }
    } catch (SQLException e) {
      logger.error("Erreur lors de la suppression de l'allocataire avec le numéro AVS {}", avsNumber, e);
      throw new RuntimeException(e);
    }
  }

  public String update(Allocataire allocataire) {
    logger.info("update() {}", allocataire.getNoAVS());
    Allocataire existingAllocataire = findByAvsNumber(allocataire.getNoAVS().toString());
    if (existingAllocataire == null) {
      return "Allocataire with AVS number " + allocataire.getNoAVS() + " not found.";
    }
    if (existingAllocataire.getNom().equals(allocataire.getNom())
        && existingAllocataire.getPrenom().equals(allocataire.getPrenom())) {
      return "No changes detected for Allocataire with AVS number " + allocataire.getNoAVS() + ". Update not needed.";
    }
    Connection connection = activeJDBCConnection();
    try {
      logger.debug("SQL: {}", QUERY_UPDATE);
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_UPDATE);
      preparedStatement.setString(1, allocataire.getNom());
      preparedStatement.setString(2, allocataire.getPrenom());
      preparedStatement.setString(3, allocataire.getNoAVS().toString());
      preparedStatement.executeUpdate();
      return "Allocataire with AVS number " + allocataire.getNoAVS() + " updated successfully.";
    } catch (SQLException e) {
      logger.error("Erreur lors de la mise à jour de l'allocataire avec le numéro AVS {}", allocataire.getNoAVS(), e);
      throw new RuntimeException(e);
    }
  }
}
