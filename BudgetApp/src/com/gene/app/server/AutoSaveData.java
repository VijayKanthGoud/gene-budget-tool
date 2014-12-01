package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.bean.BudgetSummary;
import com.gene.app.bean.GtfReport;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.DBUtil;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class AutoSaveData extends HttpServlet {
	DBUtil util = new DBUtil();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		String cellNum = req.getParameter(BudgetConstants.CELL_NUM).toString();
		String objarray = "[]";
		//String sessionObjArray = (String)session.getAttribute("objArray");
		boolean fromSession = false;
		if (req.getParameter(BudgetConstants.objArray) != null) {
			objarray = req.getParameter(BudgetConstants.objArray).toString();
		}else{
			fromSession = true;
		}
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
		UserRoleInfo user = (UserRoleInfo)session.getAttribute("userInfo");
		BudgetSummary summary = util.getSummaryFromCache(user.getCostCenter());
		Map<String,BudgetSummary> budgetMap = summary.getBudgetMap();
		BudgetSummary summaryObj = new BudgetSummary();
		JSONArray jsonArray = null;
		JSONObject rprtArray = null;
		Map<String, GtfReport> gtfReportMap =  new LinkedHashMap<String,GtfReport>();
		Map<String, GtfReport> editedGtfReportMap =  new LinkedHashMap<String,GtfReport>();
		String keyNum = "";
		String sessionKey = "";
		String cellValue = "";
		GtfReport gtfReportObj = null;
		try {
			jsonArray = new JSONArray(objarray);
			gtfReportMap = util
					.getAllReportDataFromCache(BudgetConstants.costCenter);
			for (int count = 0; count < jsonArray.length(); count++) {
				rprtArray = jsonArray.getJSONObject(count);
				keyNum = rprtArray.getString("0");
				sessionKey = sessionKey+keyNum+",";
				cellValue = rprtArray.getString("1");
				if (keyNum != null && !"".equalsIgnoreCase(keyNum.trim())) {
					gtfReportObj = gtfReportMap.get(keyNum);
					if (gtfReportObj != null) {
						if (Integer.parseInt(cellNum) == BudgetConstants.CELL_REMARKS) {
							String remarks = cellValue;
							gtfReportObj.setRemarks(remarks);
							gtfReportMap.put(keyNum, gtfReportObj);
							//editedGtfReportMap.put(keyNum, gtfReportObj);
						} else if(Integer.parseInt(cellNum) == BudgetConstants.CELL_PONUMBER){
							String poNumber = cellValue;
							gtfReportObj.setPoNumber(poNumber);
							gtfReportObj.setStatus("Active");
							gtfReportMap.put(keyNum, gtfReportObj);	
						} else {
							Map<String, Double> plannedMap = gtfReportObj
									.getPlannedMap();
							if (plannedMap != null) {
								oldPlannedValue = plannedMap.get(BudgetConstants.months[Integer
										.parseInt(cellNum)]);
								newPlannedValue = Double.parseDouble(cellValue);
								brand = gtfReportObj.getBrand();
								summaryObj = budgetMap.get(brand);
								if(summaryObj!=null){
								plannedTotal = summaryObj.getPlannedTotal();
								summaryObj.setPlannedTotal(plannedTotal+newPlannedValue-oldPlannedValue);
								budgetMap.put(brand, summaryObj);
								summary.setBudgetMap(budgetMap);
								}
								plannedMap.put(BudgetConstants.months[Integer
										.parseInt(cellNum)], Double
										.parseDouble(cellValue));
								gtfReportObj.setPlannedMap(plannedMap);
								gtfReportMap.put(keyNum, gtfReportObj);
								//editedGtfReportMap.put(keyNum, gtfReportObj);
							}
						}
					}
				}

				
			}
				String key = (String) session
						.getAttribute(BudgetConstants.KEY);
				util.saveAllReportDataToCache(BudgetConstants.costCenter,
						gtfReportMap);
				if (key != null && key.isEmpty()) {
					key = sessionKey;
				}
				String[] keys = {};
				List<GtfReport> gtfList = new ArrayList<GtfReport>();
				if(key!=null){
					keys = key.split(",");
					for(int i=0;i<keys.length;i++){
						gtfList.add(gtfReportMap.get(keys[i]));
					}
				}
				if (fromSession) {
					util.saveAllDataToDataStore(gtfList);
				}

				if ((key != null && sessionKey != null)
						&& !(key.equals(sessionKey))) {
					util.saveAllDataToDataStore(gtfList);
				}
				if(cellNum.matches("[-+]?[0-9]+") && Integer.parseInt(cellNum) == BudgetConstants.CELL_PONUMBER){
					util.saveAllDataToDataStore(gtfList);
				}
				 session.setAttribute(BudgetConstants.KEY, sessionKey);
				 session.setAttribute("objArray",objarray);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		util.putSummaryToCache(summary,user.getCostCenter());
		session.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(summary));
	}

}
