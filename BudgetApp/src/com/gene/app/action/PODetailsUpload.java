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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.ProjectSequenceGeneratorUtil;
import com.gene.app.util.Util;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.sun.org.apache.xpath.internal.operations.Gt;

@SuppressWarnings("serial")
public class PODetailsUpload extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(PODetailsUpload.class
			.getName());
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();
	String costCenter = "";
	String brand = "";
	String PM = "";
	String WBS = "";
	String WBSName = "";
	boolean isMultibrand = false;
	int curMonth =0;
	
	Map<String, GtfReport> uploadedGMems = new HashMap();
	Map<String, GtfReport> uploadedPOs = new HashMap();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "inside fileupload...");
		HttpSession session = req.getSession();
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		final FileItemFactory fileItemFactory = new DiskFileItemFactory();
		final ServletFileUpload servletFileUpload = new ServletFileUpload(
				fileItemFactory);
		String costCenter = req.getParameter("costCenter");
		int startRow = Integer.parseInt(req.getParameter("inputFrom"));
		int endRow = Integer.parseInt(req.getParameter("inputTo"));
		String objArray = req.getParameter(BudgetConstants.objArray).toString();
		List<List<String>> rowList = new ArrayList();
		uploadedPOs = new HashMap();
		uploadedGMems = new HashMap();
		curMonth = Calendar.getInstance().get(Calendar.MONTH);
		try {
			JSONArray jsonArray = new JSONArray(objArray);
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
					costCenterWiseGtfRptMap, costCenter);
		} catch (JSONException exception) {
			System.out.println(exception + "");
			LOGGER.log(Level.SEVERE,
					"ERROR OCCURED DURING  FILE UPLOAD. ERROR DETAILS : "
							+ exception);
		}
	}

	private void createOrUpdateGTFReports(UserRoleInfo user,
			List<List<String>> rowList, List<GtfReport> gtfReports,
			Map<String, GtfReport> costCenterWiseGtfRptMap, String costCentre) {
		Set<String> completeGMemoriIds = costCenterWiseGtfRptMap.keySet();
		
		Map<String, GtfReport> poMap = util
				.preparePOMap(costCenterWiseGtfRptMap);
		for (List rcvdRow : rowList) {
			String receivedGmemoriId = "";
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

			if (!rcvdRow.get(3).toString().trim().equals("")) {
				WBS = rcvdRow.get(3).toString();
			}

			if (!rcvdRow.get(4).toString().trim().equals("")) {
				WBSName = rcvdRow.get(4).toString();
			}

			//check gmemory id splitting PO Desc
			try {
				if (rcvdRow.get(6).toString().indexOf("_") == 6) {
					receivedGmemoriId = Integer
							.parseInt(rcvdRow.get(6).toString().substring(0,
									Math.min(rcvdRow.get(6).toString().length(), 6)))
							+ "";
				}
			} catch (NumberFormatException ne) {
				// continue rest of the code
			}
			
			// Check by matching PO number
			if (Util.isNullOrEmpty(rcvdRow.get(5).toString())) {
				if (poMap.get(rcvdRow.get(5).toString()) != null) {
					receivedGmemoriId = poMap.get(rcvdRow.get(5).toString())
							.getgMemoryId();
					if(receivedGmemoriId.contains(".")){
						String[] arr = receivedGmemoriId.split("\\.");
						receivedGmemoriId=receivedGmemoriId.split("\\.")[0];
						
					}
				}
			}

			/*String newgMemoriId = "";*/
			// If multibrand
			boolean newCreated = false;
			String pGmemId= new String(receivedGmemoriId);
			if (!"".equalsIgnoreCase(receivedGmemoriId) && completeGMemoriIds.contains(receivedGmemoriId)) {
				GtfReport receivedGtfReport = new GtfReport();
				if (costCenterWiseGtfRptMap.get(receivedGmemoriId)
						.getMultiBrand()) {
					for (String chldgMemId : costCenterWiseGtfRptMap.get(
							receivedGmemoriId).getChildProjectList()) {
						if(costCenterWiseGtfRptMap.get(chldgMemId)!=null && costCenterWiseGtfRptMap.get(chldgMemId).getBrand().equalsIgnoreCase(brand)){
							receivedGmemoriId = costCenterWiseGtfRptMap.get(chldgMemId).getgMemoryId();
							receivedGtfReport = costCenterWiseGtfRptMap
									.get(receivedGmemoriId);
							break;
						}
					}
					if(receivedGtfReport.getgMemoryId() == null){
						createNewReport(user, rcvdRow, gtfReports, costCentre, receivedGmemoriId, costCenterWiseGtfRptMap.get(receivedGmemoriId));
						newCreated = true;
					}
					
					
				} else {
					receivedGtfReport = costCenterWiseGtfRptMap
							.get(receivedGmemoriId);
				}
				if (!newCreated) {
					updateAccrual(rcvdRow, user, gtfReports, receivedGtfReport,
							costCenter,costCenterWiseGtfRptMap.get(pGmemId));
				}
			} else {
				createNewReport(user, rcvdRow, gtfReports, costCentre,
						receivedGmemoriId, null);
			}
		}
		
		/*if (gtfReports.size() != 0) {

			// calculate multibrand and percentages.
			for(GtfReport gtf : gtfReports){
				if(gtf.getMultiBrand() && !gtf.getgMemoryId().contains(".")){
					ArrayList<String> cList = gtf.getChildProjectList();
					double pTotalVal = gtf.getPlannedMap().get(BudgetConstants.months[11]);
					for(String cId : gtf.getChildProjectList()){
						if(cId.contains(".")){
							if(costCenterWiseGtfRptMap.get(cId)!=null){
								for(GtfReport cGtf : gtfReports){
									if(!cId.equalsIgnoreCase(cGtf.getgMemoryId())){
										cGtf.setChildProjectList(cList);
										cGtf.setPercent_Allocation((cGtf.getPlannedMap().get(BudgetConstants.months[11]) * 100/ pTotalVal) );
									}
									
								}
							}
						}
					}
				}
			}*/

			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports, costCentre,
					BudgetConstants.NEW);
		//}
	}

	private void updateAccrual(List rcvdRow, UserRoleInfo user,
			List<GtfReport> gtfReports, GtfReport receivedGtfReport,
			String costCenter, GtfReport pGtfReport) {
		boolean isMB = false;
		boolean frmUploads = false;
		Map<String, Double> parentAccrualMap = new HashMap<>();
		if (WBS.contains("421")) {
			isMB = true;
			if(uploadedGMems.get(pGtfReport.getgMemoryId())!=null){
				pGtfReport = uploadedGMems.get(pGtfReport.getgMemoryId());
				frmUploads=true;
			}else{
				parentAccrualMap = pGtfReport.getAccrualsMap();
			}
		}else{
			if(uploadedGMems.get(receivedGtfReport.getgMemoryId())!=null)
			{
				receivedGtfReport = uploadedGMems.get(receivedGtfReport.getgMemoryId());
			}
		}
		receivedGtfReport.setCostCenter(costCenter);
		receivedGtfReport.setBrand(brand);
		receivedGtfReport.setRequestor(PM);
		receivedGtfReport.setProject_WBS(WBS);
		receivedGtfReport.setWBS_Name(WBSName);
		receivedGtfReport.setPoNumber(rcvdRow.get(5).toString());
		receivedGtfReport.setPoDesc(rcvdRow.get(6).toString());
		receivedGtfReport.setVendor(rcvdRow.get(7).toString());
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		String poDesc = receivedGtfReport.getPoDesc();
		if(receivedGtfReport.getProjectName() != null && receivedGtfReport.getProjectName().equalsIgnoreCase("")){
			receivedGtfReport.setProjectName(poDesc);
		}
		receivedGtfReport.setSubActivity("");
		Map<String, Double> receivedAccrualMap = receivedGtfReport
				.getAccrualsMap();
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			try {
				String val = "";
				String rcvdStr = rcvdRow.get(cnt + 8).toString();
				double pVal =0.0;
				if( rcvdStr.contains("(")){
					val = "-" + rcvdStr.substring(rcvdStr.indexOf("(")+1, rcvdStr.indexOf(")"));
				}else{
					val = rcvdStr;
				}
				if(parentAccrualMap != null && !parentAccrualMap.isEmpty() && isMB ){
					pVal = parentAccrualMap.get(BudgetConstants.months[cnt]);
					parentAccrualMap.put(
							BudgetConstants.months[cnt],
							roundDoubleValue(Double.parseDouble(val), 2) + roundDoubleValue(pVal, 2));
				}
				receivedAccrualMap.put(
						BudgetConstants.months[cnt],
						roundDoubleValue(
								Double.parseDouble(val), 2));
			} catch (NumberFormatException e1) {
				receivedAccrualMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		
		if(parentAccrualMap != null && !parentAccrualMap.isEmpty() && isMB){
			if ( frmUploads ){
				pGtfReport.setAccrualsMap(parentAccrualMap);
				uploadedGMems.put(pGtfReport.getgMemoryId(), pGtfReport);
				uploadedPOs.put(pGtfReport.getPoNumber(), pGtfReport);
				for(GtfReport gtfRp : gtfReports){
					if(gtfRp.getgMemoryId().equalsIgnoreCase(pGtfReport.getgMemoryId())){
						gtfReports.remove(gtfRp);
						gtfReports.add(pGtfReport);
						break;
					}
				}
			}else{
				for(GtfReport gtfRp : gtfReports){
					if(gtfRp.getgMemoryId().equalsIgnoreCase(pGtfReport.getgMemoryId())){
						gtfReports.remove(gtfRp);
						break;
					}
				}
				pGtfReport.setAccrualsMap(parentAccrualMap);
				uploadedGMems.put(pGtfReport.getgMemoryId(), pGtfReport);
				uploadedPOs.put(pGtfReport.getPoNumber(), pGtfReport);
				gtfReports.add(pGtfReport);
			}
		}
		
		if(uploadedGMems.get(receivedGtfReport.getgMemoryId())!=null){
			for(GtfReport gtfRp : gtfReports){
				if(gtfRp.getgMemoryId().equalsIgnoreCase(receivedGtfReport.getgMemoryId())){
					gtfReports.remove(gtfRp);
					break;
				}
			}
		}
		uploadedGMems.put(receivedGtfReport.getgMemoryId(), receivedGtfReport);
		uploadedPOs.put(receivedGtfReport.getPoNumber(), receivedGtfReport);
		gtfReports.add(receivedGtfReport);
	}

	private void createNewReport(UserRoleInfo user, List rcvdRow,
			List<GtfReport> gtfReports, String costCentre , String systemGMem, GtfReport pGtf ) {
		int noOfChild = 0;
		boolean isMB = false;
		ArrayList<String> childList = new ArrayList();
		String pUserId ="";
		String eMailId="";
		if (WBS.contains("421")) {
			if (uploadedPOs.containsKey(rcvdRow.get(5).toString()) || (Util.isNullOrEmpty(systemGMem) 
					&& (uploadedGMems.containsKey(systemGMem) || pGtf!=null))) {
				noOfChild = 1;
				if(pGtf==null){
					pGtf=uploadedGMems.get(systemGMem);
				}

			} else {
				noOfChild = 2;
			}
			isMB = true;
		} else {
			noOfChild = 1;
		}
		for (int iCnt = 0; iCnt < noOfChild; iCnt++) {
			GtfReport gtfReport = new GtfReport();
			String gMemoriId = "";
			
			try {
				gMemoriId = Integer
						.parseInt(rcvdRow
								.get(6)
								.toString()
								.substring(
										0,
										Math.min(rcvdRow.get(6).toString()
												.length(), 6)))
						+ "";
				gtfReport.setDummyGMemoriId(false);
			} catch (NumberFormatException ne) {
				
					gMemoriId = "" + generator.nextValue();
					gtfReport.setDummyGMemoriId(true);
			}
			if (isMB && iCnt == 0) {
				if (uploadedPOs.containsKey(rcvdRow.get(5).toString())) {
					String gmem = uploadedPOs.get(rcvdRow.get(5).toString()).getChildProjectList().get(0) + "."+	
							uploadedPOs.get(rcvdRow.get(5).toString()).getChildProjectList().size();
					gtfReport.setgMemoryId(gmem);
					childList = uploadedPOs.get(rcvdRow.get(5).toString()).getChildProjectList();
					childList.add(gmem);
					gMemoriId = gmem;
					gtfReport.setBrand(brand);
					gtfReport.setChildProjectList(childList);
					pUserId = uploadedPOs.get(rcvdRow.get(5).toString()).getRequestor();
					/*if(pUserId==null){
						pUserId=util.readUserRoleInfoByFName(PM).getUserName();
					}if(pUserId.contains(":")){
						pUserId = pGtf.getRequestor().split(":")[1];
					}*/
					gtfReport.setRequestor(util.readUserRoleInfoByFName(PM).getUserName()+":"+pUserId);
				} else if ( Util.isNullOrEmpty(systemGMem) && pGtf!=null) {
					
					gMemoriId = pGtf.getChildProjectList().get(0)+ "."+pGtf.getChildProjectList().size();
					childList = pGtf.getChildProjectList();
					childList.add(gMemoriId);
					gtfReport.setBrand(brand);
					pUserId = pGtf.getRequestor();
					/*if(pUserId==null){
						pUserId=util.readUserRoleInfoByFName(PM).getUserName();
					}else if(pUserId.contains(":")){
						pUserId = pGtf.getRequestor().split(":")[1];
					}*/
					gtfReport.setRequestor(util.readUserRoleInfoByFName(PM).getUserName()+":"+pUserId);
					
				}else{
					gtfReport.setBrand("Smart WBS");
					childList.add(gMemoriId);
					childList.add(gMemoriId + ".1");
					pUserId=util.readUserRoleInfoByFName(PM).getUserName();
					gtfReport.setRequestor(util.readUserRoleInfoByFName(PM).getUserName());
				}
				gtfReport.setChildProjectList(childList);
				gtfReport.setMultiBrand(true);
				gtfReport.setgMemoryId(gMemoriId);
				
				

			} else if (isMB && iCnt == 1) {
				gMemoriId = childList.get(0);
				gtfReport.setBrand(brand);
				gtfReport.setChildProjectList(childList);
				gtfReport.setMultiBrand(true);
				gtfReport.setgMemoryId(gMemoriId + ".1");
				if(pUserId==null){
					pUserId=util.readUserRoleInfoByFName(PM).getUserName();
				}
				gtfReport.setRequestor(util.readUserRoleInfoByFName(PM).getUserName()+":"+pUserId);
			} else {
				if(uploadedGMems.get(gMemoriId)!=null)
				{
					gtfReport = uploadedGMems.get(gMemoriId);
				}
				gtfReport.setBrand(brand);
				gtfReport.setMultiBrand(false);
				gtfReport.setgMemoryId(gMemoriId);
				gtfReport.setRequestor(util.readUserRoleInfoByFName(PM).getUserName());
			}

			gtfReport.setCostCenter(costCentre);
			if(Util.isNullOrEmpty(util.readUserRoleInfoByFName(PM).getUserName())){
				pUserId = util.readUserRoleInfoByFName(PM).getUserName();
				eMailId = util.readUserRoleInfoByFName(PM).getEmail();
			}else{
				pUserId = user.getUserName();
				eMailId = user.getEmail();
			}
			gtfReport.setRequestor(pUserId);
			gtfReport.setProject_WBS(WBS);
			gtfReport.setWBS_Name(WBSName);
			//gtfReport.setMultiBrand(false);

			gtfReport.setPoNumber(rcvdRow.get(5).toString());
			gtfReport.setPoDesc(rcvdRow.get(6).toString());
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
			Map<String, Double> setPlannedMap = new HashMap<String, Double>();
			Map<String, Double> parentPlannedMap = new HashMap<String, Double>();
			Map<String, Double> parentAccrualMap = new HashMap<String, Double>();
			if(isMB && noOfChild == 1){
				if ( !Util.isNullOrEmpty(systemGMem) ){
				
				parentAccrualMap = 
				uploadedGMems.get(gtfReport.getgMemoryId().split("\\.")[0]).getAccrualsMap();
				parentPlannedMap = uploadedGMems.get(gtfReport.getgMemoryId().split("\\.")[0]).getPlannedMap();
				}else{
					parentAccrualMap = pGtf.getAccrualsMap();
					parentPlannedMap = pGtf.getPlannedMap();
				}
			}
			for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
				setZeroMap.put(BudgetConstants.months[cnt], 0.0);
				try {
					String val = "";
					double pVal = 0.0;
					double pPVal = 0.0;
					String rcvdStr = rcvdRow.get(cnt + 8).toString();
					if( rcvdStr.contains("(")){
						val = "-" + rcvdStr.substring(rcvdStr.indexOf("(")+1, rcvdStr.indexOf(")"));
					}else{
						val = rcvdStr;
					}
					if(parentAccrualMap != null && !parentAccrualMap.isEmpty() && isMB && noOfChild == 1){
						pVal = parentAccrualMap.get(BudgetConstants.months[cnt]);
						pPVal=parentPlannedMap.get(BudgetConstants.months[cnt]);
						parentAccrualMap.put(
								BudgetConstants.months[cnt],
								roundDoubleValue(Double.parseDouble(val), 2) + roundDoubleValue(pVal, 2));
						parentPlannedMap.put(
								BudgetConstants.months[cnt],
								roundDoubleValue(Double.parseDouble(val), 2) + roundDoubleValue(pVal, 2));
					}
					if(cnt < curMonth ){
						setPlannedMap.put(BudgetConstants.months[cnt], 0.0);
					}else{
						setPlannedMap.put(BudgetConstants.months[cnt], roundDoubleValue(Double.parseDouble(val), 2));
					}
					receivedMap.put(
							BudgetConstants.months[cnt],
							roundDoubleValue(Double.parseDouble(val), 2) );
					
				} catch (NumberFormatException e1) {
					receivedMap.put(BudgetConstants.months[cnt], 0.0);
					setPlannedMap.put(BudgetConstants.months[cnt], 0.0);
				}
			}
			gtfReport.setPlannedMap(setPlannedMap);
			gtfReport.setBenchmarkMap(setZeroMap);
			gtfReport.setAccrualsMap(receivedMap);
			gtfReport.setVariancesMap(setZeroMap);
			gtfReport.setEmail(eMailId);
			gtfReport.setPercent_Allocation(100);
			gtfReport.setRemarks("   ");
			if (!"".equalsIgnoreCase(gtfReport.getPoNumber())) {
				gtfReport.setStatus("Active");
				gtfReport.setFlag(2);
			} else {
				gtfReport.setStatus("New");
				gtfReport.setFlag(1);
				gtfReport.setRemarks("Error with project data : PO# is blank");
			}
			
			String poDesc = gtfReport.getPoDesc();
			gtfReport.setProjectName(poDesc);

			gtfReport.setSubActivity("");
			if(parentAccrualMap != null && !parentAccrualMap.isEmpty() && isMB){
				GtfReport parentGTFReport = new GtfReport();
				if ( !Util.isNullOrEmpty(systemGMem) ){
					
					parentGTFReport = uploadedGMems.get(gtfReport.getgMemoryId().split("\\.")[0]);
					parentGTFReport.setAccrualsMap(parentAccrualMap);
					uploadedGMems.put(parentGTFReport.getgMemoryId(), parentGTFReport);
					uploadedPOs.put(parentGTFReport.getPoNumber(), parentGTFReport);
					for(GtfReport gtfRp : gtfReports){
						if(gtfRp.getgMemoryId().equalsIgnoreCase(parentGTFReport.getgMemoryId())){
							gtfReports.remove(gtfRp);
							gtfReports.add(parentGTFReport);
							break;
						}
					}
				}else{
					for(GtfReport gtfRp : gtfReports){
						if(gtfRp.getgMemoryId().equalsIgnoreCase(parentGTFReport.getgMemoryId())){
							gtfReports.remove(gtfRp);
							break;
						}
					}
					pGtf.setAccrualsMap(parentAccrualMap);
					uploadedGMems.put(pGtf.getgMemoryId(), pGtf);
					uploadedPOs.put(pGtf.getPoNumber(), pGtf);
					gtfReports.add(pGtf);
				}
			}
			
			if(uploadedGMems.get(gtfReport.getgMemoryId())!=null){
				for(GtfReport gtfRp : gtfReports){
					if(gtfRp.getgMemoryId().equalsIgnoreCase(gtfReport.getgMemoryId())){
						gtfReports.remove(gtfRp);
						break;
					}
				}
			}
			uploadedGMems.put(gtfReport.getgMemoryId(), gtfReport);
			uploadedPOs.put(gtfReport.getPoNumber(), gtfReport);
			gtfReports.add(gtfReport);

		}
	}

}
