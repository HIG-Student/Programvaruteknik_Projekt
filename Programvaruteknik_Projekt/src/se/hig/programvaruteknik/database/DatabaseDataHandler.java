package se.hig.programvaruteknik.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DatabaseDataHandler extends DataHandler
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
	catch (Exception e)
	{
	    throw new DatabaseDataHandlerException(e);
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
	    throw new DatabaseDataHandlerException(e);
	}
    }

    @Override
    protected Long saveData(String title, String data)
    {
	try
	{
	    PreparedStatement statement = connection
		    .prepareStatement("INSERT INTO data(title, data) VALUES (?, ?) RETURNING id;");
	    statement.setString(1, title);
	    statement.setString(2, data);
	    ResultSet result = statement.executeQuery();
	    if (!result.next()) throw new DatabaseDataHandlerException("Cannot save data");
	    return result.getLong("id");
	}
	catch (SQLException e)
	{
	    throw new DatabaseDataHandlerException(e);
	}
    }

    @Override
    protected String loadData(Long i)
    {
	try
	{
	    PreparedStatement statement = connection.prepareStatement("SELECT * FROM data WHERE id = ?");
	    statement.setLong(1, i);
	    ResultSet result = statement.executeQuery();
	    if (!result.next()) throw new DatabaseDataHandlerException("No data found for index " + i);
	    return result.getString("data");
	}
	catch (SQLException e)
	{
	    throw new DatabaseDataHandlerException(e);
	}
    }

    @Override
    public List<Map<String, Object>> getList()
    {
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	try
	{
	    ResultSet result = connection.createStatement().executeQuery("SELECT (id,title) FROM data");
	    while (result.next())
	    {
		Map<String, Object> entry = new TreeMap<>();
		entry.put("id", result.getLong("id"));
		entry.put("title", result.getString("title"));
		list.add(entry);
	    }
	}
	catch (SQLException e)
	{
	    throw new DatabaseDataHandlerException(e);
	}

	return list;
    }

    public class DatabaseDataHandlerException extends DataHandlerException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseDataHandlerException()
	{

	}

	public DatabaseDataHandlerException(String message)
	{
	    super(message);
	}

	public DatabaseDataHandlerException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
