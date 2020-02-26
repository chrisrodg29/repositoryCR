package nbb1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import logging.LoggerInterface;

public class DBConnection implements LoggerInterface{
	
	Connection conn = null;
	
	public DBConnection() {
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:chrisrodg29D", "HR", "HR");
			log.info("Database connection successful");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
