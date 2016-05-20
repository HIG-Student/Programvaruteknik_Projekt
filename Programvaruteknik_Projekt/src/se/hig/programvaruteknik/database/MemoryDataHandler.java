/**
 * 
 */
package se.hig.programvaruteknik.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.hig.programvaruteknik.database.DataHandler.DataHandlerException;

import java.util.TreeMap;

public class MemoryDataHandler extends DataHandler
{
    private Map<Long, String[]> data_database = new TreeMap<Long, String[]>();
    private Map<String, String> user_database = new TreeMap<String, String>();

    @Override
    protected Long saveData(String title, String json)
    {
	Long index = (long) data_database.size() + 1;
	data_database.put(index, new String[]
	{
		title,
		json
	});

	return index;
    }

    @Override
    protected String loadData(Long index)
    {
	if (!data_database
		.containsKey(index)) throw new MemoryDataHandlerException("No value at index [" + index + "]");

	try
	{
	    return data_database.get(index)[1];
	}
	catch (Throwable t)
	{
	    throw new MemoryDataHandlerException(t);
	}
    }

    @Override
    protected Long deleteData(Long index)
    {
	if (!data_database
		.containsKey(index)) throw new MemoryDataHandlerException("No value at index [" + index + "]");

	try
	{
	    data_database.remove(index);
	    return index;
	}
	catch (Throwable t)
	{
	    throw new MemoryDataHandlerException(t);
	}
    }

    @Override
    public List<Map<String, Object>> getList()
    {
	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
	for (Entry<Long, String[]> database_entry : data_database.entrySet())
	{
	    Map<String, Object> entry = new TreeMap<>();
	    entry.put("id", database_entry.getKey());
	    entry.put("title", database_entry.getValue()[0]);
	    result.add(entry);
	}
	return result;
    }

    @Override
    protected boolean validateCredentials(String username, String password)
    {
	if (!user_database.containsKey(username)) return false;

	return user_database.get(username).equals(password);
    }

    @Override
    protected void createCredentials(String username, String password) throws DataHandlerCannotCreateLoginException
    {
	if (user_database.containsKey(username)) throw new DataHandlerCannotCreateLoginException("User already exsist");
	user_database.put(username, password);
    }

    public class MemoryDataHandlerException extends DataHandlerException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MemoryDataHandlerException()
	{

	}

	public MemoryDataHandlerException(String message)
	{
	    super(message);
	}

	public MemoryDataHandlerException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
