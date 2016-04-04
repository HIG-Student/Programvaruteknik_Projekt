package se.hig.programvaruteknik.model;

import java.util.function.Supplier;

/**
 * Container of a cached value<br>
 * <br>
 * Observe that it returns the EXACT value that it caches, not a copy
 * 
 * @param <T>
 *            The type of the value to cache
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class CachedValue<T>
{
    private Supplier<T> supplier = null;
    private T cachedValue = null;
    private boolean active = true;

    /**
     * Creates a cached value holder
     */
    public CachedValue()
    {

    }

    /**
     * Creates a cached value holder
     * 
     * @param supplier
     *            The supplier of the value
     */
    public CachedValue(Supplier<T> supplier)
    {
	this.supplier = supplier;
    }

    /**
     * Indicates whether cache should be used
     * 
     * @param status
     */
    public void setActive(boolean status)
    {
	active = status;
    }

    /**
     * Checks if the caching is active
     * 
     * @return True if it is
     */
    public boolean isActive()
    {
	return active;
    }

    /**
     * Update the supplier
     * 
     * @param supplier
     *            The new supplier
     */
    public void updateSupplier(Supplier<T> supplier)
    {
	this.supplier = supplier;
	clearCache();
    }

    /**
     * Clears the cache
     */
    public void clearCache()
    {
	cachedValue = null;
    }

    /**
     * Checks if the container have a cached value
     * 
     * @return True if it have a value, else false
     */
    public boolean haveValue()
    {
	return cachedValue != null;
    }

    /**
     * Checks if the container have a supplier
     * 
     * @return True if it have a supplier, else false
     */
    public boolean haveSupplier()
    {
	return supplier != null;
    }

    /**
     * Checks if the container can give a value
     * 
     * @return True if it can, else false
     */
    public boolean canGiveValue()
    {
	return (haveValue() && isActive()) || haveSupplier();
    }

    /**
     * Gives a new cached value
     * 
     * @param value
     *            The new value
     * @return The set value
     */
    public T set(T value)
    {
	return (cachedValue = value);
    }

    /**
     * Gets cached value or generate new one
     * 
     * @return The value
     */
    public T get()
    {
	if (active == false || cachedValue == null)
	    if (supplier == null)
		throw new CachedValueException("There is no value in this container");
	    else
		return (cachedValue = supplier.get());
	else
	    return cachedValue;
    }

    /**
     * Indicates error related to the {@link CachedValue}
     */
    @SuppressWarnings("serial")
    public static class CachedValueException extends RuntimeException
    {
	/**
	 * Creates new CachedValueException
	 * 
	 * @param exception
	 *            The source exception
	 */
	public CachedValueException(Exception exception)
	{
	    super(exception);
	}

	/**
	 * Creates new CachedValueException
	 * 
	 * @param message
	 *            The reason
	 */
	public CachedValueException(String message)
	{
	    super(message);
	}
    }
}
