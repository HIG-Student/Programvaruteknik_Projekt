package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import se.hig.programvaruteknik.model.CachedValue;
import se.hig.programvaruteknik.model.DataSourceBuilder;

/**
 * A source of CSV data
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class CSVDataSourceBuilder extends DataSourceBuilder
{
    /**
     * Splits the source based on line-breaks (regex '\R')
     */
    public static final Function<String, List<String>> DEFAULT_ROWEXTRACTOR = (source) -> Arrays
	    .asList(source.split("\\R+"));

    private Function<String, Boolean> rowFilter;
    private BiConsumer<String, BiConsumer<LocalDate, Double>> dataExtractor;

    private CachedValue<String> source = new CachedValue<>();

    private CachedValue<List<String>> rows = new CachedValue<>(() ->
    {
	return DEFAULT_ROWEXTRACTOR.apply(source.get());
    });

    private CachedValue<Map<LocalDate, List<Double>>> data = new CachedValue<>(() ->
    {
	Map<LocalDate, List<Double>> result = new TreeMap<>();
	BiConsumer<LocalDate, Double> adder = (date, value) ->
	{
	    result.putIfAbsent(date, new ArrayList<>());
	    result.get(date).add(value);
	};

	for (String row : rows.get())
	{
	    if (rowFilter == null || !rowFilter.apply(row)) dataExtractor.accept(row, adder);
	}

	return result;
    });

    /**
     * Creates a datasource builder<br>
     * <br>
     * Components that needs to be added:
     * <ul>
     * <li>
     * {@link CSVDataSourceBuilder#setSourceSupplier(Supplier) SourceSupplier}
     * </li>
     * <li>
     * {@link CSVDataSourceBuilder#setNameExtractor(Function) NameExtractor} or
     * {@link CSVDataSourceBuilder#setName(String) Name}
     * </li>
     * <li>
     * {@link CSVDataSourceBuilder#setUnitExtractor(Function) UnitExtractor} or
     * {@link CSVDataSourceBuilder#setUnit(String) Unit}
     * </li>
     * <li>
     * {@link CSVDataSourceBuilder#setDataExtractor(BiConsumer) DataExtractor}
     * </li>
     * </ul>
     * Optional:
     * <ul>
     * <li>
     * {@link CSVDataSourceBuilder#setRowFilter(Function) RowFilter}
     * </li>
     * <li>
     * {@link CSVDataSourceBuilder#setDataFilter(BiFunction) DataFilter}
     * </li>
     * </ul>
     */
    public CSVDataSourceBuilder()
    {
    }

    /**
     * Creates a datasource builder<br>
     * <br>
     * Faulty components throws exceptions first when building the datasource
     * 
     * @param sourceSupplier
     *            The source supplier
     * @param nameExtractor
     *            The name extractor
     * @param unitExtractor
     *            The unit extractor
     * @param dataExtractor
     *            The data extractor
     */
    public CSVDataSourceBuilder(Supplier<String> sourceSupplier, Function<String, String> nameExtractor, Function<String, String> unitExtractor, BiConsumer<String, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	if (sourceSupplier == null) throw new DataSourceBuilderException("Missing source-supplier!");
	if (nameExtractor == null) throw new DataSourceBuilderException("Missing name extractor!");
	if (unitExtractor == null) throw new DataSourceBuilderException("Missing unit extractor!");
	if (dataExtractor == null) throw new DataSourceBuilderException("Missing data extractor!");

	setSourceSupplier(sourceSupplier);
	setNameExtractor(nameExtractor);
	setUnitExtractor(unitExtractor);
	setDataExtractor(dataExtractor);
    }

    /**
     * Creates a datasource builder<br>
     * <br>
     * Faulty components throws exceptions first when building the datasource
     * 
     * @param sourceSupplier
     *            The source supplier
     * @param name
     *            The name
     * @param unit
     *            The unit
     * @param dataExtractor
     *            The data extractor
     */
    public CSVDataSourceBuilder(Supplier<String> sourceSupplier, String name, String unit, BiConsumer<String, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	this(sourceSupplier, (source) -> name, (source) -> unit, dataExtractor);
    }

    /**
     * Sets whether to cache the downloaded data or not
     * 
     * @param caches
     *            The bool
     * @return This builder
     */
    public CSVDataSourceBuilder setCaches(boolean caches)
    {
	name.setActive(caches);
	unit.setActive(caches);
	source.setActive(caches);
	rows.setActive(caches);
	return this;
    }

    /**
     * Sets the row-extractor to use
     * 
     * @param rowExtractor
     *            The row-extractor
     * @return This builder
     */
    public CSVDataSourceBuilder setRowExtractor(Function<String, List<String>> rowExtractor)
    {
	if (rowExtractor == null) return setRowExtractor(DEFAULT_ROWEXTRACTOR);

	rows.updateSupplier(() -> rowExtractor.apply(source.get()));
	data.clearCache();
	return this;
    }

    /**
     * Sets the source supplier
     * 
     * @param sourceSupplier
     *            The source supplier
     * @return This builder
     */
    public CSVDataSourceBuilder setSourceSupplier(Supplier<String> sourceSupplier)
    {
	if (sourceSupplier == null) throw new DataSourceBuilderException("Missing source-supplier!");
	source.updateSupplier(sourceSupplier);
	rows.clearCache();
	name.clearCache();
	unit.clearCache();
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
    public CSVDataSourceBuilder setNameExtractor(Function<String, String> nameExtractor)
    {
	if (nameExtractor == null) throw new DataSourceBuilderException("Missing name extractor!");
	name.updateSupplier(() -> nameExtractor.apply(source.get()));
	return this;
    }

    /**
     * Sets the unit extractor
     * 
     * @param unitExtractor
     *            The unit extractor
     * @return This builder
     */
    public CSVDataSourceBuilder setUnitExtractor(Function<String, String> unitExtractor)
    {
	if (unitExtractor == null) throw new DataSourceBuilderException("Missing unit extractor!");
	unit.updateSupplier(() -> unitExtractor.apply(source.get()));
	return this;
    }

    /**
     * Sets the row filter
     * 
     * @param rowFilter
     *            The row filter
     * @return This builder
     */
    public CSVDataSourceBuilder setRowFilter(Function<String, Boolean> rowFilter)
    {
	this.rowFilter = rowFilter;
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
    public CSVDataSourceBuilder setDataExtractor(BiConsumer<String, BiConsumer<LocalDate, Double>> dataExtractor)
    {
	if (dataExtractor == null) throw new DataSourceBuilderException("Missing data extractor!");
	this.dataExtractor = dataExtractor;
	data.clearCache();
	return this;
    }

    @Override
    public Map<LocalDate, List<Double>> generateData()
    {
	return data.get();
    }
}
