package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.bean.GtfReport;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.DBUtil;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class MultiBrandServlet extends HttpServlet {
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String m_data = req.getParameter("objarray").toString();
		// String parent_key = req.getParameter("parentItem").toString();
		String project_id = "";
		String project_Name = "";
		String brand = "";
		String totalValue = "";
		String gMemoriId = "";
		Double percentage_Allocation = 100.0;
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("loggedInUser");
		String email = user.getEmail();
		List<GtfReport> gtfReportListFromDB = new ArrayList<GtfReport>();
		if (user == null) {
			UserService userService = UserServiceFactory.getUserService();
			email = userService.getCurrentUser().getEmail();
		}
		try {
			JSONArray jsonArray = new JSONArray(m_data);
			JSONObject rptObject = jsonArray.getJSONObject(0);
			project_Name = rptObject.getString("4");
			gMemoriId = rptObject.getString("5").split("\\.")[0];
			DBUtil util = new DBUtil();
			JSONObject rprtObject = null;
			Map<String, GtfReport> rptList = util.getAllReportsByPrjName(
					BudgetConstants.costCenter, project_Name, email);
			for (Map.Entry<String, GtfReport> rptMap : rptList.entrySet()) {
				gtfReportListFromDB.add(rptMap.getValue());
			}

			List<GtfReport> newGtfReportList = new ArrayList<GtfReport>();
			GtfReport gtfRpt = null;
			Map<String, Double> benchMarkMap = new LinkedHashMap<String, Double>();
			Map<String, Double> newBenchMap = new LinkedHashMap<String, Double>();
			Double value = 0.0;
			String keyValue = "";
			Map<String, Double> setZeroMap = new HashMap<String, Double>();
			for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
				setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			}

			// Inserts parent
			for (int par = 0; par < gtfReportListFromDB.size(); par++) {
				gtfRpt = gtfReportListFromDB.get(par);
				GtfReport newgtfReport = new GtfReport();
				if (gtfRpt.getgMemoryId().equalsIgnoreCase(gMemoriId)) {
					newgtfReport.setgMemoryId(gtfRpt.getgMemoryId());
					newgtfReport.setBrand(gtfRpt.getBrand());
					newgtfReport.setEmail(gtfRpt.getEmail());
					newgtfReport.setFlag(gtfRpt.getFlag());
					newgtfReport.setMultiBrand(true);
					newgtfReport.setPercent_Allocation(100.0);
					newgtfReport.setPoDesc(gtfRpt.getPoDesc());
					newgtfReport.setPoNumber(gtfRpt.getPoNumber());
					newgtfReport.setProject_WBS(gtfRpt.getProject_WBS());
					newgtfReport.setRemarks(gtfRpt.getRemarks());
					newgtfReport.setRequestor(gtfRpt.getRequestor());
					newgtfReport.setStatus(gtfRpt.getStatus());
					newgtfReport.setSubActivity(gtfRpt.getSubActivity());
					newgtfReport.setVendor(gtfRpt.getVendor());
					newgtfReport.setWBS_Name(gtfRpt.getWBS_Name());
					newgtfReport.setProjectName(project_Name);
					benchMarkMap = gtfRpt.getBenchmarkMap();
					if (benchMarkMap != null) {

						for (Map.Entry<String, Double> benchMapEntry : benchMarkMap
								.entrySet()) {
							keyValue = benchMapEntry.getKey();
							value = (benchMapEntry.getValue() * percentage_Allocation)
									/ (gtfRpt.getPercent_Allocation());
							newBenchMap.put(keyValue, value);
						}
						newgtfReport.setBenchmarkMap(newBenchMap);
					} else {
						newgtfReport.setBenchmarkMap(setZeroMap);
					}
					newgtfReport.setPlannedMap(gtfRpt.getPlannedMap());
					newgtfReport.setAccrualsMap(gtfRpt.getAccrualsMap());
					newgtfReport.setVariancesMap(gtfRpt.getVariancesMap());
					newgtfReport.setPercent_Allocation(percentage_Allocation);
					newGtfReportList.add(newgtfReport);
					// gtfReportListFromDB.add(gtfRpt);
					break;
				}
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				rprtObject = jsonArray.getJSONObject(i);
				if ("".equals(rprtObject.getString("3").trim())) {
					break;
				}
				project_id = rprtObject.getString("0");
				project_Name = rprtObject.getString("4");
				brand = rprtObject.getString("1");
				totalValue = rprtObject.getString("3");
				percentage_Allocation = Double.parseDouble(rprtObject
						.getString("2"));
				try {
					percentage_Allocation = Double.parseDouble(rprtObject
							.getString("2").trim());
				} catch (Exception e) {
					percentage_Allocation = 0.0;
				}
				for (int j = 0; j < gtfReportListFromDB.size(); j++) {
					gtfRpt = gtfReportListFromDB.get(j);
					if (project_id != null && !"".equals(project_id.trim())
							&& project_id.equalsIgnoreCase(gtfRpt.getId())) {
						GtfReport newgtfReport = new GtfReport();
						benchMarkMap = gtfRpt.getBenchmarkMap();
						newgtfReport.setgMemoryId(gtfRpt.getgMemoryId());
						newgtfReport.setBrand(gtfRpt.getBrand());
						newgtfReport.setEmail(gtfRpt.getEmail());
						newgtfReport.setFlag(gtfRpt.getFlag());
						newgtfReport.setMultiBrand(true);
						newgtfReport
								.setPercent_Allocation(percentage_Allocation);
						newgtfReport.setPoDesc(gtfRpt.getPoDesc());
						newgtfReport.setPoNumber(gtfRpt.getPoNumber());
						newgtfReport.setProject_WBS(gtfRpt.getProject_WBS());
						newgtfReport.setRemarks(gtfRpt.getRemarks());
						newgtfReport.setRequestor(gtfRpt.getRequestor());
						newgtfReport.setStatus(gtfRpt.getStatus());
						newgtfReport.setSubActivity(gtfRpt.getSubActivity());
						newgtfReport.setVendor(gtfRpt.getVendor());
						newgtfReport.setWBS_Name(gtfRpt.getWBS_Name());
						newgtfReport.setBenchmarkMap(gtfRpt.getBenchmarkMap());
						newgtfReport.setPlannedMap(gtfRpt.getPlannedMap());
						newgtfReport.setAccrualsMap(gtfRpt.getAccrualsMap());
						newgtfReport.setVariancesMap(gtfRpt.getVariancesMap());
						newgtfReport.setProjectName(project_Name);

						for (Map.Entry<String, Double> benchMapEntry : benchMarkMap
								.entrySet()) {
							keyValue = benchMapEntry.getKey();
							value = (benchMapEntry.getValue() * percentage_Allocation)
									/ (gtfRpt.getPercent_Allocation());
							newBenchMap.put(keyValue, value);
						}
						newgtfReport.setBenchmarkMap(newBenchMap);
						if (benchMarkMap != null) {

							for (Map.Entry<String, Double> benchMapEntry : benchMarkMap
									.entrySet()) {
								keyValue = benchMapEntry.getKey();
								value = (benchMapEntry.getValue() * percentage_Allocation)
										/ (gtfRpt.getPercent_Allocation());
								newBenchMap.put(keyValue, value);
							}
							newgtfReport.setBenchmarkMap(newBenchMap);
						} else {
							newgtfReport.setBenchmarkMap(setZeroMap);
						}
						newgtfReport
								.setPercent_Allocation(percentage_Allocation);
						newGtfReportList.add(newgtfReport);
						break;
					} else if (project_id != null
							&& "".equals(project_id.trim())) {
						GtfReport newgtfRpt = new GtfReport();
						newgtfRpt.setBrand(brand);
						String gmemoryId = (gtfRpt.getgMemoryId()
								.substring(0, (gtfRpt.getgMemoryId()
										.indexOf(".")) == -1 ? gtfRpt
										.getgMemoryId().length() : gtfRpt
										.getgMemoryId().indexOf(".")))
								+ "." + (i + 1);
						newgtfRpt.setgMemoryId(gmemoryId);
						newgtfRpt.setEmail(gtfRpt.getEmail());
						newgtfRpt.setFlag(gtfRpt.getFlag());
						newgtfRpt.setMultiBrand(true);
						newgtfRpt.setPercent_Allocation(percentage_Allocation);
						newgtfRpt.setPoDesc(gtfRpt.getPoDesc());
						newgtfRpt.setPoNumber(gtfRpt.getPoNumber());
						newgtfRpt.setProject_WBS(gtfRpt.getProject_WBS());
						newgtfRpt.setRemarks(gtfRpt.getRemarks());
						newgtfRpt.setRequestor(gtfRpt.getRequestor());
						newgtfRpt.setStatus(gtfRpt.getStatus());
						newgtfRpt.setSubActivity(gtfRpt.getSubActivity());
						newgtfRpt.setVendor(gtfRpt.getVendor());
						newgtfRpt.setWBS_Name(gtfRpt.getWBS_Name());
						newgtfRpt.setPlannedMap(setZeroMap);
						newgtfRpt.setAccrualsMap(setZeroMap);
						newgtfRpt.setVariancesMap(setZeroMap);
						newgtfRpt.setBenchmarkMap(setZeroMap);
						benchMarkMap = newgtfRpt.getBenchmarkMap();
						Double per_allocation = newgtfRpt
								.getPercent_Allocation();
						newgtfRpt.setProjectName(project_Name);
						if (per_allocation == 0.0) {
							per_allocation = 1.0;
						}
						for (Map.Entry<String, Double> benchMapEntry : benchMarkMap
								.entrySet()) {
							keyValue = benchMapEntry.getKey();
							value = (benchMapEntry.getValue() * percentage_Allocation)
									/ (per_allocation);
							newBenchMap.put(keyValue, value);
						}
						newGtfReportList.add(newgtfRpt);
						break;
					}
				}

			}
			util.removeExistingProject(gtfReportListFromDB);
			util.generateProjectIdUsingJDOTxn(newGtfReportList);
			cache.delete(BudgetConstants.costCenter);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
