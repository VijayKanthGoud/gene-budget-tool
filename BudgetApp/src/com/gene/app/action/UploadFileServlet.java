package com.gene.app.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.gene.app.model.GtfReport;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.ExcelParsingUtil;

@SuppressWarnings("serial")
public class UploadFileServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(UploadFileServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "inside fileupload...");
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
					List<List<String>> rowList = ExcelParsingUtil.readExcellData(stream);
					Map<String, GtfReport> gtfReports = new HashMap<String, GtfReport>();
					createGTFReports(rowList, gtfReports);
					LOGGER.log(Level.INFO, "RowList received :"	+ rowList);
				}
			}
		} catch (IOException | FileUploadException | InvalidFormatException exception) {
			LOGGER.log(Level.SEVERE,
					"ERROR OCCURED DURING  FILE UPLOAD. eRROR DETAILS : "
							+ exception);
		}
	}

	private void createGTFReports(List<List<String>> rowList,
			Map<String, GtfReport> gtfReports) {
		for(List<String> e : rowList){
			GtfReport gtfReport = new GtfReport();

			gtfReport.setProject_WBS(e.get(0));
			gtfReport.setWBS_Name(e.get(1));
			gtfReport.setSubActivity(e.get(2));
			gtfReport.setBrand(e.get(3));
			gtfReport.setPercent_Allocation(Double.parseDouble(e.get(4)));
			gtfReport.setPoNumber(e.get(5));
			gtfReport.setPoDesc(e.get(6));
			gtfReport.setVendor(e.get(7));
			gtfReport.setRequestor(e.get(8));
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
			.format(Calendar.getInstance().getTime());
			gtfReport.setCreateDate(timeStamp);
			gtfReport.setYear(BudgetConstants.dataYEAR);
			gtfReport.setQual_Quant("Qual_Quant");
			gtfReport.setStudy_Side("study_Side");
			gtfReport.setUnits(1);
			for(int i = 9; i <= 20; i++){
			}
		}
		
	}

}
