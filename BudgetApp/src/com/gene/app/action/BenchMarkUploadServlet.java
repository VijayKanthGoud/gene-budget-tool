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
public class BenchMarkUploadServlet extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(BenchMarkUploadServlet.class.getName());
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
		
		LOGGER.log(Level.INFO, "inside benchmark upload...");

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
		
		int uploadedType = Integer.parseInt(req.getParameter("uploadType"));
		LOGGER.log(Level.INFO, "Received uploadedType = "+ uploadedType);
		
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
		createGTFReports(user, user,rowList, gtfReports,costCentre,baseURL, uploadedType);
	}

	private void createGTFReports(UserRoleInfo user,UserRoleInfo orgUser,
		List<List<String>> rowList, List<GtfReport> gtfReports,String costCentre,String baseURL, int uploadedType) {
		
		Map<String,GtfReport> uniqueGtfRptMap = util.prepareUniqueGtfRptMap(costCentre);
		
		boolean isMultibrand = false;
		
		Map<String, GtfReport> costCenterWiseGtfRptMap = util.getAllReportDataFromCache(costCentre);
		
		for (List<String> recvdRow : rowList) {
			try{
				GtfReport gtfReport = new GtfReport();
				
				
				if (recvdRow.get(colPODesc) != null && !recvdRow.get(colPODesc).toString().trim().equals("")) {
					gtfReport.setPoDesc(recvdRow.get(colPODesc).toString().replace("\\", "\\\\")
							.replace("\"", "\\\"").replace("\'", "\\\'"));
				} else {
					gtfReport.setPoDesc("Not Available");
				}
				
				// Set project name from PO description by removing gmemori Id which is separated by '_'
				if (gtfReport.getPoDesc().indexOf("_") == 6) {
					gtfReport.setProjectName(gtfReport.getPoDesc().split("_")[1]);
				} else {
					gtfReport.setProjectName(gtfReport.getPoDesc());
				}
				
				
				// Create gtfParam to get previously existing project from unique Gtf report map
				String gtfParam = "";
				if(Util.isNullOrEmpty(gtfReport.getProjectName())){
					gtfParam = gtfReport.getProjectName();
				}
				
				
				GtfReport gtfRpt = uniqueGtfRptMap.get(gtfParam.toString());
				
				// Skip project if there is no Sub activity
				if (recvdRow.get(colSubActivity) != null	&& !recvdRow.get(colSubActivity).toString().trim().equals("")) {
					gtfReport.setSubActivity(recvdRow.get(colSubActivity).toString());
				} 
				
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

				if(gtfRpt==null){
					if (recvdRow.get(colPONum) == null || recvdRow.get(colPONum).toString().trim().equals("")) {
						gtfReport.setPoNumber("");
						gtfReport.setStatus("New");
						gtfReport.setFlag(1);
					} else{
						gtfReport.setPoNumber(recvdRow.get(colPONum).toString().trim());
						gtfReport.setStatus("Active");
						gtfReport.setFlag(2);
					}
				}else{
					gtfReport.setStatus(gtfRpt.getStatus());
					gtfReport.setFlag(gtfRpt.getFlag());
					if (recvdRow.get(colPONum) == null || recvdRow.get(colPONum).toString().trim().equals("")){
						if(!gtfRpt.getPoNumber().isEmpty()){
							gtfReport.setPoNumber(gtfRpt.getPoNumber());
							if(!"closed".equalsIgnoreCase(gtfReport.getStatus())){
								gtfReport.setStatus("Active");
								gtfReport.setFlag(2);
							}
						}else{
							gtfReport.setPoNumber("");
						}
					}else{
						gtfReport.setPoNumber(recvdRow.get(colPONum).toString().trim());
						if(!"closed".equalsIgnoreCase(gtfReport.getStatus())){
							gtfReport.setStatus("Active");
							gtfReport.setFlag(2);
						}
					}
					
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
				
				
				// Set brand from WBS name if no po number 
				if(Util.isNullOrEmpty(gtfReport.getPoNumber())){
					gtfReport.setBrand(gtfReport.getWBS_Name());
				}

				
				

				// Update if gtfReport already exists else create a new gtfReport
				if(gtfRpt != null){
					GtfReport existingParentGtfReport = costCenterWiseGtfRptMap.get(gtfRpt.getgMemoryId().split("\\.")[0]);
					boolean isSameBrand = false;
					if(!existingParentGtfReport.getMultiBrand()){
						isSameBrand = (existingParentGtfReport.getBrand().equalsIgnoreCase(gtfReport.getBrand()));
					}
					boolean brandExits = false;
					if(existingParentGtfReport!=null && existingParentGtfReport.getMultiBrand()){
						gtfReport.setChildProjectList(existingParentGtfReport.getChildProjectList());
						for(String childList : existingParentGtfReport.getChildProjectList() ){
							if(costCenterWiseGtfRptMap.get(childList) != null && gtfReport.getBrand().equalsIgnoreCase(costCenterWiseGtfRptMap.get(childList).getBrand())){
								GtfReport receivedRpt = costCenterWiseGtfRptMap.get(childList);
								gtfReport.setId(receivedRpt.getId());
								gtfReport.setgMemoryId(receivedRpt.getgMemoryId());
								gtfReport.setChildProjectList(receivedRpt.getChildProjectList());
								if("".equalsIgnoreCase(gtfReport.getPoNumber().trim())){
									gtfReport.setPoNumber(receivedRpt.getPoNumber());
									if(Util.isNullOrEmpty(gtfReport.getPoNumber()) && !"closed".equalsIgnoreCase(existingParentGtfReport.getStatus())){
										gtfReport.setStatus("Active");
										gtfReport.setFlag(2);
									}
								}
								gtfRpt= receivedRpt;
								brandExits=true;
								break;
							}
						}
						if(!brandExits){
							gtfReport.setId(null);
							String childGMemoriId = existingParentGtfReport.getgMemoryId() + "." + existingParentGtfReport.getChildProjectList().size() ;
							gtfReport.setgMemoryId(childGMemoriId);
							gtfReport.getChildProjectList().add(childGMemoriId);
							if("".equalsIgnoreCase(gtfReport.getPoNumber().trim())){
								gtfReport.setPoNumber(existingParentGtfReport.getPoNumber());
								if(Util.isNullOrEmpty(gtfReport.getPoNumber()) && !"closed".equalsIgnoreCase(existingParentGtfReport.getStatus())){
									gtfReport.setStatus("Active");
									gtfReport.setFlag(2);
								}
							}
							gtfRpt=null;
						}
					}else if (!isSameBrand && !existingParentGtfReport.getMultiBrand()){
						
						gtfReport.setId(null);
						String childGMemoriId = "";
						if(existingParentGtfReport.getChildProjectList()==null || existingParentGtfReport.getChildProjectList().isEmpty()){
							ArrayList<String> cList = new ArrayList<>();
							cList.add(existingParentGtfReport.getgMemoryId());
							childGMemoriId = existingParentGtfReport.getgMemoryId() + "." + cList.size() ;
							existingParentGtfReport.setgMemoryId(childGMemoriId);
							cList.add(childGMemoriId);
							existingParentGtfReport.setChildProjectList(cList);
							existingParentGtfReport.setMultiBrand(true);
							gtfReport.setChildProjectList(cList);
						}
						childGMemoriId = existingParentGtfReport.getgMemoryId().split("\\.")[0] + "." + existingParentGtfReport.getChildProjectList().size() ;
						gtfReport.setgMemoryId(childGMemoriId);
						gtfReport.getChildProjectList().add(childGMemoriId);
						gtfReport.setMultiBrand(true);
						if("".equalsIgnoreCase(gtfReport.getPoNumber().trim())){
							gtfReport.setPoNumber(existingParentGtfReport.getPoNumber());
							if(Util.isNullOrEmpty(gtfReport.getPoNumber()) && !"closed".equalsIgnoreCase(existingParentGtfReport.getStatus())){
								gtfReport.setStatus("Active");
								gtfReport.setFlag(2);
							}
						}
						costCenterWiseGtfRptMap.put(existingParentGtfReport.getgMemoryId(), existingParentGtfReport);
						gtfRpt=null;
					}else{
						gtfReport.setId(gtfRpt.getId());
						/*costCenterWiseGtfRptMap.remove(gtfRpt.getgMemoryId());
						CostCenterCacheUtil.putCostCenterDataToCache(costCentre, costCenterWiseGtfRptMap);*/
						String gMemoriId="";
						try {
							if(gtfReport.getPoDesc().indexOf("_")==6){
								gMemoriId = Integer.parseInt(gtfReport.getPoDesc().substring(0,	Math.min(gtfReport.getPoDesc().length(), 6))) + "";
								gtfReport.setDummyGMemoriId(false);
							}
						}
						catch (NumberFormatException ne) {
								
							}
						if(!"".equalsIgnoreCase(gMemoriId) ){
							gtfReport.setgMemoryId(gMemoriId);
						}else{
							gtfReport.setgMemoryId(gtfRpt.getgMemoryId());
						}
							
							if(gtfReport.getPoNumber()==null || "".equalsIgnoreCase(gtfReport.getPoNumber().trim())) {
								gtfReport.setPoNumber(gtfRpt.getPoNumber());
								if(Util.isNullOrEmpty(gtfReport.getPoNumber()) && !"closed".equalsIgnoreCase(gtfRpt.getStatus())){
									gtfReport.setStatus("Active");
									gtfReport.setFlag(2);
								}
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
					if (recvdRow.get(colPONum) != null && !recvdRow.get(colPONum).toString().trim().equals("")) {
						gtfReport.setPoNumber(recvdRow.get(colPONum).toString().trim());
						gtfReport.setStatus("Active");
						gtfReport.setFlag(2);
					} 
				}

				Map<String, Double> benchmarkMap = new HashMap<String, Double>();
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
							benchmarkMap.put(BudgetConstants.months[cnt], Util.getDoubleValue(value, 8));
						} else {
							benchmarkMap.put(BudgetConstants.months[cnt], 0.0);
						}
					} catch (Exception e1) {
						LOGGER.log(Level.WARNING, "Error" + e1);
						benchmarkMap.put(BudgetConstants.months[cnt], 0.0);
					}
				}
				
				if(gtfRpt ==null){
					gtfReport.setPlannedMap(benchmarkMap);
					gtfReport.setBenchmarkMap(benchmarkMap);
					gtfReport.setAccrualsMap(benchmarkMap);
					gtfReport.setVariancesMap(setZeroMap);
				}else{
					if( benchmarkMap != null){
						if(uploadedType == 2){
							gtfReport.setPlannedMap(benchmarkMap);
							gtfReport.setAccrualsMap(benchmarkMap);
						}else{
							gtfReport.setPlannedMap(gtfRpt.getPlannedMap());
							gtfReport.setAccrualsMap(gtfRpt.getAccrualsMap());
						}
						gtfReport.setBenchmarkMap(benchmarkMap);
						Map<String, Double> calVarianceMap = new HashMap<String, Double>();
						for (int cnt = 0; cnt < BudgetConstants.months.length; cnt++) {
							calVarianceMap.put(BudgetConstants.months[cnt], benchmarkMap.get(BudgetConstants.months[cnt]) - gtfReport.getAccrualsMap().get(BudgetConstants.months[cnt]));
						}
						gtfReport.setVariancesMap(calVarianceMap);
					}
				}
				gtfReport.setMultiBrand(isMultibrand);
				gtfReport.setRemarks("");

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

		changeForMultiBrand(uploadedPOs, gtfReports,costCenterWiseGtfRptMap,true);
		changeForMultiBrand(uploadWithOutPos, gtfReports,costCenterWiseGtfRptMap,false);
		
		if (gtfReports!=null && !gtfReports.isEmpty() && gtfReports.size() != 0) {
			util.generateProjectIdUsingJDOTxn(gtfReports,"",baseURL,costCentre);
		}
	}

	// Creates parent gtfReport
	private void changeForMultiBrand(Map<String, ArrayList<GtfReport>> uploadedPOs, List<GtfReport> gtfReports,Map<String, GtfReport> costCenterWiseGtfRptMap,boolean isFrmPo ) {
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		Map<String, Double> benchMrkMap = null;
		Map<String, Double> plannedMap = null;
		Map<String, Double> accrualMap = null;
		Map<String, Double> varianceMap = null;
		try{
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
		}
		Map<String, GtfReport> existingProjects = new HashMap<>();
		
		if(isFrmPo){
			existingProjects = util.preparePOMap(costCenterWiseGtfRptMap);
		}else{
			existingProjects = util.prepareProjectNameMap(costCenterWiseGtfRptMap);
		}
		
		
		for (Entry<String, ArrayList<GtfReport>> entry : uploadedPOs.entrySet()){
			ArrayList<GtfReport> receivedGtfReports = entry.getValue();
			List<String> receivedReportIds = new ArrayList<String>();
			for(GtfReport report : receivedGtfReports){
				receivedReportIds.add(report.getgMemoryId());
			}
			String searchString ="";
			if(isFrmPo){
				searchString = receivedGtfReports.get(0).getPoNumber();
			}else{
				searchString = receivedGtfReports.get(0).getProjectName();
			}
			if(existingProjects.get(searchString) != null && existingProjects.get(searchString).getMultiBrand()){
				if(receivedGtfReports.get(0).getChildProjectList() != null ){
				List<String> childList = receivedGtfReports.get(0).getChildProjectList();
				for(String child : childList){
					if(!receivedReportIds.contains(child) && child.contains(".")){
						receivedGtfReports.add(costCenterWiseGtfRptMap.get(child));
					}
				}
			}
			}
		}
		
		for (Entry<String, ArrayList<GtfReport>> entry : uploadedPOs.entrySet())
		{
		    ArrayList<GtfReport> receivedGtfReports = entry.getValue();
		   
		    
		    if(receivedGtfReports.size() > 1 ||  receivedGtfReports.get(0).getProject_WBS().trim().startsWith("421")) {
		    	
		    	GtfReport nwParentGtfReport = new GtfReport();
		    	ArrayList<String> childProjList = new ArrayList<String>();
		    	
		    	try {
					if (receivedGtfReports.get(0).getgMemoryId() != null
							&& receivedGtfReports.get(0).getgMemoryId()
									.split("\\.") != null) {
						String parentGmemId = receivedGtfReports.get(0).getgMemoryId().split("\\.")[0];
						/*boolean parentExist = false;
						for (GtfReport pGtfRpt : receivedGtfReports) {
							if (pGtfRpt.getgMemoryId().equalsIgnoreCase(parentGmemId)) {
								nwParentGtfReport = pGtfRpt;
								parentExist = true;
								break;
							}
						}*/
						if(costCenterWiseGtfRptMap.get(parentGmemId) != null && "smart wbs".equalsIgnoreCase(costCenterWiseGtfRptMap.get(parentGmemId).getBrand())){
							nwParentGtfReport = costCenterWiseGtfRptMap.get(parentGmemId);
						}else {
							nwParentGtfReport = (GtfReport) receivedGtfReports.get(0).clone();
							nwParentGtfReport.setId(null);
							
						}
					}else{
		    			nwParentGtfReport = (GtfReport) receivedGtfReports.get(0).clone();
		    			nwParentGtfReport.setId(null);
		    		}
					/*if(costCenterWiseGtfRptMap!=null && costCenterWiseGtfRptMap.get(nwParentGtfReport.getgMemoryId())!=null){
						for(String cList : costCenterWiseGtfRptMap.get(nwParentGtfReport.getgMemoryId()).getChildProjectList()){
							if (!cList.contains(".")) {
								if(costCenterWiseGtfRptMap.get(cList)!=null ){
									nwParentGtfReport = costCenterWiseGtfRptMap.get(cList);
								}
							}
						}
					}*/
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
		    	nwParentGtfReport.setgMemoryId(gMemoriId);
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
		    		if(gtfRpt.getPlannedMap().get("TOTAL") == null || gtfRpt.getPlannedMap().get("TOTAL") == 0.0){
						for (int cnt = 0; cnt < BudgetConstants.months.length; cnt++) {
							total += gtfRpt.getPlannedMap().get(BudgetConstants.months[cnt]);
						}
						
					}
		    	}
		    	nwParentGtfReport.setChildProjectList(childProjList);
		    	nwParentGtfReport.setBrand("Smart WBS");
		    	nwParentGtfReport.setPercent_Allocation(100.0);
		    	nwParentGtfReport.setRemarks("   ");
		    	receivedGtfReports.add(nwParentGtfReport);
		    	double childTotal = 0.0;
		    	double percentTotal = 0.0;
		    	double childPercentTotal = 0.0;
		    	int childSize = receivedGtfReports.size() - 1;
		    	int currChildIndex;
		    	for(GtfReport gtfRpt : receivedGtfReports){
		    		gtfRpt.setMultiBrand(true);
		    		gtfRpt.setChildProjectList(childProjList);
		    		childTotal = 0.0;
		    		childPercentTotal = 0.0;
		    		currChildIndex = 0;
		    		try{
					if (gtfRpt.getgMemoryId().contains(".")) {
						if(gtfRpt.getPlannedMap().get("TOTAL") == null || gtfRpt.getPlannedMap().get("TOTAL") == 0.0){
							for (int cnt = 0; cnt < BudgetConstants.months.length; cnt++) {
								childTotal += gtfRpt.getPlannedMap().get(BudgetConstants.months[cnt]);
							}
							
						}else{
							childTotal = gtfRpt.getPlannedMap().get("TOTAL");
						}
						childPercentTotal = Util.getDoubleValue((childTotal / total * 100) + "", 8);
						gtfRpt.setPercent_Allocation(childPercentTotal);
						if(currChildIndex == childSize-1 ){
							 childPercentTotal = Util.getDoubleValue(100 - percentTotal + "" , 8);
						}else{
							percentTotal += childPercentTotal;
						}
						currChildIndex++;
						
					}
					}catch(NumberFormatException nfe){
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
		}catch(Exception e){
			LOGGER.log(Level.WARNING, "Exception occured while uploading..." + e.toString());
		}
	}
	
	
}
