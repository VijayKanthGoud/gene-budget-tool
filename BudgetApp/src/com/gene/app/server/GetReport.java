package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.bean.BudgetSummary;
import com.gene.app.bean.GtfReport;
import com.gene.app.util.DBUtil;

@SuppressWarnings("serial")
public class GetReport extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		List<GtfReport> gtfReports = getReport();
		gtfReports = calculateVarianceMap(gtfReports);
		req.setAttribute("gtfreports", gtfReports);
		DBUtil util = new DBUtil();
		String email = (String)req.getAttribute("email");
		BudgetSummary summary = util.readBudgetSummary(email,gtfReports);
		
		req.setAttribute("summary", summary);
		RequestDispatcher rd = req.getRequestDispatcher("/listProjects");
		try {
			rd.forward(req, resp);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<GtfReport> getReport() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setOrdering("flag asc");
		List<GtfReport> gtfList = new ArrayList<GtfReport>();
		try{
			List<GtfReport> results = (List<GtfReport>) q.execute();
			if(!results.isEmpty()){
			for(GtfReport p : results){
				gtfList.add(p);
			}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			q.closeAll();
		}
		return gtfList;
	}
	public List<GtfReport> calculateVarianceMap(List<GtfReport> gtfReports){
		List<GtfReport> rptList = new ArrayList<GtfReport>();
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		double benchMark = 0.0;
		double accrual = 0.0;
		double variance = 0.0;
		Map<String, Double> benchMarkMap;
		Map<String, Double> accrualsMap;
		Map<String, Double> varianceMap;
		GtfReport report = new GtfReport();
		for(int i=0;i<gtfReports.size();i++){
			report = gtfReports.get(i);
			benchMarkMap = report.getBenchmarkMap();
			accrualsMap = report.getAccrualsMap();
			varianceMap = report.getVariancesMap();
			for(int j=0;j<month;j++){
			benchMark = benchMarkMap.get(GtfReport.months[j]);
			accrual = accrualsMap.get(GtfReport.months[j]);
			variance = benchMark-accrual;
			varianceMap.put(GtfReport.months[j], variance);
			}
			report.setVariancesMap(varianceMap);
			rptList.add(report);
		}
		return rptList;
	}
}
