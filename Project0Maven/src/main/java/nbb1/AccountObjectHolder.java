package nbb1;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import logging.LoggerInterface;

public class AccountObjectHolder implements LoggerInterface{
	
	private static AccountObjectHolder holder = new AccountObjectHolder();
	private int accountNum;
	private String accountType;
	private int owner;
	private double balance;
	private double pending;
	private int suspended;
	private int approved;
	
	public static AccountObjectHolder getAccountHolder() {
		return holder;
	}
	public int getAccountNum() {
		return accountNum;
	}
	public String getAccountType() {
		return accountType;
	}
	public int getOwner() {
		return owner;
	}
	public double getBalance() {
		return balance;
	}
	public double getPending() {
		return pending;
	}
	public int getSuspended() {
		return suspended;
	}
	public int getApproved() {
		return approved;
	}
	public void setAccountNum(int accountNum) {
		this.accountNum = accountNum;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public void setOwner(int owner) {
		this.owner = owner;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public void setPending(int pending) {
		this.pending = pending;
	}
	public void setSuspended(int suspended) {
		this.suspended = suspended;
	}
	public void setApproved(int approved) {
		this.approved = approved;
	}
	
	//CONSTRUCTOR
	private AccountObjectHolder() {
		
	}
	
	//TOSTRING	
	@Override
	public String toString() {
		return ("Account#: "+accountNum+" ... Type: "+accountType+"\nBalance: "+balance+"\nPending Transfers: "+pending);
	}
	
	public String fullAccountInfo() {
		String suspendedInterpretation = null;
		if (suspended == 1)
			suspendedInterpretation = "Yes";
		if (suspended == 0)
			suspendedInterpretation = "No";
		String approvedInterpretation = null;
		if (approved == 1)
			approvedInterpretation = "Yes";
		if (approved == 0)
			approvedInterpretation = "No";
		
		return ("Account#: "+accountNum+" ... Type: "+accountType+"\nBalance: "+balance
				+"\nPending Transfers: "+pending+"\nSuspended: "+suspendedInterpretation+"\nApproved: "+approvedInterpretation);
	}
	
	public static void createAccount(Scanner x, int userID) {
		Connection conn = new DBConnection().conn;	
		CallableStatement cstat =null;
		String sql = null;
		
		System.out.println("We'd be happy to speak with you about opening an account with us!"
				+ "\nWhat kind of account are you wanting to open?");
		System.out.println("Please choose one of the following options:"
				+ "\n1. Checking"+"\n2. Savings");
		
		int selectedType = x.nextInt();
		x.nextLine();
		
		switch(selectedType) {
		case 1:
			sql = "{call create_checking_account(?, ?)";
			break;
		case 2:
			sql = "{call create_savings_account(?, ?)";
			break;
		default:
			System.out.println("Please choose one of the available options");
			break;
		}
		
		System.out.println("How much will your initial deposit be?");
			
		double initialDeposit = x.nextDouble();
		x.nextLine();
		
		try {
			cstat=conn.prepareCall(sql);
			cstat.setInt(1, userID);
			cstat.setDouble(2, initialDeposit);
			cstat.execute();
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
		
		System.out.println("Thank you for your application! One of our New Accounts Officers will contact you within forty-eight hours to discuss opening an account.");
		log.info("New account application sent to DB");
		
	}
	
	public static boolean checkAccountAccessPermissions(int userID, int accountNum) {
		Connection conn = new DBConnection().conn;		
		PreparedStatement prepstmt=null;
		ResultSet resultSet = null;
		int checkNum = 0;
		int checkSusp = 0;
		boolean testVal = false;
		String query = "SELECT * FROM nbb_accounts_list WHERE acc_num = "+accountNum;
		try {
			prepstmt=conn.prepareStatement(query);
			resultSet=prepstmt.executeQuery();
			if (resultSet.isBeforeFirst() ) {
				resultSet.next();
				checkNum = resultSet.getInt("acc_owner");
				checkSusp = resultSet.getInt("acc_suspended");
			}else if (!resultSet.isBeforeFirst() ) {    
				System.out.println("Account number not accessible. Please enter a displayed account number.");
				AccountInteractions.getOwnedAccounts(userID);
			}
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
		
		if (checkNum == userID && checkSusp == 0)
			testVal = true;
		if (checkNum == userID && checkSusp == 1) {
			System.out.println("That account number is currently suspended. Please contact our offices.");
			log.warn("Attempted access of suspended account");
		}
		
		return testVal;			
	}
	
	public void getAccount(int inputNum) {
		Connection conn = new DBConnection().conn;	
		PreparedStatement prepstmt=null;
		ResultSet resultSet = null;
		String query = "SELECT * FROM nbb_accounts_list WHERE acc_num = ?";
		try {
			prepstmt=conn.prepareStatement(query);
			prepstmt.setInt(1, inputNum);
			resultSet=prepstmt.executeQuery();
			log.info("Account "+inputNum+" info accessed");
			while(resultSet.next()) {
				accountNum = resultSet.getInt("acc_num");
				accountType = resultSet.getString("acc_type");
				owner = resultSet.getInt("acc_owner");
				balance = resultSet.getDouble("acc_balance");
				pending = resultSet.getDouble("acc_pending");
				suspended = resultSet.getInt("acc_suspended");
				approved = resultSet.getInt("acc_approved");
			}
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
		
		
		
		
	}
	
	
	
}
