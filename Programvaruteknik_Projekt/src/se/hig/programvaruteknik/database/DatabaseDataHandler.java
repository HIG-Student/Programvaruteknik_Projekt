package se.hig.programvaruteknik.database;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DatabaseDataHandler extends DataHandler
{
    public interface DataConnection
    {
	Connection getConnection() throws SQLException;
    }

    private DataConnection dataConnection;

    public DatabaseDataHandler(DataConnection dataConnection)
    {
	this.dataConnection = dataConnection;
    }

    public DatabaseDataHandler()
    {
	dataConnection = new DataConnection()
	{
	    private DataSource dataSource;

	    {
		try
		{
		    Context initCtx = new InitialContext();
		    Context envCtx = (Context) initCtx.lookup("java:comp/env");
		    dataSource = (DataSource) envCtx.lookup("jdbc/Kaka");
		}
		catch (Exception e)
		{
		    throw new DatabaseDataHandlerException(e);
		}
	    }

	    @Override
	    public Connection getConnection() throws SQLException
	    {
		return dataSource.getConnection();
	    }
	};
    }

    public DatabaseDataHandler(String url, String user, String password)
    {
	dataConnection = new DataConnection()
	{
	    @Override
	    public Connection getConnection() throws SQLException
	    {
		return DriverManager.getConnection(url, user, password);
	    }
	};
    }

    @Override
    protected Long saveData(String title, String data)
    {
	try (Connection connection = dataConnection.getConnection())
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
	try (Connection connection = dataConnection.getConnection())
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
	try (Connection connection = dataConnection.getConnection())
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
	try (Connection connection = dataConnection.getConnection())
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

    @Override
    protected void createCredentials(String username, String password)
    {
	try (Connection connection = dataConnection.getConnection())
	{
	    PreparedStatement statement = connection
		    .prepareStatement("INSERT INTO users(name, hash, salt) VALUES (?, ?, ?)");

	    statement.setString(1, username);

	    SecureRandom random = new SecureRandom();
	    byte[] salt = new byte[64];
	    random.nextBytes(salt);
	    statement.setBytes(3, salt);

	    try
	    {
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 2, 256);
		SecretKey key = skf.generateSecret(spec);
		byte[] hash = key.getEncoded();

		statement.setBytes(2, hash);
	    }
	    catch (NoSuchAlgorithmException | InvalidKeySpecException e)
	    {
		throw new RuntimeException(e);
	    }

	    if (statement.executeUpdate() != 1) throw new DataHandlerCannotCreateLoginException("Unknow SQL error 1");
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	    throw new DataHandlerCannotCreateLoginException("Unknow SQL error 2");
	}
    }

    @Override
    protected boolean validateCredentials(String username, String password)
    {
	try (Connection connection = dataConnection.getConnection())
	{
	    PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE name = ?");
	    statement.setString(1, username);

	    ResultSet result = statement.executeQuery();
	    if (!result.next()) return false;

	    try
	    {
		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), result.getBytes("salt"), 2, 256);
		SecretKey key = skf.generateSecret(spec);
		byte[] hash = key.getEncoded();
		return Arrays.equals(result.getBytes("hash"), hash);
	    }
	    catch (NoSuchAlgorithmException | InvalidKeySpecException e)
	    {
		e.printStackTrace();
		return false;
	    }
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	    return false;
	}
    }

    public static void main(String... args)
    {
	// "jdbc:mysql://localhost:3306/mysql"
	System.out.println(
		new DatabaseDataHandler("jdbc:postgresql://localhost:5432/statistics", "kaka", "kaka")
			.validateCredentials("TesAccount", "ThisPasswordIsValid"));
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
