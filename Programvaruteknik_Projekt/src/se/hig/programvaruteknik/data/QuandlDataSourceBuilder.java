package se.hig.programvaruteknik.data;

import se.hig.programvaruteknik.data.StockSourceBuilder.StockInfo;

public class QuandlDataSourceBuilder extends JSONDataSourceBuilder {
	private String sourceName;
	
	public static enum SourceType {
		DEATH_RATE_CIRRHOSIS("Deathrate from liver cirrhosis", "Rate", "USA", "https://www.quandl.com/api/v3/datasets/NIAAA/CIRRHOSIS.json");
		
		private String name;
		private String unit;
		private String description;
		private String url;
		
		private SourceType(String name, String unit, String description, String url) {
			this.name = name;
			this.unit = unit;
			this.description = description;
			this.url = url;
		}
	}
	
    public QuandlDataSourceBuilder()
    {
	setSourceName("kibot");
	setSourceLink("http://http://www.kibot.com");

	setNameExtractor((source) -> sourceName);
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
    }

	private void setSourceType(SourceType source) {
		// TODO Auto-generated method stub
		
	}
	
	
}
