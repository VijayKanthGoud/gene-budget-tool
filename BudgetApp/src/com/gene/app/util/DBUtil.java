package com.gene.app.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.gene.app.bean.BudgetSummary;
import com.gene.app.bean.GtfReport;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.server.PMF;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sun.org.apache.xpath.internal.operations.Gte;

public class DBUtil {
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	public boolean readUserRoleInfo(String email) {
		boolean isGeneUser = false;
		UserRoleInfo userInfo = new UserRoleInfo();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(UserRoleInfo.class);
		q.setFilter("email == emailParam");
		q.declareParameters("String emailParam");
		List<UserRoleInfo> results = (List<UserRoleInfo>) q.execute(email);
		if (!results.isEmpty()) {
			/*
			 * for(UserRoleInfo role : results){ userInfo = role; }
			 */
			isGeneUser = true;
		}
		return isGeneUser;
	}
	
	public BudgetSummary readBudgetSummaryFromDB(String email) {
		boolean isGeneUser = false;
		BudgetSummary summary = new BudgetSummary();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(BudgetSummary.class);
		q.setFilter("projectOwnerEmail == emailParam");
		q.declareParameters("String emailParam");
		List<BudgetSummary> results = (List<BudgetSummary>) q.execute(email);
		if (!results.isEmpty()) {
			for(BudgetSummary summary1 : results){ 
				summary = summary1; 
			}			
		}
		return summary;
	}

	public BudgetSummary readBudgetSummary(String email,
			List<GtfReport> gtfReports) {
		double benchMarkTotal = 0.0;
		double varianceTotal = 0.0;
		double plannedTotal = 0.0;
		double accrualsTotal = 0.0;
		double percentageVarianceTotal = 0.0;
		double budgetLeftToSpend = 0.0;
		BudgetSummary summaryFromDB = null;
		summaryFromDB = readBudgetSummaryFromDB(email);
		GtfReport report = null;
		if (gtfReports != null && !gtfReports.isEmpty()) {
			for (int i = 0; i < gtfReports.size(); i++) {
				report = (GtfReport) gtfReports.get(i);
				if(report.getBenchmarkMap()!=null){
				benchMarkTotal = benchMarkTotal
						+ report.getBenchmarkMap().get("JAN")
						+ report.getBenchmarkMap().get("FEB")
						+ report.getBenchmarkMap().get("MAR")
						+ report.getBenchmarkMap().get("APR")
						+ report.getBenchmarkMap().get("MAY")
						+ report.getBenchmarkMap().get("JUN")
						+ report.getBenchmarkMap().get("JUL")
						+ report.getBenchmarkMap().get("AUG")
						+ report.getBenchmarkMap().get("SEP")
						+ report.getBenchmarkMap().get("OCT")
						+ report.getBenchmarkMap().get("NOV")
						+ report.getBenchmarkMap().get("DEC");
				}if(report.getPlannedMap()!=null){
				plannedTotal = plannedTotal
						+ report.getPlannedMap().get("JAN")
						+ report.getPlannedMap().get("FEB")
						+ report.getPlannedMap().get("MAR")
						+ report.getPlannedMap().get("APR")
						+ report.getPlannedMap().get("MAY")
						+ report.getPlannedMap().get("JUN")
						+ report.getPlannedMap().get("JUL")
						+ report.getPlannedMap().get("AUG")
						+ report.getPlannedMap().get("SEP")
						+ report.getPlannedMap().get("OCT")
						+ report.getPlannedMap().get("NOV")
						+ report.getPlannedMap().get("DEC");
				}if(report.getVariancesMap()!=null){
				varianceTotal = varianceTotal
						+ report.getVariancesMap().get("JAN")
						+ report.getVariancesMap().get("FEB")
						+ report.getVariancesMap().get("MAR")
						+ report.getVariancesMap().get("APR")
						+ report.getVariancesMap().get("MAY")
						+ report.getVariancesMap().get("JUN")
						+ report.getVariancesMap().get("JUL")
						+ report.getVariancesMap().get("AUG")
						+ report.getVariancesMap().get("SEP")
						+ report.getVariancesMap().get("OCT")
						+ report.getVariancesMap().get("NOV")
						+ report.getVariancesMap().get("DEC");
				}if(report.getAccrualsMap()!=null){
				accrualsTotal = accrualsTotal
						+ report.getAccrualsMap().get("JAN")
						+ report.getAccrualsMap().get("FEB")
						+ report.getAccrualsMap().get("MAR")
						+ report.getAccrualsMap().get("APR")
						+ report.getAccrualsMap().get("MAY")
						+ report.getAccrualsMap().get("JUN")
						+ report.getAccrualsMap().get("JUL")
						+ report.getAccrualsMap().get("AUG")
						+ report.getAccrualsMap().get("SEP")
						+ report.getAccrualsMap().get("OCT")
						+ report.getAccrualsMap().get("NOV")
						+ report.getAccrualsMap().get("DEC");
			}}
		}
		BudgetSummary summary = new BudgetSummary();
		double variancePercentage = 0.0;
		/*if(summaryFromDB == null){
			summary.setTotalBudget(60000.0);
			summary.setProjectOwnerEmail(email);
		}else{*/
		summary.setTotalBudget(summaryFromDB.getTotalBudget());
		summary.setProjectOwnerEmail(summaryFromDB.getProjectOwnerEmail());
		//}
		summary.setPlannedTotal(plannedTotal);
		summary.setBenchmarkTotal(benchMarkTotal);
		summary.setVarianceTotal(varianceTotal);
		summary.setBudgetLeftToSpend(summaryFromDB.getTotalBudget()-accrualsTotal);
		if(benchMarkTotal == 0){
			benchMarkTotal = 1;
			variancePercentage = 0;
		}else{
			variancePercentage = (benchMarkTotal-accrualsTotal)/benchMarkTotal;
		}
		summary.setPercentageVarianceTotal(variancePercentage);
		return summary;
	}
	
	public void saveReportDataToCache(GtfReport gtfReport){
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		String key = gtfReport.getId().toString();
		cache.put(key, gtfReport);
	}
	
	public void saveAllReportDataToCache(String costCenter,Map<String,GtfReport> gtfReportList){
		for(Map.Entry<String, GtfReport> gtfEntry:gtfReportList.entrySet()){
			GtfReport gtfReport = gtfEntry.getValue();
		}
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		cache.put(costCenter, gtfReportList);
	}
	
	public GtfReport readReportDataFromCache(String key,String costCenter){
		GtfReport gtfReportObj = new GtfReport();
		Map<String,GtfReport> gtfReports = new LinkedHashMap<String,GtfReport>();
		gtfReports = getAllReportDataFromCache(costCenter);
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		if(key!=null && !"".equals(key.trim())){
			gtfReportObj = (GtfReport)gtfReports.get(key);
		}
		return gtfReportObj;
	}
	
	public void saveDataToDataStore(GtfReport gtfReport){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(gtfReport);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
	
	public List<GtfReport> getDataFromDataStore(String key) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setFilter("key == keyParam");
		q.declareParameters("String keyParam");
		q.setOrdering("flag asc");
		List<GtfReport> gtfList = new ArrayList<GtfReport>();
		try{
			List<GtfReport> results = (List<GtfReport>) q.execute(key);
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
	
	public void generateProjectIdUsingJDOTxn(List<GtfReport> gtfReports) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistentAll(gtfReports);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
	
	public Map<String,GtfReport> getReport(String costCenter) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setOrdering("flag asc, projectName asc");
		Map<String,GtfReport> gtfList = new LinkedHashMap<String,GtfReport>();
		try{
			List<GtfReport> results = (List<GtfReport>) q.execute();
			if(!results.isEmpty()){
			for(GtfReport p : results){
				gtfList.put(p.getId(),p);
			}
			}
			saveAllReportDataToCache(costCenter,gtfList);
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
				if(benchMarkMap!=null){
					benchMark = benchMarkMap.get(GtfReport.months[j]);
				}if(accrualsMap!=null){
					accrual = accrualsMap.get(GtfReport.months[j]);
				}
			variance = benchMark-accrual;
			if(varianceMap!=null){
				//varianceMap = new HashMap<String, Double>();
			
			varianceMap.put(GtfReport.months[j], variance);
			}}
			report.setVariancesMap(varianceMap);
			rptList.add(report);
		}
		return rptList;
	}
	
	public Map<String,GtfReport> getAllReportDataFromCache(String costCenter){
		Map<String,GtfReport> gtfReportList = new LinkedHashMap<String,GtfReport>();
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		gtfReportList = (Map<String,GtfReport>)cache.get(costCenter);
		if(gtfReportList==null || gtfReportList.size()==0){
			gtfReportList = getReport(costCenter);
			}
		return gtfReportList;
	}
}
