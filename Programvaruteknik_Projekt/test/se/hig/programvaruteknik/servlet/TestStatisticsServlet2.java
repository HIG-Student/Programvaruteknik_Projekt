package se.hig.programvaruteknik.servlet;

import junit.framework.TestCase;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.Resolution;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;
import static org.powermock.api.mockito.PowerMockito.*;

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
public class TestStatisticsServlet2 extends TestCase
{
    public static void main(String[] args) throws Exception
    {
	HttpServletRequest request = mock(HttpServletRequest.class);
	HttpServletResponse response = mock(HttpServletResponse.class);

	StatisticsServlet servlet = new StatisticsServlet();

	StringWriter writer = new StringWriter();
	
	doReturn("Weather,GÄVLE_A").when(request).getParameter("sourceA");
	doReturn("Stock,MSFT,Value").when(request).getParameter("sourceB");
	when(response.getWriter()).thenReturn(new PrintWriter(writer));

	servlet.doGet(request, response);

	System.out.println(writer.toString());

	System.out.println("Yo");
    }
}
