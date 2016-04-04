package se.hig.programvaruteknik.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.data.ConstantSourceBuilder;
import se.hig.programvaruteknik.data.SMHILocation;
import se.hig.programvaruteknik.data.SMHISourceBuilder;
import se.hig.programvaruteknik.data.SMHISourceBuilder.DataType;
import se.hig.programvaruteknik.data.SMHISourceBuilder.Period;
import se.hig.programvaruteknik.data.StockSourceBuilder;
import se.hig.programvaruteknik.data.StockSourceBuilder.StockInfo;
import se.hig.programvaruteknik.data.StockSourceBuilder.StockName;
import se.hig.programvaruteknik.model.DataCollectionBuilder;
import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.Resolution;
import se.hig.programvaruteknik.JSONFormatter;

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
    protected JSONFormatter JSONFormatter;

    /**
     * Server entry-point
     */
    public StatisticsServlet()
    {
	this(new JSONFormatter(), new DataCollectionBuilder());
    }

    /**
     * Use custom JSONFormatter
     * 
     * @param JSONFormatter
     *            The formatter to use (can be null for no formatting)
     * @param builder
     *            The builder to use for building collections
     */
    public StatisticsServlet(JSONFormatter JSONFormatter, DataCollectionBuilder builder)
    {
	this.JSONFormatter = JSONFormatter;
	this.builder = builder;
    }

    @SuppressWarnings("serial")
    @Override
    protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException
    {
	Map<String, Object> info = new TreeMap<>();

	info.put("StockInformation", new TreeMap<String, Object>()
	{
	    {
		put("types", new TreeMap<String, Object>()
		{
		    {
			put("type", "fixed");
			put("values", new TreeMap<String, Object>()
			{
			    {
				for (StockInfo info : StockInfo.values())
				{
				    put(info.name(), new TreeMap<String, String>()
				    {
					{
					    put("name", info.getName());
					    put("description", info.getDescription());
					}
				    });
				}
			    }
			});
		    }
		});
		put("names", new TreeMap<String, Object>()
		{
		    {
			put("type", "custom");
			put("values", new TreeMap<String, Object>()
			{
			    {
				for (StockName name : StockName.values())
				{
				    put(name.name(), new TreeMap<String, String>()
				    {
					{
					    put("name", name.getDescription());
					}
				    });
				}
			    }
			});
		    }
		});
	    }
	});

	info.put("WeatherData", new TreeMap<String, Object>()
	{
	    {
		put("types", new TreeMap<String, Object>()
		{
		    {
			put("type", "fixed");
			put("values", new TreeMap<String, Object>()
			{
			    {
				for (DataType dataType : DataType.values())
				{
				    put(dataType.name(), new TreeMap<String, String>()
				    {
					{
					    put("name", dataType.name());
					}
				    });
				}
			    }
			});
		    }
		});

		put("places", new TreeMap<String, Object>()
		{
		    {
			put("type", "fixed");
			put("values", new TreeMap<String, Object>()
			{
			    {
				for (SMHILocation location : SMHILocation.values())
				{
				    put(location.name(), new TreeMap<String, String>()
				    {
					{
					    put("name", location.name());
					}
				    });
				}
			    }
			});
		    }
		});
	    }
	});

	arg1.setCharacterEncoding("UTF-8");
	arg1.getWriter().append(new Genson().serialize(info));
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

    protected String getPart(String[] all, Integer index, String onError)
    {
	if (index >= all.length) throw new RequestException(onError);
	return all[index];
    }

    protected DataSourceBuilder getDataSource(String parameters, String name)
    {
	if (parameters == null) throw new RequestException("The parameter '" + name + "' are required!");
	String[] args = parameters.split(",");

	DataSourceBuilder builder = null;

	String type = getPart(args, 0, "Source type for '" + name + "' is required!");

	if (type.equalsIgnoreCase("Constant"))
	{
	    builder = new ConstantSourceBuilder();
	}

	if (type.equalsIgnoreCase("Stock"))
	{
	    builder = new StockSourceBuilder();

	    ((StockSourceBuilder) builder).setStockInfo(
		    args.length > 3 ? getEnum(
			    StockInfo.class,
			    args[2],
			    "Invalid stock info for '" + name + "'") : StockInfo.PRICE);

	    String stock_name = getPart(args, 1, "Stock-name for '" + name + "' is required!");
	    if (!StockSourceBuilder
		    .checkValidName(stock_name)) throw new RequestException("Invalid stock name for '" + name + "'");
	    ((StockSourceBuilder) builder).setStock(stock_name, Integer.MAX_VALUE);
	}

	if (type.equalsIgnoreCase("Weather"))
	{
	    builder = new SMHISourceBuilder(
		    DataType.TEMPERATURE,
		    getEnum(
			    SMHILocation.class,
			    getPart(args, 1, "Location required for '" + name + "'"),
			    "Invalid location for '" + name + "'"));

	    ((SMHISourceBuilder) builder).setPeriod(Period.OLD);

	}

	if (builder == null) throw new RequestException("Unknown source type for '" + name + "'");
	return builder;
    }

    protected boolean getPretty(HttpServletRequest request)
    {
	return "true".equalsIgnoreCase(request.getParameter("pretty"));
    }

    protected Resolution getResolution(HttpServletRequest request)
    {
	String resolution = request.getParameter("resolution");
	if (resolution == null) return Resolution.DAY;
	try
	{
	    return Resolution.valueOf(resolution.toUpperCase());
	}
	catch (Exception e)
	{
	    throw new RequestException("Invalid resolution!");
	}
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
	    boolean pretty = getPretty(request);
	    Resolution resolution = getResolution(request);

	    DataSourceBuilder sourceA = getDataSource(request.getParameter("sourceA"), "sourceA");
	    DataSourceBuilder sourceB = getDataSource(request.getParameter("sourceB"), "sourceB");

	    builder.setXDatasource(sourceA.build());
	    builder.setYDatasource(sourceB.build());
	    builder.setResolution(resolution);

	    response.setContentType("application/json;charset=UTF-8");
	    response.getWriter().append(builder.getResult().asJSON(pretty ? JSONFormatter : null));
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

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	doGet(request, response);
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
