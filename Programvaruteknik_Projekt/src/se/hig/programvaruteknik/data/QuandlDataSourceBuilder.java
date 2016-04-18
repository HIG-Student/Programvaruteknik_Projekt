package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import se.hig.programvaruteknik.model.DataSource;

public class QuandlDataSourceBuilder extends JSONDataSourceBuilder 
{
	private String sourceName;
	
	public static enum SourceType 
	{
		DEATH_RATE_CIRRHOSIS("Cirrhosis", "Rate", "Death rate from liver cirrhosis",
				"https://www.quandl.com/api/v3/datasets/NIAAA/CIRRHOSIS.json"),
		
		US_UNEMPLOYMENT_RATE("Unemployment", "Rate", "Unemployment rate in USA",
				"https://www.quandl.com/api/v3/datasets/FRED/LNU03000000.json"),

		GAS_AND_DIESEL_PROD("Production", "Metric tons - thousand", "Gas and diesel production in Sweden",
				"https://www.quandl.com/api/v3/datasets/UN/GASDIESELOILSTOTALPRODUCTION_SWE.json"),
		
		GAS_AND_DIESEL_STOCK("Price variance", "$", "Price variance of gas and diesel in Sweden",
				"https://www.quandl.com/api/v3/datasets/UN/GASDIESELOILSCHANGESINSTOCKS_SWE.json"),
		
		ENROLMENT_EDU_SWE("Education enrolment", "Number", "Enrolment of women in education",
				"https://www.quandl.com/api/v3/datasets/UGEN/ENRL_SWE.json"),
		
		US_CRIME_RATE("Crime rate in USA", "Rate", "USA",
				"https://www.quandl.com/api/v3/datasets/FBI_UCR/USCRIME_TYPE_VIOLENTCRIMERATE.json");
		
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
	
    public QuandlDataSourceBuilder()
    {
	setSourceName("quandl");
	setSourceLink("http://www.quandl.com");
    }

    /**
     * Create a stock-source-builder
     * 
     * @param info
     *            The information that we should get about the stock
     */
    public QuandlDataSourceBuilder(SourceType source)
    {
	    this();
		setSourceType(source);
		setSourceSupplier(DataSupplierFactory.createURLFetcher(source.getUrl()));
		setListExtractor(
				(data) -> listToMap((((List<List<Object>>) ((Map<String, Object>) data.get("dataset")).get("data")))));
		setDataExtractor(
				(entry, adder) -> adder.accept(LocalDate.parse((String) entry.get("0")), (Double) entry.get("1")));
		setNameExtractor((data) -> ((Map<String, Object>) data.get("dataset")).get("name").toString().split(",")[0]);
		setUnit(source.getUnit());
    }

	private void setSourceType(SourceType source) 
	{
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("serial")
	public List<Map<String, Object>> listToMap(List<List<Object>> list) {
		return list.stream().map((entry) -> new TreeMap<String, Object>() {
			{
				put("0", entry.get(0));
				put("1", entry.get(1));
			}
		}).collect(Collectors.toList());
	}

	public static void main(String[] args) {
		QuandlDataSourceBuilder build = new QuandlDataSourceBuilder(SourceType.DEATH_RATE_CIRRHOSIS);
		DataSource source = build.build();
		for (Entry<LocalDate, Double> entry : source.getData().entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
