package org.mariadb.jdbc;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeneratedTest  extends BaseTest {

    /**
     * Tables initialisation.
     * @throws SQLException exception
     */
    @BeforeClass()
    public static void initClass() throws SQLException {
        createTable("genkeys", "priKey INT NOT NULL AUTO_INCREMENT, dataField VARCHAR(64), PRIMARY KEY (priKey)");
    }

    /*
     Test with different APIs that generated keys work. Also test that any name in generatedKeys.getXXX(String name)
     can be passed and is equivalent to generatedKeys.getXXX(1). This might not be 100% compliant, but is a simple
     and effective solution for MySQL that does not does not support more than a single autogenerated value.
    */
    @Test
    public void generatedKeys() throws Exception {
        Statement st = sharedConnection.createStatement();
        st.executeUpdate("insert into genkeys(dataField) values('a')", Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = st.getGeneratedKeys();
        assertTrue(rs.next());
        assertEquals(rs.getInt(1), 1);
        assertEquals(rs.getInt("priKey"), 1);
        assertEquals(rs.getInt("foo"), 1);
        int[] indexes = {1, 2, 3};
        st.executeUpdate("insert into genkeys(dataField) values('b')", indexes);
        rs = st.getGeneratedKeys();
        assertTrue(rs.next());
        assertEquals(rs.getInt(1), 2);
        try {
            assertEquals(rs.getInt(2), 2);
            assertFalse("should never get here", true);
        } catch (SQLException e) {
            // eat
        }

        String[] columnNames = {"priKey", "Alice", "Bob"};
        st.executeUpdate("insert into genkeys(dataField) values('c')", columnNames);
        rs = st.getGeneratedKeys();
        assertTrue(rs.next());
        for (int i = 0; i < 3; i++) {
            assertEquals(rs.getInt(columnNames[i]), 3);
        }
    }

}