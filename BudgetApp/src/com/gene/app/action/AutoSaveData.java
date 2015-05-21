package com.gene.app.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
		String costCenter = req.getParameter("costCenter").toString();
		String poErrorMsg = "";
		//String sessionObjArray = (String)session.getAttribute("objArray");
		boolean fromSession = false;
		if (req.getParameter(BudgetConstants.objArray) != null) {
			objarray = req.getParameter(BudgetConstants.objArray).toString();
		}else{
			fromSession = true;
		}
		LOGGER.log(Level.INFO, "objarray : " + objarray);
		
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int qtr = month / 3;
		Map<String, Date> cutofDates = util.getCutOffDates();
		Date cutOfDate = cutofDates.get(qtr+"");
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
		double benchMarkTotal = 0.0;
		double plannedTotal = 0.0;
		double oldAccrualValue = 0.0;
		
		UserRoleInfo user = (UserRoleInfo)session.getAttribute("userInfo");
		BudgetSummary summary = new BudgetSummary();
		if(user != null && costCenter != null &&util.getSummaryFromCache(costCenter) != null){
			user.setSelectedCostCenter(costCenter);
			summary = util.getSummaryFromCache(costCenter);
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
					.getAllReportDataFromCache(costCenter);
			completeGtfRptMap = util.getAllReportDataCollectionFromCache(BudgetConstants.GMEMORI_COLLECTION);
			for (int count = 0; count < jsonArray.length(); count++) {
				rprtArray = jsonArray.getJSONObject(count);
				keyNum = rprtArray.getString("0");
				//costCenter = rprtArray.getString("2");
				LOGGER.log(Level.INFO, "KeyNum received : " + keyNum);
				
				cellValue = rprtArray.getString("1");
				if(Integer.parseInt(cellNum) == BudgetConstants.CELL_GMEMORI_ID){
					sessionKey = sessionKey+cellValue+",";
				}else{
					sessionKey = sessionKey+keyNum+",";
				}
				if (keyNum != null && !"".equalsIgnoreCase(keyNum.trim())) {
					gtfReportObj = gtfReportMap.get(keyNum);
					
					if (gtfReportObj != null) {
						if (Integer.parseInt(cellNum) == BudgetConstants.CELL_REMARKS) {
							//if(cellValue.contains("\"")){
								cellValue = cellValue.replace("\\", "\\\\").replace("\"", "\\\"").replace("\'", "\\\'").replace("<", "&lt;").replace(">", "&gt;");
							//}
							String remarks = cellValue;
							gtfReportObj.setRemarks(remarks);
							gtfReportMap.put(keyNum, gtfReportObj);
						} else if(Integer.parseInt(cellNum) == BudgetConstants.CELL_PONUMBER){
							String poNumber = cellValue;
							boolean poExists = util.validatePONum(poNumber);
							if(poExists){
								poErrorMsg = "PO Number already exists !!!";
								throw new Exception("PO Number already exists !!!");
							}
							LOGGER.log(Level.INFO, "Changing PO NUMBER ... ");
							
							LOGGER.log(Level.INFO, "cellValue : " + cellValue);
							gtfReportObj.setPoNumber(poNumber);
							LOGGER.log(Level.INFO, "poNumber : " + poNumber);
							gtfReportObj.setStatus("Active");
							LOGGER.log(Level.INFO, "Changed project status to : " + gtfReportObj.getStatus());
							gtfReportObj.setFlag(2);
							LOGGER.log(Level.INFO, "Set flag to : " + gtfReportObj.getFlag());
							gtfReportMap.put(keyNum, gtfReportObj);	
						} else if (Integer.parseInt(cellNum) == BudgetConstants.CELL_PNAME
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_PWBS
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_SUBACTVTY
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_VENDOR
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_UNIT
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_BRAND) {
							String strValue = cellValue;
							if(strValue == null){
								strValue = "";
							}
							switch (Integer.parseInt(cellNum)) {
							case BudgetConstants.CELL_PNAME:
								gtfReportObj.setProjectName(strValue.replace("\\", "\\\\")
										.replace("\"", "\\\"").replace("\'", "\\\'"));
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
								if(gtfReportObj.getMultiBrand()){
									gtfReportMap = deleteChildProjects(gtfReportObj,gtfReportMap);
									gtfReportObj.setMultiBrand(false);
									gtfReportObj.setChildProjectList(new ArrayList<String>());
								}
								gtfReportObj.setBrand(strValue);
								break;
							case BudgetConstants.CELL_UNIT:
								try {
									gtfReportObj.setUnits(Integer
											.parseInt(strValue));
								} catch (Exception e) {
									gtfReportObj.setUnits(0);
								}
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
							//keyNum = rprtArray.getString("1");
							gMemoriIdFromStudy = cellValue;
							gtfReportObj.setgMemoryId(gMemoriIdFromStudy);
							gtfReportObj.setDummyGMemoriId(false);
							gtfReportMap.put(gMemoriIdFromStudy, gtfReportObj);
							completeGtfRptMap.put(gMemoriIdFromStudy, gtfReportObj);
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
								if(mapType.equalsIgnoreCase("accrual") && gtfReportObj.getStatus().equalsIgnoreCase(BudgetConstants.status_Active)){
									oldAccrualValue = accrualMap.get(BudgetConstants.months[Integer
									                                                        .parseInt(cellNum)]);
										summaryObj.setAccrualTotal(summaryObj.getAccrualTotal()+newPlannedValue-oldAccrualValue);
										summaryObj.setVarianceTotal(summaryObj.getBenchmarkTotal()-summaryObj.getAccrualTotal());
								}
								budgetMap.put(brand, summaryObj);
								summary.setBudgetMap(budgetMap);
								util.putSummaryToCache(summary, costCenter);
								}
								plannedMap.put(BudgetConstants.months[Integer
										.parseInt(cellNum)], Double
										.parseDouble(cellValue));
								if(gtfReportObj.getStatus().equalsIgnoreCase(BudgetConstants.status_New) || gtfReportObj.getStatus().equalsIgnoreCase(BudgetConstants.status_Active)){
									if( qtr != (Integer.parseInt(cellNum)/3)  || cutOfDate.after(new Date())){
										benchMarkMap.put(BudgetConstants.months[Integer
																			.parseInt(cellNum)], newPlannedValue);
										gtfReportObj.setBenchmarkMap(benchMarkMap);
									}
									varianceMap
											.put(BudgetConstants.months[Integer.parseInt(cellNum)],
													newPlannedValue - accrualMap.get(BudgetConstants.months[Integer.parseInt(cellNum)]));
								}
								if(mapType.equalsIgnoreCase("accrual") /*&& !(gtfReportObj.getgMemoryId().contains("."))*/){
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
				util.saveAllReportDataToCache(costCenter,
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
		}catch(Exception e){
			//resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"PO Number already exists !!!");
			//e.printStackTrace();
			poErrorMsg = "<poError>:"+"PO Number already exists !!!";
		}
		if(user != null && user.getSelectedCostCenter() != null){
			util.putSummaryToCache(summary,costCenter);
		}
		session.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(summary)+poErrorMsg);
	}

	public Map<String,GtfReport> deleteChildProjects(GtfReport gtfReportObj, Map<String, GtfReport> gtfReportMap){
		List<String> childPrjList = gtfReportObj.getChildProjectList();
		List<GtfReport> gtfPrjList = new ArrayList<GtfReport>();
		if(childPrjList!=null && !childPrjList.isEmpty()){
			GtfReport gtfRpt = new GtfReport();
			for(int i=0;i<childPrjList.size();i++){
			gtfRpt = gtfReportMap.get(childPrjList.get(i));
			if(gtfRpt.getgMemoryId().contains(".")){
			gtfPrjList.add(gtfRpt);
			}
			}
		}
		util.storeProjectsToCache(gtfPrjList, gtfReportObj.getCostCenter(), BudgetConstants.OLD);
		util.removeExistingProject(gtfPrjList);
		gtfReportMap = util.getAllReportDataFromCache(gtfReportObj.getCostCenter());
		return gtfReportMap;
	}
	public String validatePONum1(String PONumber,String cc){
		System.out.println("PO Number::::::::");
		//List<CostCenter_Brand> ccList = readCostCenterBrandMappingData();
		//Map<String, GtfReport> costCenterWiseGtfRptMap = null;
		//Map<String, GtfReport> poMap = null;
		String poExists = "false";
		//if(ccList!=null && !ccList.isEmpty()){
			//for(CostCenter_Brand cc: ccList){
			/*	costCenterWiseGtfRptMap = getAllReportDataFromCache(cc);
				poMap = preparePOMap(costCenterWiseGtfRptMap);
				if(poMap!=null && !poMap.isEmpty()){
					if(poMap.get(PONumber)!=null){
						poExists = "true";
						//break;
					}
				}*/
			//}
	//	}
		//System.out.println("ccList.contains(7121)"+ccList.contains("7121"));
		return poExists;
	}
}
