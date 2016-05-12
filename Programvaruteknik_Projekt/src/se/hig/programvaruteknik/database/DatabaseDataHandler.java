package se.hig.programvaruteknik.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DatabaseDataHandler implements DataHandler
{
    private Connection connection;

    public DatabaseDataHandler()
    {
	connection = createConnection();
    }

    public DatabaseDataHandler(String url, String user, String password)
    {
	connection = createConnection(url, user, password);
    }

    private Connection createConnection(String url, String user, String password)
    {
	try
	{
	    return DriverManager.getConnection(url, user, password);
	}
	catch (SQLException e)
	{
	    throw new DatabaseDataSaverException(e);
	}
    }

    private Connection createConnection()
    {
	try
	{
	    Context initCtx = new InitialContext();
	    Context envCtx = (Context) initCtx.lookup("java:comp/env");
	    DataSource ds = (DataSource) envCtx.lookup("jdbc/webapp");
	    return ds.getConnection();

	}
	catch (Exception e)
	{
	    throw new DatabaseDataSaverException(e);
	}
    }

    @Override
    public Long save(String title, String data)
    {
	if (title == null) throw new DatabaseDataSaverException("Missing title");

	if (data == null) throw new DatabaseDataSaverException("Missing data");

	try
	{
	    PreparedStatement statement = connection
		    .prepareStatement("INSERT INTO data(title, data) VALUES (?, ?) RETURNING id;");
	    statement.setString(1, title);
	    statement.setString(2, data);
	    ResultSet result = statement.executeQuery();
	    if (!result.next()) throw new DatabaseDataSaverException("Cannot save data");
	    return result.getLong("id");
	}
	catch (SQLException e)
	{
	    throw new DatabaseDataSaverException(e);
	}
    }

    @Override
    public String load(Long i)
    {
	if (i == null) throw new DatabaseDataSaverException("Null is invalid index");

	if (i < 0) throw new DatabaseDataSaverException("Negative index is not valid");

	try
	{
	    PreparedStatement statement = connection.prepareStatement("SELECT * FROM data WHERE id = ?");
	    statement.setLong(1, i);
	    ResultSet result = statement.executeQuery();
	    if (!result.next()) throw new DatabaseDataSaverException("No data found for index " + i);
	    return result.getString("data");
	}
	catch (SQLException e)
	{
	    throw new DatabaseDataSaverException(e);
	}
    }

    @SuppressWarnings("serial")
    public class DatabaseDataSaverException extends RuntimeException
    {
	public DatabaseDataSaverException()
	{

	}

	public DatabaseDataSaverException(String message)
	{
	    super(message);
	}

	public DatabaseDataSaverException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
