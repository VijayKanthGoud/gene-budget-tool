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
						receivedGmemoriId=receivedGmemoriId.split(".")[0];
						
					}
				}
			}

			/*String newgMemoriId = "";*/
			// If multibrand
			if (!"".equalsIgnoreCase(receivedGmemoriId) && completeGMemoriIds.contains(receivedGmemoriId)) {
				GtfReport receivedGtfReport = new GtfReport();
				if (costCenterWiseGtfRptMap.get(receivedGmemoriId)
						.getMultiBrand()) {
					for (String chldgMemId : costCenterWiseGtfRptMap.get(
							receivedGmemoriId).getChildProjectList()) {
						if(costCenterWiseGtfRptMap.get(chldgMemId).getBrand().equalsIgnoreCase(brand)){
							receivedGmemoriId = costCenterWiseGtfRptMap.get(chldgMemId).getgMemoryId();
							receivedGtfReport = costCenterWiseGtfRptMap
									.get(receivedGmemoriId);
							/*matchFound = true;*/
							break;
						}
					}
				} else {
					receivedGtfReport = costCenterWiseGtfRptMap
							.get(receivedGmemoriId);
				}
				updateAccrual(rcvdRow, user, gtfReports, receivedGtfReport,
						costCenter);
			} else {
				
				createNewReport(user, rcvdRow, gtfReports, costCentre/*, newgMemoriId*/);
			}
		}
		
		if (gtfReports.size() != 0) {
			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports, costCentre,
					BudgetConstants.NEW);
		}
	}

	private void updateAccrual(List rcvdRow, UserRoleInfo user,
			List<GtfReport> gtfReports, GtfReport receivedGtfReport,
			String costCenter) {
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
				receivedAccrualMap.put(
						BudgetConstants.months[cnt],
						roundDoubleValue(
								Double.parseDouble(rcvdRow.get(cnt + 8).toString()), 2));
			} catch (NumberFormatException e1) {
				receivedAccrualMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReports.add(receivedGtfReport);
	}

	private void createNewReport(UserRoleInfo user, List rcvdRow,
			List<GtfReport> gtfReports, String costCentre/*, String gMemoriId*/) {
		GtfReport gtfReport = new GtfReport();

		gtfReport.setCostCenter(costCentre);
		gtfReport.setBrand(brand);
		gtfReport.setRequestor(PM);
		gtfReport.setProject_WBS(WBS);
		gtfReport.setWBS_Name(WBSName);
		gtfReport.setMultiBrand(false);

		gtfReport.setPoNumber(rcvdRow.get(5).toString());
		gtfReport.setPoDesc(rcvdRow.get(6).toString());
		gtfReport.setVendor(rcvdRow.get(7).toString());
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		gtfReport.setCreateDate(timeStamp);
		gtfReport.setYear(BudgetConstants.dataYEAR);
		gtfReport.setQual_Quant("Qual_Quant");
		gtfReport.setStudy_Side("study_Side");
		gtfReport.setUnits(1);
		Map<String, Double> receivedMap = new HashMap<String, Double>();
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			try {
				receivedMap.put(
						BudgetConstants.months[cnt],
						roundDoubleValue(
								Double.parseDouble(rcvdRow.get(cnt + 8).toString()), 2));
			} catch (NumberFormatException e1) {
				receivedMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReport.setPlannedMap(setZeroMap);
		gtfReport.setBenchmarkMap(setZeroMap);
		gtfReport.setAccrualsMap(receivedMap);
		gtfReport.setVariancesMap(setZeroMap);
		gtfReport.setEmail(user.getEmail());
		gtfReport.setPercent_Allocation(100);
		if(!"".equalsIgnoreCase(gtfReport.getPoNumber())){
			gtfReport.setStatus("Active");
			gtfReport.setFlag(2);
		}else{
			gtfReport.setStatus("New");
			gtfReport.setFlag(1);
			gtfReport.setRemarks("Error with project data : PO# is blank");
		}
		String poDesc = gtfReport.getPoDesc();
		gtfReport.setProjectName(poDesc);
		String gMemoriId;
		try {
			gMemoriId = Integer.parseInt(gtfReport.getPoDesc().substring(0,
					Math.min(poDesc.length(), 6)))
					+ "";
			gtfReport.setDummyGMemoriId(false);
		} catch (NumberFormatException ne) {
			
			gMemoriId = "" + generator.nextValue();
			gtfReport.setDummyGMemoriId(true);
		}
		gtfReport.setgMemoryId(gMemoriId);
		gtfReport.setSubActivity("");
		gtfReports.add(gtfReport);
	}

}
