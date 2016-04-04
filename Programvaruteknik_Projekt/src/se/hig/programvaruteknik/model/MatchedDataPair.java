package se.hig.programvaruteknik.model;

/**
 * A class representing two data values that makes a pair
 * 
 * @author Viktor Hanstorp (ndi14vhp@student.hig.se)
 */
public class MatchedDataPair
{
    private Double xValue;
    private Double yValue;

    /**
     * Creates a pair
     * 
     * @param xValue
     *            The x value
     * @param yValue
     *            The y value
     */
    public MatchedDataPair(Double xValue, Double yValue)
    {
	this.xValue = xValue;
	this.yValue = yValue;
    }

    /**
     * Get the x value
     * 
     * @return The x value
     */
    public Double getXValue()
    {
	return xValue;
    }

    /**
     * Get the y value
     * 
     * @return The y value
     */
    public Double getYValue()
    {
	return yValue;
    }

    @Override
    public String toString()
    {
	return "(" + xValue + " : " + yValue + ")";
    }
}
