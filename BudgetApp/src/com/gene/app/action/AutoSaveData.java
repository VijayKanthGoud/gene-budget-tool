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

import com.gene.app.dao.CostCenterCacheUtil;
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
	private final static Logger LOGGER = Logger.getLogger(AutoSaveData.class
			.getName());
	DBUtil util = new DBUtil();
	CostCenterCacheUtil costCenterCacheUtil = new CostCenterCacheUtil();
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Autosave called...");
		HttpSession session = req.getSession();
		String cellNum = req.getParameter(BudgetConstants.CELL_NUM).toString();
		LOGGER.log(Level.INFO, "cellNum : " + cellNum);
		String mapType = req.getParameter(BudgetConstants.MAP_TYPE).toString();
		LOGGER.log(Level.INFO, "mapType : " + mapType);
		String objarray = "[]";
		objarray = req.getParameter(BudgetConstants.objArray).toString();
		LOGGER.log(Level.INFO, "objarray : " + objarray);
		String costCenter = req.getParameter("costCenter").toString();
		LOGGER.log(Level.INFO, "costCenter : " + costCenter);
		
		String poErrorMsg = "";
		
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int qtr = month / 3;
		Map<String, Date> cutofDates = util.getCutOffDates();
		Date cutOfDate = cutofDates.get(qtr + "");
		LOGGER.log(Level.INFO, "cutOfDate : " + cutOfDate);
		String brand = "";
		 final String url = req.getRequestURL().toString();
         final String baseURL = url.substring(0, url.length()
                            - req.getRequestURI().length())
                            + req.getContextPath() + "/";
		double oldPlannedValue = 0.0;
		double newPlannedValue = 0.0;
		double plannedTotal = 0.0;
		double accrualTotal = 0.0;
		double varianceTotal = 0.0;
		double benchMarkTotal = 0.0;
		double oldAccrualValue = 0.0;

		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		BudgetSummary summary = new BudgetSummary();

		// User from session if not found get from user services
		if (user != null && costCenter != null
				&& util.getSummaryFromCache(costCenter) != null) {
			user.setSelectedCostCenter(costCenter);
			summary = util.getSummaryFromCache(costCenter);
		} else {
			UserService userService = UserServiceFactory.getUserService();
			String email = userService.getCurrentUser().getEmail();
			user = util.readUserRoleInfo(email);
		}

		Map<String, BudgetSummary> budgetMap = summary.getBudgetMap();
		BudgetSummary summaryObj = new BudgetSummary();
		BudgetSummary newSummaryObj = new BudgetSummary();
		JSONArray jsonArray = null;
		JSONObject rprtArray = null;

		Map<String, GtfReport> gtfReportMap = new LinkedHashMap<String, GtfReport>();

		String keyNum = "";
		String sessionKey = "";
		String cellValue = "";
		GtfReport gtfReportObj = null;
		try {
			jsonArray = new JSONArray(objarray);
			
			// Retrieved all gtfReports for that cost center
			gtfReportMap = util.getAllReportDataFromCache(costCenter);

			for (int count = 0; count < jsonArray.length(); count++) {
				rprtArray = jsonArray.getJSONObject(count);
				keyNum = rprtArray.getString("0");
				LOGGER.log(Level.INFO, "Cell Num received : " + keyNum);
				cellValue = rprtArray.getString("1");
				LOGGER.log(Level.INFO, "Cell Value received : " + keyNum);
				if (Integer.parseInt(cellNum) == BudgetConstants.CELL_GMEMORI_ID) {
					sessionKey = sessionKey + cellValue + ",";
				} else {
					sessionKey = sessionKey + keyNum + ",";
				}
				if (keyNum != null && !"".equalsIgnoreCase(keyNum.trim())) {
					gtfReportObj = gtfReportMap.get(keyNum);

					if (gtfReportObj != null) {
						// on change of remark cell
						if (Integer.parseInt(cellNum) == BudgetConstants.CELL_REMARKS) {
							cellValue = cellValue.replace("\\", "\\\\")
									.replace("\"", "\\\"")
									.replace("\'", "\\\'").replace("<", "&lt;")
									.replace(">", "&gt;");
							String remarks = cellValue;
							gtfReportObj.setRemarks(remarks);
							gtfReportMap.put(keyNum, gtfReportObj);
						} 
						// on change of PO number
						else if (Integer.parseInt(cellNum) == BudgetConstants.CELL_PONUMBER) {
							String poNumber = cellValue;
							boolean poExists = util.validatePONum(poNumber);
							if (poExists) {
								poErrorMsg = "<poError>:" + "PO Number already exists !!!";
								break;
							}
							LOGGER.log(Level.INFO, "Changing PO NUMBER ... : "
									+ poNumber);
							gtfReportObj.setPoNumber(poNumber);
							gtfReportObj.setStatus("Active");
							gtfReportObj.setFlag(2);
							gtfReportMap.put(keyNum, gtfReportObj);
						} 
						// on change of others
						else if (Integer.parseInt(cellNum) == BudgetConstants.CELL_PNAME
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_PWBS
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_SUBACTVTY
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_VENDOR
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_UNIT
								|| Integer.parseInt(cellNum) == BudgetConstants.CELL_BRAND) {
							String strValue = cellValue;
							if (strValue == null) {
								strValue = "";
							}
							switch (Integer.parseInt(cellNum)) {
							case BudgetConstants.CELL_PNAME:
								gtfReportObj.setProjectName(strValue
										.replace("\\", "\\\\")
										.replace("\"", "\\\"")
										.replace("\'", "\\\'"));
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
								summaryObj = budgetMap.get(gtfReportObj.getBrand());
								newSummaryObj = budgetMap.get(strValue);
								for(int i=0;i<BudgetConstants.months.length;i++){
								plannedTotal = plannedTotal+gtfReportObj.getPlannedMap().get(BudgetConstants.months[i]);
								benchMarkTotal = benchMarkTotal+gtfReportObj.getPlannedMap().get(BudgetConstants.months[i]);
								varianceTotal = varianceTotal+gtfReportObj.getPlannedMap().get(BudgetConstants.months[i]);
								accrualTotal = accrualTotal+gtfReportObj.getPlannedMap().get(BudgetConstants.months[i]);
								}
								if(summaryObj == null){
									summaryObj = new BudgetSummary();
								}if(newSummaryObj == null){
									newSummaryObj = new BudgetSummary();
								}
								newSummaryObj.setPlannedTotal(newSummaryObj.getPlannedTotal()+plannedTotal);
								newSummaryObj.setBenchmarkTotal(newSummaryObj.getBenchmarkTotal()+benchMarkTotal);
								newSummaryObj.setVarianceTotal(newSummaryObj.getVarianceTotal()+varianceTotal);
								newSummaryObj.setAccrualTotal(newSummaryObj.getAccrualTotal()+accrualTotal);
								summaryObj.setPlannedTotal(summaryObj.getPlannedTotal()-plannedTotal);
								summaryObj.setBenchmarkTotal(summaryObj.getBenchmarkTotal()-benchMarkTotal);
								summaryObj.setVarianceTotal(summaryObj.getVarianceTotal()-varianceTotal);
								summaryObj.setAccrualTotal(summaryObj.getAccrualTotal()-accrualTotal);
								budgetMap.put(gtfReportObj.getBrand(), summaryObj);
								budgetMap.put(strValue, newSummaryObj);
								summary.setBudgetMap(budgetMap);
								util.putSummaryToCache(summary, costCenter);
								if (gtfReportObj.getMultiBrand()) {
									gtfReportMap = deleteChildProjects(
											gtfReportObj, gtfReportMap,baseURL,costCenter);
									gtfReportObj.setMultiBrand(false);
									gtfReportObj
											.setChildProjectList(new ArrayList<String>());
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
						} 
						// on edit of planned map
						else {
							Map<String, Double> plannedMap = gtfReportObj
									.getPlannedMap();
							Map<String, Double> accrualMap = gtfReportObj
									.getAccrualsMap();
							Map<String, Double> varianceMap = gtfReportObj
									.getVariancesMap();
							Map<String, Double> benchMarkMap = gtfReportObj
									.getBenchmarkMap();
							oldAccrualValue = 0.0;
							if (plannedMap != null) {

								oldPlannedValue = plannedMap
										.get(BudgetConstants.months[Integer
												.parseInt(cellNum)]);

								newPlannedValue = Double.parseDouble(cellValue);

								brand = gtfReportObj.getBrand().trim();
								summaryObj = budgetMap.get(brand);
								if (summaryObj != null) {
									plannedTotal = summaryObj.getPlannedTotal();
									summaryObj
											.setPlannedTotal(plannedTotal
													+ newPlannedValue
													- oldPlannedValue);
									if (mapType.equalsIgnoreCase("accrual")
											&& gtfReportObj
													.getStatus()
													.equalsIgnoreCase(
															BudgetConstants.status_Active)) {
										oldAccrualValue = accrualMap
												.get(BudgetConstants.months[Integer
														.parseInt(cellNum)]);
										summaryObj.setAccrualTotal(summaryObj
												.getAccrualTotal()
												+ newPlannedValue
												- oldAccrualValue);
										summaryObj.setVarianceTotal(summaryObj
												.getBenchmarkTotal()
												- summaryObj.getAccrualTotal());
									}
									budgetMap.put(brand, summaryObj);
									summary.setBudgetMap(budgetMap);
									util.putSummaryToCache(summary, costCenter);
								}
								plannedMap.put(BudgetConstants.months[Integer
										.parseInt(cellNum)], Double
										.parseDouble(cellValue));
								if (gtfReportObj.getStatus().equalsIgnoreCase(
										BudgetConstants.status_New)
										|| gtfReportObj
												.getStatus()
												.equalsIgnoreCase(
														BudgetConstants.status_Active)) {
									if (qtr != (Integer.parseInt(cellNum) / 3)
											|| cutOfDate.after(new Date())) {
										benchMarkMap.put(
												BudgetConstants.months[Integer
														.parseInt(cellNum)],
												newPlannedValue);
										gtfReportObj
												.setBenchmarkMap(benchMarkMap);
									}
									varianceMap
											.put(BudgetConstants.months[Integer
													.parseInt(cellNum)],
													benchMarkMap
															.get(BudgetConstants.months[Integer
																	.parseInt(cellNum)])
															- accrualMap
																	.get(BudgetConstants.months[Integer
																			.parseInt(cellNum)]));
								}
								gtfReportObj.setPlannedMap(plannedMap);
								gtfReportMap.put(keyNum, gtfReportObj);
							}
						}
					}
				}
			}
			//util.saveAllReportDataToCache(costCenter, gtfReportMap);
			String[] keys = {};
			List<GtfReport> gtfList = new ArrayList<GtfReport>();
			if (sessionKey != null) {
				keys = sessionKey.split(",");
				for (int i = 0; i < keys.length; i++) {
					gtfList.add(gtfReportMap.get(keys[i]));
				}
			}
			costCenterCacheUtil.putCostCenterDataToCache(costCenter, gtfReportMap);
			util.generateProjectIdUsingJDOTxn(gtfList,"",baseURL,costCenter);
			session.setAttribute(BudgetConstants.KEY, sessionKey);
			session.setAttribute("objArray", objarray);
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		if (user != null && user.getSelectedCostCenter() != null) {
			util.putSummaryToCache(summary, costCenter);
		}
		session.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(summary) + poErrorMsg);
	}

	/**
	 * Delete child projects if a multibrand is converted in to a single brand
	 * project.
	 * 
	 * @param gtfReport
	 *            the project report objects
	 * @param gtfReportMap
	 *            the map containing report objects
	 * @return the map containing all reports for that cost center
	 */
	public Map<String, GtfReport> deleteChildProjects(GtfReport gtfReport,
			Map<String, GtfReport> gtfReportMap,String baseURL,String costCenter) {
		List<String> childPrjList = gtfReport.getChildProjectList();
		List<GtfReport> gtfPrjList = new ArrayList<GtfReport>();
		if (childPrjList != null && !childPrjList.isEmpty()) {
			GtfReport gtfRpt = new GtfReport();
			for (String childId : childPrjList) {
				gtfRpt = gtfReportMap.get(childId);
				if (gtfRpt.getgMemoryId().contains(".")) {
					gtfPrjList.add(gtfRpt);
				}
			}
		}
		util.removeExistingProject(gtfPrjList,baseURL,costCenter);
		gtfReportMap = util
				.getAllReportDataFromCache(gtfReport.getCostCenter());
		return gtfReportMap;
	}
	
}
