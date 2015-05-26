package com.gene.app.action;

import static com.gene.app.util.Util.roundDoubleValue;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.ProjectSequenceGeneratorUtil;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class StoreReport extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(StoreReport.class.getName());
	Map<String, Double> brandMap = new TreeMap<String, Double>();
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	String gMemoriId = "";
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "Inside store report");
		HttpSession session = req.getSession();
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		resp.setContentType(BudgetConstants.contentType);
		String objarray = req.getParameter(BudgetConstants.objArray).toString();
		String costCenter = req.getParameter("costCenter").toString();
		user.setSelectedCostCenter(costCenter);
		LOGGER.log(Level.INFO, "objarray : " + objarray +"\nUser is : "+user);
		storeProjectData(objarray, user, req, resp);
		session.setAttribute("userInfo", user);
	}

	public void storeProjectData(String objarray, UserRoleInfo user, HttpServletRequest req,
			HttpServletResponse resp) {
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		JSONArray jsonArray = null;
		GtfReport gtfReport = null;
		JSONObject rprtObject = null;
		String remarks = null;
		String multiBrand = "";
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		String status = "";
		String gmultiIdList="";
		String poNum = "";
		int flag = 0;
		try {
			jsonArray = new JSONArray(objarray);
			for (int count = 0; count < jsonArray.length(); count++) {
				gtfReport = new GtfReport();
				rprtObject = jsonArray.getJSONObject(count);
				gtfReport.setEmail(user.getEmail());
				poNum=rprtObject
				.getString(BudgetConstants.New_GTFReport_PoNumber)
				.toString().trim();
				if (poNum.length() > 0 && !"0".equalsIgnoreCase(poNum)) {
					status = BudgetConstants.status_Active;
				} else {
					poNum="";
					status = BudgetConstants.New_GTFReport_Status;
				}
				if (BudgetConstants.status_New.equalsIgnoreCase(status.trim())) {
					flag = 1;
				} else if (BudgetConstants.status_Active
						.equalsIgnoreCase(status.trim())) {
					flag = 2;
				} else {
					flag = 3;
				}
				gtfReport.setFlag(flag);
				gtfReport.setStatus(status);
				gtfReport.setRequestor(rprtObject
						.getString(BudgetConstants.New_GTFReport_ProjectOwner));
				gtfReport.setProject_WBS(rprtObject
						.getString(BudgetConstants.New_GTFReport_Project_WBS));
				gtfReport.setSubActivity(rprtObject
						.getString(BudgetConstants.New_GTFReport_SubActivity));
				gtfReport.setPoNumber(poNum);
				String poDesc = rprtObject
						.getString(BudgetConstants.New_GTFReport_PoDesc);
				gtfReport.setPoDesc(poDesc);
				gtfReport.setVendor(rprtObject
						.getString(BudgetConstants.New_GTFReport_Vendor));
				try{
					gtfReport.setUnits(Integer.parseInt(rprtObject.getString(BudgetConstants.New_GTFReport_Unit)));
				}catch(Exception ne){
					gtfReport.setUnits(0);
				}
				gtfReport.setCreateDate(timeStamp);
				gtfReport.setYear(BudgetConstants.dataYEAR);
				gtfReport.setCostCenter(user.getSelectedCostCenter());
				try {
					remarks = ((rprtObject
							.getString(BudgetConstants.New_GTFReport_Remarks) != null) && (!""
							.equalsIgnoreCase(rprtObject.getString(
									BudgetConstants.New_GTFReport_Remarks)
									.trim()))) ? (rprtObject
							.getString(BudgetConstants.New_GTFReport_Remarks))
							: "";
					//if (remarks.contains("\"")) {
						remarks = remarks.replace("\\", "\\\\")
								.replace("\"", "\\\"").replace("\'", "\\\'").replace("<", "&lt;").replace(">", "&gt;");
					//}
				} catch (com.google.appengine.labs.repackaged.org.json.JSONException exception) {
					remarks = "";
				}
				gtfReport.setRemarks(remarks);
				multiBrand = rprtObject.getString(BudgetConstants.isMultiBrand);
		 
				/*Map<String, Integer> getAllGmemoriIds = util
						.getAllgMemoriId();
				if(getAllGmemoriIds.get(rprtObject
						.getString(BudgetConstants.New_GTFReport_gMemoriId))!=null){
					
					try {
						resp.sendRedirect("/");
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}*/

				if (multiBrand != null
						&& !"".equalsIgnoreCase(multiBrand.trim())
						&& "true".equalsIgnoreCase(multiBrand.trim())) {
					prepareMultiBrandProjectData(gtfReports, gtfReport,
							rprtObject, timeStamp);
				} else {
					prepareSingleBrandProjectData(gtfReports, gtfReport,
							rprtObject, false, timeStamp);
				}
			}
			if(gtfReports.size() != 0){
				/*try {
					storeRprtTogMemori(req, resp, gtfReports);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				util.generateProjectIdUsingJDOTxn(gtfReports);
				util.storeProjectsToCache(gtfReports, user.getSelectedCostCenter(),
						BudgetConstants.NEW);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	public void prepareSingleBrandProjectData(List<GtfReport> gtfReports,
			GtfReport gtfReport, JSONObject rprtObject, boolean isMultibrand,
			String timeStamp) {
		try {
			
			if("".equalsIgnoreCase(rprtObject
					.getString(BudgetConstants.New_GTFReport_gMemoriId).trim()) || "0".equalsIgnoreCase(rprtObject
							.getString(BudgetConstants.New_GTFReport_gMemoriId).trim())){
				gMemoriId = ""+generator.nextValue();
				gtfReport.setDummyGMemoriId(true);
				gtfReport.setgMemoryId(gMemoriId);
			}else{
				gtfReport.setDummyGMemoriId(false);
				gtfReport.setgMemoryId(rprtObject
						.getString(BudgetConstants.New_GTFReport_gMemoriId));
				
			}
			boolean isValidGMemoriId = util.validategMemoriId(gtfReport.getgMemoryId());
			if(isValidGMemoriId){
				throw new Error(gtfReport.getgMemoryId()+" : gMemoriId already exists !!!");
			}
			//boolean isDummyId = isDummyGMemoriId(gMemoriId);
			
			gtfReport.setProjectName(rprtObject
					.getString(BudgetConstants.New_GTFReport_ProjectName).replace("\\", "\\\\")
					.replace("\"", "\\\"").replace("\'", "\\\'"));
			gtfReport.setBrand(rprtObject.getString(
					BudgetConstants.New_GTFReport_Brand).trim());
			gtfReport.setCreateDate(timeStamp);
			gtfReport.setYear(BudgetConstants.dataYEAR);
			gtfReport.setQual_Quant("Qual_Quant");
			gtfReport.setStudy_Side("study_Side");
			try {
				gtfReport
						.setPercent_Allocation(BudgetConstants.GTF_Percent_Total);
			} catch (NumberFormatException e) {
				gtfReport.setPercent_Allocation(0);
			}
			Map<String, Double> plannedMap = new HashMap<String, Double>();
			Map<String, Double> setZeroMap = new HashMap<String, Double>();
			for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
				setZeroMap.put(BudgetConstants.months[cnt], 0.0);
				try {
					plannedMap
							.put(BudgetConstants.months[cnt],
									roundDoubleValue(
											Double.parseDouble(rprtObject.getString(Integer
													.toString(cnt
															+ BudgetConstants.months.length
															- 1))), 2));
				} catch (NumberFormatException e) {
					plannedMap.put(BudgetConstants.months[cnt], 0.0);
				}
			}
			if(isMultibrand){
				try {
					plannedMap
					.put(BudgetConstants.months[12],
							roundDoubleValue(Double.parseDouble(rprtObject.getString("51")),2));
				} catch (NumberFormatException e) {
					plannedMap.put(BudgetConstants.months[12], 0.0);
				}
			}
			gtfReport.setPlannedMap(plannedMap);
			gtfReport.setBenchmarkMap(plannedMap);
			gtfReport.setAccrualsMap(setZeroMap);
			gtfReport.setVariancesMap(plannedMap);
			gtfReport.setMultiBrand(isMultibrand);
			gtfReports.add(gtfReport);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void prepareMultiBrandProjectData(List<GtfReport> gtfReports,
			GtfReport gtfReport1, JSONObject rprtObject, String timeStamp) {
		prepareSingleBrandProjectData(gtfReports, gtfReport1, rprtObject, true,
				timeStamp);
		
		GtfReport gtfReport = null;
		JSONArray jsonArray = null;
		JSONObject multiBrandObject = null;
		Double value = 0.0;
		double percent_allocation = 0.0;
		String prj_owner;
		String prj_owner_email;
		DBUtil util = new DBUtil();
		String email = "";
		ArrayList<String> gmultiIdList= new ArrayList<>();
		gmultiIdList.add(gtfReport1.getgMemoryId());
		try {
			String mutliBrandArray = rprtObject
					.getString(BudgetConstants.multiBrandInput);
			jsonArray = new JSONArray(mutliBrandArray);
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					gtfReport = (GtfReport) gtfReport1.clone();
				} catch (CloneNotSupportedException e1) {
					e1.printStackTrace();
				}
				multiBrandObject = jsonArray.getJSONObject(i);
				if (multiBrandObject.getString("4") == null
						|| "".equals(multiBrandObject.getString("4").trim())) {
					break;
				}
				gtfReport.setCreateDate(timeStamp);
				gtfReport.setYear(BudgetConstants.dataYEAR);
				//gtfReport.setProjectName(multiBrandObject.getString("4"));
				gtfReport.setBrand(multiBrandObject.getString("1").trim());
				//gtfReport.setgMemoryId(multiBrandObject.getString("5"));
				
				if(multiBrandObject.getString("5").trim().indexOf('.')==0){
					
					if(!gtfReport.isDummyGMemoriId()){
						gtfReport.setgMemoryId(gtfReport.getgMemoryId()+multiBrandObject.getString("5"));
					}else{
					gtfReport.setgMemoryId(gMemoriId+multiBrandObject.getString("5"));
					gtfReport.setDummyGMemoriId(true);
					}
				}else{
					gtfReport.setDummyGMemoriId(false);
					gtfReport.setgMemoryId(multiBrandObject.getString("5"));
				}
				/*String gMemoriId = multiBrandObject.getString("5");
				boolean isDummyId = isDummyGMemoriId(gMemoriId);
				gtfReport.setDummyGMemoriId(isDummyId);
				gtfReport.setgMemoryId(gMemoriId);*/
				prj_owner = multiBrandObject.getString("7");
				prj_owner_email = util.getPrjEmailByName(prj_owner);
				gtfReport
						.setRequestor(multiBrandObject.getString("7")
								+ ":"
								+ rprtObject
										.getString(BudgetConstants.New_GTFReport_ProjectOwner));
				email = gtfReport.getEmail();
				gtfReport.setEmail(prj_owner_email + ":" + email);
				try {
					gtfReport.setPercent_Allocation(Double
							.parseDouble(multiBrandObject.getString("2")));
				} catch (NumberFormatException e) {
					gtfReport.setPercent_Allocation(0);
				}
				percent_allocation = Double.parseDouble(multiBrandObject
						.getString("2"));
				//gtfReport.setgMemoryId(multiBrandObject.getString("5"));
				Map<String, Double> plannedMap = new HashMap<String, Double>();
				Map<String, Double> setZeroMap = new HashMap<String, Double>();
				Map<String, Double> parentPlannedMap = gtfReport
						.getPlannedMap();
				for(int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
					setZeroMap.put(BudgetConstants.months[cnt], 0.0);
				}
				for (int cnt = 0; cnt < BudgetConstants.months.length - 1; cnt++) {
					try {
						value = roundDoubleValue(
								parentPlannedMap.get(BudgetConstants.months[cnt])
										* percent_allocation / 100, 2);
						plannedMap.put(BudgetConstants.months[cnt], value);
					} catch (NumberFormatException e) {
						plannedMap.put(BudgetConstants.months[cnt], 0.0);
					}
				}
				plannedMap
						.put(BudgetConstants.months[BudgetConstants.months.length - 1],
								Double.parseDouble(multiBrandObject
										.getString("3")));
				gtfReport.setPlannedMap(plannedMap);
				gtfReport.setBenchmarkMap(plannedMap);
				gtfReport.setAccrualsMap(setZeroMap);
				gtfReport.setVariancesMap(plannedMap);
				gtfReport.setMultiBrand(true);
				gmultiIdList.add(gtfReport.getgMemoryId());
				gtfReports.add(gtfReport);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for(String gmemId: gmultiIdList){
			for(GtfReport gtf : gtfReports){
				if(gmemId.equalsIgnoreCase(gtf.getgMemoryId())){
					gtf.setChildProjectList(gmultiIdList);
				}
			}
		}
	}
	public void storeRprtTogMemori(HttpServletRequest req,
			HttpServletResponse resp, List<GtfReport> gtfReports) throws ClientProtocolException,
			IOException, JSONException {

		try {
			ArrayList<String> scopes = new ArrayList<>();
			scopes.add("https://www.googleapis.com/auth/userinfo.email");
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();
			AppIdentityService.GetAccessTokenResult accessToken = appIdentity
					.getAccessToken(scopes);
			Gson gson = new Gson();
			String request = gson.toJson(gtfReports);
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
				
				/*
				// Note: Should check the content-encoding.

				JSONTokener response_tokens = new JSONTokener(
						connection.getInputStream());
				JSONObject response = new JSONObject();
				String errString = "";
				try {
					response = new JSONObject(response_tokens);

					GtfReport gMemori = gson.fromJson(response.toString(),
							GtfReport.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					errString = "No result found.";
					e.printStackTrace();
				}

				PrintWriter pw = resp.getWriter();

				pw.println("<html>");

				pw.println("<body>");
				pw.println("<br>");
				pw.println("<br>");
				pw.println("<form method=\"POST\" action=\"http://1-dot-budget-mgmt-tool.appspot.com/WSAppClient\">");
				pw.println("gMemori Id   <br>");
				pw.println("<input type=\"text\" name=\"gMemoriId\">");
				pw.println("<br>");
				pw.println("<br><br>");
				pw.println(" <input type=\"submit\" value=\"Submit\">");

				pw.println("<br><br>");
				pw.println("Response is    <br>");
				pw.println("<br><br>");
				if (errString.length() == 0) {
					pw.println(response.toString().replace(",", "<br>")
							.replace("{", "").replace("}", ""));
				} else {
					pw.println(errString);
				}
				pw.println("</form>");
				pw.println("</body>");
				pw.println("</html>");

				try {
					response.getString("errorInfo");
				} catch (RuntimeException ex) {
				}
				try {
					final long gMemoriId = response.getLong("gMemoriId");
					String result = String.valueOf(gMemoriId);
					System.out.println("gMemoriId is:::" + result);
				} catch (RuntimeException ex) {
				}
			*/} else {
				throw new Exception();
			}

		} catch (Exception e) {
			// Error handling elided.
			String result = e.getMessage();

			System.out.println("result:::" + result);

		}

	}
/*	public void insertUserRoleInfo(User user) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		UserRoleInfo userInfo = new UserRoleInfo();
		userInfo.setEmail(user.getEmail());
		userInfo.setBrand(brandMap);
		userInfo.setUserName(user.getNickname());
		userInfo.setRole("Project Owner");

		try {
			pm.makePersistent(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}*/

	public boolean isDummyGMemoriId(String gMemoriId){
		boolean isDummyId = false;
		if(gMemoriId!=null && !"".equalsIgnoreCase(gMemoriId.trim())){
			if(gMemoriId.contains(".")){
				if(gMemoriId.length()!=8){
					isDummyId = true;
				}
			}else{
				if(gMemoriId.length()!=6){
					isDummyId = true;
				}
			}
		}
		return isDummyId;
	}
}
