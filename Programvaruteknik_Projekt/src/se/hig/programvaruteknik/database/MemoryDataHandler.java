/**
 * 
 */
package se.hig.programvaruteknik.database;

import java.util.Map;
import java.util.TreeMap;

public class MemoryDataHandler implements DataHandler
{
    private Map<Long, String[]> database = new TreeMap<Long, String[]>();

    @Override
    public Long save(String title, String json)
    {
	if (title == null) throw new DataSaverException("Missing title");

	if (json == null) throw new DataSaverException("Missing data");

	Long index = (long) database.size();
	database.put(index, new String[]
	{
		title,
		json
	});

	return index;
    }

    @Override
    public String load(Long index)
    {
	try
	{
	    return database.get(index)[1];
	}
	catch (Throwable t)
	{
	    throw new DataSaverException(t);
	}
    }
}
