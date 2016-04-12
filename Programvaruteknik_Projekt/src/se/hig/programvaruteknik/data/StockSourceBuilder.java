package se.hig.programvaruteknik.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import se.hig.programvaruteknik.Utils;

import static se.hig.programvaruteknik.Utils.*;

/**
 * Builds sources from
 * <a href="http://www.kibot.com/api/historical_data_api_sdk.aspx">kibot</a>
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class StockSourceBuilder extends CSVDataSourceBuilder
{
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final String URL = "http://api.kibot.com/?action=history&symbol=%s&interval=daily&period=%d";

    private String stockName;
    private String stockType;

    /**
     * The information to get about the stock
     */
    public static enum StockInfo
    {
	/**
	 * The volume of the stock
	 */
	VOLUME("Volym", "st", "Mängden aktier som har handlats med", 5),

	/**
	 * The end price of the stock
	 */
	PRICE("Pris", "$", "Priset på aktien", 4),

	/**
	 * The change of the stock
	 */
	CHANGE("Ändring", "$", "Prisändring på aktien", (data) ->
	{
	    return (Double.parseDouble(data[4]) - Double.parseDouble(data[1]));
	}),

	/**
	 * The difference between the max and min values
	 */
	FLUCTUATION("Ostadighet", "$", "Skiljnaden på högsta och lägsta värdet", (data) ->
	{
	    return (Double.parseDouble(data[2]) - Double.parseDouble(data[3]));
	});

	private String name;
	private String unit;
	private String description;
	private Function<String[], Double> extractor;

	/**
	 * Get the name of the stock information type
	 * 
	 * @return The name
	 */
	public String getName()
	{
	    return name;
	}

	/**
	 * Get the unit of the stock information type
	 * 
	 * @return The unit
	 */
	public String getUnit()
	{
	    return unit;
	}

	/**
	 * Get the description of the stock information type
	 * 
	 * @return The description
	 */
	public String getDescription()
	{
	    return description;
	}

	private Function<String[], Double> getExtractor()
	{
	    return extractor;
	}

	private StockInfo(String name, String unit, String description, Integer index)
	{
	    this(name, unit, description, (data) -> Double.parseDouble(data[index]));
	}

	private StockInfo(String name, String unit, String description, Function<String[], Double> extractor)
	{
	    this.name = name;
	    this.unit = unit;
	    this.description = description;
	    this.extractor = extractor;
	}
    }

    /**
     * The name of the stock
     */
    @SuppressWarnings("javadoc") // Javadoc for each enum? >.<
    public static enum StockName
    {
	MSFT("Microsoft", "https://www.microsoft.com"),

	IBM("International Business Machines", "http://www.ibm.com"),

	XOM("Exxon Mobil Corp", null),

	WFC("Wells Fargo & Co", null),

	JPM("JPMorgan Chase & Co", null),

	T("AT&T Inc", null),

	PG("Procter & Gamble Co/The", null),

	CVX("Chevron Corp", null),

	BAC("Bank of America Corp", null),

	JNJ("Johnson & Johnson", null),

	GE("General Electric Co", null),

	OIH("Market Vectors Oil Services ETF", null),

	WDC("Western Digital", null),

	IVE("S&P 500 Value Index", null);

	private String description;
	private String url;

	private StockName(String description, String url)
	{
	    this.description = description;
	    this.url = url;
	}

	/**
	 * Get description about this stock name
	 * 
	 * @return The description
	 */
	public String getDescription()
	{
	    return description;
	}

	/**
	 * Get an url related to this stock name
	 * 
	 * @return The url
	 */
	public String getUrl()
	{
	    return url;
	}

	@Override
	public String toString()
	{
	    return name() + ": " + description;
	}
    }

    private String getData(StockName stock, Integer amount)
    {
	return getData(stock.name(), amount);
    }

    // Inspiration from
    // http://stackoverflow.com/questions/6467848/how-to-get-http-response-code-for-a-url-in-java
    private String getData(String stockName, Integer amount)
    {
	try
	{
	    HttpURLConnection connection = (HttpURLConnection) new URL(
		    String.format(URL, stockName.toUpperCase(), Math.min(amount, Integer.MAX_VALUE))).openConnection();
	    connection.connect();

	    if (connection.getResponseCode() != 200) throw new StockSourceBuilderException(
		    "Error: " + connection.getResponseMessage());

	    String response = readStream(connection.getInputStream());

	    if ("401 Not Logged In".equals(response))
	    {
		{
		    connection = (HttpURLConnection) new URL(
			    "http://api.kibot.com/?action=login&user=guest&password=guest").openConnection();
		    connection.setRequestMethod("GET");
		    connection.connect();
		    response = readStream(connection.getInputStream());
		    if ("403 Login Failed".equals(response)) throw new StockSourceBuilderException(
			    "Cannot login with guest account to kibot");
		}

		{
		    connection = (HttpURLConnection) new URL(
			    String.format(URL, stockName.toUpperCase(), Math.min(amount, Integer.MAX_VALUE)))
				    .openConnection();
		    connection.connect();
		    response = readStream(connection.getInputStream());
		    if ("401 Not Logged In".equals(response)) throw new StockSourceBuilderException(
			    "Cannot login with guest account to kibot");
		}
	    }

	    if ("404 Symbol Not Found"
		    .equals(response)) throw new StockSourceBuilderException("Invalid stock name: " + stockName);

	    return response;
	}
	catch (Exception e)
	{
	    if (e instanceof StockSourceBuilderException)
		throw (StockSourceBuilderException) e;
	    else
		throw new StockSourceBuilderException(e);
	}
    }

    /**
     * Checks if the supplied string is a valid stock name
     * 
     * @param name
     *            The name to check
     * @return True if it is a valid name, else false
     */
    public static boolean checkValidName(String name)
    {
	name = name.toUpperCase();

	for (StockName stock : StockName.values())
	{
	    if (stock.name().equals(name)) return true;
	}

	try
	{
	    HttpURLConnection login = (HttpURLConnection) new URL(String.format(URL, name, 1)).openConnection();
	    login.setRequestMethod("GET");
	    login.connect();
	    return !"404 Symbol Not Found".equals(Utils.readStream(login.getInputStream()));
	}
	catch (IOException e)
	{
	    return false;
	}
    }

    /**
     * Create a stock-source-builder
     * 
     */
    public StockSourceBuilder()
    {
	setSourceName("kibot");
	setSourceLink("http://http://www.kibot.com");

	setNameExtractor((source) -> stockName + " , " + stockType);
    }

    /**
     * Create a stock-source-builder
     * 
     * @param info
     *            The information that we should get about the stock
     */
    public StockSourceBuilder(StockInfo info)
    {
	this();
	setStockInfo(info);
    }

    /**
     * Sets the data to get
     * 
     * @param info
     *            The data to get from the stock
     * @return This builder
     */
    public StockSourceBuilder setStockInfo(StockInfo info)
    {
	stockType = info.getName();
	setUnit(info.getUnit());

	setRowExtractor(DEFAULT_ROWEXTRACTOR);
	setDataExtractor((line, adder) ->
	{
	    String[] data = line.split(",");
	    adder.accept(LocalDate.parse(data[0], DATE_FORMATTER), info.getExtractor().apply(data));
	});
	return this;
    }

    /**
     * Sets the stock to build data about
     * 
     * @param stock
     *            The stock to get data about
     * @param amount
     *            The amount of data to get
     * @return This builder
     */
    public StockSourceBuilder setStock(StockName stock, Integer amount)
    {
	stockName = stock.name();
	setSourceSupplier(() -> getData(stock, amount));
	return this;
    }

    /**
     * Sets the stock to build data about
     * 
     * @param stock
     *            The stock name to get data about
     * @param amount
     *            The amount of data to get
     * @return This builder
     * 
     * @throws StockSourceBuilderException
     *             If the stock name is invalid
     */
    public StockSourceBuilder setStock(String stock, Integer amount)
    {
	stockName = stock;
	setSourceSupplier(() -> getData(stock, amount));
	return this;
    }

    /**
     * Indicates exception when building source from the kibot
     */
    @SuppressWarnings("serial")
    public class StockSourceBuilderException extends DataSourceBuilderException
    {

	/**
	 * Create exception
	 * 
	 * @param exception
	 *            The exception that resulted in this exception
	 */
	public StockSourceBuilderException(Exception exception)
	{
	    super(exception);
	}

	/**
	 * Create exception
	 * 
	 * @param exception
	 *            The reason
	 */
	public StockSourceBuilderException(String exception)
	{
	    super(exception);
	}
    }
}
