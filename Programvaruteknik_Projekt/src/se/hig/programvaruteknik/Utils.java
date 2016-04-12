package se.hig.programvaruteknik;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Supplier;

/**
 * Clever utilities
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class Utils
{
    /**
     * Returns the first non-null parameter
     * 
     * @param defaultValues
     *            Default values
     * @return The first non-null default value
     */
    @SafeVarargs
    public static <T> T or(T... defaultValues)
    {
	if (defaultValues == null) return null;

	for (T option : defaultValues)
	{
	    if (option != null) return option;
	}

	return null;
    }

    /**
     * Returns the value of the supplier if possible, else return the first
     * non-null default value
     * 
     * @param supplier
     *            Supplier of a value
     * @param defaultValues
     *            Default values
     * @return The first non-null option
     */
    @SafeVarargs
    public static <T> T or(Supplier<T> supplier, T... defaultValues)
    {
	try
	{
	    T value = supplier.get();
	    if (value != null) return value;
	}
	catch (Exception e)
	{

	}

	return or(defaultValues);
    }

    /**
     * Read a stream
     * 
     * @param stream
     *            The stream to read
     * @return The string
     * @throws Exception
     *             on errors
     */
    public static String readStream(InputStream stream)
    {
	try (BufferedReader in = new BufferedReader(new InputStreamReader(stream)))
	{
	    StringBuilder builder = new StringBuilder();
	    String inputLine;
	    while ((inputLine = in.readLine()) != null)
	    {
		builder.append(inputLine);
		builder.append("\n");
	    }
	    return builder.toString().trim();
	}
	catch (Exception e)
	{
	    throw new RuntimeException(e);
	}
    }
}
