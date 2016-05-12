/**
 * 
 */
package se.hig.programvaruteknik.database;

public interface DataHandler
{
    public Long save(String title, String json);

    public String load(Long index);

    @SuppressWarnings("serial")
    public class DataSaverException extends RuntimeException
    {
	public DataSaverException()
	{

	}

	public DataSaverException(String message)
	{
	    super(message);
	}

	public DataSaverException(Throwable throwable)
	{
	    super(throwable);
	}
    }
}
