package se.hig.programvaruteknik.database;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.verification.MockAwareVerificationMode;

import se.hig.programvaruteknik.database.DataHandler.DataHandlerCannotCreateLoginException;

@SuppressWarnings("javadoc")
public class TestMemoryDataHandler
{
    private MemoryDataHandler memoryDataHandler;
    private Map<Long, String[]> data_database;

    private Map<String, String> user_database;
    private Map<String, Long> user_id_database;

    @Before
    public void setUp() throws Exception
    {
	memoryDataHandler = new MemoryDataHandler();

	{
	    Field f = MemoryDataHandler.class.getDeclaredField("data_database");
	    f.setAccessible(true);
	    data_database = (Map<Long, String[]>) f.get(memoryDataHandler);
	}

	{
	    Field f = MemoryDataHandler.class.getDeclaredField("user_database");
	    f.setAccessible(true);
	    user_database = (Map<String, String>) f.get(memoryDataHandler);
	}

	{
	    Field f = MemoryDataHandler.class.getDeclaredField("user_id_database");
	    f.setAccessible(true);
	    user_id_database = (Map<String, Long>) f.get(memoryDataHandler);
	}
    }

    @Test
    public void testSave() throws Exception
    {
	assertEquals(new Long(1), memoryDataHandler._saveData("a", "aa"));
	assertEquals(new Long(2), memoryDataHandler._saveData("b", "bb"));
	assertEquals(new Long(3), memoryDataHandler._saveData("c", "cc"));
	assertEquals(new Integer(3), (Integer) data_database.size());
	assertArrayEquals(new String[]
	{
		"a",
		"aa"
	}, data_database.get(1L));
	assertArrayEquals(new String[]
	{
		"b",
		"bb"
	}, data_database.get(2L));
	assertArrayEquals(new String[]
	{
		"c",
		"cc"
	}, data_database.get(3L));
    }

    @Test
    public void testLoad() throws Exception
    {
	data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});
	data_database.put(2L, new String[]
	{
		"b",
		"bb"
	});
	data_database.put(3L, new String[]
	{
		"c",
		"cc"
	});

	assertEquals("aa", memoryDataHandler._loadData(1L));
	assertEquals("bb", memoryDataHandler._loadData(2L));
	assertEquals("cc", memoryDataHandler._loadData(3L));
    }

    @Test(expected = MemoryDataHandler.MemoryDataHandlerException.class)
    public void testLoadWhenNone() throws Exception
    {
	memoryDataHandler._loadData(1L);
    }

    @Test(expected = MemoryDataHandler.MemoryDataHandlerException.class)
    public void testLoadOutOfIndex() throws Exception
    {
	data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});

	assertEquals("aa", memoryDataHandler._loadData(2L));
    }

    @Test
    public void testDelete() throws Exception
    {
	data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});
	data_database.put(2L, new String[]
	{
		"b",
		"bb"
	});
	data_database.put(3L, new String[]
	{
		"c",
		"cc"
	});

	assertEquals(new Long(1L), memoryDataHandler._deleteData(1L));
	assertEquals(new Long(3L), memoryDataHandler._deleteData(3L));
	assertEquals(new Long(2L), memoryDataHandler._deleteData(2L));
	assertEquals(0, data_database.size());
    }

    @Test(expected = MemoryDataHandler.MemoryDataHandlerException.class)
    public void testDeleteWhenNone() throws Exception
    {
	memoryDataHandler.deleteData(1L);
    }

    @Test(expected = MemoryDataHandler.MemoryDataHandlerException.class)
    public void testDeleteOutOfIndex() throws Exception
    {
	data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});

	memoryDataHandler._loadData(2L);
    }

    @Test
    public void testEmptyGetList()
    {
	assertEquals(new ArrayList<Map<String, Object>>(), memoryDataHandler.getList());
    }

    @Test
    public void testList()
    {
	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
	for (Entry<Long, String[]> entry : data_database.entrySet())
	{
	    Map<String, Object> value = new TreeMap<>();
	    value.put("id", entry.getKey());
	    value.put("title", entry.getValue()[0]);
	    result.add(value);
	}

	assertEquals(result, memoryDataHandler.getList());
    }

    @Test
    public void testInvalidUserLogin()
    {
	assertEquals(false, memoryDataHandler._validateLogin("something", "something"));
    }

    @Test
    public void testInvalidPasswordLogin()
    {
	user_database.put("something", "nothing");
	assertEquals(false, memoryDataHandler._validateLogin("something", "something"));
    }

    @Test
    public void testValidLogin()
    {
	user_database.put("something", "something");
	assertEquals(true, memoryDataHandler._validateLogin("something", "something"));
    }

    @Test
    public void testCreateLogin()
    {
	memoryDataHandler.createLogin("something", "something_p");
	assertEquals(user_database.get("something"), "something_p");
    }

    @Test(expected = DataHandlerCannotCreateLoginException.class)
    public void testCreateExsistingLogin()
    {
	user_database.put("something", "something_p");
	memoryDataHandler.createLogin("something", "something_p");
    }

    @Test(expected = MemoryDataHandler.MemoryDataHandlerException.class)
    public void testGetUserIdNoUser()
    {
	memoryDataHandler._getUserId("IAmNotAUser");
    }

    @Test
    public void testGetUserId()
    {
	memoryDataHandler._createLogin("user_A", "NoPass");
	assertEquals(1L, memoryDataHandler._getUserId("user_A"));

	memoryDataHandler._createLogin("user_B", "NoPass");
	assertEquals(2L, memoryDataHandler._getUserId("user_B"));

	memoryDataHandler._createLogin("user_C", "NoPass");
	assertEquals(3L, memoryDataHandler._getUserId("user_C"));
    }
}