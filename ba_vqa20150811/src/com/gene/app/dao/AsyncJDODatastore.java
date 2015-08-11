package com.gene.app.dao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.model.GtfReport;
import com.gene.app.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AsyncJDODatastore extends HttpServlet
{
	private final static Logger LOGGER = Logger
			.getLogger(AsyncJDODatastore.class.getName());

	CostCenterCacheUtil costCenterCacheUtil = new CostCenterCacheUtil();
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Entered AsyncJDODatastore:::");
		List<GtfReport> gtfReports;

		String option=req.getHeader("option");
		String cCenter=req.getHeader("cCenter");
		String gMemoriId=req.getHeader("gMemoriId");
		ObjectInputStream objectInputStream = new ObjectInputStream(req.getInputStream());
		List<GtfReport> gtfRptList=null;
		try {
			gtfRptList = (List<GtfReport>)objectInputStream.readObject();
			LOGGER.log(Level.INFO, "gtfRptList"+new Gson().toJson(gtfRptList));
		} catch (ClassNotFoundException e1) {
			LOGGER.log(Level.INFO, e1.getStackTrace().toString());
			e1.printStackTrace();
		}
		Map<String,GtfReport> gtfReportMap =  costCenterCacheUtil.getCostCenterDataFromCache(cCenter);
		if(Util.isNullOrEmpty(gMemoriId) && gtfReportMap.get(gMemoriId)!=null){
			GtfReport gtfRpt = gtfReportMap.get(gMemoriId);
			List<String> cList = new ArrayList<>();
			if(gtfRpt.getMultiBrand() && gtfRpt.getChildProjectList()!=null){
				cList = gtfReportMap.get(gMemoriId).getChildProjectList();
			}else{
				cList.add(gMemoriId);
			}
			for(String childId : cList){
				gtfReportMap.remove(childId);
			}
		}
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			if("append".equalsIgnoreCase(option)){
				List<GtfReport> updatedGtfReport = (List)pm.makePersistentAll(gtfRptList);
				for(GtfReport gtf :updatedGtfReport){
					gtfReportMap.put(gtf.getgMemoryId(),gtf);
				}
			}
			else if ("delete".equalsIgnoreCase(option)){
				for(GtfReport gtfs : gtfRptList){
					pm.deletePersistent(pm.getObjectById(gtfs.getClass(),gtfs.getId()));
				} 
				for(GtfReport gtf :gtfRptList){
					gtfReportMap.remove(gtf.getgMemoryId());
				}
			}
			costCenterCacheUtil.putCostCenterDataToCache(cCenter, gtfReportMap);
		}catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.INFO, e.getStackTrace().toString());
		} finally {
			LOGGER.log(Level.INFO, "closing all datastore call.");
			pm.close();
		}
	}

}
