package com.gene.app.bean;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
@PersistenceCapable
public class BudgetSummary  implements Serializable {

	@Override
	public String toString() {
		return "BudgetSummary [projectOwnerEmail=" + projectOwnerEmail
				+ ", totalBudget=" + totalBudget + ", budgetLeftToSpend="
				+ budgetLeftToSpend + ", benchmarkTotal=" + benchmarkTotal
				+ ", plannedTotal=" + plannedTotal + ", varianceTotal="
				+ varianceTotal + ", accrualTotal=" + accrualTotal
				+ ", budgetMap=" + budgetMap + ", percentageVarianceTotal="
				+ percentageVarianceTotal + "]";
	}

	@Persistent
	private String projectOwnerEmail;
	
	@Persistent
	private double totalBudget;
	
	private double budgetLeftToSpend;
	
	private double benchmarkTotal;
	
	private double plannedTotal;
	
	private double varianceTotal;
	
	private double accrualTotal;
	
	private Map<String,BudgetSummary> budgetMap;
	
	public Map<String, BudgetSummary> getBudgetMap() {
		return budgetMap;
	}

	public void setBudgetMap(Map<String, BudgetSummary> budgetMap) {
		this.budgetMap = budgetMap;
	}

	public double getAccrualTotal() {
		return accrualTotal;
	}

	public void setAccrualTotal(double accrualTotal) {
		this.accrualTotal = accrualTotal;
	}

	private double percentageVarianceTotal;
		
	public String getProjectOwnerEmail() {
		return projectOwnerEmail;
	}

	public void setProjectOwnerEmail(String projectOwnerEmail) {
		this.projectOwnerEmail = projectOwnerEmail;
	}

	public double getTotalBudget() {
		return totalBudget;
	}

	public void setTotalBudget(double totalBudget) {
		this.totalBudget = totalBudget;
	}

	public double getBudgetLeftToSpend() {
		return budgetLeftToSpend;
	}

	public void setBudgetLeftToSpend(double budgetLeftToSpend) {
		this.budgetLeftToSpend = budgetLeftToSpend;
	}

	public double getBenchmarkTotal() {
		return benchmarkTotal;
	}

	public void setBenchmarkTotal(double benchmarkTotal) {
		this.benchmarkTotal = benchmarkTotal;
	}

	public double getPlannedTotal() {
		return plannedTotal;
	}

	public void setPlannedTotal(double plannedTotal) {
		this.plannedTotal = plannedTotal;
	}

	public double getVarianceTotal() {
		return varianceTotal;
	}

	public void setVarianceTotal(double varianceTotal) {
		this.varianceTotal = varianceTotal;
	}

	public double getPercentageVarianceTotal() {
		return percentageVarianceTotal;
	}

	public void setPercentageVarianceTotal(double percentageVarianceTotal) {
		this.percentageVarianceTotal = percentageVarianceTotal;
	}

	
}
