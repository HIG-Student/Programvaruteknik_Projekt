package se.hig.programvaruteknik.database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


//http://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
public class JDBCExample {

	public static void main(String[] argv) {

		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;
		
		try {

			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/statistics", "kaka", "kaka");
			
			String sql = "INSERT INTO data(user_id, data, title) VALUES (44, 'hejhej', 'greeting')";
			
			Statement statement = connection.createStatement();
			
			statement.executeUpdate(sql);
			
		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}
		
		try {

			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/statistics", "kaka", "kaka");
			
			connection.prepareCall("").executeQuery();

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control of your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
	}

}