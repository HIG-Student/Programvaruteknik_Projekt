package se.hig.programvaruteknik.database;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestDatabaseHandler {

	DatabaseHandler dbHandler;
	String databaseURL = "localhost:3306/exampleDataBaseUrl";
	
	@Before
	public void setUp(){
		dbHandler = new DatabaseHandler();
	}
	
	@Test
	public void testSetDatabaseUrl() {
		dbHandler.setDatabaseURL(databaseURL);
		assertEquals(databaseURL, dbHandler.getDatabaseUrl());
	}

}
