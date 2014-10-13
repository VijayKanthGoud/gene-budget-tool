package com.gene.app.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
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
				//Query q = pm.newQuery(GtfReport.class);
				List<GtfReport> results = new ArrayList<GtfReport>();
				Extent<GtfReport> extent = pm.getExtent(GtfReport.class, false);
				for (GtfReport p : extent) {
					results.add(p);
					//System.out.println("results = "+p.getBrand());				
				}
				extent.closeAll();
				return results;
				}
		/*List<GtfReport> gtfList = new ArrayList<GtfReport>();
		Iterable<Entity> gtfReports = Util.listEntities("GtfReport", null, null);
		
		for(Entity gtfReportEntity : gtfReports){
			GtfReport gtfReport = new GtfReport();
			List<Object> d = new ArrayList<>();
			d=(List<Object>) gtfReportEntity.getProperty("forecastMap");
			
			System.out.println("d is"+d);
			Map<String, Double> forecastMap = new LinkedHashMap<>();
			for(int i=0; i< d.size(); i++){
				forecastMap.put((String) d.get(i), Double.parseDouble(d.get(++i).toString()));
			}
			gtfReport.setForecastMap(forecastMap);
			gtfReport.setgMemoryId((int)gtfReportEntity.getProperty("gMemoryId"));
			gtfReport.setPercent_Allocation((int)gtfReportEntity.getProperty("brand"));
			gtfReport.setPoDesc(gtfReportEntity.getProperty("poDesc").toString());
			gtfReport.setPoNumber(gtfReportEntity.getProperty("poNumber").toString());
			gtfReport.setProject_WBS(gtfReportEntity.getProperty("project_WBS").toString());
			gtfReport.setRequestor(gtfReportEntity.getProperty("requestor").toString());
			gtfReport.setSubActivity(gtfReportEntity.getProperty("subActivity").toString());
			gtfReport.setVendor(gtfReportEntity.getProperty("vendor").toString());
			gtfReport.setWBS_Name(gtfReportEntity.getProperty("WBS_Name").toString());
			
			gtfList.add(gtfReport);
			
		}
		System.out.println(gtfList);
	}*/

}
