package se.hig.programvaruteknik.data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.hig.programvaruteknik.model.DataSourceBuilder;
import se.hig.programvaruteknik.model.SourceGenerator;

/**
 * Generates constant data, ignores any suppliers and extractors except
 * {@link DataSourceBuilder#setDataReducer(java.util.function.BiFunction)
 * DataReducer} and
 * {@link DataSourceBuilder#setDataFilter(java.util.function.BiFunction)
 * DataFilter}<br>
 * <br>
 * Generates the value "4" for all days between 1970-01-01 to 2016-02-29
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class ConstantSourceBuilder extends DataSourceBuilder
{
    /**
     * Creates the constant source builder
     */
    public ConstantSourceBuilder()
    {
	setName("Random value");
	setUnit("N/A");

	setSourceName("xkcd");
	setSourceLink("https://xkcd.com/221/");
    }

    @Override
    protected Map<LocalDate, List<Double>> generateData()
    {
	Map<LocalDate, List<Double>> map = new TreeMap<>();
	for (int day = 0; day < 16861; day++)
	{
	    map.put(LocalDate.ofEpochDay(day), Arrays.asList(new Double(4)));
	}

	return map;
    }

    @SourceGenerator("Constant")
    private static DataSourceBuilder generate()
    {
	return new ConstantSourceBuilder();
    }
}
