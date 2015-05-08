package com.gene.app.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;

public class UserDetailsUpload extends HttpServlet{
	private final static Logger LOGGER = Logger.getLogger(PODetailsUpload.class
			.getName());
	DBUtil util = new DBUtil();
	String unixId = "";
	String userName = "";
	String email = "";
	String costCenter = "";
	String selectedCostCenter = "";
	public void doPost(HttpServletRequest req, HttpServletResponse res){
		LOGGER.log(Level.INFO, "inside fileupload...");
		final FileItemFactory fileItemFactory = new DiskFileItemFactory();
		final ServletFileUpload servletFileUpload = new ServletFileUpload(
				fileItemFactory);
		String sheetName = req.getParameter("sheetName").toString();
		costCenter = sheetName.substring(sheetName.length()-4);
		selectedCostCenter = costCenter;
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
						/*if(k==2){
							costCenter = jsonArray.getJSONArray(count).get(k).toString();
							selectedCostCenter = costCenter;
						}*/
						list.add(jsonArray.getJSONArray(count).get(k));
					} else {
						list.add("");
					}
				}
				rowList.add(list);
			}
			insertUserData(rowList);
			insertCC_BrandData(rowList);
		} catch (JSONException exception) {
			System.out.println(exception + "");
			LOGGER.log(Level.SEVERE,
					"ERROR OCCURED DURING  FILE UPLOAD. ERROR DETAILS : "
							+ exception);
		}
	}
	
	private void insertUserData(List<List<String>> rowList){
		UserRoleInfo user = new UserRoleInfo();
		Map<String,UserRoleInfo> userMap = util.readAllUserInfo();
		List<UserRoleInfo> userList = new ArrayList<UserRoleInfo>();
		UserRoleInfo userFromDB = new UserRoleInfo();
		String existingCC = "";
		String costCenter1 = costCenter;
		String role = "Project Owner";
		for (List rcvdRow : rowList) {
			if (rcvdRow.get(9).toString().trim().equals("")) {
				continue;
			}else{
				if(!rcvdRow.get(9).toString().trim().equals("")){
					unixId = rcvdRow.get(9).toString();
					email = unixId+"@gene.com";
					userFromDB = userMap.get(email);
					if(!(userFromDB==null)){
						existingCC = userFromDB.getCostCenter();
					}
					
				}if(!rcvdRow.get(2).toString().trim().equals("")){
					costCenter1 = rcvdRow.get(2).toString();
					selectedCostCenter = (costCenter1.contains(":"))?costCenter1.split(":")[0]:costCenter1; 
				}if(Util.isNullOrEmpty(existingCC) && !existingCC.contains(costCenter1)){
					costCenter1 = existingCC+":"+costCenter1;
					selectedCostCenter = (costCenter1.contains(":"))?costCenter1.split(":")[0]:costCenter1;
				}if(!rcvdRow.get(7).toString().trim().equals("") && !rcvdRow.get(8).toString().trim().equals("")){
					userName = rcvdRow.get(8).toString()+" "+rcvdRow.get(7).toString();
				}if(!rcvdRow.get(10).toString().trim().equals("")){
					role = rcvdRow.get(10).toString();
				}
				user = userMap.get(email);//new UserRoleInfo();
				if(user==null){
					user = new UserRoleInfo();
				}				
				user.setCostCenter(costCenter1);
				user.setUserName(unixId);
				user.setSelectedCostCenter(selectedCostCenter);
				user.setEmail(email);
				user.setFullName(userName);
				user.setRole(role);
				userList.add(user);
				userMap.put(email, user);
			}
		}
		util.storeUserData(userList);
		util.putUserInfoToCache(userMap);
	}
	
	private void insertCC_BrandData(List<List<String>> rowList){
		CostCenter_Brand cc_brand = new CostCenter_Brand();
		String brand = "";
		String brandFromDB = "";
		String total = "";
		boolean ccExists = false;
		int index = 0;
		List<CostCenter_Brand> ccList = util.readCostCenterBrandMappingData();
		for(int i=0;i<ccList.size();i++){
			if(ccList.get(i).getCostCenter().equalsIgnoreCase(costCenter)){
				cc_brand = ccList.get(i);
				ccExists = true;
				index = i;
				brandFromDB = cc_brand.getBrandFromDB();
				break;
			}
		}
		for (List rcvdRow : rowList) {
			if(!rcvdRow.get(3).toString().trim().equals("") 
					&& !rcvdRow.get(4).toString().trim().equals("")){
				
				if(!brandFromDB.contains(rcvdRow.get(3).toString())){
					total = rcvdRow.get(4).toString().trim().substring(1);
					brand = brandFromDB+brand+rcvdRow.get(3).toString()+":"+total+";";
				}
			}
		}
		System.out.println("brand is::::::::"+brand);
		cc_brand.setCostCenter(costCenter);
		System.out.println("New Text()::::::"+new Text(brand));
		if(Util.isNullOrEmpty(brand)){
		cc_brand.setBrandFromDB(new Text(brand));
		}
		if(ccExists){
			ccList.remove(index);
		}
		ccList.add(cc_brand);
		util.putCCDataToCache(ccList);
		util.storeCC_BrandData(cc_brand);
	}
}
