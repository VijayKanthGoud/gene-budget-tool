package com.gene.app.bean;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class UserRoleInfo implements Serializable {
	@Override
	public String toString() {
		return "UserRoleInfo [UserName=" + UserName + ", brand=" + brand
				+ ", cost_center=" + cost_center + ", email=" + email
				+ ", role=" + role + "]";
	}

	@Persistent
	private String UserName;

	// holds brand level budget
	@Persistent
	private Map<String,Double> brand;

	// holds cost center level budget
	@Persistent
	private Map<String,Double> cost_center;

	private String costCenter;
	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public void setBrand(Map<String, Double> brand) {
		this.brand = brand;
	}

	public void setCost_center(Map<String, Double> cost_center) {
		this.cost_center = cost_center;
	}

	@Persistent
	private String email;
	
	@Persistent
	private String role;

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	/*public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCost_center() {
		return cost_center;
	}

	public void setCost_center(String cost_center) {
		this.cost_center = cost_center;
	}*/

	public String getEmail() {
		return email;
	}

	public Map<String, Double> getBrand() {
		return brand;
	}

	public Map<String, Double> getCost_center() {
		return cost_center;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
