package se.hig.programvaruteknik.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.JSONFormatter;

/**
 * A collection of data
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class DataCollection
{
    private Map<String, MatchedDataPair> data;
    
    private String title;
    
    private String xName;
    private String yName;
    
    private String xUnit;
    private String yUnit;

    private String xSourceName;
    private String xSourceLink;

    private String ySourceLink;
    private String ySourceName;

    /**
     * Set the name of the source of source x
     * 
     * @param xSourceName
     *            The name or null
     */
    public void setXSourceName(String xSourceName)
    {
	this.xSourceName = xSourceName;
    }

    /**
     * Get the name of the source of source x
     * 
     * @return The name or null
     */
    public String getXSourceName()
    {
	return xSourceName;
    }

    /**
     * Set the link of the source of source x
     * 
     * @param xSourceLink
     *            The link or null
     */
    public void setXSourceLink(String xSourceLink)
    {
	this.xSourceLink = xSourceLink;
    }

    /**
     * Get the link of the source of source x
     * 
     * @return The link or null
     */
    public String getXSourceLink()
    {
	return xSourceLink;
    }

    /**
     * Get the name of the source of source y
     * 
     * @return The name or null
     */
    public String getYSourceName()
    {
	return ySourceName;
    }

    /**
     * Get the link of the source of source y
     * 
     * @return The link or null
     */
    public String getYSourceLink()
    {
	return ySourceLink;
    }

    /**
     * Populates a collection of data
     * 
     * @param title
     *            The title for this collection
     * @param sourceX
     *            The data-source for the x values
     * @param sourceY
     *            The data-source for the y values
     * @param data
     *            The data to put in this collection
     */
    public DataCollection(String title, DataSource sourceX, DataSource sourceY, Map<String, MatchedDataPair> data)
    {
	this.data = data;
	this.title = title;

	xUnit = sourceX.getUnit();
	yUnit = sourceY.getUnit();
	
	xName = sourceX.getName();
	yName = sourceY.getName();

	xSourceName = sourceX.getSourceName();
	xSourceLink = sourceX.getSourceLink();

	ySourceName = sourceY.getSourceName();
	ySourceLink = sourceY.getSourceLink();
    }

    /**
     * The title
     * 
     * @return The title
     */
    public String getTitle()
    {
	return title;
    }

    /**
     * Get the unit for the x values
     * 
     * @return The unit for the x values
     */
    public String getXUnit()
    {
	return xUnit;
    }

    /**
     * Get the unit for the y values
     * 
     * @return The unit for the y values
     */
    public String getYUnit()
    {
	return yUnit;
    }
    
    /**
     * Get the name for the x values
     * 
     * @return The name for the x values
     */
    public String getXName()
    {
	return xName;
    }

    /**
     * Get the name for the y values
     * 
     * @return The name for the y values
     */
    public String getYName()
    {
	return yName;
    }

    /**
     * Get the data in this collection
     * 
     * @return The data in this collection
     */
    public Map<String, MatchedDataPair> getData()
    {
	return data;
    }

    @Override
    public String toString()
    {
	return "[DataCollection: " + title + "]";
    }

    /**
     * Returns a JSON representation of this collection
     * 
     * @return The JSON string
     */
    public String asJSON()
    {
	return asJSON(new JSONFormatter());
    }

    /**
     * Returns a JSON representation of this collection
     * 
     * @param formatter
     *            A formatter to format the JSON string with<br>
     *            If null, it is not formatted
     * 
     * @return The JSON string
     */
    @SuppressWarnings("serial")
    public String asJSON(JSONFormatter formatter)
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
	    {
		put("data", new TreeMap<String, Object>()
		{
		    {
			put("name", getTitle());
			put("a_name", getXName());
			put("a_unit", getXUnit());
			put("a_source_name", getXSourceName());
			put("a_source_link", getXSourceLink());
			put("b_name", getYName());
			put("b_unit", getYUnit());
			put("b_source_name", getYSourceName());
			put("b_source_link", getYSourceLink());
			put("data", new TreeMap<String, Object>()
			{
			    {
				for (Entry<String, MatchedDataPair> entry : getData().entrySet())
				    put(entry.getKey(), new TreeMap<String, Object>()
				    {
					{
					    put("a", entry.getValue().getXValue());
					    put("b", entry.getValue().getYValue());
					}
				    });
			    }
			});
		    }
		});
	    }
	}));
    }
}
