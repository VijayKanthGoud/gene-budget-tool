package com.gene.app.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;

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
	
	public static Map sortCaseInsensitive(Map rcvdMap){
		if(rcvdMap !=null && rcvdMap.size() != 0){
			Map sortedMap = new TreeMap((String.CASE_INSENSITIVE_ORDER));
			sortedMap.putAll(rcvdMap);
			return sortedMap;
		}else{
			return new TreeMap();
		}
	}
	
	public static List<String> getFileNamesFromCS(String bucketName){
		 final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
	     .initialRetryDelayMillis(10)
	     .retryMaxAttempts(10)
	     .totalRetryPeriodMillis(15000)
	     .build());
		List<String> fileNameList = new ArrayList<String>();
		 ListResult res;
		try {
			res = gcsService.list(BudgetConstants.BUCKET_NAME, null);
			while (res.hasNext()) {
				 ListItem item = res.next();
				 System.out.println("res = "+item.getName());
				 fileNameList.add(item.getName());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return fileNameList;
	}
	
	/*public static String escapeHTML(String val){
		String quot = &quot;
		
		return "";
	}
	
	private static Map<String,String> escapeSeqMap = new HashMap<String,String>();*/
	
}
