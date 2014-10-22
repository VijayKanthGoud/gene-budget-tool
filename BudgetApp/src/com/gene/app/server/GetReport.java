package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.bean.GtfReport;

@SuppressWarnings("serial")
public class GetReport extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		List<GtfReport> gtfReports = getReport();
		req.setAttribute("gtfreports", gtfReports);
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

}
