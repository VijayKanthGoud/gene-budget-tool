package com.gene.app.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
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
	@PUT
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
		GtfReport gtfReport = null;
		String costCenter = prjParam.getCostCentre();
		String pUnixId = prjParam.getpUnixId();
		UserRoleInfo userInfo = util.readUserRoleInfoByName(pUnixId);
		System.out.println("userInfo"+userInfo.getUserName());
		System.out.println("userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
		System.out.println("costCenter = "+costCenter);
		if(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())){
			System.out.println("If loop userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
			eObj.setStatusCode(401);
			eObj.setStatusMessage("Authentication Failed !!!");
			return eObj;
		}else{
			if(!Util.isNullOrEmpty(costCenter) && !userInfo.getCostCenter().contains(costCenter)){
				System.out.println("else If 402 loop userInfo null or empty"+((!Util.isNullOrEmpty(costCenter) && !userInfo.getCostCenter().contains(costCenter))));
				eObj.setStatusCode(402);
				eObj.setStatusMessage("User is not mapped to costCentre: " +costCenter+" !!!");
				return eObj;
			}
		}
		Map<String,GtfReport> gtfRptMap = util.getAllReportDataFromCache(costCenter);
		String gMemoriId = prjParam.getgMemoriId();
		System.out.println("gMemoriId"+gMemoriId);
		System.out.println("gtfRptMap.get(gMemoriId)"+gtfRptMap.get(gMemoriId));
		if(gtfRptMap.get(gMemoriId)==null || (gMemoriId.length()!=6)){
			eObj.setStatusCode(405);
			eObj.setStatusMessage("gMemori Id : " +gMemoriId+" doesn't exist in CostCenter: "+costCenter+" of Budgeting Tool !!!");
			return eObj;
		}
		System.out.println("gtfRptMap"+gtfRptMap);
		for(Map.Entry<String, GtfReport> gtfEntry: gtfRptMap.entrySet()){
			if(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().length()<10){
				gtfReport = gtfEntry.getValue();
				System.out.println("prjParam.getProjectOwner() : "+prjParam.getpUnixId() +"::::" +gtfReport.getRequestor());
				if((gtfReport.getRequestor().contains(":") && !prjParam.getpUnixId().equalsIgnoreCase(gtfReport.getRequestor().split(":")[0]) ) ||
						(!gtfReport.getRequestor().contains(":") && !prjParam.getpUnixId().equalsIgnoreCase(gtfReport.getRequestor()))){
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
				gtfReport.setRequestor(prjParam.getpUnixId());
				gtfReport.setCostCenter(costCenter);
				gtfReport.setProjectName(prjParam.getProjectName());
				gtfReports.add(gtfReport);
			}
		}
		System.out.println("status Code"+eObj.getStatusCode());
		System.out.println("status Message"+eObj.getStatusMessage());
		util.generateProjectIdUsingJDOTxn(gtfReports);
		util.storeProjectsToCache(gtfReports, costCenter, BudgetConstants.NEW);
		eObj.setStatusCode(200);
		eObj.setStatusMessage("Successful !!!");
		return eObj;
	}

}
