package com.gene.app.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


@SuppressWarnings("serial")
public class DownloadFileServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(DownloadFileServlet.class.getName());
	Map<String, GtfReport> gtfReports = new LinkedHashMap<String, GtfReport>();
	Map<String, GtfReport> completeGtfRptMap = new LinkedHashMap<String, GtfReport>();
	DBUtil util = new DBUtil();

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		UserService userService;
		String email = "";
		Map<String, GtfReport> gtfReports = new LinkedHashMap<String, GtfReport>();
		Map<String, GtfReport> completeGtfRptMap = new LinkedHashMap<String, GtfReport>();
		String objarray = request.getParameter(BudgetConstants.objArray).toString();
		String costCenter = request.getParameter("costCenter").toString();
		String viewSelected = request.getParameter("viewSelected").toString();
		String brandSelected = request.getParameter("brandSelected").toString();
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
		
		
		user.setSelectedCostCenter(costCenter);
		// LOGGER.log(Level.INFO, "gtfReports from cache"+gtfReports);
	Map<String, GtfReport> gtfReportMap = util
				.getAllReportDataFromCache(user.getSelectedCostCenter());
		
		JSONArray jsonArray = null;
		//GtfReport gtfJSONReport = null;
		JSONObject rprtObject = null;
		List <GtfReport> list = new ArrayList<GtfReport>();
		try {
			
			Map<String,Double> userBrandMap= new LinkedHashMap<String,Double>();
			Object[] myBrands = {}; 
		if(objarray!=null && !"".equalsIgnoreCase(objarray)){
			jsonArray = new JSONArray(objarray);
		for (int count = 0; count < jsonArray.length(); count++) {
			//gtfJSONReport = new GtfReport();
			rprtObject = jsonArray.getJSONObject(count);
			if(rprtObject.getString("0")!=null && !"".equalsIgnoreCase(rprtObject.getString("0")) ){
			//if(rprtObject.getString("37")!=null && !"".equalsIgnoreCase(rprtObject.getString("37")) && "false".equalsIgnoreCase(rprtObject.getString("37"))){
			list.add(gtfReportMap.get(rprtObject.getString("0")));
			//}
			}
			
		}}else{
			if(!Util.isNullOrEmpty(viewSelected)){
				viewSelected ="My Brands";
			}
			if(!Util.isNullOrEmpty(brandSelected)){
				CostCenter_Brand ccBrandMap = new CostCenter_Brand();
				 List<CostCenter_Brand> costCenterList =util.readCostCenterBrandMappingData();
				for(CostCenter_Brand ccBrand : costCenterList){
					if(user.getSelectedCostCenter().equalsIgnoreCase(ccBrand.getCostCenter().trim())){
						ccBrandMap = ccBrand;
						break;
					}
				}
				if(ccBrandMap.getBrandFromDB().contains("Indirect Product")){
					brandSelected = "Indirect Product";
				}else{
					userBrandMap = util.getBrandMap(ccBrandMap.getBrandFromDB());
					Map<String,Double> sortedMap = new TreeMap<String,Double>(userBrandMap);
					myBrands = sortedMap.keySet().toArray();
					brandSelected =myBrands[0].toString();
				//	brandSelected =ccBrandMap.getBrandFromDB().split(":")[0];
				}
			}
			
			switch(viewSelected){
			case "My Brands":
				list = util.getReportListByBrand(gtfReportMap,BudgetConstants.USER_ROLE_BRAND_OWNER,brandSelected);
				break;
			case "My Projects":
				list = util.getReportList(gtfReportMap, BudgetConstants.USER_ROLE_PRJ_OWNER, user.getEmail());
				break;
			case "My Cost Center":
				list = util.getReportListCC(gtfReportMap);
				break;
			default:
				list = util.getReportListCC(gtfReportMap);
				break;
			}
			
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		  
		  Collections.sort(list, new Comparator<GtfReport>() {
			  
			 UserRoleInfo user=new UserRoleInfo();
			@Override
			public int compare(GtfReport gtfReport1, GtfReport gtfReport2) {
				int val=0;
				if(gtfReport1.getMultiBrand()==true && gtfReport2.getMultiBrand()==true){
					return -(gtfReport1.getProject_WBS().compareTo(gtfReport2.getProject_WBS())) ;
					
				}
				
				if(gtfReport1.getMultiBrand()==true && gtfReport2.getMultiBrand()==true){
					
					if(!gtfReport1.getProject_WBS().isEmpty() && !gtfReport2.getProject_WBS().isEmpty()){
						return gtfReport1.getBrand().compareTo(gtfReport2.getBrand());
					}else{
						if((gtfReport1.getProject_WBS().isEmpty() && !gtfReport2.getProject_WBS().isEmpty()) ||
								(!gtfReport1.getProject_WBS().isEmpty() && gtfReport2.getProject_WBS().isEmpty())	){
							return -(gtfReport1.getProject_WBS().compareTo(gtfReport2.getProject_WBS())) ;
						}else{
							return (gtfReport1.getBrand().compareTo(gtfReport2.getBrand())) ;
						}
						
					}
					
					
				}
				
				else if(gtfReport1.getMultiBrand()==false && gtfReport2.getMultiBrand()==true){
					
					return -1;	 
				}
                 else if(gtfReport1.getMultiBrand()==false && gtfReport2.getMultiBrand()==false){
					if(!gtfReport1.getProject_WBS().isEmpty() && !gtfReport2.getProject_WBS().isEmpty()){
						return gtfReport1.getBrand().compareTo(gtfReport2.getBrand());
					}else{
						if((gtfReport1.getProject_WBS().isEmpty() && !gtfReport2.getProject_WBS().isEmpty()) ||
								(!gtfReport1.getProject_WBS().isEmpty() && gtfReport2.getProject_WBS().isEmpty())	){
							return -(gtfReport1.getProject_WBS().compareTo(gtfReport2.getProject_WBS())) ;
						}else{
							return (gtfReport1.getBrand().compareTo(gtfReport2.getBrand())) ;
						}
						
					}
				}
				else if(gtfReport1.getMultiBrand()==true && gtfReport2.getMultiBrand()==false){
					return  1;
				}
				if(gtfReport1.getBrand().compareTo(gtfReport2.getBrand())==0){
					return gtfReport1.getRequestor().compareTo(gtfReport2.getRequestor());
				}
				else {
					return gtfReport1.getBrand().compareTo(gtfReport2.getBrand());
				}
			} 
		  });
		response.setHeader("Content-Disposition",
				"attachment; filename= "+costCenter+" Fact Worksheet.xlsx");
		response.setContentType("application/vnd.ms-excel");
		OutputStream outStream = response.getOutputStream();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Sample sheet");
		int rowCount = 3, cellCount = 0;
		String requestor="";
		createHeader(sheet);
		
		for (int i=0;i<list.size();i++) {
			requestor="";
			GtfReport gtfReport = list.get(i);
			if(gtfReport.getBrand()==null /*|| "Smart WBS".equalsIgnoreCase(gtfReport.getBrand() )*/){
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
			String poDesc = "";
			if(Util.isNullOrEmpty(gtfReport.getgMemoryId()) && gtfReport.getgMemoryId().length()<10){
				poDesc = gtfReport.getgMemoryId()+"_"+gtfReport.getProjectName();
			}else{
				poDesc = gtfReport.getProjectName();
			}
			row.createCell(cellCount++).setCellValue(poDesc);
			row.createCell(cellCount++).setCellValue(gtfReport.getVendor());
			if(gtfReport.getRequestor().contains(":")){
				requestor = gtfReport.getRequestor().split(":")[0];
			}else{
				requestor = gtfReport.getRequestor();
			}
			row.createCell(cellCount++).setCellValue(util.readUserRoleInfoByName(requestor).getFullName());
			Map<String, Double> map = gtfReport.getPlannedMap();
			double total = 0.0;
			for(int count = 0; count < map.size() - 1; count++){
				row.createCell(cellCount++).setCellValue(map.get(BudgetConstants.months[count]));
				total += map.get(BudgetConstants.months[count]);
			}
			row.createCell(cellCount++).setCellValue(total);
			row.createCell(cellCount++).setCellValue(gtfReport.getUnits());
			String remarks = gtfReport.getRemarks();
			remarks = remarks.replace("\\\\", "\\").replace("\\\"", "\"")
					.replace("\\\'", "\'");
			row.createCell(cellCount+2).setCellValue(StringEscapeUtils.unescapeHtml(remarks));
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
		Cell cell1 = row1.createCell(25);
		cell1.setCellValue("Check");
		Row row2 = sheet.createRow(2);
		for(int cnt = 0; cnt <= 25; cnt++){
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
			}else if(cnt == 25){
				cellR1.setCellValue("Remarks");
			}
		}
		int count = 9;
		for(String m : BudgetConstants.months){
			Cell cellR1 = row2.createCell(count++);
			cellR1.setCellValue(m);
		}
	}
	/*public List<GtfReport> getReportListCC(Map<String, GtfReport> gtfReports) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		if (gtfReports != null) {
			for (Map.Entry<String, GtfReport> gtfEntry : gtfReports.entrySet()) {
				gtfReport = gtfEntry.getValue();
				gtfReportList.add(gtfReport);
			}
		}
		return gtfReportList;
	}*/
}
