package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
	public static Connection getConnection() throws SQLException {
		Connection conn= null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:/pool"); 
			// "pool.jocl" URI [/src]
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
