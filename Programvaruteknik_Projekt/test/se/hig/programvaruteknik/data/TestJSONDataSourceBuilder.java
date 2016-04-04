package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSourceBuilder.DataSourceBuilderException;

@SuppressWarnings("javadoc")
public class TestJSONDataSourceBuilder
{
    private static String source = "{ \"name\": \"Stuffies\" , \"unit\": \"pwns\" , \"stuff\": [{ \"date\": \"2016-02-26\" , \"value\": 1 },{\"date\": \"2016-02-27\" , \"invalid\": true , \"value\": 2},{\"date\": \"2016-02-28\" , \"value\": 3}] }";
    private JSONDataSourceBuilder builder;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
    {
	builder = new JSONDataSourceBuilder();
	builder.setSourceSupplier(() -> source);
	builder.setNameExtractor((root) -> root.get("name").toString());
	builder.setUnitExtractor((root) -> root.get("unit").toString());
	builder.setListExtractor((root) -> (List<Map<String, Object>>) root.get("stuff"));
	builder.setDataExtractor(
		(entry, adder) -> adder.accept(
			LocalDate.parse(entry.get("date").toString()),
			Double.parseDouble(entry.get("value").toString())));
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

	builder.setEntryFilter((entry) -> entry.containsKey("invalid") && entry.get("invalid").equals(true));
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
	builder.setListExtractor(null);
	builder.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingData()
    {
	builder.setDataExtractor(null);
	builder.build();
    }
}
