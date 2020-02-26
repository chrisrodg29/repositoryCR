package nbb1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPackager {	
	
	//Internal fields
	private static ConnectionPackager createdWrapper = new ConnectionPackager();
	private Connection actualconnection = null;

	
	//private constructor
	private ConnectionPackager() {
		try {
			actualconnection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:chrisrodg29D", "HR", "HR");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//External callables


	public static ConnectionPackager getInstance() {
		return createdWrapper;
	}
	
	public Connection getConnection() {
		return actualconnection;
	}

}
