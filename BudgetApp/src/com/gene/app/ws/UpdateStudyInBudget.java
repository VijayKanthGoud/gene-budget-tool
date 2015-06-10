package com.gene.app.ws;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.GtfReport;
import com.gene.app.model.ProjectParameters;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.gene.app.ws.exception.ErrorObject;
import com.google.gson.Gson;

@Path("/updateProjectDetails")
public class UpdateStudyInBudget {
	private final static Logger LOGGER = Logger
			.getLogger(UpdateStudyInBudget.class.getName());
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateProjectDetails(String gmem,@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse)
			throws IOException {
		ErrorObject eObj = new ErrorObject();
		Gson gson = new Gson();
		System.out.println("start time :"+System.currentTimeMillis());
		ProjectParameters gMemori = gson.fromJson(gmem, ProjectParameters.class);
		eObj= storeProjectData(gMemori, eObj);
		String request = gson.toJson(eObj);
		System.out.println("end time :"+System.currentTimeMillis());
		LOGGER.log(Level.INFO, "request :::::"+request);
		return request;
	}

	public ErrorObject storeProjectData(ProjectParameters prjParam, ErrorObject eObj){
		//Date date = new Date();
		long starttime = System.currentTimeMillis();
		LOGGER.log(Level.INFO, "Start Time: "+starttime);
		DBUtil util = new DBUtil();
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		List<String> selectedCC = new ArrayList<>();
		GtfReport gtfReport = null;
		List<String> costCenterList = prjParam.getCostCentres();
		String pUnixId = prjParam.getProjectOwner();
		
		long userStartTime = System.currentTimeMillis();
		UserRoleInfo userInfo = util.readUserRoleInfoByName(pUnixId);
		long userEndTime = System.currentTimeMillis();
		LOGGER.log(Level.INFO, "util.readUserRoleInfoByName(pUnixId) execution time"+(userEndTime-userStartTime));
		LOGGER.log(Level.INFO, "userInfo"+userInfo.getUserName());
		LOGGER.log(Level.INFO, "costCenterList"+costCenterList);
		LOGGER.log(Level.INFO, "userInfo.getCostCenter() :::"+userInfo.getCostCenter());
		LOGGER.log(Level.INFO, "userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
		if(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())){
			LOGGER.log(Level.INFO, "If loop userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
			eObj.setStatusCode(401);
			eObj.setStatusMessage("Authentication Failed !!!");
			LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
			LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
			return eObj;
		}else{
			for(String cCenter:costCenterList){
				if(Util.isNullOrEmpty(cCenter) && userInfo.getCostCenter().contains(cCenter)){
					selectedCC.add(cCenter);
				}
			}
			
			if(selectedCC.isEmpty()){
				eObj.setStatusCode(402);
				eObj.setStatusMessage("User is not mapped to costCentres: " +costCenterList+" !!!");
				LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
				LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
				return eObj;
			}
		}
		GtfReport gtfRpt = new GtfReport();
		List<GtfReport>	gtfRptlst = new ArrayList<GtfReport>();
		if(prjParam.getgMemoriId()!=null && !"".equals(prjParam.getgMemoriId())){
			long GMemIdStartTime = System.currentTimeMillis();
			gtfRptlst = util.readProjectDataByGMemId(prjParam.getgMemoriId());
			long GMemIdEndTime = System.currentTimeMillis();
			LOGGER.log(Level.INFO, "util.readProjectDataByGMemId(prjParam.getgMemoriId()) execution time"+(GMemIdEndTime-GMemIdStartTime));
			if(gtfRptlst!=null && !gtfRptlst.isEmpty()){
				gtfRpt = gtfRptlst.get(0);
			}
		}if( gtfRpt!=null && Util.isNullOrEmpty(gtfRpt.getgMemoryId()) && !selectedCC.contains(gtfRpt.getCostCenter())){
			eObj.setStatusCode(404);
			eObj.setStatusMessage("Project doesn't exist in costCenter " +costCenterList+" !!!");
			LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
			LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
			return eObj;
		}
		System.out.println("selectedCC::::"+selectedCC);
		Map<String,GtfReport> gtfRptMap = new HashMap<String,GtfReport>();
		String gMemoriId = "";
		String selectedCostCenter = "";
		boolean isGMemIdExists = false; 
		for(String cc: selectedCC){
			long GMemIdStartTime = System.currentTimeMillis();
			gtfRptMap = util.getAllReportDataFromCache(cc);
			gMemoriId = prjParam.getgMemoriId();
			LOGGER.log(Level.INFO, "selectedCC::::"+gtfRptMap);
			LOGGER.log(Level.INFO, "gMemoriId::::"+gMemoriId);
			LOGGER.log(Level.INFO, "gMemoriId.length()::::"+gMemoriId.length());
			if(gtfRptMap.get(gMemoriId)!=null && (gMemoriId.length()==6)){
				isGMemIdExists = true;
				selectedCostCenter = cc;
				
			}
		}
		//Map<String,GtfReport> gtfRptMap = util.getAllReportDataFromCache(costCenter);
		//String gMemoriId = prjParam.getgMemoriId();
		LOGGER.log(Level.INFO, "gMemoriId"+gMemoriId);
		LOGGER.log(Level.INFO, "gtfRptMap.get(gMemoriId)"+gtfRptMap.get(gMemoriId));
		if(!isGMemIdExists){
			createProjectInBudget(prjParam,costCenterList.get(0));
			eObj.setStatusCode(200);
			eObj.setStatusMessage("gMemori Id : " +gMemoriId+" has been created in Budgeting Tool !!!");
			LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
			LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
			return eObj;
		}else{
			LOGGER.log(Level.INFO, "gtfRptMap"+gtfRptMap);
		for(Map.Entry<String, GtfReport> gtfEntry: gtfRptMap.entrySet()){
			if(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().length()<10){
				gtfReport = gtfEntry.getValue();
				LOGGER.log(Level.INFO, "prjParam.getProjectOwner() : "+prjParam.getProjectOwner() +"::::" +gtfReport.getRequestor());
				if((gtfReport.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfReport.getRequestor().split(":")[1]) ) ||
						(!gtfReport.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfReport.getRequestor()))){
					eObj.setStatusCode(403);
					eObj.setStatusMessage("User is not authorised to edit the project !!!");
					LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
					LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
					return eObj;
				}
				int flag = 0;
				String status = "";
				
				if("Closed".equalsIgnoreCase(prjParam.getpStatus().toLowerCase())){
					if ((!Util.isNullOrEmpty(gtfReport.getPoNumber()))) {
						eObj.setStatusCode(405);
						eObj.setStatusMessage("Project cannot be closed in gMemori Budget as PO number is blank !!!");
						LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
						LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
						return eObj;
					} else{
					flag = 3;
					status = "Closed";
					}
				} else {
					if ((!Util.isNullOrEmpty(gtfReport.getPoNumber()))) {
						LOGGER.log(Level.INFO, "PO Number is blank. Hence status is set to New !!!");
						flag = 1;
						status = BudgetConstants.status_New;
					} else {
						flag = 2;
						status = BudgetConstants.status_Active;
					} 
				}
				gtfReport.setStatus(status);
				gtfReport.setFlag(flag);
				//gtfReport.setRequestor(prjParam.getProjectOwner());
				gtfReport.setCostCenter(selectedCostCenter);
				gtfReport.setProjectName(prjParam.getProjectName().replace("\\", "\\\\")
						.replace("\"", "\\\"").replace("\'", "\\\'"));
				gtfReports.add(gtfReport);
			}
		}
		}
		LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
		LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
		util.generateProjectIdUsingJDOTxn(gtfReports);
		util.storeProjectsToCache(gtfReports, selectedCostCenter, BudgetConstants.NEW);
		eObj.setStatusCode(200);
		eObj.setStatusMessage("Successful !!!");
		//date = new Date();
		//long t2 = date.getTime();
		//LOGGER.log(Level.INFO, "Time taken : "+(t2-t1));
		return eObj;
	}

	public void createProjectInBudget(ProjectParameters prjParam,String costCenter){
		DBUtil util = new DBUtil();
		int flag = 0;
		String status = "";
		String remarks = "";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
		.format(Calendar.getInstance().getTime());
		GtfReport gtfRpt = new GtfReport();
		gtfRpt.setgMemoryId(prjParam.getgMemoriId());
		gtfRpt.setDummyGMemoriId((prjParam.getgMemoriId().length()==6)?false:true);
		gtfRpt.setFlag(1);
		gtfRpt.setStatus(BudgetConstants.status_New);
		gtfRpt.setRequestor(prjParam.getProjectOwner());
		gtfRpt.setCostCenter(costCenter);
		gtfRpt.setProjectName(prjParam.getProjectName().replace("\\", "\\\\")
				.replace("\"", "\\\"").replace("\'", "\\\'"));
		Map<String,Double> userBrandMap= new LinkedHashMap<String,Double>();
		Object[] myBrands = {};
		CostCenter_Brand ccBrandMap = new CostCenter_Brand();
		 List<CostCenter_Brand> costCenterList =util.readCostCenterBrandMappingData();
		for(CostCenter_Brand ccBrand : costCenterList){
			if(costCenter.equalsIgnoreCase(ccBrand.getCostCenter().trim())){
				ccBrandMap = ccBrand;
				break;
			}
		}
		userBrandMap = util.getBrandMap(ccBrandMap.getBrandFromDB());
		Map<String,Double> sortedMap = new TreeMap<String,Double>(userBrandMap);
		myBrands = sortedMap.keySet().toArray();
		gtfRpt.setBrand(myBrands[0].toString());
		gtfRpt.setCreateDate(timeStamp);
		gtfRpt.setYear(BudgetConstants.dataYEAR);
		gtfRpt.setQual_Quant("Qual_Quant");
		gtfRpt.setStudy_Side("study_Side");
		gtfRpt.setPercent_Allocation(BudgetConstants.GTF_Percent_Total);
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
		}
		gtfRpt.setPlannedMap(setZeroMap);
		gtfRpt.setBenchmarkMap(setZeroMap);
		gtfRpt.setAccrualsMap(setZeroMap);
		gtfRpt.setVariancesMap(setZeroMap);
		gtfRpt.setMultiBrand(false);
		gtfRpt.setRemarks("Project is created from Study.. Brand and Status defaulted");
		gtfRpt.setEmail(prjParam.getProjectOwner()+"@gene.com");
		//gtfRpt.setFlag(flag);
		//gtfRpt.setStatus(prjParam.getpStatus());
		gtfRpt.setRequestor(prjParam.getProjectOwner());
		gtfRpt.setProject_WBS("");
		gtfRpt.setSubActivity("");
		gtfRpt.setPoNumber("");
		gtfRpt.setPoDesc("");
		gtfRpt.setVendor("");
		gtfRpt.setUnits(0);
		List<GtfReport> gtfRptList = new ArrayList<GtfReport>();
		gtfRptList.add(gtfRpt);
		util.saveDataToDataStore(gtfRpt);
		util.storeProjectsToCache(gtfRptList, costCenter, BudgetConstants.NEW);
	}
}
