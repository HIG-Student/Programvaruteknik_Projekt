package se.hig.programvaruteknik.database;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.hig.programvaruteknik.database.DataHandler.DataHandlerCannotCreateLoginException;
import se.hig.programvaruteknik.database.DataHandler.DataHandlerException;

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
	    protected Long _saveData(String title, String json)
	    {
		return 42L;
	    }

	    @Override
	    protected String _loadData(Long index)
	    {
		return "[data]";
	    }

	    @Override
	    protected Long _deleteData(Long index)
	    {
		return index;
	    }

	    @Override
	    public List<Map<String, Object>> getList()
	    {
		return list;
	    }

	    @Override
	    protected boolean _validateLogin(String username, String password)
	    {
		return (username == "gnu" && password == "gnu");
	    }

	    @Override
	    protected void _createLogin(String username, String password)
	    {

	    }

	    @Override
	    protected long _getUserId(String username)
	    {
		return 42;
	    }
	};
    }

    @Test(expected = DataHandler.DataHandlerException.class)
    public void testSaveWithNullTitle()
    {
	dataHandler.saveData(null, "[json]");
    }

    @Test(expected = DataHandler.DataHandlerException.class)
    public void testSaveWithNullData()
    {
	dataHandler.saveData("[title]", null);
    }

    @Test(expected = DataHandler.DataHandlerException.class)
    public void testLoadWhenNegativeInParameter()
    {
	dataHandler.loadData(-1L);
    }

    @Test(expected = DataHandler.DataHandlerException.class)
    public void testLoadWhenNullInParameter()
    {
	dataHandler.loadData(null);
    }

    @Test
    public void testLoad()
    {
	assertEquals("[data]", dataHandler.loadData(42L));
    }

    @Test
    public void testDelete()
    {
	assertEquals(new Long(1L), dataHandler.deleteData(1L));
    }

    @Test(expected = DataHandler.DataHandlerException.class)
    public void testDeleteWhenNegativeInParameter()
    {
	dataHandler.deleteData(-1L);
    }

    @Test(expected = DataHandler.DataHandlerException.class)
    public void testDeleteWhenNullInParameter()
    {
	dataHandler.deleteData(null);
    }

    @Test
    public void testSave()
    {
	assertEquals(new Long(42L), dataHandler.saveData("[title]", "[data]"));
    }

    @Test
    public void testLoginNoUsername()
    {
	assertEquals(false, dataHandler.validateLogin(null, "something"));
    }

    @Test
    public void testLoginNoPassword()
    {
	assertEquals(false, dataHandler.validateLogin("something", null));
    }

    @Test
    public void testInvalidLogin()
    {
	assertEquals(false, dataHandler.validateLogin("something", "something"));
    }

    @Test
    public void testValidLogin()
    {
	assertEquals(true, dataHandler.validateLogin("gnu", "gnu"));
    }

    @Test
    public void testCreateLogin()
    {
	dataHandler.createLogin("something", "something");
    }

    @Test(expected = DataHandlerCannotCreateLoginException.class)
    public void testCreateLoginNullName()
    {
	dataHandler.createLogin(null, "something");
    }

    @Test(expected = DataHandlerCannotCreateLoginException.class)
    public void testCreateLoginNullPassword()
    {
	dataHandler.createLogin("something", null);
    }

    @Test
    public void testGetUserId()
    {
	assertEquals(42L, dataHandler.getUserId("something"));
    }

    @Test(expected = DataHandlerException.class)
    public void testGetUserIdNameNull()
    {
	dataHandler.getUserId(null);
    }
}
