package com.gene.app.action;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.BudgetSummary;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class GetReport extends HttpServlet {
	/*private final static Logger LOGGER = Logger
			.getLogger(GetReport.class.getName());*/
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	DBUtil util = new DBUtil();
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession();
		String queryString = req.getQueryString();
		resp.setContentType(BudgetConstants.contentType);
		UserRoleInfo user = (UserRoleInfo)session.getAttribute("userInfo");
		UserService userService;
		String email ="";
		Map<String,GtfReport> gtfReports = new LinkedHashMap<String,GtfReport>();
		Map<String,GtfReport> completeGtfRptMap = new LinkedHashMap<String,GtfReport>();
		String selectedBrand = req.getParameter("brandValue");
		String selectedView = req.getParameter("selectedView");
		String selectedCC = req.getParameter("getCCValue");
		String gMemoriId = req.getParameter("gMemoriId");
		//LOGGER.log(Level.INFO, "Inside GetReport");
		if(user==null){
		userService = UserServiceFactory.getUserService();//(User)session.getAttribute("loggedInUser");
		String requestUri = req.getRequestURI();
		Principal userPrincipal = req.getUserPrincipal();
		String loginLink = userService.createLoginURL(requestUri);
		if(userPrincipal==null){
			resp.sendRedirect(loginLink);
			return;
		}
		email = userService.getCurrentUser().getEmail();
		user = util.readUserRoleInfo(email);
		//LOGGER.log(Level.INFO, "email in userService"+email);
		}else{
		email = user.getEmail();
		//LOGGER.log(Level.INFO, "email in session UserRoleInfo"+email);
		}
		user.setSelectedCostCenter((selectedCC==null || "".equalsIgnoreCase(selectedCC.trim()))?user.getSelectedCostCenter():selectedCC);
	//	gtfReports = util.getAllReportDataFromCache(user.getCostCenter());
		completeGtfRptMap = util.getAllReportDataCollectionFromCache(BudgetConstants.GMEMORI_COLLECTION);
		//LOGGER.log(Level.INFO, "gtfReports from cache"+gtfReports);
		//List<GtfReport> gtfReportList = getReportList(gtfReports,BudgetConstants.USER_ROLE_PRJ_OWNER,email);
		List<GtfReport> gtfReportList = null;
		//gtfReports = util.getAllReportDataFromCache(user.getCostCenter());
		// for project owner role
		System.out.println(":::"+selectedBrand+":::::"+selectedView+"::::"+selectedCC);
		Map<String,Double> userBrandMap= new LinkedHashMap<String,Double>();
		Object[] myBrands = {}; 
		if(user.getRole()!=null && !"".equalsIgnoreCase(user.getRole().trim()) && !user.getRole().contains("Admin")){
			gtfReports = util.getAllReportDataFromCache(user.getSelectedCostCenter());
			gtfReportList = getRptListForLoggedInUser(user,selectedView,selectedBrand,gtfReports);
		}
		// for admin role
		else if(user.getRole()!=null && !"".equalsIgnoreCase(user.getRole().trim()) && user.getRole().contains("Admin")){
			if(selectedView==null || "".equalsIgnoreCase(selectedView.trim()) 
					|| selectedCC==null || "".equalsIgnoreCase(selectedCC.trim())){
				if(gMemoriId != null && !"".equalsIgnoreCase(gMemoriId.trim())){
					selectedView = "My Projects";
				}else{
					selectedView = "My Brands";
				}
				selectedCC = "7135";
				user.setSelectedCostCenter(selectedCC);
				user.setCostCenter("7527:7034:7035:7121:7712:7135:7713:7428:7512:7574:7136:7138");
			}
			if(!Util.isNullOrEmpty(selectedBrand)){
				CostCenter_Brand ccBrandMap = new CostCenter_Brand();
				 List<CostCenter_Brand> costCenterList =util.readCostCenterBrandMappingData();
				for(CostCenter_Brand ccBrand : costCenterList){
					if(selectedCC.equalsIgnoreCase(ccBrand.getCostCenter().trim())){
						ccBrandMap = ccBrand;
						break;
					}
				}
				if(ccBrandMap.getBrandFromDB().contains("Avastin")){
					selectedBrand = "Avastin";
				}else{
					userBrandMap = util.getBrandMap(ccBrandMap.getBrandFromDB());
					Map<String,Double> sortedMap = new TreeMap<String,Double>(userBrandMap);
					myBrands = sortedMap.keySet().toArray();
					selectedBrand =myBrands[0].toString();
				}
			}	
		//user.setCostCenter(selectedCC);
			//}
		gtfReports = util.getAllReportDataFromCache(selectedCC);
		gtfReportList = getRptListForLoggedInUser(user, selectedView, selectedBrand, gtfReports);
		}
		req.setAttribute("selectedView", selectedView);
		req.setAttribute("brandValue", selectedBrand);
		req.setAttribute("getCCValue", selectedCC);
		List<GtfReport> queryGtfRptList = new ArrayList<GtfReport>();
		if (gMemoriId != null && !"".equalsIgnoreCase(gMemoriId.trim())) {
			req.setAttribute("accessreq", "external");
		} else {
			req.setAttribute("accessreq", "internal");
		}
		queryGtfRptList = gtfReportList;
			
		//LOGGER.log(Level.INFO, "gtfReportList from cache based on email"+gtfReportList);
		//String qParam = req.getParameter("gMemoriId");
		if(queryGtfRptList!=null && !queryGtfRptList.isEmpty()){
		Collections.sort( queryGtfRptList, new Comparator<GtfReport>()
		        {
		            public int compare( GtfReport o1, GtfReport o2 )
		            {
		            	if(o1.getFlag() == o2.getFlag()) {
		            		if((o1.getProjectName()).compareTo(o2.getProjectName()) ==0){
		            			return (o1.getgMemoryId()).compareTo(o2.getgMemoryId());
		            		}
		            		return (o1.getProjectName()).compareTo(o2.getProjectName());
		            	}
		            	return o1.getFlag() - o2.getFlag();
		            }
		        } );
		queryGtfRptList = util.calculateVarianceMap(queryGtfRptList);
		}
		
		//LOGGER.log(Level.INFO, "gtfReportList from after calculating variance map"+gtfReportList);
		req.setAttribute(BudgetConstants.REQUEST_ATTR_GTFReports, queryGtfRptList);
		DBUtil util = new DBUtil();
		//UserRoleInfo user = util.readUserRoleInfo(email);
		//BudgetSummary summary = util.readBudgetSummary(email,BudgetConstants.costCenter,gtfReportList,user);
		BudgetSummary summary = util.readBudgetSummary(user.getSelectedCostCenter());
		//LOGGER.log(Level.INFO, "summary from util.readBudgetSummary(user.getCostCenter())"+summary);
		req.setAttribute("user", user);
		session.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		RequestDispatcher rd = req.getRequestDispatcher(BudgetConstants.GetReport_REDIRECTURL);
		try {
			rd.forward(req, resp);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<GtfReport> getReportListByBrand(
			Map<String, GtfReport> gtfReports, String userType,
			String selectedBrand) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		if (gtfReports != null) {

			for (Map.Entry<String, GtfReport> gtfEntry : gtfReports.entrySet()) {
				gtfReport = gtfEntry.getValue();
				if ((selectedBrand != null && !"".equals(selectedBrand.trim()))
						&& (gtfReport.getBrand().trim().toLowerCase())
								.equalsIgnoreCase(selectedBrand.toLowerCase()
										.trim())) {
					gtfReportList.add(gtfReport);
				}
			}
		}
		return gtfReportList;
	}

	public List<GtfReport> getReportListCC(Map<String, GtfReport> gtfReports) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		if (gtfReports != null) {
			for (Map.Entry<String, GtfReport> gtfEntry : gtfReports.entrySet()) {
				gtfReport = gtfEntry.getValue();
				gtfReportList.add(gtfReport);
			}
		}
		return gtfReportList;
	}

	public List<GtfReport> getRptListForLoggedInUser(UserRoleInfo user,
			String selectedView, String selectedBrand,
			Map<String, GtfReport> gtfReports) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		if (selectedView == null || "".equalsIgnoreCase(selectedView.trim())) {
			if (selectedBrand != null
					&& !"".equalsIgnoreCase(selectedBrand.trim())) {
				selectedView = "My Brands";
			} else {
				selectedView = "";
			}
		}
		switch (selectedView) {
		case "My Projects":
			gtfReportList = getReportList(gtfReports,
					BudgetConstants.USER_ROLE_PRJ_OWNER, user.getEmail());
			break;
		case "My Brands":
			if (selectedBrand != null
					&& !"".equalsIgnoreCase(selectedBrand.trim())) {
				gtfReportList = getReportListByBrand(gtfReports,
						BudgetConstants.USER_ROLE_BRAND_OWNER, selectedBrand);
			}
			break;
		case "My Cost Center":
			gtfReportList = getReportListCC(gtfReports);
			break;
		default:
			gtfReportList = getReportList(gtfReports,
					BudgetConstants.USER_ROLE_PRJ_OWNER, user.getEmail());
			selectedView = "My Projects";
		}
		return gtfReportList;
	}

	public List<GtfReport> getRptListForAdmin(UserRoleInfo user,
			String selectedView, String selectedBrand,
			Map<String, GtfReport> gtfReports) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		gtfReportList = getReportListCC(gtfReports);
		return gtfReportList;
	}
	
	public List<GtfReport> getReportList(Map<String,GtfReport>gtfReports,String userType,String email){
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		
		if(gtfReports!=null){
			
			for(Map.Entry<String, GtfReport> gtfEntry:gtfReports.entrySet()){
				gtfReport = gtfEntry.getValue();
				if((email !=null && !"".equals(email.trim())) && (gtfReport.getEmail().trim().toLowerCase()).contains(email.toLowerCase())){
				gtfReportList.add(gtfReport);
			}}
		}
		return gtfReportList;
	}
	public List<GtfReport> getQueryGtfReportList(List<GtfReport>gtfReports,String gMemoriId, HttpServletRequest req, UserRoleInfo user){
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		gtfReportList =	util.readProjectDataById(gMemoriId,user);
		if(gtfReportList==null){
			gtfReportList = new ArrayList<GtfReport>();
			System.out.println("gtfReportList = "+gtfReportList.isEmpty());
		}else if(gtfReportList!=null && !gtfReportList.isEmpty()){
			gtfReport = gtfReportList.get(0);
			System.out.println("gtfReport = "+gtfReport.getPlannedMap());
			req.setAttribute("selectedView", "My Projects");
			req.setAttribute("getCCValue", gtfReport.getCostCenter());
			user.setSelectedCostCenter(gtfReport.getCostCenter());
			//gtfReports.add(gtfReport);
		}
		
		/*String qParam = ""; 
		Iterator<GtfReport> iter = null;
			if(gtfReports!=null){
				iter = gtfReports.iterator();
				
				qParam = req.getParameter("gMemoriId");
				//int dotpos = qParam.indexOf(".");
				if(dotpos>0){
				qParam = qParam.substring(0,dotpos);
				}
			while(iter.hasNext()){
			if(queryString.contains("gMemoriId")) {
				
					gtfReport = iter.next();
					if(gtfReport.getgMemoryId().contains(qParam)) {
					gtfReportList.add(gtfReport);
					}
			}else if(queryString.contains("projectName")){
				qParam = req.getParameter("projectName");
					gtfReport = iter.next();
					if(qParam.equalsIgnoreCase(gtfReport.getProjectName())) {
					gtfReportList.add(gtfReport);
					}
			}else if(queryString.contains("brand")){
				qParam = req.getParameter("brand");
					gtfReport = iter.next();
					if(qParam.equalsIgnoreCase(gtfReport.getBrand())) {
					gtfReportList.add(gtfReport);
					}
			}
	}}*/
		return gtfReportList;
	}
	/*public List<GtfReport> getRptListByGmemId(List<GtfReport>gtfReports,String qParam){
		GtfReport gtfReport = null;
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
	if(gtfReports!=null){
		Iterator<GtfReport> iter = gtfReports.iterator();
		while(iter.hasNext()){
			gtfReport = iter.next();
			if(qParam.equalsIgnoreCase(gtfReport.getgMemoryId())) {
			gtfReportList.add(gtfReport);
			}
		}}
	return gtfReportList;
	}*/
}
