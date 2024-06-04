package unitTests;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class DBUnitUtils {

    public static IDataSet loadDataSet(String filename) throws DataSetException {
        InputStream is = null;
        try {
            // Try to load as a classpath resource
            is = DBUnitUtils.class.getClassLoader().getResourceAsStream(filename);
            if (is == null) {
                // If not found in classpath, try to load as a file path
                File file = new File(filename);
                if (file.exists()) {
                    is = new FileInputStream(file);
                } else {
                    throw new IllegalArgumentException("File not found: " + filename);
                }
            }
            return new FlatXmlDataSetBuilder().build(is);
        } catch (Exception e) {
            throw new DataSetException("Error loading dataset from file: " + filename, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    // Log or handle closing exception if necessary
                }
            }
        }
    }
}
