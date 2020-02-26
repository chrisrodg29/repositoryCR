package nbb1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import logging.LoggerInterface;

public class LoginShell implements LoggerInterface{
	
	private static LoginShell shell = new LoginShell();
	private String Username = null;
	private String Pword = null;
	public String getUsername() {
		return Username;
	}
	public String getPword() {
		return Pword;
	}	

	private LoginShell() {
		
	}
	
	public static LoginShell getShell() {
		return shell; 
	}
	
	public void login(Scanner x) {
		System.out.println("Please enter username and password \n(If you're new to NBB, please enter \"NEW\")");
		System.out.println("Username:");
		Username = x.nextLine();
		if (Username.equalsIgnoreCase("new")) {
			NewUserSender.fillFields(x);
			NewUserSender.sendToDB();
			Username = NewUserSender.getUsername();
			Pword = NewUserSender.getPword();
		} else {
		System.out.println("Password:");
		Pword = x.nextLine();
		}
	}

	public boolean validate(){
		//Connection conn = ConnectionPackager.getInstance().getConnection();
		Connection conn = new DBConnection().conn;
		log.info("Connection created");
		PreparedStatement prepstmt = null;
		ResultSet rs = null;
		String query = "SELECT pword FROM nbb_users WHERE username = ?";
		String accPword = null;
		Boolean val_confirm = false;		
		
		try {
			prepstmt=conn.prepareStatement(query);
			prepstmt.setString(1, shell.Username);
			rs=prepstmt.executeQuery();
			rs.next();
			accPword = rs.getString("pword");			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(shell.Pword.equals(accPword)) {
			val_confirm = true;
		}
		return val_confirm;		
	}
	
	public int getUserID(String username) {
		Connection conn = new DBConnection().conn;	
		log.info("Connection created");
		PreparedStatement prepstmt=null;
		ResultSet resultSet = null;
		int userID = 0;
		String query = "SELECT user_id FROM nbb_users WHERE username = ?";
		try {
			prepstmt=conn.prepareStatement(query);
			prepstmt.setString(1, username);
			resultSet=prepstmt.executeQuery();
			log.info("Account information accessed");
			resultSet.next();
			userID = resultSet.getInt("user_id");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return userID;
		
	}

}
