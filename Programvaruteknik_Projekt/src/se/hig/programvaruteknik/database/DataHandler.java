/**
 * 
 */
package se.hig.programvaruteknik.database;

import java.util.List;
import java.util.Map;

public abstract class DataHandler
{
    public final Long save(String title, String data)
    {
	if (title == null) throw new DataHandlerException("Save: Missing title");
	if (data == null) throw new DataHandlerException("Save: Missing data");

	return saveData(title, data);
    }

    protected abstract Long saveData(String title, String json);

    public final String load(Long index)
    {
	if (index == null) throw new DataHandlerException("Load: Null is not a valid index");
	if (index < 0) throw new DataHandlerException("Load: Negative index is not valid");
	if (index == 0) throw new DataHandlerException("Load: Index begins at 1, not 0");

	return loadData(index);
    }

    protected abstract String loadData(Long index);

    public final Long delete(Long index)
    {
	if (index == null) throw new DataHandlerException("Delete: Null is not a valid index");
	if (index < 0) throw new DataHandlerException("Delete: Negative index is not valid");
	if (index == 0) throw new DataHandlerException("Delete: Index begins at 1, not 0");

	return deleteData(index);
    }

    protected abstract Long deleteData(Long index);

    public abstract List<Map<String, Object>> getList();

    @SuppressWarnings("serial")
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
}
