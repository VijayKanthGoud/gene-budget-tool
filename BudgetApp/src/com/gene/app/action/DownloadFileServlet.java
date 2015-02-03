package com.gene.app.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class DownloadFileServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(DownloadFileServlet.class.getName());
	Map<String, GtfReport> gtfReports = new LinkedHashMap<String, GtfReport>();
	Map<String, GtfReport> completeGtfRptMap = new LinkedHashMap<String, GtfReport>();
	DBUtil util = new DBUtil();

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		UserService userService;
		String email = "";
		Map<String, GtfReport> gtfReports = new LinkedHashMap<String, GtfReport>();
		Map<String, GtfReport> completeGtfRptMap = new LinkedHashMap<String, GtfReport>();
		// LOGGER.log(Level.INFO, "Inside GetReport");
		if (user == null) {
			userService = UserServiceFactory.getUserService();// (User)session.getAttribute("loggedInUser");
			email = userService.getCurrentUser().getEmail();
			user = util.readUserRoleInfo(email);
			// LOGGER.log(Level.INFO, "email in userService"+email);
		} else {
			email = user.getEmail();
			// LOGGER.log(Level.INFO, "email in session UserRoleInfo"+email);
		}
		// LOGGER.log(Level.INFO, "gtfReports from cache"+gtfReports);
		Map<String, GtfReport> gtfReportMap = util
				.getAllReportDataFromCache(user.getCostCenter());
		response.setHeader("Content-Disposition",
				"attachment; filename=Report.xlsx");
		response.setContentType("application/vnd.ms-excel");
		OutputStream outStream = response.getOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sample sheet");
		int rowCount = 3, cellCount = 0;
		createHeader(sheet);
		for (GtfReport gtfReport : gtfReportMap.values()) {
			if("Total Products(MB)".equalsIgnoreCase(gtfReport.getBrand())){
				continue;
			}
			Row row = sheet.createRow(rowCount++);
			row.createCell(cellCount++)
					.setCellValue(gtfReport.getProject_WBS());
			row.createCell(cellCount++)
					.setCellValue(gtfReport.getWBS_Name());
			row.createCell(cellCount++)
					.setCellValue(gtfReport.getSubActivity());
			row.createCell(cellCount++).setCellValue(gtfReport.getBrand());
			row.createCell(cellCount++).setCellValue(
					gtfReport.getPercent_Allocation());
			row.createCell(cellCount++).setCellValue(gtfReport.getPoNumber());
			row.createCell(cellCount++).setCellValue(gtfReport.getPoDesc());
			row.createCell(cellCount++).setCellValue(gtfReport.getVendor());
			row.createCell(cellCount++).setCellValue(gtfReport.getRequestor());
			Map<String, Double> map = gtfReport.getAccrualsMap();
			double total = 0.0;
			boolean hasEntered=false;
			int currMonth = Calendar.getInstance().get(Calendar.MONTH);
			for(int count = 0; count < map.size() - 1; count++){
				if(!hasEntered && currMonth<count){
					map = gtfReport.getPlannedMap();
					hasEntered=true;
				}
				row.createCell(cellCount++).setCellValue(map.get(BudgetConstants.months[count]));
				total += map.get(BudgetConstants.months[count]);
			}
			row.createCell(cellCount++).setCellValue(total);
			cellCount = 0;
		}
		workbook.write(response.getOutputStream());
		outStream.flush();
		outStream.close();

	}

	private void createHeader(XSSFSheet sheet) {
		Row row0 = sheet.createRow(0);
		Cell bsCell = row0.createCell(0);
		bsCell.setCellValue("Budget Summary");
		Row row1 = sheet.createRow(1);
		int currMonth = Calendar.getInstance().get(Calendar.MONTH);
		for (int monthCount = 0; monthCount < 12; monthCount++) {
			Cell cellType = row1.createCell(monthCount + 9);
			if (monthCount <= currMonth) {
				cellType.setCellValue("Actual");
			} else {
				cellType.setCellValue("Forecast");
			}
		}
		for (int count = 0; count < 3; count++) {
			Cell cell = row1.createCell(count + 21);
			cell.setCellValue("FY");
		}
		Cell cell = row1.createCell(24);
		cell.setCellValue("Check");
		
		Row row2 = sheet.createRow(2);
		for(int cnt = 0; cnt <= 24; cnt++){
			Cell cellR1 = row2.createCell(cnt);
			if(cnt == 0){
				cellR1.setCellValue("Project WBS");
			}else if(cnt == 1){
				cellR1.setCellValue("WBS Name");
			}else if(cnt == 2){
				cellR1.setCellValue("Sub Activity");
			}else if(cnt == 3){
				cellR1.setCellValue("Brand");
			}else if(cnt == 4){
				cellR1.setCellValue("Allocation Percentage");
			}else if(cnt == 5){
				cellR1.setCellValue("PO Number");
			}else if(cnt == 6){
				cellR1.setCellValue("PO Description");
			}else if(cnt == 7){
				cellR1.setCellValue("Vendor");
			}else if(cnt == 8){
				cellR1.setCellValue("Requestor");
			}else if(cnt == 22){
				cellR1.setCellValue("Unit");
			}else if(cnt == 23){
				cellR1.setCellValue("PO Total");
			}else if(cnt == 24){
				cellR1.setCellValue("Variance");
			}
		}
		int count = 9;
		for(String m : BudgetConstants.months){
			Cell cellR1 = row2.createCell(count++);
			cellR1.setCellValue(m);
		}
	}

}
