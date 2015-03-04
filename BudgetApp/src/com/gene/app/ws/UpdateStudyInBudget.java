package com.gene.app.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.ProjectParameters;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.gene.app.ws.exception.ErrorObject;
import com.google.gson.Gson;

@Path("/updateProjectDetails")
public class UpdateStudyInBudget {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateProjectDetails(String gmem,@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse)
			throws IOException {
		ErrorObject eObj = new ErrorObject();
		Gson gson = new Gson();
		ProjectParameters gMemori = gson.fromJson(gmem, ProjectParameters.class);
		eObj= storeProjectData(gMemori, eObj);
		String request = gson.toJson(eObj);
		System.out.println("request :::::"+request);
		return request;
	}

	public ErrorObject storeProjectData(ProjectParameters prjParam, ErrorObject eObj){
		DBUtil util = new DBUtil();
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		List<String> selectedCC = new ArrayList<>();
		GtfReport gtfReport = null;
		List<String> costCenterList = prjParam.getCostCentres();
		String pUnixId = prjParam.getProjectOwner();
		UserRoleInfo userInfo = util.readUserRoleInfoByName(pUnixId);
		System.out.println("userInfo"+userInfo.getUserName());
		System.out.println("userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
		if(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())){
			System.out.println("If loop userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
			eObj.setStatusCode(401);
			eObj.setStatusMessage("Authentication Failed !!!");
			return eObj;
		}else{
			//List<String> ccBrandList = util.readAllCostCenters();
			
			for(String cCenter:costCenterList){
				if(Util.isNullOrEmpty(cCenter) && userInfo.getCostCenter().contains(cCenter)){
					selectedCC.add(cCenter);
				}
			}
			
			if(selectedCC.isEmpty()){
				//System.out.println("else If 402 loop userInfo null or empty"+((!Util.isNullOrEmpty(selectedCC) && !userInfo.getCostCenter().contains(costCenter))));
				eObj.setStatusCode(402);
				eObj.setStatusMessage("User is not mapped to costCentres: " +costCenterList+" !!!");
				return eObj;
			}
		}
		Map<String,GtfReport> gtfRptMap = new HashMap<String,GtfReport>();
		String gMemoriId = "";
		String selectedCostCenter = "";
		boolean isGMemIdExists = false; 
		for(String cc: selectedCC){
			gtfRptMap = util.getAllReportDataFromCache(cc);
			gMemoriId = prjParam.getgMemoriId();
			if(gtfRptMap.get(gMemoriId)!=null && (gMemoriId.length()==6)){
				isGMemIdExists = true;
				selectedCostCenter = cc;
			}
		}
		//Map<String,GtfReport> gtfRptMap = util.getAllReportDataFromCache(costCenter);
		//String gMemoriId = prjParam.getgMemoriId();
		System.out.println("gMemoriId"+gMemoriId);
		System.out.println("gtfRptMap.get(gMemoriId)"+gtfRptMap.get(gMemoriId));
		if(!isGMemIdExists){
			eObj.setStatusCode(405);
			eObj.setStatusMessage("gMemori Id : " +gMemoriId+" doesn't exist in CostCenter associated to user: "+pUnixId+" of Budgeting Tool !!!");
			return eObj;
		}
		System.out.println("gtfRptMap"+gtfRptMap);
		for(Map.Entry<String, GtfReport> gtfEntry: gtfRptMap.entrySet()){
			if(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().length()<10){
				gtfReport = gtfEntry.getValue();
				System.out.println("prjParam.getProjectOwner() : "+prjParam.getProjectOwner() +"::::" +gtfReport.getRequestor());
				if((gtfReport.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfReport.getRequestor().split(":")[0]) ) ||
						(!gtfReport.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfReport.getRequestor()))){
					eObj.setStatusCode(403);
					eObj.setStatusMessage("User is not authorised to edit the project !!!");
					return eObj;
				}
				switch(prjParam.getpStatus()){
				case "New":
					gtfReport.setFlag(1);
					break;
				case "Active":
					gtfReport.setFlag(2);
					break;
				case "Closed":
					gtfReport.setFlag(3);
					break;
				default:
					break;
				}
				gtfReport.setStatus(prjParam.getpStatus());
				gtfReport.setRequestor(prjParam.getProjectOwner());
				gtfReport.setCostCenter(selectedCostCenter);
				gtfReport.setProjectName(prjParam.getProjectName());
				gtfReports.add(gtfReport);
			}
		}
		System.out.println("status Code"+eObj.getStatusCode());
		System.out.println("status Message"+eObj.getStatusMessage());
		util.generateProjectIdUsingJDOTxn(gtfReports);
		util.storeProjectsToCache(gtfReports, selectedCostCenter, BudgetConstants.NEW);
		eObj.setStatusCode(200);
		eObj.setStatusMessage("Successful !!!");
		return eObj;
	}

}
