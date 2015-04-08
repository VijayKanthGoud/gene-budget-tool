package com.gene.app.action;

import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.gene.app.util.Util;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@SuppressWarnings("serial")
public class FactSheetUploadServlet extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(FactSheetUploadServlet.class.getName());
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();
	Map<String, ArrayList<GtfReport>> uploadWithOutPos = new HashMap<String, ArrayList<GtfReport>>();
	Map<String, ArrayList<GtfReport>> uploadedPOs = new HashMap<String, ArrayList<GtfReport>>();

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
		String costCentre = req.getParameter("costCenter");
		uploadedPOs = new HashMap();
		uploadWithOutPos = new HashMap();
		int fromLine = Integer.parseInt(req.getParameter("inputFrom"));
		int toLine = Integer.parseInt(req.getParameter("inputTo"));
		List<List<String>> rowList = new ArrayList();
		try {
			JSONArray jsonArray = new JSONArray(objarray);
			for (int count = fromLine-1; count < toLine; count++) {
				List list = new ArrayList();
				for (int k = 0; k < jsonArray.getJSONArray(count).length(); k++) {
					String varCol = jsonArray.getJSONArray(count).get(k)
							.toString();
					if (!varCol.equalsIgnoreCase("null")) {
						list.add(jsonArray.getJSONArray(count).get(k));
					} else {
						list.add("");
					}
				}
				if(Util.isNullOrEmpty(list.get(1).toString()) || Util.isNullOrEmpty(list.get(2).toString()) ||
						Util.isNullOrEmpty(list.get(3).toString()) || Util.isNullOrEmpty(list.get(4).toString()) ||
						Util.isNullOrEmpty(list.get(5).toString()) || Util.isNullOrEmpty(list.get(6).toString()) ||
						Util.isNullOrEmpty(list.get(7).toString()) || Util.isNullOrEmpty(list.get(8).toString()) ||
						Util.isNullOrEmpty(list.get(9).toString()) || Util.isNullOrEmpty(list.get(10).toString()) ){
					if(list.get(9).toString().indexOf("_")!= 6 && list.get(8).toString().equals("#") ||
							(!Util.isNullOrEmpty(list.get(9).toString())) && !Util.isNullOrEmpty(list.get(8).toString())){
						continue;
					}else{
						rowList.add(list);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		createGTFReports(user, user,rowList, gtfReports,costCentre);
	}

	private void createGTFReports(UserRoleInfo user,UserRoleInfo orgUser,
		List<List<String>> rowList, List<GtfReport> gtfReports,String costCentre) {
		Map<String,GtfReport> uniqueGtfRptMap = util.prepareUniqueGtfRptMap(costCentre);
		boolean isMultibrand = false;
		Map<String,UserRoleInfo> userMap = util.readAllUserInfo();
		List<GtfReport> removeGtfReports = new ArrayList<>();
		HashMap<String,String> removeGmemoriIds = new HashMap<>();
		Map<String, GtfReport> costCenterWiseGtfRptMap = util
				.getAllReportDataFromCache(costCentre);
		for (List recvdRow : rowList) {
			try{
				GtfReport gtfReport = new GtfReport();
				if (recvdRow.get(5) != null && !recvdRow.get(5).toString().equals("#")
						&& !recvdRow.get(5).toString().trim().equals("")) {
					gtfReport.setSubActivity(recvdRow.get(5).toString());
				} else {
					gtfReport.setSubActivity("");
					continue;
				}
				if (recvdRow.get(11) != null && !recvdRow.get(11).toString().equals("#")
						&& !recvdRow.get(11).toString().trim().equals("")) {
					if(util.readUserRoleInfoByFName(recvdRow.get(11).toString()) != null && 
							util.readUserRoleInfoByFName(recvdRow.get(11).toString()).getUserName() != null){
						gtfReport.setRequestor(util.readUserRoleInfoByFName(recvdRow.get(11).toString()).getUserName());
						gtfReport.setEmail(util.readUserRoleInfoByFName(recvdRow.get(11).toString()).getEmail());
					}else{
						gtfReport.setRequestor(orgUser.getUserName());
						gtfReport.setEmail(orgUser.getEmail());
					}
				} else {
					gtfReport.setRequestor(orgUser.getUserName());
					gtfReport.setEmail(orgUser.getEmail());
				}
				gtfReport.setCostCenter(costCentre);

				if (recvdRow.get(3) != null && !recvdRow.get(3).toString().equals("#")
						&& !recvdRow.get(3).toString().trim().equals("")) {
					gtfReport.setProject_WBS(recvdRow.get(3).toString());
				} else {
					gtfReport.setProject_WBS("");
				}

				if (recvdRow.get(4) != null && !recvdRow.get(4).toString().equals("#")
						&& !recvdRow.get(4).toString().toString().trim().equals("")) {
					gtfReport.setWBS_Name(recvdRow.get(4).toString());
				} else {
					gtfReport.setWBS_Name("");
				}



				if (recvdRow.get(6) != null && !recvdRow.get(6).toString().equals("#")
						&& !recvdRow.get(6).toString().trim().equals("")) {
					if("Total Products".equalsIgnoreCase(recvdRow.get(6).toString())){
						gtfReport.setBrand(gtfReport.getWBS_Name());	
					}else{
						gtfReport.setBrand(recvdRow.get(6).toString());
					}

				} else {
					gtfReport.setBrand("No brand");
				}

				if (recvdRow.get(7) != null && !recvdRow.get(7).toString().equals("#")
						&& !recvdRow.get(7).toString().trim().equals("")) {
					try {
						gtfReport.setPercent_Allocation(Double.parseDouble(recvdRow
								.get(7).toString()));
					} catch (NumberFormatException ne) {
						gtfReport.setPercent_Allocation(100);
					}
				} else {
					gtfReport.setPercent_Allocation(100);
				}

				if (recvdRow.get(8) != null && !recvdRow.get(8).toString().equals("#")
						&& !recvdRow.get(8).toString().trim().equals("")) {
					gtfReport.setPoNumber(recvdRow.get(8).toString());
					gtfReport.setStatus("Active");
					gtfReport.setFlag(2);
				} else {
					gtfReport.setPoNumber("");
					gtfReport.setStatus("New");
					gtfReport.setFlag(1);
				}

				if (recvdRow.get(9) != null && !recvdRow.get(9).toString().equals("#")
						&& !recvdRow.get(9).toString().trim().equals("")) {
					gtfReport.setPoDesc(recvdRow.get(9).toString());
				} else {
					gtfReport.setPoDesc("Not Available");
				}

				if (recvdRow.get(10) != null && !recvdRow.get(10).toString().equals("#")
						&& !recvdRow.get(10).toString().trim().equals("")) {
					gtfReport.setVendor(recvdRow.get(10).toString());
				} else {

					gtfReport.setVendor("");
				}

				/*if (recvdRow.get(8) != null && !recvdRow.get(8).equals("#")
					&& !recvdRow.get(8).toString().trim().equals("")) {
				gtfReport.setRequestor(util.readUserRoleInfoByFName(recvdRow.get(8).toString()).getUserName());
			} else {
				gtfReport.setRequestor("");
			}*/

				String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(Calendar.getInstance().getTime());
				gtfReport.setCreateDate(timeStamp);
				gtfReport.setYear(BudgetConstants.dataYEAR);
				gtfReport.setQual_Quant("Qual_Quant");
				gtfReport.setStudy_Side("study_Side");

				if(Util.isNullOrEmpty(recvdRow.get(26).toString()) && !"-".equalsIgnoreCase(recvdRow.get(26).toString().trim())){ 
					try{
						gtfReport.setUnits(Integer.parseInt(recvdRow.get(26).toString()));
					}catch(Exception e){
						gtfReport.setUnits(0);
					}
				}else{
					gtfReport.setUnits(0);
				}
				Map<String, Double> plannedMap = new HashMap<String, Double>();
				Map<String, Double> setZeroMap = new HashMap<String, Double>();
				for (int cnt = 0; cnt < BudgetConstants.months.length; cnt++) {
					setZeroMap.put(BudgetConstants.months[cnt], 0.0);
					try {

						if (recvdRow.get(cnt + 12) != null
								&& !recvdRow.get(cnt + 12).toString().trim()
								.equals("")) {
							String value = "0.0";
							if (recvdRow.get(cnt + 12).toString().contains("(")) {
								value = "-" + recvdRow.get(cnt + 12).toString().replaceAll("[^\\d.]", "");
							}else{
								value = recvdRow.get(cnt + 12).toString();
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
				gtfReport.setRemarks("   ");

				if (recvdRow.get(2) != null && !recvdRow.get(2).toString().equals("#")
						&& !recvdRow.get(2).toString().trim().equals("")) {
					gtfReport.setProjectName((recvdRow.get(2).toString()));
				} else {
					if(gtfReport.getPoDesc().indexOf("_")==6){
						gtfReport.setProjectName(gtfReport.getPoDesc().split("_")[1]);
					}else{
						gtfReport.setProjectName(gtfReport.getPoDesc());
					}
				}
				if(Util.isNullOrEmpty(gtfReport.getPoNumber())){
					gtfReport.setBrand(gtfReport.getWBS_Name());
				}

				// Update existing reports
				StringBuilder gtfParam = new StringBuilder("");
				if(Util.isNullOrEmpty(gtfReport.getBrand())){
					gtfParam = gtfParam.append(gtfReport.getBrand() + ":");
				}else{
					gtfParam = gtfParam.append(":");
				}
				if(Util.isNullOrEmpty(gtfReport.getRequestor())){
					gtfParam = gtfParam.append(gtfReport.getRequestor() + ":");
				}else{
					gtfParam = gtfParam.append(":");
				}
				if(Util.isNullOrEmpty(gtfReport.getProjectName())){
					gtfParam = gtfParam.append(gtfReport.getProjectName());
				}
				GtfReport gtfRpt = uniqueGtfRptMap.get(gtfParam.toString());

				// Update if gmemori id already exists else create a new gmemori id either from poDesc or generate new
				if(gtfRpt != null){
					removeGtfReports.add(gtfRpt);
					gtfReport.setgMemoryId(gtfRpt.getgMemoryId());
				}else{
					String gMemoriId;
					try {
						if(gtfReport.getPoDesc().indexOf("_")==6){
							gMemoriId = Integer.parseInt(gtfReport.getPoDesc().substring(0,
									Math.min(gtfReport.getPoDesc().length(), 6)))
									+ "";
							gtfReport.setDummyGMemoriId(false);
							gtfReport.setProjectName(gtfReport.getPoDesc().split("_")[1]);
						}else{
							gMemoriId = "" + generator.nextValue();
							gtfReport.setDummyGMemoriId(true);
						}
					} catch (NumberFormatException ne) {
						gMemoriId = "" + generator.nextValue();
						gtfReport.setDummyGMemoriId(true);
					}

					gtfReport.setgMemoryId(gMemoriId);
				}



				if(Util.isNullOrEmpty(gtfReport.getPoNumber())){
					ArrayList<GtfReport> poUpdated = new ArrayList<>();
					if (uploadedPOs.get(gtfReport.getPoNumber()) != null) {
						poUpdated = uploadedPOs.get(gtfReport.getPoNumber());
					}
					poUpdated.add(gtfReport);

					uploadedPOs.put(gtfReport.getPoNumber(), poUpdated);
				}else{

					ArrayList<GtfReport> noPoUpdated = new ArrayList<>();
					if(uploadWithOutPos.get(gtfReport.getProjectName())!=null){
						noPoUpdated = uploadWithOutPos.get(gtfReport.getProjectName());
						noPoUpdated.add(gtfReport);
						uploadWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);

					}else{
						noPoUpdated.add(gtfReport);
						uploadWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);
					}
				}
			}catch(Exception e){
				System.out.println(recvdRow);
			}
		}

		changeForMultiBrand(uploadedPOs, gtfReports,costCenterWiseGtfRptMap);
		changeForMultiBrand(uploadWithOutPos, gtfReports,costCenterWiseGtfRptMap);
		
		if (gtfReports!=null && !gtfReports.isEmpty() && gtfReports.size() != 0) {
			if(removeGtfReports!=null && !removeGtfReports.isEmpty() && removeGtfReports.size() >0){
				util.removeExistingProject(removeGtfReports);
				util.storeProjectsToCache(removeGtfReports,costCentre, BudgetConstants.OLD);
			}
			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports, costCentre,
					BudgetConstants.NEW);

		}
	}

	private void changeForMultiBrand(Map<String, ArrayList<GtfReport>> uploadedPOs, List<GtfReport> gtfReports,Map<String, GtfReport> costCenterWiseGtfRptMap) {
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		Map<String, Double> plannedMap = null;
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
		}
		for (Entry<String, ArrayList<GtfReport>> entry : uploadedPOs.entrySet())
		{
		    ArrayList<GtfReport> receivedGtfReports = entry.getValue();
		    if(receivedGtfReports.size() > 1 || receivedGtfReports.get(0).getProject_WBS().trim().startsWith("421")) {
		    	GtfReport nwParentGtfReport = new GtfReport();
		    	ArrayList<String> childProjList = new ArrayList<String>();
		    	try {
					nwParentGtfReport = (GtfReport) receivedGtfReports.get(0).clone();
					if(costCenterWiseGtfRptMap!=null && costCenterWiseGtfRptMap.get(nwParentGtfReport.getgMemoryId())!=null){
						for(String cList : costCenterWiseGtfRptMap.get(nwParentGtfReport.getgMemoryId()).getChildProjectList()){
							if (!cList.contains(".")) {
								if(costCenterWiseGtfRptMap.get(cList)!=null ){
									nwParentGtfReport = costCenterWiseGtfRptMap.get(cList);
								}
								break;
							}
						}
					}
					nwParentGtfReport.setPlannedMap(setZeroMap);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
		    	plannedMap = new HashMap(setZeroMap);
		    	String gMemoriId = nwParentGtfReport.getgMemoryId();
		    	int count = 1;
				double total = 0.0;
				if (gMemoriId.contains(".")) {
					gMemoriId = gMemoriId.split("\\.")[0];

				}
		    	childProjList.add(gMemoriId);
		    	for(GtfReport gtfRpt : receivedGtfReports){
		    		Map<String, Double> receivedChildMap = new HashMap(gtfRpt.getPlannedMap());
		    		for (Entry<String, Double> entryMap : receivedChildMap.entrySet()){
		    			plannedMap.put(entryMap.getKey(), plannedMap.get(entryMap.getKey()) + entryMap.getValue());
		    		}
		    		nwParentGtfReport.setPlannedMap(plannedMap);
		    		gtfRpt.setgMemoryId(gMemoriId +"."+ (count));
					childProjList.add(gMemoriId + "." + (count++));
		    		//childProjList.add(gMemoriId +"."+ (count++));
		    	}
		    	for(GtfReport gtfRpt : receivedGtfReports){
		    		total += gtfRpt.getPlannedMap().get("TOTAL");
		    	}
		    	nwParentGtfReport.setChildProjectList(childProjList);
		    	nwParentGtfReport.setBrand("Smart WBS");
		    	nwParentGtfReport.setRemarks("   ");
		    	nwParentGtfReport.setBenchmarkMap(nwParentGtfReport.getPlannedMap());
		    	receivedGtfReports.add(nwParentGtfReport);
		    	for(GtfReport gtfRpt : receivedGtfReports){
		    		gtfRpt.setMultiBrand(true);
		    		gtfRpt.setChildProjectList(childProjList);
		    		try{
					if (gtfRpt.getgMemoryId().contains(".")) {
						gtfRpt.setPercent_Allocation(Util.roundDoubleValue((gtfRpt.getPlannedMap()
								.get("TOTAL") / total) * 100 , 2));
					}}catch(NumberFormatException nfe){
						gtfRpt.setPercent_Allocation(100.0);
					}catch(ArithmeticException ae){
						gtfRpt.setPercent_Allocation(100.0);
					}
		    		gtfReports.add(gtfRpt);
		    		
		    	}
		    	//uploadedPOs.put(entry.getKey(), receivedGtfReports);
		    }else{
		    	try {
					gtfReports.add((GtfReport) receivedGtfReports.get(0).clone());
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
	}
	
	
}
