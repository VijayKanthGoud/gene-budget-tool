package com.gene.app.ws;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

@Path("/disableProject")
public class DeleteStudyPrjFromBudget {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateProjectDetails(String gmem,@Context HttpServletRequest httpRequest, @Context HttpServletResponse httpResponse)
			throws IOException {
		ErrorObject eObj = new ErrorObject();
		Gson gson = new Gson();
		System.out.println("gmem = "+gmem);
		ProjectParameters gMemori = gson.fromJson(gmem, ProjectParameters.class);
		//eObj= disableProjectData(gMemori, eObj);
		eObj.setStatusCode(200);
		eObj.setStatusMessage("Successful !!!");
		String response = gson.toJson(eObj);
		System.out.println("Response :::::"+response);
		return response;
	}
	public ErrorObject disableProjectData(ProjectParameters prjParam, ErrorObject eObj){
		DBUtil util = new DBUtil();
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		List<String> selectedCC = new ArrayList<>();
		GtfReport gtfReport = null;
		List<String> costCenterList = prjParam.getCostCentres();
		String pUnixId = prjParam.getProjectOwner();
		UserRoleInfo userInfo = util.readUserRoleInfoByName(pUnixId);
		System.out.println("userInfo"+userInfo.getUserName());
		System.out.println("costCenterList"+costCenterList);
		System.out.println("userInfo.getCostCenter() :::"+userInfo.getCostCenter());
		System.out.println("userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
		if(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())){
			System.out.println("If loop userInfo null or empty"+(userInfo == null || !Util.isNullOrEmpty(userInfo.getEmail())));
			eObj.setStatusCode(401);
			eObj.setStatusMessage("Authentication Failed !!!");
			System.out.println("status Code"+eObj.getStatusCode());
			System.out.println("status Message"+eObj.getStatusMessage());
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
				System.out.println("status Code"+eObj.getStatusCode());
				System.out.println("status Message"+eObj.getStatusMessage());
				return eObj;
			}
		}
		GtfReport gtfRpt = new GtfReport();
		List<GtfReport>	gtfRptlst = new ArrayList<GtfReport>();
		if(prjParam.getgMemoriId()!=null && !"".equals(prjParam.getgMemoriId())){
			gtfRptlst = util.readProjectDataByGMemId(prjParam.getgMemoriId());
			System.out.println("gtfRptlst = "+gtfRptlst);
			if(gtfRptlst!=null && !gtfRptlst.isEmpty()){
				gtfRpt = gtfRptlst.get(0);
				System.out.println("gtfRpt = "+gtfRpt);
			} else{
				eObj.setStatusCode(404);
				eObj.setStatusMessage("Project does not exist in costCenter " +costCenterList+" !!!");
				return eObj;
			}
		}if( gtfRpt!=null && Util.isNullOrEmpty(gtfRpt.getgMemoryId()) && !selectedCC.contains(gtfRpt.getCostCenter())){
			eObj.setStatusCode(404);
			eObj.setStatusMessage("Project doesn't exist in costCenter " +costCenterList+" !!!");
			System.out.println("status Code"+eObj.getStatusCode());
			System.out.println("status Message"+eObj.getStatusMessage());
			return eObj;
		}if(gtfRpt!=null && Util.isNullOrEmpty(gtfRpt.getRequestor()) 
				&& (gtfRpt.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfRpt.getRequestor().split(":")[1]) ) ||
				(!gtfRpt.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfRpt.getRequestor()))){
			eObj.setStatusCode(403);
			eObj.setStatusMessage("User is not authorised to delete the project !!!");
			return eObj;
		}
		System.out.println("selectedCC::::"+selectedCC);
		Map<String,GtfReport> gtfRptMap = new HashMap<String,GtfReport>();
		String gMemoriId = "";
		String selectedCostCenter = "";
		boolean isGMemIdExists = false; 
		for(String cc: selectedCC){
			gtfRptMap = util.getAllReportDataFromCache(cc);
			gMemoriId = prjParam.getgMemoriId();
			System.out.println("selectedCC::::"+gtfRptMap);
			System.out.println("gMemoriId::::"+gMemoriId);
			System.out.println("gMemoriId.length()::::"+gMemoriId.length());
			if(gtfRptMap.get(gMemoriId)!=null /*&& (gMemoriId.length()==6)*/){
				isGMemIdExists = true;
				selectedCostCenter = cc;
				
			}
		}
		//Map<String,GtfReport> gtfRptMap = util.getAllReportDataFromCache(costCenter);
		//String gMemoriId = prjParam.getgMemoriId();
		System.out.println("gMemoriId"+gMemoriId);
		System.out.println("gtfRptMap.get(gMemoriId)"+gtfRptMap.get(gMemoriId));
		if(!isGMemIdExists){
			//disableProjectInBudget(prjParam,costCenterList.get(0));
			eObj.setStatusCode(404);
			eObj.setStatusMessage("Project does not exist in costCenter "+ costCenterList+" !!!");
			System.out.println("status Code"+eObj.getStatusCode());
			System.out.println("status Message"+eObj.getStatusMessage());
			return eObj;
		}else{
		System.out.println("gtfRptMap"+gtfRptMap);
		String status = "";
		int currQtr = Calendar.getInstance().get(Calendar.MONTH)/3;
		int prjCreationQtr = 0;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		Map<String,Date> cutOffDateMap =  util.getCutOffDates();
		Date cutOffDate =cutOffDateMap.get(currQtr+"");
		System.out.println("cutOffDate :::"+cutOffDate+ "currQtr = "+currQtr);
		for(Map.Entry<String, GtfReport> gtfEntry: gtfRptMap.entrySet()){
			status = "";
			if(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().length()<=10){
				gtfReport = gtfEntry.getValue();
				System.out.println("gtfReport.getCreateDate() :::"+gtfReport.getCreateDate());
				try {
					cal.setTime(sdf.parse(gtfReport.getCreateDate()));
				System.out.println("prjParam.getProjectOwner() : "+prjParam.getProjectOwner() +"::::" +gtfReport.getRequestor());
				if(Util.isNullOrEmpty(userInfo.getRole()) && "Admin".equalsIgnoreCase(userInfo.getRole())){
					status = "Disabled";
				}else{
				if((gtfReport.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfReport.getRequestor().split(":")[1]) ) ||
						(!gtfReport.getRequestor().contains(":") && !prjParam.getProjectOwner().equalsIgnoreCase(gtfReport.getRequestor()))){
					eObj.setStatusCode(403);
					eObj.setStatusMessage("User is not authorised to delete the project !!!");
					System.out.println("status Code"+eObj.getStatusCode());
					System.out.println("status Message"+eObj.getStatusMessage());
					return eObj;
				}if(Util.isNullOrEmpty(gtfReport.getStatus()) && !"New".equalsIgnoreCase(gtfReport.getStatus())){
					eObj.setStatusCode(408);
					eObj.setStatusMessage("Project status is not Planned !!!");
					System.out.println("status Code"+eObj.getStatusCode());
					System.out.println("status Message"+eObj.getStatusMessage());
					return eObj;
				}
				if ((Util.isNullOrEmpty(gtfReport.getPoNumber()))) {
						eObj.setStatusCode(407);
						eObj.setStatusMessage("PO exists and the project cannot be deleted!!!");
						System.out.println("status Code"+eObj.getStatusCode());
						System.out.println("status Message"+eObj.getStatusMessage());
						return eObj;
				} else if(/*cal.get(Calendar.MONTH)/3) >= currQtr &&*/ cutOffDate.after(sdf.parse(gtfReport.getCreateDate()))){
						eObj.setStatusCode(406);
						eObj.setStatusMessage("Benchmark exists and the project cannot be deleted!!!");
						return eObj;
				}else{
						status = "Disabled";
				}
				
			}
				gtfReport.setStatus(status);
				gtfReport.setCostCenter(selectedCostCenter);
				/*gtfReport.setProjectName(prjParam.getProjectName().replace("\\", "\\\\")
						.replace("\"", "\\\"").replace("\'", "\\\'"));*/
				gtfReports.add(gtfReport);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
		}
		}
		System.out.println("status Code"+eObj.getStatusCode());
		System.out.println("status Message"+eObj.getStatusMessage());
		util.generateProjectIdUsingJDOTxn(gtfReports);
		util.storeProjectsToCache(gtfReports, selectedCostCenter, BudgetConstants.NEW);
		eObj.setStatusCode(200);
		eObj.setStatusMessage("Successful !!!");
		System.out.println("gftReport List :::::"+util.getAllReportDataFromCache(selectedCostCenter));
		return eObj;
	}
}

//locked benchmark logic
//  projCreatedQtr >= currQtr && cutOffDate > projectCreateDate --- delete the project