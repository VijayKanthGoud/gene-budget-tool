package com.gene.app.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gene.app.model.GtfReport;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class CostCenterCacheUtil {
	private final static Logger LOGGER = Logger
			.getLogger(CostCenterCacheUtil.class.getName());

	private static MemcacheService service = MemcacheServiceFactory
			.getMemcacheService();
	{
		service.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
	}

	public static void putCostCenterDataToCache(String costCenter,
			Map<String, GtfReport> gtfReportMap) {
		// LOGGER.log(Level.INFO, "costCenter : "+costCenter);
		long extn = 0;
		for (Map<String, GtfReport> gtfReportChunk : divideToChunks(gtfReportMap)) {
			// LOGGER.log(Level.INFO,
			// " for loop costCenter : "+costCenter+extn);
			service.put(costCenter + (extn++), gtfReportChunk);
		}
		extn--;
		// LOGGER.log(Level.INFO,
		// "Final extn "+extn+"for costcenter : "+costCenter);
		@SuppressWarnings("unchecked")
		Map<String, Long> costCentersExtensionDetails = (Map<String, Long>) service
				.get("costCentersExtensionDetails");
		if (costCentersExtensionDetails == null)
			costCentersExtensionDetails = new LinkedHashMap<>();
		costCentersExtensionDetails.put(costCenter, extn);
		// LOGGER.log(Level.INFO,
		// "costCentersExtensionDetails : "+costCentersExtensionDetails);
		service.put("costCentersExtensionDetails", costCentersExtensionDetails);
	}

	public static void addProjectToCostCenterCache(String costCenter,
			List<GtfReport> gtfReports) {
		// LOGGER.log(Level.INFO, "in addProjectToCostCenterCache");
		Map<String, Long> costCentersExtensionDetails = (Map<String, Long>) service
				.get("costCentersExtensionDetails");
		Long extn = costCentersExtensionDetails.get(costCenter);
		Map<String, GtfReport> gtfReportChunk = (Map<String, GtfReport>) service
				.get(costCenter + extn);
		Map<String, GtfReport> gtfReportMap = new LinkedHashMap<>();
		// LOGGER.log(Level.INFO,
		// "gtfReportChunk : "+costCenter+extn+" : "+gtfReportChunk);
		for (GtfReport report : gtfReports) {
			gtfReportMap.put(report.getgMemoryId(), report);
		}
		gtfReportChunk.putAll(gtfReportMap);
		try {
			service.put(costCenter + extn, gtfReportChunk);
			// LOGGER.log(Level.INFO, "Added to memcache : "+gtfReportChunk);
		} catch (Exception e) {
			// LOGGER.log(Level.INFO, "Caught exception : "+e.getMessage());
			// LOGGER.log(Level.INFO, "Dividing to chunks");
			for (Map<String, GtfReport> gtfReportChunkofChunk : divideToChunks(gtfReportChunk)) {
				service.put(costCenter + (extn++), gtfReportChunkofChunk);
			}
			extn--;
			costCentersExtensionDetails.put(costCenter, extn);
			// LOGGER.log(Level.INFO, "Cost center details");
			service.put("costCentersExtensionDetails",
					costCentersExtensionDetails);
		}
	}

	public static Map<String, GtfReport> getCostCenterDataFromCache(
			String costCenter) {
		// LOGGER.log(Level.INFO,
		// "in getCostCenterDataFromCache  costcenter : "+costCenter);
		Map<String, GtfReport> gtfReportMap = new LinkedHashMap<>();
		Map<String, Long> costCentersExtensionDetails = (Map<String, Long>) service
				.get("costCentersExtensionDetails");
		if (costCentersExtensionDetails == null) {
			// LOGGER.log(Level.INFO, "costCentersExtensionDetails is null");
			return gtfReportMap;
		}
		Long extn = costCentersExtensionDetails.get(costCenter);
		if (extn == null) {
			// LOGGER.log(Level.INFO, "extn is null");
			return gtfReportMap;
		}
		// LOGGER.log(Level.INFO, "Extn : "+extn);
		for (int i = 0; i <= extn; i++) {
			try {
				gtfReportMap.putAll((Map<String, GtfReport>) service
						.get(costCenter + i));
				// LOGGER.log(Level.INFO, "gtfReportMap : "+gtfReportMap);
			} catch (Exception e) {
				e.printStackTrace(System.out);
				return null;
			}
		}
		// LOGGER.log(Level.INFO,
		// "getCostCenterDataFromCache : size : "+gtfReportMap);
		return gtfReportMap;
	}

	private static List<Map<String, GtfReport>> divideToChunks(
			Map<String, GtfReport> data) {
		List<Map<String, GtfReport>> chunksOfData = new ArrayList<>();
		// LOGGER.log(Level.INFO, "Index Size: " + data.size());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			oos.close();
			// LOGGER.log(Level.INFO, "Data Size: " + baos.size());
			long sizeOfDataInKB = baos.size() / 1024;
			// LOGGER.log(Level.INFO, "sizeOfDataInKB : "+sizeOfDataInKB);
			long noOfChunks = (sizeOfDataInKB / 800) + 1;
			// LOGGER.log(Level.INFO, "noOfChunks : "+noOfChunks);
			long numberOfEntitiesInOneMap = data.size() / noOfChunks;
			// LOGGER.log(Level.INFO,
			// "numberOfEntitiesInOneMap : "+numberOfEntitiesInOneMap);
			long counter = 0;
			Map<String, GtfReport> temp = new HashMap<String, GtfReport>();
			for (String key : data.keySet()) {
				if (counter >= numberOfEntitiesInOneMap) {
					counter = 0;
					chunksOfData.add(temp);
					// LOGGER.log(Level.INFO, "temp : "+temp.size());
					temp = new HashMap<String, GtfReport>();
				}
				temp.put(key, data.get(key));
				counter++;
			}
			if (temp.size() > 0) {
				chunksOfData.add(temp);
				// LOGGER.log(Level.INFO, "temp : "+temp.size());
			}
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return chunksOfData;
	}

}
