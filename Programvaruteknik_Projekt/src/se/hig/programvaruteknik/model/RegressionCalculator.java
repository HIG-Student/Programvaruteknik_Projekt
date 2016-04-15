package se.hig.programvaruteknik.model;

public class RegressionCalculator {

	private double xSum, ySum, xySum, xSquaredSum, ySquaredSum, nbrOfElements, minXValue, maxXValue;

	public RegressionCalculator(int[] xValues, int[] yValues) {
		nbrOfElements = xValues.length;
		xSum = getSumOf(xValues);
		ySum = getSumOf(yValues);
		xySum = getSumOfArrayNodeProducts(xValues, yValues);
		xSquaredSum = getSumOfArrayNodeProducts(xValues, xValues);
		ySquaredSum = getSumOfArrayNodeProducts(yValues, yValues);
		minXValue = calcMinXValue(xValues);
		maxXValue = calcMaxXValue(xValues);
	}
	
	private double calcMaxXValue(int[] xValues) {
		double minX = Integer.MAX_VALUE;
		for (int i = 0; i < xValues.length; i++) {
			if (minX > xValues[i])
				minX = xValues[i];
		}
		return minX;
	}

	private double calcMinXValue(int[] xValues){
		double maxX = Integer.MIN_VALUE;
		for (int i = 0; i < xValues.length; i++) {
			if (maxX < xValues[i])
				maxX = xValues[i];
		}
		return maxX;
	}
	
	private double getSumOfArrayNodeProducts(int[] aValues, int[] bValues) {
		double sum = 0;
		for (int i = 0; i < nbrOfElements; i++) {
			sum += aValues[i]*bValues[i];
		}
		return sum;
	}

	private double getSumOf(int[] values) {
		double sum = 0;
		for (int i = 0; i < values.length; i++) {
			sum += values[i];
		}
		return sum;
	}

	public double getKValue(){
		return ((nbrOfElements*xySum)-(xSum*ySum))/
				((nbrOfElements*xSquaredSum)-(xSum*xSum));
	}

	public double getMValue(){
		return ((xSquaredSum*ySum)-(xSum*xySum))/
				((nbrOfElements*xSquaredSum)-(xSum*xSum));
	}

	public double getRValue() {
		return (Math.pow(((nbrOfElements*xySum)-(xSum*ySum)), 2))/
				(((nbrOfElements*xSquaredSum)-(xSum*xSum))*((nbrOfElements*ySquaredSum)-(ySum*ySum)));
	}

	public double getRegLineMinYValue() {
		return straightLineEqFofX(getMinXValue());
	}

	public double getRegLineMaxYValue() {
		return straightLineEqFofX(getMaxXValue());
	}
	
	private double straightLineEqFofX(double x){
		return (getKValue()*x)+getMValue();
	}

	public double getMaxXValue() {
		return minXValue;
	}

	public double getMinXValue() {
		return maxXValue;
	}

}
