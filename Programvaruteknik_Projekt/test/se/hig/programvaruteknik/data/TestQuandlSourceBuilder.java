package se.hig.programvaruteknik.data;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import se.hig.programvaruteknik.data.QuandlDataSourceBuilder.SourceType;
import se.hig.programvaruteknik.model.DataSource;

@SuppressWarnings("javadoc")
public class TestQuandlSourceBuilder
{

    @Test
    public void testDeathRateGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.DEATH_RATE_CIRRHOSIS_USA);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestCirrhosisUsaData.json"));
	DataSource source = builder.build();

	assertEquals(96, source.getData().size());

	assertEquals(source.getData().get(LocalDate.of(2005, 1, 1)), new Double(9.2));
	assertEquals(source.getData().get(LocalDate.of(1910, 1, 1)), new Double(22.1));
    }

    @Test
    public void testUnemploymentGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.UNEMPLOYMENT_RATE_USA);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestUnemploymentUsaData.json"));
	DataSource source = builder.build();
	assertEquals(820, source.getData().size());

	assertEquals(source.getData().get(LocalDate.of(2016, 3, 1)), new Double(8116));
	assertEquals(source.getData().get(LocalDate.of(1948, 1, 1)), new Double(2351));
    }

    @Test
    public void testAlcoholGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.ALCOHOL_CONSUMPTION_USA);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestAlcoholUsaData.json"));
	DataSource source = builder.build();
	assertEquals(43, source.getData().size());

	assertEquals(source.getData().get(LocalDate.of(2008, 1, 1)), new Double(62));
	assertEquals(source.getData().get(LocalDate.of(1939, 1, 1)), new Double(58));
    }

    @Test
    public void testGasProdGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.GAS_AND_DIESEL_PROD_SWE);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestGasAndDieselProdSweData.json"));
	DataSource source = builder.build();
	assertEquals(21, source.getData().size());

	assertEquals(source.getData().get(LocalDate.of(2010, 12, 31)), new Double(7558));
	assertEquals(source.getData().get(LocalDate.of(1990, 12, 31)), new Double(6213));
    }

    @Test
    public void testGasStockGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.GAS_AND_DIESEL_STOCK_SWE);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestGasAndDieselStockSweData.json"));
	DataSource source = builder.build();
	assertEquals(21, source.getData().size());

	assertEquals(source.getData().get(LocalDate.of(2010, 12, 31)), new Double(-435));
	assertEquals(source.getData().get(LocalDate.of(1990, 12, 31)), new Double(-64));
    }

    @Test
    public void testEnrollmentEducationGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.ENROLMENT_EDU_SWE);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestEnrolmentSweData.json"));
	DataSource source = builder.build();
	assertEquals(7, source.getData().size());

	assertEquals(source.getData().get(LocalDate.of(2005, 12, 31)), new Double(1241907));
	assertEquals(source.getData().get(LocalDate.of(1999, 12, 31)), new Double(1208790));
    }

    @Test
    public void testEmploymentOverallGetData()
    {
	QuandlDataSourceBuilder builder = new QuandlDataSourceBuilder(SourceType.EMPLOYMENT_RATE_OVERALL_SWE);
	builder.setSourceSupplier(DataSupplierFactory.createFileFetcher("data/test/TestEmploymentRateSweData.json"));
	DataSource source = builder.build();
	assertEquals(38, source.getData().size());

	assertEquals(source.getData().get(LocalDate.of(2016, 12, 31)), new Double(4.931));
	assertEquals(source.getData().get(LocalDate.of(1980, 12, 31)), new Double(4.269));
    }

}