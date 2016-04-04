package se.hig.programvaruteknik.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.Test;

import se.hig.programvaruteknik.model.DataSourceBuilder.DataSourceBuilderException;

@SuppressWarnings("javadoc")
public class TestDataSourceBuilder
{
    @SuppressWarnings(
    {
	    "serial",
	    "unchecked",
	    "rawtypes"
    })
    private Map<LocalDate, List<Double>> inflatedMap(Map<LocalDate, Double> map)
    {
	return map.entrySet().stream().collect(
		Collectors.<Entry<LocalDate, Double>, LocalDate, List<Double>> toMap(
			(key) -> key.getKey(),
			(value) -> new ArrayList()
			{
			    {
				add(value.getValue());
			    }
			}));
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingName()
    {
	new DataSourceBuilder()
	{
	    {
		setUnit("Unit");
	    }

	    @Override
	    protected Map<LocalDate, List<Double>> generateData()
	    {
		return new TreeMap<>();
	    }
	}.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingUnit()
    {
	new DataSourceBuilder()
	{
	    {
		setName("Name");
	    }

	    @Override
	    protected Map<LocalDate, List<Double>> generateData()
	    {
		return new TreeMap<>();
	    }
	}.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingUnitAndName()
    {
	new DataSourceBuilder()
	{
	    @Override
	    protected Map<LocalDate, List<Double>> generateData()
	    {
		return new TreeMap<>();
	    }
	}.build();
    }

    @Test(expected = DataSourceBuilderException.class)
    public void testMissingData()
    {
	new DataSourceBuilder()
	{
	    {
		setUnit("Unit");
		setName("Name");
	    }

	    @Override
	    protected Map<LocalDate, List<Double>> generateData()
	    {
		return null;
	    }
	}.build();
    }

    @Test
    public void testGenerateData()
    {
	String testName = "TestName";
	String testUnit = "TestUnit";

	String testSourceName = "TestSourceName";
	String testSourceLink = "TestSourceLink";

	Map<LocalDate, Double> data = new TreeMap<>();
	data.put(LocalDate.of(2016, 2, 27), 10d);
	data.put(LocalDate.of(2016, 2, 28), 10d);

	DataSource source = new DataSourceBuilder()
	{
	    {
		setName(testName);
		setUnit(testUnit);

		setSourceName(testSourceName);
		setSourceLink(testSourceLink);
	    }

	    @Override
	    protected Map<LocalDate, List<Double>> generateData()
	    {
		return inflatedMap(data);
	    }
	}.build();

	assertEquals(testName, source.getName());
	assertEquals(testUnit, source.getUnit());

	assertEquals(testSourceName, source.getSourceName());
	assertEquals(testSourceLink, source.getSourceLink());

	assertEquals(data, source.getData());
    }

    @Test
    public void testFilter()
    {
	Map<LocalDate, Double> inputData = new TreeMap<>();
	Map<LocalDate, Double> expectedData = new TreeMap<>();

	inputData.put(LocalDate.of(2016, 2, 27), 10d);
	expectedData.put(LocalDate.of(2016, 2, 27), 10d);

	inputData.put(LocalDate.of(2016, 2, 28), 20d);

	DataSource source = new DataSourceBuilder()
	{
	    {
		setName("Name");
		setUnit("Unit");
		setDataFilter((date, value) -> value.equals(20d));
	    }

	    @Override
	    protected Map<LocalDate, List<Double>> generateData()
	    {
		return inflatedMap(inputData);
	    }
	}.build();

	assertEquals(expectedData, source.getData());
    }
}
