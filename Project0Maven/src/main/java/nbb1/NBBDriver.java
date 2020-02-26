package nbb1;

import java.util.Scanner;

import logging.LoggerInterface;

public class NBBDriver implements LoggerInterface{
	
	public static void main(String[] args) {
		
		log.info("Program started");
		
		System.out.println("Welcome to New Beginnings Bank!");
		Scanner inputx = new Scanner(System.in);
		
		
		//LOGIN PHASE
		LoginShell shell = LoginShell.getShell();
		Boolean valSuccess = false;
		while (!valSuccess) {
			shell.login(inputx);
			valSuccess = LoginShell.getShell().validate();
			if(!valSuccess) {
				System.out.println("Oops! Username/Password did not match our records. Please try again.");
				log.info("Unsuccessful login");
			}
		}
		log.info("Successful login");
		int userID = shell.getUserID(shell.getUsername());
		
		//ACCOUNT SELECT PHASE
		AccountSelect.accountSelectPlatform(inputx, userID);

		
		
		System.out.println("Thank you for your business! Have a nice day!");
		inputx.close();
		log.info("Program ended");
	}

}
