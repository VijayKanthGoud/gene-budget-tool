package com.gene.app.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.BudgetSummary;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class AutoSaveData extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(AutoSaveData.class.getName());
	DBUtil util = new DBUtil();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Inside Autosave...");
		HttpSession session = req.getSession();
		String cellNum = req.getParameter(BudgetConstants.CELL_NUM).toString();
		LOGGER.log(Level.INFO, "cellNum : " + cellNum);
		String objarray = "[]";
		String mapType = req.getParameter(BudgetConstants.MAP_TYPE).toString();
		LOGGER.log(Level.INFO, "mapType : " + mapType);
		UserService userService;
		String email="";
		String gMemoriIdFromStudy = "";
		//String sessionObjArray = (String)session.getAttribute("objArray");
		boolean fromSession = false;
		if (req.getParameter(BudgetConstants.objArray) != null) {
			objarray = req.getParameter(BudgetConstants.objArray).toString();
		}else{
			fromSession = true;
		}
		LOGGER.log(Level.INFO, "objarray : " + objarray);
		/*else if(sessionObjArray!=null){
		}
			objarray = sessionObjArray;
			fromSession = true;
		}else if(sessionObjArray == null){
			sessionObjArray = "[]";
		}*/
		String brand = "";
		double oldPlannedValue = 0.0;
		double newPlannedValue = 0.0;
		double plannedTotal = 0.0;
		double oldAccrualValue = 0.0;
		UserRoleInfo user = (UserRoleInfo)session.getAttribute("userInfo");
		BudgetSummary summary = new BudgetSummary();
		if(user != null && user.getCostCenter() != null &&util.getSummaryFromCache(user.getCostCenter()) != null){
			summary = util.getSummaryFromCache(user.getCostCenter());
		}else{
			userService = UserServiceFactory.getUserService();//(User)session.getAttribute("loggedInUser");
			email = userService.getCurrentUser().getEmail();
			user = util.readUserRoleInfo(email);
		}
		Map<String,BudgetSummary> budgetMap = summary.getBudgetMap();
		BudgetSummary summaryObj = new BudgetSummary();
		JSONArray jsonArray = null;
		JSONObject rprtArray = null;
		Map<String, GtfReport> gtfReportMap =  new LinkedHashMap<String,GtfReport>();
		Map<String, GtfReport> completeGtfRptMap =  new LinkedHashMap<String,GtfReport>();
		Map<String, GtfReport> editedGtfReportMap =  new LinkedHashMap<String,GtfReport>();
		String keyNum = "";
		String sessionKey = "";
		String cellValue = "";
		GtfReport gtfReportObj = null;
		try {
			jsonArray = new JSONArray(objarray);
			gtfReportMap = util
					.getAllReportDataFromCache(user.getCostCenter());
			completeGtfRptMap = util.getAllReportDataCollectionFromCache(BudgetConstants.GMEMORI_COLLECTION);
			for (int count = 0; count < jsonArray.length(); count++) {
				rprtArray = jsonArray.getJSONObject(count);
				keyNum = rprtArray.getString("0");
				
				LOGGER.log(Level.INFO, "KeyNum received : " + keyNum);
				sessionKey = sessionKey+keyNum+",";
				cellValue = rprtArray.getString("1");
				if (keyNum != null && !"".equalsIgnoreCase(keyNum.trim())) {
					gtfReportObj = gtfReportMap.get(keyNum);
					
					if (gtfReportObj != null) {
						if (Integer.parseInt(cellNum) == BudgetConstants.CELL_REMARKS) {
							if(cellValue.contains("\"")){
								cellValue = cellValue.replace("\\", "\\\\").replace("\"", "\\\"").replace("\'", "\\\'");
							}
							String remarks = cellValue;
							gtfReportObj.setRemarks(remarks);
							gtfReportMap.put(keyNum, gtfReportObj);
						} else if(Integer.parseInt(cellNum) == BudgetConstants.CELL_PONUMBER){
							LOGGER.log(Level.INFO, "Changing PO NUMBER ... ");
							String poNumber = cellValue;
							LOGGER.log(Level.INFO, "cellValue : " + cellValue);
							gtfReportObj.setPoNumber(poNumber);
							LOGGER.log(Level.INFO, "poNumber : " + poNumber);
							gtfReportObj.setStatus("Active");
							LOGGER.log(Level.INFO, "Changed project status to : " + gtfReportObj.getStatus());
							gtfReportObj.setFlag(2);
							LOGGER.log(Level.INFO, "Set flag to : " + gtfReportObj.getFlag());
							gtfReportMap.put(keyNum, gtfReportObj);	
						} else if(Integer.parseInt(cellNum) == BudgetConstants.CELL_PNAME ||
								Integer.parseInt(cellNum) == BudgetConstants.CELL_PWBS ||
								Integer.parseInt(cellNum) == BudgetConstants.CELL_SUBACTVTY ||
								Integer.parseInt(cellNum) == BudgetConstants.CELL_VENDOR ||
								Integer.parseInt(cellNum) == BudgetConstants.CELL_BRAND){
							String strValue = cellValue;
							switch(Integer.parseInt(cellNum)){
							case BudgetConstants.CELL_PNAME:
								gtfReportObj.setProjectName(strValue);
								break;
							case BudgetConstants.CELL_PWBS:
								gtfReportObj.setProject_WBS(strValue);
								break;
							case BudgetConstants.CELL_SUBACTVTY:
								gtfReportObj.setSubActivity(strValue);
								break;
							case BudgetConstants.CELL_VENDOR:
								gtfReportObj.setVendor(strValue);
								break;
							case BudgetConstants.CELL_BRAND:
								gtfReportObj.setBrand(strValue);
								break;
							default:
								break;
								
							}
							gtfReportMap.put(keyNum, gtfReportObj);	
						}else if(Integer.parseInt(cellNum) == BudgetConstants.CELL_GMEMORI_ID){
							boolean result = util.validategMemoriId(cellValue);
							if(result){
								throw new Error("gMemori Id already exists");
							}
							gtfReportMap.remove(keyNum);
							completeGtfRptMap.remove(keyNum);
							keyNum = rprtArray.getString("1");
							gMemoriIdFromStudy = cellValue;
							gtfReportObj.setgMemoryId(gMemoriIdFromStudy);
							gtfReportObj.setDummyGMemoriId(false);
							gtfReportMap.put(keyNum, gtfReportObj);
							completeGtfRptMap.put(keyNum, gtfReportObj);
						}
							else {
						
							Map<String, Double> plannedMap = gtfReportObj
									.getPlannedMap();
							Map<String, Double> accrualMap = gtfReportObj
									.getAccrualsMap();
							Map<String, Double> varianceMap = gtfReportObj
									.getVariancesMap();
							Map<String, Double> benchMarkMap = gtfReportObj
									.getBenchmarkMap();
							oldAccrualValue=0.0;
							if (plannedMap != null) {
								
								oldPlannedValue = plannedMap.get(BudgetConstants.months[Integer
										.parseInt(cellNum)]);
								
								newPlannedValue = Double.parseDouble(cellValue);
								
								brand = gtfReportObj.getBrand().trim();
								summaryObj = budgetMap.get(brand);
								if(summaryObj!=null){// && !(gtfReportObj.getgMemoryId().contains("."))){
								plannedTotal = summaryObj.getPlannedTotal();
								summaryObj.setPlannedTotal(plannedTotal+newPlannedValue-oldPlannedValue);
								if(!(gtfReportObj.getgMemoryId().contains(".")) && gtfReportObj.getStatus().equalsIgnoreCase(BudgetConstants.status_Active)){
									oldAccrualValue = accrualMap.get(BudgetConstants.months[Integer
									                										.parseInt(cellNum)]);
								summaryObj.setAccrualTotal(summaryObj.getAccrualTotal()+newPlannedValue-oldAccrualValue);
								summaryObj.setVarianceTotal(summaryObj.getBenchmarkTotal()-summaryObj.getAccrualTotal());
								}
								budgetMap.put(brand, summaryObj);
								summary.setBudgetMap(budgetMap);
								util.putSummaryToCache(summary, user.getCostCenter());
								}
								plannedMap.put(BudgetConstants.months[Integer
										.parseInt(cellNum)], Double
										.parseDouble(cellValue));
								if(gtfReportObj.getStatus().equalsIgnoreCase(BudgetConstants.status_New)){
									benchMarkMap.put(BudgetConstants.months[Integer
																			.parseInt(cellNum)], newPlannedValue);
									gtfReportObj.setBenchmarkMap(benchMarkMap);
									}
								if(mapType.equalsIgnoreCase("accrual") && !(gtfReportObj.getgMemoryId().contains("."))){
									accrualMap.put(BudgetConstants.months[Integer
																			.parseInt(cellNum)], newPlannedValue);
									varianceMap.put(BudgetConstants.months[Integer
																			.parseInt(cellNum)], benchMarkMap.get(BudgetConstants.months[Integer
																			.parseInt(cellNum)]) - newPlannedValue);
									gtfReportObj.setAccrualsMap(accrualMap);
									gtfReportObj.setVariancesMap(varianceMap);
								}
								gtfReportObj.setPlannedMap(plannedMap);
								gtfReportMap.put(keyNum, gtfReportObj);
							}
						}
					}
				}

				
			}
				String key = (String) session
						.getAttribute(BudgetConstants.KEY);
				util.saveAllReportDataToCache(user.getCostCenter(),
						gtfReportMap);
				util.saveAllReportDataToCache(BudgetConstants.GMEMORI_COLLECTION, gtfReportMap);
				String[] keys = {};
				List<GtfReport> gtfList = new ArrayList<GtfReport>();
				if(sessionKey!=null){
					keys = sessionKey.split(",");
					for(int i=0;i<keys.length;i++){
						gtfList.add(gtfReportMap.get(keys[i]));
					}
				}
				if (true){//if(fromSession) {
					util.saveAllDataToDataStore(gtfList);
				}

				 session.setAttribute(BudgetConstants.KEY, sessionKey);
				 session.setAttribute("objArray",objarray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(user != null && user.getCostCenter() != null){
			util.putSummaryToCache(summary,user.getCostCenter());
		}
		session.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(summary));
	}

}
