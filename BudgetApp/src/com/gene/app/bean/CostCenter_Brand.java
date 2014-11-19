package com.gene.app.bean;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class CostCenter_Brand  implements Serializable {
	
	@Persistent
	private String costCenter;
	@Persistent
	private String brandFromDB;
	
	private Map<String,Double> brand;
	public String getBrandFromDB() {
		return brandFromDB;
	}
	public void setBrandFromDB(String brandFromDB) {
		this.brandFromDB = brandFromDB;
	}
	public String getCostCenter() {
		return costCenter;
	}
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	public Map<String, Double> getBrand() {
		return brand;
	}
	public void setBrand(Map<String, Double> brand) {
		this.brand = brand;
	}
	

}
