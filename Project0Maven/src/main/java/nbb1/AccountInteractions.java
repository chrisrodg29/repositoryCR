package nbb1;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

import logging.LoggerInterface;
import oracle.jdbc.OracleTypes;

public class AccountInteractions implements LoggerInterface{
	
	public static void getOwnedAccounts(int accountOwner) {
		Connection conn = ConnectionPackager.getInstance().getConnection();		
		CallableStatement cstat = null;
		ResultSet rs = null;
		
		String sql = "{call get_owned_accounts(?, ?)}";
		
		try {
			cstat = conn.prepareCall(sql);	
			cstat.setInt(1, accountOwner);
			cstat.registerOutParameter(2, OracleTypes.CURSOR);
			cstat.execute();
			log.info("Account numbers accessed");
			rs = (ResultSet) cstat.getObject(2);
			
			System.out.println("Accounts Available:");
			while (rs.next()) {
				System.out.println(rs.getInt("acc_num"));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void customerAccountMenu(Scanner x, int selectedAccount) {
		boolean exitVal = false;
		while(exitVal == false) {
			AccountObjectHolder.getAccountHolder().getAccount(selectedAccount);
			System.out.println(AccountObjectHolder.getAccountHolder());
			System.out.println("What would you like to do?\nAvailable Options:");
			System.out.println("1. Deposit \n2. Withdraw \n3. Transfer Funds");
			if(AccountObjectHolder.getAccountHolder().getPending() != 0)
				System.out.println("4. Accept Incoming Transfers");
			System.out.println("0. Exit");
			
			int interactSelection = x.nextInt();
			switch(interactSelection) {
			case 0:
				exitVal = true;
				break;
			case 1:
				System.out.println("How much would you like to deposit?");
				double depositAmount = x.nextDouble();
				accountProcedure("deposit", selectedAccount, depositAmount, 0);
				log.info("Deposit to "+selectedAccount);
				break;
			case 2:
				System.out.println("How much would you like to withdraw?");
				double withdrawalAmount = x.nextDouble();
				accountProcedure("withdraw", selectedAccount, withdrawalAmount, 0);
				log.info("Withdrawal from "+selectedAccount);
				break;
			case 3:
				System.out.println("How much would you like to transfer?");
				double transferAmount = x.nextDouble();
				x.nextLine();
				System.out.println("Into which account are we transferring?");
				int targetAccount = x.nextInt();
				accountProcedure("transfer", selectedAccount, transferAmount, targetAccount);
				log.info("Transfer from "+selectedAccount+" to " + targetAccount);
				break;
			case 4:
				if(AccountObjectHolder.getAccountHolder().getPending() != 0) {
					accountProcedure("accept", selectedAccount, 0, 0);
					log.info("Pending accepted for Account"+selectedAccount);
					break;
				}
			default:
				System.out.println("Please choose one of the available options.");
				log.error("Invalid option chosen: "+interactSelection);
				break;
			}
		}
			
	}
	
	public static void accountProcedure(String procedureType, int selectedAccount, double transactionAmount, int targetAccount) {
		Connection conn = ConnectionPackager.getInstance().getConnection();
		CallableStatement cstat = null;
		String procedure = null;
		String procedurevars = null;
		String customerPrompt = null;
				
		switch (procedureType) {
			case "deposit":
				procedure = "nbb_deposit";
				procedurevars = "(?, ?, ?)";
				break;
			case "withdraw":
				procedure = "nbb_withdrawal";
				procedurevars = "(?, ?, ?)";
				break;
			case "transfer":
				procedure = "nbb_initiate_transfer";
				procedurevars = "(?, ?, ?, ?)";
				break;
			case "accept":
				procedure = "nbb_accept_transfer";
				procedurevars = "(?, ?)";
				break;			
		}

		String sql = "{call "+procedure+ " "+procedurevars+"}";
		
		try {
			cstat = conn.prepareCall(sql);			
			cstat.registerOutParameter(1, java.sql.Types.VARCHAR);
			cstat.setInt(2, selectedAccount);
			if(!procedureType.equals("accept"))
				cstat.setDouble(3, transactionAmount);
			if(procedureType.equals("transfer"))
				cstat.setInt(4, targetAccount);
			cstat.execute();
			log.info(procedure+" procedure successful");
			customerPrompt = cstat.getString(1);
			System.out.println(customerPrompt);	
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void employeeAccountMenu(Scanner x, int selectedAccount) {
		boolean exitVal = false;
		while(exitVal == false) {
			AccountObjectHolder.getAccountHolder().getAccount(selectedAccount);
			System.out.println(AccountObjectHolder.getAccountHolder().fullAccountInfo());
			System.out.println("What would you like to do?\nAvailable Options:");
			System.out.println("1. Suspend/Unsuspend \n2. Approve Account \n3. Reject Account \n4. View Transaction Log");
			System.out.println("0. Exit");
			
			int interactSelection = x.nextInt();
			switch(interactSelection) {
			case 0:
				exitVal = true;
				break;
			case 1:
				if (AccountObjectHolder.getAccountHolder().getSuspended() == 0) {
					employeeAccountChange(selectedAccount, 1, 0);
					System.out.println("Account Suspended.");
					log.info("Account "+selectedAccount+" suspended");
				} else {
					employeeAccountChange(selectedAccount, 1, 1);
					System.out.println("Account Unsuspended.");
					log.info("Account "+selectedAccount+" unsuspended");
				}
				break;
			case 2:
				employeeAccountChange(selectedAccount, 2, 0);
				System.out.println("Account approved.");
				log.info("Account "+selectedAccount+" approved");
				break;
			case 3:
				employeeAccountChange(selectedAccount, 3, 0);
				System.out.println("Account rejected.");
				log.info("Account "+selectedAccount+" rejected");
				exitVal = true;
				break;
			case 4:
				employeeAccountChange(selectedAccount, 4, 0);
				log.info("Account "+selectedAccount+" transaction log pulled");
			default:
				System.out.println("Please choose one of the available options");
				log.error("Invalid option selected");
				break;
			}
			
		}
			
	}
	
	public static void employeeAccountChange(int selectedAccount, int action, int currentStatus) {
		Connection conn = new DBConnection().conn;		
		PreparedStatement prepstmt=null;
		String query = null;
		ResultSet rs = null;
		switch(action) {
		case 1:
			if (currentStatus == 0)
				query = "UPDATE nbb_accounts_list SET acc_suspended = 1 WHERE acc_num = ?";
			else
				query = "UPDATE nbb_accounts_list SET acc_suspended = 0 WHERE acc_num = ?";
			break;
		case 2:
			double balance = AccountObjectHolder.getAccountHolder().getBalance();
			double pending = AccountObjectHolder.getAccountHolder().getPending();
			double total = balance + pending;
			query = "UPDATE nbb_accounts_list SET acc_balance = "+total+", acc_pending = 0, "
					+ "acc_suspended = 0, acc_approved = 1 WHERE acc_num = ?";
			break;
		case 3:
			query = "DELETE FROM nbb_accounts_list WHERE acc_num = ?";
			break;
		case 4:
			query = ("SELECT * FROM nbb_transaction_history WHERE trans_acc_num = ? ORDER BY TIME_STAMP");
			//NEED TO CREATE PROCEDURE and callable method for accessing, or need to flesh out prepared statement
			//May have to separate this to new method entirely
			break;
		}
		
		try {
			prepstmt=conn.prepareStatement(query);
			prepstmt.setInt(1, selectedAccount);
			if(action != 4)
				prepstmt.execute();
			if(action == 4) {
				rs = prepstmt.executeQuery();
				while(rs.next()) {
					System.out.println(
							"Timestamp: "+rs.getString("TIME_STAMP")
							+"  Transaction Amount: "+rs.getDouble("TRANSACTION_AMOUNT")
							+"  Balance: "+rs.getDouble("NEW_BALANCE")
							+"  Pending: "+rs.getDouble("NEW_PENDING")
							+"  Transaction Type: "+rs.getString("TRANSACTION_TYPE"));
					log.info("Transaction for "+selectedAccount+" recorded");
				}
				
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
