package com.gene.app.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.gene.app.bean.BudgetSummary;
import com.gene.app.bean.CostCenter_Brand;
import com.gene.app.bean.GtfReport;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.server.PMF;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class DBUtil {
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	public UserRoleInfo readUserRoleInfo(String email) {
		boolean isGeneUser = false;
		Map<String,UserRoleInfo> userMap = new LinkedHashMap<String,UserRoleInfo>();
		userMap = readAllUserInfo();
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
	public Map<String,UserRoleInfo> readAllUserInfo(){
		//String key = costCenter+BudgetConstants.seperator+UserRoleInfo.class.getName();
		String key = UserRoleInfo.class.getName();
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
	public String getPrjEmailByName(String prj_owner){
		String email = "";
		Map<String,UserRoleInfo> userMap = readAllUserInfo();
		if(userMap!=null && !userMap.isEmpty()){
		for(Map.Entry<String, UserRoleInfo> user:userMap.entrySet()){
			if(prj_owner==null){
				email = "";
			}else{
				if(!"".equals(prj_owner.trim()) && prj_owner.equalsIgnoreCase(user.getValue().getUserName())){
					email = user.getValue().getEmail();
					break;
				}
			}
		}
		}
		return email;
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
		Double brandBudget = 0.0;
		Map<String,Double> brandMap = user.getBrand();
		Set<String> brandSet = new TreeSet<String>();
		brandSet = brandMap.keySet();
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
	}*/
	
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
		
	public void saveAllDataToDataStore(List<GtfReport> gtfReportList){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistentAll(gtfReportList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
	public Map<String,GtfReport> getReport(String costCenter, boolean resetCache) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setOrdering(BudgetConstants.GTFReportOrderingParameters_getReport);
		Map<String,GtfReport> gtfList = new LinkedHashMap<String,GtfReport>();
		try{
			List<GtfReport> results = (List<GtfReport>) q.execute();
			if(!results.isEmpty()){
			for(GtfReport p : results){
				gtfList.put(p.getgMemoryId(),p);
			}
			}
			if(resetCache){
				saveAllReportDataToCache(costCenter,gtfList);
			}
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
			gtfReportList = getReport(costCenter, true);
			}
		return gtfReportList;
	}
	
	public Map<String,GtfReport> getAllReportsByPrjName(String costCenter,String prjName,String email){
		Map<String,GtfReport> rptList = getAllReportDataFromCache(BudgetConstants.costCenter);
		Map<String,GtfReport> newRptList = new LinkedHashMap<String, GtfReport>(); 
		GtfReport gtfRpt = new GtfReport();
		List<GtfReport> listReports = new ArrayList<GtfReport>();
		if(rptList==null || rptList.isEmpty()){
			listReports=readProjectDataByProjectName(email,prjName) ;
			for(GtfReport nReport:listReports){
			newRptList.put(nReport.getgMemoryId(),nReport);
			}
		}else{
		for(Map.Entry<String, GtfReport> rptMap: rptList.entrySet()){
			gtfRpt = rptMap.getValue();
		if(prjName!=null && !"".equals(prjName) && prjName.equalsIgnoreCase(gtfRpt.getProjectName())){
			newRptList.put(rptMap.getKey(), gtfRpt);
		}
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
	
	public BudgetSummary readBudgetSummary(String costCenter){
		BudgetSummary summary = new BudgetSummary();
		String key = BudgetConstants.costCenter+BudgetConstants.seperator+CostCenter_Brand.class.getName();
		// read costcenter_brand table data and put it in cache
		List<CostCenter_Brand> costCenterList = (List<CostCenter_Brand>)cache.get(key);
		if(costCenterList==null || costCenterList.isEmpty()){
			costCenterList = readCostCenterBrandMappingData();
		}
		// prepare BudgetSummaryData
		Map<String,Map<String,BudgetSummary>> brandlevelBudgetMap = prepareBrandData(costCenterList);
		// get project list matching the brand
		Map<String,GtfReport> gtfRptList = getAllReportDataFromCache(BudgetConstants.costCenter);
		Map<String,BudgetSummary> brandMap = null;
		brandMap = getProjectListByBrand(gtfRptList,brandlevelBudgetMap,costCenter);
		if(brandMap == null){
			brandMap = new LinkedHashMap<String,BudgetSummary>();
		}
		summary.setBudgetMap(brandMap);
		cache.put(BudgetConstants.GMBT_SUMMARY+costCenter, summary);
		return summary;
	}
	
	public BudgetSummary getSummaryFromCache(String costCenter){
		BudgetSummary summary = (BudgetSummary)cache.get(BudgetConstants.GMBT_SUMMARY+costCenter);
		if(summary == null){
			summary = readBudgetSummary(costCenter);
		}
		return summary;
	}
	
	public void putSummaryToCache(BudgetSummary summary,String costCenter){
		cache.put(BudgetConstants.GMBT_SUMMARY+costCenter, summary);
	}
	// reading costcenter_brand table data and put it in cache
	
		public List<CostCenter_Brand> readCostCenterBrandMappingData(){
			String key = BudgetConstants.costCenter+BudgetConstants.seperator+CostCenter_Brand.class.getName();
			cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
			CostCenter_Brand cost_brand = new CostCenter_Brand();
			List<CostCenter_Brand> costCenterList = new ArrayList<CostCenter_Brand>();
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query q = pm.newQuery(CostCenter_Brand.class);
			//q.declareParameters("String emailParam");
			List<CostCenter_Brand> results = (List<CostCenter_Brand>) q.execute();
			if(!results.isEmpty()){
			for (CostCenter_Brand p : results) {
				cost_brand = p;
				costCenterList.add(cost_brand);
			}
			}
			cache.put(key, costCenterList);
			return costCenterList;
		}
		
		public Map<String,BudgetSummary> prepareBrandMap(String brands){
			Map<String,BudgetSummary> brandMap = new HashMap<String,BudgetSummary>();
			//Perjeta:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=30000.0;
			//Avastin:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=40000.0;
			//Tarceva:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=50000.0;
			//Onart:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=60000.0;
			String[] brandsArray = brands.split(";"); 
			String[] budgetArray = null;
			String[] valueArray = null;
			String value = "";
			BudgetSummary summary = new BudgetSummary();
			for(int i=0;i<brandsArray.length;i++){
				 summary = new BudgetSummary();
				value = brandsArray[i];
				budgetArray = value.split(":");
				for(int j=0;j<budgetArray.length;j++){
					valueArray = budgetArray[j].split("=");
					if(j==1){
						summary.setPlannedTotal(Double.parseDouble(valueArray[1]));
					}else if(j==2){
						summary.setAccrualTotal(Double.parseDouble(valueArray[1]));
					}else if(j==3){
						summary.setBenchmarkTotal(Double.parseDouble(valueArray[1]));
					}else if(j==4){
						summary.setVarianceTotal(Double.parseDouble(valueArray[1]));
					}else if(j==5){
						summary.setTotalBudget(Double.parseDouble(valueArray[1]));
					}
				}
				brandMap.put(budgetArray[0], summary);
			}
			return brandMap;
		}
		public Map<String,Map<String,BudgetSummary>> prepareBrandData(List<CostCenter_Brand> costCenterList){
			CostCenter_Brand costCenter_Brand = null; 
			Map<String,BudgetSummary> brandMap = null;
			Map<String,Map<String,BudgetSummary>> costCenterBrandMap = new LinkedHashMap<String,Map<String,BudgetSummary>>();
			if(costCenterList!=null && !costCenterList.isEmpty()){
				for(int i=0;i<costCenterList.size();i++){
					costCenter_Brand = (CostCenter_Brand)costCenterList.get(i);
					String brands = costCenter_Brand.getBrandFromDB();
					// prepare brandmap
					brandMap = prepareBrandMap(brands);
					costCenterBrandMap.put(costCenter_Brand.getCostCenter(), brandMap);
				}
			}
			return costCenterBrandMap;
		}
		public Map<String,BudgetSummary> getProjectListByBrand(Map<String, GtfReport> gtfRptList,Map<String,Map<String,BudgetSummary>> brandDataMap,String costCenter){
			Map<String,BudgetSummary> brandMap = new LinkedHashMap<String,BudgetSummary>();
			GtfReport gtfReport = new GtfReport();
			String brand = "";
			Double plannedTotal = 0.0;
			Double benchMarkTotal = 0.0;
			Double accrualTotal = 0.0;
			Double varianceTotal = 0.0;
			
			BudgetSummary summary = new BudgetSummary();
			for(Entry<String, Map<String, BudgetSummary>> brandEntry: brandDataMap.entrySet()){
				if(costCenter.equalsIgnoreCase(brandEntry.getKey())){
				brandMap = brandEntry.getValue();
				for(Entry<String, BudgetSummary> budgetEntry: brandMap.entrySet()){
					brand = budgetEntry.getKey();
					summary = budgetEntry.getValue();
					plannedTotal = 0.0;
					benchMarkTotal = 0.0;
					accrualTotal = 0.0;
					varianceTotal = 0.0;
					//for(int i=0;i<gtfRptList.size();i++){
					if(gtfRptList!=null && !gtfRptList.isEmpty()){
					for(Entry<String, GtfReport> gtfRptEnty:gtfRptList.entrySet()){
						gtfReport =gtfRptEnty.getValue();
						if(brand.equalsIgnoreCase(gtfReport.getBrand())){
							for(int j=0;j<BudgetConstants.months.length-1;j++){
									if (gtfReport.getPlannedMap() != null) {
										plannedTotal = plannedTotal
												+ gtfReport.getPlannedMap().get(
														BudgetConstants.months[j]);
									}
									if (gtfReport.getBenchmarkMap() != null) {
										benchMarkTotal = benchMarkTotal
												+ gtfReport.getBenchmarkMap().get(
														BudgetConstants.months[j]);
									}
									if (gtfReport.getAccrualsMap() != null) {
										accrualTotal = accrualTotal
												+ gtfReport.getAccrualsMap().get(
														BudgetConstants.months[j]);
									}
									if (gtfReport.getVariancesMap() != null) {
										varianceTotal = varianceTotal
												+ gtfReport.getVariancesMap().get(
														BudgetConstants.months[j]);
									}
							}
						}
					}
					}
					summary.setAccrualTotal(accrualTotal);
					summary.setVarianceTotal(varianceTotal);
					summary.setPlannedTotal(plannedTotal);
					summary.setBenchmarkTotal(benchMarkTotal);
					summary.setBudgetLeftToSpend(summary.getTotalBudget()-summary.getPlannedTotal());
					brandMap.put(brand, summary);
				}
				break;
				}
			}
			return brandMap;
		}
		
	public void storeProjectsToCache(List<GtfReport> gtfReports) {
		Map<String, GtfReport> gtfReportFromCache = getAllReportDataFromCache(BudgetConstants.costCenter);
		GtfReport report = new GtfReport();
		for (int i = 0; i < gtfReports.size(); i++) {
			report = gtfReports.get(i);
			gtfReportFromCache.put(report.getgMemoryId(), report);
		}
		cache.put(BudgetConstants.costCenter, gtfReportFromCache);
	}

	@SuppressWarnings("unchecked")
	public void updateReports() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setFilter("status==statusParam");
		q.declareParameters("String statusParam");
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		Map<String, GtfReport> gtfMap = new LinkedHashMap<String, GtfReport>();
		Map<String, Double> plannedMap = new LinkedHashMap<String, Double>();
		Map<String, Double> accrualsMap = new LinkedHashMap<String, Double>();
		try {
			gtfReports = (List<GtfReport>) q.execute("Active");
			if (!gtfReports.isEmpty()) {
				
				for (GtfReport gtfReport : gtfReports) {
					plannedMap.clear();
					plannedMap.putAll(gtfReport.getPlannedMap());
					gtfReport.setBenchmarkMap(plannedMap);
					accrualsMap.clear();
					accrualsMap.putAll(gtfReport.getAccrualsMap());
					gtfReport.setPlannedMap(accrualsMap);
					gtfMap.put(gtfReport.getgMemoryId(), gtfReport);
				}
			}
			pm.makePersistentAll(gtfReports);
			saveAllReportDataToCache(BudgetConstants.costCenter, gtfMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
	}
		
		public double round(double value, int places) {
		    if (places < 0) throw new IllegalArgumentException();

		    BigDecimal bd = new BigDecimal(value);
		    bd = bd.setScale(places, RoundingMode.HALF_UP);
		    return bd.doubleValue();
		}
}
