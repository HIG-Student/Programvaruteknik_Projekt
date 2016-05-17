package se.hig.programvaruteknik.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.data.ConstantSourceBuilder;
import se.hig.programvaruteknik.data.QuandlDataSourceBuilder;
import se.hig.programvaruteknik.data.SMHILocation;
import se.hig.programvaruteknik.data.SMHISourceBuilder;
import se.hig.programvaruteknik.data.SMHISourceBuilder.DataType;
import se.hig.programvaruteknik.data.SMHISourceBuilder.Period;
import se.hig.programvaruteknik.data.StockSourceBuilder;
import se.hig.programvaruteknik.data.StockSourceBuilder.StockInfo;
import se.hig.programvaruteknik.database.DataHandler;
import se.hig.programvaruteknik.database.DatabaseDataHandler;
import se.hig.programvaruteknik.database.MemoryDataHandler;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.MergeType;
import se.hig.programvaruteknik.model.Resolution;
import se.hig.programvaruteknik.JSONFormatter;
import se.hig.programvaruteknik.JSONOutputter;

/**
 * Servlet implementation class SampleServlet
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
@WebServlet("/SampleServlet")
public class StatisticsServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;

    protected DataCollectionBuilder builder;
    protected JSONFormatter JSON_Formatter;
    protected Function<InputStream, Map<String, Object>> JSONConverter;
    protected DataSourceGenerator dataSourceGenerator;
    protected DataHandler dataHandler;

    /**
     * Server entry-point
     */
    public StatisticsServlet()
    {
	this(
		new JSONFormatter(),
		new DataCollectionBuilder(),
		(stream) -> new Genson().deserialize(stream, TreeMap.class),
		new DataSourceGenerator(
			ConstantSourceBuilder.class,
			StockSourceBuilder.class,
			QuandlDataSourceBuilder.class),
		new DatabaseDataHandler());
    }

    /**
     * Use custom JSONFormatter
     * 
     * @param JSONFormatter
     *            The formatter to use (can be null for no formatting)
     * @param builder
     *            The builder to use for building collections
     * @param JSONConverter
     *            A function that can convert a JSON InputStream to a Map
     *            <String,Object>
     * @param dataSaver
     *            A datasaver that saves data
     * 
     */
    public StatisticsServlet(JSONFormatter JSONFormatter, DataCollectionBuilder builder, Function<InputStream, Map<String, Object>> JSONConverter, DataSourceGenerator dataSourceGenerator, DataHandler dataSaver)
    {
	this.JSON_Formatter = JSONFormatter;
	this.builder = builder;
	this.JSONConverter = JSONConverter;
	this.dataSourceGenerator = dataSourceGenerator;
	this.dataHandler = dataSaver;
    }

    @Override
    protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
    {

    }

    @SuppressWarnings(
    {
	    "unchecked",
	    "rawtypes"
    })
    protected <E extends Enum> E getEnum(Class<E> enumType, String name, String onError)
    {
	try
	{
	    return (E) Enum.valueOf(enumType, name);
	}
	catch (Exception e)
	{
	    throw new RequestException(onError);
	}
    }

    protected DataSourceBuilder getDataSource(Map<String, Object> data)
    {
	String sourceType = getValue(data, "source-type");

	if ("Constant".equalsIgnoreCase(sourceType)) return new ConstantSourceBuilder();

	if ("Stock".equalsIgnoreCase(sourceType))
	{
	    StockSourceBuilder builder = new StockSourceBuilder();

	    if (data.containsKey("stock-info"))
	    {
		builder.setStockInfo(
			tryOrThrow(
				() -> StockInfo.valueOf((String) getValue(data, "stock-info")),
				"Invalid 'stock-info'"));
	    }
	    else
		builder.setStockInfo(StockInfo.PRICE);

	    String stockName = getValue(data, "stock-name");

	    if (!StockSourceBuilder
		    .checkValidName(stockName)) throw new RequestException("Invalid stock name: '" + stockName + "'");
	    builder.setStock(stockName, Integer.MAX_VALUE);

	    return builder;
	}

	if ("Weather".equalsIgnoreCase(sourceType))
	{
	    String weatherLocation = getValue(data, "weather-location");

	    SMHISourceBuilder builder = new SMHISourceBuilder(
		    DataType.TEMPERATURE,
		    getEnum(SMHILocation.class, weatherLocation, "Invalid location: '" + weatherLocation + "'"));

	    builder.setPeriod(Period.OLD);

	    return builder;
	}

	throw new RequestException("Unknown 'source-type': " + sourceType);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	response.setCharacterEncoding("UTF-8");

	try
	{
	    request.getRequestDispatcher("viewer.html").forward(request, response);
	}
	catch (Throwable e)
	{
	    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error!");
	    e.printStackTrace();
	}
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	response.setCharacterEncoding("UTF-8");
	response.setContentType("application/json;charset=UTF-8");

	try
	{
	    Map<String, Object> body = JSONConverter.apply(request.getInputStream());

	    if (body == null) throw new RequestException("Need data!");

	    Boolean pretty = getValue(body, "pretty", (String str) -> "true".equalsIgnoreCase(str), false);

	    String actionType = getValue(body, "type");

	    JSONOutputter outputter;

	    if ("save".equalsIgnoreCase(actionType))
	    {
		Map<String, Object> raw_json = getValue(body, "data");
		String json = new Genson().serialize(raw_json);
		String title = getValue(raw_json, "title");

		Long savedAt_Index = dataHandler.save(title, json);

		outputter = new JSONOutputter()
		{
		    @Override
		    public String asJSON(JSONFormatter formatter)
		    {
			Map<String, Object> result = new TreeMap<>();
			Map<String, Object> data = new TreeMap<>();
			data.put("id", savedAt_Index);
			data.put("title", title);
			result.put("data", data);
			return formatter.format(new Genson().serialize(result));
		    }
		};
	    }
	    else if ("delete".equalsIgnoreCase(actionType))
	    {
		Long index;
		Object longing = getValue(body, "data");
		if (longing instanceof String)
		    index = Long.parseLong((String) longing);
		else
		    index = (Long) longing;

		outputter = new JSONOutputter()
		{
		    @Override
		    public String asJSON(JSONFormatter formatter)
		    {
			Map<String, Object> result = new TreeMap<>();
			result.put("data", dataHandler.delete(index));
			return formatter.format(new Genson().serialize(result));
		    }
		};
	    }
	    else if ("load".equalsIgnoreCase(actionType))
	    {
		Long index;
		Object longing = getValue(body, "data");
		if (longing instanceof String)
		    index = Long.parseLong((String) longing);
		else
		    index = (Long) longing;

		Object loaded_data = new Genson().deserialize(dataHandler.load(index), TreeMap.class);

		outputter = new JSONOutputter()
		{
		    @Override
		    public String asJSON(JSONFormatter formatter)
		    {
			Map<String, Object> result = new TreeMap<>();
			result.put("data", loaded_data);
			return formatter.format(new Genson().serialize(result));
		    }
		};
	    }
	    else if ("list".equalsIgnoreCase(actionType))
	    {
		outputter = new JSONOutputter()
		{
		    @Override
		    public String asJSON(JSONFormatter formatter)
		    {
			Map<String, Object> result = new TreeMap<>();
			result.put("data", dataHandler.getList());
			return formatter.format(new Genson().serialize(result));
		    }
		};
	    }
	    else if ("sources".equalsIgnoreCase(actionType))
	    {
		outputter = new JSONOutputter()
		{
		    @Override
		    public String asJSON(JSONFormatter formatter)
		    {
			Map<String, Object> result = new TreeMap<>();
			result.put("data", dataSourceGenerator.getSources());

			return formatter.format(new Genson().serialize(result));
		    }
		};
	    }
	    else
	    {
		Map<String, Object> data = getValue(body, "data");

		Resolution resolution = getValue(
			body,
			"resolution",
			(String str) -> Resolution.valueOf(str),
			Resolution.DAY);

		if ("data-source".equalsIgnoreCase(actionType))
		{
		    DataSourceBuilder builder = dataSourceGenerator.getBuilder(getValue(data, "source"));
		    outputter = builder.build();
		}
		else if ("data-correlation".equalsIgnoreCase(actionType))
		{
		    MergeType mergeTypeX = getValue(
			    body,
			    "merge-type-x",
			    (String str) -> MergeType.fromString(str),
			    MergeType.AVERAGE);

		    MergeType mergeTypeY = getValue(
			    body,
			    "merge-type-y",
			    (String str) -> MergeType.fromString(str),
			    MergeType.AVERAGE);

		    DataSourceBuilder sourceA = dataSourceGenerator.getBuilder(getValue(data, "sourceA"));
		    DataSourceBuilder sourceB = dataSourceGenerator.getBuilder(getValue(data, "sourceB"));

		    builder.setXDatasource(sourceA.build());
		    builder.setYDatasource(sourceB.build());

		    builder.setXMergeType(mergeTypeX);
		    builder.setYMergeType(mergeTypeY);
		    builder.setResolution(resolution);

		    outputter = builder.getResult();
		}
		else
		{
		    throw new RequestException("Unknown type!");
		}
	    }
	    response.getWriter().append(outputter.asJSON(pretty ? JSON_Formatter : JSONFormatter.NONE));
	}
	catch (RequestException e)
	{
	    response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	    e.printStackTrace();
	}
	catch (Throwable e)
	{
	    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error!");
	    e.printStackTrace();
	}
    }

    private <T> T getValue(Map<String, Object> map, String key, T defaultValue)
    {
	if (map.containsKey(key))
	{
	    try
	    {
		return (T) map.get(key);
	    }
	    catch (Exception e)
	    {

	    }
	}

	return defaultValue;
    }

    private <T> T getValue(Map<String, Object> map, String key)
    {
	if (!map.containsKey(key)) throw new RequestException("'" + key + "' is required!");

	try
	{
	    return (T) map.get(key);
	}
	catch (Exception e)
	{
	    throw new RequestException("'" + key + "' is invalid!");
	}
    }

    private <T, V> T getValue(Map<String, Object> map, String key, Function<V, T> func)
    {
	if (!map.containsKey(key)) throw new RequestException("'" + key + "' is required!");

	try
	{
	    return func.apply((V) map.get(key));
	}
	catch (Exception e)
	{
	    throw new RequestException("'" + key + "' is invalid!");
	}
    }

    private <T, V> T getValue(Map<String, Object> map, String key, Function<V, T> func, T defaultValue)
    {
	if (map.containsKey(key))
	{
	    try
	    {
		return func.apply((V) map.get(key));
	    }
	    catch (Exception e)
	    {

	    }
	}

	return defaultValue;
    }

    private <T> T tryOrThrow(Supplier<T> supplier, String error)
    {
	try
	{
	    return supplier.get();
	}
	catch (Exception e)
	{
	    throw new RequestException(error);
	}
    }

    @SuppressWarnings("serial")
    private class RequestException extends RuntimeException
    {
	private RequestException(String e)
	{
	    super(e);
	}
    }
}
