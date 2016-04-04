package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.owlike.genson.Genson;

import se.hig.programvaruteknik.model.CachedValue;
import se.hig.programvaruteknik.model.DataSourceBuilder;

/**
 * A source of JSON data
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class JSONDataSourceBuilder extends DataSourceBuilder
{
    private Function<Map<String, Object>, List<Map<String, Object>>> listExtractor;
    private BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor;
    private Function<Map<String, Object>, Boolean> entryFilter;

    private CachedValue<String> source = new CachedValue<>();

    @SuppressWarnings("unchecked")
    private Supplier<Map<String, Object>> DEFAULT_ROOT_SUPPLIER = () -> new Genson()
	    .deserialize(source.get(), Map.class);

    private CachedValue<Map<String, Object>> root = new CachedValue<>(DEFAULT_ROOT_SUPPLIER);

    private CachedValue<List<Map<String, Object>>> list = new CachedValue<>(() ->
    {
	return listExtractor.apply(root.get());
    });

    private CachedValue<Map<LocalDate, List<Double>>> data = new CachedValue<>(() ->
    {
	Map<LocalDate, List<Double>> result = new TreeMap<>();

	BiConsumer<LocalDate, Double> adder = (date, value) ->
	{
	    result.putIfAbsent(date, new ArrayList<Double>());
	    result.get(date).add(value);
	};

	for (Map<String, Object> map : list.get())
	{
	    if (entryFilter == null || !entryFilter.apply(map)) dataExtractor.accept(map, adder);
	}

	return result;
    });

    /**
     * Creates a datasource builder<br>
     * <br>
     * Components that needs to be added:
     * <ul>
     * <li>
     * {@link JSONDataSourceBuilder#setSourceSupplier(Supplier) SourceSupplier}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setNameExtractor(Function) NameExtractor} or
     * {@link JSONDataSourceBuilder#setName(String) Name}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setUnitExtractor(Function) UnitExtractor} or
     * {@link JSONDataSourceBuilder#setUnit(String) Unit}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setListExtractor(Function) ListExtractor}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setDataExtractor(BiConsumer) DataExtractor}
     * </li>
     * </ul>
     * Optional:
     * <ul>
     * <li>
     * {@link JSONDataSourceBuilder#setEntryFilter(Function) EntryFilter}
     * </li>
     * <li>
     * {@link JSONDataSourceBuilder#setDataFilter(BiFunction) DataFilter}
     * </li>
     * </ul>
     */
    public JSONDataSourceBuilder()
    {

    }

    /**
     * Creates a JSON datasource
     * 
     * @param sourceSupplier
     *            The supplier of the JSON source
     * @param nameExtractor
     *            The extractor to get the name with
     * @param unitExtractor
     *            The extractor to get the unit with
     * @param listExtractor
     *            The extractor to get the list with
     * @param dataExtractor
     *            The extractor to get the data from a list entry with
     */
    public JSONDataSourceBuilder(Supplier<String> sourceSupplier, Function<Map<String, Object>, String> nameExtractor, Function<Map<String, Object>, String> unitExtractor, Function<Map<String, Object>, List<Map<String, Object>>> listExtractor, BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	setSourceSupplier(sourceSupplier);
	setExtractors(nameExtractor, unitExtractor, listExtractor, dataExtractor);
    }

    /**
     * Creates a JSON datasource
     * 
     * @param root
     *            The root element
     * @param nameExtractor
     *            The extractor to get the name with
     * @param unitExtractor
     *            The extractor to get the unit with
     * @param listExtractor
     *            The extractor to get the list with
     * @param dataExtractor
     *            The extractor to get the data from a list entry with
     */
    public JSONDataSourceBuilder(Map<String, Object> root, Function<Map<String, Object>, String> nameExtractor, Function<Map<String, Object>, String> unitExtractor, Function<Map<String, Object>, List<Map<String, Object>>> listExtractor, BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	this.root.updateSupplier(() -> root);
	setExtractors(nameExtractor, unitExtractor, listExtractor, dataExtractor);
    }

    private void setExtractors(Function<Map<String, Object>, String> nameExtractor, Function<Map<String, Object>, String> unitExtractor, Function<Map<String, Object>, List<Map<String, Object>>> listExtractor, BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	setNameExtractor(nameExtractor);
	setUnitExtractor(unitExtractor);
	setListExtractor(listExtractor);
	setDataExtractor(dataExtractor);
    }

    /**
     * Sets whether to cache the downloaded data or not
     * 
     * @param caches
     *            The bool
     * @return This builder
     */
    public JSONDataSourceBuilder setCaches(boolean caches)
    {
	source.setActive(caches);
	root.setActive(caches);
	list.setActive(caches);
	data.setActive(caches);
	name.setActive(caches);
	unit.setActive(caches);
	return this;
    }

    /**
     * Sets the source supplier
     * 
     * @param sourceSupplier
     *            The source supplier
     * @return This builder
     */
    public JSONDataSourceBuilder setSourceSupplier(Supplier<String> sourceSupplier)
    {
	if (sourceSupplier == null) throw new DataSourceBuilderException("Missing source supplier!");
	source.updateSupplier(sourceSupplier);
	root.updateSupplier(DEFAULT_ROOT_SUPPLIER);
	list.clearCache();
	data.clearCache();
	name.clearCache();
	unit.clearCache();
	return this;
    }

    /**
     * Sets the list-extractor to use
     * 
     * @param listExtractor
     *            The list-extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setListExtractor(Function<Map<String, Object>, List<Map<String, Object>>> listExtractor)
    {
	if (listExtractor == null) throw new DataSourceBuilderException("Missing list extractor!");
	this.listExtractor = listExtractor;
	list.clearCache();
	data.clearCache();
	return this;
    }

    /**
     * Sets the name extractor
     * 
     * @param nameExtractor
     *            The name extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setNameExtractor(Function<Map<String, Object>, String> nameExtractor)
    {
	if (nameExtractor == null) throw new DataSourceBuilderException("Missing name extractor!");
	name.updateSupplier(() -> nameExtractor.apply(root.get()));
	return this;
    }

    /**
     * Sets the unit extractor
     * 
     * @param unitExtractor
     *            The unit extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setUnitExtractor(Function<Map<String, Object>, String> unitExtractor)
    {
	if (unitExtractor == null) throw new DataSourceBuilderException("Missing unit extractor!");
	unit.updateSupplier(() -> unitExtractor.apply(root.get()));
	return this;
    }

    /**
     * Sets the entry filter
     * 
     * @param entryFilter
     *            The entry filter
     * @return This builder
     */
    public JSONDataSourceBuilder setEntryFilter(Function<Map<String, Object>, Boolean> entryFilter)
    {
	this.entryFilter = entryFilter;
	data.clearCache();
	return this;
    }

    /**
     * Sets the data extractor
     * 
     * @param dataExtractor
     *            The data extractor
     * @return This builder
     */
    public JSONDataSourceBuilder setDataExtractor(BiConsumer<Map<String, Object>, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	if (dataExtractor == null) throw new DataSourceBuilderException("Missing data extractor!");
	this.dataExtractor = dataExtractor;
	data.clearCache();
	return this;
    }

    @Override
    protected Map<LocalDate, List<Double>> generateData()
    {
	if (data.haveValue() && data.isActive()) return data.get();

	if (listExtractor == null) throw new DataSourceBuilderException("Missing list extractor!");
	if (dataExtractor == null) throw new DataSourceBuilderException("Missing data extractor!");

	return data.get();
    }
}
