package com.gene.app.util;

public interface BudgetConstants {

	//DBUtil.java
	public static final String[] months = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
		"JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "TOTAL" };
	public static final String total = "TOTAL";
	public static final String seperator = " - ";
	public static final String GTFReportFilterString = "key == keyParam";
	public static final String GTFReportParameters = "String keyParam";
	public static final String GTFReportOrderingParameters = "flag asc";
	public static final String GTFReportOrderingParameters_getReport = "flag asc, projectName asc, gMemoryId asc";
	
	// This variable is used in UI layer and service layer
	public static final String costCenter = "307673";
	
	//StoreReport.java
	public static final String contentType = "text/plain";
	public static final String objArray = "objarray";
	public static final String loggedInUser = "loggedInUser";
	public static final String status_New = "New";
	public static final String status_Active = "Active";
	public static final String GTFReport_ProjectWBS = "4";
	public static final String GTFReport_ProjectName = "1";
	public static final String GTFReport_Status = "2";
	public static final String GTFReport_Requestor = "3";
	public static final String GTFReport_WBS_Name = "5";
	public static final String GTFReport_SubActivity = "6";
	public static final String GTFReport_Brand = "7";
	public static final String GTFReport_Percent_Allocation = "8";
	public static final String GTFReport_PoNumber = "9";
	public static final String GTFReport_PoDesc = "10";
	public static final String GTFReport_Vendor = "11";
	public static final String GTFReport_Remarks = "25";
	public static final String REQUEST_ATTR_GTFReports = "gtfreports";
	public static final String REQUEST_ATTR_SUMMARY = "summary";
	public static final String GetReport_REDIRECTURL = "/listProjects";
	public static final String USER_ROLE_PRJ_OWNER = "prjOwner";
	public static final String KEY = "key";
	public static final String CELL_VALUE = "cellValue";
	public static final String CELL_NUM = "celNum";
	public static final int CELL_REMARKS = 13;
	public static final String isMultiBrand = "37";
	public static final String multiBrandInput = "36";
}
