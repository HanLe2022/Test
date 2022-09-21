
package test_java3;

import static java.lang.System.exit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnection {
    private static Connection instance = null;

    public MySqlConnection(String databaseName) {
        String user ="root";
        String pass ="1234567908";
        int port = 3306;
        String db_url = "jdbc:mysql://localhost:"+port+"/"+ databaseName;
        try {
            instance = (Connection) DriverManager.getConnection(db_url, user, pass);
            System.out.println("Kết nối thành công!");
        } catch (SQLException ex) {
            System.out.println("Lỗi kết nối: " + ex);
        }
    }
    
    public static Connection getMySqlConnection(String databaseName) {
        if(instance == null) {
            new MySqlConnection(databaseName);
        }
        return instance;
    }
    
}