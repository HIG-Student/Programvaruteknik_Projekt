/**
 * 
 */
package se.hig.programvaruteknik;

/**
 * Can output JSON
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public interface JSONOutputter
{
    /**
     * Returns a JSON representation of this object<br>
     * Using a new {@link JSONFormatter}
     * 
     * @return The JSON string
     */
    public default String asJSON()
    {
	return asJSON(new JSONFormatter());
    }

    /**
     * Returns a JSON representation of this object
     * 
     * @param formatter
     *            A formatter to format the JSON string with<br>
     *            If null, it is not formatted
     * 
     * @return The JSON string
     */
    public String asJSON(JSONFormatter formatter);
}
