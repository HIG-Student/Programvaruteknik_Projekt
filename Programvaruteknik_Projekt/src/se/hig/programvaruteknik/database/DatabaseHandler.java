package se.hig.programvaruteknik.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {

	public DatabaseHandler(String url, String user, String password) {
		createConnection(url, user, password);
	}

	private Connection createConnection(String url, String user, String password) {
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new DatabaseHandlerException();
		}
	}

	public class DatabaseHandlerException extends RuntimeException {

	}

}
