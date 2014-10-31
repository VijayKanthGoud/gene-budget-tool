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
				/*if(Integer.parseInt(cellNum)==13){
					String remarks = cellValue;
					gtfReportObj.setRemarks(remarks);
					gtfReportMap.put(keyNum, gtfReportObj);
				}else{*/
				Map<String, Double> plannedMap = gtfReportObj.getPlannedMap();
				if(plannedMap!=null){
				plannedMap.put(GtfReport.months[Integer.parseInt(cellNum)], Double.parseDouble(cellValue));
				gtfReportObj.setPlannedMap(plannedMap);
				gtfReportMap.put(keyNum, gtfReportObj);
				//}
			}}
		}
		
		//try {
			/*JSONObject rprtObject = new JSONObject(rowItem);
			dsFlag = rprtObject.getString("28");
			key = rprtObject.getString("27");
			String projectWBS = rprtObject.getString("4");
			gtfReport.setProjectName(rprtObject.getString("1"));
			String status = rprtObject.getString("2");
			int flag = 0;
			if("New".equalsIgnoreCase(status.trim())){
				flag = 1;
			}else if("Active".equalsIgnoreCase(status.trim())){
				flag = 2;
			}else{
				flag = 3;
			}
			gtfReport.setFlag(flag);
			gtfReport.setStatus(status);
			gtfReport.setRequestor(rprtObject.getString("3"));
			gtfReport.setProject_WBS(rprtObject.getString("4"));
			gtfReport.setWBS_Name(rprtObject.getString("5"));
			gtfReport.setSubActivity(rprtObject.getString("6"));
			gtfReport.setBrand(rprtObject.getString("7"));
			try {
				gtfReport.setPercent_Allocation(Integer.parseInt(rprtObject
						.getString("8")));
			} catch (NumberFormatException e) {
				gtfReport.setPercent_Allocation(0);
			}
			gtfReport.setPoNumber(rprtObject.getString("9"));
			gtfReport.setPoDesc(rprtObject.getString("10"));
			gtfReport.setVendor(rprtObject.getString("11"));
			gtfReport.setId(rprtObject.getString("27"));
			String remarks = null;
			try{
				remarks = ((rprtObject.getString("25")!=null) && (!"".equalsIgnoreCase(rprtObject.getString("25").trim())))?(rprtObject.getString("25")):"";
			}catch(com.google.appengine.labs.repackaged.org.json.JSONException exception){
				remarks = "";
			}
			gtfReport.setRemarks(remarks);
			Map<String, Double> benchmarkMap = new HashMap<String, Double>();
			Map<String, Double> setZeroMap = new HashMap<String, Double>();
			for (int cnt = 12; cnt <= 23; cnt++) {
				setZeroMap.put(GtfReport.months[cnt - 12], 0.0);
				try {
					benchmarkMap.put(GtfReport.months[cnt - 12],
							Double.parseDouble(rprtObject.getString(Integer.toString(cnt))));
				} catch (NumberFormatException e ) {
					benchmarkMap.put(GtfReport.months[0], 0.0);
				}
			}
			gtfReport.setBenchmarkMap(benchmarkMap);
			gtfReport.setPlannedMap(setZeroMap);
			gtfReport.setAccrualsMap(setZeroMap);
			gtfReport.setVariancesMap(setZeroMap);
			*/
		/*} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
