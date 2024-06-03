package unitTests;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import java.io.InputStream;

public class DBUnitUtils {

    public static IDataSet loadDataSet(String filename) {
        try (InputStream is = DBUnitUtils.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new RuntimeException("File not found: " + filename);
            }
            return new FlatXmlDataSetBuilder().build(is);
        } catch (Exception e) {
            throw new RuntimeException("Error loading dataset from file: " + filename, e);
        }
    }
}
