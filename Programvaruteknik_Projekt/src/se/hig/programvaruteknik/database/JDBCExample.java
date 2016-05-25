package se.hig.programvaruteknik.database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

//http://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/

//http://www.tutorialspoint.com/postgresql/postgresql_java.htm
public class JDBCExample
{

    public static void main(String[] argv)
    {

	System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

	try
	{

	    Context initCtx = new InitialContext();
	    Context envCtx = (Context) initCtx.lookup("java:comp/env");
	    DataSource ds = (DataSource) envCtx.lookup("jdbc/webapp");

	    Connection connection = ds.getConnection();

	    try
	    {
		String sql = "INSERT INTO data(data, title) VALUES (?, ?)";

		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, "Dennis har ingen Gnu");
		statement.setString(2, "Dennis är en Gnu");
		statement.execute();

	    }
	    catch (SQLException e)
	    {

		System.out.println("Connection Failed! Check output console");
		e.printStackTrace();
		return;

	    }

	}
	catch (Exception e)
	{

	    System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
	    e.printStackTrace();
	    return;

	}

	System.out.println("PostgreSQL JDBC Driver Registered!");

	Connection connection = null;

	try
	{

	    connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/statistics", "kaka", "kaka");

	    Statement stmt = connection.createStatement();
	    ResultSet rs = stmt.executeQuery("SELECT * FROM data;");
	    while (rs.next())
	    {
		int id = rs.getInt("id");
		int user_id = rs.getInt("user_id");
		String title = rs.getString("title");
		String data = rs.getString("data");

		System.out.println("ID = " + id);
		System.out.println("USER_ID = " + user_id);
		System.out.println("TITLE = " + title);
		System.out.println("data = " + data);
		System.out.println();
	    }

	}
	catch (SQLException e)
	{

	    System.out.println("Connection Failed! Check output console");
	    e.printStackTrace();
	    return;

	}

	if (connection != null)
	{
	    System.out.println("You made it, take control of your database now!");
	}
    }

}