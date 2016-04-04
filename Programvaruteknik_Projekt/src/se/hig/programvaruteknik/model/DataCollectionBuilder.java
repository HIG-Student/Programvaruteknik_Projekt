package se.hig.programvaruteknik.model;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A builder for {@link DataCollection}s<br>
 * <br>
 * Merge types defaults to {@link MergeType#SUM}
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class DataCollectionBuilder
{
    private MergeType xMergeType = MergeType.SUM;
    private MergeType yMergeType = MergeType.SUM;
    private String title = null;
    private DataSource xData;
    private DataSource yData;
    private Resolution resolution;

    private CachedValue<Map<String, MatchedDataPair>> resultingData = new CachedValue<>(
	    () -> matchData(xData, xMergeType, yData, yMergeType, resolution));

    /**
     * Creation of a builder that builds a {@link DataCollection}<br>
     * <br>
     * Requires:
     * <ul>
     * <li>{@link DataCollectionBuilder#setXDatasource(DataSource) X datasource}
     * </li>
     * <li>{@link DataCollectionBuilder#setYDatasource(DataSource) Y datasource}
     * </li>
     * <li>{@link DataCollectionBuilder#setResolution(Resolution) Resolution}
     * </li>
     * </ul>
     * Optional:
     * <ul>
     * <li>{@link DataCollectionBuilder#setTitle(String) Title}
     * </li>
     * <li>{@link DataCollectionBuilder#setXMergeType(MergeType) X mergetype}
     * </li>
     * <li>{@link DataCollectionBuilder#setYMergeType(MergeType) Y mergetype}
     * </li>
     * </ul>
     */
    public DataCollectionBuilder()
    {

    }

    /**
     * Creation of a builder that builds a {@link DataCollection}<br>
     * <br>
     * Optional:
     * <ul>
     * <li>{@link DataCollectionBuilder#setTitle(String) Title}
     * </li>
     * <li>{@link DataCollectionBuilder#setXMergeType(MergeType) X mergetype}
     * </li>
     * <li>{@link DataCollectionBuilder#setYMergeType(MergeType) Y mergetype}
     * </li>
     * </ul>
     * 
     * @param xData
     *            The x values
     * @param yData
     *            The y values
     * @param resolution
     *            The resolution
     */
    public DataCollectionBuilder(DataSource xData, DataSource yData, Resolution resolution)
    {
	setXDatasource(xData);
	setYDatasource(yData);
	setResolution(resolution);
    }

    /**
     * Set the x datasource
     * 
     * @param xData
     *            The datasource
     * @return This builder (for chaining)
     */
    public DataCollectionBuilder setXDatasource(DataSource xData)
    {
	this.xData = xData;
	resultingData.clearCache();
	return this;
    }

    /**
     * Set the y datasource
     * 
     * @param yData
     *            The datasource
     * @return This builder (for chaining)
     */
    public DataCollectionBuilder setYDatasource(DataSource yData)
    {
	this.yData = yData;
	resultingData.clearCache();
	return this;
    }

    /**
     * Sets the title
     * 
     * @param title
     *            The new title
     * @return This builder (for chaining)
     */
    public DataCollectionBuilder setTitle(String title)
    {
	this.title = title;
	return this;
    }

    /**
     * Sets the resolution
     * 
     * @param resolution
     *            The new resolution
     * @return This builder (for chaining)
     */
    public DataCollectionBuilder setResolution(Resolution resolution)
    {
	this.resolution = resolution;
	resultingData.clearCache();
	return this;
    }

    /**
     * Gets the title
     * 
     * @return The title
     */
    public String getTitle()
    {
	return title == null ? (xData.getName() + " : " + yData.getName()) : title;
    }

    /**
     * Sets how to merge the x values
     * 
     * @param xMergeType
     *            The merge type
     * @return This builder (for chaining)
     */
    public DataCollectionBuilder setXMergeType(MergeType xMergeType)
    {
	this.xMergeType = xMergeType;
	resultingData.clearCache();
	return this;
    }

    /**
     * Sets how to merge the y values
     * 
     * @param yMergeType
     *            The merge type
     * @return This builder (for chaining)
     */
    public DataCollectionBuilder setYMergeType(MergeType yMergeType)
    {
	this.yMergeType = yMergeType;
	resultingData.clearCache();
	return this;
    }

    static List<String> collectKeys(DataSource source, Resolution resolution)
    {
	return source.getData().keySet().stream().map(resolution::toKey).distinct().collect(Collectors.toList());
    }

    static Map<String, MatchedDataPair> matchData(DataSource xSource, MergeType xMergeType, DataSource ySource, MergeType yMergeType, Resolution resolution)
    {
	List<String> xKeys = collectKeys(xSource, resolution);
	List<String> yKeys = collectKeys(ySource, resolution);

	List<String> commonKeys = xKeys.stream().filter((key) -> yKeys.contains(key)).collect(Collectors.toList());

	Function<DataSource, Map<String, List<Double>>> dataOrganizer = (source) -> source
		.getData()
		.entrySet()
		.stream()
		.filter((entry) -> commonKeys.contains(resolution.toKey(entry)))
		.collect(
			Collectors.groupingBy(
				(entry) -> resolution.toKey(entry),
				Collectors.mapping((entry) -> entry.getValue(), Collectors.toList())));

	Map<String, List<Double>> xData = dataOrganizer.apply(xSource);
	Map<String, List<Double>> yData = dataOrganizer.apply(ySource);

	Map<String, MatchedDataPair> matches = commonKeys.stream().collect(
		Collectors.<String, String, MatchedDataPair> toMap(
			key -> key,
			key -> new MatchedDataPair(
				xMergeType.merge(xData.get(key)),
				yMergeType.merge(yData.get(key)))));

	return matches;
    }

    private Map<String, MatchedDataPair> copyMap(Map<String, MatchedDataPair> source)
    {
	return source.entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    /**
     * Build the {@link DataCollection}
     * 
     * @return the resulting {@link DataCollection}
     */
    public DataCollection getResult()
    {
	return new DataCollection(getTitle(), xData, yData, copyMap(resultingData.get()));
    }
}
