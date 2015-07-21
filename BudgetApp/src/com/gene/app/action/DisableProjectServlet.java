package com.gene.app.action;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.ProjectParameters;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.gene.app.ws.exception.ErrorObject;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.gson.Gson;


/**
 * The Class DisableProjectServlet.
 */
@SuppressWarnings("serial")
public class DisableProjectServlet extends HttpServlet {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger
			.getLogger(DisableProjectServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String gMemoriId = "";
		String costCenter = "";
		String unixId = "";
		Gson gson = new Gson();
		DBUtil util = new DBUtil();
		if(Util.isNullOrEmpty(req.getParameter("gMem"))){
			gMemoriId = req.getParameter("gMem");
		}if(Util.isNullOrEmpty(req.getParameter("costCenter"))){
			costCenter = req.getParameter("costCenter");			
		}
		if(Util.isNullOrEmpty(req.getParameter("projectOwner"))){
			unixId = req.getParameter("projectOwner");
		}
		LOGGER.log(Level.INFO, "Request received to disable: " + gMemoriId);
		if(gMemoriId.length()==10){
			util.disableProject(gMemoriId, costCenter);
			ErrorObject respFrmStudy = new ErrorObject();
			respFrmStudy.setStatusCode(200);
			respFrmStudy.setStatusMessage("Project deleted Successfully !!!");
			resp.getWriter().write(gson.toJson(respFrmStudy));
		}else{
			ProjectParameters prjParam = new ProjectParameters();
			prjParam.setgMemoriId(gMemoriId);
			/*List<String> costCenters = new ArrayList<String>();
			costCenters.add(costCenter);
			prjParam.setCostCentres(costCenters);*/
			prjParam.setUnixId(unixId);
			ErrorObject respFrmStudy = deleteProjectFromStudy(req,resp,prjParam);
			if(respFrmStudy.getStatusCode()==200){
			util.disableProject(gMemoriId, costCenter);			
		}
	}
	}
	public ErrorObject deleteProjectFromStudy(HttpServletRequest req,
			HttpServletResponse resp, ProjectParameters prjParam){
		ErrorObject respFrmStudy = new ErrorObject();
		try{
		ArrayList<String> scopes = new ArrayList<>();
		scopes.add("https://www.googleapis.com/auth/userinfo.email");
		AppIdentityService appIdentity = AppIdentityServiceFactory
				.getAppIdentityService();
		AppIdentityService.GetAccessTokenResult accessToken = appIdentity
				.getAccessToken(scopes);
		Gson gson = new Gson();
		String request = gson.toJson(prjParam);
		URL url = new URL(
				"https://memori-qa.appspot.com/web-service/project/deactivate");
		HttpURLConnection connection = (HttpURLConnection) url
				.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Content-Type", "application/json");
		connection.addRequestProperty("Authorization", "OAuth "
				+ accessToken.getAccessToken());
		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(BudgetConstants.TIME_OUT_PERIOD);
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
				String result = String.valueOf(respFrmStudy.getStatusMessage());
			} catch (RuntimeException ex) {
			}
			System.out.println("error msg"+respFrmStudy.getStatusMessage());
			System.out.println("error code"+respFrmStudy.getStatusCode());
			gson = new Gson();
			resp.getWriter().write(gson.toJson(respFrmStudy));
		} else {
			throw new Exception("Response Code is : "+responseCode);
		}

	} catch (Exception e) {
		// Error handling elided.
		String result = e.getMessage();
		System.out.println("result:::" + result);
		req.setAttribute("Error Msg",  e.getMessage() +"::::"+e.getStackTrace());
	}
		/*Gson gson = new Gson();
		respFrmStudy.setStatusCode(403);
		respFrmStudy.setStatusMessage("User is not authorised to delete the project");
		try {
			resp.getWriter().write(gson.toJson(respFrmStudy));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return respFrmStudy;
}
}
