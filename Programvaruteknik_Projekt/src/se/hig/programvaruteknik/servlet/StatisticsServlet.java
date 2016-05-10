package se.hig.programvaruteknik.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.data.ConstantSourceBuilder;
import se.hig.programvaruteknik.data.QuandlDataSourceBuilder;
import se.hig.programvaruteknik.data.SMHILocation;
import se.hig.programvaruteknik.data.SMHISourceBuilder;
import se.hig.programvaruteknik.data.SMHISourceBuilder.DataType;
import se.hig.programvaruteknik.data.SMHISourceBuilder.Period;
import se.hig.programvaruteknik.data.StockSourceBuilder;
import se.hig.programvaruteknik.data.StockSourceBuilder.StockInfo;
import se.hig.programvaruteknik.data.StockSourceBuilder.StockName;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.MergeType;
import se.hig.programvaruteknik.model.Param;
import se.hig.programvaruteknik.model.Resolution;
import se.hig.programvaruteknik.model.SourceGenerator;
import se.hig.programvaruteknik.JSONFormatter;
import se.hig.programvaruteknik.JSONOutputter;
import se.hig.programvaruteknik.Utils;

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

    public void testing()
    {
		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/webapp");

			Connection connection = ds.getConnection();

			try {
				String sql = "INSERT INTO data(data, title) VALUES (?, ?)";

				PreparedStatement statement = connection.prepareStatement(sql);
				statement.setString(1, "Dennis har inga Gnuer");
				statement.setString(2, "42");
				statement.execute();

			} catch (SQLException e) {

				System.out.println("Connection Failed! Check output console");
				e.printStackTrace();
				return;

			}

		} catch (Exception e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");
    }
    
    /**
     * Server entry-point
     */
    public StatisticsServlet()
    {
	this(
		new JSONFormatter(),
		new DataCollectionBuilder(),
		(stream) -> new Genson().deserialize(stream, Map.class),
		new DataSourceGenerator(
			ConstantSourceBuilder.class,
			StockSourceBuilder.class,
			QuandlDataSourceBuilder.class));
	
	testing();
	System.out.println("GO!");
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
     */
    public StatisticsServlet(JSONFormatter JSONFormatter, DataCollectionBuilder builder, Function<InputStream, Map<String, Object>> JSONConverter, DataSourceGenerator dataSourceGenerator)
    {
	this.JSON_Formatter = JSONFormatter;
	this.builder = builder;
	this.JSONConverter = JSONConverter;
	this.dataSourceGenerator = dataSourceGenerator;
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

	    String sourceType = getValue(body, "type");

	    JSONOutputter outputter;

	    if ("sources".equalsIgnoreCase(sourceType))
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

		if ("data-source".equalsIgnoreCase(sourceType))
		{
		    DataSourceBuilder builder = dataSourceGenerator.getBuilder(getValue(data, "source"));
		    outputter = builder.build();
		}
		else
		    if ("data-correlation".equalsIgnoreCase(sourceType))
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
