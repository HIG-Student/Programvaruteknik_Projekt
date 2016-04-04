package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import se.hig.programvaruteknik.data.SMHISourceBuilder.DataType;
import se.hig.programvaruteknik.data.SMHISourceBuilder.Period;
import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSource.DataSourceException;

@SuppressWarnings("javadoc")
public class TestSMHIData
{
    private static SMHISourceBuilder temperatureBuilder;

    @BeforeClass
    public static void init() throws DataSourceException
    {
	SMHILocation location = SMHILocation.GÄVLE_A;
	SMHISourceBuilder.DataType dataType = DataType.TEMPERATURE;

	temperatureBuilder = new SMHISourceBuilder((url) ->
	{
	    assertEquals("Can't fetch url form enum", url, String.format(location.url, dataType.parameter));
	    return DataSupplierFactory.createFileFetcher("data/test/TestSMHIData1.json").get();
	} , dataType, location);

	temperatureBuilder.setPeriod((url) ->
	{
	    switch (url)
	    {
	    case "http://opendata-download-metobs.smhi.se/api/version/latest/parameter/2/station/107420/period/corrected-archive.json":
		return DataSupplierFactory
			.createFileFetcher("data/test/TestSMHIData2.json")
			.get();
	    case "http://opendata-download-metobs.smhi.se/api/version/latest/parameter/2/station/107420/period/corrected-archive/data.csv":
		return DataSupplierFactory
			.createFileFetcher("data/test/TestSMHIData3.csv")
			.get();
	    default:
		fail("'setPeriod' calls unknown url!");
		return null;
	    }
	} , Period.OLD);
    }

    @Test
    public void testData() throws DataSourceException, IOException
    {
	DataSource dataSource = temperatureBuilder.build();
	assertEquals("Gävle A", dataSource.getName());
	assertEquals("degree celsius", dataSource.getUnit());

	Map<LocalDate, Double> data = dataSource.getData();
	assertEquals(new Double(7386), new Double(data.size()));

	assertEquals(new Double(19.1), data.get(LocalDate.of(1995, 8, 1)));
	assertEquals(new Double(17.7), data.get(LocalDate.of(1995, 8, 2)));

	assertEquals(new Double(4.3), data.get(LocalDate.of(2015, 10, 30)));
	assertEquals(new Double(8.0), data.get(LocalDate.of(2015, 10, 31)));
    }
}
