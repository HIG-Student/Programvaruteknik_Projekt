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

@SuppressWarnings("javadoc")
public class TestMemoryDataHandler
{
    private MemoryDataHandler memoryDataHandler;
    private Map<Long, String[]> database;

    @Before
    public void setUp() throws Exception
    {
	memoryDataHandler = new MemoryDataHandler();

	Field f = MemoryDataHandler.class.getDeclaredField("database");
	f.setAccessible(true);
	database = (Map<Long, String[]>) f.get(memoryDataHandler);
    }

    @Test
    public void testSave() throws Exception
    {
	assertEquals(new Long(1), memoryDataHandler.saveData("a", "aa"));
	assertEquals(new Long(2), memoryDataHandler.saveData("b", "bb"));
	assertEquals(new Long(3), memoryDataHandler.saveData("c", "cc"));
	assertEquals(new Integer(3), (Integer) database.size());
	assertArrayEquals(new String[]
	{
		"a",
		"aa"
	}, database.get(1L));
	assertArrayEquals(new String[]
	{
		"b",
		"bb"
	}, database.get(2L));
	assertArrayEquals(new String[]
	{
		"c",
		"cc"
	}, database.get(3L));
    }

    @Test
    public void testLoad() throws Exception
    {
	database.put(1L, new String[]
	{
		"a",
		"aa"
	});
	database.put(2L, new String[]
	{
		"b",
		"bb"
	});
	database.put(3L, new String[]
	{
		"c",
		"cc"
	});

	assertEquals("aa", memoryDataHandler.loadData(1L));
	assertEquals("bb", memoryDataHandler.loadData(2L));
	assertEquals("cc", memoryDataHandler.loadData(3L));
    }

    @Test(expected = MemoryDataHandler.MemoryDataHandlerException.class)
    public void testLoadWhenNone() throws Exception
    {
	assertEquals("aa", memoryDataHandler.loadData(1L));
    }

    @Test(expected = MemoryDataHandler.MemoryDataHandlerException.class)
    public void testLoadOutOfIndex() throws Exception
    {
	database.put(1L, new String[]
	{
		"a",
		"aa"
	});

	assertEquals("aa", memoryDataHandler.loadData(2L));
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
	for (Entry<Long, String[]> entry : database.entrySet())
	{
	    Map<String, Object> value = new TreeMap<>();
	    value.put("id", entry.getKey());
	    value.put("title", entry.getValue()[0]);
	    result.add(value);
	}

	assertEquals(result, memoryDataHandler.getList());
    }
}
