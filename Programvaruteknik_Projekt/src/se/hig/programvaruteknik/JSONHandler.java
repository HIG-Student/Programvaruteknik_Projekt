/**
 * 
 */
package se.hig.programvaruteknik;

import java.util.Map;

public interface JSONHandler
{
    public String objectToJSON(Map<String, Object> object);

    public Map<String, Object> JSONToObject(String JSON);
}
