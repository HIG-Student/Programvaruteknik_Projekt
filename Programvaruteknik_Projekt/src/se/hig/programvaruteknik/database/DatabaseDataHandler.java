package se.hig.programvaruteknik.database;

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

/**
 * A data handler that works against a database
 * 
 * @author Viktor Hanstorp
 */
public class DatabaseDataHandler extends DataHandler
{
    private static int ITERATION_COUNT_FOR_HASH = 20000;
    private static int KEY_LENGTH_FOR_HASH = 256;

    /**
     * An abstraction of a entity that can return a connection
     */
    public interface DataConnection
    {
	/**
	 * Get a connection
	 * 
	 * @return the connection
	 * 
	 * @throws SQLException
	 *             if error occurs
	 */
	Connection getConnection() throws SQLException;
    }

    private DataConnection dataConnection;

    /**
     * Creates a database data handler that uses a specific data-connection
     * 
     * @param dataConnection
     *            the data-connection to use
     */
    public DatabaseDataHandler(DataConnection dataConnection)
    {
	this.dataConnection = dataConnection;
    }

    /**
     * Creates a database data handler<br>
     * <br>
     * Tries to look for a connection to use in the server context by the name
     * "jdbc/Kaka"
     * 
     * @throws DatabaseException
     *             if cannot find the connection in the context
     */
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
		    throw new DatabaseException(e);
		}
	    }

	    @Override
	    public Connection getConnection() throws SQLException
	    {
		return dataSource.getConnection();
	    }
	};
    }

    /**
     * Creates a database data handler
     * 
     * @param url
     *            the url to use
     * @param user
     *            the user to use
     * @param password
     *            the password to use
     */
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
    protected Long _saveData(Long userId, String title, String data)
    {
	try (Connection connection = dataConnection.getConnection())
	{
	    PreparedStatement statement = connection
		    .prepareStatement("INSERT INTO saves(author, title, data) VALUES (?, ?, ?) RETURNING id;");
	    statement.setLong(1, userId);
	    statement.setString(2, title);
	    statement.setString(3, data);
	    ResultSet result = statement.executeQuery();
	    if (!result.next()) throw new DatabaseException("Cannot save data");
	    return result.getLong("id");
	}
	catch (SQLException e)
	{
	    throw new DatabaseException(e);
	}
    }

    @Override
    protected String _loadData(Long userId, Long i)
    {
	try (Connection connection = dataConnection.getConnection())
	{
	    PreparedStatement statement = connection
		    .prepareStatement("SELECT * FROM saves WHERE id = ? AND author = ?");
	    statement.setLong(1, i);
	    statement.setLong(2, userId);
	    ResultSet result = statement.executeQuery();
	    if (!result.next()) throw new DatabaseException("No data found for index " + i);
	    String data = result.getString("data");
	    if (result.next()) throw new DatabaseException("Found more than one entry!");
	    return data;
	}
	catch (SQLException e)
	{
	    throw new DatabaseException(e);
	}
    }

    @Override
    protected Long _deleteData(Long userId, Long index)
    {
	try (Connection connection = dataConnection.getConnection())
	{
	    PreparedStatement statement = connection.prepareStatement("DELETE FROM saves WHERE id = ? AND author = ?");
	    statement.setLong(1, index);
	    statement.setLong(2, userId);
	    int updates = statement.executeUpdate();
	    if (updates == 0) throw new DatabaseException("No data found for index " + index);
	    if (updates > 1) throw new DatabaseException("Ooops, deleted too much for index " + index);
	    return index;
	}
	catch (SQLException e)
	{
	    throw new DatabaseException(e);
	}
    }

    @Override
    public List<Map<String, Object>> getList(Long userId)
    {
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	try (Connection connection = dataConnection.getConnection())
	{
	    PreparedStatement statement = connection.prepareStatement("SELECT id,title FROM saves WHERE author = ?");
	    statement.setLong(1, userId);

	    ResultSet result = statement.executeQuery();
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
	    throw new DatabaseException(e);
	}

	return list;
    }

    @Override
    protected void _createLogin(String username, String password)
    {
	try (Connection connection = dataConnection.getConnection())
	{
	    DatabaseUser user = getUser(connection, username);

	    if (user != null) throw new UserAlreadyExistsException("User '" + username + "' does already exist");

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
		PBEKeySpec spec = new PBEKeySpec(
			password.toCharArray(),
			salt,
			ITERATION_COUNT_FOR_HASH,
			KEY_LENGTH_FOR_HASH);
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
    protected boolean _validateLogin(String username, String password)
    {
	try
	{
	    DatabaseUser user = getUser(username);
	    if (user == null) return false;

	    try
	    {
		return Arrays.equals(user.hash, hashPassword(password, user.salt));
	    }
	    catch (NoSuchAlgorithmException | InvalidKeySpecException e)
	    {
		e.printStackTrace();
		return false;
	    }
	}
	catch (UserDoesNotExistsException e)
	{
	    return false;
	}
    }

    @Override
    protected long _getUserId(String username)
    {
	DatabaseUser user = getUser(username);
	if (user == null)
	    throw new DatabaseException("No user with name '" + username + "'");
	else
	    return getUser(username).id;
    }

    private DatabaseUser getUser(String username)
    {
	try (Connection connection = dataConnection.getConnection())
	{
	    return getUser(connection, username);
	}
	catch (SQLException e)
	{
	    throw new DatabaseException(e);
	}
    }

    private DatabaseUser getUser(Connection connection, String username) throws SQLException
    {
	PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE name = ?");
	statement.setString(1, username);

	ResultSet result = statement.executeQuery();
	if (!result.next()) return null;

	DatabaseUser user = new DatabaseUser(
		result.getLong("id"),
		result.getString("name"),
		result.getBytes("hash"),
		result.getBytes("salt"));

	if (result.next()) throw new DatabaseException("Too many users with name " + username + " found");

	return user;
    }

    private byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
	SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
	PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT_FOR_HASH, KEY_LENGTH_FOR_HASH);
	SecretKey key = skf.generateSecret(spec);
	return key.getEncoded();
    }

    private class DatabaseUser
    {
	private long id;
	private String name;

	private byte[] hash;
	private byte[] salt;

	private DatabaseUser(long id, String name, byte[] hash, byte[] salt)
	{
	    this.id = id;
	    this.name = name;
	    this.hash = hash;
	    this.salt = salt;
	}
    }

    /**
     * Exception that indicates that there already exists a user with the
     * supplied name
     */
    public class UserAlreadyExistsException extends DatabaseException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception that indicates that there already exists a user
	 * with the supplied name
	 */
	public UserAlreadyExistsException()
	{

	}

	/**
	 * Creates an exception that indicates that there already exists a user
	 * with the supplied name
	 * 
	 * @param message
	 *            additional inforamtion
	 */
	public UserAlreadyExistsException(String message)
	{
	    super(message);
	}

	/**
	 * Creates an exception that indicates that there already exists a user
	 * with the supplied name
	 * 
	 * @param throwable
	 *            the throwable that evoked this exception
	 */
	public UserAlreadyExistsException(Throwable throwable)
	{
	    super(throwable);
	}
    }

    /**
     * Exception that indicates that the user that is requested does not
     * exist
     */
    public class UserDoesNotExistsException extends DatabaseException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception that indicates that the user that is requested
	 * does not exist
	 */
	public UserDoesNotExistsException()
	{

	}

	/**
	 * Creates an exception that indicates that the user that is requested
	 * does not exist
	 * 
	 * @param message
	 *            additional inforamtion
	 */
	public UserDoesNotExistsException(String message)
	{
	    super(message);
	}

	/**
	 * Creates an exception that indicates that the user that is requested
	 * does not exist
	 * 
	 * @param throwable
	 *            the throwable that evoked this exception
	 */
	public UserDoesNotExistsException(Throwable throwable)
	{
	    super(throwable);
	}
    }

    /**
     * Exception that indicates that there were an error related to the database
     */
    public class DatabaseException extends DataHandlerException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception that indicates that there was an error related
	 * to the database
	 */
	public DatabaseException()
	{

	}

	/**
	 * Creates an exception that indicates that there was an error related
	 * to the database
	 * 
	 * @param message
	 *            additional information
	 */
	public DatabaseException(String message)
	{
	    super(message);
	}

	/**
	 * Creates an exception that indicates that there was an error related
	 * to the database
	 * 
	 * @param throwable
	 *            the throwable that evoked this exception
	 */
	public DatabaseException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
