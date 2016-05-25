/**
 * 
 */
package se.hig.programvaruteknik.database;

import java.util.List;
import java.util.Map;

public abstract class DataHandler
{
    public final Long saveData(String title, String data)
    {
	if (title == null) throw new DataHandlerException("Save: Missing title");
	if (data == null) throw new DataHandlerException("Save: Missing data");

	return _saveData(title, data);
    }

    protected abstract Long _saveData(String title, String json);

    public final String loadData(Long index)
    {
	if (index == null) throw new DataHandlerException("Load: Null is not a valid index");
	if (index < 0) throw new DataHandlerException("Load: Negative index is not valid");
	if (index == 0) throw new DataHandlerException("Load: Index begins at 1, not 0");

	return _loadData(index);
    }

    protected abstract String _loadData(Long index);

    public final Long deleteData(Long index)
    {
	if (index == null) throw new DataHandlerException("Delete: Null is not a valid index");
	if (index < 0) throw new DataHandlerException("Delete: Negative index is not valid");
	if (index == 0) throw new DataHandlerException("Delete: Index begins at 1, not 0");

	return _deleteData(index);
    }

    protected abstract Long _deleteData(Long index);

    public abstract List<Map<String, Object>> getList();

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
