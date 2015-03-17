package com.gene.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {
	
	/**
	 * To round up a double value.
	 *
	 * @param Value the value to be rounded up
	 * @param Rounder up to how many decimal places
	 * @return Rounded up double value
	 */
	public static double roundDoubleValue(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	/**
	 * Checks if it is an int value.
	 *
	 * @param value the value
	 * @return true, if is an int value
	 */
	public static boolean isInt(String value){
		return value.matches("[-+]?[0-9]+");
	}
	
	public static boolean isNullOrEmpty(String value){
		if(value!=null && !"".equals(value)){
			return true;
		}else{
			return false;
		}
	}
	
}
