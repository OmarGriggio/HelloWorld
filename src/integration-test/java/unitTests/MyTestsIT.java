package unitTests;

import ch.hearc.cafheg.business.allocations.AllocationService;
import ch.hearc.cafheg.infrastructure.persistance.AllocataireMapper;
import ch.hearc.cafheg.infrastructure.persistance.AllocationMapper;
import ch.hearc.cafheg.infrastructure.persistance.Database;
import ch.hearc.cafheg.infrastructure.persistance.Migrations;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class MyTestsIT {

    private static final Logger log = LoggerFactory.getLogger(MyTestsIT.class);

    private IDatabaseConnection connection;
    private IDataSet dataTest;
    private Database database;
    private ITable expectedTable;
    private ITable actualData;
    private AllocationService allocationService;

    @BeforeEach
    public void setup() {
        try {
            // Start the DB and run migrations
            database = new Database();
            Migrations migrations = new Migrations(database, true);
            database.start();
            migrations.start();

            // Populate the DB with data from XML file
            connection = new DatabaseConnection(Database.activeJDBCConnection());
            loadTestData("data.xml");

            allocationService = new AllocationService(new AllocataireMapper(), new AllocationMapper());
        } catch (DatabaseUnitException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTestData(String dataFile) {
        try {
            dataTest = DBUnitUtils.loadDataSet(dataFile);
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataTest);
        } catch (Exception e) {
            throw new RuntimeException("Error loading test data", e);
        }
    }

    @Test
    @DisplayName("Test Delete Allocataire by NO_AVS")
    public void deleteAllocataireByNoAVS_GivenValidAndInvalidIds_ShouldReflectInDatabase() throws DataSetException, SQLException, DatabaseUnitException {
        // Arrange
        String validAvs = "AVS001";
        String invalidAvs = "AVS002";

        // Act
        allocationService.deleteAllocataire(validAvs);
        allocationService.deleteAllocataire(invalidAvs);

        // Assert
        actualData = connection.createQueryTable("ALLOCATAIRES", "SELECT * FROM ALLOCATAIRES");
        dataTest = DBUnitUtils.loadDataSet("expectedDataAfterDeletion.xml");
        expectedTable = dataTest.getTable("ALLOCATAIRES");

        Assertion.assertEquals(expectedTable, actualData);
    }

    @Test
    @DisplayName("Test Update Allocataire's name and surname by NO_AVS")
    public void updateAllocataireNameByNoAVS_GivenValidAndInvalidIds_ShouldReflectInDatabase() throws DataSetException, SQLException, DatabaseUnitException {
        // Arrange
        String validAvs = "AVS001";
        String newName = "NewName";
        String newSurname = "NewSurname";
        String invalidAvs = "AVS002";
        String invalidName = "Smith";
        String invalidSurname = "Jane";

        // Act
        allocationService.updateAllocataire(validAvs, newName, newSurname);
        allocationService.updateAllocataire(invalidAvs, invalidName, invalidSurname);

        // Assert
        actualData = connection.createQueryTable("ALLOCATAIRES", "SELECT * FROM ALLOCATAIRES");
        dataTest = DBUnitUtils.loadDataSet("expectedDataAfterUpdate.xml");
        expectedTable = dataTest.getTable("ALLOCATAIRES");

        Assertion.assertEquals(expectedTable, actualData);
    }
}
