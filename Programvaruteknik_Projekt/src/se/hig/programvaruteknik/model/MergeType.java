package se.hig.programvaruteknik.model;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lambda that defines how to merge {@link MatchedDataPair MatchedDataPairs}
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
@FunctionalInterface
public interface MergeType
{
    /**
     * Sum all values
     */
    public final static MergeType SUM = (list) ->
    {
	return list.stream().collect(Collectors.summarizingDouble((value) -> value)).getSum();
    };

    /**
     * Take the average of all values
     */
    public final static MergeType AVERAGE = (list) ->
    {
	return list.stream().collect(Collectors.summarizingDouble((value) -> value)).getAverage();
    };

    /**
     * Take the median of the values
     */
    public final static MergeType MEDIAN = (list) ->
    {
	if (list.size() == 0) return 0d;

	return list.get((int) Math.floor(list.size() / 2));
    };

    /**
     * Merges {@link MatchedDataPair MatchedDataPairs}
     * 
     * @param data
     *            {@link MatchedDataPair MatchedDataPairs}
     * @param extractor
     *            {@link Function Lambda} that extracts
     *            the value form the
     *            {@link MatchedDataPair}
     * @return merged {@link MatchedDataPair}
     */
    public Double merge(List<Double> data);

    /**
     * Gets a MergeType from a string
     * 
     * @param name
     *            The string to cast to a MergeType
     * @return the MergeType
     * @throws NotAMergeTypeException
     *             If the string cannot be casted to an MergeType
     */
    public static MergeType fromString(String name)
    {
	try
	{
	    for (Field field : MergeType.class.getDeclaredFields())
	    {
		if (field.getType() == MergeType.class && field
			.getName()
			.equalsIgnoreCase(name)) return (MergeType) field.get(null);
	    }
	}
	catch (IllegalAccessException e)
	{
		
	}
	throw new NotAMergeTypeException("'" + name + "' is not a MergeType");
    }

    /**
     * A exception that represent that the supplied value is not a MergeType
     * 
     * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
     */
    public class NotAMergeTypeException extends RuntimeException
    {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Makes an exception with an message
	 * 
	 * @param message
	 *            The message to use
	 */
	public NotAMergeTypeException(String message)
	{
	    super(message);
	}
    }
}
