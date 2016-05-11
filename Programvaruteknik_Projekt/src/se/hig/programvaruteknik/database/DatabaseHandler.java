package se.hig.programvaruteknik.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {

	private Connection connection;

	public DatabaseHandler(String url, String user, String password) {
		connection = createConnection(url, user, password);
	}

	public int save(String title, String data) {

		if (title == null)
			throw new DatabaseHandlerException("Missing title");

		if (data == null)
			throw new DatabaseHandlerException("Missing data");

		try {
			String sql = "INSERT INTO data(data, title) VALUES (?, ?)";

			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, data);
			statement.setString(2, title);
			statement.execute();

			return 0;

		} catch (SQLException e) {
			throw new DatabaseHandlerException(e);
		}
	}

	public String load(Integer i) {

		if (i == null)
			throw new DatabaseHandlerException("Null is invalid index");

		if (i < 0)
			throw new DatabaseHandlerException("Negative index is not valid");
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

	private Connection createConnection(String url, String user, String password) {
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new DatabaseHandlerException();
		}
	}

	@SuppressWarnings("serial")
	public class DatabaseHandlerException extends RuntimeException {
		public DatabaseHandlerException() {
		}

		public DatabaseHandlerException(String message) {
			super(message);
		}

		public DatabaseHandlerException(Throwable throwable) {
			super(throwable);
		}
	}
}
