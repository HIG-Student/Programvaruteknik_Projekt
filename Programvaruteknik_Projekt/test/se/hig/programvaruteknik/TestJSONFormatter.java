
package se.hig.programvaruteknik;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.TestCase;

@SuppressWarnings("javadoc")
public class TestJSONFormatter
{
    @Test
    public void testObject()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("{\n\t\"gnu\": 42\n}", formatter.format("{\"gnu\":42}"));
	assertEquals("{\n\t\"gnu\": \n\t{\n\t\t\n\t}\n}", formatter.format("{\"gnu\": {}}"));
    }

    @Test
    public void testNumberList()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("[\n\t1,\n\t2,\n\t3\n]", formatter.format("[1,2,3]"));
    }

    @Test
    public void testMultilevel()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("{\n\t{\n\t\t{\n\t\t\t\n\t\t}\n\t}\n}", formatter.format("{{{}}}"));
    }

    @Test
    public void testSpecialTokens()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("{\n\t\n}", formatter.format("{\n  \t \n\r}"));
    }

    @Test
    public void testObjectBreaks()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("{\n\t\n}", formatter.format("{}"));
    }

    @Test
    public void testArrayBreaks()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("[\n\t\n]", formatter.format("[]"));
    }

    @Test
    public void testNotMessWithString()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("\"{[,]}\"", formatter.format("\"{[,]}\""));
    }

    @Test
    public void testNotMessWithNumber()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("-34.67e-7", formatter.format("-34.67e-7"));
    }

    @Test
    public void testFixSpacesInNumber()
    {
	JSONFormatter formatter = new JSONFormatter();
	assertEquals("-34.67e-7", formatter.format("- 3 4. 67 e- 7"));
    }

    @Test
    public void testNoFormat()
    {
	assertEquals(
		"[{\"yo\":{\"no\": 56,\"li\":[45,1],\"st\":\"{{[[}!\"}}]",
		JSONFormatter.NONE.format("[{\"yo\":{\"no\": 56,\"li\":[45,1],\"st\":\"{{[[}!\"}}]"));
    }
}
