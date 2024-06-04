package ch.hearc.cafheg.infrastructure.persistance;

import ch.hearc.cafheg.business.versements.Enfant;
import ch.hearc.cafheg.business.allocations.NoAVS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnfantMapper extends Mapper {

  private static final Logger logger = LoggerFactory.getLogger(EnfantMapper.class);

  private final String QUERY_FIND_ENFANT_BY_ID = "SELECT NO_AVS, NOM, PRENOM FROM ENFANTS WHERE NUMERO=?";

  public Enfant findById(long id) throws SQLException {
    logger.info("Recherche d'un enfant par son id {}", id);
    Connection connection = activeJDBCConnection();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_ENFANT_BY_ID);
      preparedStatement.setLong(1, id);
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        logger.debug("resultSet#next");
        return new Enfant(new NoAVS(resultSet.getString(1)),
            resultSet.getString(2), resultSet.getString(3));
      } else {
        logger.warn("Aucun enfant trouvé avec l'id {}", id);
        return null;
      }
    } catch (SQLException e) {
      logger.error("Erreur lors de la recherche d'un enfant par id {}", id, e);
      throw new RuntimeException(e);
    }
  }

}
