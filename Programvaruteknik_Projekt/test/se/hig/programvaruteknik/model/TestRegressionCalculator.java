package se.hig.programvaruteknik.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRegressionCalculator {
	
	RegressionCalculator regCalc;
	double expectedMValue = 3.76502732240437000000; 
	double expectedKValue = 0.7190961453256530000000;
	double expectedRValue = 0.9632129660496860000;
	double expectedFOfXMin = 3.7650273224043700000;
	double expectedFOfXMax = 18.1469502289174000000;
	double expectedMaxX = 20;
	double expectedMinX = 0;

	
	//int[] xValues = {0,1,3,4,5,7,9,10,15,18,19,20};
	//int[] yValues = {3,4,5,7,8,9,11,13,14,15,17,19};
	double[][] xyValues = {{0,3},{1,4},{3,5},{4,7},{5,8},{7,9},{9,11},{10,13},{15,14},{18,15},{19,17},{20,19}};
	
	
	@Before
	public void setUp() throws Exception {
		regCalc = new RegressionCalculator(xyValues);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMValue() {
		assertEquals(expectedMValue, regCalc.getMValue(), 0.00000001);
	}
	
	@Test
	public void testGetKValue() {
		assertEquals(expectedKValue, regCalc.getKValue(), 0.000000001);
	}
	
	@Test
	public void testGetRValue() {
		assertEquals(expectedRValue, regCalc.getRValue(), 0.000000001);
	}
	
	@Test
	public void testGetRegLineMinYValue() {
		assertEquals(expectedFOfXMin, regCalc.getRegLineMinYValue(), 0.000000001);
	}
	
	@Test
	public void testGetRegLineMaxYValue() {
		assertEquals(expectedFOfXMax, regCalc.getRegLineMaxYValue(), 0.000000001);
	}
	
	@Test
	public void testGetMinXValue() {
		assertEquals(expectedMinX, regCalc.getMinXValue(), 0.000000001);
	}
	
	@Test
	public void testGetMaxXValue() {
		assertEquals(expectedMaxX, regCalc.getMaxXValue(), 0.000000001);
	}
	
	
	
	
}
