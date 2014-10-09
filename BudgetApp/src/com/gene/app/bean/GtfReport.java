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

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String gMemoryId;

	@Persistent
	private String requestor;

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
	private Map<String, Double> forecastMap;

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

	public Map<String, Double> getForecastMap() {
		return forecastMap;
	}

	public void setForecastMap(Map<String, Double> forecastMap) {
		this.forecastMap = forecastMap;
	}
}
