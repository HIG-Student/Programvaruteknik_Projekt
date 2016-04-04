package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.owlike.genson.Genson;

/**
 * Builds temperature sources
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class SMHISourceBuilder extends CSVDataSourceBuilder
{
    /**
     * The type of data to get
     */
    public enum DataType
    {
	/**
	 * Air temperature
	 */
	TEMPERATURE(2, (row, adder) ->
	{
	    adder.accept(LocalDate.parse(row.split(";")[2]), Double.parseDouble(row.split(";")[3]));
	}),

	/**
	 * Rain
	 */
	RAIN(7, (row, adder) ->
	{
	    adder.accept(LocalDate.parse(row.split(";")[0]), Double.parseDouble(row.split(";")[2]));
	});

	/**
	 * The parameter to use
	 */
	public final int parameter;

	/**
	 * The data extractor use
	 */
	public final BiConsumer<String, BiConsumer<LocalDate, Double>> data_extractor;

	DataType(int parameter, BiConsumer<String, BiConsumer<LocalDate, Double>> data_extractor)
	{
	    this.parameter = parameter;
	    this.data_extractor = data_extractor;
	}
    }

    private Map<Period, String> periods = new TreeMap<>();

    private DataType dataType;

    /**
     * Sets the datatype
     * 
     * @param dataType
     *            The datatype
     * @return This builder
     */
    public SMHISourceBuilder setDataDype(DataType dataType)
    {
	this.dataType = dataType;
	return this;
    }

    /**
     * Creates a new builder
     * 
     * @param dataType
     *            The datatype to get
     * 
     * @param location
     *            The location to get data from
     * 
     * @throws DataSourceBuilderException
     *             If errors occurs
     */
    public SMHISourceBuilder(DataType dataType, SMHILocation location)
    {
	this((url) -> DataSupplierFactory.createURLFetcher(url).get(), dataType, location);
    }

    /**
     * Creates a new builder
     * 
     * @param dataFetcher
     *            The fetcher that fetches data from the supplied url
     * @param dataType
     *            The datatype to get
     * 
     * @param location
     *            The location to get data from
     * 
     * @throws DataSourceBuilderException
     *             If errors occurs
     */
    @SuppressWarnings("unchecked")
    public SMHISourceBuilder(Function<String, String> dataFetcher, DataType dataType, SMHILocation location)
    {
	setSourceName("SMHI");
	setSourceLink("http://www.smhi.se");

	try
	{
	    for (Map<String, Object> period : (List<Map<String, Object>>) new Genson()
		    .deserialize(dataFetcher.apply(String.format(location.url, dataType.parameter)), Map.class)
		    .get("period"))
	    {
		Period period_span = Period.fromKey((String) period.get("key"));
		if (period_span != null)
		{
		    for (Map<String, Object> link : (List<Map<String, Object>>) period.get("link"))
		    {
			if (link.get("type").equals("application/json"))
			{
			    periods.put(period_span, (String) link.get("href"));
			}
		    }
		}
	    }

	    setNameExtractor((source) -> source.split("\\R+")[1].split(";")[0]);
	    setUnitExtractor((source) -> source.split("\\R+")[3].split(";")[2]);
	    setRowExtractor((source) ->
	    {
		String[] rows = source.split("\\R+");
		for (int i = 0; i < 20; i++)
		    if (rows[i]
			    .startsWith("Från Datum Tid (UTC);Till Datum Tid (UTC);Representativt dygn;")) return Arrays
				    .asList(Arrays.copyOfRange(source.split("\\R+"), i + 1, rows.length));
		throw new DataSourceBuilderException("Incorrect data format!");
	    });
	    setDataExtractor(dataType.data_extractor);
	}
	catch (Exception exception)
	{
	    throw (DataSourceBuilderException) (exception instanceof DataSourceBuilderException ? exception : new DataSourceBuilderException(
		    exception));
	}

    }

    /**
     * Get available periods
     * 
     * @return The available periods
     */
    public Period[] getPeriods()
    {
	return periods.keySet().toArray(new Period[0]);
    }

    /**
     * Check if the period is available
     * 
     * @param period
     *            The period to check
     * @return The result
     */
    public boolean isAvailable(Period period)
    {
	for (Period availible : getPeriods())
	    if (availible.equals(period)) return true;
	return false;
    }

    private Period period = null;

    /**
     * Sets the period
     * 
     * @param period
     *            The period to pick
     * 
     * @return This builder
     */
    public SMHISourceBuilder setPeriod(Period period)
    {
	return setPeriod((url) -> DataSupplierFactory.createURLFetcher(url).get(), period);
    }

    /**
     * Sets the period
     * 
     * @param dataFetcher
     *            The fetcher that fetches data from the supplied url
     * 
     * @param period
     *            The period to pick
     * 
     * @return This builder
     */
    public SMHISourceBuilder setPeriod(Function<String, String> dataFetcher, Period period)
    {
	if (!isAvailable(period)) throw new DataSourceBuilderException("Unavailable period");
	this.period = period;

	setSourceSupplier(() ->
	{
	    @SuppressWarnings("unchecked")
	    String csv_url = ((Map<String, List<Map<String, List<Map<String, String>>>>>) new Genson()
		    .deserialize(dataFetcher.apply(periods.get(this.period)), Map.class))
			    .get("data")
			    .get(0)
			    .get("link")
			    .get(0)
			    .get("href");

	    return dataFetcher.apply(csv_url);
	});

	return this;
    }

    /**
     * Specifies a period to get data from
     */
    public enum Period
    {
	/**
	 * The latest day
	 */
	LATEST("latest-day"),

	/**
	 * The latest four months
	 */
	FOUR_MONTHS("latest-months"),

	/**
	 * Older data
	 */
	OLD("corrected-archive");

	private String key;

	Period(String key)
	{
	    this.key = key;
	}

	/**
	 * Get period based on key
	 * 
	 * @param key
	 *            The key to get the period from
	 * @return The period
	 */
	public static Period fromKey(String key)
	{
	    switch (key)
	    {
	    case "latest-day":
	    case "latest-months":
		return null;
	    default:
		break;
	    }

	    for (Period p : Period.values())
		if (p.key.equals(key)) return p;

	    return null;
	}
    }

    /**
     * Filter to filter temperatures
     */
    @FunctionalInterface
    public interface TemperatureFilter
    {
	/**
	 * Filters temperatures
	 * 
	 * @param date
	 *            The date
	 * @param temperature
	 *            The temperature
	 * @return Whether it should be removed
	 */
	public boolean filter(LocalDate date, Double temperature);
    }

}
