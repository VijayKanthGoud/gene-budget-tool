package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.bean.GtfReport;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class StoreReport extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		String objarray = req.getParameter("objarray").toString();
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		try {
			JSONArray jsonArray = new JSONArray(objarray);
			for (int count = 0; count < jsonArray.length(); count++) {
				GtfReport gtfReport = new GtfReport();
				JSONObject rprtObject = jsonArray.getJSONObject(count);
				String projectWBS = rprtObject.getString("2");
				if(projectWBS == null || projectWBS.isEmpty() || projectWBS.length() == 0){
					break;
				}
				System.out.println("rprtObject : " + rprtObject);
				gtfReport.setRequestor(rprtObject.getString("1"));
				gtfReport.setProject_WBS(rprtObject.getString("2"));
				gtfReport.setWBS_Name(rprtObject.getString("3"));
				gtfReport.setSubActivity(rprtObject.getString("4"));
				gtfReport.setBrand(rprtObject.getString("5"));
				try {
					gtfReport.setPercent_Allocation(Integer.parseInt(rprtObject
							.getString("6")));
				} catch (NumberFormatException e) {
					gtfReport.setPercent_Allocation(0);
				}
				gtfReport.setPoNumber(rprtObject.getString("7"));
				gtfReport.setPoDesc(rprtObject.getString("8"));
				gtfReport.setVendor(rprtObject.getString("9"));
				Map<String, Double> benchmarkMap = new HashMap<String, Double>();
				Map<String, Double> setZeroMap = new HashMap<String, Double>();
				for (int cnt = 10; cnt <= 21; cnt++) {
					setZeroMap.put(GtfReport.months[cnt - 10], 0.0);
					try {
						benchmarkMap.put(GtfReport.months[cnt - 10],
								Double.parseDouble(rprtObject.getString(Integer.toString(cnt))));
					} catch (NumberFormatException e ) {
						benchmarkMap.put(GtfReport.months[0], 0.0);
					}
				}
				gtfReport.setBenchmarkMap(benchmarkMap);
				gtfReport.setPlannedMap(setZeroMap);
				gtfReport.setAccrualsMap(setZeroMap);
				gtfReport.setVariancesMap(setZeroMap);
				//gtfReport.setStatus("Active");
				gtfReports.add(gtfReport);
			}

		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		generateProjectIdUsingJDOTxn(gtfReports);
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

}
