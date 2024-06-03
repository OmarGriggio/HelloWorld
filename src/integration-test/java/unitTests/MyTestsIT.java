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

import java.sql.Connection;
import java.sql.SQLException;

public class MyTestsIT {

    private static final Logger log = LoggerFactory.getLogger(MyTestsIT.class);

    private IDatabaseConnection dbUnitConnection;
    private Database database;
    private AllocationService allocationService;

    @BeforeEach
    public void setup() {
        try {
            // Start the DB and run migrations
            database = new Database();
            database.start();
            Migrations migrations = new Migrations(database, true);
            migrations.start();

            // Initialize DBUnit connection
            Connection jdbcConnection = Database.activeJDBCConnection();
            dbUnitConnection = new DatabaseConnection(jdbcConnection);

            // Load test data
            loadTestData("data.xml");

            // Initialize services
            allocationService = new AllocationService(new AllocataireMapper(), new AllocationMapper());
        } catch (DatabaseUnitException  e) {
            log.error("Error setting up the database connection and loading test data", e);
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        database.stop();
    }

    private void loadTestData(String dataFile) {
        try {
            IDataSet dataSet = DBUnitUtils.loadDataSet(dataFile);
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
        } catch (Exception e) {
            log.error("Error loading test data from file: {}", dataFile, e);
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
        try {
            allocationService.deleteAllocataire(invalidAvs);
        } catch (IllegalArgumentException e) {
            log.info("Expected exception for invalid AVS: {}", invalidAvs, e);
        }

        // Assert
        ITable actualData = dbUnitConnection.createQueryTable("ALLOCATAIRES", "SELECT * FROM ALLOCATAIRES");
        IDataSet expectedData = DBUnitUtils.loadDataSet("expectedDataAfterDeletion.xml");
        ITable expectedTable = expectedData.getTable("ALLOCATAIRES");

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
        try {
            allocationService.updateAllocataire(invalidAvs, invalidName, invalidSurname);
        } catch (IllegalArgumentException e) {
            log.info("Expected exception for invalid AVS: {}", invalidAvs, e);
        }

        // Assert
        ITable actualData = dbUnitConnection.createQueryTable("ALLOCATAIRES", "SELECT * FROM ALLOCATAIRES");
        IDataSet expectedData = DBUnitUtils.loadDataSet("expectedDataAfterUpdate.xml");
        ITable expectedTable = expectedData.getTable("ALLOCATAIRES");

        Assertion.assertEquals(expectedTable, actualData);
    }

    // Other test methods can be added here...
}
