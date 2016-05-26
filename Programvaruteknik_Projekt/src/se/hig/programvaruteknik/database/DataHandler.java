/**
 * 
 */
package se.hig.programvaruteknik.database;

import java.util.List;
import java.util.Map;

/**
 * @author Viktor Hanstorp
 *         docs by Fredrik
 */
public abstract class DataHandler
{

    /**
     * Method controls the title and data value if not correct<br>
     * an exception is thrown
     * 
     * @param userId
     *            user id
     * @param title
     *            the title that the save should have
     * @param data
     *            the data to save
     * 
     * @return saveData the id of the entry
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
     * Method controls the index value if not correct<br>
     * an exception is thrown
     * 
     * @param index
     *            containing data
     * @return loadData and index content
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
     * Method controls the index value if not correct<br>
     * an exception is thrown
     * 
     * @param index
     *            containing data
     * @return deleteData and index content
     */
    public final Long deleteData(Long userId, Long index)
    {
	if (index == null) throw new DataHandlerException("Delete: Null is not a valid index");
	if (index < 0) throw new DataHandlerException("Delete: Negative index is not valid");
	if (index == 0) throw new DataHandlerException("Delete: Index begins at 1, not 0");

	return _deleteData(userId, index);
    }

    protected abstract Long _deleteData(Long userId, Long index);

    public abstract List<Map<String, Object>> getList(Long userId);

    /**
     * Exception class that extends RuntimeException
     *
     */
    public final boolean validateLogin(String username, String password)
    {
	if (username == null || password == null) return false;

	return _validateLogin(username.toLowerCase(), password);
    }

    protected abstract boolean _validateLogin(String username, String password);

    public final void createLogin(String username, String password) throws DataHandlerCannotCreateLoginException
    {
	if (username == null) throw new DataHandlerCannotCreateLoginException("Name cannot be null!");
	if (password == null) throw new DataHandlerCannotCreateLoginException("Password cannot be null!");

	_createLogin(username.toLowerCase(), password);
    }

    protected abstract void _createLogin(String username, String password);

    public final long getUserId(String username)
    {
	if (username == null) throw new DataHandlerException("Name cannot be null!");

	return _getUserId(username.toLowerCase());
    }

    protected abstract long _getUserId(String username);

    public class DataHandlerException extends RuntimeException
    {
	public DataHandlerException()
	{

	}

	public DataHandlerException(String message)
	{
	    super(message);
	}

	public DataHandlerException(Throwable throwable)
	{
	    super(throwable);
	}
    }

    public class DataHandlerCannotCreateLoginException extends DataHandlerException
    {
	public DataHandlerCannotCreateLoginException()
	{

	}

	public DataHandlerCannotCreateLoginException(String message)
	{
	    super(message);
	}

	public DataHandlerCannotCreateLoginException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
