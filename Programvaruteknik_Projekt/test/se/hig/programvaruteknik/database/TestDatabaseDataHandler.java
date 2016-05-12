/**
 * 
 */
package se.hig.programvaruteknik.database;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.powermock.api.mockito.PowerMockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
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

    @Before
    public void setUp() throws Exception
    {
	database = new TreeMap<Long, String[]>();
	databaseHandler = mock(DatabaseDataHandler.class);

	Connection connection = mock(Connection.class);
	PreparedStatement statement = mock(PreparedStatement.class);

	doReturn(statement).when(connection, "prepareStatement", "SELECT * FROM data WHERE id = ?");

	doReturn(connection).when(databaseHandler, "createConnection");

    }

    @Test
    public void test() throws Exception
    {

    }
}
