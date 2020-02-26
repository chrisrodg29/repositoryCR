package nbb1;

import java.util.Scanner;

import logging.LoggerInterface;

public class AccountSelect implements LoggerInterface{	

	public static void accountSelectPlatform(Scanner x, int userID) {
		boolean exitVal = false;
		if(userID != 0) {
			while(exitVal == false) {
				
				boolean testVal = false;
				while (testVal == false) {
					System.out.println("\n");
					AccountInteractions.getOwnedAccounts(userID);
					System.out.println("Please choose an account, or choose one of the available options.");
					System.out.println("9. Apply for new account \n0. Logout ");
					int selection = 1;
					selection = x.nextInt();
					switch(selection) {
					case 9:
						AccountObjectHolder.createAccount(x, userID);
						testVal = true;
						exitVal = true;
						break;
					case 0:
						testVal = true;
						exitVal = true;
						break;
					default://accessing stored accounts
						AccountObjectHolder.getAccountHolder().getAccount(selection);
						testVal = AccountObjectHolder.checkAccountAccessPermissions(userID, selection);
						if (testVal == true) {
							//TRANSACTION PHASE
							AccountInteractions.customerAccountMenu(x, selection);			}
					}				
				}
			}
			
			

		} else if(userID == 0) {
			while(exitVal == false) {
				System.out.println("Employee Menu\n");
				int selection = 1;
				
				boolean testVal = false;
				while (testVal == false) {
					System.out.println("Please enter account number to lookup, or enter 0 to logout");
					selection = x.nextInt();
					switch(selection) {
					case 0:
						testVal = true;
						exitVal = true;
						break;
					default://accessing stored accounts
						AccountInteractions.employeeAccountMenu(x, selection);
						//System.out.println("Invalid selection");
					}				
				}
			}
		}
	
	}
	
}
