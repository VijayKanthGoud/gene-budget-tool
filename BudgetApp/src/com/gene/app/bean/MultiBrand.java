package com.gene.app.bean;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class MultiBrand implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Persistent
	private String brand;
	@Persistent
	private double percent_allocation;
	@Persistent
	private double totalValue;
	@Persistent
	private String project_Name;
	@Persistent
	private String gMemeoriID;

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public double getPercent_allocation() {
		return percent_allocation;
	}

	public void setPercent_allocation(double percent_allocation) {
		this.percent_allocation = percent_allocation;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	public String getProject_Name() {
		return project_Name;
	}

	public void setProject_Name(String project_Name) {
		this.project_Name = project_Name;
	}

	public String getgMemeoriID() {
		return gMemeoriID;
	}

	public void setgMemeoriID(String gMemeoriID) {
		this.gMemeoriID = gMemeoriID;
	}

}
