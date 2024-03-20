
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class MyTestsIT {

    private Connection jdbcConnection;
    private IDatabaseConnection connection;

    @Before
    public void setUp() throws DatabaseUnitException, SQLException {
        jdbcConnection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        connection = new DatabaseConnection(jdbcConnection);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.xml");
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(inputStream);
        DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
    }

    @Test
    public void testAllocatairesTable() throws DatabaseUnitException, SQLException {
        ITable expectedTable = connection.createQueryTable("ALLOCATAIRES", "SELECT * FROM ALLOCATAIRES");
        ITable actualTable = connection.createQueryTable("result_name", "SELECT * FROM ALLOCATAIRES");
        assertEquals(expectedTable, actualTable);
    }

    @Test
    public void testEquals1Is1_ShouldBeTrue() {
        assert (1 == 1);
    }
}