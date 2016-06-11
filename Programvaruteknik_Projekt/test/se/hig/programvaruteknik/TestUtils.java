package se.hig.programvaruteknik;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;

import org.junit.Test;

import se.hig.programvaruteknik.data.StockSourceBuilder;

@SuppressWarnings("javadoc")
public interface TestUtils
{
    public static class Or
    {
	@Test
	public void testNull()
	{
	    Object Null = null;
	    assertEquals(null, Utils.or(Null));
	}

	@Test
	public void testOneValue()
	{
	    assertEquals("yo", Utils.or("yo"));
	}

	@Test
	public void testOneNullAndOneValue()
	{
	    assertEquals("yo", Utils.or(null, "yo"));
	}

	@Test
	public void testTwoNullAndOneValue()
	{
	    assertEquals("yo", Utils.or(null, null, "yo"));
	}

	@Test
	public void testOneValueAndOneNull()
	{
	    assertEquals("yo", Utils.or("yo", null));
	}

	@Test
	public void testFalse()
	{
	    assertEquals(false, Utils.or(false, true));
	}

	@Test
	public void testArray()
	{
	    assertEquals(true, Utils.or(new Object[] { null, true }));
	}
	
	@Test
	public void testWhateverNull() 
	{
		assertEquals("yo", Utils.or("yo", "yo"));
	}
	
    }
    
    public static class ReadStream 
    {
    	@Test (expected=Exception.class)
    	public void testNull()
    	{
    		Utils.readStream(null);
    	}
    	
    	@Test 
    	public void testReadStream() 
    	{
    		Integer amount = 10;
    		final String URL = "http://api.kibot.com/?action=history&symbol=%s&interval=daily&period=%d";
			try {
				HttpURLConnection connection = (HttpURLConnection) new URL(
					    String.format(URL, StockSourceBuilder.StockName.IBM, Math.min(amount, Integer.MAX_VALUE))).openConnection();
				    connection.connect();
				String response = Utils.readStream(connection.getInputStream());
				assertTrue(response != null);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
    	}
    	
    }
}
