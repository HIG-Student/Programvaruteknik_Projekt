package se.hig.programvaruteknik.servlet;

import junit.framework.TestCase;
import se.hig.programvaruteknik.JSONFormatter;
import se.hig.programvaruteknik.model.DataCollection;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.Resolution;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;
import static org.powermock.api.mockito.PowerMockito.*;

import static org.junit.Assert.*;
import org.junit.Before;

@SuppressWarnings(
{
	"javadoc"
})

@RunWith(PowerMockRunner.class)
@PrepareForTest(
{
	StatisticsServlet.class,
	DataSourceBuilder.class,
	Resolution.class
})
public class TestStatisticsServlet extends TestCase
{
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StatisticsServlet servlet;
    private DataSourceBuilder datasourceBuilder;
    private DataCollectionBuilder collectionBuilder;
    private DataCollection collection;
    private JSONFormatter formatter;

    private Resolution resolution;

    private StringWriter writer;

    @Override
    public void setUp() throws Exception
    {
	request = mock(HttpServletRequest.class);
	response = mock(HttpServletResponse.class);
	datasourceBuilder = mock(DataSourceBuilder.class);
	collectionBuilder = mock(DataCollectionBuilder.class);
	collection = mock(DataCollection.class);
	formatter = mock(JSONFormatter.class);

	mockStatic(Resolution.class, Resolution.class);

	resolution = mock(Resolution.class);

	servlet = spy(new StatisticsServlet(formatter, collectionBuilder));

	doReturn(true).when(servlet).getPretty(request);
	doReturn(resolution).when(servlet).getResolution(request);
	doReturn(datasourceBuilder).when(servlet).getDataSource(null, "sourceA");
	doReturn(datasourceBuilder).when(servlet).getDataSource(null, "sourceB");

	doReturn(collection).when(collectionBuilder).getResult();
	doReturn("formatter").when(collection).asJSON(formatter);
	doReturn("no_formatter").when(collection).asJSON(null);

	writer = new StringWriter();
	when(response.getWriter()).thenReturn(new PrintWriter(writer));

    }

    @Test
    public void testingPrettyTrue() throws Exception
    {
	doReturn("true").when(request).getParameter("pretty");
	doCallRealMethod().when(servlet).getPretty(request);
	servlet.doGet(request, response);
	assertEquals("formatter", writer.toString());
    }

    @Test
    public void testingPrettyMixedCaseTrue() throws Exception
    {
	doReturn("tRue").when(request).getParameter("pretty");
	doCallRealMethod().when(servlet).getPretty(request);
	servlet.doGet(request, response);
	assertEquals("formatter", writer.toString());
    }

    @Test
    public void testingPrettyFalse() throws Exception
    {
	doReturn("false").when(request).getParameter("pretty");
	doCallRealMethod().when(servlet).getPretty(request);
	servlet.doGet(request, response);
	assertEquals("no_formatter", writer.toString());
    }

    @Test
    public void testingPrettyMixedCaseFalse() throws Exception
    {
	doReturn("fAlse").when(request).getParameter("pretty");
	doCallRealMethod().when(servlet).getPretty(request);
	servlet.doGet(request, response);
	assertEquals("no_formatter", writer.toString());
    }

    @Test
    public void testingPrettyNull() throws Exception
    {
	doReturn(null).when(request).getParameter("pretty");
	doCallRealMethod().when(servlet).getPretty(request);
	servlet.doGet(request, response);
	assertEquals("no_formatter", writer.toString());
    }

    @Test
    public void testingNoResolution() throws Exception
    {
	doReturn(null).when(request).getParameter("resolution");
	assertEquals(resolution, servlet.getResolution(request));
    }

    @SuppressWarnings("static-access")
    @Test
    public void testingResolution() throws Exception
    {
	when(resolution.valueOf("mocked")).thenReturn(resolution);
	doReturn("mocked").when(request).getParameter("resolution");
	assertEquals(resolution, servlet.getResolution(request));
    }

    @Test
    public void testingIncorrectResolution() throws Exception
    {
	doReturn("mocked").when(request).getParameter("resolution");
	assertEquals(resolution, servlet.getResolution(request));
    }
}
