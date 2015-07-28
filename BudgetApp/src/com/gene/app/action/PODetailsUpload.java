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
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.ProjectSequenceGeneratorUtil;
import com.gene.app.util.Util;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@SuppressWarnings("serial")
public class PODetailsUpload extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(PODetailsUpload.class
			.getName());
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();
	String brand = "";
	String PM = "";
	//String WBS = "";
	//String WBSName = "";
	/** The is multibrand. */
	boolean isMultibrand = false;
	int curMonth =0;

	Map<String, GtfReport> uploadedGMems = new HashMap<String, GtfReport>();
	Map<String, ArrayList<GtfReport>> uploadedPOs = new HashMap<String, ArrayList<GtfReport>>();
	Map<String, ArrayList<GtfReport>> uploadedWithOutPos = new HashMap<String, ArrayList<GtfReport>>();

	Map<String, ArrayList<GtfReport>> addedPOs = new HashMap<String, ArrayList<GtfReport>>();
	Map<String, ArrayList<GtfReport>> addedWithOutPos = new HashMap<String, ArrayList<GtfReport>>();

	Map<String, ArrayList<GtfReport>> updateddPOs = new HashMap<String, ArrayList<GtfReport>>();
	Map<String, ArrayList<GtfReport>> updatedWithOutPos = new HashMap<String, ArrayList<GtfReport>>();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "inside fileupload...");
		 final String url = req.getRequestURL().toString();
         final String baseURL = url.substring(0, url.length()
                            - req.getRequestURI().length())
                            + req.getContextPath() + "/";
		HttpSession session = req.getSession();
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		if(user != null && !user.getRole().equalsIgnoreCase("admin")){
			resp.sendError(411, "User doesn't have permission to upload.");
			return;
		}
		String costCenter = req.getParameter("costCenter");
		int startRow = Integer.parseInt(req.getParameter("inputFrom"));
		int endRow = Integer.parseInt(req.getParameter("inputTo"));
		String objArray = req.getParameter(BudgetConstants.objArray).toString();
		List<List<String>> rowList = new ArrayList();
		uploadedPOs = new HashMap<String, ArrayList<GtfReport>>();
		uploadedGMems = new HashMap<String, GtfReport>();
		uploadedWithOutPos = new HashMap<String, ArrayList<GtfReport>>();
		addedPOs = new HashMap<String, ArrayList<GtfReport>>();
		addedWithOutPos = new HashMap<String, ArrayList<GtfReport>>();
		updateddPOs = new HashMap<String, ArrayList<GtfReport>>();
		updatedWithOutPos = new HashMap<String, ArrayList<GtfReport>>();
		curMonth = Calendar.getInstance().get(Calendar.MONTH);
		try {
			JSONArray jsonArray = new JSONArray(objArray);
			//endRow = endRow < jsonArray.length()? endRow : jsonArray.length();
			for (int count = startRow - 1; count < endRow; count++) {
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
				rowList.add(list);
			}
			List<GtfReport> gtfReports = new ArrayList<GtfReport>();
			Map<String, GtfReport> costCenterWiseGtfRptMap = new LinkedHashMap<String, GtfReport>();
			costCenterWiseGtfRptMap = util
					.getAllReportDataFromCache(costCenter);
			createOrUpdateGTFReports(user, rowList, gtfReports,
					costCenterWiseGtfRptMap, costCenter,baseURL);
		} catch (JSONException exception) {
			System.out.println(exception + "");
			LOGGER.log(Level.SEVERE,
					"ERROR OCCURED DURING  FILE UPLOAD. ERROR DETAILS : "
							+ exception);
		}
	}

	private void createOrUpdateGTFReports(UserRoleInfo user,
			List<List<String>> rowList, List<GtfReport> gtfReports,
			Map<String, GtfReport> costCenterWiseGtfRptMap, String costCentre,String baseURL) {
		Set<String> completeGMemoriIds = costCenterWiseGtfRptMap.keySet();

		Map<String, GtfReport> poMap = util
				.preparePOMap(costCenterWiseGtfRptMap);
		Map<String, GtfReport> projNameMap = util
				.prepareProjectNameMap(costCenterWiseGtfRptMap);
		
		for (List rcvdRow : rowList) {
			try{
			if(rcvdRow.get(1).toString().trim().isEmpty() && 
					rcvdRow.get(3).toString().trim().isEmpty() && 
					rcvdRow.get(5).toString().trim().isEmpty() && 
					rcvdRow.get(6).toString().trim().isEmpty()){
				continue;
			}
			String receivedGmemoriId = "";
			String poGmemoriId = "";
			if (rcvdRow.get(1).toString().contains(" Total")) {
				continue;
			} else if (!rcvdRow.get(1).toString().trim().equals("")) {
				brand = rcvdRow.get(1).toString();
			}

			if (rcvdRow.get(2).toString().contains(" Total")) {
				continue;
			} else if (!rcvdRow.get(2).toString().trim().equals("")) {
				PM = rcvdRow.get(2).toString();
			}

			/*if (!rcvdRow.get(3).toString().trim().equals("")) {
				WBS = rcvdRow.get(3).toString();
			}*/
/*
			if (!rcvdRow.get(4).toString().trim().equals("")) {
				WBSName = rcvdRow.get(4).toString();
			}*/

			// check gmemory id splitting PO Desc
			try {
				if (rcvdRow.get(6).toString().indexOf("_") == 6) {
					receivedGmemoriId = Integer.parseInt(rcvdRow
							.get(6)
							.toString()
							.substring(
									0,
									Math.min(
											rcvdRow.get(6).toString().length(),
											6)))
											+ "";
				}
			} catch (NumberFormatException ne) {
				// continue rest of the code
			}

			// Check by matching PO number
			if (!Util.isNullOrEmpty(receivedGmemoriId)
					&& Util.isNullOrEmpty(rcvdRow.get(5).toString())) {
				if (poMap.get(rcvdRow.get(5).toString()) != null) {
					receivedGmemoriId = poMap.get(rcvdRow.get(5).toString())
							.getgMemoryId();
					if (receivedGmemoriId.contains(".")) {
						receivedGmemoriId = receivedGmemoriId.split("\\.")[0];

					}
				}
			}else{/*
				if(Util.isNullOrEmpty(rcvdRow.get(5).toString())){
					if (poMap.get(rcvdRow.get(5).toString()) != null) {
						poGmemoriId = poMap.get(rcvdRow.get(5).toString())
								.getgMemoryId();
						if (poGmemoriId.contains(".")) {
							poGmemoriId = poGmemoriId.split("\\.")[0];

						}
						System.out.println("poGmemoriId is"+poGmemoriId);
						if(costCenterWiseGtfRptMap.get(poGmemoriId)!=null ){
							if(costCenterWiseGtfRptMap.get(poGmemoriId).getMultiBrand()){
								ArrayList<String> childGmems = new ArrayList<>();
								for(String cGmemId: costCenterWiseGtfRptMap.get(poGmemoriId).getChildProjectList()){
									GtfReport cGtf = costCenterWiseGtfRptMap.get(cGmemId);
									if(cGtf!=null){
										System.out.println("cGtf is"+cGtf.getgMemoryId() + "cGmemId:::"+cGmemId);
									costCenterWiseGtfRptMap.remove(cGmemId);
									String newGmemId = "";
									if (cGmemId.contains(".")) {
									newGmemId =receivedGmemoriId+"."+cGmemId.split("\\.")[1];
									}else{
										newGmemId =receivedGmemoriId;
									}
									childGmems.add(newGmemId);
									cGtf.setgMemoryId(newGmemId);	
									costCenterWiseGtfRptMap.put(newGmemId, cGtf);
									}
								}
								for(String cGmemId: costCenterWiseGtfRptMap.get(poGmemoriId).getChildProjectList()){
									costCenterWiseGtfRptMap.get(cGmemId).setChildProjectList(childGmems);;
								}
							}else{
								GtfReport cGtf = costCenterWiseGtfRptMap.get(poGmemoriId);
								costCenterWiseGtfRptMap.remove(poGmemoriId);
								cGtf.setgMemoryId(receivedGmemoriId);	
								costCenterWiseGtfRptMap.put(receivedGmemoriId, cGtf);
							}
						}
					}
				}
			*/}
			
			// Check by matching Project name number
			if (!Util.isNullOrEmpty(receivedGmemoriId)
					&& Util.isNullOrEmpty(rcvdRow.get(6).toString())) {
				if (projNameMap.get(rcvdRow.get(6).toString()) != null) {
					receivedGmemoriId = projNameMap.get(rcvdRow.get(6).toString())
							.getgMemoryId();
					if (receivedGmemoriId.contains(".")) {
						receivedGmemoriId = receivedGmemoriId.split("\\.")[0];

					}
				}
			}
			//util.saveAllReportDataToCache(costCentre, costCenterWiseGtfRptMap);
			// If multibrand
			boolean newCreated = false;
			String pGmemId = new String(receivedGmemoriId);
			if (!"".equalsIgnoreCase(receivedGmemoriId)
					&& (completeGMemoriIds.contains(receivedGmemoriId))) {
				GtfReport receivedGtfReport = new GtfReport();
				if (costCenterWiseGtfRptMap.get(receivedGmemoriId)
						.getMultiBrand()) {
					for (String chldgMemId : costCenterWiseGtfRptMap.get(
							receivedGmemoriId).getChildProjectList()) {
						if (costCenterWiseGtfRptMap.get(chldgMemId) != null
								&& costCenterWiseGtfRptMap.get(chldgMemId)
								.getBrand().equalsIgnoreCase(brand)) {
							receivedGmemoriId = costCenterWiseGtfRptMap.get(
									chldgMemId).getgMemoryId();
							receivedGtfReport = costCenterWiseGtfRptMap
									.get(receivedGmemoriId);
							break;
						}
					}
					if (receivedGtfReport.getgMemoryId() == null) {
						createNewReport(user, rcvdRow, gtfReports, costCentre,
								receivedGmemoriId,
								costCenterWiseGtfRptMap.get(receivedGmemoriId));
						newCreated = true;
					}

				} else {
					receivedGtfReport = costCenterWiseGtfRptMap
							.get(receivedGmemoriId);
				}
				if (!newCreated) {
					updateAccrual(rcvdRow, user, gtfReports, receivedGtfReport,
							costCentre, costCenterWiseGtfRptMap.get(pGmemId),
							costCenterWiseGtfRptMap);
				}
			} else {
				createNewReport(user, rcvdRow, gtfReports, costCentre,
						receivedGmemoriId, null);
			}
		}
		catch(Exception e){
			System.out.println(rcvdRow);
		}
		}
		changeForNewMultiBrand(uploadedPOs, gtfReports, costCenterWiseGtfRptMap);
		changeForNewMultiBrand(uploadedWithOutPos, gtfReports,
				costCenterWiseGtfRptMap);
		changeForAddedMultiBrand(addedPOs, gtfReports, costCenterWiseGtfRptMap);
		changeForAddedMultiBrand(addedWithOutPos, gtfReports,
				costCenterWiseGtfRptMap);
		util.generateProjectIdUsingJDOTxn(gtfReports,"",baseURL,costCentre);
		//util.storeProjectsToCache(null,gtfReports, costCentre);
	}

	private void updateAccrual(List rcvdRow, UserRoleInfo user,
			List<GtfReport> gtfReports, GtfReport receivedGtfReport,
			String costCenter, GtfReport pGtfReport,Map<String, GtfReport> costCenterWiseGtfRptMap) {
		
		boolean isMultiBrand = false;	
		if(pGtfReport!=null && pGtfReport.getChildProjectList()!=null && pGtfReport.getMultiBrand()){

			for(String chldgMemId : pGtfReport.getChildProjectList()){
				if (costCenterWiseGtfRptMap.get(chldgMemId) != null
						&& costCenterWiseGtfRptMap.get(chldgMemId)
						.getBrand().equalsIgnoreCase(brand)) {
					receivedGtfReport = costCenterWiseGtfRptMap.get(chldgMemId);
					break;
				}
			}
			isMultiBrand = true;
		}

		receivedGtfReport.setCostCenter(costCenter);
		//receivedGtfReport.setBrand(brand);
		//receivedGtfReport.setRequestor(util.readUserRoleInfoByFName(PM).getUserName());
		receivedGtfReport.setProject_WBS(rcvdRow.get(3).toString());
		receivedGtfReport.setWBS_Name(rcvdRow.get(4).toString());
		if(isMultiBrand && !Util.isNullOrEmpty(rcvdRow.get(5).toString()) && Util.isNullOrEmpty(pGtfReport.getPoNumber())){
			receivedGtfReport.setPoNumber(pGtfReport.getPoNumber());
			receivedGtfReport.setStatus("Active");
			receivedGtfReport.setFlag(2);
			
		}else if(!Util.isNullOrEmpty(receivedGtfReport.getPoNumber()) && Util.isNullOrEmpty(rcvdRow.get(5).toString())){
			receivedGtfReport.setPoNumber(rcvdRow.get(5).toString());
			receivedGtfReport.setStatus("Active");
			receivedGtfReport.setFlag(2);
		}else if(!Util.isNullOrEmpty(receivedGtfReport.getPoNumber())){
			receivedGtfReport.setRemarks("Error with project data : PO# is blank");
		}
			
		receivedGtfReport.setPoDesc(rcvdRow.get(6).toString());
		receivedGtfReport.setVendor(rcvdRow.get(7).toString());
		String poDesc = receivedGtfReport.getPoDesc();
		if(receivedGtfReport.getProjectName() != null && receivedGtfReport.getProjectName().trim().equalsIgnoreCase("")){
			receivedGtfReport.setProjectName(poDesc);
		}
		//receivedGtfReport.setSubActivity("");
		Map<String, Double> receivedAccrualMap = receivedGtfReport
				.getAccrualsMap();
		Map<String, Double> receivedPlannedMap = receivedGtfReport
				.getPlannedMap();

		Map<String, Double> receivedParentAccrualMap = new HashMap();
		Map<String, Double> receivedParentPlannedMap = new HashMap();
		if(isMultiBrand){
			receivedParentAccrualMap = pGtfReport.getAccrualsMap();
			receivedParentPlannedMap = pGtfReport.getPlannedMap();
		}
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			try {
				String val = "";
				String rcvdStr = rcvdRow.get(cnt + 8).toString();
				if( rcvdStr.contains("(")){
					val = "-" + rcvdStr.substring(rcvdStr.indexOf("(")+1, rcvdStr.indexOf(")"));
				}else{
					val = rcvdStr;
				}
				double prevVal = receivedAccrualMap.get(BudgetConstants.months[cnt]);
				double prevPlannedVal = receivedPlannedMap.get(BudgetConstants.months[cnt]);
				
				receivedAccrualMap.put(
						BudgetConstants.months[cnt],
						roundDoubleValue(
								Double.parseDouble(val), 5));
				
				// updates the future month values
				if(cnt <=  curMonth){
					receivedPlannedMap.put(
							BudgetConstants.months[cnt],
							roundDoubleValue(
									Double.parseDouble(val), 5));
				}
				
				if(isMultiBrand){
					receivedParentAccrualMap.put(BudgetConstants.months[cnt],
							receivedParentAccrualMap.get(BudgetConstants.months[cnt]) - prevVal + roundDoubleValue(
									Double.parseDouble(val), 5));
					
					// updates the future month values
					if(cnt <=  curMonth){
						receivedParentPlannedMap.put(BudgetConstants.months[cnt],
							receivedParentPlannedMap.get(BudgetConstants.months[cnt]) - prevPlannedVal + roundDoubleValue(
									Double.parseDouble(val), 5));
					}
					
				}
			} catch (NumberFormatException e1) {
				receivedAccrualMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}

		uploadedGMems.put(receivedGtfReport.getgMemoryId(), receivedGtfReport);
		gtfReports.add(receivedGtfReport);
		if(isMultiBrand){
			gtfReports.add(pGtfReport);
		}
	}

	private void createNewReport(UserRoleInfo user, List rcvdRow,
			List<GtfReport> gtfReports, String costCentre , String systemGMem, GtfReport pGtf ) {
		String pUserId ="";
		String eMailId="";

		GtfReport gtfReport = new GtfReport();
		String gMemoriId = "";
		gtfReport.setMultiBrand(false);
		
		// no parent gtf report
		if(pGtf==null){
			try {
				gtfReport.setPoDesc(rcvdRow.get(6).toString());
				if(rcvdRow.get(6).toString().indexOf("_")==6){

					gMemoriId = Integer.parseInt(gtfReport.getPoDesc().substring(0,
							Math.min(gtfReport.getPoDesc().length(), 6)))
							+ "";
					gtfReport.setDummyGMemoriId(false);
					gtfReport.setProjectName(gtfReport.getPoDesc().split("_")[1]);
				}else{
					gMemoriId = "" + generator.nextValue();
					gtfReport.setDummyGMemoriId(true);
					gtfReport.setProjectName(gtfReport.getPoDesc());
				}
			} catch (NumberFormatException ne) {
				gMemoriId = "" + generator.nextValue();
				gtfReport.setDummyGMemoriId(true);
				gtfReport.setProjectName(gtfReport.getPoDesc());
			}
		}else{
			gMemoriId = pGtf.getgMemoryId();
			gtfReport.setProjectName(pGtf.getProjectName());
		}
		gtfReport.setCostCenter(costCentre);
		if(Util.isNullOrEmpty(util.readUserRoleInfoByFName(PM).getUserName())){
			pUserId = util.readUserRoleInfoByFName(PM).getUserName();
			eMailId = util.readUserRoleInfoByFName(PM).getEmail();
		}else{
			pUserId = user.getUserName();
			eMailId = user.getEmail();
		}
		gtfReport.setgMemoryId(gMemoriId);
		gtfReport.setBrand(brand);
		gtfReport.setRequestor(pUserId);
		gtfReport.setProject_WBS(rcvdRow.get(3).toString());
		gtfReport.setWBS_Name(rcvdRow.get(4).toString());
		//gtfReport.setMultiBrand(false);
		if(Util.isNullOrEmpty(rcvdRow.get(5).toString().trim())){
			gtfReport.setPoNumber(rcvdRow.get(5).toString());
		}else{
			gtfReport.setPoNumber("");
		}
		gtfReport.setVendor(rcvdRow.get(7).toString());
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
		.format(Calendar.getInstance().getTime());
		gtfReport.setCreateDate(timeStamp);
		gtfReport.setYear(BudgetConstants.dataYEAR);
		gtfReport.setQual_Quant("Qual_Quant");
		gtfReport.setStudy_Side("study_Side");
		gtfReport.setUnits(0);
		Map<String, Double> receivedMap = new HashMap<String, Double>();
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		Map<String, Double> plannedMap = new HashMap<String, Double>();


		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			try {
				String val = "";
				String rcvdStr = rcvdRow.get(cnt + 8).toString();
				if( rcvdStr.contains("(")){
					val = "-" + rcvdStr.substring(rcvdStr.indexOf("(")+1, rcvdStr.indexOf(")"));
				}else{
					val = rcvdStr;
				}
				/*if(cnt < curMonth ){
					plannedMap.put(BudgetConstants.months[cnt], 0.0);
				}else{*/
					plannedMap.put(BudgetConstants.months[cnt], roundDoubleValue(Double.parseDouble(val), 5));
				/*}*/
				receivedMap.put(
						BudgetConstants.months[cnt],
						roundDoubleValue(Double.parseDouble(val), 5) );

			} catch (NumberFormatException e1) {
				receivedMap.put(BudgetConstants.months[cnt], 0.0);
				plannedMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReport.setPlannedMap(plannedMap);
		gtfReport.setBenchmarkMap(plannedMap);
		gtfReport.setAccrualsMap(receivedMap);
		gtfReport.setVariancesMap(setZeroMap);
		gtfReport.setEmail(eMailId);
		gtfReport.setPercent_Allocation(100);
		gtfReport.setRemarks("");
		
		if (!"".equalsIgnoreCase(gtfReport.getPoNumber())) {
			gtfReport.setStatus("Active");
			gtfReport.setFlag(2);
		} else if(pGtf!=null && pGtf.getgMemoryId()!=null && Util.isNullOrEmpty(pGtf.getPoNumber())){
				gtfReport.setPoNumber(pGtf.getPoNumber());
				gtfReport.setStatus("Active");
				gtfReport.setFlag(2);	
		} else{
				gtfReport.setStatus("New");
				gtfReport.setFlag(1);
				gtfReport.setRemarks("Error with project data : PO# is blank");
		}


		//gtfReport.setSubActivity("");
		uploadedGMems.put(gtfReport.getgMemoryId(), gtfReport);

		if(pGtf ==null){
			if(Util.isNullOrEmpty(gtfReport.getPoNumber())){
				ArrayList<GtfReport> poUpdated = new ArrayList<>();
				if (uploadedPOs.get(gtfReport.getPoNumber()) != null) {
					poUpdated = uploadedPOs.get(gtfReport.getPoNumber());
				}
				poUpdated.add(gtfReport);

				uploadedPOs.put(gtfReport.getPoNumber(), poUpdated);
			}else{
				ArrayList<GtfReport> noPoUpdated = new ArrayList<>();
				if(uploadedWithOutPos.get(gtfReport.getProjectName())!=null){
					noPoUpdated = uploadedWithOutPos.get(gtfReport.getProjectName());
					noPoUpdated.add(gtfReport);
					uploadedWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);

				}else{
					noPoUpdated.add(gtfReport);
					uploadedWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);
				}
			}
		}else{

			if(Util.isNullOrEmpty(gtfReport.getPoNumber())){
				ArrayList<GtfReport> poUpdated = new ArrayList<>();
				if (addedPOs.get(gtfReport.getPoNumber()) != null) {
					poUpdated = addedPOs.get(gtfReport.getPoNumber());
				}
				poUpdated.add(gtfReport);

				addedPOs.put(gtfReport.getPoNumber(), poUpdated);
			}else{
				ArrayList<GtfReport> noPoUpdated = new ArrayList<>();
				if(addedWithOutPos.get(gtfReport.getProjectName())!=null){
					noPoUpdated = addedWithOutPos.get(gtfReport.getProjectName());
					noPoUpdated.add(gtfReport);
					addedWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);

				}else{
					noPoUpdated.add(gtfReport);
					addedWithOutPos.put(gtfReport.getProjectName(), noPoUpdated);
				}
			}

		}
	}

	private void changeForNewMultiBrand(Map<String, ArrayList<GtfReport>> uploadedPOs, List<GtfReport> gtfReports,Map<String, GtfReport> costCenterWiseGtfRptMap) {
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
		}
		for (Entry<String, ArrayList<GtfReport>> entry : uploadedPOs.entrySet())
		{
			ArrayList<GtfReport> receivedGtfReports = entry.getValue();
			if(receivedGtfReports.size() > 1 || (receivedGtfReports.get(0).getProject_WBS().trim().startsWith("421."))) {
				GtfReport nwParentGtfReport = new GtfReport();
				ArrayList<String> childProjList = new ArrayList<String>();
				try {
					nwParentGtfReport = (GtfReport) receivedGtfReports.get(0).clone();
					nwParentGtfReport.setPlannedMap(new HashMap(setZeroMap));
					nwParentGtfReport.setAccrualsMap(new HashMap(setZeroMap));
					nwParentGtfReport.setVariancesMap(new HashMap(setZeroMap));
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				String gMemoriId = nwParentGtfReport.getgMemoryId();
				int count = 1;
				double total = 0.0;
				childProjList.add(gMemoriId );
				for(GtfReport gtfRpt : receivedGtfReports){
					Map<String, Double> receivedChildPlndMap = new HashMap(gtfRpt.getPlannedMap());
					for (Entry<String, Double> entryMap : receivedChildPlndMap.entrySet()){
						nwParentGtfReport.getPlannedMap().put(entryMap.getKey(), nwParentGtfReport.getPlannedMap().get(entryMap.getKey()) + entryMap.getValue());
					}

					Map<String, Double> receivedChildAcrlMap = new HashMap(gtfRpt.getAccrualsMap());
					for (Entry<String, Double> entryMap : receivedChildAcrlMap.entrySet()){
						nwParentGtfReport.getAccrualsMap().put(entryMap.getKey(), nwParentGtfReport.getAccrualsMap().get(entryMap.getKey()) + entryMap.getValue());
					}
					gtfRpt.setgMemoryId(gMemoriId +"."+ (count));
					childProjList.add(gMemoriId + "." + (count++));
				}
				nwParentGtfReport.setBenchmarkMap(nwParentGtfReport.getPlannedMap());
				Map<String, Double> receivedChildVarMap = new HashMap(nwParentGtfReport.getAccrualsMap());
				for (Entry<String, Double> entryMap : receivedChildVarMap.entrySet()){
					nwParentGtfReport.getVariancesMap().put(entryMap.getKey(), nwParentGtfReport.getBenchmarkMap().get(entryMap.getKey()) - nwParentGtfReport.getAccrualsMap().get(entryMap.getKey()));
				}

				nwParentGtfReport.setChildProjectList(childProjList);
				nwParentGtfReport.setBrand("Smart WBS");
				nwParentGtfReport.setMultiBrand(true);

				receivedGtfReports.add(nwParentGtfReport);
				for(GtfReport gtfRpt : receivedGtfReports){
					if (gtfRpt.getgMemoryId().contains(".")) {
						total += gtfRpt.getPlannedMap().get("TOTAL");
					}
				}
				for(GtfReport gtfRpt : receivedGtfReports){
					nwParentGtfReport.setRemarks(gtfRpt.getRemarks());
					if (gtfRpt.getgMemoryId().contains(".")) {
						gtfRpt.setRemarks("");
					}
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


	private void changeForAddedMultiBrand(Map<String, ArrayList<GtfReport>> uploadedPOs, List<GtfReport> gtfReports,Map<String, GtfReport> costCenterWiseGtfRptMap) {
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
		}
		for (Entry<String, ArrayList<GtfReport>> entry : uploadedPOs.entrySet())
		{
			ArrayList<GtfReport> receivedGtfReports = entry.getValue();
			GtfReport nwParentGtfReport = new GtfReport();
			ArrayList<String> childProjList = new ArrayList<String>();
			nwParentGtfReport = costCenterWiseGtfRptMap.get(receivedGtfReports.get(0).getgMemoryId());
			ArrayList<GtfReport> existingGtfReports = new ArrayList<>();
			String gMemoriId = nwParentGtfReport.getgMemoryId();
			int count = nwParentGtfReport.getChildProjectList().size();
			double total = 0.0;
			if(nwParentGtfReport.getPlannedMap()==null || nwParentGtfReport.getPlannedMap().isEmpty()){
				nwParentGtfReport.setPlannedMap(new HashMap(setZeroMap));
			}
			if(nwParentGtfReport.getAccrualsMap()==null || nwParentGtfReport.getAccrualsMap().isEmpty()){
				nwParentGtfReport.setAccrualsMap(new HashMap(setZeroMap));
			}
			childProjList = nwParentGtfReport.getChildProjectList();
			for(String cId : childProjList){
				existingGtfReports.add(costCenterWiseGtfRptMap.get(cId));
			}
			for(GtfReport gtfRpt : receivedGtfReports){
				Map<String, Double> receivedChildPlndMap = new HashMap(gtfRpt.getPlannedMap());
				for (Entry<String, Double> entryMap : receivedChildPlndMap.entrySet()){
					nwParentGtfReport.getPlannedMap().put(entryMap.getKey(), nwParentGtfReport.getPlannedMap().get(entryMap.getKey()) + entryMap.getValue());
				}

				Map<String, Double> receivedChildAcrlMap = new HashMap(gtfRpt.getAccrualsMap());
				for (Entry<String, Double> entryMap : receivedChildAcrlMap.entrySet()){
					nwParentGtfReport.getAccrualsMap().put(entryMap.getKey(), nwParentGtfReport.getAccrualsMap().get(entryMap.getKey()) + entryMap.getValue());
				}
				gtfRpt.setgMemoryId(gMemoriId +"."+ (count));
				childProjList.add(gMemoriId + "." + (count++));
			}
			nwParentGtfReport.setBenchmarkMap(nwParentGtfReport.getPlannedMap());
			Map<String, Double> receivedChildVarMap = new HashMap(nwParentGtfReport.getAccrualsMap());
			for (Entry<String, Double> entryMap : receivedChildVarMap.entrySet()){
				nwParentGtfReport.getVariancesMap().put(entryMap.getKey(), nwParentGtfReport.getBenchmarkMap().get(entryMap.getKey()) - nwParentGtfReport.getAccrualsMap().get(entryMap.getKey()));
			}

			nwParentGtfReport.setChildProjectList(childProjList);
			nwParentGtfReport.setBrand("Smart WBS");
			for(GtfReport cList : existingGtfReports){
				if(cList.getgMemoryId().equalsIgnoreCase(nwParentGtfReport.getgMemoryId())){
					existingGtfReports.remove(cList);
					break;
				}
			}
			receivedGtfReports.add(nwParentGtfReport);
			receivedGtfReports.addAll(existingGtfReports);
			for(GtfReport gtfRpt : receivedGtfReports){
				if (gtfRpt.getgMemoryId().contains(".")) {
					total += gtfRpt.getPlannedMap().get("TOTAL");
				}
			}
			for(GtfReport gtfRpt : receivedGtfReports){
				gtfRpt.setMultiBrand(true);
				gtfRpt.setChildProjectList(childProjList);
				try{
					if (gtfRpt.getgMemoryId().contains(".")) {
						gtfRpt.setPercent_Allocation(Util.roundDoubleValue((gtfRpt.getPlannedMap()
								.get("TOTAL") / total) * 100 , 2));
					}
					else{
						gtfRpt.setRemarks("Note: Brand accrual distribution different from original forecast.");
					}
				}catch(NumberFormatException nfe){
						gtfRpt.setPercent_Allocation(100.0);
				}catch(ArithmeticException ae){
						gtfRpt.setPercent_Allocation(100.0);
				}
				gtfReports.add(gtfRpt);

			}
		}
	}
}
