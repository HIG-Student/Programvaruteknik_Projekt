/**
 * Interface that converts strings into JSon strings
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
package se.hig.programvaruteknik;

import java.util.Map;

public interface JSONHandler
{
    public String objectToJSON(Map<String, Object> object);

    public Map<String, Object> JSONToObject(String JSON);
}
