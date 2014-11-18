package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.bean.GtfReport;
import com.gene.app.bean.MultiBrand;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.DBUtil;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class StoreReport extends HttpServlet {
	Map<String,Double> brandMap = new TreeMap<String,Double>();
	DBUtil util = new DBUtil();
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String email = "";
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute(BudgetConstants.loggedInUser);
		email = user.getEmail();
		resp.setContentType(BudgetConstants.contentType);
		String objarray = req.getParameter(BudgetConstants.objArray).toString();
		storeProjectData(objarray, email);
		/*boolean isMultiBrand = Boolean.parseBoolean(req.getParameter("multibrand").toString());
		String email = "";
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute(BudgetConstants.loggedInUser);
		email = user.getEmail();
		String brand = "";
		Map<String,MultiBrand> multiBrandMap = prepareMultiBrandMap();
		if(isMultiBrand){
			storeMultiBrandProject(objarray, email,multiBrandMap);
		}else{
			storeSingleBrandProject(objarray, email);
		}
		brandMap.put(brand, 60000.0);*/
		//insertUserRoleInfo((User)session.getAttribute(BudgetConstants.loggedInUser));
	}
	
	
	public void storeProjectData(String objarray, String email){
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		JSONArray jsonArray = null;
		GtfReport gtfReport = null;
		JSONObject rprtObject = null;
		String projectWBS = "";
		String remarks = null;
		String multiBrand = "";
		try{
			jsonArray = new JSONArray(objarray);
			for (int count = 0; count < jsonArray.length(); count++) {
				gtfReport = new GtfReport();
				rprtObject = jsonArray.getJSONObject(count);
				projectWBS = rprtObject.getString(BudgetConstants.GTFReport_ProjectWBS);
				if(projectWBS == null || projectWBS.isEmpty() || projectWBS.length() == 0){
					continue;
				}
				gtfReport.setEmail(email);
				String status = rprtObject.getString(BudgetConstants.GTFReport_Status);
				int flag = 0;
				if(BudgetConstants.status_New.equalsIgnoreCase(status.trim())){
					flag = 1;
				}else if(BudgetConstants.status_Active.equalsIgnoreCase(status.trim())){
					flag = 2;
				}else{
					flag = 3;
				}
				gtfReport.setFlag(flag);
				gtfReport.setStatus(status);
				//gtfReport.setStatus(rprtObject.getString("2"));
				gtfReport.setRequestor(rprtObject.getString(BudgetConstants.GTFReport_Requestor));
				gtfReport.setProject_WBS(rprtObject.getString(BudgetConstants.GTFReport_ProjectWBS));
				gtfReport.setWBS_Name(rprtObject.getString(BudgetConstants.GTFReport_WBS_Name));
				gtfReport.setSubActivity(rprtObject.getString(BudgetConstants.GTFReport_SubActivity));
				gtfReport.setPoNumber(rprtObject.getString(BudgetConstants.GTFReport_PoNumber));
				String poDesc = rprtObject.getString(BudgetConstants.GTFReport_PoDesc);
				gtfReport.setPoDesc(poDesc.substring(7, poDesc.length()));
				gtfReport.setVendor(rprtObject.getString(BudgetConstants.GTFReport_Vendor));
				
				try{
					remarks = ((rprtObject.getString(BudgetConstants.GTFReport_Remarks)!=null) && (!"".equalsIgnoreCase(rprtObject.getString(BudgetConstants.GTFReport_Remarks).trim())))?(rprtObject.getString(BudgetConstants.GTFReport_Remarks)):"";
				}catch(com.google.appengine.labs.repackaged.org.json.JSONException exception){
					remarks = "";
				}
				gtfReport.setRemarks(remarks);
				multiBrand = rprtObject.getString(BudgetConstants.isMultiBrand);
				if(multiBrand !=null && !"".equalsIgnoreCase(multiBrand.trim()) && "true".equalsIgnoreCase(multiBrand.trim())){
					prepareMultiBrandProjectData(gtfReports,gtfReport,rprtObject);
				}else{
					prepareSingleBrandProjectData(gtfReports,gtfReport,rprtObject);
				}
				//gtfReports.add(gtfReport);
			}
			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}
	
	public void prepareSingleBrandProjectData(List<GtfReport> gtfReports,GtfReport gtfReport,JSONObject rprtObject){
		try{
			
		gtfReport.setProjectName(rprtObject.getString(BudgetConstants.GTFReport_ProjectName));
		gtfReport.setBrand(rprtObject.getString(BudgetConstants.GTFReport_Brand));
		try {
			gtfReport.setPercent_Allocation(Integer.parseInt(rprtObject
					.getString(BudgetConstants.GTFReport_Percent_Allocation)));
		} catch (NumberFormatException e) {
			gtfReport.setPercent_Allocation(0);
		}
		String poDesc = rprtObject.getString(BudgetConstants.GTFReport_PoDesc);
		gtfReport.setgMemoryId(poDesc.substring(0, 6));
		Map<String, Double> plannedMap = new HashMap<String, Double>();
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		for (int cnt = 0; cnt <= BudgetConstants.months.length-1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			try {
				plannedMap.put(BudgetConstants.months[cnt],
						Double.parseDouble(rprtObject.getString(Integer.toString(cnt+BudgetConstants.months.length-1))));
			} catch (NumberFormatException e ) {
				plannedMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReport.setPlannedMap(plannedMap);
		gtfReport.setBenchmarkMap(setZeroMap);
		gtfReport.setAccrualsMap(setZeroMap);
		gtfReport.setVariancesMap(setZeroMap);
		gtfReport.setMultiBrand(false);
		gtfReports.add(gtfReport);
		
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public void prepareMultiBrandProjectData(List<GtfReport> gtfReports,GtfReport gtfReport,JSONObject rprtObject){
		prepareSingleBrandProjectData(gtfReports, gtfReport, rprtObject);
		JSONArray jsonArray = null;
		JSONObject multiBrandObject = null;
		Double value = 0.0;
		double percent_allocation = 0.0;
		try{
		String mutliBrandArray = rprtObject.getString(BudgetConstants.multiBrandInput);
		jsonArray = new JSONArray(mutliBrandArray);
		for(int i=0;i<jsonArray.length();i++){
			multiBrandObject = jsonArray.getJSONObject(i);
		gtfReport.setProjectName(multiBrandObject.getString("4"));
		gtfReport.setBrand(multiBrandObject.getString("1"));
		try {
			gtfReport.setPercent_Allocation(Double.parseDouble(multiBrandObject
					.getString("2")));
		} catch (NumberFormatException e) {
			gtfReport.setPercent_Allocation(0);
		}
		percent_allocation = Double.parseDouble(multiBrandObject.getString("2"));
		gtfReport.setgMemoryId(multiBrandObject.getString("5"));
		Map<String, Double> plannedMap = new HashMap<String, Double>();
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		Map<String,Double> parentPlannedMap = gtfReport.getPlannedMap();
		for (int cnt = 0; cnt <= BudgetConstants.months.length-1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			try {
				value = parentPlannedMap.get(BudgetConstants.months[cnt])*percent_allocation;
				plannedMap.put(BudgetConstants.months[cnt],value);
			} catch (NumberFormatException e ) {
				plannedMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReport.setPlannedMap(plannedMap);
		gtfReport.setBenchmarkMap(setZeroMap);
		gtfReport.setAccrualsMap(setZeroMap);
		gtfReport.setVariancesMap(setZeroMap);
		gtfReport.setMultiBrand(true);
		gtfReports.add(gtfReport);
		
		}
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	/**
	 * Store single brand project.
	 *
	 * @param objarray the objarray
	 * @param email the email
	 */
	/*public void storeSingleBrandProject(String objarray, String email){
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		try {
			JSONArray jsonArray = new JSONArray(objarray);
			for (int count = 0; count < jsonArray.length(); count++) {
				GtfReport gtfReport = new GtfReport();
				JSONObject rprtObject = jsonArray.getJSONObject(count);
				String projectWBS = rprtObject.getString(BudgetConstants.GTFReport_ProjectWBS);
				if(projectWBS == null || projectWBS.isEmpty() || projectWBS.length() == 0){
					continue;
				}
				gtfReport.setProjectName(rprtObject.getString(BudgetConstants.GTFReport_ProjectName));
				gtfReport.setEmail(email);
				String status = rprtObject.getString(BudgetConstants.GTFReport_Status);
				int flag = 0;
				if(BudgetConstants.status_New.equalsIgnoreCase(status.trim())){
					flag = 1;
				}else if(BudgetConstants.status_Active.equalsIgnoreCase(status.trim())){
					flag = 2;
				}else{
					flag = 3;
				}
				gtfReport.setFlag(flag);
				gtfReport.setStatus(status);
				//gtfReport.setStatus(rprtObject.getString("2"));
				gtfReport.setRequestor(rprtObject.getString(BudgetConstants.GTFReport_Requestor));
				gtfReport.setProject_WBS(rprtObject.getString(BudgetConstants.GTFReport_ProjectWBS));
				gtfReport.setWBS_Name(rprtObject.getString(BudgetConstants.GTFReport_WBS_Name));
				gtfReport.setSubActivity(rprtObject.getString(BudgetConstants.GTFReport_SubActivity));
				gtfReport.setBrand(rprtObject.getString(BudgetConstants.GTFReport_Brand));
				String brand = rprtObject.getString(BudgetConstants.GTFReport_Brand);
				try {
					gtfReport.setPercent_Allocation(Integer.parseInt(rprtObject
							.getString(BudgetConstants.GTFReport_Percent_Allocation)));
				} catch (NumberFormatException e) {
					gtfReport.setPercent_Allocation(0);
				}
				gtfReport.setPoNumber(rprtObject.getString(BudgetConstants.GTFReport_PoNumber));
				String poDesc = rprtObject.getString(BudgetConstants.GTFReport_PoDesc);
				gtfReport.setgMemoryId(poDesc.substring(0, 6));
				gtfReport.setPoDesc(poDesc.substring(7, poDesc.length()));
				gtfReport.setVendor(rprtObject.getString(BudgetConstants.GTFReport_Vendor));
				String remarks = null;
				try{
					remarks = ((rprtObject.getString(BudgetConstants.GTFReport_Remarks)!=null) && (!"".equalsIgnoreCase(rprtObject.getString(BudgetConstants.GTFReport_Remarks).trim())))?(rprtObject.getString(BudgetConstants.GTFReport_Remarks)):"";
				}catch(com.google.appengine.labs.repackaged.org.json.JSONException exception){
					remarks = "";
				}
				gtfReport.setRemarks(remarks);
				Map<String, Double> benchmarkMap = new HashMap<String, Double>();
				Map<String, Double> setZeroMap = new HashMap<String, Double>();
				for (int cnt = 0; cnt <= BudgetConstants.months.length-1; cnt++) {
					setZeroMap.put(BudgetConstants.months[cnt], 0.0);
					try {
						benchmarkMap.put(BudgetConstants.months[cnt],
								Double.parseDouble(rprtObject.getString(Integer.toString(cnt+BudgetConstants.months.length-1))));
					} catch (NumberFormatException e ) {
						benchmarkMap.put(BudgetConstants.months[cnt], 0.0);
					}
				}
				gtfReport.setBenchmarkMap(benchmarkMap);
				gtfReport.setPlannedMap(setZeroMap);
				gtfReport.setAccrualsMap(setZeroMap);
				gtfReport.setVariancesMap(setZeroMap);
				gtfReport.setMultiBrand(false);
				gtfReports.add(gtfReport);
				
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		util.generateProjectIdUsingJDOTxn(gtfReports);
		util.storeProjectsToCache(gtfReports);
	}
	
	*//**
	 * Store multi brand project.
	 *
	 * @param objarray the objarray
	 * @param email the email
	 *//*
	public void storeMultiBrandProject(String objarray, String email,Map<String,MultiBrand> multiBrandMap){
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		boolean isFirstLoop = true;
		try {
			JSONArray jsonArray = new JSONArray(objarray);
			String requestor = "";
			String projectName = "";
			String status = "";
			int flag = 0;
			String subActivity = "";
			String poNumber = "";
			String vendor = "";
			String poDesc = "";
			String projectWBS = "";
			String WBSName = "";
			
			for (int count = 0; count < jsonArray.length(); count++) {
				GtfReport gtfReport = new GtfReport();
				JSONObject rprtObject = jsonArray.getJSONObject(count);
				if(isFirstLoop){
					isFirstLoop = false;
					requestor = rprtObject.getString(BudgetConstants.GTFReport_Requestor);
					projectName = rprtObject.getString(BudgetConstants.GTFReport_ProjectName);
					status = rprtObject.getString(BudgetConstants.GTFReport_Status);
					if(BudgetConstants.status_New.equalsIgnoreCase(status.trim())){
						flag = 1;
					}else if(BudgetConstants.status_Active.equalsIgnoreCase(status.trim())){
						flag = 2;
					}else{
						flag = 3;
					}
					subActivity = rprtObject.getString(BudgetConstants.GTFReport_SubActivity);
					poNumber = rprtObject.getString(BudgetConstants.GTFReport_PoNumber);
					vendor = rprtObject.getString(BudgetConstants.GTFReport_Vendor);
					poDesc = rprtObject.getString(BudgetConstants.GTFReport_PoDesc);
					projectWBS = rprtObject.getString(BudgetConstants.GTFReport_ProjectWBS);
					WBSName = rprtObject.getString(BudgetConstants.GTFReport_WBS_Name);
				}

				gtfReport.setRequestor(requestor);
				gtfReport.setProjectName(projectName);
				gtfReport.setEmail(email);
				String brand = rprtObject.getString(BudgetConstants.GTFReport_Brand);
				if(brand == null || brand.isEmpty() || brand.length() == 0){
					continue;
				}
				gtfReport.setFlag(flag);
				gtfReport.setStatus(status);
				gtfReport.setProject_WBS(projectWBS);
				gtfReport.setWBS_Name(WBSName);
				gtfReport.setSubActivity(subActivity);
				gtfReport.setBrand(brand);
				try {
					gtfReport.setPercent_Allocation(Double.parseDouble(rprtObject
							.getString(BudgetConstants.GTFReport_Percent_Allocation)));
				} catch (NumberFormatException e) {
					gtfReport.setPercent_Allocation(0);
				}
				gtfReport.setPoNumber(poNumber);
				if (count != 0) {
					gtfReport
							.setgMemoryId(poDesc.substring(0, 6) + "." + count);
				}else{
					gtfReport
					.setgMemoryId(poDesc.substring(0, 6));
				}
				gtfReport.setPoDesc(poDesc.substring(7, poDesc.length()));
				gtfReport.setVendor(vendor);
				String remarks = null;
				try{
					remarks = ((rprtObject.getString(BudgetConstants.GTFReport_Remarks)!=null) && (!"".equalsIgnoreCase(rprtObject.getString(BudgetConstants.GTFReport_Remarks).trim())))?(rprtObject.getString(BudgetConstants.GTFReport_Remarks)):"";
				}catch(com.google.appengine.labs.repackaged.org.json.JSONException exception){
					remarks = "";
				}
				gtfReport.setRemarks(remarks);
				Map<String, Double> benchmarkMap = new HashMap<String, Double>();
				Map<String, Double> setZeroMap = new HashMap<String, Double>();
				for (int cnt = 0; cnt <= BudgetConstants.months.length-1; cnt++) {
					setZeroMap.put(BudgetConstants.months[cnt], 0.0);
					try {
						benchmarkMap.put(BudgetConstants.months[cnt],
								Double.parseDouble(rprtObject.getString(Integer.toString(cnt+BudgetConstants.months.length-1))));
					} catch (NumberFormatException e ) {
						benchmarkMap.put(BudgetConstants.months[cnt], 0.0);
					}
				}
				gtfReport.setBenchmarkMap(benchmarkMap);
				gtfReport.setPlannedMap(setZeroMap);
				gtfReport.setAccrualsMap(setZeroMap);
				gtfReport.setVariancesMap(setZeroMap);
				gtfReport.setMultiBrand(true);
				gtfReports.add(gtfReport);
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		util.generateProjectIdUsingJDOTxn(gtfReports);
		util.storeProjectsToCache(gtfReports);
		//cache.delete(BudgetConstants.costCenter);
	}*/

	/**
	 * Generate project id using jdo txn.
	 *
	 * @param gtfReports the gtf reports
	 */
	

	
	/**
	 * Require to manually insert user role info in to datastore.
	 * Required at the initiation of a new application.
	 *
	 * @param user the user
	 */
	public void insertUserRoleInfo(User user){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		UserRoleInfo userInfo = new UserRoleInfo();
		userInfo.setEmail(user.getEmail());
		userInfo.setBrand(brandMap);
		userInfo.setUserName(user.getNickname());
		userInfo.setRole("Project Owner");
		
		try{
			pm.makePersistent(userInfo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			pm.close();
		}
	}

}
