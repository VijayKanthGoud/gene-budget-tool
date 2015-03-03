package com.gene.app.model;

import java.util.LinkedHashMap;

public class ProjectParameters {

	private String gMemoriId;
	private String projectName;
	private String costCentre;
	private String pStatus;
	private String pUnixId;
	
	public String getpStatus() {
		return pStatus;
	}
	public void setpStatus(String pStatus) {
		this.pStatus = pStatus;
	}
	public String getgMemoriId() {
		return gMemoriId;
	}
	public void setgMemoriId(String gMemoriId) {
		this.gMemoriId = gMemoriId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getCostCentre() {
		return costCentre;
	}
	public void setCostCentre(String costCentre) {
		this.costCentre = costCentre;
	}
	
	public String getpUnixId() {
		return pUnixId;
	}
	public void setpUnixId(String pUnixId) {
		this.pUnixId = pUnixId;
	}
}
