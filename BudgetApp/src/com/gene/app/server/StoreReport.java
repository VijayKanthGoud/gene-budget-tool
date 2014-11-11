package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.bean.GtfReport;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.google.appengine.api.users.User;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class StoreReport extends HttpServlet {
	Map<String,Double> brandMap = new TreeMap<String,Double>();
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType(BudgetConstants.contentType);
		String objarray = req.getParameter(BudgetConstants.objArray).toString();
		boolean isMultiBrand = Boolean.parseBoolean(req.getParameter("multibrand").toString());
		String email = "";
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute(BudgetConstants.loggedInUser);
		email = user.getEmail();
		String brand = "";
		if(isMultiBrand){
			storeMultiBrandProject(objarray, email);
		}else{
			storeSingleBrandProject(objarray, email);
		}
		
		
		//insertUserRoleInfo((User)session.getAttribute(BudgetConstants.loggedInUser));
	}
	
	public void storeSingleBrandProject(String objarray, String email){
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
				brandMap.put(brand, 60000.0);
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
		generateProjectIdUsingJDOTxn(gtfReports);
		
	}
	
	public void storeMultiBrandProject(String objarray, String email){
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
		generateProjectIdUsingJDOTxn(gtfReports);
		//insertUserRoleInfo(user);
	}

	public void generateProjectIdUsingJDOTxn(List<GtfReport> gtfReports) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistentAll(gtfReports);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	
	public void insertUserRoleInfo(User user){/*
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
	*/}
	
}
