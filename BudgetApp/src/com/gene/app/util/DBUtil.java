package com.gene.app.util;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.gene.app.bean.BudgetSummary;
import com.gene.app.bean.GtfReport;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.server.PMF;

public class DBUtil {

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
			}
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
}
