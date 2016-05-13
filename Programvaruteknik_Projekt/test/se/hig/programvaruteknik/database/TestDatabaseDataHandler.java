/**
 * 
 */
package se.hig.programvaruteknik.database;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
{
	DatabaseDataHandler.class
})
@SuppressWarnings("javadoc")
public class TestDatabaseDataHandler
{
    private Map<Long, String[]> database;
    private DatabaseDataHandler databaseHandler;

    private PreparedStatement makeLoadStatement() throws SQLException
    {
	PreparedStatement statement = mock(PreparedStatement.class);

	Store<Long> index = new Store<Long>(null);

	doAnswer(invocation ->
	{
	    index.value = invocation.getArgumentAt(1, Long.class);
	    return null;
	}).when(statement).setLong(Matchers.eq(1), Matchers.anyInt());

	doAnswer(a ->
	{
	    if (index.value == null) throw new SQLException();

	    Long i = index.value;
	    ResultSet resultSet = mock(ResultSet.class);
	    Store<Boolean> first = new Store<Boolean>(true);

	    doAnswer(b ->
	    {
		if (!first.value) throw new SQLException();
		first.value = false;
		return true;
	    }).when(resultSet).next();

	    doAnswer(b ->
	    {
		if (first.value) throw new SQLException();
		if (!database.containsKey(i)) throw new SQLException();
		return database.get(i)[1];
	    }).when(resultSet).getString("data");

	    return resultSet;
	}).when(statement).executeQuery();

	return statement;
    }

    private PreparedStatement makeSaveStatement() throws SQLException
    {
	PreparedStatement statement = mock(PreparedStatement.class);

	Store<String> title = new Store<String>(null);
	Store<String> data = new Store<String>(null);

	doAnswer(invocation ->
	{
	    title.value = invocation.getArgumentAt(1, String.class);
	    return null;
	}).when(statement).setString(Matchers.eq(1), Matchers.anyString());

	doAnswer(invocation ->
	{
	    data.value = invocation.getArgumentAt(1, String.class);
	    return null;
	}).when(statement).setString(Matchers.eq(2), Matchers.anyString());

	doAnswer(a ->
	{
	    if (title.value == null) throw new SQLException();
	    if (data.value == null) throw new SQLException();

	    String title_value = title.value;
	    String data_value = data.value;

	    Long index = (long) (database.size() + 1);

	    database.put(index, new String[]
	    {
		    title_value,
		    data_value
	    });

	    ResultSet resultSet = mock(ResultSet.class);
	    Store<Boolean> first = new Store<Boolean>(true);

	    doAnswer(b ->
	    {
		if (!first.value) throw new SQLException();
		first.value = false;
		return true;
	    }).when(resultSet).next();

	    doAnswer(b ->
	    {
		if (first.value) throw new SQLException();
		return index;
	    }).when(resultSet).getLong("id");

	    return resultSet;
	}).when(statement).executeQuery();

	return statement;
    }

    private Statement makeListStatement() throws SQLException
    {
	Statement statement = mock(PreparedStatement.class);

	doAnswer(a ->
	{
	    ResultSet resultSet = mock(ResultSet.class);
	    Store<Integer> index = new Store<Integer>(-1);

	    List<Entry<Long, String[]>> list = new ArrayList<>();
	    for (Entry<Long, String[]> entry : database.entrySet())
		list.add(entry);

	    doAnswer(b ->
	    {
		return ++index.value < list.size();
	    }).when(resultSet).next();

	    doAnswer(b ->
	    {
		return list.get(index.value).getKey();
	    }).when(resultSet).getLong("id");

	    doAnswer(b ->
	    {
		return list.get(index.value).getValue()[0];
	    }).when(resultSet).getString("title");

	    return resultSet;
	}).when(statement).executeQuery("SELECT (id,title) FROM data");

	return statement;
    }

    @Before
    public void setUp() throws Exception
    {
	database = new TreeMap<Long, String[]>();

	suppress(method(DatabaseDataHandler.class, "createConnection"));
	databaseHandler = spy(new DatabaseDataHandler());

	Connection connection = mock(Connection.class);

	doReturn(makeLoadStatement()).when(connection, "prepareStatement", "SELECT * FROM data WHERE id = ?");
	doReturn(makeSaveStatement()).when(
		connection,
		"prepareStatement",
		"INSERT INTO data(title, data) VALUES (?, ?) RETURNING id;");
	doReturn(makeListStatement()).when(connection).createStatement();

	Field f = DatabaseDataHandler.class.getDeclaredField("connection");
	f.setAccessible(true);
	f.set(databaseHandler, connection);
    }

    @Test
    public void testSave() throws Exception
    {
	assertEquals(new Long(1), databaseHandler.saveData("a", "aa"));
	assertEquals(new Long(2), databaseHandler.saveData("b", "bb"));
	assertEquals(new Long(3), databaseHandler.saveData("c", "cc"));
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

	assertEquals("aa", databaseHandler.loadData(1L));
	assertEquals("bb", databaseHandler.loadData(2L));
	assertEquals("cc", databaseHandler.loadData(3L));
    }

    @Test(expected = DatabaseDataHandler.DatabaseDataHandlerException.class)
    public void testLoadWhenNone() throws Exception
    {
	assertEquals("aa", databaseHandler.loadData(1L));
    }

    @Test(expected = DatabaseDataHandler.DatabaseDataHandlerException.class)
    public void testLoadOutOfIndex() throws Exception
    {
	database.put(1L, new String[]
	{
		"a",
		"aa"
	});

	assertEquals("aa", databaseHandler.loadData(2L));
    }

    @Test
    public void testEmptyGetList()
    {
	assertEquals(new ArrayList<Map<String, Object>>(), databaseHandler.getList());
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

	assertEquals(result, databaseHandler.getList());
    }

    class Store<T>
    {
	public T value;

	public Store(T value)
	{
	    this.value = value;
	}
    }
}
