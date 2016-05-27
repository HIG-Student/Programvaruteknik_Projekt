/**
 * 
 */
package se.hig.programvaruteknik.database;

import java.util.List;
import java.util.Map;

/**
 * @author Viktor Hanstorp
 */
public abstract class DataHandler
{

    /**
     * Save data as a user
     * 
     * @param userId
     *            the user that saves the data
     * @param title
     *            the title that the save should have
     * @param data
     *            the data to save
     * 
     * @return saveData the id of the entry
     * 
     * @throws DataHandlerException
     *             if any parameter is null
     */
    public final Long saveData(Long userId, String title, String data)
    {
	if (userId == null) throw new DataHandlerException("Save: Missing user id");
	if (title == null) throw new DataHandlerException("Save: Missing title");
	if (data == null) throw new DataHandlerException("Save: Missing data");

	return _saveData(userId, title, data);
    }

    protected abstract Long _saveData(Long userId, String title, String json);

    /**
     * Load data as a user
     * 
     * @param userId
     *            the user that loads the data
     * @param index
     *            the index of the data to load
     * 
     * @return the loaded data
     * 
     * @throws DataHandlerException
     *             if any parameter is null
     */
    public final String loadData(Long userId, Long index)
    {
	if (index == null) throw new DataHandlerException("Load: Null is not a valid index");
	if (index < 0) throw new DataHandlerException("Load: Negative index is not valid");
	if (index == 0) throw new DataHandlerException("Load: Index begins at 1, not 0");

	return _loadData(userId, index);
    }

    protected abstract String _loadData(Long userId, Long index);

    /**
     * Delete data as user
     * 
     * @param userId
     *            the user that deletes the data
     * @param index
     *            the index of the data to delete
     * 
     * @return the index of the deleted data
     * 
     * @throws DataHandlerException
     *             if any parameter is null
     */
    public final Long deleteData(Long userId, Long index)
    {
	if (index == null) throw new DataHandlerException("Delete: Null is not a valid index");
	if (index < 0) throw new DataHandlerException("Delete: Negative index is not valid");
	if (index == 0) throw new DataHandlerException("Delete: Index begins at 1, not 0");

	return _deleteData(userId, index);
    }

    protected abstract Long _deleteData(Long userId, Long index);

    /**
     * Get saved data as a user
     * 
     * @param userId
     *            the user to load data
     * 
     * @return the saved data
     */
    public abstract List<Map<String, Object>> getList(Long userId);

    /**
     * Validating login
     * 
     * @param username
     *            the user name to validate
     * @param password
     *            the password to validate
     * 
     * @return the validity of the credentials
     *
     */
    public final boolean validateLogin(String username, String password)
    {
	if (username == null || password == null) return false;

	return _validateLogin(username.toLowerCase(), password);
    }

    protected abstract boolean _validateLogin(String username, String password);

    /**
     * Create a login
     * 
     * @param username
     *            the username to use
     * @param password
     *            the password to use
     * 
     * @throws DataHandlerCannotCreateLoginException
     *             if parameters are null or if the login cannot otherwise be
     *             created
     */
    public final void createLogin(String username, String password) throws DataHandlerCannotCreateLoginException
    {
	if (username == null) throw new DataHandlerCannotCreateLoginException("Name cannot be null!");
	if (password == null) throw new DataHandlerCannotCreateLoginException("Password cannot be null!");

	_createLogin(username.toLowerCase(), password);
    }

    protected abstract void _createLogin(String username, String password);

    /**
     * Get the if of a user
     * 
     * @param username
     *            the user to get id from
     * 
     * @return the id of the user
     * 
     * @throws DataHandlerException
     *             if username is null
     */
    public final long getUserId(String username)
    {
	if (username == null) throw new DataHandlerException("Name cannot be null!");

	return _getUserId(username.toLowerCase());
    }

    protected abstract long _getUserId(String username);

    /**
     * An exception related to the database handler
     */
    public class DataHandlerException extends RuntimeException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * An exception that indicates that there was an error with the data
	 * handler
	 */
	public DataHandlerException()
	{

	}

	/**
	 * An exception that indicates that there was an error with the data
	 * handler
	 * 
	 * @param message
	 *            the information about the exception
	 */
	public DataHandlerException(String message)
	{
	    super(message);
	}

	/**
	 * An exception that indicates that there was an error with the data
	 * handler
	 * 
	 * @param throwable
	 *            the throwable that evoked this exception
	 */
	public DataHandlerException(Throwable throwable)
	{
	    super(throwable);
	}
    }

    /**
     * An exception that indicates that the data handelr cannot create a login
     */
    public class DataHandlerCannotCreateLoginException extends DataHandlerException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * An exception that indicates that the data handelr cannot create a
	 * login
	 */
	public DataHandlerCannotCreateLoginException()
	{

	}

	/**
	 * An exception that indicates that the data handelr cannot create a
	 * login
	 * 
	 * @param message
	 *            additional information
	 */
	public DataHandlerCannotCreateLoginException(String message)
	{
	    super(message);
	}

	/**
	 * An exception that indicates that the data handelr cannot create a
	 * login
	 * 
	 * @param throwable
	 *            the throwable that evoked this exception
	 */
	public DataHandlerCannotCreateLoginException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
