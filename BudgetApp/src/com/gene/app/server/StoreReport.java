package com.gene.app.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.bean.GtfReport;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.DBUtil;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import static com.gene.app.util.Util.roundDoubleValue;




@SuppressWarnings("serial")
public class StoreReport extends HttpServlet {
	Map<String,Double> brandMap = new TreeMap<String,Double>();
	DBUtil util = new DBUtil();
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String email = "";
		HttpSession session = req.getSession();
		//User user = (User)session.getAttribute(BudgetConstants.loggedInUser);
		UserRoleInfo user = (UserRoleInfo)session.getAttribute("userInfo");
		email = user.getEmail();
		resp.setContentType(BudgetConstants.contentType);
		String objarray = req.getParameter(BudgetConstants.objArray).toString();
		storeProjectData(objarray, user, resp);
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
	
	
	public void storeProjectData(String objarray, UserRoleInfo user, HttpServletResponse resp){
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		JSONArray jsonArray = null;
		GtfReport gtfReport = null;
		JSONObject rprtObject = null;
		String projectWBS = "";
		String remarks = null;
		String multiBrand = "";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
		try{
			jsonArray = new JSONArray(objarray);
			for (int count = 0; count < jsonArray.length(); count++) {
				gtfReport = new GtfReport();
				rprtObject = jsonArray.getJSONObject(count);
				/*projectWBS = rprtObject.getString(BudgetConstants.New_GTFReport_ProjectWBS);
				if(projectWBS == null || projectWBS.isEmpty() || projectWBS.length() == 0){
					continue;
				}*/
				gtfReport.setEmail(user.getEmail());
				String status =BudgetConstants.New_GTFReport_Status;
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
				
				gtfReport.setRequestor(rprtObject.getString(BudgetConstants.New_GTFReport_ProjectOwner));
				gtfReport.setProject_WBS(rprtObject.getString(BudgetConstants.New_GTFReport_Project_WBS));
				//gtfReport.setWBS_Name(rprtObject.getString(BudgetConstants.New_GTFReport_WBS_Name));
				gtfReport.setSubActivity(rprtObject.getString(BudgetConstants.New_GTFReport_SubActivity));
				gtfReport.setPoNumber(rprtObject.getString(BudgetConstants.New_GTFReport_PoNumber));
				String poDesc = rprtObject.getString(BudgetConstants.New_GTFReport_PoDesc);
				gtfReport.setPoDesc(poDesc);
				gtfReport.setVendor(rprtObject.getString(BudgetConstants.New_GTFReport_Vendor));
				gtfReport.setCreateDate(timeStamp);
				gtfReport.setYear(BudgetConstants.dataYEAR);
				gtfReport.setCostCenter(user.getCostCenter());
				try{
					remarks = ((rprtObject.getString(BudgetConstants.New_GTFReport_Remarks)!=null) && (!"".equalsIgnoreCase(rprtObject.getString(BudgetConstants.New_GTFReport_Remarks).trim())))?(rprtObject.getString(BudgetConstants.New_GTFReport_Remarks)):"";
				}catch(com.google.appengine.labs.repackaged.org.json.JSONException exception){
					remarks = "";
				}
				gtfReport.setRemarks(remarks);
				multiBrand = rprtObject.getString(BudgetConstants.isMultiBrand);
				
				Map<String,GtfReport> gtfReportMap = util.getAllReportDataFromCache(BudgetConstants.costCenter);
				Set<String> existingGmemoriIds = gtfReportMap.keySet();
				if(existingGmemoriIds.contains(rprtObject.getString(BudgetConstants.New_GTFReport_gMemoriId))){
					try {
						resp.sendRedirect("/");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    break;
				}
				
				if(multiBrand !=null && !"".equalsIgnoreCase(multiBrand.trim()) && "true".equalsIgnoreCase(multiBrand.trim())){
					prepareMultiBrandProjectData(gtfReports,gtfReport,rprtObject,timeStamp);
				}else{
					prepareSingleBrandProjectData(gtfReports,gtfReport,rprtObject, false,timeStamp);
				}
				//gtfReports.add(gtfReport);
			}
			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} 
	}
	
	public void prepareSingleBrandProjectData(List<GtfReport> gtfReports,GtfReport gtfReport,JSONObject rprtObject, boolean isMultibrand,String timeStamp){
		try{
		
		gtfReport.setgMemoryId(rprtObject.getString(BudgetConstants.New_GTFReport_gMemoriId));
		gtfReport.setProjectName(rprtObject.getString(BudgetConstants.New_GTFReport_ProjectName));
		gtfReport.setBrand(rprtObject.getString(BudgetConstants.New_GTFReport_Brand));
		gtfReport.setCreateDate(timeStamp);
		gtfReport.setYear(BudgetConstants.dataYEAR);
		try {
			gtfReport.setPercent_Allocation(BudgetConstants.GTF_Percent_Total);
		} catch (NumberFormatException e) {
			gtfReport.setPercent_Allocation(0);
		}
		//String poDesc = rprtObject.getString(BudgetConstants.New_GTFReport_PoDesc);
	//	gtfReport.setgMemoryId(poDesc.substring(0, 6));
		Map<String, Double> plannedMap = new HashMap<String, Double>();
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		for (int cnt = 0; cnt <= BudgetConstants.months.length-1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			try {
				plannedMap.put(BudgetConstants.months[cnt],
						roundDoubleValue(Double.parseDouble(rprtObject.getString(Integer.toString(cnt+BudgetConstants.months.length-1))),2));
			} catch (NumberFormatException e ) {
				plannedMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReport.setPlannedMap(plannedMap);
		gtfReport.setBenchmarkMap(setZeroMap);
		gtfReport.setAccrualsMap(setZeroMap);
		gtfReport.setVariancesMap(setZeroMap);
		gtfReport.setMultiBrand(isMultibrand);
		gtfReports.add(gtfReport);
		
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	public void prepareMultiBrandProjectData(List<GtfReport> gtfReports,GtfReport gtfReport1,JSONObject rprtObject,String timeStamp){
		prepareSingleBrandProjectData(gtfReports, gtfReport1, rprtObject, true,timeStamp);
		GtfReport gtfReport = null; 
		JSONArray jsonArray = null;
		JSONObject multiBrandObject = null;
		Double value = 0.0;
		double percent_allocation = 0.0;
		String prj_owner;
		String prj_owner_email;
		DBUtil util = new DBUtil();
		String email = "";
		Map<String,UserRoleInfo> userMap = util.readAllUserInfo();
		try{
		String mutliBrandArray = rprtObject.getString(BudgetConstants.multiBrandInput);
		jsonArray = new JSONArray(mutliBrandArray);
		for(int i=0;i<jsonArray.length();i++){
			try {
				gtfReport = (GtfReport)gtfReport1.clone();
			} catch (CloneNotSupportedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			multiBrandObject = jsonArray.getJSONObject(i);
			if(multiBrandObject.getString("4") ==null || "".equals(multiBrandObject.getString("4").trim())){
				break;
			}
			gtfReport.setCreateDate(timeStamp);
		gtfReport.setYear(BudgetConstants.dataYEAR);
		gtfReport.setProjectName(multiBrandObject.getString("4"));
		gtfReport.setBrand(multiBrandObject.getString("1"));
		gtfReport.setgMemoryId(multiBrandObject.getString("5"));
		prj_owner = multiBrandObject.getString("7");
		prj_owner_email = util.getPrjEmailByName(prj_owner);
		gtfReport.setRequestor(multiBrandObject.getString("7")+":"+rprtObject.getString(BudgetConstants.New_GTFReport_ProjectOwner));
		email = gtfReport.getEmail();
		gtfReport.setEmail(prj_owner_email+":"+email);
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
		for (int cnt = 0; cnt < BudgetConstants.months.length-1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			try {
				value = roundDoubleValue(parentPlannedMap.get(BudgetConstants.months[cnt])*percent_allocation/100, 2);
				plannedMap.put(BudgetConstants.months[cnt],value);
			} catch (NumberFormatException e ) {
				plannedMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		plannedMap.put(BudgetConstants.months[BudgetConstants.months.length-1], Double.parseDouble(multiBrandObject.getString("3")));
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
