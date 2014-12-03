package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class GetReport extends HttpServlet {

	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	DBUtil util = new DBUtil();
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession();
		resp.setContentType(BudgetConstants.contentType);
		UserRoleInfo user = (UserRoleInfo)session.getAttribute("userInfo");
		UserService userService;
		String email ="";
		Map<String,GtfReport> gtfReports = new LinkedHashMap<String,GtfReport>();
		
		if(user==null){
		userService = UserServiceFactory.getUserService();//(User)session.getAttribute("loggedInUser");
		email = userService.getCurrentUser().getEmail();
		user = util.readUserRoleInfo(email);
		}else{
		email = user.getEmail();
		}
		gtfReports = util.getAllReportDataFromCache(user.getCostCenter());
		
		List<GtfReport> gtfReportList = getReportList(gtfReports,BudgetConstants.USER_ROLE_PRJ_OWNER,email);
		Collections.sort( gtfReportList, new Comparator<GtfReport>()
		        {
		            public int compare( GtfReport o1, GtfReport o2 )
		            {
		            	if((o2.getProjectName()).compareTo(o1.getProjectName()) ==0){
		        			return (o1.getgMemoryId()).compareTo(o2.getgMemoryId());
		        		}
		        		return (o2.getProjectName()).compareTo(o1.getProjectName());
		            }
		        } );
		gtfReportList = util.calculateVarianceMap(gtfReportList);
		req.setAttribute(BudgetConstants.REQUEST_ATTR_GTFReports, gtfReportList);
		DBUtil util = new DBUtil();
		//UserRoleInfo user = util.readUserRoleInfo(email);
		//BudgetSummary summary = util.readBudgetSummary(email,BudgetConstants.costCenter,gtfReportList,user);
		BudgetSummary summary = util.readBudgetSummary(user.getCostCenter());
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
}
