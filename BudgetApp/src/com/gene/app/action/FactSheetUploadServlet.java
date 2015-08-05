package com.gene.app.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.ProjectSequenceGeneratorUtil;
import com.gene.app.util.Util;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@SuppressWarnings("serial")
public class FactSheetUploadServlet extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(FactSheetUploadServlet.class.getName());
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();
	UserService userService = UserServiceFactory.getUserService();
	
	
	final int colProjectWBS = 3;
	final int colWBSName = 4;
	final int colSubActivity = 5;
	final int colBrand = 6;
	final int colAllocPerc = 7;
	final int colPONum = 8;
	final int colPODesc = 9;
	final int colVendor = 10;
	final int colRequestor = 11;
	
	
	
	// Map of Project Name ~ Projects
	Map<String, ArrayList<GtfReport>> uploadWithOutPos;
	// Map of PO Number ~ Projects
	Map<String, ArrayList<GtfReport>> uploadedPOs;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		LOGGER.log(Level.INFO, "inside factsheet upload ...");
		
		User userLoggedIn = userService.getCurrentUser();
		UserRoleInfo user = util.readUserRoleInfo(userLoggedIn.getEmail());
		
		// Handle unauthorized access
		if(user != null && !user.getRole().equalsIgnoreCase("admin")){
			resp.sendError(411, "User doesn't have permission to upload.");
			return;
		}
		
		final String url = req.getRequestURL().toString();
        final String baseURL = url.substring(0, url.length()
                           - req.getRequestURI().length())
                           + req.getContextPath() + "/";
		
        String objarray = req.getParameter(BudgetConstants.objArray).toString();
		String [] objArrayStr = objarray.split("],");
		LOGGER.log(Level.INFO, "Received JSON String = "+ objArrayStr.length);
		
		String costCentre = req.getParameter("costCenter");
		LOGGER.log(Level.INFO, "Received Cost Center = "+ costCentre);
		
		uploadedPOs = new HashMap<String, ArrayList<GtfReport>>();
		uploadWithOutPos = new HashMap<String, ArrayList<GtfReport>>();
		
		int fromLine = Integer.parseInt(req.getParameter("inputFrom"));
		int toLine = Integer.parseInt(req.getParameter("inputTo"));
		
		List<List<String>> rowList = new ArrayList<List<String>>();
		try {
			JSONArray jsonArray = new JSONArray(objarray);
			for (int count = fromLine-1; count < toLine; count++) {
				List<String> list = new ArrayList<String>();
				for (int cnt = 0; cnt < jsonArray.getJSONArray(count).length(); cnt++) {
					String varCol = jsonArray.getJSONArray(count).get(cnt)
							.toString();
					if (!varCol.equalsIgnoreCase("null")) {
						list.add(jsonArray.getJSONArray(count).get(cnt).toString());
					} else {
						list.add("");
					}
				}
				
				if(Util.isNullOrEmpty(list.get(1).toString()) || Util.isNullOrEmpty(list.get(2).toString()) ||
						Util.isNullOrEmpty(list.get(colProjectWBS).toString()) || Util.isNullOrEmpty(list.get(colWBSName).toString()) ||
						Util.isNullOrEmpty(list.get(colSubActivity).toString()) || Util.isNullOrEmpty(list.get(colBrand).toString()) ||
						Util.isNullOrEmpty(list.get(colAllocPerc).toString()) || Util.isNullOrEmpty(list.get(colPONum).toString()) ||
						Util.isNullOrEmpty(list.get(colPODesc).toString()) || Util.isNullOrEmpty(list.get(colVendor).toString()) ){
					// Skip if no PO number and PO Description
					if((!Util.isNullOrEmpty(list.get(colPODesc).toString())) && !Util.isNullOrEmpty(list.get(colPONum).toString())){
						continue;
					}else{
						rowList.add(list);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		createGTFReports(user, user,rowList, gtfReports,costCentre,baseURL);
	}

	private void createGTFReports(UserRoleInfo user,UserRoleInfo orgUser,
		List<List<String>> rowList, List<GtfReport> gtfReports,String costCentre,String baseURL) {
		
		Map<String,GtfReport> uniqueGtfRptMap = util.prepareUniqueGtfRptMap(costCentre);
		
		boolean isMultibrand = false;
		
		Map<String, GtfReport> costCenterWiseGtfRptMap = util.getAllReportDataFromCache(costCentre);
		
		for (List<String> recvdRow : rowList) {
			try{
				GtfReport gtfReport = new GtfReport();
				
				// Skip project if there is no Sub activity
				if (recvdRow.get(colSubActivity) != null	&& !recvdRow.get(colSubActivity).toString().trim().equals("")) {
					gtfReport.setSubActivity(recvdRow.get(colSubActivity).toString());
				} /*else {
					gtfReport.setSubActivity("");
				//	continue;
				}*/
				
				// Add Project Owner from Requestor if not found read from details of user uploaded the fact sheet
				if (recvdRow.get(colRequestor) != null && !recvdRow.get(colRequestor).toString().trim().equals("")) {
					if(util.readUserRoleInfoByFName(recvdRow.get(colRequestor).toString()) != null && 
							util.readUserRoleInfoByFName(recvdRow.get(colRequestor).toString()).getUserName() != null){
						gtfReport.setRequestor(util.readUserRoleInfoByFName(recvdRow.get(colRequestor).toString()).getUserName());
						gtfReport.setEmail(util.readUserRoleInfoByFName(recvdRow.get(colRequestor).toString()).getEmail());
					}else{
						gtfReport.setRequestor(orgUser.getUserName());
						gtfReport.setEmail(orgUser.getEmail());
					}
				} else {
					gtfReport.setRequestor(orgUser.getUserName());
					gtfReport.setEmail(orgUser.getEmail());
				}
				

				gtfReport.setCostCenter(costCentre);

				if (recvdRow.get(colProjectWBS) != null && !recvdRow.get(colProjectWBS).toString().trim().equals("")) {
					gtfReport.setProject_WBS(recvdRow.get(colProjectWBS).toString());
				} else {
					gtfReport.setProject_WBS("");
				}

				if (recvdRow.get(colWBSName) != null && !recvdRow.get(colWBSName).toString().toString().trim().equals("")) {
					gtfReport.setWBS_Name(recvdRow.get(colWBSName).toString());
				} else {
					gtfReport.setWBS_Name("");
				}

				// if brand name contains 'Total Product' replace it with WBS name
				if (recvdRow.get(colBrand) != null && !recvdRow.get(colBrand).toString().trim().equals("")) {
					if("Total Products".equalsIgnoreCase(recvdRow.get(colBrand).toString())){
						gtfReport.setBrand(gtfReport.getWBS_Name());	
					}else{
						gtfReport.setBrand(recvdRow.get(colBrand).toString());
					}
				} else {
					gtfReport.setBrand("No brand");
				}

				gtfReport.setPercent_Allocation(100);

				if (recvdRow.get(colPONum) != null && !recvdRow.get(colPONum).toString().trim().equals("")) {
					gtfReport.setPoNumber(recvdRow.get(colPONum).toString());
					gtfReport.setStatus("Active");
					gtfReport.setFlag(2);
				} else {
					gtfReport.setPoNumber("");
					gtfReport.setStatus("New");
					gtfReport.setFlag(1);
				}

				if (recvdRow.get(colPODesc) != null && !recvdRow.get(colPODesc).toString().trim().equals("")) {
					gtfReport.setPoDesc(recvdRow.get(colPODesc).toString().replace("\\", "\\\\")
							.replace("\"", "\\\"").replace("\'", "\\\'"));
				} else {
					gtfReport.setPoDesc("Not Available");
				}

				if (recvdRow.get(colVendor) != null && !recvdRow.get(colVendor).toString().trim().equals("")) {
					gtfReport.setVendor(recvdRow.get(colVendor).toString());
				} else {
					gtfReport.setVendor("");
				}

				String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime());
				gtfReport.setCreateDate(timeStamp);
				gtfReport.setYear(BudgetConstants.dataYEAR);
				gtfReport.setQual_Quant("Qual_Quant");
				gtfReport.setStudy_Side("study_Side");

				if(Util.isNullOrEmpty(recvdRow.get(26).toString()) && !"-".equalsIgnoreCase(recvdRow.get(26).toString().trim())){ 
					try{
						gtfReport.setUnits(Integer.parseInt(recvdRow.get(26).toString()));
					}catch(Exception e){
						gtfReport.setUnits(0);
					}
				}else{
					gtfReport.setUnits(0);
				}
				
				// Set project name from PO description by removing gmemori Id which is separated by '_'
				if (gtfReport.getPoDesc().indexOf("_") == 6) {
					gtfReport.setProjectName(gtfReport.getPoDesc().split("_")[1]);
				} else {
					gtfReport.setProjectName(gtfReport.getPoDesc());
				}
				
				// Set brand from WBS name if no po number 
				if(Util.isNullOrEmpty(gtfReport.getPoNumber())){
					gtfReport.setBrand(gtfReport.getWBS_Name());
				}

				
				// Create gtfParam to get previously existing project from unique Gtf report map
				StringBuilder gtfParam = new StringBuilder("");
				if(Util.isNullOrEmpty(gtfReport.getBrand())){
					gtfParam = gtfParam.append(gtfReport.getBrand() + ":");
				}else{
					gtfParam = gtfParam.append(":");
				}
				if(Util.isNullOrEmpty(gtfReport.getProjectName())){
					gtfParam = gtfParam.append(gtfReport.getProjectName());
				}
				
				
				GtfReport gtfRpt = uniqueGtfRptMap.get(gtfParam.toString());

				// Update if gtfReport already exists else create a new gtfReport
				if(gtfRpt != null){
					gtfReport.setId(gtfRpt.getId());
					gtfReport.setgMemoryId(gtfRpt.getgMemoryId());
					gtfReport.setChildProjectList(gtfRpt.getChildProjectList());
					if("".equalsIgnoreCase(gtfReport.getPoNumber().trim())){
						gtfReport.setPoNumber(gtfRpt.getPoNumber());
						if(Util.isNullOrEmpty(gtfReport.getPoNumber())){
							gtfReport.setStatus("Active");
							gtfReport.setFlag(2);
						}
					}
				}else{
					String gMemoriId;
					try {
						if(gtfReport.getPoDesc().indexOf("_")==6){
							gMemoriId = Integer.parseInt(gtfReport.getPoDesc().substring(0,	Math.min(gtfReport.getPoDesc().length(), 6))) + "";
							gtfReport.setDummyGMemoriId(false);
						}else{
							gMemoriId = "" + generator.nextValue();
							gtfReport.setDummyGMemoriId(true);
						}
					} catch (NumberFormatException ne) {
						gMemoriId = "" + generator.nextValue();
						gtfReport.setDummyGMemoriId(true);
					}

					gtfReport.setgMemoryId(gMemoriId);
				}

				Map<String, Double> plannedMap = new HashMap<String, Double>();
				Map<String, Double> setZeroMap = new HashMap<String, Double>();
				
				// Overrides and sets received value to planned, Benchmark and variance
				for (int cnt = 0; cnt < BudgetConstants.months.length; cnt++) {
					setZeroMap.put(BudgetConstants.months[cnt], 0.0);
					try {
						if (recvdRow.get(cnt + 12) != null
								&& !recvdRow.get(cnt + 12).toString().trim()
								.equals("")) {
							String value = "0.0";
							if (recvdRow.get(cnt + 12).toString().contains("(")) {
								value = "-" + recvdRow.get(cnt + 12).toString().replaceAll("[^\\d.]", "");
							}else{
								value = recvdRow.get(cnt + 12).toString();
							}
							plannedMap.put(BudgetConstants.months[cnt], Double.parseDouble(value));
						} else {
							plannedMap.put(BudgetConstants.months[cnt], 0.0);
						}
					} catch (Exception e1) {
						LOGGER.log(Level.WARNING, "Error" + e1);
						plannedMap.put(BudgetConstants.months[cnt], 0.0);
					}
				}
				
				// always updates the planned-map and bechmark-map without modifying accrual-map
				gtfReport.setPlannedMap(plannedMap);
				gtfReport.setBenchmarkMap(plannedMap);
				if(gtfRpt ==null){
					gtfReport.setAccrualsMap(setZeroMap);
					gtfReport.setVariancesMap(plannedMap);
				}else{
					if(gtfRpt.getAccrualsMap()!=null){
						gtfReport.setAccrualsMap(gtfRpt.getAccrualsMap());
						Map<String, Double> calVarianceMap = new HashMap<String, Double>();
						for (int cnt = 0; cnt < BudgetConstants.months.length; cnt++) {
							calVarianceMap.put(BudgetConstants.months[cnt], plannedMap.get(BudgetConstants.months[cnt]) - gtfReport.getAccrualsMap().get(BudgetConstants.months[cnt]));
						}
						gtfReport.setVariancesMap(calVarianceMap);
					}else{
						gtfReport.setAccrualsMap(setZeroMap);
						gtfReport.setVariancesMap(plannedMap);
					}
				}
				gtfReport.setMultiBrand(isMultibrand);
				gtfReport.setRemarks("");

				// If PO number contains # or Blank map with uploadWithOutPos map
				if(Util.isNullOrEmpty(gtfReport.getPoNumber()) && !gtfReport.getPoNumber().equalsIgnoreCase("#") && !gtfReport.getPoNumber().equalsIgnoreCase("blank") && !gtfReport.getPoNumber().startsWith("1")){
					ArrayList<GtfReport> poUpdated = new ArrayList<GtfReport>();
					if (uploadedPOs.get(gtfReport.getPoNumber()) != null) {
						poUpdated = uploadedPOs.get(gtfReport.getPoNumber());
					}
					poUpdated.add(gtfReport);
					uploadedPOs.put(gtfReport.getPoNumber(), poUpdated);
				}else{
					ArrayList<GtfReport> noPoUpdated = new ArrayList<>();
					if(uploadWithOutPos.get(gtfReport.getProjectName())!=null){
						noPoUpdated = uploadWithOutPos.get(gtfReport.getProjectName());
						noPoUpdated.add(gtfReport);
						uploadWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);

					}else{
						noPoUpdated.add(gtfReport);
						uploadWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);
					}
				}
			}catch(Exception e){
				LOGGER.log(Level.WARNING, "Exception occured while uploading..." + recvdRow + e);
			}
		}

		changeForMultiBrand(uploadedPOs, gtfReports,costCenterWiseGtfRptMap);
		changeForMultiBrand(uploadWithOutPos, gtfReports,costCenterWiseGtfRptMap);
		
		if (gtfReports!=null && !gtfReports.isEmpty() && gtfReports.size() != 0) {
			util.generateProjectIdUsingJDOTxn(gtfReports,"",baseURL,costCentre);
		}
	}

	// Creates parent gtfReport
	private void changeForMultiBrand(Map<String, ArrayList<GtfReport>> uploadedPOs, List<GtfReport> gtfReports,Map<String, GtfReport> costCenterWiseGtfRptMap) {
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		Map<String, Double> benchMrkMap = null;
		Map<String, Double> plannedMap = null;
		Map<String, Double> accrualMap = null;
		Map<String, Double> varianceMap = null;
		
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
		}
		for (Entry<String, ArrayList<GtfReport>> entry : uploadedPOs.entrySet())
		{
		    ArrayList<GtfReport> receivedGtfReports = entry.getValue();
		    if(receivedGtfReports.size() > 1 || receivedGtfReports.get(0).getProject_WBS().trim().startsWith("421")) {
		    	GtfReport nwParentGtfReport = new GtfReport();
		    	ArrayList<String> childProjList = new ArrayList<String>();
		    	try {
					nwParentGtfReport = (GtfReport) receivedGtfReports.get(0).clone();
					if(costCenterWiseGtfRptMap!=null && costCenterWiseGtfRptMap.get(nwParentGtfReport.getgMemoryId())!=null){
						for(String cList : costCenterWiseGtfRptMap.get(nwParentGtfReport.getgMemoryId()).getChildProjectList()){
							if (!cList.contains(".")) {
								if(costCenterWiseGtfRptMap.get(cList)!=null ){
									nwParentGtfReport = costCenterWiseGtfRptMap.get(cList);
								}
								break;
							}
						}
					}
					nwParentGtfReport.setPlannedMap(setZeroMap);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
		    	benchMrkMap = new HashMap<String, Double>(setZeroMap);
		    	plannedMap = new HashMap<String, Double>(setZeroMap);
		    	accrualMap = new HashMap<String, Double>(setZeroMap);
		    	varianceMap = new HashMap<String, Double>(setZeroMap);
		    	
		    	String gMemoriId = nwParentGtfReport.getgMemoryId();
		    	int count = 1;
				double total = 0.0;
				if (gMemoriId.contains(".")) {
					gMemoriId = gMemoriId.split("\\.")[0];

				}
		    	childProjList.add(gMemoriId);
		    	for(GtfReport gtfRpt : receivedGtfReports){
		    		Map<String, Double> receivedChildBenchMrkMap = new HashMap<String, Double>(gtfRpt.getBenchmarkMap());
		    		for (Entry<String, Double> entryMap : receivedChildBenchMrkMap.entrySet()){
		    			benchMrkMap.put(entryMap.getKey(), benchMrkMap.get(entryMap.getKey()) + entryMap.getValue());
		    		}
		    		nwParentGtfReport.setBenchmarkMap(benchMrkMap);
		    		
		    		Map<String, Double> receivedChildPlannedMap = new HashMap<String, Double>(gtfRpt.getPlannedMap());
		    		for (Entry<String, Double> entryMap : receivedChildPlannedMap.entrySet()){
		    			plannedMap.put(entryMap.getKey(), plannedMap.get(entryMap.getKey()) + entryMap.getValue());
		    		}
		    		nwParentGtfReport.setPlannedMap(plannedMap);
		    		
		    		Map<String, Double> receivedChildAccrualMap = new HashMap<String, Double>(gtfRpt.getAccrualsMap());
		    		for (Entry<String, Double> entryMap : receivedChildAccrualMap.entrySet()){
		    			accrualMap.put(entryMap.getKey(), accrualMap.get(entryMap.getKey()) + entryMap.getValue());
		    		}
		    		nwParentGtfReport.setAccrualsMap(accrualMap);
		    		
		    		Map<String, Double> receivedChildVarianceMap = new HashMap<String, Double>(gtfRpt.getVariancesMap());
		    		for (Entry<String, Double> entryMap : receivedChildVarianceMap.entrySet()){
		    			varianceMap.put(entryMap.getKey(), varianceMap.get(entryMap.getKey()) + entryMap.getValue());
		    		}
		    		nwParentGtfReport.setVariancesMap(varianceMap);
		    		
		    		gtfRpt.setgMemoryId(gMemoriId +"."+ (count));
					childProjList.add(gMemoriId + "." + (count++));
		    	}
		    	for(GtfReport gtfRpt : receivedGtfReports){
		    		total += gtfRpt.getPlannedMap().get("TOTAL");
		    	}
		    	nwParentGtfReport.setChildProjectList(childProjList);
		    	nwParentGtfReport.setBrand("Smart WBS");
		    	nwParentGtfReport.setPercent_Allocation(100.0);
		    	nwParentGtfReport.setRemarks("   ");
		    	receivedGtfReports.add(nwParentGtfReport);
		    	for(GtfReport gtfRpt : receivedGtfReports){
		    		gtfRpt.setMultiBrand(true);
		    		gtfRpt.setChildProjectList(childProjList);
		    		try{
					if (gtfRpt.getgMemoryId().contains(".")) {
						gtfRpt.setPercent_Allocation(Util.roundDoubleValue((gtfRpt.getPlannedMap()
								.get("TOTAL") / total) * 100 , 2));
					}}catch(NumberFormatException nfe){
						gtfRpt.setPercent_Allocation(100.0);
					}catch(ArithmeticException ae){
						gtfRpt.setPercent_Allocation(100.0);
					}
		    		gtfReports.add(gtfRpt);
		    	}
		    }else{
		    	try {
					gtfReports.add((GtfReport) receivedGtfReports.get(0).clone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
		    }
		}
	}
	
	
}
