package se.hig.programvaruteknik.database;

import static org.junit.Assert.*;

import java.sql.DriverManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sun.corba.se.pept.transport.Connection;

import se.hig.programvaruteknik.data.StockSourceBuilder;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DatabaseHandler.class })
public class TestDatabaseHandler {

	DatabaseHandler dbHandler;

	@Before
	public void setUp() {
		dbHandler = spy(new DatabaseHandler());

		Connection connection = mock(Connection.class);

		when(dbHandler, method(DatabaseHandler.class, "createConnection", String.class, String.class, String.class))
				.withArguments("jdbc:postgresql://127.0.0.1:5432/statistics", "kaka", "kaka").thenReturn(connection);

		dbHandler = new DatabaseHandler(driverManager);
	}

	@Test
	public void testSaveAndLoadData() {
		assertEquals(0, dbHandler.save("title", "jsondata"));
		assertEquals("jsondata", dbHandler.load(0));
	}

	@Test
	public void testSaveAndLoadMoreData() {
		assertEquals(0, dbHandler.save("title1", "jsondata1"));
		assertEquals(1, dbHandler.save("title2", "jsondata2"));
		assertEquals(2, dbHandler.save("title3", "jsondata3"));

		assertEquals("jsondata1", dbHandler.load(0));
		assertEquals("jsondata2", dbHandler.load(1));
		assertEquals("jsondata3", dbHandler.load(2));
	}

	@Test(expected = DatabaseHandlerException.class)
	public void testSaveWithNullTitle() {
		dbHandler.save(null, "jsondata");
	}

	@Test(expected = DatabaseHandlerException.class)
	public void testSaveWithNullData() {
		dbHandler.save("title", null);
	}

	@Test(expected = DatabaseHandlerException.class)
	public void testLoadWhenNoData() {
		dbHandler.load(0);
	}

	@Test(expected = DatabaseHandlerException.class)
	public void testLoadWhenNegativeInParameter() {
		dbHandler.load(-1);
	}

	@Test(expected = DatabaseHandlerException.class)
	public void testLoadWhenNullInParameter() {
		dbHandler.load(null);
	}

}
