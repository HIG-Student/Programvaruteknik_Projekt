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
	if (title == null) throw new DataSaverException("Save: Missing title");
	if (data == null) throw new DataSaverException("Save: Missing data");

	return saveData(title, data);
    }

    protected abstract Long saveData(String title, String json);

    public final String load(Long index)
    {
	if (index == null) throw new DataSaverException("Load: Null is not a valid index");
	if (index < 0) throw new DataSaverException("Load: Negative index is not valid");
	if (index == 0) throw new DataSaverException("Load: Index begins at 1, not 0");

	return loadData(index);
    }

    protected abstract String loadData(Long index);

    public abstract List<Map<String, Object>> getList();

    @SuppressWarnings("serial")
    public class DataSaverException extends RuntimeException
    {
	public DataSaverException()
	{

	}

	public DataSaverException(String message)
	{
	    super(message);
	}

	public DataSaverException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
