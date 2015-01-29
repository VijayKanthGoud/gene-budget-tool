package com.gene.app.action;

import static com.gene.app.util.Util.roundDoubleValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.ExcelParsingUtil;
import com.gene.app.util.ProjectSequenceGeneratorUtil;

@SuppressWarnings("serial")
public class UserUploadServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(UserUploadServlet.class.getName());
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
		try {
			final FileItemIterator fileItemIterator = servletFileUpload
					.getItemIterator(req);
			while (fileItemIterator.hasNext()) {
				final FileItemStream fileItemStream = (FileItemStream) fileItemIterator
						.next();
				String uploadedFileName;
				if (fileItemStream.isFormField()) {
					uploadedFileName = fileItemStream.getName();
				} else if (fileItemStream.getFieldName().equals("file")) {
					uploadedFileName = fileItemStream.getName();
					LOGGER.log(Level.INFO, "UPLOAD FILE NAME :"
							+ uploadedFileName);
					InputStream inputStream = fileItemStream.openStream();
					InputStream stream = new ByteArrayInputStream(
							IOUtils.toByteArray(inputStream));
					List<List<String>> rowList = ExcelParsingUtil
							.readExcellData(stream, "PO Detail By PM", 6, 21);
					List<GtfReport> gtfReports = new ArrayList<GtfReport>();
					Map<String, GtfReport> costCenterWiseGtfRptMap = new LinkedHashMap<String, GtfReport>();
					costCenterWiseGtfRptMap = util
							.getAllReportDataFromCache(user.getCostCenter());
					createOrUpdateGTFReports(user, rowList, gtfReports,
							costCenterWiseGtfRptMap);
				}
			}
		} catch (IOException | FileUploadException | InvalidFormatException exception) {
			LOGGER.log(Level.SEVERE,
					"ERROR OCCURED DURING  FILE UPLOAD. eRROR DETAILS : "
							+ exception);
		}
		resp.sendRedirect("/getreport");
	}

	private void createOrUpdateGTFReports(UserRoleInfo user,
			List<List<String>> rowList, List<GtfReport> gtfReports,
			Map<String, GtfReport> costCenterWiseGtfRptMap) {
		Set<String> completeGMemoriIds = costCenterWiseGtfRptMap.keySet();
		for (List<String> rcvdRow : rowList) {
			String receivedGmemoriId = "";
			try {
				receivedGmemoriId = Integer.parseInt(rcvdRow.get(6).substring(
						0, Math.min(rcvdRow.get(6).length(), 6)))
						+ "";
				if (completeGMemoriIds.contains(receivedGmemoriId)) {
					GtfReport receivedGtfReport = costCenterWiseGtfRptMap
							.get(receivedGmemoriId);
					updateAccrual(rcvdRow, user, gtfReports, receivedGtfReport);
				} else {
					createNewReport(user, rcvdRow, gtfReports);
				}
			} catch (NumberFormatException ne) {
				createNewReport(user, rcvdRow, gtfReports);
			}
			if (gtfReports.size() != 0) {
				util.generateProjectIdUsingJDOTxn(gtfReports);
				util.storeProjectsToCache(gtfReports, user.getCostCenter(),
						BudgetConstants.NEW);
			}
		}
	}

	private void updateAccrual(List<String> rcvdRow, UserRoleInfo user,
			List<GtfReport> gtfReports, GtfReport receivedGtfReport) {
		Map<String, Double> receivedAccrualMap = receivedGtfReport
				.getAccrualsMap();
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			try {
				receivedAccrualMap.put(
						BudgetConstants.months[cnt],
						roundDoubleValue(
								Double.parseDouble(rcvdRow.get(cnt + 8)), 2));
			} catch (NumberFormatException e1) {
				receivedAccrualMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReports.add(receivedGtfReport);
	}

	private void createNewReport(UserRoleInfo user, List<String> rcvdRow,
			List<GtfReport> gtfReports) {
		GtfReport gtfReport = new GtfReport();
		if (!rcvdRow.get(0).trim().equals("NO DATA")) {
			costCenter = rcvdRow.get(0);
		}
		gtfReport.setCostCenter(costCenter);

		if (rcvdRow.get(1).contains(" Total")) {
			return;
		} else if (!rcvdRow.get(1).trim().equals("NO DATA")) {
			brand = rcvdRow.get(1);
		}
		gtfReport.setBrand(brand);

		if (rcvdRow.get(2).contains(" Total")) {
			return;
		} else if (!rcvdRow.get(2).trim().equals("NO DATA")) {
			PM = rcvdRow.get(2);
		}
		gtfReport.setRequestor(PM);

		if (!rcvdRow.get(3).trim().equals("NO DATA")) {
			WBS = rcvdRow.get(3);
		}
		gtfReport.setProject_WBS(WBS);

		if (!rcvdRow.get(4).trim().equals("NO DATA")) {
			WBSName = rcvdRow.get(4);
		}
		gtfReport.setWBS_Name(WBSName);

		gtfReport.setPoNumber(rcvdRow.get(5));
		gtfReport.setPoDesc(rcvdRow.get(6));
		gtfReport.setVendor(rcvdRow.get(7));
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
				.format(Calendar.getInstance().getTime());
		gtfReport.setCreateDate(timeStamp);
		gtfReport.setYear(BudgetConstants.dataYEAR);
		gtfReport.setQual_Quant("Qual_Quant");
		gtfReport.setStudy_Side("study_Side");
		gtfReport.setUnits(1);
		Map<String, Double> plannedMap = new HashMap<String, Double>();
		Map<String, Double> setZeroMap = new HashMap<String, Double>();
		for (int cnt = 0; cnt <= BudgetConstants.months.length - 1; cnt++) {
			setZeroMap.put(BudgetConstants.months[cnt], 0.0);
			try {
				plannedMap.put(
						BudgetConstants.months[cnt],
						roundDoubleValue(
								Double.parseDouble(rcvdRow.get(cnt + 8)), 2));
			} catch (NumberFormatException e1) {
				plannedMap.put(BudgetConstants.months[cnt], 0.0);
			}
		}
		gtfReport.setPlannedMap(plannedMap);
		gtfReport.setBenchmarkMap(plannedMap);
		gtfReport.setAccrualsMap(plannedMap);
		gtfReport.setVariancesMap(setZeroMap);
		gtfReport.setMultiBrand(isMultibrand);
		gtfReport.setEmail(user.getEmail());
		gtfReport.setPercent_Allocation(100);
		gtfReport.setStatus("New");
		String poDesc = gtfReport.getPoDesc();
		gtfReport.setProjectName(poDesc);
		String gMemoriId;
		try {
			gMemoriId = Integer.parseInt(gtfReport.getPoDesc().substring(0,
					Math.min(poDesc.length(), 6)))
					+ "";
		} catch (NumberFormatException ne) {
			gMemoriId = "" + generator.nextValue();
		}
		gtfReport.setgMemoryId(gMemoriId);
		gtfReport.setFlag(1);
		gtfReport.setSubActivity("");
		gtfReports.add(gtfReport);
	}

}
