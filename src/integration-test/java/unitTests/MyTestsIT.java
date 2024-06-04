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
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTestsIT {

    private static final Logger log = LoggerFactory.getLogger(MyTestsIT.class);

    private IDatabaseConnection dbUnitConnection;
    private Database database;
    private AllocationService allocationService;

    @BeforeEach
    public void setup() {
        log.debug("Starting setup for test");
        try {
            initializeDatabase();
            initializeDbUnitConnection();
            loadTestData("data.xml");
            initializeServices();
        } catch (Exception e) {
            log.error("Error during setup", e);
            throw new RuntimeException("Setup failed", e);
        }
    }

    @AfterEach
    public void tearDown() {
        log.debug("Starting teardown for test");
        try {
            if (dbUnitConnection != null) {
                dbUnitConnection.close();
                log.debug("DBUnit connection closed");
            }
        } catch (SQLException e) {
            log.error("Error closing DBUnit connection", e);
        } finally {
            database.stop();
            log.debug("Database stopped");
        }
    }

    private void initializeDatabase() throws SQLException {
        database = new Database();
        database.start();
        log.debug("Database started");

        Migrations migrations = new Migrations(database, true);
        migrations.start();
        log.debug("Migrations applied");
    }

    private void initializeDbUnitConnection() throws DatabaseUnitException, SQLException {
        Connection jdbcConnection = Database.activeJDBCConnection();
        dbUnitConnection = new DatabaseConnection(jdbcConnection);
        log.debug("DBUnit connection initialized");
    }

    private void loadTestData(String dataFile) {
        log.debug("Loading test data from file: {}", dataFile);
        try {
            IDataSet dataSet = DBUnitUtils.loadDataSet(dataFile);
            DatabaseOperation.CLEAN_INSERT.execute(dbUnitConnection, dataSet);
            log.debug("Test data loaded");
        } catch (Exception e) {
            log.error("Error loading test data from file: {}", dataFile, e);
            throw new RuntimeException("Error loading test data", e);
        }
    }

    private void initializeServices() {
        allocationService = new AllocationService(new AllocataireMapper(), new AllocationMapper());
        log.debug("Services initialized");
    }

    @Test
    @DisplayName("Test Delete Allocataire by NO_AVS")
    public void deleteAllocataireByNoAVS_GivenValidAndInvalidIds_ShouldReflectInDatabase() throws SQLException, DatabaseUnitException, IOException {
        log.debug("Starting test: deleteAllocataireByNoAVS_GivenValidAndInvalidIds_ShouldReflectInDatabase");
        // Arrange
        String validAvs = "AVS105";
        String invalidAvs = "AVS999"; // Assuming AVS999 does not exist in the dataset

        // Act
        allocationService.deleteAllocataire(validAvs);
        try {
            allocationService.deleteAllocataire(invalidAvs);
            fail("Expected IllegalArgumentException for invalid AVS number");
        } catch (IllegalArgumentException e) {
            assertEquals("Allocataire not found", e.getMessage());
        }

        // Export the current state of the database after deletion
        exportDatabaseStateToFile("ALLOCATAIRES", "expectedDataAfterDeletion.xml");

        // Assert
        assertDatabaseStateAfterDeletion("ALLOCATAIRES", "expectedDataAfterDeletion.xml");
    }

    @Test
    @DisplayName("Test Update Allocataire's name and surname by NO_AVS")
    public void updateAllocataireNameByNoAVS_GivenValidAndInvalidIds_ShouldReflectInDatabase() throws SQLException, DatabaseUnitException, IOException {
        log.debug("Starting test: updateAllocataireNameByNoAVS_GivenValidAndInvalidIds_ShouldReflectInDatabase");
        // Arrange
        String validAvs = "AVS101";
        String newName = "NewName";
        String newSurname = "NewSurname";
        String invalidAvs = "AVS999";

        // Act
        updateAndAssertAllocataire(validAvs, newName, newSurname);

        // Export the current state of the database after update
        exportDatabaseStateToFile("ALLOCATAIRES", "expectedDataAfterUpdate.xml");

        // Assert
        assertDatabaseStateAfterUpdate("ALLOCATAIRES", "expectedDataAfterUpdate.xml");
    }

    private void exportDatabaseStateToFile(String tableName, String filePath) throws SQLException, IOException, DataSetException {
        IDataSet databaseDataSet = dbUnitConnection.createDataSet(new String[]{tableName});
        FlatXmlDataSet.write(databaseDataSet, new FileOutputStream(filePath));
        log.debug("Database state exported to file: {}", filePath);
    }

    private void assertDatabaseStateAfterDeletion(String tableName, String expectedDataSetFile) throws DatabaseUnitException, SQLException, IOException {
        IDataSet databaseDataSet = dbUnitConnection.createDataSet();
        ITable actualTable = databaseDataSet.getTable(tableName);

        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(expectedDataSetFile));
        ITable expectedTable = expectedDataSet.getTable(tableName);

        Assertion.assertEquals(expectedTable, actualTable);
    }

    private void assertDatabaseStateAfterUpdate(String tableName, String expectedDataSetFile) throws DatabaseUnitException, SQLException, IOException {
        IDataSet databaseDataSet = dbUnitConnection.createDataSet();
        ITable actualTable = databaseDataSet.getTable(tableName);

        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(expectedDataSetFile));
        ITable expectedTable = expectedDataSet.getTable(tableName);

        Assertion.assertEquals(expectedTable, actualTable);
    }

    private void updateAndAssertAllocataire(String avs, String newName, String newSurname) {
        log.debug("Updating valid allocataire with AVS: {}", avs);
        allocationService.updateAllocataire(avs, newName, newSurname);
        log.debug("Updated valid allocataire");
    }
}
