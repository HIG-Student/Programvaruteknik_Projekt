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

    private class DatabaseUser
    {
	public Long id;
	public String name;
	public byte[] hash;
	public byte[] salt;

	public Long current_database_id = 1L;
	public Map<Long, String[]> data_database = new TreeMap<Long, String[]>();
    }

    private DatabaseUser __createUser(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
	return __createUser(username, password, current_user_id++);
    }

    private DatabaseUser __createUser(String username, String password, Long user_id) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
	return new DatabaseUser()
	{
	    {
		this.id = user_id;

		this.name = username;

		SecureRandom random = new SecureRandom();
		this.salt = new byte[64];
		random.nextBytes(this.salt);

		SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec(
			password.toCharArray(),
			salt,
			ITERATION_COUNT_FOR_HASH,
			KEY_LENGTH_FOR_HASH);
		SecretKey key = skf.generateSecret(spec);
		this.hash = key.getEncoded();

		user_name_database.put(this.name, this);
		user_id_database.put(this.id, this);
	    }
	};
    }

    private DatabaseUser __getUser(String name)
    {
	return user_name_database.get(name);
    }

    private DatabaseUser __getUser(Long id)
    {
	return user_id_database.get(id);
    }

    private Map<String, DatabaseUser> user_name_database;
    private Map<Long, DatabaseUser> user_id_database;
    private Long current_user_id;

    DatabaseUser testUser;

    private DatabaseDataHandler databaseHandler;

    private PreparedStatement makeDeleteStatement() throws SQLException
    {
	PreparedStatement statement = mock(PreparedStatement.class);

	Store<Long> user_id = new Store<Long>(null);
	Store<Long> index = new Store<Long>(null);

	doAnswer(invocation ->
	{
	    index.value = invocation.getArgumentAt(1, Long.class);
	    return null;
	}).when(statement).setLong(Matchers.eq(1), Matchers.anyInt());

	doAnswer(invocation ->
	{
	    user_id.value = invocation.getArgumentAt(1, Long.class);
	    return null;
	}).when(statement).setLong(Matchers.eq(2), Matchers.anyInt());

	doAnswer(a ->
	{
	    if (user_id.value == null) throw new SQLException();
	    if (index.value == null) throw new SQLException();

	    DatabaseUser user = __getUser(user_id.value);
	    if (user == null) throw new SQLException("No such user, have the Gnu been eating them again?");

	    user.data_database.remove(index.value);

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

	Store<Long> user_id = new Store<Long>(null);
	Store<Long> index = new Store<Long>(null);
	doAnswer(invocation ->
	{
	    index.value = invocation.getArgumentAt(1, Long.class);
	    return null;
	}).when(statement).setLong(Matchers.eq(1), Matchers.anyInt());

	doAnswer(invocation ->
	{
	    user_id.value = invocation.getArgumentAt(1, Long.class);
	    return null;
	}).when(statement).setLong(Matchers.eq(2), Matchers.anyInt());

	doAnswer(a ->
	{
	    if (user_id.value == null) throw new SQLException();
	    if (index.value == null) throw new SQLException();

	    DatabaseUser user = __getUser(user_id.value);
	    if (user == null) throw new SQLException("No such user, have the Gnu been eating them again?");

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
		if (!user.data_database.containsKey(i)) throw new SQLException();
		return user.data_database.get(i)[1];
	    }).when(resultSet).getString("data");

	    return resultSet;
	}).when(statement).executeQuery();

	return statement;
    }

    private PreparedStatement makeSaveStatement() throws SQLException
    {
	PreparedStatement statement = mock(PreparedStatement.class);

	Store<Long> user_id = new Store<Long>(null);
	Store<String> title = new Store<String>(null);
	Store<String> data = new Store<String>(null);

	doAnswer(invocation ->
	{
	    user_id.value = invocation.getArgumentAt(1, Long.class);
	    return null;
	}).when(statement).setLong(Matchers.eq(1), Matchers.anyLong());

	doAnswer(invocation ->
	{
	    title.value = invocation.getArgumentAt(1, String.class);
	    return null;
	}).when(statement).setString(Matchers.eq(2), Matchers.anyString());

	doAnswer(invocation ->
	{
	    data.value = invocation.getArgumentAt(1, String.class);
	    return null;
	}).when(statement).setString(Matchers.eq(3), Matchers.anyString());

	doAnswer(a ->
	{
	    if (user_id.value == null) throw new SQLException();
	    if (title.value == null) throw new SQLException();
	    if (data.value == null) throw new SQLException();

	    Long user_id_value = user_id.value;
	    String title_value = title.value;
	    String data_value = data.value;

	    DatabaseUser user = __getUser(user_id.value);
	    if (user == null) throw new SQLException("No such user, have the Gnu been eating them again?");

	    Long index = user.current_database_id++;

	    user.data_database.put(index, new String[]
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

	Store<Long> user_id = new Store<Long>(null);

	doAnswer(invocation ->
	{
	    user_id.value = invocation.getArgumentAt(1, Long.class);
	    return null;
	}).when(statement).setLong(Matchers.eq(1), Matchers.anyLong());

	doAnswer(a ->
	{
	    ResultSet resultSet = mock(ResultSet.class);
	    Store<Integer> index = new Store<Integer>(-1);

	    DatabaseUser user = __getUser(user_id.value);
	    if (user == null) throw new SQLException("No such user, have the Gnu been eating them again?");

	    List<Entry<Long, String[]>> list = new ArrayList<>();
	    for (Entry<Long, String[]> entry : user.data_database.entrySet())
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

	Store<String> _name = new Store<String>(null);
	Store<byte[]> _hash = new Store<byte[]>(null);
	Store<byte[]> _salt = new Store<byte[]>(null);

	doAnswer(invocation ->
	{
	    _name.value = invocation.getArgumentAt(1, String.class);
	    return null;
	}).when(statement).setString(Matchers.eq(1), Matchers.anyString());

	doAnswer(invocation ->
	{
	    _hash.value = invocation.getArgumentAt(1, byte[].class);
	    return null;
	}).when(statement).setBytes(Matchers.eq(2), Matchers.any(byte[].class));

	doAnswer(invocation ->
	{
	    _salt.value = invocation.getArgumentAt(1, byte[].class);
	    return null;
	}).when(statement).setBytes(Matchers.eq(3), Matchers.any(byte[].class));

	doAnswer(a ->
	{
	    if (_name.value == null) throw new SQLException();
	    if (_hash.value == null) throw new SQLException();
	    if (_salt.value == null) throw new SQLException();

	    DatabaseUser user = new DatabaseUser();
	    user.id = current_user_id++;
	    user.name = _name.value;
	    user.hash = _hash.value;
	    user.salt = _salt.value;

	    user_name_database.put(user.name, user);
	    user_id_database.put(user.id, user);

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

	    ResultSet resultSet = mock(ResultSet.class);

	    DatabaseUser user = __getUser(name.value);

	    if (user == null)
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
		    return user.id;
		}).when(resultSet).getLong("id");

		doAnswer(b ->
		{
		    if (first.value) throw new SQLException();
		    return user.name;
		}).when(resultSet).getString("name");

		doAnswer(b ->
		{
		    if (first.value) throw new SQLException();
		    return user.hash;
		}).when(resultSet).getBytes("hash");

		doAnswer(b ->
		{
		    if (first.value) throw new SQLException("No name set!");
		    return user.salt;
		}).when(resultSet).getBytes("salt");
	    }
	    return resultSet;
	}).when(statement).executeQuery();

	return statement;
    }

    @Before
    public void setUp() throws Exception
    {
	user_id_database = new TreeMap<>();
	user_name_database = new TreeMap<>();
	current_user_id = 1L;

	testUser = __createUser("GNU", "GNU is not my password");

	Connection connection = mock(Connection.class);

	doReturn(makeCreateCredentialsStatement()).when(
		connection,
		"prepareStatement",
		"INSERT INTO users(name, hash, salt) VALUES (?, ?, ?)");

	doReturn(makeLoadStatement()).when(
		connection,
		"prepareStatement",
		"SELECT * FROM saves WHERE id = ? AND author = ?");

	doReturn(makeSaveStatement()).when(
		connection,
		"prepareStatement",
		"INSERT INTO saves(author, title, data) VALUES (?, ?, ?) RETURNING id;");

	doReturn(makeDeleteStatement()).when(
		connection,
		"prepareStatement",
		"DELETE FROM saves WHERE id = ? AND author = ?");

	doReturn(makeListStatement()).when(
		connection,
		"prepareStatement",
		"SELECT id,title FROM saves WHERE author = ?");

	doReturn(makeGetUserStatement()).when(connection, "prepareStatement", "SELECT * from users WHERE name = ?");

	DataConnection dataConnection = mock(DataConnection.class);
	when(dataConnection.getConnection()).thenReturn(connection);

	databaseHandler = spy(new DatabaseDataHandler(dataConnection));

	Whitebox.setInternalState(DatabaseDataHandler.class, "ITERATION_COUNT_FOR_HASH", ITERATION_COUNT_FOR_HASH);
	Whitebox.setInternalState(DatabaseDataHandler.class, "KEY_LENGTH_FOR_HASH", KEY_LENGTH_FOR_HASH);
    }

    @Test(expected = DatabaseException.class)
    public void testSaveNoUser() throws Exception
    {
	databaseHandler._saveData(5L, "a", "aa");
    }

    @Test
    public void testSave() throws Exception
    {
	assertEquals(new Long(1), databaseHandler._saveData(testUser.id, "a", "aa"));
	assertEquals(new Long(2), databaseHandler._saveData(testUser.id, "b", "bb"));
	assertEquals(new Long(3), databaseHandler._saveData(testUser.id, "c", "cc"));
	assertEquals(new Integer(3), (Integer) testUser.data_database.size());

	assertArrayEquals(new String[]
	{
		"a",
		"aa"
	}, testUser.data_database.get(1L));
	assertArrayEquals(new String[]
	{
		"b",
		"bb"
	}, testUser.data_database.get(2L));
	assertArrayEquals(new String[]
	{
		"c",
		"cc"
	}, testUser.data_database.get(3L));
    }

    @Test(expected = Throwable.class)
    public void testLoadNoUser() throws Exception
    {
	databaseHandler._loadData(5L, 1L);
    }

    @Test
    public void testLoad() throws Exception
    {
	testUser.data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});
	testUser.data_database.put(2L, new String[]
	{
		"b",
		"bb"
	});
	testUser.data_database.put(3L, new String[]
	{
		"c",
		"cc"
	});

	assertEquals("aa", databaseHandler._loadData(testUser.id, 1L));
	assertEquals("bb", databaseHandler._loadData(testUser.id, 2L));
	assertEquals("cc", databaseHandler._loadData(testUser.id, 3L));
    }

    @Test(expected = DatabaseDataHandler.DatabaseException.class)
    public void testLoadOutOfIndex() throws Exception
    {
	databaseHandler._loadData(testUser.id, 1L);
    }

    @Test(expected = Throwable.class)
    public void testDeleteNoUser() throws Exception
    {
	databaseHandler._deleteData(5L, 1L);
    }

    @Test
    public void testDelete() throws Exception
    {
	testUser.data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});
	testUser.data_database.put(2L, new String[]
	{
		"b",
		"bb"
	});
	testUser.data_database.put(3L, new String[]
	{
		"c",
		"cc"
	});

	assertEquals(new Long(1L), databaseHandler._deleteData(testUser.id, 1L));
	assertEquals(new Long(3L), databaseHandler._deleteData(testUser.id, 3L));
	assertEquals(new Long(2L), databaseHandler._deleteData(testUser.id, 2L));
	assertEquals(0, testUser.data_database.size());
    }

    @Test(expected = DatabaseDataHandler.DatabaseException.class)
    public void testDeleteWhenNone() throws Exception
    {
	databaseHandler.deleteData(5L, 1L);
    }

    @Test(expected = Throwable.class)
    public void testGetListNoUser()
    {
	databaseHandler.getList(5L);
    }

    @Test
    public void testEmptyGetList()
    {
	assertEquals(new ArrayList<Map<String, Object>>(), databaseHandler.getList(testUser.id));
    }

    @Test
    public void testList()
    {
	testUser.data_database.put(1L, new String[]
	{
		"a",
		"aa"
	});
	testUser.data_database.put(2L, new String[]
	{
		"b",
		"bb"
	});
	testUser.data_database.put(3L, new String[]
	{
		"c",
		"cc"
	});

	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
	for (Entry<Long, String[]> entry : testUser.data_database.entrySet())
	{
	    Map<String, Object> value = new TreeMap<>();
	    value.put("id", entry.getKey());
	    value.put("title", entry.getValue()[0]);
	    result.add(value);
	}

	assertEquals(result, databaseHandler.getList(testUser.id));
    }

    @Test
    public void testCreateLogin() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
	String password = "something_p";

	databaseHandler.createLogin("something", password);
	assertEquals(2, user_name_database.size());
	assertEquals(true, user_name_database.containsKey("something"));

	// Test hashing

	DatabaseUser user = user_name_database.get("something");

	byte[] salt = user.salt;

	SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
	PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT_FOR_HASH, KEY_LENGTH_FOR_HASH);
	SecretKey key = skf.generateSecret(spec);
	byte[] hash = key.getEncoded();

	assertArrayEquals(hash, user.hash);
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
	databaseHandler._getUserId("This is not a valid username?");
    }

    @Test
    public void testGetUserIdOk()
    {
	databaseHandler._createLogin("Ok", "NoPass");
	assertEquals(2L, databaseHandler._getUserId("Ok"));

	databaseHandler._createLogin("Ok2", "NoPass");
	assertEquals(3L, databaseHandler._getUserId("Ok2"));

	databaseHandler._createLogin("Ok3", "NoPass");
	assertEquals(4L, databaseHandler._getUserId("Ok3"));
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
