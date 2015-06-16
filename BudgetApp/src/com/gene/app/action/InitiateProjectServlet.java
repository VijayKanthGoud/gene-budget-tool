package com.gene.app.action;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.ProjectParameters;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.gene.app.ws.exception.ErrorObject;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class InitiateProjectServlet extends HttpServlet{
	private final static Logger LOGGER = Logger
			.getLogger(InitiateProjectServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("initializeURL");
		String rcostCenter = req.getParameter("ccId");
		String rgMemId = req.getParameter("dummyGMemId");
		ProjectParameters prjParam = new ProjectParameters();
		List<String> costCenters = new ArrayList<String>();
		costCenters.add(rcostCenter);
		prjParam.setCostCentres(costCenters);
		String rUnixId = req.getParameter("unixId");
		prjParam.setProjectOwner(rUnixId);
		String rprj_name = req.getParameter("prj_name");
		prjParam.setProjectName(rprj_name);
		System.out.println("rcostCenter" + rcostCenter + "::::" + rgMemId
				+ "::::" + rprj_name + "::::" + rUnixId);
		storeRprtTogMemori(req, resp, prjParam);
	}

	public void storeRprtTogMemori(HttpServletRequest req,
			HttpServletResponse resp, ProjectParameters prjParam) throws IOException{
		Gson gson = new Gson();
		ErrorObject respFrmStudy = new ErrorObject();
		try {
			ArrayList<String> scopes = new ArrayList<>();
			scopes.add("https://www.googleapis.com/auth/userinfo.email");
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();
			AppIdentityService.GetAccessTokenResult accessToken = appIdentity
					.getAccessToken(scopes);
			
			String request = gson.toJson(prjParam);
			//String request = prepareInitiatePrjReqURL(ccId, unixId, prj_name);
			URL url = new URL(
					"http://memori-qa.appspot.com/web-service/project/preInitiate");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.addRequestProperty("Content-Type", "application/json");
			connection.addRequestProperty("Authorization", "OAuth "
					+ accessToken.getAccessToken());
			connection.setInstanceFollowRedirects(false);

			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			LOGGER.log(Level.INFO, "Request :" + request.toString());
			writer.write(request);
			writer.close();
			
			final int responseCode = connection.getResponseCode();
			LOGGER.log(Level.INFO, "Response Code is" + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				//final long newGMemId = resp.getInt("statusCode");
				JSONTokener response_tokens = new JSONTokener(connection.getInputStream());
				JSONObject response = new JSONObject();
				String errString="";
				
				try {
					response = new JSONObject(response_tokens);
					respFrmStudy = gson.fromJson(response.toString(), ErrorObject.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					errString = "No result found.";
					e.printStackTrace();
				}

				try {
					respFrmStudy.getStatusMessage();
				} catch (RuntimeException ex) {
				}
				try {
					//final long gMemoriId = response.getInt("statusCode");
					String result = String.valueOf(respFrmStudy.getStatusMessage());
					System.out.println("gMemoriId is:::"+respFrmStudy.getNewGMemId());
				} catch (RuntimeException ex) {
				}
				System.out.println("req.getParameter(dummyGMemId)"+req.getParameter("dummyGMemId"));
				System.out.println("prjParam.getCostCentres().get(0)"+prjParam.getCostCentres().get(0));
				System.out.println("respFrmStudy.getNewGMemId()"+respFrmStudy.getNewGMemId());
				System.out.println("error msg"+respFrmStudy.getStatusMessage());
				System.out.println("error code"+respFrmStudy.getStatusCode());
				if(Util.isNullOrEmpty(respFrmStudy.getNewGMemId())){
				updateGMemoriIdInBudget(req.getParameter("dummyGMemId"),prjParam.getCostCentres().get(0),respFrmStudy.getNewGMemId());
				//resp.sendRedirect("http://memori-qa.appspot.com/initiateProject?gMemoriId="+respFrmStudy.getNewGMemId());
				req.setAttribute("gMemoriId", respFrmStudy.getNewGMemId());
				req.setAttribute("Error Code", respFrmStudy.getStatusCode());
				req.setAttribute("Error Msg", respFrmStudy.getStatusMessage());
				}else{
					//throw new Exception("Project was not created in study. Error reason: "+respFrmStudy.getStatusMessage());
					req.setAttribute("Error Code", respFrmStudy.getStatusCode());
					req.setAttribute("Error Msg", "Project was not created in study. Error reason: "+respFrmStudy.getStatusMessage());
				}
				gson = new Gson();
				resp.getWriter().write(gson.toJson(respFrmStudy));
				/*RequestDispatcher rd = req.getRequestDispatcher("/getreport");
				rd.forward(req, resp);*/
			} else {
				//throw new Exception("Response Code is : "+responseCode);
				respFrmStudy.setStatusCode(connection.getResponseCode());
				respFrmStudy.setStatusMessage(connection.getResponseMessage());
				resp.getWriter().write(gson.toJson(respFrmStudy));
			}

		} catch (Exception e) {
			// Error handling elided.
			String result = e.getMessage();
			System.out.println("result:::" + result);
			respFrmStudy.setStatusCode(414);
			respFrmStudy.setStatusMessage(result);
			resp.getWriter().write(gson.toJson(respFrmStudy));
			req.setAttribute("Error Msg",  e.getMessage() +"::::"+e.getStackTrace());
		}

	}

	public void updateGMemoriIdInBudget(String gMemoriId,String costCenter,String gMemIdFrmStudy){
		DBUtil util = new DBUtil();
		Map<String,GtfReport> gtfRptMap = util.getAllReportDataFromCache(costCenter);
		String ccFromStudy = "";
		List<GtfReport> gtfRptList = new ArrayList<GtfReport>();
		List<GtfReport> oldgtfRptList = new ArrayList<GtfReport>();
		String newGMemId = "";
		GtfReport gtfRpt = new GtfReport();
		ArrayList<String> newChildList = null;
		List<String> oldChildList = new ArrayList<String>();
		if(gtfRptMap!=null && !gtfRptMap.isEmpty() && gtfRptMap.get(gMemoriId)!=null){
			for(Map.Entry<String, GtfReport> gtfEntry : gtfRptMap.entrySet()){
				newGMemId = "";
				gtfRpt = gtfEntry.getValue();
				if(gtfEntry.getKey().equals(gMemoriId) || gtfEntry.getKey().contains(gMemoriId)){
					if(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().contains(".")){
						newGMemId=gMemIdFrmStudy+"."+gtfEntry.getKey().substring(gtfEntry.getKey().indexOf(".")+1);//split(".")[1];
					}else if(gtfEntry.getKey().equals(gMemoriId)){
						newGMemId = gMemIdFrmStudy;
					}
					try {
						oldgtfRptList.add((GtfReport) gtfRpt.clone());
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gtfRpt.setgMemoryId(newGMemId);
					oldChildList = gtfRpt.getChildProjectList();
					newChildList = new ArrayList<String>();
					if(oldChildList != null && !oldChildList.isEmpty()){
						for(String chld : oldChildList ){
							newChildList.add(chld.contains(".")?(gMemIdFrmStudy+chld.substring(chld.indexOf("."))):gMemIdFrmStudy);
						}
						gtfRpt.setChildProjectList(newChildList);
					}
					gtfRptList.add(gtfRpt);
				}
			}
			if(oldgtfRptList!=null && !oldgtfRptList.isEmpty()){
				for(int i=0;i<oldgtfRptList.size();i++){
					gtfRptMap.remove(oldgtfRptList.get(i).getgMemoryId());
				}
			}
			if(gtfRptList!=null && !gtfRptList.isEmpty()){
				for(int i=0;i<gtfRptList.size();i++){
					gtfRptMap.put(gtfRptList.get(i).getgMemoryId(),gtfRptList.get(i));
				}
			}
			System.out.println("gtfRptMap in update"+gtfRptMap);
			System.out.println("gtfRptList in update"+gtfRptList.get(0).getgMemoryId());
			util.removeExistingProject(oldgtfRptList);
			util.storeProjectsToCache(oldgtfRptList,costCenter, BudgetConstants.OLD);
			util.storeProjectsToCache(gtfRptList,costCenter, BudgetConstants.NEW);
			util.generateProjectIdUsingJDOTxn(gtfRptList);
			System.out.println("gtfRptMap from cc = "+util.getAllReportDataFromCache(costCenter));
		}
		
	}
}
