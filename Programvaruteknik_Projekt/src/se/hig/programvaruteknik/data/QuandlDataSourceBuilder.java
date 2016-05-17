package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import se.hig.programvaruteknik.model.DataSource;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.Param;
import se.hig.programvaruteknik.model.SourceGenerator;

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

	public String getName()
	{
	    return name;
	}

	public String getUnit()
	{
	    return unit;
	}

	public String getDescription()
	{
	    return description;
	}

	public String getUrl()
	{
	    return url;
	}

    }
    
    public SourceType source;

    public QuandlDataSourceBuilder()
    {
	setSourceName("quandl");
	setSourceLink("http://www.quandl.com");
    }

    public QuandlDataSourceBuilder(SourceType source)
    {
	this();
	this.source = source;
	setFetchFromWebsite("");  //required, but should be removed
	setListExtractor(
		(data) -> listToMap((((List<List<Object>>) ((Map<String, Object>) data.get("dataset")).get("data")))));
	setDataExtractor(
		(entry, adder) -> adder.accept(LocalDate.parse((String) entry.get("0")), (Double) entry.get("1")));
	setNameExtractor((data) -> ((Map<String, Object>) data.get("dataset")).get("name").toString().split(",")[0]);
	setUnit(source.getUnit());
    }
    
    public void setFetchFromWebsite(String apiKey)
    {
	if (apiKey == null || apiKey == "") apiKey = "?api_key=aS5eE67bVGDB2snkV9Wc";

	setSourceSupplier(
			(DataSupplierFactory.createURLFetcher
					(source.getUrl() + apiKey)));
    }

    @SuppressWarnings("serial")
    public List<Map<String, Object>> listToMap(List<List<Object>> list)
    {
	return list.stream().map((entry) -> new TreeMap<String, Object>()
	{
	    {
		put("0", entry.get(0));
		put("1", entry.get(1));
	    }
	}).collect(Collectors.toList());
    }

    @SourceGenerator("Quandl data")
    public static DataSourceBuilder generate(@Param(value = "type", name = "Type", suggestEnum = SourceType.class, onlyEnum = true) SourceType type)
    {
	return new QuandlDataSourceBuilder(type);
    }
}
