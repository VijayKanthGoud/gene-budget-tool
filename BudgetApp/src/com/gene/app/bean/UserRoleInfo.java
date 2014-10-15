package com.gene.app.bean;

import java.io.Serializable;

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

	@Persistent
	private String brand;

	@Persistent
	private String cost_center;

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

	public String getBrand() {
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
	}

	public String getEmail() {
		return email;
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
