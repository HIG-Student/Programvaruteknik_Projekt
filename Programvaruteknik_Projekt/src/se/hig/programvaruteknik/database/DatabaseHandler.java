package se.hig.programvaruteknik.database;

import java.sql.*;

public class DatabaseHandler {

	private String databaseURL;
	
	public void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}

	public Object getDatabaseUrl() {
		return databaseURL;
	}

}
