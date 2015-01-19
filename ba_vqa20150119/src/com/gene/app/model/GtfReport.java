package com.gene.app.model;

import java.io.Serializable;
import java.util.Map;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable="true")
public class GtfReport implements Serializable,Cloneable {

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
	private double percent_Allocation;

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
	
	@Persistent
	private Boolean multiBrand;
	
	@Persistent
	private Map<String,MultiBrand> mutliBrandMap;
	
	@Persistent
	private String createDate;
	
	@Persistent
	private String year;
	
	@Persistent
	private String costCenter;
	
	@Persistent
	private String qual_Quant;
	
	@Persistent
	private String study_Side;
	
	@Persistent
	private int units;
	
	@Persistent
	private boolean isDummyGMemoriId;
	
	/*public GtfReport(GtfReport gtfReport1) {
		// TODO Auto-generated constructor stub
		this.gtfReport = gtfReport1;
	}

	public GtfReport() {
		// TODO Auto-generated constructor stub
		super();
	}*/
	
	public boolean isDummyGMemoriId() {
		return isDummyGMemoriId;
	}

	public void setDummyGMemoriId(boolean isDummyGMemoriId) {
		this.isDummyGMemoriId = isDummyGMemoriId;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

	public Map<String, MultiBrand> getMutliBrandMap() {
		return mutliBrandMap;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setMutliBrandMap(Map<String, MultiBrand> mutliBrandMap) {
		this.mutliBrandMap = mutliBrandMap;
	}

	public Boolean getMultiBrand() {
		return multiBrand;
	}

	public void setMultiBrand(Boolean multiBrand) {
		this.multiBrand = multiBrand;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
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

	public double getPercent_Allocation() {
		return percent_Allocation;
	}

	public void setPercent_Allocation(double percent_Allocation) {
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
	
	public String getQual_Quant() {
		return qual_Quant;
	}

	public void setQual_Quant(String qual_Quant) {
		this.qual_Quant = qual_Quant;
	}

	public String getStudy_Side() {
		return study_Side;
	}

	public void setStudy_Side(String study_Side) {
		this.study_Side = study_Side;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		this.units = units;
	}
}
