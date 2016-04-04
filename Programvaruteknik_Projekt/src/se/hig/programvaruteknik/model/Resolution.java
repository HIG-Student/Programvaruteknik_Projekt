package se.hig.programvaruteknik.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map.Entry;

/**
 * How to group data
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public enum Resolution
{
    /**
     * Group into years<br>
     * <br>
     * Uses {@link DateTimeFormatter#ofPattern(String,Locale)
     * DateTimeFormatter.ofPattern("yyyy",Locale.UK)}
     */
    YEAR("yyyy"),

    /**
     * Group into quarters of a year<br>
     * <br>
     * Uses {@link DateTimeFormatter#ofPattern(String,Locale)
     * DateTimeFormatter.ofPattern("yyyy-'Q'Q",Locale.UK)}
     */
    QUARTER("yyyy-'Q'Q"),

    /**
     * Group into months<br>
     * <br>
     * Uses {@link DateTimeFormatter#ofPattern(String,Locale)
     * DateTimeFormatter.ofPattern("yyyy-MM",Locale.UK)}
     */
    MONTH("yyyy-MM"),

    /**
     * Group into weeks<br>
     * <br>
     * Uses {@link DateTimeFormatter#ofPattern(String,Locale)
     * DateTimeFormatter.ofPattern("YYYY-'W'w",Locale.UK)}
     */
    WEEK("YYYY-'W'w"),

    /**
     * Group into days<br>
     * <br>
     * Uses {@link DateTimeFormatter#ofPattern(String,Locale)
     * DateTimeFormatter.ofPattern("yyyy-MM-dd",Locale.UK)}
     */
    DAY("yyyy-MM-dd");

    private final ResolutionResolver resolver;

    Resolution(ResolutionResolver resolver)
    {
	this.resolver = resolver;
    }

    /**
     * @param pattern
     *            the pattern to give to<br>
     *            {@link DateTimeFormatter#ofPattern(String,Locale)}<br>
     *            Where Locale is {@link Locale#UK}<br>
     *            <br>
     *            Negative years appends '-' in that method<br>
     *            Years over 9999 appends '+' in that method<br>
     */
    Resolution(String pattern)
    {
	// We use a specific Locale because the default locale can be different
	// on different computers
	// Locale.UK is used because it have the correct handling of Weeks
	// (starts on Mondays) that is not found in the Locale.ROOT, which we
	// would otherwise use
	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.UK);
	resolver = (date) -> date.format(formatter);
    }

    /**
     * Converts a {@link LocalDate} to a key
     * 
     * @param date
     *            the {@link LocalDate} to convert
     * @return The key
     */
    public String toKey(LocalDate date)
    {
	return resolver.resolve(date);
    }

    /**
     * Converts a {@link Entry} to a key
     * 
     * @param entry
     *            The {@link Entry} to convert
     * @return The key
     */
    public String toKey(Entry<LocalDate, Double> entry)
    {
	return toKey(entry.getKey());
    }

    @FunctionalInterface
    private interface ResolutionResolver
    {
	public String resolve(LocalDate date);
    }
}
