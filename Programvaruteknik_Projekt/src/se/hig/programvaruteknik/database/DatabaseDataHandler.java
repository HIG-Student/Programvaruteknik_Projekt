package se.hig.programvaruteknik.database;

import java.sql.Connection;
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
    private DataSource dataSource;

    public DatabaseDataHandler()
    {
	dataSource = createDataSource();
    }

    private DataSource createDataSource()
    {
	try
	{
	    Context initCtx = new InitialContext();
	    Context envCtx = (Context) initCtx.lookup("java:comp/env");
	    return (DataSource) envCtx.lookup("jdbc/Kaka");
	}
	catch (Exception e)
	{
	    throw new DatabaseDataHandlerException(e);
	}
    }

    @Override
    protected Long saveData(String title, String data)
    {
	try (Connection connection = dataSource.getConnection())
	{
	    PreparedStatement statement = connection
		    .prepareStatement("INSERT INTO saves(title, data) VALUES (?, ?) RETURNING id;");
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
	try (Connection connection = dataSource.getConnection())
	{
	    PreparedStatement statement = connection.prepareStatement("SELECT * FROM saves WHERE id = ?");
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
    protected Long deleteData(Long index)
    {
	try (Connection connection = dataSource.getConnection())
	{
	    PreparedStatement statement = connection.prepareStatement("DELETE FROM saves WHERE id = ?");
	    statement.setLong(1, index);
	    if (statement
		    .executeUpdate() != 1) throw new DatabaseDataHandlerException("No data found for index " + index);
	    return index;
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
	try (Connection connection = dataSource.getConnection())
	{
	    ResultSet result = connection.prepareStatement("SELECT id,title FROM saves").executeQuery();
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
