package com.gene.app.server;

import java.io.IOException;
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
		if (req.getParameter(BudgetConstants.objArray) != null) {
			objarray = req.getParameter(BudgetConstants.objArray).toString();
		}
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

		try {
			jsonArray = new JSONArray(objarray);
			for (int count = 0; count < jsonArray.length(); count++) {
				GtfReport gtfReportObj = null;
				rprtArray = jsonArray.getJSONObject(count);
				String keyNum = "";
				keyNum = rprtArray.getString("0");
				String cellValue = "";
				cellValue = rprtArray.getString("1");
				Map<String, GtfReport> gtfReportMap = util
						.getAllReportDataFromCache(BudgetConstants.costCenter);
				if (keyNum != null && !"".equalsIgnoreCase(keyNum.trim())) {
					gtfReportObj = gtfReportMap.get(keyNum);
					if (gtfReportObj != null) {
						if (Integer.parseInt(cellNum) == BudgetConstants.CELL_REMARKS) {
							String remarks = cellValue;
							gtfReportObj.setRemarks(remarks);
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
								plannedTotal = summaryObj.getPlannedTotal();
								summaryObj.setPlannedTotal(plannedTotal+newPlannedValue-oldPlannedValue);
								budgetMap.put(brand, summaryObj);
								summary.setBudgetMap(budgetMap);
								plannedMap.put(BudgetConstants.months[Integer
										.parseInt(cellNum)], Double
										.parseDouble(cellValue));
								gtfReportObj.setPlannedMap(plannedMap);
								gtfReportMap.put(keyNum, gtfReportObj);
							}
						}
					}
				}

				String sessionKey = (String) session
						.getAttribute(BudgetConstants.KEY);
				util.saveAllReportDataToCache(BudgetConstants.costCenter,
						gtfReportMap);
				if (sessionKey != null && sessionKey.isEmpty()) {
					sessionKey = keyNum;
				}
				GtfReport sessionGtfReport = util.readReportDataFromCache(
						sessionKey, BudgetConstants.costCenter);
				if (keyNum == null && cellValue == null && cellNum == null
						&& sessionKey != null) {
					util.saveDataToDataStore(sessionGtfReport);
				}

				if ((keyNum != null && sessionKey != null)
						&& !(keyNum.equals(sessionKey))) {
					util.saveDataToDataStore(sessionGtfReport);
				}
				 session.setAttribute(BudgetConstants.KEY, keyNum);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*BudgetSummary summary = util
				.readBudgetSummary(BudgetConstants.costCenter);*/
		util.putSummaryToCache(summary,user.getCostCenter());
		session.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(summary));
	}

}
