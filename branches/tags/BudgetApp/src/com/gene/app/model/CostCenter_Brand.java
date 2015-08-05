package com.gene.app.model;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class CostCenter_Brand  implements Serializable {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String Id;
	
	@Persistent
	private String costCenter;
	@Persistent
	private Text brandFromDB;
	
	private Map<String,Double> brand;
	public String getBrandFromDB() {
		return brandFromDB.getValue();
	}
	public void setBrandFromDB(Text brandFromDB) {
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
