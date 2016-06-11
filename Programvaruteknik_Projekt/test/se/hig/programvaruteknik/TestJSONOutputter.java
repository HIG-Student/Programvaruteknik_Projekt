/**
 * 
 */
package se.hig.programvaruteknik;

import static org.junit.Assert.*;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestJSONOutputter
{
    @Test
    public void testDefault()
    {
	assertEquals("[Stringy]", new JSONOutputter()
	{
	    @Override
	    public String asJSON(JSONFormatter formatter)
	    {
		assertNotNull(formatter);
		return "[Stringy]";
	    }
	}.asJSON());
    }
}
