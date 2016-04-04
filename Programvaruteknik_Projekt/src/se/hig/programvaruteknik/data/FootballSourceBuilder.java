package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Building football data from
 * <a href="http://www.everysport.com">Everysport</a>
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class FootballSourceBuilder extends JSONDataSourceBuilder
{
    /**
     * Extracts the start date
     */
    public static Function<Map<String, Object>, LocalDate> DATE_EXTRACTOR = (entry) ->
    {
	return LocalDate.parse(entry.get("startDate").toString().substring(0, 10));
    };

    /**
     * Extracts the goals made by the home team
     */
    public final static Function<Map<String, Object>, Double> HOME_GOALS_EXTRACTOR = (entry) ->
    {
	return Double.parseDouble(entry.get("homeTeamScore").toString());
    };

    /**
     * Extracts the goals made by the away team
     */
    public final static Function<Map<String, Object>, Double> AWAY_GOALS_EXTRACTOR = (entry) ->
    {
	return Double.parseDouble(entry.get("visitingTeamScore").toString());
    };

    /**
     * Extracts the sum of the goals made by both teams
     */
    public final static Function<Map<String, Object>, Double> TOTAL_GOALS_EXTRACTOR = (entry) ->
    {
	return HOME_GOALS_EXTRACTOR.apply(entry) + AWAY_GOALS_EXTRACTOR.apply(entry);
    };

    /**
     * Extracts the spectators
     */
    @SuppressWarnings("unchecked")
    public final static Function<Map<String, Object>, Double> SPECTATORS_EXTRACTOR = (entry) ->
    {
	return Double.parseDouble(((Map<String, Object>) entry.get("facts")).get("spectators").toString());
    };

    /**
     * Create a football datasource builder
     * 
     */
    @SuppressWarnings("unchecked")
    public FootballSourceBuilder()
    {
	setSourceName("Everysport");
	setSourceLink("http://www.everysport.com");

	setListExtractor((root) -> (List<Map<String, Object>>) root.get("events"));
    }

    /**
     * Sets the {@link FootballSourceBuilder#setSourceSupplier(Supplier)
     * supplier} to one that fetch data from the
     * <a href="www.everysport.com">Everysport website</a><br>
     * <br>
     * <a href=
     * "https://github.com/menmo/everysport-api-documentation/blob/master/endpoints/GET_events.md">
     * http://api.everysport.com/v1/events?apikey={key}&{parameters}</a>
     * 
     * 
     * @param apikey
     *            The api key to use
     * 
     * @param parameters
     *            The parameters to use
     * @return This builder
     */
    public FootballSourceBuilder setFetchFromWebsite(String apikey, String parameters)
    {
	if (parameters == null || parameters == "") parameters = "league=63925&limit=240";

	setSourceSupplier(
		DataSupplierFactory.createURLFetcher(
			String.format("http://api.everysport.com/v1/events?apikey=%s&%s", apikey, parameters)));

	return this;
    }

    /**
     * Sets the {@link FootballSourceBuilder#setSourceSupplier(Supplier)
     * supplier} to one that fetch data from the
     * <a href="www.everysport.com">Everysport website</a><br>
     * <br>
     * <a href=
     * "https://github.com/menmo/everysport-api-documentation/blob/master/endpoints/GET_events.md">
     * http://api.everysport.com/v1/events?apikey={key}&{parameters}</a>
     * 
     * 
     * @param parameters
     *            The parameters to use
     * @return This builder
     */
    public FootballSourceBuilder setFetchFromWebsite(String parameters)
    {
	return setFetchFromWebsite("1769e0fdbeabd60f479b1dcaff03bf5c", parameters);
    }

    /**
     * Sets the {@link FootballSourceBuilder#setSourceSupplier(Supplier)
     * supplier} to one that fetch data from the
     * <a href="www.everysport.com">Everysport website</a>
     * 
     * @return This builder
     */
    public FootballSourceBuilder setFetchFromWebsite()
    {
	return setFetchFromWebsite("1769e0fdbeabd60f479b1dcaff03bf5c", "");
    }

    /**
     * Sets the data extractor
     * 
     * @param dateExtractor
     *            The date extractor
     * @param dataExtractor
     *            The data extractor
     * @return This builder
     */
    public FootballSourceBuilder setDataExtractor(Function<Map<String, Object>, LocalDate> dateExtractor, Function<Map<String, Object>, Double> dataExtractor)
    {
	setDataExtractor((entry, adder) ->
	{
	    adder.accept(dateExtractor.apply(entry), dataExtractor.apply(entry));
	});
	return this;
    }

    /**
     * Sets the data extractor<br>
     * <br>
     * {@link FootballSourceBuilder#DATE_EXTRACTOR} is used
     * 
     * @param dataExtractor
     *            The data extractor
     * @return This builder
     */
    public FootballSourceBuilder setDataExtractor(Function<Map<String, Object>, Double> dataExtractor)
    {
	setDataExtractor((entry, adder) ->
	{
	    adder.accept(DATE_EXTRACTOR.apply(entry), dataExtractor.apply(entry));
	});
	return this;
    }
}
