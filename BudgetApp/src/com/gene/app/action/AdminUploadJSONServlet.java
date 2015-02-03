package com.gene.app.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.ProjectSequenceGeneratorUtil;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@SuppressWarnings("serial")
public class AdminUploadJSONServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(AdminUploadJSONServlet.class.getName());
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "inside fileupload...");
		UserService userService = UserServiceFactory.getUserService();
		User userLoggedIn = userService.getCurrentUser();
		UserRoleInfo user = util.readUserRoleInfo(userLoggedIn.getEmail());
		String objarray = req.getParameter(BudgetConstants.objArray).toString();
		String [] objArrayStr = objarray.split("],");
		System.out.println("objArrayStr = "+objArrayStr.length);
		String costCentre = "";
		for(int i=0;i<objArrayStr.length;i++){
			System.out.println("objArrayStr values "+i+ " ::: "+objArrayStr[i]);
		}
		List<List<String>> rowList = new ArrayList();

		try {
			JSONArray jsonArray = new JSONArray(objarray);
			costCentre = jsonArray.getJSONArray(19).get(3)
					.toString().split("\\s")[0];
			for (int count = 23; count < jsonArray.length(); count++) {
				List list = new ArrayList();
				for (int k = 3; k < jsonArray.getJSONArray(count).length(); k++) {
					String varCol = jsonArray.getJSONArray(count).get(k)
							.toString();
					if (!varCol.equalsIgnoreCase("null")) {
						list.add(jsonArray.getJSONArray(count).get(k));
					} else {
						list.add("");
					}
				}
				rowList.add(list);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		createGTFReports(user, user,rowList, gtfReports,costCentre);
	}

	private void createGTFReports(UserRoleInfo user,UserRoleInfo orgUser,
			List<List<String>> rowList, List<GtfReport> gtfReports,String costCentre) {
		boolean isMultibrand = false;
		Map<String,UserRoleInfo> userMap = util.readAllUserInfo();
		for (List recvdRow : rowList) {
			GtfReport gtfReport = new GtfReport();
			if (recvdRow.get(2) != null && !recvdRow.get(2).equals("#")
					&& !recvdRow.get(2).toString().trim().equals("")) {
				gtfReport.setSubActivity(recvdRow.get(2).toString());
			} else {
				gtfReport.setSubActivity("");
				continue;
			}
			if (recvdRow.get(8) != null && !recvdRow.get(8).equals("#")
					&& !recvdRow.get(8).toString().trim().equals("")) {
				gtfReport.setRequestor(recvdRow.get(8).toString());
				//user = new UserRoleInfo();
				if(userMap!=null && !userMap.isEmpty()){
					user = util.getUserByName(userMap,recvdRow.get(8).toString());
					if(user==null || user.getEmail()==null){
						user = orgUser;
						gtfReport.setRequestor(user.getFullName());
					}
				}
			} else {
				user = orgUser;
				gtfReport.setRequestor(user.getFullName());
			}
			gtfReport.setCostCenter(costCentre);

			if (recvdRow.get(0) != null && !recvdRow.get(0).equals("#")
					&& !recvdRow.get(0).toString().trim().equals("")) {
				gtfReport.setProject_WBS(recvdRow.get(0).toString());
			} else {
				gtfReport.setProject_WBS("");
			}

			if (recvdRow.get(1) != null && !recvdRow.get(1).equals("#")
					&& !recvdRow.get(1).toString().toString().trim().equals("")) {
				gtfReport.setWBS_Name(recvdRow.get(1).toString());
			} else {
				gtfReport.setWBS_Name(recvdRow.get(1).toString());
			}

			

			if (recvdRow.get(3) != null && !recvdRow.get(3).equals("#")
					&& !recvdRow.get(3).toString().trim().equals("")) {
				gtfReport.setBrand(recvdRow.get(3).toString());
			} else {
				gtfReport.setBrand("");
			}

			if (recvdRow.get(4) != null && !recvdRow.get(4).equals("#")
					&& !recvdRow.get(4).toString().trim().equals("")) {
				try {
					gtfReport.setPercent_Allocation(Double.parseDouble(recvdRow
							.get(4).toString()));
				} catch (NumberFormatException ne) {
					gtfReport.setPercent_Allocation(100);
				}
			} else {
				gtfReport.setPercent_Allocation(100);
			}

			if (recvdRow.get(5) != null && !recvdRow.get(5).equals("#")
					&& !recvdRow.get(5).toString().trim().equals("")) {
				gtfReport.setPoNumber(recvdRow.get(5).toString());
				gtfReport.setStatus("Active");
				gtfReport.setFlag(2);
			} else {
				gtfReport.setPoNumber("");
				gtfReport.setStatus("New");
				gtfReport.setFlag(1);
			}

			if (recvdRow.get(6) != null && !recvdRow.get(6).equals("#")
					&& !recvdRow.get(6).toString().trim().equals("")) {
				gtfReport.setPoDesc(recvdRow.get(6).toString());
			} else {
				gtfReport.setPoDesc("");
			}

			if (recvdRow.get(7) != null && !recvdRow.get(7).equals("#")
					&& !recvdRow.get(7).toString().trim().equals("")) {
				gtfReport.setVendor(recvdRow.get(7).toString());
			} else {
				gtfReport.setVendor("");
			}

			/*if (recvdRow.get(8) != null && !recvdRow.get(8).equals("#")
					&& !recvdRow.get(8).toString().trim().equals("")) {
				gtfReport.setRequestor(recvdRow.get(8).toString());
			} else {
				gtfReport.setRequestor("");
			}*/

			String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
					.format(Calendar.getInstance().getTime());
			gtfReport.setCreateDate(timeStamp);
			gtfReport.setYear(BudgetConstants.dataYEAR);
			gtfReport.setQual_Quant("Qual_Quant");
			gtfReport.setStudy_Side("study_Side");
			gtfReport.setUnits(1);
			Map<String, Double> plannedMap = new HashMap<String, Double>();
			Map<String, Double> setZeroMap = new HashMap<String, Double>();
			for (int cnt = 0; cnt < BudgetConstants.months.length; cnt++) {
				setZeroMap.put(BudgetConstants.months[cnt], 0.0);
				try {

					if (recvdRow.get(cnt + 9) != null
							&& !recvdRow.get(cnt + 9).toString().trim()
									.equals("")) {
						String value = "0.0";
						if (recvdRow.get(cnt + 9).toString().contains("(")) {
							value = "-" + recvdRow.get(cnt + 9).toString().replaceAll("[^\\d.]", "");
						}else{
							value = recvdRow.get(cnt + 9).toString();
						}
						plannedMap.put(BudgetConstants.months[cnt], Double.parseDouble(value));
					} else {
						plannedMap.put(BudgetConstants.months[cnt], 0.0);
					}
				} catch (Exception e1) {
					System.out.println(e1);
					plannedMap.put(BudgetConstants.months[cnt], 0.0);
				}
			}
			gtfReport.setPlannedMap(plannedMap);
			gtfReport.setBenchmarkMap(plannedMap);
			gtfReport.setAccrualsMap(setZeroMap);
			gtfReport.setVariancesMap(setZeroMap);
			gtfReport.setMultiBrand(isMultibrand);
			gtfReport.setEmail(user.getEmail());

			String poDesc = gtfReport.getPoDesc();
			gtfReport.setProjectName(poDesc);
			String gMemoriId;
			try {
				if(gtfReport.getPoDesc().indexOf("_")==6){
				gMemoriId = Integer.parseInt(gtfReport.getPoDesc().substring(0,
						Math.min(poDesc.length(), 6)))
						+ "";
				gtfReport.setDummyGMemoriId(false);
				}else{
					gMemoriId = "" + generator.nextValue();
					gtfReport.setDummyGMemoriId(true);
				}
			} catch (NumberFormatException ne) {
				gMemoriId = "" + generator.nextValue();
				gtfReport.setDummyGMemoriId(true);
			}

			gtfReport.setgMemoryId(gMemoriId);

			gtfReports.add(gtfReport);
		}
		if (gtfReports.size() != 0) {
			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports, user.getCostCenter(),
					BudgetConstants.NEW);
		}
	}
}
