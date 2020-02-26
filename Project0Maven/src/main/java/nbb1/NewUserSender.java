package nbb1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import logging.LoggerInterface;

public class NewUserSender implements LoggerInterface{

	String query = null;	
	static String role = "customer";
	static String username;
	static String pword;
	static String pwordCheck;
	static String firstName;
	static String lastName;
	static String email;
	static String street;
	static String city;
	static String stateAbbr;
	static int zipcode;
	
	public static String getUsername() {
		return username;
	}

	public static String getPword() {
		return pword;
	}
	
	public static void fillFields(Scanner x) {
		
		System.out.println("We'd be happy to help you create a new user account!");
		System.out.println("Please fill out each of the following fields:");
		System.out.println("Preferred Username?");
		username = x.nextLine();
		do {
			System.out.println("Password?");
			pword = x.nextLine();
			System.out.println("Please confirm password");
			pwordCheck = x.nextLine();
			if (!pword.equals(pwordCheck))
				System.out.println("Oops! Passwords didn't match. Please try again.\n");
		}while (!pword.equals(pwordCheck));		
		System.out.println("What is your: First Name?");
		firstName = x.nextLine();
		System.out.println("Last Name?");
		lastName = x.nextLine();
		System.out.println("Email address?");
		email = x.nextLine();
		System.out.println("Street Address?");
		street = x.nextLine();
		System.out.println("City?");
		city = x.nextLine();
		System.out.println("State? (Please use the two letter abbreviation)");
		stateAbbr = x.nextLine();
		System.out.println("Zipcode?");
		zipcode = x.nextInt();	
		log.info("New user profile info received");
	}
	
	public static void sendToDB() {
		Connection conn = new DBConnection().conn;		
		PreparedStatement prepstmt=null;
		//ResultSet resultSet = null;  NOT NEEDED
		String query = null;
		
		query = "INSERT INTO nbb_users (user_id, role_descript, username, pword, first_name, last_name, email, street, city, state_abbr, zipcode)"
				+ "VALUES (IDIncrementer.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			prepstmt=conn.prepareStatement(query);
			prepstmt.setString(1, role);
			prepstmt.setString(2, username);
			prepstmt.setString(3, pword);
			prepstmt.setString(4, firstName);
			prepstmt.setString(5, lastName);
			prepstmt.setString(6, email);
			prepstmt.setString(7, street);
			prepstmt.setString(8, city);
			prepstmt.setString(9, stateAbbr);
			prepstmt.setInt(10, zipcode);
			prepstmt.execute();
			log.info("New user profile info recorded");
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
		
		System.out.println("Your user profile has been created!");
	}

}
