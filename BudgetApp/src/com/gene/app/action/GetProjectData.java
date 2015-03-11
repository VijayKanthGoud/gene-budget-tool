package com.gene.app.action;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.PMF;
import com.gene.app.model.GtfReport;

@SuppressWarnings("serial")
public class GetProjectData extends HttpServlet {
	private final static Logger LOGGER = Logger
			.getLogger(GetProjectData.class.getName());
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
	}
	public void readProjectDataByProjectOwner(String projectOwner) {
		//LOGGER.log(Level.INFO, "results = ");
		//projectOwner = "Chris Sung";
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setFilter("requestor == requestorParam");
		q.declareParameters("String requestorParam");
		List<GtfReport> results = (List<GtfReport>) q.execute(projectOwner);
		if(!results.isEmpty()){
		for (GtfReport p : results) {
			LOGGER.log(Level.INFO, "getBrand = " + p.getRequestor());
			LOGGER.log(Level.INFO, "getPoNumber = " + p.getPoNumber());
		}
		}
	}
	
	/*public void readProjectDataByProjectOwnerAndBrand(String projectOwner,String brand) {
		projectOwner = "Chris Sung";
		brand = "Total Products";
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setFilter("requestor == requestorParam && brand == brandParam");
		q.declareParameters("String requestorParam,String brandParam");
		List<GtfReport> results = (List<GtfReport>) q.execute(projectOwner,brand);
		if(!results.isEmpty()){
		for (GtfReport p : results) {
			LOGGER.log(Level.INFO, "getBrand = " + p.getRequestor());
			LOGGER.log(Level.INFO, "getPoNumber = " + p.getPoNumber());
		}
		}
	}
	
	public void readProjectDataByProjectOwnerBrandAndCC(String projectOwner,String brand,String costCenter) {
		projectOwner = "Chris Sung";
		brand = "Total Products";
		costCenter = "costCenter1";
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setFilter("requestor == requestorParam && brand == brandParam && costCenter == costCenterParam");
		q.declareParameters("String requestorParam,String brandParam,String costCenterParam");
		List<GtfReport> results = (List<GtfReport>) q.execute(projectOwner,brand);
		if(!results.isEmpty()){
		for (GtfReport p : results) {
			LOGGER.log(Level.INFO, "getBrand = " + p.getRequestor());
			LOGGER.log(Level.INFO, "getPoNumber = " + p.getPoNumber());
		}
		}
	}*/
}
