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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.ProjectParameters;
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
		String costCenter = req.getParameter("ccId");
		String gMemId = req.getParameter("dummyGMemId");
		ProjectParameters prjParam = new ProjectParameters();
		prjParam.setCostCentre(costCenter);
		prjParam.setpUnixId(req.getParameter("unixId"));
		prjParam.setProjectName(req.getParameter("prj_name"));
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
					"https://wsapp-dot-gbmt-dev.appspot.com/rest/createProjectDetails");
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

				updateGMemoriIdInBudget(req.getParameter("dummyGMemId"),prjParam.getCostCentre(),respFrmStudy.getNewGMemId());
				resp.sendRedirect("https://memori-dev.appspot.com/initiateProject?gMemoriId="+respFrmStudy.getNewGMemId());
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
		List<GtfReport> gtfRptList = new ArrayList<GtfReport>();
		String newGMemId = "";
		if(gtfRptMap!=null && !gtfRptMap.isEmpty()){
			for(Map.Entry<String, GtfReport> gtfEntry : gtfRptMap.entrySet()){
				newGMemId = "";
				if(gtfEntry.getKey().equals(gMemoriId) || gtfEntry.getKey().contains(gMemoriId)){
					if(gtfEntry.getKey().contains(gMemoriId) && gtfEntry.getKey().contains(".")){
						newGMemId=gMemIdFrmStudy+"."+gtfEntry.getKey().split(".")[1];
					}else{
						newGMemId = gMemIdFrmStudy;
					}
					gtfEntry.getValue().setgMemoryId(newGMemId);
					gtfRptMap.remove(gMemoriId);
					gtfRptMap.put(newGMemId, gtfEntry.getValue());
					gtfRptList.add(gtfEntry.getValue());
				}
			}
			util.generateProjectIdUsingJDOTxn(gtfRptList);
			util.saveAllReportDataToCache(costCenter, gtfRptMap);
		}
	}
}
