package se.hig.programvaruteknik;

import static org.junit.Assert.*;

import org.junit.Test;

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
    }
}
