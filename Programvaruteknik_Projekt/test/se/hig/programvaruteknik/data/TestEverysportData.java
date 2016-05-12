package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import se.hig.programvaruteknik.data.FootballSourceBuilder;
import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSource.DataSourceException;

@SuppressWarnings("javadoc")
public class TestEverysportData
{
    @Test
    public void testData() throws DataSourceException, IOException
    {
	FootballSourceBuilder footballSource = new FootballSourceBuilder();
	footballSource.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestEverysportData.json"));
	footballSource.setName("Football data");
	footballSource.setUnit("Goals");
	footballSource.setDataExtractor(FootballSourceBuilder.TOTAL_GOALS_EXTRACTOR);
	DataSource dataSource = footballSource.build();

	assertEquals("Football data", dataSource.getName());
	assertEquals("Goals", dataSource.getUnit());

	Map<LocalDate, Double> data = dataSource.getData();

	assertEquals(new Double(30), new Double(data.size()));

	assertEquals(new Double(2 + 2), data.get(LocalDate.of(2014, 3, 30)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 4, 6)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 4, 12)));
	assertEquals(new Double(3 + 0), data.get(LocalDate.of(2014, 4, 17)));
	assertEquals(new Double(2 + 2), data.get(LocalDate.of(2014, 4, 21)));
	assertEquals(new Double(0 + 2), data.get(LocalDate.of(2014, 4, 28)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 5, 5)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 5, 8)));
	assertEquals(new Double(0 + 1), data.get(LocalDate.of(2014, 5, 11)));
	assertEquals(new Double(2 + 2), data.get(LocalDate.of(2014, 5, 19)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 5, 26)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 6, 2)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 7, 5)));
	assertEquals(new Double(1 + 1), data.get(LocalDate.of(2014, 7, 13)));
	assertEquals(new Double(2 + 0), data.get(LocalDate.of(2014, 7, 19)));
	assertEquals(new Double(3 + 2), data.get(LocalDate.of(2014, 7, 26)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 8, 4)));
	assertEquals(new Double(3 + 1), data.get(LocalDate.of(2014, 8, 10)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 8, 13)));
	assertEquals(new Double(0 + 0), data.get(LocalDate.of(2014, 8, 16)));
	assertEquals(new Double(2 + 1), data.get(LocalDate.of(2014, 8, 25)));
	assertEquals(new Double(0 + 1), data.get(LocalDate.of(2014, 8, 30)));
	assertEquals(new Double(3 + 1), data.get(LocalDate.of(2014, 9, 14)));
	assertEquals(new Double(4 + 0), data.get(LocalDate.of(2014, 9, 19)));
	assertEquals(new Double(1 + 0), data.get(LocalDate.of(2014, 9, 24)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 9, 29)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 10, 4)));
	assertEquals(new Double(1 + 2), data.get(LocalDate.of(2014, 10, 19)));
	assertEquals(new Double(3 + 1), data.get(LocalDate.of(2014, 10, 25)));
	assertEquals(new Double(2 + 1), data.get(LocalDate.of(2014, 11, 1)));
    }

    @Ignore("TODO: FIX!")
    @Test
    public void testFull()
    {
	FootballSourceBuilder footballSource = new FootballSourceBuilder();
	footballSource
		.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestEverysportData_Full.json"));
	footballSource.setFetchFromWebsite("apikey=1769e0fdbeabd60f479b1dcaff03bf5c", "");
	footballSource.setFetchFromWebsite("apikey=1769e0fdbeabd60f479b1dcaff03bf5c", null);
	footballSource.setFetchFromWebsite("apikey=1769e0fdbeabd60f479b1dcaff03bf5c", "league=63925&limit=240");
	footballSource.setName("Football data");
	footballSource.setUnit("Goals");
	footballSource.setDataExtractor(FootballSourceBuilder.TOTAL_GOALS_EXTRACTOR);
	footballSource.setEntryFilter((obj) ->
	{
	    return false;
	});
	DataSource dataSource = footballSource.build();

	assertEquals("Football data", dataSource.getName());
	assertEquals("Goals", dataSource.getUnit());

	Map<LocalDate, Double> data = dataSource.getData();

	assertEquals(new Double(240), new Double(data.size()));
    }
    
    @Test
    public void testFetchFromURL() 
    {
    	FootballSourceBuilder footballSource = new FootballSourceBuilder();
    	footballSource
    		.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestEverysportData_Full.json"));
    	footballSource.setFetchFromWebsite
    	("http://api.everysport.com/v1/events?apikey=1769e0fdbeabd60f479b1dcaff03bf5c&league=63925&limit=240");
    	footballSource.setName("Football data");
    	footballSource.setUnit("Goals");
    	footballSource.setFetchFromWebsite();
    	footballSource.setDataExtractor(FootballSourceBuilder.DATE_EXTRACTOR, FootballSourceBuilder.SPECTATORS_EXTRACTOR);
    }
}
