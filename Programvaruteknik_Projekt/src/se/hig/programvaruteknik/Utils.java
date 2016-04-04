package se.hig.programvaruteknik;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
     * @param options
     *            Options
     * @return The first non-null option
     */
    @SafeVarargs
    public static <T> T or(T... options)
    {
	if (options == null) return null;

	for (T option : options)
	{
	    if (option != null) return option;
	}

	return null;
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
