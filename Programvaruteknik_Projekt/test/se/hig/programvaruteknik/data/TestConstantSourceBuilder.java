package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import org.junit.Test;

import se.hig.programvaruteknik.model.DataSource;

@SuppressWarnings("javadoc")
public class TestConstantSourceBuilder
{
    @Test
    public void test()
    {
	ConstantSourceBuilder builder = new ConstantSourceBuilder();
	DataSource source = builder.build();

	assertEquals("Random value", source.getName());
	assertEquals("N/A", source.getUnit());

	assertEquals("xkcd", source.getSourceName());
	assertEquals("https://xkcd.com/221/", source.getSourceLink());

	assertEquals(16861, source.getData().size());

	for (Double d : source.getData().values())
	    assertEquals(new Double(4), d);
    }
}
