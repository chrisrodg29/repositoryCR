package nbb1;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class AccountInteractionsTest {


	
	@Test
	void testGoodAccountPermissions() {
		boolean x = AccountObjectHolder.checkAccountAccessPermissions(2, 1001);
		assert(x==true);
	}
	
	@Test
	void testBadAccountPermissions() {
		boolean y = AccountObjectHolder.checkAccountAccessPermissions(2, 1002);
		assert(y==false);
	}

}
