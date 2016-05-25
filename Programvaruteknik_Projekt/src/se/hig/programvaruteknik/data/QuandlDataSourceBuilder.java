package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.Param;
import se.hig.programvaruteknik.model.SourceGenerator;

/**
 * Building data from Quandl
 * <a href="https://www.quandl.com/browse">Quandl</a>
 * 
 * @author Miran Batti (kommer)
 * @author Fredrik Lindorf (ntn13flf@student.hig.se)
 * docs Fredrik
 */
public class QuandlDataSourceBuilder extends JSONDataSourceBuilder
{

    public static enum SourceType
    {
	DEATH_RATE_CIRRHOSIS_USA("Cirrhosis", "Rate", "Death rate from liver cirrhosis", "https://www.quandl.com/api/v3/datasets/NIAAA/CIRRHOSIS.json"),

	UNEMPLOYMENT_RATE_USA("Unemployment", "Rate", "Unemployment rate in USA", "https://www.quandl.com/api/v3/datasets/FRED/LNU03000000.json"),

	ALCOHOL_CONSUMPTION_USA("Alcohol consumption", "%", "Percentage of population in USA consuming alcohol", "https://www.quandl.com/api/v3/datasets/NIAAA/DRINKERS.json"),

	GAS_AND_DIESEL_PROD_SWE("Production", "Metric tons - thousand", "Gas and diesel production in Sweden", "https://www.quandl.com/api/v3/datasets/UN/GASDIESELOILSTOTALPRODUCTION_SWE.json"),

	GAS_AND_DIESEL_STOCK_SWE("Price variance", "$", "Price variance of gas and diesel in Sweden", "https://www.quandl.com/api/v3/datasets/UN/GASDIESELOILSCHANGESINSTOCKS_SWE.json"),

	ENROLMENT_EDU_SWE("Education enrolment", "Number", "Enrolment of women in education", "https://www.quandl.com/api/v3/datasets/UGEN/ENRL_SWE.json"),

	EMPLOYMENT_RATE_OVERALL_SWE("Employment", "Value", "Employment ", "https://www.quandl.com/api/v3/datasets/ODA/SWE_LE.json");

	private String name;
	private String unit;
	private String description;
	private String url;

	private SourceType(String name, String unit, String description, String url)
	{
	    this.name = name;
	    this.unit = unit;
	    this.description = description;
	    this.url = url;
	}

	/**
	 * The name of the data source
	 * 
	 * @return the name
	 */
	public String getName()
	{
	    return name;
	}

	/**
	 * The unit of the data source
	 * 
	 * @return the unit
	 */
	public String getUnit()
	{
	    return unit;
	}

	/**
	 * The description of the data source
	 * 
	 * @return the description
	 */
	public String getDescription()
	{
	    return description;
	}

	/**
	 * The url of the data source
	 * 
	 * @return the url
	 */
	public String getUrl()
	{
	    return url;
	}
    }

    public SourceType source;

    /**
     * Builder for quandl data sources
     */
    public QuandlDataSourceBuilder()
    {
	setSourceName("quandl");
	setSourceLink("http://www.quandl.com");
    }
    
    /**
     * Builds specific data sources for example
     * QuandlDataSource DEATH_RATE_CIRRHOSIS_USA
     * 
     * @param source
     */
    public QuandlDataSourceBuilder(SourceType source)
    {
	this();
	this.source = source;
	setFetchFromWebsite(""); // required, but should be removed
	setListExtractor(
		(data) -> listToMap((((List<List<Object>>) ((Map<String, Object>) data.get("dataset")).get("data")))));
	setDataExtractor(
		(entry, adder) -> adder.accept(LocalDate.parse((String) entry.get("0")), (Double) entry.get("1")));
	setNameExtractor((data) -> ((Map<String, Object>) data.get("dataset")).get("name").toString().split(",")[0]);
	setUnit(source.getUnit());
    }

    /**
     * Sets the data fetched from www.quandl.com
     * 
     * @param apiKey
     */
    public void setFetchFromWebsite(String apiKey)
    {
	if (apiKey == null || apiKey == "") apiKey = "?api_key=aS5eE67bVGDB2snkV9Wc";

	setSourceSupplier((DataSupplierFactory.createURLFetcher(source.getUrl() + apiKey)));
    }
    
    private List<Map<String, Object>> listToMap(List<List<Object>> list)
    {
	return list.stream().map((entry) -> new TreeMap<String, Object>()
	{
	    /**
	     * 
	     */
	    private static final long serialVersionUID = 1L;

	    {
		put("0", entry.get(0));
		put("1", entry.get(1));
	    }
	}).collect(Collectors.toList());
    }
    
    
    /**
     * special method for gathering data to dropdown boxes<br>
     * <br>
     * based on matching parameters
     * @param type
     * @return QuandlDataSourceBuilder
     */
    @SourceGenerator("Quandl data")
    public static DataSourceBuilder generate(@Param(value = "type", name = "Type", suggestEnum = SourceType.class, onlyEnum = true) SourceType type)
    {
	return new QuandlDataSourceBuilder(type);
    }
}
