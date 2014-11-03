package com.gene.app.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.bean.GtfReport;
import com.gene.app.util.DBUtil;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class AutoSaveData  extends HttpServlet{
	DBUtil util = new DBUtil();	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		GtfReport gtfReport = new GtfReport();
		//String rowItem = req.getParameter("objarray").toString();
		String keyNum = req.getParameter("key").toString();
		String cellValue = req.getParameter("cellValue").toString();
		String cellNum = req.getParameter("celNum").toString();
		String dsFlag = "";
		String key = "";
		String costCenter = "307673";
		GtfReport gtfReportObj = null;
		Map<String,GtfReport> gtfReportMap = util.getAllReportDataFromCache(costCenter);
		if(keyNum!=null && !"".equalsIgnoreCase(keyNum.trim())){
			gtfReportObj = gtfReportMap.get(keyNum);
			if(gtfReportObj!=null){
				if(Integer.parseInt(cellNum)==13){
					String remarks = cellValue;
					gtfReportObj.setRemarks(remarks);
					gtfReportMap.put(keyNum, gtfReportObj);
				}else{
				Map<String, Double> plannedMap = gtfReportObj.getPlannedMap();
				if(plannedMap!=null){
				plannedMap.put(GtfReport.months[Integer.parseInt(cellNum)], Double.parseDouble(cellValue));
				gtfReportObj.setPlannedMap(plannedMap);
				gtfReportMap.put(keyNum, gtfReportObj);
				}
			}}
		}
		
		
		String sessionKey = (String)session.getAttribute("key");
		GtfReport sessionGtfReport = util.readReportDataFromCache(sessionKey,costCenter);
		util.saveAllReportDataToCache(costCenter, gtfReportMap);//ReportDataToCache(gtfReportObj);
		if(keyNum==null && cellValue==null && cellNum==null && sessionKey !=null){
			util.saveDataToDataStore(sessionGtfReport);
		}
		
		if((keyNum!=null && sessionKey!=null) && !(keyNum.equals(sessionKey))){
			util.saveDataToDataStore(sessionGtfReport);
		}
		session.setAttribute("key", keyNum);
		
	}

}
