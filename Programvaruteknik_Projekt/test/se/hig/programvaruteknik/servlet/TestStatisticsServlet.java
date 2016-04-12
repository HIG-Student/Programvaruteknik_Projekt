package se.hig.programvaruteknik.servlet;

import junit.framework.TestCase;
import se.hig.programvaruteknik.JSONFormatter;
import se.hig.programvaruteknik.model.DataCollection;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.MergeType;
import se.hig.programvaruteknik.model.Resolution;
import se.hig.programvaruteknik.JSONOutputter;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.expectation.WithOrWithoutExpectedArguments;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;
import static org.powermock.api.mockito.PowerMockito.*;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;

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
@Ignore("Needs fixing")
public class TestStatisticsServlet extends TestCase
{
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StatisticsServlet servlet;

    private JSONFormatter formatter;
    private DataCollectionBuilder collectionBuilder;
    private Function<InputStream, Map<String, Object>> JSONConverter;

    private Map<String, Object> requestData;
    private Map<String, Object> dataData;

    private Map<String, Object> sourceData;
    private Map<String, Object> sourceDataA;
    private Map<String, Object> sourceDataB;

    private Resolution resolution;

    private StringWriter writer;

    private DataSourceBuilder sourceBuilder;
    private DataSourceBuilder sourceBuilderA;
    private DataSourceBuilder sourceBuilderB;

    private DataSource sourceA;
    private DataSource sourceB;

    private JSONOutputter JSONOutputter;
    private JSONOutputter JSONOutputterAB;

    @Override
    public void setUp() throws Exception
    {
	request = mock(HttpServletRequest.class);
	response = mock(HttpServletResponse.class);

	requestData = new TreeMap<String, Object>();
	dataData = new TreeMap<String, Object>();
	sourceData = new TreeMap<String, Object>();
	sourceDataA = new TreeMap<String, Object>();
	sourceDataB = new TreeMap<String, Object>();

	sourceBuilder = mock(DataSourceBuilder.class);
	sourceBuilderA = mock(DataSourceBuilder.class);
	sourceBuilderB = mock(DataSourceBuilder.class);

	JSONOutputter = mock(JSONOutputter.class);
	JSONOutputterAB = mock(JSONOutputter.class);

	formatter = mock(JSONFormatter.class);
	collectionBuilder = mock(DataCollectionBuilder.class);
	JSONConverter = (stream) -> requestData;

	mockStatic(Resolution.class, Resolution.class);

	resolution = mock(Resolution.class);

	servlet = spy(new StatisticsServlet(formatter, collectionBuilder, JSONConverter));

	Method getValue = method(StatisticsServlet.class, "getValue", Map.class, String.class);

	when(servlet, getValue).withArguments(requestData, "type").thenReturn("custom");
	when(servlet, getValue).withArguments(requestData, "data").thenReturn(dataData);
	when(servlet, getValue).withArguments(requestData, "pretty").thenReturn("true");

	when(servlet, getValue).withArguments(requestData, "resolution").thenReturn(Resolution.DAY);
	when(servlet, getValue).withArguments(requestData, "merge-type-x").thenReturn(MergeType.AVERAGE);
	when(servlet, getValue).withArguments(requestData, "merge-type-y").thenReturn(MergeType.AVERAGE);

	when(servlet, getValue).withArguments(requestData, "source").thenReturn(sourceData);
	when(servlet, getValue).withArguments(requestData, "sourceA").thenReturn(sourceDataA);
	when(servlet, getValue).withArguments(requestData, "sourceB").thenReturn(sourceDataB);

	doReturn(sourceBuilder).when(servlet).getDataSource(sourceData);
	doReturn(sourceBuilderA).when(servlet).getDataSource(sourceDataA);
	doReturn(sourceBuilderB).when(servlet).getDataSource(sourceDataB);

	doReturn(JSONOutputter).when(sourceBuilder).build();
	doReturn(sourceA).when(sourceBuilderA).build();
	doReturn(sourceB).when(sourceBuilderB).build();

	doReturn(JSONOutputterAB).when(collectionBuilder).getResult();

	doReturn("data_source").when(JSONOutputter).asJSON(formatter);
	doReturn("data_source_AB").when(JSONOutputterAB).asJSON(formatter);

	doReturn("data_source_NO_FORMATTER").when(JSONOutputter).asJSON(null);
	doReturn("data_source_AB_NO_FORMATTER").when(JSONOutputterAB).asJSON(null);

	writer = new StringWriter();
	when(response.getWriter()).thenReturn(new PrintWriter(writer));
    }
    /*
     * @Test
     * public void testingPrettyTrue() throws Exception
     * {
     * StatisticsServlet servlet = spy(servlet);
     * 
     * 
     * getValue(body, "pretty", (String str) -> "true".equalsIgnoreCase(str),
     * false);
     * 
     * doReturn("true").when(request).getParameter("pretty");
     * doCallRealMethod().when(servlet).getPretty(request);
     * servlet.doGet(request, response);
     * assertEquals("formatter", writer.toString());
     * }
     * 
     * @Test
     * public void testingPrettyMixedCaseTrue() throws Exception
     * {
     * doReturn("tRue").when(request).getParameter("pretty");
     * doCallRealMethod().when(servlet).getPretty(request);
     * servlet.doGet(request, response);
     * assertEquals("formatter", writer.toString());
     * }
     * 
     * @Test
     * public void testingPrettyFalse() throws Exception
     * {
     * doReturn("false").when(request).getParameter("pretty");
     * doCallRealMethod().when(servlet).getPretty(request);
     * servlet.doGet(request, response);
     * assertEquals("no_formatter", writer.toString());
     * }
     * 
     * @Test
     * public void testingPrettyMixedCaseFalse() throws Exception
     * {
     * doReturn("fAlse").when(request).getParameter("pretty");
     * doCallRealMethod().when(servlet).getPretty(request);
     * servlet.doGet(request, response);
     * assertEquals("no_formatter", writer.toString());
     * }
     * 
     * @Test
     * public void testingPrettyNull() throws Exception
     * {
     * doReturn(null).when(request).getParameter("pretty");
     * doCallRealMethod().when(servlet).getPretty(request);
     * servlet.doGet(request, response);
     * assertEquals("no_formatter", writer.toString());
     * }
     * 
     * @Test
     * public void testingNoResolution() throws Exception
     * {
     * doReturn(null).when(request).getParameter("resolution");
     * assertEquals(resolution, servlet.getResolution(request));
     * }
     * 
     * @SuppressWarnings("static-access")
     * 
     * @Test
     * public void testingResolution() throws Exception
     * {
     * when(resolution.valueOf("mocked")).thenReturn(resolution);
     * doReturn("mocked").when(request).getParameter("resolution");
     * assertEquals(resolution, servlet.getResolution(request));
     * }
     * 
     * @Test
     * public void testingIncorrectResolution() throws Exception
     * {
     * doReturn("mocked").when(request).getParameter("resolution");
     * assertEquals(resolution, servlet.getResolution(request));
     * }
     */
}
