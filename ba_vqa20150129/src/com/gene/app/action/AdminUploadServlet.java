package com.gene.app.action;

import static com.gene.app.util.Util.roundDoubleValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class AdminUploadServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(AdminUploadServlet.class.getName());
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();

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
			// FileItemStream fileItemStream = null;
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
					createGTFReports(user, rowList, gtfReports);
				}
			}
		} catch (IOException | FileUploadException | InvalidFormatException exception) {
			LOGGER.log(Level.SEVERE,
					"ERROR OCCURED DURING  FILE UPLOAD. eRROR DETAILS : "
							+ exception);
		}
		resp.sendRedirect("/getreport");
	}

	private void createGTFReports(UserRoleInfo user,
			List<List<String>> rowList, List<GtfReport> gtfReports) {
		String costCenter = "";
		String brand = "";
		String PM = "";
		String WBS = "";
		String WBSName = "";
		boolean isMultibrand = false;

		for (List<String> e : rowList) {
			GtfReport gtfReport = new GtfReport();
			if (!e.get(0).trim().equals("NO DATA")) {
				costCenter = e.get(0);
			}
			gtfReport.setCostCenter(costCenter);

			if (e.get(1).contains(" Total")) {
				continue;
			} else if (!e.get(1).trim().equals("NO DATA")) {
				brand = e.get(1);
			}
			gtfReport.setBrand(brand);

			if (e.get(2).contains(" Total")) {
				continue;
			} else if (!e.get(2).trim().equals("NO DATA")) {
				PM = e.get(2);
			}
			gtfReport.setRequestor(PM);

			if (!e.get(3).trim().equals("NO DATA")) {
				WBS = e.get(3);
			}
			gtfReport.setProject_WBS(WBS);

			if (!e.get(4).trim().equals("NO DATA")) {
				WBSName = e.get(4);
			}
			gtfReport.setWBS_Name(WBSName);

			gtfReport.setPoNumber(e.get(5));
			gtfReport.setPoDesc(e.get(6));
			gtfReport.setVendor(e.get(7));
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
									Double.parseDouble(e.get(cnt + 8)), 2));
				} catch (NumberFormatException e1) {
					plannedMap.put(BudgetConstants.months[cnt], 0.0);
				}
			}
			gtfReport.setPlannedMap(plannedMap);
			gtfReport.setBenchmarkMap(plannedMap);
			gtfReport.setAccrualsMap(setZeroMap);
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
		if (gtfReports.size() != 0) {
			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports, user.getCostCenter(),
					BudgetConstants.NEW);
		}
	}
}
