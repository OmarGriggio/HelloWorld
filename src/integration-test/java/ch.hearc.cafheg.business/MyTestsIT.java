package ch.hearc.cafheg.business;

/*import org.dbunit.DBTestCase;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;

public class MyTestsIT extends DBTestCase {

    private static final String JDBC_DRIVER = "your.driver.here";
    private static final String JDBC_URL = "your.url.here";
    private static final String USER = "your.username.here";
    private static final String PASSWORD = "your.password.here";

    public MyTestsIT(String name) {
        super(name);
    }

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        IDataSet dataSet = getDataSet();
        try (Connection jdbcConnection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        }
    }

    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/integration-test/data/data.xml"));
    }

    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.NONE;
    }

    @Test
    public void testAllocatairesData() throws Exception {
        // Get the actual data from the database
        try (Connection jdbcConnection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
            IDataSet databaseDataSet = connection.createDataSet();
            ITable actualTable = databaseDataSet.getTable("ALLOCATAIRES");

            // Get the expected data from the XML dataset
            IDataSet expectedDataSet = getDataSet();
            ITable expectedTable = expectedDataSet.getTable("ALLOCATAIRES");

            // Assert that the actual data matches the expected data
            Assertion.assertEquals(expectedTable, actualTable);
        }
    }
}

 */