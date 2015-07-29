package com.gene.app.model;

import java.util.List;

public class ProjectParameters {

	private String gMemoriId;
	private String projectName;
	private List<String> costCentres;
	private String pStatus;
	private String projectOwner;
	private String unixId;
	
	public String getUnixId() {
		return unixId;
	}
	public void setUnixId(String unixId) {
		this.unixId = unixId;
	}
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
	public List<String> getCostCentres() {
		return costCentres;
	}
	public void setCostCentres(List<String> costCentre) {
		this.costCentres = costCentre;
	}
	
	public String getProjectOwner() {
		return projectOwner;
	}
	public void setProjectOwner(String projectOwner) {
		this.projectOwner = projectOwner;
	}
}
