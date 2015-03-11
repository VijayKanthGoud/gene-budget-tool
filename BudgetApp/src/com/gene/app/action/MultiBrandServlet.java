package com.gene.app.action;

import static com.gene.app.util.Util.roundDoubleValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class MultiBrandServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger
			.getLogger(MultiBrandServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Inside MultiBrandServlet...");
		String m_data = req.getParameter("objarray").toString();
		LOGGER.log(Level.INFO, "Received from client m_data : " + m_data);
		String sumTotal = req.getParameter("sumTotal")
				.toString();
		String costCenter = req.getParameter("costCenter").toString();
		LOGGER.log(Level.INFO, "sumTotal : " + sumTotal);
		String project_id = "";
		String projectName = "";
		String projectOwner = "";
		String brand = "";
		String totalValue = "";
		String gMemoriId = "";
		String childgMemoriId = "";
		Double percentageAllocation = 100.0;
		DBUtil util = new DBUtil();
		HttpSession session = req.getSession();
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		String email = user.getEmail();
		LOGGER.log(Level.INFO, "User email is : " + email);
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		// List containing the project and all its sub-projects retrieved
		List<GtfReport> oldGtfReportList = new ArrayList<GtfReport>();
		if (user == null || user.getEmail().equalsIgnoreCase("")) {
			UserService userService = UserServiceFactory.getUserService();
			email = userService.getCurrentUser().getEmail();
			user = util.readUserInfoFromDB(email);
			LOGGER.log(Level.INFO,
					"Issue in directly reading email, retrieved from UserService : "
							+ email);
		}
		user.setSelectedCostCenter(costCenter);
		try {
			JSONArray jsonArray = new JSONArray(m_data);
			JSONObject rptObject = jsonArray.getJSONObject(0);
			projectName = rptObject.getString("4");
			gMemoriId = rptObject.getString("5").split("\\.")[0];
			JSONObject rprtObject = null;
			Map<String, GtfReport> rptList = util.getAllReportsByPrjName(
					user.getSelectedCostCenter(), projectName, email,gMemoriId);
			for (Map.Entry<String, GtfReport> rptMap : rptList.entrySet()) {
				oldGtfReportList.add(rptMap.getValue());
			}
			// List containing all the new projects to be created
			List<GtfReport> masterGtfReportList = new ArrayList<GtfReport>();
			GtfReport gtfRpt = null;
			Map<String, Double> plannedMap = new LinkedHashMap<String, Double>();
			Map<String, Double> parentPlannedMap = new LinkedHashMap<String, Double>();
			Double value = 0.0;
			String prj_owner_email = "";
			Map<String, Double> setZeroMap = new HashMap<String, Double>();
			for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
				setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			}
			ArrayList<String> gmultiIdList= new ArrayList<>();
			gmultiIdList.add(gMemoriId);
			// Inserts parent
			for (int par = 0; par < oldGtfReportList.size(); par++) {
				gtfRpt = oldGtfReportList.get(par);
				GtfReport paretnGtfReport = new GtfReport();
				if (gtfRpt.getgMemoryId().equalsIgnoreCase(gMemoriId)) {
					LOGGER.log(Level.INFO, "Parent project is : " + gMemoriId);
					paretnGtfReport.setCreateDate(gtfRpt.getCreateDate());
					paretnGtfReport.setYear(gtfRpt.getYear());
					paretnGtfReport.setgMemoryId(gtfRpt.getgMemoryId());
					paretnGtfReport.setBrand("Smart WBS");
					paretnGtfReport.setEmail(gtfRpt.getEmail());
					paretnGtfReport.setFlag(gtfRpt.getFlag());
					paretnGtfReport.setMultiBrand(true);
					paretnGtfReport.setPercent_Allocation(100.0);
					paretnGtfReport.setPoDesc(gtfRpt.getPoDesc());
					paretnGtfReport.setPoNumber(gtfRpt.getPoNumber());
					paretnGtfReport.setProject_WBS(gtfRpt.getProject_WBS());
					paretnGtfReport.setRemarks(gtfRpt.getRemarks());
					paretnGtfReport.setRequestor(gtfRpt.getRequestor());
					paretnGtfReport.setStatus(gtfRpt.getStatus());
					paretnGtfReport.setSubActivity(gtfRpt.getSubActivity());
					paretnGtfReport.setVendor(gtfRpt.getVendor());
					paretnGtfReport.setUnits(gtfRpt.getUnits());
					paretnGtfReport.setWBS_Name(gtfRpt.getWBS_Name());
					paretnGtfReport.setProjectName(projectName);
					paretnGtfReport.setBenchmarkMap(gtfRpt.getBenchmarkMap());
					plannedMap = gtfRpt.getPlannedMap();
					plannedMap.put(BudgetConstants.total,
							roundDoubleValue(Double.parseDouble(sumTotal), 2));
					parentPlannedMap = new LinkedHashMap<String, Double>(
							plannedMap);
					paretnGtfReport.setPlannedMap(plannedMap);
					paretnGtfReport.setAccrualsMap(gtfRpt.getAccrualsMap());
					paretnGtfReport.setVariancesMap(gtfRpt.getVariancesMap());
					paretnGtfReport.setPercent_Allocation(percentageAllocation);
					paretnGtfReport.setCostCenter(user.getSelectedCostCenter());
					masterGtfReportList.add(paretnGtfReport);
					LOGGER.log(Level.INFO, "New parent created : "
							+ paretnGtfReport.getgMemoryId());
					break;
				}
			}

			// Loop for child projects
			for (int i = 0; i < jsonArray.length(); i++) {
				rprtObject = jsonArray.getJSONObject(i);
				if ("".equals(rprtObject.getString("3").trim())) {
					break;
				}
				project_id = rprtObject.getString("0");
				projectName = rprtObject.getString("4");
				brand = rprtObject.getString("1");
				projectOwner = rprtObject.getString("7");
				totalValue = rprtObject.getString("3");
				childgMemoriId = rprtObject.getString("5");
				percentageAllocation = Double.parseDouble(rprtObject
						.getString("2"));
				try {
					percentageAllocation = Double.parseDouble(rprtObject
							.getString("2").trim());
				} catch (Exception e) {
					percentageAllocation = 0.0;
				}
				
				for (int j = 0; j < oldGtfReportList.size(); j++) {
					gtfRpt = oldGtfReportList.get(j);
					if (project_id != null && !"".equals(project_id.trim())
							&& project_id.equalsIgnoreCase(gtfRpt.getId())) {
						GtfReport childGtfReport = new GtfReport();
						plannedMap = new LinkedHashMap<>();
						plannedMap = gtfRpt.getPlannedMap();
						childGtfReport.setgMemoryId(gtfRpt.getgMemoryId());
						gmultiIdList.add(gtfRpt.getgMemoryId());
						childGtfReport.setBrand(gtfRpt.getBrand());
						childGtfReport.setEmail(gtfRpt.getEmail());
						childGtfReport.setFlag(gtfRpt.getFlag());
						childGtfReport.setMultiBrand(true);
						childGtfReport.setCreateDate(gtfRpt.getCreateDate());
						childGtfReport.setYear(gtfRpt.getYear());
						childGtfReport
								.setPercent_Allocation(percentageAllocation);
						childGtfReport.setPoDesc(gtfRpt.getPoDesc());
						childGtfReport.setPoNumber(gtfRpt.getPoNumber());
						childGtfReport.setProject_WBS(gtfRpt.getProject_WBS());
						String remarks = gtfRpt.getRemarks();
						if (remarks.contains("\"")) {
							remarks = remarks.replace("\\", "\\\\")
									.replace("\"", "\\\"")
									.replace("\'", "\\\'");
						}
						childGtfReport.setRemarks(remarks);
						childGtfReport.setRequestor(projectOwner + ":"
								+ user.getUserName());
						childGtfReport.setStatus(gtfRpt.getStatus());
						childGtfReport.setSubActivity(gtfRpt.getSubActivity());
						childGtfReport.setVendor(gtfRpt.getVendor());
						childGtfReport.setUnits(gtfRpt.getUnits());
						childGtfReport.setWBS_Name(gtfRpt.getWBS_Name());
						childGtfReport.setBenchmarkMap(gtfRpt.getBenchmarkMap());

						childGtfReport.setAccrualsMap(gtfRpt.getAccrualsMap());
						childGtfReport.setVariancesMap(gtfRpt.getVariancesMap());
						childGtfReport.setProjectName(projectName);
						childGtfReport.setCostCenter(user.getSelectedCostCenter());
						for (int cnt = 0; cnt < BudgetConstants.months.length - 1; cnt++) {
							setZeroMap.put(BudgetConstants.months[cnt], 0.0);
							try {
								value = roundDoubleValue(
										parentPlannedMap.get(BudgetConstants.months[cnt])
												* percentageAllocation / 100,
										2);
								plannedMap.put(BudgetConstants.months[cnt],
										value);
							} catch (Exception e) {
								plannedMap
										.put(BudgetConstants.months[cnt], 0.0);
							}
						}
						plannedMap
								.put(BudgetConstants.months[BudgetConstants.months.length - 1],
										Double.parseDouble(totalValue));
						childGtfReport.setPlannedMap(plannedMap);
						childGtfReport
								.setPercent_Allocation(percentageAllocation);
						masterGtfReportList.add(childGtfReport);
						break;
					}
					// newly added sub-projects
					else if (project_id != null && "".equals(project_id.trim())) {
						GtfReport newChildGtfReport = new GtfReport();
						newChildGtfReport.setBrand(brand);
						plannedMap = new LinkedHashMap<String, Double>(
								setZeroMap);
						newChildGtfReport.setgMemoryId(childgMemoriId);
						gmultiIdList.add(childgMemoriId);
						newChildGtfReport.setCreateDate(timeStamp);
						newChildGtfReport.setYear(BudgetConstants.dataYEAR);
						prj_owner_email = util.getPrjEmailByName(projectOwner);
						newChildGtfReport.setRequestor(projectOwner + ":"
								+ user.getUserName());
						email = gtfRpt.getEmail();
						newChildGtfReport.setEmail(prj_owner_email + ":" + email);
						newChildGtfReport.setFlag(gtfRpt.getFlag());
						newChildGtfReport.setMultiBrand(true);
						newChildGtfReport.setPercent_Allocation(percentageAllocation);
						newChildGtfReport.setPoDesc(gtfRpt.getPoDesc());
						newChildGtfReport.setPoNumber(gtfRpt.getPoNumber());
						newChildGtfReport.setProject_WBS(gtfRpt.getProject_WBS());
						String remarks = gtfRpt.getRemarks();
						if (remarks.contains("\"")) {
							remarks = remarks.replace("\\", "\\\\")
									.replace("\"", "\\\"")
									.replace("\'", "\\\'");
						}
						newChildGtfReport.setRemarks(remarks);
						newChildGtfReport.setStatus(gtfRpt.getStatus());
						newChildGtfReport.setSubActivity(gtfRpt.getSubActivity());
						newChildGtfReport.setVendor(gtfRpt.getVendor());
						newChildGtfReport.setUnits(gtfRpt.getUnits());
						newChildGtfReport.setWBS_Name(gtfRpt.getWBS_Name());
						newChildGtfReport.setPlannedMap(setZeroMap);
						newChildGtfReport.setAccrualsMap(setZeroMap);
						newChildGtfReport.setVariancesMap(setZeroMap);
						newChildGtfReport.setBenchmarkMap(setZeroMap);
						newChildGtfReport.setCostCenter(user.getSelectedCostCenter());
						Double per_allocation = newChildGtfReport
								.getPercent_Allocation();
						newChildGtfReport.setProjectName(projectName);
						if (per_allocation == 0.0) {
							per_allocation = 1.0;
						}
						for (int cnt = 0; cnt < BudgetConstants.months.length - 1; cnt++) {
							setZeroMap.put(BudgetConstants.months[cnt], 0.0);
							try {
								value = roundDoubleValue(
										parentPlannedMap.get(BudgetConstants.months[cnt])
												* percentageAllocation / 100,
										2);
								plannedMap.put(BudgetConstants.months[cnt],
										value);
							} catch (NumberFormatException e) {
								plannedMap
										.put(BudgetConstants.months[cnt], 0.0);
							}
						}
						plannedMap
								.put(BudgetConstants.months[BudgetConstants.months.length - 1],
										Double.parseDouble(totalValue));
						newChildGtfReport.setPlannedMap(plannedMap);
						masterGtfReportList.add(newChildGtfReport);
						break;
					}
				}

			}
			
			for(String gmemId: gmultiIdList){
				for(GtfReport gtf : masterGtfReportList){
					if(gmemId.equalsIgnoreCase(gtf.getgMemoryId())){
						gtf.setChildProjectList(gmultiIdList);
					}
				}
			}
			
			
			LOGGER.log(Level.INFO,
					"Number of reports removed from the datastore : "
							+ oldGtfReportList.size());
			util.removeExistingProject(oldGtfReportList);
			util.storeProjectsToCache(oldGtfReportList,user.getSelectedCostCenter(), BudgetConstants.OLD);
			LOGGER.log(Level.INFO,
					"Number of reports new report(s) inserted in to the datastore : "
							+ masterGtfReportList.size());
			util.generateProjectIdUsingJDOTxn(masterGtfReportList);
			util.storeProjectsToCache(masterGtfReportList,user.getSelectedCostCenter(),BudgetConstants.NEW);
			session.setAttribute("userInfo",user);
		} catch (JSONException e) {
			LOGGER.log(Level.SEVERE, "JSONException caught in Multibrand Servlet :" + e);
		}
	}
}
