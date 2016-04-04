package se.hig.programvaruteknik.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import java.util.TreeMap;

/**
 * Generic builder of datasources
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public abstract class DataSourceBuilder
{
    private String sourceName = null;
    private String sourceLink = null;

    /**
     * Set the name of the source
     * 
     * @param sourceName
     *            The name
     * @return This builder
     */
    public DataSourceBuilder setSourceName(String sourceName)
    {
	this.sourceName = sourceName;
	return this;
    }

    /**
     * Set the link of the source
     * 
     * @param sourceLink
     *            The link
     * @return This builder
     */
    public DataSourceBuilder setSourceLink(String sourceLink)
    {
	this.sourceLink = sourceLink;
	return this;
    }

    /**
     * Get the name of the source
     * 
     * @return The name of the source or null
     */
    public String getSourceName()
    {
	return sourceName;
    }

    /**
     * Get the link of the source
     * 
     * @return The link of the source or null
     */
    public String getSourceLink()
    {
	return sourceLink;
    }

    private BiFunction<LocalDate, Double, Boolean> dataFilter;
    private BiFunction<LocalDate, List<Double>, Double> dataReducer = REDUCER_SUM;

    /**
     * Reduces conflicting keys by summing them
     */
    public static final BiFunction<LocalDate, List<Double>, Double> REDUCER_SUM = (key, list) -> list
	    .stream()
	    .reduce(Double::sum)
	    .get();

    /**
     * The name of the datasource
     */
    public final CachedValue<String> name = new CachedValue<>();

    /**
     * Sets the data filter
     * 
     * @param dataFilter
     *            The data filter
     * @return This builder
     */
    public DataSourceBuilder setDataFilter(BiFunction<LocalDate, Double, Boolean> dataFilter)
    {
	this.dataFilter = dataFilter;
	return this;
    }

    /**
     * Sets the data reducer<br>
     * <br>
     * Reduces data with same key to a single value
     * 
     * @param dataReducer
     *            The data reducer
     * @return This builder
     */
    public DataSourceBuilder setDataReducer(BiFunction<LocalDate, List<Double>, Double> dataReducer)
    {
	this.dataReducer = dataReducer;
	return this;
    }

    /**
     * Sets the name of the datasource
     * 
     * @param name
     *            The name
     * @return This builder
     */
    public DataSourceBuilder setName(String name)
    {
	this.name.updateSupplier(() -> name);
	return this;
    }

    /**
     * The unit of the datasource
     */
    public final CachedValue<String> unit = new CachedValue<>();

    /**
     * Sets the unit of the datasource
     * 
     * @param unit
     *            The unit
     * @return This builder
     */
    public DataSourceBuilder setUnit(String unit)
    {
	this.unit.updateSupplier(() -> unit);
	return this;
    }

    protected abstract Map<LocalDate, List<Double>> generateData();

    /**
     * Builds the data source
     * 
     * @return The data source
     * @throws DataSourceBuilderException
     *             If errors occurs
     */
    public final DataSource build()
    {
	return new DataSource()
	{
	    private Map<LocalDate, Double> data;

	    private String name = null;
	    private String unit = null;

	    private String sourceName = null;
	    private String sourceLink = null;

	    {
		try
		{
		    Map<LocalDate, List<Double>> generatedData = generateData();
		    if (generatedData == null) throw new DataSourceBuilderException("Missing data");
		    if (!DataSourceBuilder.this.name
			    .canGiveValue()) throw new DataSourceBuilderException("Missing name");
		    if (!DataSourceBuilder.this.unit
			    .canGiveValue()) throw new DataSourceBuilderException("Missing unit");
		    if (dataReducer == null) throw new DataSourceBuilderException("Missing reducer");

		    name = DataSourceBuilder.this.name.get();
		    unit = DataSourceBuilder.this.unit.get();

		    sourceName = DataSourceBuilder.this.getSourceName();
		    sourceLink = DataSourceBuilder.this.getSourceLink();

		    Map<LocalDate, Double> rawData = new TreeMap<>();
		    for (Entry<LocalDate, List<Double>> entry : generatedData.entrySet())
		    {
			Double value = dataReducer.apply(entry.getKey(), entry.getValue());
			if (dataFilter == null || !dataFilter.apply(entry.getKey(), value))
			{
			    rawData.put(entry.getKey(), value);
			}
		    }

		    data = Collections.unmodifiableMap(rawData);
		}
		catch (Exception exception)
		{
		    throw (DataSourceBuilderException) (exception instanceof DataSourceBuilderException ? exception : new DataSourceBuilderException(
			    exception));
		}
	    }

	    @Override
	    public String getUnit()
	    {
		return unit;
	    }

	    @Override
	    public String getName()
	    {
		return name;
	    }

	    @Override
	    public String getSourceName()
	    {
		return sourceName;
	    }

	    @Override
	    public String getSourceLink()
	    {
		return sourceLink;
	    }

	    @Override
	    public Map<LocalDate, Double> getData()
	    {
		return data;
	    }
	};
    }

    /**
     * Indicates errors when building a rain source
     */
    @SuppressWarnings("serial")
    public class DataSourceBuilderException extends RuntimeException
    {
	/**
	 * Create exception
	 * 
	 * @param exception
	 *            The exception that resulted in this exception
	 */
	public DataSourceBuilderException(Exception exception)
	{
	    super(exception);
	}

	/**
	 * Create exception
	 * 
	 * @param exception
	 *            The reason
	 */
	public DataSourceBuilderException(String exception)
	{
	    super(exception);
	}
    }
}
