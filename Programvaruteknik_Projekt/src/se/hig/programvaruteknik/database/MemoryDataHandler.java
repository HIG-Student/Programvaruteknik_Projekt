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
    private Map<Long, String[]> database = new TreeMap<Long, String[]>();

    @Override
    protected Long saveData(String title, String json)
    {
	Long index = (long) database.size() + 1;
	database.put(index, new String[]
	{
		title,
		json
	});

	return index;
    }

    @Override
    protected String loadData(Long index)
    {
	if (!database.containsKey(index)) throw new MemoryDataHandlerException("No value at index [" + index + "]");

	try
	{
	    return database.get(index)[1];
	}
	catch (Throwable t)
	{
	    throw new MemoryDataHandlerException(t);
	}
    }

    @Override
    protected Long deleteData(Long index)
    {
	if (!database.containsKey(index)) throw new MemoryDataHandlerException("No value at index [" + index + "]");

	try
	{
	    database.remove(index);
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
	for (Entry<Long, String[]> database_entry : database.entrySet())
	{
	    Map<String, Object> entry = new TreeMap<>();
	    entry.put("id", database_entry.getKey());
	    entry.put("title", database_entry.getValue()[0]);
	    result.add(entry);
	}
	return result;
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
