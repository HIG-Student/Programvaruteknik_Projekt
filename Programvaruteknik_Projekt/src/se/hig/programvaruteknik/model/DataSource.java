package se.hig.programvaruteknik.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.JSONFormatter;
import se.hig.programvaruteknik.JSONOutputter;

/**
 * Interface representing an source of data
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public interface DataSource extends JSONOutputter
{
    /**
     * Get the name of the data source
     * 
     * @return The name
     */
    public String getName();

    /**
     * Get the unit of the data source
     * 
     * @return The unit
     */
    public String getUnit();

    /**
     * Get the name of the source or null
     * 
     * @return The name
     */
    public default String getSourceName()
    {
	return null;
    }

    /**
     * Get the link of the source or null
     * 
     * @return The unit
     */
    public default String getSourceLink()
    {
	return null;
    }

    /**
     * Get the data in the data source
     * 
     * @return The data
     */
    public Map<LocalDate, Double> getData();

    /**
     * Represent an error related to the datasource
     */
    public class DataSourceException extends Exception
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create an exception
	 * 
	 * @param exception
	 *            The source exception
	 */
	public DataSourceException(Exception exception)
	{
	    super(exception);
	}

	/**
	 * Create an exception
	 * 
	 * @param exception
	 *            The reason
	 */
	public DataSourceException(String exception)
	{
	    super(exception);
	}
    }

    /**
     * Returns a JSON representation of this source
     * 
     * @param formatter
     *            A formatter to format the JSON string with<br>
     *            If null, it is not formatted
     * 
     * @return The JSON string
     */
    @Override
    public default String asJSON(JSONFormatter formatter)
    {
	if (formatter == null) formatter = new JSONFormatter()
	{
	    @Override
	    public String format(String JSON)
	    {
		return JSON;
	    }
	};

	return formatter.format(new Genson().serialize(new TreeMap<String, Object>()
	{
	    /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

	    {
		put("data", new TreeMap<String, Object>()
		{
		    /**
		     * 
		     */
		    private static final long serialVersionUID = 1L;

		    {
			put("name", getName());
			put("unit", getUnit());
			put("source_name", getSourceName());
			put("source_link", getSourceLink());
			put("data", new TreeMap<String, Double>()
			{
			    /**
			     * 
			     */
			    private static final long serialVersionUID = 1L;

			    {
				for (Entry<LocalDate, Double> entry : getData().entrySet())
				    put(entry.getKey().toString(), entry.getValue());
			    }
			});
		    }
		});
	    }
	}));
    }
}
