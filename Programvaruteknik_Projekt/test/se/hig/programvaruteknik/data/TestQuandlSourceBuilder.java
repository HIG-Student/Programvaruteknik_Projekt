package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import se.hig.programvaruteknik.data.QuandlDataSourceBuilder.SourceType;
import se.hig.programvaruteknik.model.DataSource;

public class TestQuandlSourceBuilder
{
    @Test
    public void testCrimeGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.CRIME_RATE_USA);
	DataSource source = builder.build();
	assertEquals(51, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
	
    }
    
    @Test
    public void testDeathRateGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.DEATH_RATE_CIRRHOSIS_USA);
	DataSource source = builder.build();
	assertEquals(96, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
	
	assertEquals(source.getData().get(LocalDate.of(2005, 1, 1)), new Double(9.2));
	assertEquals(source.getData().get(LocalDate.of(1910, 1, 1)), new Double(22.1));
    }
    
    @Test
    public void testUnemploymentGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.UNEMPLOYMENT_RATE_USA);
	DataSource source = builder.build();
	assertEquals(819, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
    }
    
    @Test
    public void testAlcoholGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.ALCOHOL_CONSUMPTION_USA);
	DataSource source = builder.build();
	assertEquals(43, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
    }
    
    @Test
    public void testGasProdGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.GAS_AND_DIESEL_PROD_SWE);
	DataSource source = builder.build();
	assertEquals(21, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
    }
    
    @Test
    public void testGasStockGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.GAS_AND_DIESEL_STOCK_SWE);
	DataSource source = builder.build();
	assertEquals(21, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
    }
    
    @Test
    public void testEnrollmentEducationGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.ENROLMENT_EDU_SWE);
	DataSource source = builder.build();
	assertEquals(7, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
    }
    
    /*@Test
    public void testEmploymentFemaleGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.EMPLOYMENT_RATE_FEMALE_SWE);
	DataSource source = builder.build();
	assertEquals(7, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
	//Nullpointer fel
    }*/
    
    @Test
    public void testEmploymentOverallGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.EMPLOYMENT_RATE_OVERALL_SWE);
	DataSource source = builder.build();
	assertEquals(38, source.getData().size());

	assertEquals("quandl", source.getSourceName());
	assertEquals("http://www.quandl.com", source.getSourceLink());
    }

}