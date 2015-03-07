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
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		System.out.println("initializeURL");
		String rcostCenter = req.getParameter("ccId");
		String costCenter = rcostCenter.replace("_", " ");
		String rgMemId = req.getParameter("dummyGMemId");
		String gMemId = rgMemId.replace("_", " ");
		ProjectParameters prjParam = new ProjectParameters();
		List<String> costCenters = new ArrayList<String>();
		costCenters.add(costCenter);
		prjParam.setCostCentres(costCenters);
		String rUnixId = req.getParameter("unixId");
		String unixId = rUnixId.replace("_", " ");
		prjParam.setProjectOwner(unixId);
		String rprj_name = req.getParameter("prj_name");
		String prj_name = rprj_name.replace("_", " ");
		prjParam.setProjectName(prj_name);
		storeRprtTogMemori(req, resp, prjParam);
	}

	public void storeRprtTogMemori(HttpServletRequest req,
			HttpServletResponse resp, ProjectParameters prjParam) {

		try {
			ArrayList<String> scopes = new ArrayList<>();
			scopes.add("https://www.googleapis.com/auth/userinfo.email");
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();
			AppIdentityService.GetAccessTokenResult accessToken = appIdentity
					.getAccessToken(scopes);
			Gson gson = new Gson();
			String request = gson.toJson(prjParam);
			//String request = prepareInitiatePrjReqURL(ccId, unixId, prj_name);
			URL url = new URL(
					"http://memori-dev.appspot.com/web-service/project/preInitiate");
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
				ErrorObject respFrmStudy = new ErrorObject();
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
				resp.sendRedirect("https://memori-dev.appspot.com/initiateProject?gMemoriId="+respFrmStudy.getNewGMemId());
				}else{
					throw new Exception("Project was not created in study. Error reason: "+respFrmStudy.getStatusMessage());
				}
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			// Error handling elided.
			String result = e.getMessage();
			System.out.println("result:::" + result);
		}

	}

	public void updateGMemoriIdInBudget(String gMemoriId,String costCenter,String gMemIdFrmStudy){
		DBUtil util = new DBUtil();
		Map<String,GtfReport> gtfRptMap = util.getAllReportDataFromCache(costCenter);
		System.out.println("gtfMap ::"+gtfRptMap);
		String ccFromStudy = "";
		List<GtfReport> gtfRptList = new ArrayList<GtfReport>();
		List<GtfReport> oldgtfRptList = new ArrayList<GtfReport>();
		String newGMemId = "";
		GtfReport gtfRpt = new GtfReport();
		System.out.println("gMemoriId = "+gMemoriId);
		System.out.println("(gtfRptMap!=null && !gtfRptMap.isEmpty() && gtfRptMap.get(gMemoriId)!=null)"+(gtfRptMap.get(gMemoriId)!=null));
		if(gtfRptMap!=null && !gtfRptMap.isEmpty() && gtfRptMap.get(gMemoriId)!=null){
			for(Map.Entry<String, GtfReport> gtfEntry : gtfRptMap.entrySet()){
				newGMemId = "";
				gtfRpt = gtfEntry.getValue();
				if(gtfEntry.getKey().equals(gMemoriId) || gtfEntry.getKey().contains(gMemoriId)){
					if(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().contains(".")){
						System.out.println("in if"+(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().contains(".")));
						newGMemId=gMemIdFrmStudy+"."+gtfEntry.getKey().split(".")[1];
					}else if(gtfEntry.getKey().equals(gMemoriId)){
						System.out.println("else if");
						newGMemId = gMemIdFrmStudy;
					}
					System.out.println("gMemoriId"+gMemoriId);
					System.out.println("newGMemId"+newGMemId);
					oldgtfRptList.add(gtfRpt);
					util.removeExistingProject(oldgtfRptList);
					util.storeProjectsToCache(oldgtfRptList,costCenter, BudgetConstants.OLD);
					gtfRpt.setgMemoryId(newGMemId);
					System.out.println("gtfRptMap before remove"+gtfRptMap);
					gtfRptMap.remove(gMemoriId);
					gtfRptMap.put(newGMemId, gtfRpt);
					System.out.println("gtfRptMap after remove"+gtfRptMap);
					//ccFromStudy = costCenter;
					gtfRptList.add(gtfRpt);
					/*util.generateProjectIdUsingJDOTxn(gtfRptList);
					util.storeProjectsToCache(gtfRptList,costCenter, BudgetConstants.NEW);*/
				}
			}
			System.out.println("gtfRptMap"+gtfRptMap);
			util.saveAllReportDataToCache(costCenter, gtfRptMap);
			System.out.println("gtfRptList"+gtfRptList);
			util.generateProjectIdUsingJDOTxn(gtfRptList);
			/*util.removeExistingProject(oldGtfReportList);
			util.storeProjectsToCache(oldGtfReportList,user.getSelectedCostCenter(), BudgetConstants.OLD);*/
		}
		
	}
}
