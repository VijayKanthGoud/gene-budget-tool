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
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

@SuppressWarnings("serial")
public class AdminUploadJSONServlet extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(AdminUploadJSONServlet.class.getName());
	DBUtil util = new DBUtil();
	ProjectSequenceGeneratorUtil generator = new ProjectSequenceGeneratorUtil();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		LOGGER.log(Level.INFO, "inside fileupload...");
		HttpSession session = req.getSession();
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		
		String objarray = req.getParameter(BudgetConstants.objArray).toString();
		
		List<List<String>> rowList = new ArrayList();
		
		try {
			JSONArray jsonArray = new JSONArray(objarray);
			for (int count = 24; count < jsonArray.length(); count++) {
				List list = new ArrayList();
				for(int k=3; k < jsonArray.getJSONArray(count).length(); k++){
					String varCol = jsonArray.getJSONArray(count).get(k).toString();
					if(!varCol.equalsIgnoreCase("null") && !varCol.trim().equalsIgnoreCase("")){
						list.add(jsonArray.getJSONArray(count).get(k));	
					}else{
						list.add("NO DATA");	
					}
				}
				rowList.add(list);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		createGTFReports(user, rowList, gtfReports);
	}

	private void createGTFReports(UserRoleInfo user,
			List<List<String>> rowList, List<GtfReport> gtfReports) {
		boolean isMultibrand = false;

		for (List<String> recvdRow : rowList) {
			GtfReport gtfReport = new GtfReport();
			gtfReport.setCostCenter(user.getCostCenter());
			
			if(recvdRow.get(0) != null){
				gtfReport.setProject_WBS(recvdRow.get(0));
			}else{
				gtfReport.setProject_WBS("");
			}
			
			if(recvdRow.get(1) != null){
				gtfReport.setWBS_Name(recvdRow.get(1));
			}else{
				gtfReport.setWBS_Name(recvdRow.get(1));
			}		
			
			if(recvdRow.get(2) != null){
				gtfReport.setSubActivity(recvdRow.get(2));
			}else{
				gtfReport.setSubActivity("");
			}	
			
			if(recvdRow.get(3) != null){
				gtfReport.setBrand(recvdRow.get(3));
			}else{
				gtfReport.setBrand("");
			}
			
			gtfReport.setPercent_Allocation(100);
			
			if(recvdRow.get(5) != null){
				gtfReport.setPoNumber(recvdRow.get(5));
			}else{
				gtfReport.setPoNumber("");
			}
			
			if(recvdRow.get(6) != null){
				gtfReport.setPoDesc(recvdRow.get(6));
			}else{
				gtfReport.setPoDesc("");
			}
			
			if(recvdRow.get(7) != null){
				gtfReport.setVendor(recvdRow.get(7));
			}else{
				gtfReport.setVendor("");
			}
			
			if(recvdRow.get(8) != null){
				gtfReport.setRequestor(recvdRow.get(8));
			}else{
				gtfReport.setRequestor("");
			}
			
			
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
					
					if(recvdRow.get(cnt + 8) != null && new Double(recvdRow.get(cnt + 8)).toString().trim().equals("")){
						System.out.println("rcvd 1" + Double.parseDouble(recvdRow.get(cnt + 8)+""));
						
						
						plannedMap.put(BudgetConstants.months[cnt], Double.parseDouble(recvdRow.get(cnt + 8)+""));
					}else{
						plannedMap.put(BudgetConstants.months[cnt], 0.0);
					}
				} catch (Exception e1) {
					plannedMap.put(BudgetConstants.months[cnt], 0.0);
				}
			}
			gtfReport.setPlannedMap(plannedMap);
			gtfReport.setBenchmarkMap(plannedMap);
			gtfReport.setAccrualsMap(setZeroMap);
			gtfReport.setVariancesMap(setZeroMap);
			gtfReport.setMultiBrand(isMultibrand);
			gtfReport.setEmail(user.getEmail());
			
			
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
			
			if(gtfReport.getPoNumber() != null || gtfReport.getPoNumber().trim().equals("")){
				gtfReport.setStatus("New");
				gtfReport.setFlag(1);
			}else{
				gtfReport.setStatus("Active");
				gtfReport.setFlag(2);
			}
			gtfReport.setgMemoryId(gMemoriId);
			
			
			gtfReports.add(gtfReport);
		}
		if (gtfReports.size() != 0) {
			util.generateProjectIdUsingJDOTxn(gtfReports);
			util.storeProjectsToCache(gtfReports, user.getCostCenter(),
					BudgetConstants.NEW);
		}
	}
}
