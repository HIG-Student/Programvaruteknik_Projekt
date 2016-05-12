package se.hig.programvaruteknik.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import se.hig.programvaruteknik.data.StockSourceBuilder;

import static org.powermock.api.mockito.PowerMockito.*;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(
{
	DatabaseDataHandler.class
})
public class TestDatabaseHandler
{

    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/statistics";
    private static final String USERNAME = "kaka";
    private static final String PASSWORD = "kaka";

    DatabaseDataHandler dbHandler;

    @Before
    public void setUp() throws Exception
    {
	dbHandler = spy(new DatabaseDataHandler(DB_URL, USERNAME, PASSWORD));

	Connection connection = mock(Connection.class);

	when(dbHandler, method(DatabaseDataHandler.class, "createConnection", String.class, String.class, String.class))
		.withArguments(DB_URL, USERNAME, PASSWORD)
		.thenReturn(connection);

	PreparedStatement statement = mock(PreparedStatement.class);
	when(connection.prepareStatement("INSERT INTO data(data, title) VALUES (?, ?)")).thenReturn(statement);

	doNothing().when(statement).setString(1, "[value1]");
	doNothing().when(statement).setString(2, "[value2]");
	doReturn(true).when(statement).execute();

	doThrow(dbHandler.new DatabaseHandlerException())
		.when(dbHandler)
		.save((String) Matchers.isNull(), Matchers.any());
	doThrow(dbHandler.new DatabaseHandlerException())
		.when(dbHandler)
		.save(Matchers.any(), (String) Matchers.isNull());

	doThrow(dbHandler.new DatabaseHandlerException()).when(dbHandler).load(null);
	doThrow(dbHandler.new DatabaseHandlerException()).when(dbHandler).load(-1);

    }

    @Test
    public void testSaveAndLoadData()
    {
	assertEquals(0, dbHandler.save("title", "jsondata"));
	assertEquals("jsondata", dbHandler.load(0));
    }

    @Test
    public void testSaveAndLoadMoreData()
    {
	assertEquals(0, dbHandler.save("title1", "jsondata1"));
	assertEquals(1, dbHandler.save("title2", "jsondata2"));
	assertEquals(2, dbHandler.save("title3", "jsondata3"));

	assertEquals("jsondata1", dbHandler.load(0));
	assertEquals("jsondata2", dbHandler.load(1));
	assertEquals("jsondata3", dbHandler.load(2));
    }

    @Test(expected = DatabaseHandlerException.class)
    public void testSaveWithNullTitle()
    {
	dbHandler.save(null, "jsondata");
    }

    @Test(expected = DatabaseHandlerException.class)
    public void testSaveWithNullData()
    {
	dbHandler.save("title", null);
    }

    @Test(expected = DatabaseHandlerException.class)
    public void testLoadWhenNoData()
    {
	dbHandler.load(0);
    }

    @Test(expected = DatabaseHandlerException.class)
    public void testLoadWhenNegativeInParameter()
    {
	dbHandler.load(-1);
    }

    @Test(expected = DatabaseHandlerException.class)
    public void testLoadWhenNullInParameter()
    {
	dbHandler.load(null);
    }

}
