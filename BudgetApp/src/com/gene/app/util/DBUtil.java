package com.gene.app.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
	public UserRoleInfo readUserRoleInfo(String email,String costCenter) {
		boolean isGeneUser = false;
		Map<String,UserRoleInfo> userMap = new LinkedHashMap<String,UserRoleInfo>();
		userMap = readAllUserInfo(costCenter);
		UserRoleInfo user = new UserRoleInfo();
		if(userMap!=null && !userMap.isEmpty()){
		user = userMap.get(email);
		if(user!=null){
			return user;
		}else{
			user = readUserInfoFromDB(email);
		}
		}
		return user;
	}
	
	public UserRoleInfo readUserInfoFromDB(String email){
		UserRoleInfo user = new UserRoleInfo();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(UserRoleInfo.class);
		q.declareParameters("String emailParam");
		List<UserRoleInfo> results = (List<UserRoleInfo>) q.execute(email);
		if(!results.isEmpty()){
		for (UserRoleInfo p : results) {
			user = p;
		}
		}
		return user;
	}
	public Map<String,UserRoleInfo> readAllUserInfo(String costCenter){
		String key = costCenter+BudgetConstants.seperator+UserRoleInfo.class.getName();
		Map<String,UserRoleInfo> userMap = new LinkedHashMap<String,UserRoleInfo>();
		userMap = (Map<String,UserRoleInfo>)cache.get(key);
		if(userMap==null || userMap.isEmpty()){
		userMap =  new LinkedHashMap<String,UserRoleInfo>();
		UserRoleInfo userInfo = new UserRoleInfo();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(UserRoleInfo.class);
		List<UserRoleInfo> results = (List<UserRoleInfo>) q.execute();
		if (results!=null && !results.isEmpty()) {
			for(UserRoleInfo role : results)
			{ 
				userInfo = role; 
				userMap.put(userInfo.getEmail(), userInfo);
			}
		}
		cache.put(key, userMap);
		}
		return userMap;
		
	}
	
	public Map<String,BudgetSummary> readAllBudgetSummary(String costCenter){
		String key = costCenter+BudgetConstants.seperator+BudgetSummary.class.getName();
		Map<String,BudgetSummary> budgetMap = new LinkedHashMap<String,BudgetSummary>();
		budgetMap = (Map<String,BudgetSummary>)cache.get(key);
		if(budgetMap==null || budgetMap.isEmpty()){
			budgetMap = new LinkedHashMap<String,BudgetSummary>();
			BudgetSummary budgetInfo = new BudgetSummary();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(BudgetSummary.class);
		List<BudgetSummary> results = (List<BudgetSummary>) q.execute();
		if (!results.isEmpty()) {
			for(BudgetSummary budget : results)
			{ 
				budgetInfo = budget; 
				budgetMap.put(budgetInfo.getProjectOwnerEmail(), budgetInfo);
			}
		}
		cache.put(key, budgetMap);
		}
		return budgetMap;
		
	}
	public BudgetSummary readBudgetSummaryFromDB(String email,String costCenter) {
		boolean isGeneUser = false;
		String key = costCenter+BudgetConstants.seperator+BudgetSummary.class.getName();
		Map<String,BudgetSummary> budgetMap = new LinkedHashMap<String,BudgetSummary>();
		budgetMap = readAllBudgetSummary(costCenter);
		BudgetSummary summary = new BudgetSummary();
		if(budgetMap!=null && !budgetMap.isEmpty()){
		summary = budgetMap.get(email);
		}
		return summary;
	}
	
	/*public BudgetSummary readUserBasedBudgetFromDB(String email,String costCenter) {
		boolean isGeneUser = false;
		String key = costCenter+BudgetConstants.seperator+BudgetSummary.class.getName();
		Map<String,BudgetSummary> budgetMap = new LinkedHashMap<String,BudgetSummary>();
		budgetMap = readAllBudgetSummary(costCenter);
		BudgetSummary summary = new BudgetSummary();
		if(budgetMap!=null && !budgetMap.isEmpty()){
		summary = budgetMap.get(email);
		}
		return summary;
	}*/

	/*public BudgetSummary readBudgetSummary(String email,String costCenter,
			List<GtfReport> gtfReports,UserRoleInfo user) {
		double benchMarkTotal = 0.0;
		double varianceTotal = 0.0;
		double plannedTotal = 0.0;
		double accrualsTotal = 0.0;
		double tempBenchMarkTotal = 0.0;
		double tempVarianceTotal = 0.0;
		double tempPlannedTotal = 0.0;
		double tempAccrualsTotal = 0.0;
		BudgetSummary summaryFromDB = null;
		//summaryFromDB = readBudgetSummaryFromDB(email,costCenter);
		GtfReport report = null;
		String month = "";
		Object[] brand = {};
		String brandValue = "";
		Set<String> brandSet = new TreeSet<String>();
		Map<String,BudgetSummary> budgetMap = new LinkedHashMap<String,BudgetSummary>();
		BudgetSummary summaryByBrand = new BudgetSummary();
		if (gtfReports != null && !gtfReports.isEmpty()) {
			for(int i=0;i<gtfReports.size();i++){
				brandSet.add(gtfReports.get(i).getBrand());
			}
			System.out.println("brandset = "+brandSet);
			brand = brandSet.toArray();
			for(int k = 0; k<brand.length; k++){
				brandValue = brand[k].toString();
			for (int i = 0; i < gtfReports.size(); i++) {
				report = (GtfReport) gtfReports.get(i);
				if(brandValue!=null && !"".equalsIgnoreCase(brandValue.trim()) && brandValue.equals(report.getBrand())){
					summaryByBrand = budgetMap.get(brandValue);
					if(summaryByBrand==null){
						summaryByBrand = new BudgetSummary();
					}
				benchMarkTotal = 0.0;
				varianceTotal = 0.0;
				plannedTotal = 0.0;
				accrualsTotal = 0.0;
				for(int j=0;j<BudgetConstants.months.length-1;j++){
					month = BudgetConstants.months[j];
				if(report.getBenchmarkMap()!=null){
				benchMarkTotal = benchMarkTotal	+ report.getBenchmarkMap().get(month);
				}if(report.getPlannedMap()!=null){
				plannedTotal = plannedTotal	+ report.getPlannedMap().get(month);
				}if(report.getVariancesMap()!=null){
				varianceTotal = varianceTotal + report.getVariancesMap().get(month);
				}if(report.getAccrualsMap()!=null){
				accrualsTotal = accrualsTotal + report.getAccrualsMap().get(month);
				}
				}
				summaryByBrand.setTotalBudget(summaryFromDB.getTotalBudget());
				summaryByBrand.setProjectOwnerEmail(summaryFromDB.getProjectOwnerEmail());
				tempPlannedTotal = summaryByBrand.getPlannedTotal();
				summaryByBrand.setPlannedTotal(plannedTotal+tempPlannedTotal);
				tempBenchMarkTotal = summaryByBrand.getBenchmarkTotal();
				summaryByBrand.setBenchmarkTotal(benchMarkTotal+tempBenchMarkTotal);
				tempVarianceTotal = summaryByBrand.getVarianceTotal();
				summaryByBrand.setVarianceTotal(varianceTotal+tempVarianceTotal);
				tempAccrualsTotal = summaryByBrand.getAccrualTotal();
				summaryByBrand.setAccrualTotal(accrualsTotal+tempAccrualsTotal);
				summaryByBrand.setBudgetLeftToSpend(summaryFromDB.getTotalBudget() - summaryByBrand.getAccrualTotal());
				if(summaryByBrand.getBenchmarkTotal() == 0){
					summaryByBrand.setBenchmarkTotal(1);
					summaryByBrand.setPercentageVarianceTotal(0);
				}else{
							summaryByBrand
									.setPercentageVarianceTotal((summaryByBrand
											.getBenchmarkTotal() - summaryByBrand
											.getAccrualTotal())
											/ summaryByBrand
													.getBenchmarkTotal());
				}
				budgetMap.put(brandValue, summaryByBrand);
				}
				
			}
			
			}
		}
		BudgetSummary summary = new BudgetSummary();
		summary.setBudgetMap(budgetMap);
		double variancePercentage = 0.0;
		summary.setTotalBudget(summaryFromDB.getTotalBudget());
		summary.setProjectOwnerEmail(summaryFromDB.getProjectOwnerEmail());
		summary.setPlannedTotal(plannedTotal);
		summary.setBenchmarkTotal(benchMarkTotal);
		summary.setVarianceTotal(varianceTotal);
		summary.setBudgetLeftToSpend(summaryFromDB.getTotalBudget()-accrualsTotal);
		summary.setAccrualTotal(accrualsTotal);
		if(benchMarkTotal == 0){
			benchMarkTotal = 1;
			variancePercentage = 0;
		}else{
			variancePercentage = (benchMarkTotal-accrualsTotal)/benchMarkTotal;
		}
		summary.setPercentageVarianceTotal(variancePercentage);
		return summary;
	}
		*/
	
	public BudgetSummary readBudgetSummary(String email,String costCenter,
			List<GtfReport> gtfReports,UserRoleInfo user) {
		double benchMarkTotal = 0.0;
		double varianceTotal = 0.0;
		double plannedTotal = 0.0;
		double accrualsTotal = 0.0;
		double tempBenchMarkTotal = 0.0;
		double tempVarianceTotal = 0.0;
		double tempPlannedTotal = 0.0;
		double tempAccrualsTotal = 0.0;
		BudgetSummary summaryFromDB = null;
		//summaryFromDB = readBudgetSummaryFromDB(email,costCenter);
		GtfReport report = null;
		String month = "";
		Object[] brand = {};
		String brandValue = "";
		Double brandBudget = 0.0;
		Map<String,Double> brandMap = user.getBrand();
		Set<String> brandSet = new TreeSet<String>();
		brandSet = brandMap.keySet();
		Map<String,BudgetSummary> budgetMap = new LinkedHashMap<String,BudgetSummary>();
		BudgetSummary summaryByBrand = new BudgetSummary();
		if (gtfReports != null && !gtfReports.isEmpty()) {
			/*for(int i=0;i<gtfReports.size();i++){
				brandSet.add(gtfReports.get(i).getBrand());
			}*/
			System.out.println("brandset = "+brandSet);
			brand = brandSet.toArray();
			for(int k = 0; k<brand.length; k++){
				brandValue = brand[k].toString();
				brandBudget = brandMap.get(brandValue);
			for (int i = 0; i < gtfReports.size(); i++) {
				report = (GtfReport) gtfReports.get(i);
				if(brandValue!=null && !"".equalsIgnoreCase(brandValue.trim()) && brandValue.equals(report.getBrand())){
					summaryByBrand = budgetMap.get(brandValue);
					if(summaryByBrand==null){
						summaryByBrand = new BudgetSummary();
					}
				benchMarkTotal = 0.0;
				varianceTotal = 0.0;
				plannedTotal = 0.0;
				accrualsTotal = 0.0;
				for(int j=0;j<BudgetConstants.months.length-1;j++){
					month = BudgetConstants.months[j];
				if(report.getBenchmarkMap()!=null){
				benchMarkTotal = benchMarkTotal	+ report.getBenchmarkMap().get(month);
				}if(report.getPlannedMap()!=null){
				plannedTotal = plannedTotal	+ report.getPlannedMap().get(month);
				}if(report.getVariancesMap()!=null){
				varianceTotal = varianceTotal + report.getVariancesMap().get(month);
				}if(report.getAccrualsMap()!=null){
				accrualsTotal = accrualsTotal + report.getAccrualsMap().get(month);
				}
				}
				summaryByBrand.setTotalBudget(brandBudget);
				summaryByBrand.setProjectOwnerEmail(email);
				tempPlannedTotal = summaryByBrand.getPlannedTotal();
				summaryByBrand.setPlannedTotal(plannedTotal+tempPlannedTotal);
				tempBenchMarkTotal = summaryByBrand.getBenchmarkTotal();
				summaryByBrand.setBenchmarkTotal(benchMarkTotal+tempBenchMarkTotal);
				tempVarianceTotal = summaryByBrand.getVarianceTotal();
				summaryByBrand.setVarianceTotal(varianceTotal+tempVarianceTotal);
				tempAccrualsTotal = summaryByBrand.getAccrualTotal();
				summaryByBrand.setAccrualTotal(accrualsTotal+tempAccrualsTotal);
				summaryByBrand.setBudgetLeftToSpend(brandBudget - summaryByBrand.getAccrualTotal());
				if(summaryByBrand.getBenchmarkTotal() == 0){
					summaryByBrand.setBenchmarkTotal(1);
					summaryByBrand.setPercentageVarianceTotal(0);
				}else{
							summaryByBrand
									.setPercentageVarianceTotal((summaryByBrand
											.getBenchmarkTotal() - summaryByBrand
											.getAccrualTotal())
											/ summaryByBrand
													.getBenchmarkTotal());
				}
				budgetMap.put(brandValue, summaryByBrand);
				}
				
			}
			
			}
		}
		BudgetSummary summary = new BudgetSummary();
		summary.setBudgetMap(budgetMap);
		double variancePercentage = 0.0;
		summary.setTotalBudget(brandBudget);
		summary.setProjectOwnerEmail(email);
		summary.setPlannedTotal(plannedTotal);
		summary.setBenchmarkTotal(benchMarkTotal);
		summary.setVarianceTotal(varianceTotal);
		summary.setBudgetLeftToSpend(brandBudget-accrualsTotal);
		summary.setAccrualTotal(accrualsTotal);
		if(benchMarkTotal == 0){
			benchMarkTotal = 1;
			variancePercentage = 0;
		}else{
			variancePercentage = (benchMarkTotal-accrualsTotal)/benchMarkTotal;
		}
		summary.setPercentageVarianceTotal(variancePercentage);
		return summary;
	}
	
	public void saveAllReportDataToCache(String costCenter,Map<String,GtfReport> gtfReportList){
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
		
	public Map<String,GtfReport> getReport(String costCenter) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setOrdering(BudgetConstants.GTFReportOrderingParameters_getReport);
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
			pm.close();
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
					benchMark = benchMarkMap.get(BudgetConstants.months[j]);
				}if(accrualsMap!=null){
					accrual = accrualsMap.get(BudgetConstants.months[j]);
				}
			variance = benchMark-accrual;
			if(varianceMap!=null){
			varianceMap.put(BudgetConstants.months[j], variance);
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
	
	public Map<String,GtfReport> getAllReportsByPrjName(String costCenter,String prjName,String email){
		Map<String,GtfReport> rptList = (Map<String,GtfReport>)cache.get(BudgetConstants.costCenter);
		Map<String,GtfReport> newRptList = new LinkedHashMap<String, GtfReport>(); 
		GtfReport gtfRpt = new GtfReport();
		for(Map.Entry<String, GtfReport> rptMap: rptList.entrySet()){
			gtfRpt = rptMap.getValue();
		if(prjName!=null && !"".equals(prjName) && prjName.equalsIgnoreCase(gtfRpt.getProjectName())){
			newRptList.put(rptMap.getKey(), gtfRpt);
		}
		}
		return newRptList;
	}
	
	// methods for multibrand
	public List<GtfReport> readProjectDataByProjectName(String email,String projectName) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query q = pm.newQuery(GtfReport.class);
		if("".equals(projectName)){
			q.setFilter("email==emailParam");
			q.declareParameters("String emailParam");
		}else{
			q.setFilter("projectName == projectNameParam && email==emailParam");
			q.declareParameters("String projectNameParam,String emailParam");
		}
		
		
		//try {
		List<GtfReport> results = (List<GtfReport>) q.execute(projectName,email);
		if(!results.isEmpty()){
		for (GtfReport p : results) {
			gtfReportList.add(p);
		}
		}/*}catch(Exception e){
			e.printStackTrace();
		}finally {
			q.closeAll();
			pm.close();
		}*/
		pm.close();
		return gtfReportList;
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
	public void removeExistingProject(List<GtfReport> gtfReports) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.deletePersistentAll(gtfReports);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
}
