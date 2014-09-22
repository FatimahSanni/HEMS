
package HEMS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1302";
    private static final String CONN_STRING = "jdbc:mysql://localhost:3306/efhg";
    
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
    }
}
