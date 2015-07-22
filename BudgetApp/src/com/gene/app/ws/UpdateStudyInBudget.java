package com.gene.app.ws;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
		final String url = httpRequest.getRequestURL().toString();
		final String baseURL = url.substring(0, url.length()
				- httpRequest.getRequestURI().length())
				+ httpRequest.getContextPath() + "/";
		Gson gson = new Gson();
		LOGGER.log(Level.INFO, gmem);
		ProjectParameters gMemori = gson.fromJson(gmem, ProjectParameters.class);
		eObj= storeProjectData(gMemori, eObj,baseURL);
		String request = gson.toJson(eObj);
		return request;
	}

	public ErrorObject storeProjectData(ProjectParameters prjParam, ErrorObject eObj,String baseURL){
		//Date date = new Date();
		DBUtil util = new DBUtil();
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		List<String> selectedCC = new ArrayList<>();

		List<String> costCenterList = prjParam.getCostCentres();
		String pUnixId = prjParam.getProjectOwner();

		UserRoleInfo userInfo = util.readUserRoleInfoByName(pUnixId);
		boolean isGMemIdExists = false; 
		if(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())){
			eObj.setStatusCode(401);
			eObj.setStatusMessage("Authentication Failed !!!");
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
				return eObj;
			}
		}
		GtfReport gtfRpt = new GtfReport();
		List<GtfReport>	gtfRptlst = new ArrayList<GtfReport>();
		String selectedCostCenter = "";
		String gMemoriId = prjParam.getgMemoriId();
		if(prjParam.getgMemoriId()!=null && !"".equals(prjParam.getgMemoriId())){
			gtfRptlst = util.readProjectDataByGMemId(prjParam.getgMemoriId());
			//LOGGER.log(Level.INFO, gtfRptlst + "selectedCC = "+selectedCC);
			if(gtfRptlst!=null && !gtfRptlst.isEmpty()){
				gtfRpt = gtfRptlst.get(0);
				isGMemIdExists = true;
				selectedCostCenter = gtfRpt.getCostCenter();
			}if( gtfRpt!=null && Util.isNullOrEmpty(gtfRpt.getgMemoryId()) && !selectedCC.contains(gtfRpt.getCostCenter())){
				eObj.setStatusCode(404);
				eObj.setStatusMessage("Project doesn't exist in costCenter " +costCenterList+" !!!");
				LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
				LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
				return eObj;
			}
			if(!isGMemIdExists){
				selectedCostCenter = costCenterList.get(0);
				createProjectInBudget(prjParam,costCenterList.get(0),baseURL);
				eObj.setStatusCode(200);
				eObj.setStatusMessage("gMemori Id : " +gMemoriId+" has been created in Budgeting Tool !!!");
				LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
				LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
				return eObj;
			}else if("Closed".equalsIgnoreCase(prjParam.getpStatus().toLowerCase())){
				for( GtfReport gtfEntry: gtfRptlst){
					if(Util.isNullOrEmpty(gtfEntry.getgMemoryId()) && gtfEntry.getgMemoryId().length()<10){
						if((gtfEntry.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfEntry.getRequestor().split(":")[1]) ) ||
								(!gtfEntry.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfEntry.getRequestor()))){
							eObj.setStatusCode(403);
							eObj.setStatusMessage("User is not authorised to edit the project !!!");
							LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
							LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());
							return eObj;
						}
						int flag = 0;
						String status = "";

						if("Closed".equalsIgnoreCase(prjParam.getpStatus().toLowerCase())){
							if ((!Util.isNullOrEmpty(gtfEntry.getPoNumber()))) {
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
							if ((!Util.isNullOrEmpty(gtfEntry.getPoNumber()))) {
								LOGGER.log(Level.INFO, "PO Number is blank. Hence status is set to New !!!");
								flag = 1;
								status = BudgetConstants.status_New;
							} else {
								flag = 2;
								status = BudgetConstants.status_Active;
							} 
						}
						gtfEntry.setStatus(status);
						gtfEntry.setFlag(flag);
						gtfEntry.setCostCenter(selectedCostCenter);
						gtfEntry.setProjectName(prjParam.getProjectName().replace("\\", "\\\\")
								.replace("\"", "\\\"").replace("\'", "\\\'"));
						gtfReports.add(gtfEntry);
					}
				}
				if(gtfReports!=null && !gtfReports.isEmpty()){
					util.generateProjectIdUsingJDOTxn(gtfReports,"",baseURL,selectedCostCenter);
				}
			}
		}
		LOGGER.log(Level.INFO, "status Code"+eObj.getStatusCode());
		LOGGER.log(Level.INFO, "status Message"+eObj.getStatusMessage());

		eObj.setStatusCode(200);
		eObj.setStatusMessage("Successful !!!");
		return eObj;
	}

	public void createProjectInBudget(ProjectParameters prjParam,String costCenter,String baseURL){
		DBUtil util = new DBUtil();
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
		gtfRpt.setRequestor(prjParam.getProjectOwner());
		gtfRpt.setProject_WBS("");
		gtfRpt.setSubActivity("");
		gtfRpt.setPoNumber("");
		gtfRpt.setPoDesc("");
		gtfRpt.setVendor("");
		gtfRpt.setUnits(0);
		List<GtfReport> gtfRptList = new ArrayList<GtfReport>();
		gtfRptList.add(gtfRpt);
		util.generateProjectIdUsingJDOTxn(gtfRptList,"",baseURL,costCenter);
	}
}
