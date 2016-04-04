package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSourceBuilder.DataSourceBuilderException;

@SuppressWarnings("javadoc")
public class TestCSVDataSourceBuilder
{
    private static String source = "Fetched from http://www.example.com\nName;Unit\nStuffies;pwns\n\nDate;Invalid;Value\n2016-02-26;;1\n2016-02-27;true;2\n2016-02-28;;3";
    private CSVDataSourceBuilder builder;

    @Before
    public void setUp()
    {
	builder = new CSVDataSourceBuilder();
	builder.setSourceSupplier(() -> source);
	builder.setNameExtractor((source) -> source.split("\\R+", 4)[2].split(";")[0]);
	builder.setUnitExtractor((source) -> source.split("\\R+", 4)[2].split(";")[1]);
	builder.setRowExtractor((source) ->
	{
	    String[] rows = source.split("\\R+");
	    for (int i = 0; i < 20; i++)
		if (rows[i].startsWith("Date;Invalid;Value")) return Arrays
			.asList(Arrays.copyOfRange(source.split("\\R+"), i + 1, rows.length));
	    throw builder.new DataSourceBuilderException("Incorrect format");
	});
	builder.setDataExtractor(
		(row, adder) -> adder
			.accept(LocalDate.parse(row.split(";", 2)[0]), Double.parseDouble(row.split(";", 4)[2])));
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingAll()
    {
	new JSONDataSourceBuilder().build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testIncorrectSource()
    {
	builder.setSourceSupplier(() -> "Yo i amz incorrect");
	builder.build();
    }

    @Test
    public void testGenerate()
    {
	Map<LocalDate, Double> expectedData = new TreeMap<LocalDate, Double>();
	expectedData.put(LocalDate.of(2016, 2, 26), 1d);
	expectedData.put(LocalDate.of(2016, 2, 27), 2d);
	expectedData.put(LocalDate.of(2016, 2, 28), 3d);

	DataSource dataSource = builder.build();

	assertEquals("Stuffies", dataSource.getName());
	assertEquals("pwns", dataSource.getUnit());
	assertEquals(expectedData, dataSource.getData());
    }

    @Test
    public void testFilterEntry()
    {
	Map<LocalDate, Double> expectedData = new TreeMap<LocalDate, Double>();
	expectedData.put(LocalDate.of(2016, 2, 26), 1d);
	expectedData.put(LocalDate.of(2016, 2, 28), 3d);

	builder.setRowFilter((row) -> row.split(";")[1].equals("true"));
	DataSource dataSource = builder.build();

	assertEquals(expectedData, dataSource.getData());
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingSource()
    {
	builder.setSourceSupplier(null);
	builder.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingList()
    {
	builder.setRowExtractor(null);
	builder.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingData()
    {
	builder.setDataExtractor(null);
	builder.build();
    }
}
