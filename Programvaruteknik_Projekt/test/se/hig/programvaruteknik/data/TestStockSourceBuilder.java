package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.TreeMap;

import org.junit.Test;

import se.hig.programvaruteknik.JSONFormatter;
import se.hig.programvaruteknik.data.StockSourceBuilder.StockInfo;
import se.hig.programvaruteknik.data.StockSourceBuilder.StockName;
import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSource.DataSourceException;

@SuppressWarnings(
{
	"javadoc",
	"serial"
})
public class TestStockSourceBuilder
{
    @Test
    public void testGetData()
    {
	StockSourceBuilder builder = new StockSourceBuilder(StockInfo.VOLUME);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestStockData.txt"));
	DataSource source = builder.build();

	assertEquals(2, source.getData().size());

	assertEquals("kibot", source.getSourceName());
	assertEquals("http://http://www.kibot.com", source.getSourceLink());
    }

    @Test
    public void testGetVolume()
    {
	StockSourceBuilder builder = new StockSourceBuilder(StockInfo.VOLUME);
	builder.setStock("TEST", 0);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestStockData.txt"));
	DataSource source = builder.build();
	assertEquals(source.getData(), new TreeMap<LocalDate, Double>()
	{
	    {
		put(LocalDate.of(2015, 1, 13), new Double(35494208));
		put(LocalDate.of(2015, 1, 14), new Double(29454208));
	    }
	});
	assertEquals("TEST , Volym", source.getName());
	assertEquals("st", source.getUnit());
    }

    @Test
    public void testGetPrice()
    {
	StockSourceBuilder builder = new StockSourceBuilder(StockInfo.PRICE);
	builder.setStock("TEST", 0);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestStockData.txt"));
	DataSource source = builder.build();
	assertEquals(source.getData(), new TreeMap<LocalDate, Double>()
	{
	    {
		put(LocalDate.of(2015, 1, 13), new Double(44.81));
		put(LocalDate.of(2015, 1, 14), new Double(44.42));
	    }
	});
	assertEquals("TEST , Pris", source.getName());
	assertEquals("$", source.getUnit());
    }

    @Test
    public void testGetChange()
    {
	StockSourceBuilder builder = new StockSourceBuilder(StockInfo.CHANGE);
	builder.setStock("TEST", 0);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestStockData.txt"));
	DataSource source = builder.build();
	assertEquals(source.getData(), new TreeMap<LocalDate, Double>()
	{
	    {
		put(LocalDate.of(2015, 1, 13), new Double(44.81 - 45.4));
		put(LocalDate.of(2015, 1, 14), new Double(44.42 - 44.43));
	    }
	});
	assertEquals("TEST , Ändring", source.getName());
	assertEquals("$", source.getUnit());
    }

    @Test
    public void testGetFluctuation()
    {
	StockSourceBuilder builder = new StockSourceBuilder(StockInfo.FLUCTUATION);
	builder.setStock("TEST", 0);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestStockData.txt"));
	DataSource source = builder.build();
	assertEquals(source.getData(), new TreeMap<LocalDate, Double>()
	{
	    {
		put(LocalDate.of(2015, 1, 13), new Double(46.31 - 44.52));
		put(LocalDate.of(2015, 1, 14), new Double(44.7 - 44.1));
	    }
	});
	assertEquals("TEST , Ostadighet", source.getName());
	assertEquals("$", source.getUnit());
    }
    
    @Test
    public void testGetSourceAsJSON()
    {
    	StockSourceBuilder builder = new StockSourceBuilder(StockInfo.FLUCTUATION);
    	JSONFormatter formatter = new JSONFormatter();
    	builder.setStock("TEST", 0);
    	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestStockData.txt"));
    	DataSource source = builder.build();
    	assertNotNull(source.asJSON(formatter));
    	}
    
    @Test
    public void testDataSourceException() 
    {
    	StockSourceBuilder builder = new StockSourceBuilder(StockInfo.FLUCTUATION);
    	JSONFormatter formatter = new JSONFormatter();
    	builder.setStock(StockName.BAC, 0);
    	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestStockData.txt"));
    	Exception ex = new Exception("aa");
    	DataSourceException source = new DataSourceException(ex);
    	source = new DataSourceException("aa");
    }
}
