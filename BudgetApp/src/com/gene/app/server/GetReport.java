package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.bean.BudgetSummary;
import com.gene.app.bean.GtfReport;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.DBUtil;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class GetReport extends HttpServlet {

	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	DBUtil util = new DBUtil();
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType(BudgetConstants.contentType);
		UserService userService = UserServiceFactory.getUserService();//(User)session.getAttribute("loggedInUser");
		String email = userService.getCurrentUser().getEmail();
		Map<String,GtfReport> gtfReports = new LinkedHashMap<String,GtfReport>();
		gtfReports = util.getAllReportDataFromCache(BudgetConstants.costCenter);
		
		List<GtfReport> gtfReportList = getReportList(gtfReports,BudgetConstants.USER_ROLE_PRJ_OWNER,email);
		gtfReportList = util.calculateVarianceMap(gtfReportList);
		req.setAttribute(BudgetConstants.REQUEST_ATTR_GTFReports, gtfReportList);
		DBUtil util = new DBUtil();
		UserRoleInfo user = util.readUserRoleInfo(email, BudgetConstants.costCenter);
		//BudgetSummary summary = util.readBudgetSummary(email,BudgetConstants.costCenter,gtfReportList,user);
		BudgetSummary summary = util.readBudgetSummary(BudgetConstants.costCenter);
		req.setAttribute("user", user);
		req.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		RequestDispatcher rd = req.getRequestDispatcher(BudgetConstants.GetReport_REDIRECTURL);
		try {
			rd.forward(req, resp);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public List<GtfReport> getReportList(Map<String,GtfReport>gtfReports,String userType,String email){
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		
		if(gtfReports!=null){
			
			for(Map.Entry<String, GtfReport> gtfEntry:gtfReports.entrySet()){
				gtfReport = gtfEntry.getValue();
				if((email !=null && !"".equals(email.trim())) && email.equalsIgnoreCase(gtfReport.getEmail())){
				gtfReportList.add(gtfReport);
			}}
		}
		return gtfReportList;
	}
}
