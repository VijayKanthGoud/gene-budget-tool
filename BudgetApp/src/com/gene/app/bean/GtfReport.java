package com.gene.app.bean;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class GtfReport implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String Id;
	
	@Persistent
	private String gMemoryId;
	
	@Persistent
	private int flag;
	
	@Persistent
	private String remarks;
	
	@Persistent
	private String requestor;
	
	@Persistent
	private String projectName;

	@Persistent
	private String project_WBS;

	@Persistent
	private String WBS_Name;

	@Persistent
	private String subActivity;

	@Persistent
	private String brand;

	@Persistent
	private int percent_Allocation;

	@Persistent
	private String poNumber;

	@Persistent
	private String poDesc;

	@Persistent
	private String vendor;
	
	@Persistent
	private String status;

	@Persistent
	private Map<String, Double> benchmarkMap;
	
	@Persistent
	private Map<String, Double> plannedMap;
	
	@Persistent
	private Map<String, Double> accrualsMap;
	
	@Persistent
	private Map<String, Double> variancesMap;
	
	@Persistent
	private String email;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
			"JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "TOTAL" };

	public String getgMemoryId() {
		return gMemoryId;
	}

	public void setgMemoryId(String gMemoryId) {
		this.gMemoryId = gMemoryId;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getProject_WBS() {
		return project_WBS;
	}

	public void setProject_WBS(String project_WBS) {
		this.project_WBS = project_WBS;
	}

	public String getWBS_Name() {
		return WBS_Name;
	}

	public void setWBS_Name(String wBS_Name) {
		WBS_Name = wBS_Name;
	}

	public String getSubActivity() {
		return subActivity;
	}

	public void setSubActivity(String subActivity) {
		this.subActivity = subActivity;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public int getPercent_Allocation() {
		return percent_Allocation;
	}

	public void setPercent_Allocation(int percent_Allocation) {
		this.percent_Allocation = percent_Allocation;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getPoDesc() {
		return poDesc;
	}

	public void setPoDesc(String poDesc) {
		this.poDesc = poDesc;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public Map<String, Double> getBenchmarkMap() {
		return benchmarkMap;
	}

	public void setBenchmarkMap(Map<String, Double> benchmarkMap) {
		this.benchmarkMap = benchmarkMap;
	}

	public Map<String, Double> getPlannedMap() {
		return plannedMap;
	}

	public void setPlannedMap(Map<String, Double> plannedMap) {
		this.plannedMap = plannedMap;
	}

	public Map<String, Double> getAccrualsMap() {
		return accrualsMap;
	}

	public void setAccrualsMap(Map<String, Double> accrualsMap) {
		this.accrualsMap = accrualsMap;
	}

	public Map<String, Double> getVariancesMap() {
		return variancesMap;
	}

	public void setVariancesMap(Map<String, Double> variancesMap) {
		this.variancesMap = variancesMap;
	}
	
	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}
