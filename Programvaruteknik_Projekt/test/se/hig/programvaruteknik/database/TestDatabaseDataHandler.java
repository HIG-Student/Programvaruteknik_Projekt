/**
 * 
 */
package se.hig.programvaruteknik.database;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import se.hig.programvaruteknik.database.DatabaseDataHandler.DataConnection;
import se.hig.programvaruteknik.database.DatabaseDataHandler.DatabaseException;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
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

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
{
	DatabaseDataHandler.class
})
@PowerMockIgnore("javax.crypto.*")
@SuppressWarnings("javadoc")
public class TestDatabaseDataHandler
{
    private static int ITERATION_COUNT_FOR_HASH = 1;
    private static int KEY_LENGTH_FOR_HASH = 10;

    private Map<Long, String[]> data_database;
    private Map<String, byte[][]> user_database;
    private Map<String, Long> user_id_mapping;
    private Long next_user_id = 1L;

    private DatabaseDataHandler databaseHandler;

    private PreparedStatement makeDeleteStatement() throws SQLException
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

	    if (!data_database.containsKey(index.value)) throw new SQLException();
	    data_database.remove(index.value);

	    Long i = index.value;
	    ResultSet resultSet = mock(ResultSet.class);
	    Store<Boolean> first = new Store<Boolean>(true);

	    doAnswer(b ->
	    {
		boolean was_first = first.value;
		first.value = false;
		return was_first;
	    }).when(resultSet).next();

	    doAnswer(b ->
	    {
		if (first.value) throw new SQLException();
		return i;
	    }).when(resultSet).getLong("id");

	    return 1L;
	}).when(statement).executeUpdate();

	return statement;
    }

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
		boolean was_first = first.value;
		first.value = false;
		return was_first;
	    }).when(resultSet).next();

	    doAnswer(b ->
	    {
		if (first.value) throw new SQLException();
		if (!data_database.containsKey(i)) throw new SQLException();
		return data_database.get(i)[1];
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

	    Long index = (long) (data_database.size() + 1);

	    data_database.put(index, new String[]
	    {
		    title_value,
		    data_value
	    });

	    ResultSet resultSet = mock(ResultSet.class);
	    Store<Boolean> first = new Store<Boolean>(true);

	    doAnswer(b ->
	    {
		boolean was_first = first.value;
		first.value = false;
		return was_first;
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
	PreparedStatement statement = mock(PreparedStatement.class);

	doAnswer(a ->
	{
	    ResultSet resultSet = mock(ResultSet.class);
	    Store<Integer> index = new Store<Integer>(-1);

	    List<Entry<Long, String[]>> list = new ArrayList<>();
	    for (Entry<Long, String[]> entry : data_database.entrySet())
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
	}).when(statement).executeQuery();

	return statement;
    }

    private PreparedStatement makeCreateCredentialsStatement() throws SQLException
    {
	PreparedStatement statement = mock(PreparedStatement.class);

	Store<String> name = new Store<String>(null);
	Store<byte[]> hash = new Store<byte[]>(null);
	Store<byte[]> salt = new Store<byte[]>(null);

	doAnswer(invocation ->
	{
	    name.value = invocation.getArgumentAt(1, String.class);
	    return null;
	}).when(statement).setString(Matchers.eq(1), Matchers.anyString());

	doAnswer(invocation ->
	{
	    hash.value = invocation.getArgumentAt(1, byte[].class);
	    return null;
	}).when(statement).setBytes(Matchers.eq(2), Matchers.any(byte[].class));

	doAnswer(invocation ->
	{
	    salt.value = invocation.getArgumentAt(1, byte[].class);
	    return null;
	}).when(statement).setBytes(Matchers.eq(3), Matchers.any(byte[].class));

	doAnswer(a ->
	{
	    if (name.value == null) throw new SQLException();
	    if (hash.value == null) throw new SQLException();
	    if (salt.value == null) throw new SQLException();

	    user_id_mapping.put(name.value, next_user_id++);
	    user_database.put(name.value, new byte[][]
	    {
		    hash.value,
		    salt.value
	    });

	    return 1;
	}).when(statement).executeUpdate();

	return statement;
    }

    private PreparedStatement makeGetUserStatement() throws SQLException
    {
	PreparedStatement statement = mock(PreparedStatement.class);

	Store<String> name = new Store<String>(null);

	doAnswer(invocation ->
	{
	    name.value = invocation.getArgumentAt(1, String.class);
	    return null;
	}).when(statement).setString(Matchers.eq(1), Matchers.anyString());

	doAnswer(a ->
	{
	    if (name.value == null) throw new SQLException();
	    String username = name.value;

	    ResultSet resultSet = mock(ResultSet.class);

	    if (!user_database.containsKey(username))
	    {
		doAnswer(b ->
		{
		    return false;
		}).when(resultSet).next();
	    }
	    else
	    {
		Store<Boolean> first = new Store<Boolean>(true);

		doAnswer(b ->
		{
		    boolean was_first = first.value;
		    first.value = false;
		    return was_first;
		}).when(resultSet).next();

		doAnswer(b ->
		{
		    if (first.value) throw new SQLException();
		    return user_id_mapping.get(username);
		}).when(resultSet).getLong("id");

		doAnswer(b ->
		{
		    if (first.value) throw new SQLException();
		    return username;
		}).when(resultSet).getString("name");

		doAnswer(b ->
		{
		    if (first.value) throw new SQLException();
		    return user_database.get(username)[0];
		}).when(resultSet).getBytes("hash");

		doAnswer(b ->
		{
		    if (first.value) throw new SQLException();
		    return user_database.get(username)[1];
		}).when(resultSet).getBytes("salt");
	    }
	    return resultSet;
	}).when(statement).executeQuery();

	return statement;
    }

    @Before
    public void setUp() throws Exception
    {
	next_user_id = 1L;

	data_database = new TreeMap<Long, String[]>();
	user_database = new TreeMap<String, byte[][]>();
	user_id_mapping = new TreeMap<String, Long>();

	Connection connection = mock(Connection.class);

	doReturn(makeCreateCredentialsStatement()).when(
		connection,
		"prepareStatement",
		"INSERT INTO users(name, hash, salt) VALUES (?, ?, ?)");

	doReturn(makeLoadStatement()).when(connection, "prepareStatement", "SELECT * FROM saves WHERE id = ?");
	doReturn(makeSaveStatement()).when(
		connection,
		"prepareStatement",
		"INSERT INTO saves(title, data) VALUES (?, ?) RETURNING id;");
	doReturn(makeDeleteStatement()).when(connection, "prepareStatement", "DELETE FROM saves WHERE id = ?");
	doReturn(makeListStatement()).when(connection, "prepareStatement", "SELECT id,title FROM saves");

	doReturn(makeGetUserStatement()).when(connection, "prepareStatement", "SELECT * from users WHERE name = ?");

	DataConnection dataConnection = mock(DataConnection.class);
	when(dataConnection.getConnection()).thenReturn(connection);

	databaseHandler = spy(new DatabaseDataHandler(dataConnection));

	Whitebox.setInternalState(DatabaseDataHandler.class, "ITERATION_COUNT_FOR_HASH", ITERATION_COUNT_FOR_HASH);
	Whitebox.setInternalState(DatabaseDataHandler.class, "KEY_LENGTH_FOR_HASH", KEY_LENGTH_FOR_HASH);
    }

    @Test
    public void testSave() throws Exception
    {
	assertEquals(new Long(1), databaseHandler._saveData("a", "aa"));
	assertEquals(new Long(2), databaseHandler._saveData("b", "bb"));
	assertEquals(new Long(3), databaseHandler._saveData("c", "cc"));
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

	assertEquals("aa", databaseHandler._loadData(1L));
	assertEquals("bb", databaseHandler._loadData(2L));
	assertEquals("cc", databaseHandler._loadData(3L));
    }

    @Test(expected = DatabaseDataHandler.DatabaseException.class)
    public void testLoadWhenNone() throws Exception
    {
	assertEquals("aa", databaseHandler._loadData(1L));
    }

    @Test(expected = DatabaseDataHandler.DatabaseException.class)
    public void testLoadOutOfIndex() throws Exception
    {
	data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});

	assertEquals("aa", databaseHandler._loadData(2L));
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

	assertEquals(new Long(1L), databaseHandler._deleteData(1L));
	assertEquals(new Long(3L), databaseHandler._deleteData(3L));
	assertEquals(new Long(2L), databaseHandler._deleteData(2L));
	assertEquals(0, data_database.size());
    }

    @Test(expected = DatabaseDataHandler.DatabaseException.class)
    public void testDeleteWhenNone() throws Exception
    {
	databaseHandler.deleteData(1L);
    }

    @Test(expected = DatabaseDataHandler.DatabaseException.class)
    public void testDeleteOutOfIndex() throws Exception
    {
	data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});

	databaseHandler._loadData(2L);
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
	for (Entry<Long, String[]> entry : data_database.entrySet())
	{
	    Map<String, Object> value = new TreeMap<>();
	    value.put("id", entry.getKey());
	    value.put("title", entry.getValue()[0]);
	    result.add(value);
	}

	assertEquals(result, databaseHandler.getList());
    }

    @Test
    public void testCreateLogin() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
	String password = "something_p";

	databaseHandler.createLogin("something", password);
	assertEquals(1, user_database.size());
	assertEquals(true, user_database.containsKey("something"));

	// Test hashing

	byte[][] user_data = user_database.get("something");

	byte[] salt = user_data[1];

	SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
	PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT_FOR_HASH, KEY_LENGTH_FOR_HASH);
	SecretKey key = skf.generateSecret(spec);
	byte[] hash = key.getEncoded();

	assertArrayEquals(hash, user_data[0]);
    }

    @Test(expected = DatabaseDataHandler.UserAlreadyExistsException.class)
    public void testCreateExistingLogin()
    {
	databaseHandler._createLogin("something", "something_p");
	databaseHandler._createLogin("something", "something_else_p");
    }

    @Test
    public void testValidateLoginDoesNotExist()
    {
	assertEquals(false, databaseHandler._validateLogin("no", "yes"));
    }

    @Test
    public void testValidateLoginOk()
    {
	databaseHandler._createLogin("Ok", "NoPass");
	assertEquals(true, databaseHandler._validateLogin("Ok", "NoPass"));
    }

    @Test(expected = DatabaseDataHandler.DatabaseException.class)
    public void testGetUserIdNoUser()
    {
	databaseHandler._getUserId("GNU");
    }

    @Test
    public void testGetUserIdOk()
    {
	databaseHandler._createLogin("Ok", "NoPass");
	assertEquals(1L, databaseHandler._getUserId("Ok"));

	databaseHandler._createLogin("Ok2", "NoPass");
	assertEquals(2L, databaseHandler._getUserId("Ok2"));

	databaseHandler._createLogin("Ok3", "NoPass");
	assertEquals(3L, databaseHandler._getUserId("Ok3"));
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
