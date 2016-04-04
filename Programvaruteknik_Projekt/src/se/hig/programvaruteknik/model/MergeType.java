package se.hig.programvaruteknik.model;

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
}
