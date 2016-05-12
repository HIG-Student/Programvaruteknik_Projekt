package se.hig.programvaruteknik.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestDataHandler
{
    List<Map<String, Object>> list;
    DataHandler dataHandler;

    @Before
    public void setUp() throws Exception
    {
	list = new ArrayList<Map<String, Object>>();

	dataHandler = new DataHandler()
	{
	    @Override
	    protected Long saveData(String title, String json)
	    {
		return 42L;
	    }

	    @Override
	    protected String loadData(Long index)
	    {
		return "[data]";
	    }

	    @Override
	    public List<Map<String, Object>> getList()
	    {
		return list;
	    }
	};
    }

    @Test(expected = DataHandler.DataSaverException.class)
    public void testSaveWithNullTitle()
    {
	dataHandler.save(null, "[json]");
    }

    @Test(expected = DataHandler.DataSaverException.class)
    public void testSaveWithNullData()
    {
	dataHandler.save("[title]", null);
    }

    @Test(expected = DataHandler.DataSaverException.class)
    public void testLoadWhenNegativeInParameter()
    {
	dataHandler.load(-1L);
    }

    @Test(expected = DataHandler.DataSaverException.class)
    public void testLoadWhenNullInParameter()
    {
	dataHandler.load(null);
    }
}
