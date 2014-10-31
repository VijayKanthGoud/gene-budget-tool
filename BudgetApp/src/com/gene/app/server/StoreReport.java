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
import javax.servlet.http.HttpSession;

import com.gene.app.bean.GtfReport;
import com.google.appengine.api.users.User;
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
		String email = "";
		HttpSession session = req.getSession();
		User user = (User)session.getAttribute("loggedInUser");
		email = user.getEmail();
		try {
			JSONArray jsonArray = new JSONArray(objarray);
			for (int count = 0; count < jsonArray.length(); count++) {
				GtfReport gtfReport = new GtfReport();
				JSONObject rprtObject = jsonArray.getJSONObject(count);
				String projectWBS = rprtObject.getString("4");
				if(projectWBS == null || projectWBS.isEmpty() || projectWBS.length() == 0){
					continue;
				}
				System.out.println("rprtObject : " + rprtObject);
				gtfReport.setProjectName(rprtObject.getString("1"));
				gtfReport.setEmail(email);
				String status = rprtObject.getString("2");
				int flag = 0;
				if("New".equalsIgnoreCase(status.trim())){
					flag = 1;
				}else if("Active".equalsIgnoreCase(status.trim())){
					flag = 2;
				}else{
					flag = 3;
				}
				gtfReport.setFlag(flag);
				gtfReport.setStatus(status);
				//gtfReport.setStatus(rprtObject.getString("2"));
				gtfReport.setRequestor(rprtObject.getString("3"));
				gtfReport.setProject_WBS(rprtObject.getString("4"));
				gtfReport.setWBS_Name(rprtObject.getString("5"));
				gtfReport.setSubActivity(rprtObject.getString("6"));
				gtfReport.setBrand(rprtObject.getString("7"));
				try {
					gtfReport.setPercent_Allocation(Integer.parseInt(rprtObject
							.getString("8")));
				} catch (NumberFormatException e) {
					gtfReport.setPercent_Allocation(0);
				}
				gtfReport.setPoNumber(rprtObject.getString("9"));
				String poDesc = rprtObject.getString("10");
				System.out.println("poDesc"+poDesc);
				gtfReport.setgMemoryId(poDesc.substring(0, 6));
				gtfReport.setPoDesc(poDesc.substring(7, poDesc.length()));
				gtfReport.setVendor(rprtObject.getString("11"));
				String remarks = null;
				try{
					remarks = ((rprtObject.getString("25")!=null) && (!"".equalsIgnoreCase(rprtObject.getString("25").trim())))?(rprtObject.getString("25")):"";
				}catch(com.google.appengine.labs.repackaged.org.json.JSONException exception){
					remarks = "";
				}
				gtfReport.setRemarks(remarks);
				Map<String, Double> benchmarkMap = new HashMap<String, Double>();
				Map<String, Double> setZeroMap = new HashMap<String, Double>();
				for (int cnt = 12; cnt <= 23; cnt++) {
					setZeroMap.put(GtfReport.months[cnt - 12], 0.0);
					try {
						benchmarkMap.put(GtfReport.months[cnt - 12],
								Double.parseDouble(rprtObject.getString(Integer.toString(cnt))));
					} catch (NumberFormatException e ) {
						benchmarkMap.put(GtfReport.months[0], 0.0);
					}
				}
				gtfReport.setBenchmarkMap(benchmarkMap);
				gtfReport.setPlannedMap(setZeroMap);
				gtfReport.setAccrualsMap(setZeroMap);
				gtfReport.setVariancesMap(setZeroMap);
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
