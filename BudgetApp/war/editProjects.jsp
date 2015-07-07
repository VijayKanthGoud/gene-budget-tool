<%@page import="com.gene.app.model.*"%>
<%@page import="com.gene.app.dao.DBUtil"%>
<%@page import="com.gene.app.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="javax.servlet.RequestDispatcher"%>

<%@ include file="header.jsp"%>


<%
String prjView="";
String brandView="";
String ccView="";
	String color ="";
	ArrayList<Object> userlist;
	List<GtfReport> gtfReports = (List<GtfReport>) request
			.getAttribute("gtfreports");
	for (GtfReport report : gtfReports) {
		LOGGER.log(Level.INFO, "Reports received : " + report.getgMemoryId());
	}
	Calendar cal = Calendar.getInstance();
	int year = cal.get(Calendar.YEAR);
	int month = cal.get(Calendar.MONTH);
	int qtr = month / 3;
	session = request.getSession();
	String key = (String) session.getAttribute("key");
	if (key == null) {
		key = "";
	}
	
	Map<String, Date> cutofDates = util.getCutOffDates();
	Date cutOfDate = cutofDates.get(qtr+"");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>

<link rel="stylesheet" href="SlickGrid-master/slick.grid.css"
	type="text/css" />
<link rel="stylesheet"
	href="SlickGrid-master/css/smoothness/jquery-ui-1.8.16.custom.css"
	type="text/css" />
<link rel="stylesheet" href="SlickGrid-master/examples/examples.css"
	type="text/css" />
<html>
<body onload="getAvailableTags();">
	<div align="center">
		<table
			style="border: 1px solid gray; background: #EAF4FD; width: 100%; font-weight: normal; color: #2271B0; float: left;">
			<tr>
				<td style="width: 20%; padding-bottom: 2%;  padding-top: 2%" rowspan="2">
					<table class="summarytable"
						style="color: #2271B0; white-space: nowrap; height: 117px; width: 220px;">
						<tr>
							<td style="padding-left: 20px;"><input type="radio"
								name="selectedmode" value="planned">Forecast View <input
								type="radio" name="selectedmode" value="All" checked="checked">Detail View</td>
						</tr>
						<tr>
							<td style="padding-left: 20px;"><input type="checkbox"
								id="hideColumns" name="hideColumns" value="hide" checked>Hide
								PO Details</td>
						</tr>
					</table>
				</td>
				<%
					UserRoleInfo userInfo = (UserRoleInfo)session.getAttribute("userInfo");
				%>
				<td
					style="padding-left: 1.5%; width: 50%; text-align: center;">
					<table align="center">
						<tr>
							<td width="100px"><span
								style="color: #105596; font-size: 22px; font-weight: bold; letter-spacing: 5px; padding-top: 8px;">
									<%
										String viewSelected = (String)request.getAttribute("selectedView");
													System.out.println("viewSelected = "+viewSelected);
														if(viewSelected==null || "".equalsIgnoreCase(viewSelected.trim())){
															viewSelected = "My Projects";
														}if("My Projects".equalsIgnoreCase(viewSelected)){
															prjView = "selected";
														}else if("My Brands".equalsIgnoreCase(viewSelected)){
															brandView = "selected";
														}else{
															ccView = "selected";
														}
									%>
							</span> <span
								style="font-size: 14px; font-weight: bold; color: #105596;">Select
									View : </span></td>
							<td><select id="selectedUserView" name="selectedUserView"
								onchange="selectUserView()" autofocus
								style="width: 150px; color: #105596;">
									<option <%=prjView%>>My Projects
										</options>
									<option <%=brandView%>>My Brands
										</options>
									<option <%=ccView%>>My Cost Center
										</options>
							</select></td>
						</tr>
						<tr>
							<%
								if(!userInfo.getRole().contains("Admin")) {
							%><td><span
								style="font-size: 14px; font-weight: bold;  color: #105596;">Select
									Cost Center :</span></td>
							<td><select id="getCostCenter" name="ccValue"
								style="width: 102px; height: 23px; color: #105596;"
								onchange="getCostCenterDetails()">
									<%-- <option> <%=userInfo.getCostCenter() %> </option>  --%>
									<%
										String ccSelected = (String)request.getAttribute("getCCValue");
													String[] costcenter1= userInfo.getCostCenter().split(":");
													String costc;
												   	if(costcenter1!=null){
														for(int k=0;k<costcenter1.length;k++){
															costc = costcenter1[k];
															if(costc!=null && !"".equals(costc) && ccSelected!=null && !"".equals(ccSelected) && ccSelected.equalsIgnoreCase(costc)){
									%>
									<option value="<%=costc%>" selected><%=costc%></option>
									<%
										} else if((costc)!=null && !"".equals(costc)){
									%>
									<option value="<%=costc%>"><%=costc%></option>
									<%
										} } }
									%>
							</select></td>
							<%
								} else{
							%>
							<td><span
								style="font-size: 14px; font-weight: bold; color: #105596;">Select
									Cost Center : </span></td>
							<td><select id="getCostCenter" name="ccValue"
								style="width: 100px; height: 23px; color: #105596;"
								onchange="getCostCenterDetails()">
									<%
										List<CostCenter_Brand> cc_brandList = util.readCostCenterBrandMappingData();
													String ccSelected = (String)request.getAttribute("getCCValue");
													CostCenter_Brand cc_brand = new CostCenter_Brand();
														if(cc_brandList!=null && !cc_brandList.isEmpty()){
															for(int i=0;i<cc_brandList.size();i++){
																cc_brand = cc_brandList.get(i);
																if(cc_brand.getCostCenter()!=null 
																		&& !"".equals(cc_brand.getCostCenter()) 
																		&& ccSelected.equalsIgnoreCase(cc_brand.getCostCenter())){
									%>
									<option value="<%=cc_brand.getCostCenter()%>" selected><%=cc_brand.getCostCenter()%></option>
									<%
										} else if(cc_brand.getCostCenter()!=null 
															&& !"".equals(cc_brand.getCostCenter())){
									%>
									<option value="<%=cc_brand.getCostCenter()%>"><%=cc_brand.getCostCenter()%></option>
									<%
										} } }
									%>
							</select> <%
 	}String selectedView = (String)request.getAttribute("selectedView");
 				String selectedBrand = (String)request.getAttribute("brandValue");
 %></td>
						</tr>
						<tr id="dropdown">
							<td><span
								style="font-size: 14px; font-weight: bold; color: #105596;">
									Select Brand :&nbsp;&nbsp; </span></td>
							<td><select id="getBrand1" name="brandValue"
								onchange="getProjectsBrandwise()"
								style="width: 190px; color: #105596;">
									<%
										String selectedCostCenter = (String)request.getAttribute("getCCValue");
											if(selectedCostCenter==null || "".equals(selectedCostCenter)){
											selectedCostCenter = userInfo.getSelectedCostCenter();
											}
											Map<String,Double> userBrandMap= new LinkedHashMap<String,Double>();
											List<CostCenter_Brand> ccList = new ArrayList<CostCenter_Brand>();
											ccList = util.readCostCenterBrandMappingData();
											for(CostCenter_Brand cc: ccList){
												if(cc!=null && cc.getCostCenter()!=null && !"".equalsIgnoreCase(cc.getCostCenter()) && selectedCostCenter.equalsIgnoreCase(cc.getCostCenter())){
													userBrandMap = util.getBrandMap(cc.getBrandFromDB());
												}
											}
											//Map<String,Double> userBrandMap= userInfo.getCCBrandMap().get(selectedCostCenter); 
											
											Object[] myBrands = {}; 
											String brandValue1="";
											String brandValue=(String)request.getAttribute("brandValue");
											
											if(userBrandMap!=null && !userBrandMap.isEmpty()){
												myBrands = userBrandMap.keySet().toArray();
											    for(int i=0;i<myBrands.length;i++){ 
											    	if(brandValue==null || brandValue==""){
														brandValue =  myBrands[0].toString();
													} 
									                        brandValue1 = myBrands[i].toString();
									                        if(brandValue.equals(brandValue1)){
									%>
									<option value="<%=brandValue1%>" selected><%=brandValue1%></option>
									<%
										}else{
									%>
									<option value="<%=brandValue1%>"><%=brandValue1%></option>
									<%
										}}
											}
									%>
							</select></td>
						</tr>
						<tr> 
						<td style="padding-left: 21.5%; padding-top: 20px;" colspan='2'>
								<input type=text style="float: left; width: 150px;"
								id="txtSearch"> <img src="images/search.png" height="20"
								width="20" align="bottom" style="float: left;"
								title="Search in Project Name, gMemori Id, Brand and Comments.">
							</td>
						</tr>


			</table>
			<div id="selectthebrand">
				
				<div id="header"
			    style="width: 100%; height: 26px; background-color: #005691; color: white; border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; font-size: 20px; letter-spacing: 2px; padding-top: 4px;"  align = center>Export CostCenter  
		     </div><br>
		     <div align="center">
		     <span id="brandVal"  style="font-size:15;">
		     Brand : <span id="selectedBrandValue"> </span>
		     &nbsp;&nbsp;
		     </br>
		     </span>
		     </br>
				<input type="radio" value="0" id="selectCC" name="selectCC" > <span id = "selectedCCValue" style="font-size:15;">Current View(<%=(String)request.getAttribute("getCCValue")%>)</span></input>&nbsp;&nbsp;
				&nbsp;&nbsp;&nbsp;
				<input type="radio" value="1" id="selectCC" name="selectCC" > <span style="font-size:15;">Total MA&S Cost Centers</span></input><br><br>
				</div>
				<button class="myButton" value="" onclick="exportExcelData();" style="height: 25px; letter-spacing:1px;" align= 'right'> Ok</button>&nbsp;
				<button class="myButton" value="" onclick="closepopup();" style="height: 25px; letter-spacing:1px;" align= 'right'> Cancel</button>
				
				</div>
	<div id="getCostCentreProjects">
		
			<form method="GET" id="getCostCentre" action="/getreport">
			<input type="hidden" name="selectedView" id="selectedView1"/>
			<input type="hidden" name="getCCValue" id="getCostCenter1"/>
			<input type="hidden" name="brandValue" id="getBrandCC"/>	
			</form>
			
		</div>
	<div id="getMyProjects">
		
			<form method="GET" id="getProjects" action="/getreport">
			<input type="hidden" name="selectedView" id="selectedView2"/>
			<input type="hidden" name="getCCValue" id="getCostCenter2"/>
			<input type="hidden" name="brandValue" id="getBrand2"/>		
			</form>
			
		</div>	
		
	
		<div align='center' style='padding-right: 50px;'>
			<form method="GET" id="getBrand" action="/getreport" >
			<input type="hidden" name="selectedView" id="selectedView3"/>
			<input type="hidden" name="getCCValue" id="getCostCenter3"/>
			<input type="hidden" name="brandValue" id="getBrand3"/>
				<br/>
			
			</form>
			
		</div>
	 	<td style="width: 1%;" rowspan="2">
					<table class="summarytable"
						style="color: #2271B0; white-space: nowrap; font-weight: bold;">
						<%
							BudgetSummary summary = (BudgetSummary) session.getAttribute("summary");
											Map<String, BudgetSummary> budgetMap = Util.sortCaseInsensitive(summary.getBudgetMap());
											BudgetSummary budgetSummary = new BudgetSummary();
											UserRoleInfo user = (UserRoleInfo) request.getAttribute("user");
											String cc = user.getSelectedCostCenter();
											Map<String,Double> brandMap= new LinkedHashMap<String,Double>();
											/* ccList = new ArrayList<CostCenter_Brand>();
											ccList = util.readCostCenterBrandMappingData();
											 */for(CostCenter_Brand cc1: ccList){
												if(cc1!=null && cc1.getCostCenter()!=null && !"".equalsIgnoreCase(cc1.getCostCenter()) && selectedCostCenter.equalsIgnoreCase(cc1.getCostCenter())){
													brandMap = util.getBrandMap(cc1.getBrandFromDB());
												}
											}
											//Map<String,Double> brandMap = user.getCCBrandMap().get(cc);
											LOGGER.log(Level.INFO, "brandMaps received : " + brandMap);
											Object[] brands = {}; 
											if(brandMap!=null && !brandMap.isEmpty()){
												brands = brandMap.keySet().toArray();
											}
											//System.out.println(":::::::"+brands[0]);
						%>
						<script>
					<%@ include file="scripts/editProjects.js"%>
						 
					</script>

						<tr align='center'>
							<td colspan=2>Annual Brand Summary &nbsp;($ in 1000's) <img alt="" src="images/refresh.png" height="25"
								width="25" align='left' onclick="getBrandTotals()"></td>
						</tr>
						<tr>
						<tr>
							<td>Select Brand:</td>
							<td><select id="brandType"
								onchange="getBrandTotals()"
								style="color: #2271B0;">
									<%
										String option = "";
									                            if(brandValue==null || brandValue==""){
									            					brandValue = "Avastin";
									            				} 
									                            budgetSummary = budgetMap.get(brandValue);
									                            if(budgetMap!=null && !budgetMap.isEmpty()){
									                            	Object[] budgets = budgetMap.keySet().toArray();
									                            for(int i=0;i<budgets.length;i++){ 
									                            option = budgets[i].toString();
									                            if(brandValue.equals(option)){
									%>
									<option value="<%=option%>" selected><%=option%></option>
									<%
										}else{
									%>
									<option value="<%=option%>"><%=option%></option>
									<%
										}}}
									%>
							</select></td>
						</tr>
						<tr>
							<td><span  title="Current Overall Budget">Budget:</span></td>
							<td style="text-align: right;"><span id="totalBudget"  > <%=Util.roundDoubleValue(budgetSummary.getTotalBudget(),4)%></span></td>
						</tr>

						<tr>
							<td><span  title="Total Overall Forecast">Total Forecast:</span></td>
							<td style="text-align: right;"><span id="plannedTotal"  ><%=Util.roundDoubleValue(budgetSummary.getPlannedTotal(),4)%></span></td>
						</tr>
						<tr>
							<td><span title="= Budget - Total Forecast">Unallocated Forecast:</span></td>
							<td style="text-align: right;"><span id="budgetLeftToSpend"><%=Util.roundDoubleValue((budgetSummary.getTotalBudget() - budgetSummary.getPlannedTotal()),4) %></span></td>
						</tr>
						<tr>
							<!-- td style="padding-left: 20px;">2017</td> -->
							<td><span title = "Total Dollars Spent" >Total Accrual:</td>
							<td style="text-align: right;"><span id="accrualTotal"><%=Util.roundDoubleValue(budgetSummary.getAccrualTotal(),4)%></span></td>
						</tr>
						<tr>
							<td><span id = "varTotalLabel" title = "= Budget - Total Accrual" >Budget LTS:</span></td>
							<td style="text-align: right;"> <span id="varTotalText" ><span
									id="varianceTotal"><%=Util.roundDoubleValue(budgetSummary.getBudgetLeftToSpend(), 4)%></span></span>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	<div id="statusMessage"></div>
	<div id="displayGrid"
		style="width: 100%; height: 44.5%; min-height: 200px;"></div>
	<div id="multibrandEdit">
		<div id="header"
			style="width: 100%; height: 26px; background-color: #2271B0; color: white; border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; font-size: 20px; letter-spacing: 5px; padding-top: 8px;"
			align=center>Multi-brand</div>
		<div id="multibrandGrid" style="width: 100%; height: 200px;"></div>
		<div align='center'>
		<button id="addRow" class="myButton1" value="" onclick="addNewRow();" onmouseover="showMessage();" onmouseout="hideMessage();"
				style="height: 20px;  letter-spacing: 1px;">
				+</button>
			<button id="deleteSel" class="myButton" value="" onclick="deleteSelectedProjects();"
				style="height: 20px;  letter-spacing: 1px;">
				Delete selected</button>
			<button id="saveClose" class="myButton" value=""
				onclick="saveAndClose();"
				style="height: 20px; letter-spacing: 1px;">Save
				and close</button>
			<button class="myButton" value="" onclick="closeWithoutSave();"
				style="height: 20px; letter-spacing: 1px;">
				Cancel</button>
		</div>
	</div>
	<div id="back"></div>

	<script src="SlickGrid-master/lib/firebugx.js"></script>
	<script src="SlickGrid-master/lib/jquery-1.7.min.js"></script>
	<script src="SlickGrid-master/lib/jquery-ui-1.8.16.custom.min.js"></script>
	<script src="SlickGrid-master/lib/jquery.event.drag-2.2.js"></script>
	<script src="SlickGrid-master/plugins/slick.autotooltips.js"></script>
	<script src="SlickGrid-master/plugins/slick.cellrangedecorator.js"></script>
	<script src="SlickGrid-master/plugins/slick.cellrangeselector.js"></script>
	<script src="SlickGrid-master/plugins/slick.cellexternalcopymanager.js"></script>
	<script src="SlickGrid-master/plugins/slick.cellselectionmodel.js"></script>
	<script src="SlickGrid-master/slick.editors.js"></script>
	<script src="SlickGrid-master/slick.formatters.js"></script>
	<script src="SlickGrid-master/slick.grid.frozen.js"></script>
	<script src="SlickGrid-master/slick.dataview.js"></script>
	<script src="SlickGrid-master/slick.core.js"></script>
	<script src="SlickGrid-master/plugins/slick.autotooltips.js"></script>
	<script src="SlickGrid-master/slick.groupitemmetadataprovider.js"></script>
	<script src="scripts/fileHandle.js"></script>
	<script>
	var map = {};
	var idBrandMap = {}
    // rdoSelectedmode holds the radio(Planned/All) button object
	var rdoSelectedmode = $('input[name="selectedmode"]');
	
    // chkBoxHideColumns holds the checkbox(Hide Columns) object
	var chkBoxHideColumns = $('input[name="hideColumns"]');
	
    //External wrapper for data grid with advance functionalities 
	var dataView;
    
    // It is the actual displayed table on the UI
	var grid;
    
	var addsave=0;
	
	// data is the original grid data array containing objects representing each line in the edit project grid
	var data = [];
	
	// m_data is the popup grid data array 
	var m_data = [];
	
	// itemclicked global variable is take to use the clicked row (in the grid) data in other methods
	var itemClicked;
	
	var popUpWindow;
	
	// initializing the multi-brand popup data with five blank rows for intial display 
	 for (var i = 0; i < 5; i++) {
		var d = (m_data[i] = {});
		d[0] = "";
		d[1] = "";
		d[2] = "";
		d[3] = "";
		d[4] = "";
		d[5] = "";
		d[6] = "";
		d[7] = "";
 	} 
	var multiBrandToSingle = false;
	var radioString = "All";
	var totalSize = 0;
	var numHideColumns = <%=BudgetConstants.NUMBER_OF_HDN_COLS%>;
	var columnNames = [ "Status", "Project Name", "Brand", "$ in 1000's", "gMemori Id", "Project Owner",
	        			"Project WBS", "SubActivity", "Allocation %", "PO Number", "Vendor", "Units",
	        			"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
	        			"DEC", "Total", "Comments"];
	var noOfNew = 0;
	var noOfActive = 0;
	var noOfClosed = 0;
	var newExist=false;
	var activeExist=false;
	var closedExist=false;
	var frmStudy=false;
	var columnValiation=false;
	var lastKeyPressed;
	
	function specialCharValidator(value) {
		  if ((!/[^a-zA-Z0-9 .-_]/.test(value))) {
			  return {valid: true, msg: null};
		  }
		  else {
			  return {valid: false, msg: "Please enter valid characters."};
		  }
	}
	
	function projectWBSValidator(value) {
		  if ((!/[^a-zA-Z0-9.]/.test(value))) {
			  return {valid: true, msg: null};
		  }
		  else {
			  return {valid: false, msg: "Please enter valid characters."};
		  }
	}
	
	// Columns displayed when hide columns is unchecked
	var columns = [ 
		{ id : 1, name : columnNames[0], field : <%=BudgetConstants.STATUS_FIELD%>, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[1], field : <%=BudgetConstants.PROJECT_NAME_FIELD%>, width : 150, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField},
		{ id : 3, name : columnNames[2], field :  <%=BudgetConstants.BRAND_FIELD%>, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Auto},
		{ id : 4, name : columnNames[3], field : <%=BudgetConstants.$_IN_1000_FIELD%>, width : 110, formatter : Slick.Formatters.cancelButton, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 5, name : columnNames[4], field : <%=BudgetConstants.GMEMORI_ID_FIELD%>, width : 90, formatter : Slick.Formatters.gMemoriHyperLink },
		{ id : 6, name : columnNames[5], field : <%=BudgetConstants.PROJECT_OWNER_FIELD%>, width : 90},
		{ id : 7, name : columnNames[6], field : <%=BudgetConstants.PROJECT_WBS_FIELD%>, width : 90, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField, validator: projectWBSValidator},
		{ id : 8, name : columnNames[7], field : <%=BudgetConstants.SUBACTIVITY_FIELD%>, width : 90, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField, validator: specialCharValidator},
		{ id : 9, name : columnNames[8], field : <%=BudgetConstants.ALLOCATION_PERCENTAGE_FIELD%>, width : 90, editor : Slick.Editors.Text},
		{ id : 10, name : columnNames[9], field : <%=BudgetConstants.PO_NUMBER_FIELD%>, width : 90, editor : Slick.Editors.PONumberText, formatter : Slick.Formatters.poField},
		{ id : 11, name : columnNames[10], field : <%=BudgetConstants.VENDOR_FIELD%>, width : 90, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField, validator: specialCharValidator},
		{ id : 12, name : columnNames[11], field : <%=BudgetConstants.UNIT_FIELD%>, width : 90, editor : Slick.Editors.Integer, formatter : Slick.Formatters.editableField},
		{ id : 13, name : columnNames[12], field : <%=BudgetConstants.JAN_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[13], field : <%=BudgetConstants.FEB_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[14], field : <%=BudgetConstants.MAR_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[15], field : <%=BudgetConstants.APR_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[16], field : <%=BudgetConstants.MAY_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[17], field : <%=BudgetConstants.JUN_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[18], field : <%=BudgetConstants.JUL_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[19], field : <%=BudgetConstants.AUG_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[20], field : <%=BudgetConstants.SEP_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[21], field : <%=BudgetConstants.OCT_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[22], field : <%=BudgetConstants.NOV_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[23], field : <%=BudgetConstants.DEC_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[24], field : <%=BudgetConstants.TOTAL_FIELD%>, width : 90, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 26, name : columnNames[25], field : <%=BudgetConstants.REMARK_FIELD%>, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	}];

	//Columns displayed when hide columns is checked
	var hidecolumns = [ 
		{ id : 1, name : columnNames[0], field : <%=BudgetConstants.STATUS_FIELD%>, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[1], field :  <%=BudgetConstants.PROJECT_NAME_FIELD%>, width : 150, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField},
		{ id : 3, name : columnNames[2], field : <%=BudgetConstants.BRAND_FIELD%>, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Auto},
		{ id : 4, name : columnNames[3], field : <%=BudgetConstants.$_IN_1000_FIELD%>, width : 110, formatter : Slick.Formatters.cancelButton, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 5, name : columnNames[4], field : <%=BudgetConstants.GMEMORI_ID_FIELD%>, width : 90, formatter : Slick.Formatters.gMemoriHyperLink },
		{ id : 6, name : columnNames[5], field : <%=BudgetConstants.PROJECT_OWNER_FIELD%>, width : 90},
		{ id : 13, name : columnNames[12], field : <%=BudgetConstants.JAN_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[13], field : <%=BudgetConstants.FEB_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[14], field : <%=BudgetConstants.MAR_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[15], field : <%=BudgetConstants.APR_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[16], field : <%=BudgetConstants.MAY_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[17], field : <%=BudgetConstants.JUN_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[18], field : <%=BudgetConstants.JUL_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[19], field : <%=BudgetConstants.AUG_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[20], field : <%=BudgetConstants.SEP_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[21], field : <%=BudgetConstants.OCT_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[22], field : <%=BudgetConstants.NOV_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[23], field : <%=BudgetConstants.DEC_FIELD%>, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[24], field : <%=BudgetConstants.TOTAL_FIELD%>, width : 90, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 26, name : columnNames[25], field : <%=BudgetConstants.REMARK_FIELD%>, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	}]
	var searchString = "";
	
	// Grouping columns acording to status(New, Active, Closed)
	

	

	var options = {
		editable : true,
		enableAddRow : true,
		enableCellNavigation : true,
		asyncEditorLoading : false,
		autoEdit : true,
		frozenColumn : 3,
		enableColumnReorder: false
	};


	// Display total for active new and closed projects (roll up total)
	



    // Method called to store changed value in to memcache
	
	
	$(function() {
		
		
		if($(window).width() < 900){
			$('#cautionWindow').show().fadeIn(100);
			$('#back').addClass('black_overlay').fadeIn(100);
		}else{
			$('#cautionWindow').hide();
			$('#back').removeClass('black_overlay').fadeIn(100);
		}
		
		
		var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
			groupItemMetadataProvider : groupItemMetadataProvider,
			inlineFilters : true
		});

		var indent = 0;
		var parents = [];
		if(frmStudy==false){
		<%if(gtfReports == null || gtfReports.isEmpty()) {%>
		createNewProjects();
		newExist=true;
		<%}else{
		for(int counter = 0; counter< gtfReports.size(); counter++ ){%>
			if("<%=gtfReports.get(counter).getStatus()%>" == "New"){
				newExist=true;
			}else if("<%=gtfReports.get(counter).getStatus()%>" == "Active"){
				activeExist=true;
			}else if("<%=gtfReports.get(counter).getStatus()%>" == "Closed"){
				closedExist=true;
			}
			
		<%}
		}%>
		
			if(newExist ==false){
				dummyNewProjects();		
			}
		}
		var jsId = -1;
		var dLength= data.length;
		// prepare the data
		<%String requestor = "";
		String role="";
		for (int i = 0; i < gtfReports.size(); i++) {
			boolean isFirst = true;%>
			idBrandMap['<%=gtfReports.get(i).getgMemoryId()%>'] = '<%=gtfReports.get(i).getBrand()%>';
			<%
			for (int count = 0; count < 4; count++) {%>
			
			<%GtfReport gReport = gtfReports.get(i);%>
			var pStat = "<%=gReport.getStatus()%>";
			if(pStat == "Closed" && activeExist==false){
				dLength += 1;
	       	 	dummyActiveProjects();
		 	}
				var d = (data[++jsId + dLength] = {});
			 	var parent;
    	   		d["id"] = "id_" + (parseInt(jsId) + parseInt(dLength));
    	    	d["indent"] = indent;
    	    	d["parent"] = parent;
    	    	d[0]="";
    	 		d[25]="";
       	 		d[26]="<%=gReport.getStatus()%>";
       	 		var gmemoriID = "<%=gReport.getgMemoryId()%>";
        		d[27]=gmemoriID;
        		d[28]="<%=gReport.getBrand()%>";
        		d[29]="<%=gReport.getProjectName()%>";
        		d[30]="";	
        		d[31]="<%=gReport.getId()%>";
        		d[32]="<%=gReport.getRemarks()%>";
        		d[33]="New";
        		d[34]=gmemoriID;
        		d[35]="";
        		d[37]=<%=gReport.getMultiBrand()%>;
        		d[38]="<%=gReport.getCreateDate()%>";
        		d[39]="<%=gReport.getYear()%>";
				if(gmemoriID.indexOf(".") > -1){
					d[34]=gmemoriID.split(".")[0];
				}
				d[42]="<%=gReport.getProjectName()%>" + " :: " + d[34];
				d[44]="<%=gReport.getBrand()%>";
				d[47]="<%=gReport.getCostCenter()%>";
				<%requestor = gReport.getRequestor();
				if(requestor.contains(":")){
					requestor = requestor.substring(0,requestor.indexOf(":"));
				}%>
				d[48]="<%=requestor%>";
				<% role = user.getRole();%>
				d[50]="<%=role%>";
				d[53]= [];
				<%if(gReport.getChildProjectList() != null && gReport.getChildProjectList().size() != 0){
				%>	
				d[53] = <%=gReport.getChildProjectList()%>;
				<%}
				%>
				<%if(gReport.getRequestor().contains(":")){ %>
					d[54] = "<%=gReport.getRequestor().split(":")[0]%>";
				<%}else{
				%>
					d[54] = "<%=gReport.getRequestor()%>";
				<%}
				%>
        		<%if(isFirst){
    				isFirst = false;
    				requestor = gReport.getRequestor();
    				if(requestor.contains(":")){
    					requestor = requestor.substring(0,requestor.indexOf(":"));
    				}%>    
   			 		d[0]=gmemoriID;
   			 		
    				d[1]="<%=requestor%>";
    				d[2]="<%=gReport.getProjectName()%>";
    				d[3]="<%=gReport.getProject_WBS()%>";
    				d[4]="<%=gReport.getWBS_Name()%>";
    				d[5]="<%=gReport.getSubActivity()%>";
    				d[6]="<%=gReport.getBrand()%>";
    				d[7]="<%=gReport.getPercent_Allocation()%>";
    				d[8]="<%=gReport.getPoNumber()%>";
    				d[9]="<%=gReport.getPoDesc()%>";
    				d[10]="<%=gReport.getVendor()%>";
    				d[49]="<%=gReport.getUnits()%>";
    				d[11]="<%=BudgetConstants.FORECAST%>";
    				d[12]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("JAN"))%>";
    				d[13]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("FEB"))%>";
    				d[14]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("MAR"))%>";
    				d[15]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("APR"))%>";
    				d[16]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("MAY"))%>";
    				d[17]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("JUN"))%>";
    				d[18]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("JUL"))%>";
    				d[19]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("AUG"))%>";
    				d[20]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("SEP"))%>";
    				d[21]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("OCT"))%>";
    				d[22]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("NOV"))%>";
    				d[23]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("DEC"))%>";
    				d[41]="<%=gReport.getPercent_Allocation()%>";
    				d[51]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("TOTAL"))%>";
    				<%-- if(<%=gReport.getMultiBrand()%> == true){
    					d[24]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("TOTAL"))%>";
    				}else{ --%>
    					d[24]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("JAN") + 
						gtfReports.get(i).getPlannedMap().get("FEB") + 
						gtfReports.get(i).getPlannedMap().get("MAR") + 
						gtfReports.get(i).getPlannedMap().get("APR") + 
						gtfReports.get(i).getPlannedMap().get("MAY") + 
						gtfReports.get(i).getPlannedMap().get("JUN") + 
						gtfReports.get(i).getPlannedMap().get("JUL") + 
						gtfReports.get(i).getPlannedMap().get("AUG") + 
						gtfReports.get(i).getPlannedMap().get("SEP") + 
						gtfReports.get(i).getPlannedMap().get("OCT") + 
						gtfReports.get(i).getPlannedMap().get("NOV") + 
						gtfReports.get(i).getPlannedMap().get("DEC"))%>";
    				/* } */
    				d[25]="<%=gtfReports.get(i).getRemarks()%>";
  				<%} else{%>  
  				for(var cnt=1;cnt<11;cnt++){
  						d[cnt]=""; 
  					}
   				if(jsId % 4 == 1){
   				d[41]="<%=gtfReports.get(i).getPercent_Allocation()%>";
   				if((d[26] == "New" && (gmemoriID.indexOf(".") == -1)) || (d[26] !="New"  && ( (gmemoriID.indexOf(".") == -1) || (gmemoriID.indexOf(".") != -1  && '<%=viewSelected%>' == "My Brands") ))){ 
   				d[11]="<%=BudgetConstants.QUARTERLY_TARGET%>";
				d[12]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("JAN"))%>";
				d[13]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("FEB"))%>";
				d[14]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("MAR"))%>";
				d[15]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("APR"))%>";
				d[16]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("MAY"))%>";
				d[17]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("JUN"))%>";
				d[18]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("JUL"))%>";
				d[19]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("AUG"))%>";
				d[20]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("SEP"))%>";
				d[21]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("OCT"))%>";
				d[22]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("NOV"))%>";
				d[23]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("DEC"))%>";
				d[24]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getBenchmarkMap().get("JAN") + 
						gtfReports.get(i).getBenchmarkMap().get("FEB") + 
    					gtfReports.get(i).getBenchmarkMap().get("MAR") + 
    					gtfReports.get(i).getBenchmarkMap().get("APR") + 
    					gtfReports.get(i).getBenchmarkMap().get("MAY") + 
    					gtfReports.get(i).getBenchmarkMap().get("JUN") + 
    					gtfReports.get(i).getBenchmarkMap().get("JUL") + 
    					gtfReports.get(i).getBenchmarkMap().get("AUG") + 
    					gtfReports.get(i).getBenchmarkMap().get("SEP") + 
    					gtfReports.get(i).getBenchmarkMap().get("OCT") + 
    					gtfReports.get(i).getBenchmarkMap().get("NOV") + 
   				 		gtfReports.get(i).getBenchmarkMap().get("DEC"))%>";
   				 }else{
   	   				d[11]="";
   					for (var j = 12; j < 25; j++) {
						d[j] = 0.0;
						}
					d[33]="";
   				} 
   				} if(jsId % 4 == 2){
				d[11]="<%=BudgetConstants.ACCRUAL%>";
				d[41]="<%=gtfReports.get(i).getPercent_Allocation()%>";
				if(d[26]!="New"  && ( (gmemoriID.indexOf(".") == -1) || (gmemoriID.indexOf(".") != -1  && '<%=viewSelected%>' == "My Brands") )){
					d[12]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("JAN"))%>";
					d[13]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("FEB"))%>";
					d[14]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("MAR"))%>";
					d[15]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("APR"))%>";
					d[16]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("MAY"))%>";
					d[17]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("JUN"))%>";
					d[18]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("JUL"))%>";
					d[19]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("AUG"))%>";
					d[20]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("SEP"))%>";
					d[21]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("OCT"))%>";
					d[22]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("NOV"))%>";
					d[23]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("DEC"))%>";
					d[24]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getAccrualsMap().get("JAN") + 
							gtfReports.get(i).getAccrualsMap().get("FEB") + 
    				gtfReports.get(i).getAccrualsMap().get("MAR") + 
    				gtfReports.get(i).getAccrualsMap().get("APR") + 
    				gtfReports.get(i).getAccrualsMap().get("MAY") + 
    				gtfReports.get(i).getAccrualsMap().get("JUN") + 
    				gtfReports.get(i).getAccrualsMap().get("JUL") + 
    				gtfReports.get(i).getAccrualsMap().get("AUG") + 
    				gtfReports.get(i).getAccrualsMap().get("SEP") + 
    				gtfReports.get(i).getAccrualsMap().get("OCT") + 
    				gtfReports.get(i).getAccrualsMap().get("NOV") + 
    				gtfReports.get(i).getAccrualsMap().get("DEC"))%>";
    				} else{
    					for (var j = 12; j < 25; j++) {
    						d[j] = 0.0;
    						}
    					d[33]="";
    					}
   
   				} if(jsId % 4 == 3){
				d[11]="<%=BudgetConstants.QUARTERLY_LTS%>";
				d[41]="<%=gtfReports.get(i).getPercent_Allocation()%>";
				if(d[26]!="New"  && ( (gmemoriID.indexOf(".") == -1) || (gmemoriID.indexOf(".") != -1  && '<%=viewSelected%>' == "My Brands") )){
					d[12]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("JAN"))%>";
					d[13]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("FEB"))%>";
					d[14]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("MAR"))%>";
					d[15]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("APR"))%>";
					d[16]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("MAY"))%>";
					d[17]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("JUN"))%>";
					d[18]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("JUL"))%>";
					d[19]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("AUG"))%>";
					d[20]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("SEP"))%>";
					d[21]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("OCT"))%>";
					d[22]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("NOV"))%>";
					d[23]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("DEC"))%>";
					d[24]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getVariancesMap().get("JAN") + 
						gtfReports.get(i).getVariancesMap().get("FEB") + 
    					gtfReports.get(i).getVariancesMap().get("MAR") + 
    					gtfReports.get(i).getVariancesMap().get("APR") + 
    					gtfReports.get(i).getVariancesMap().get("MAY") + 
    					gtfReports.get(i).getVariancesMap().get("JUN") + 
    					gtfReports.get(i).getVariancesMap().get("JUL") + 
    					gtfReports.get(i).getVariancesMap().get("AUG") + 
    					gtfReports.get(i).getVariancesMap().get("SEP") + 
    					gtfReports.get(i).getVariancesMap().get("OCT") + 
    					gtfReports.get(i).getVariancesMap().get("NOV") + 
    					gtfReports.get(i).getVariancesMap().get("DEC"))%>";
    					}else{
    						for (var j = 12; j < 25; j++) {
    							d[j] = 0.0;
    							}
    						d[33]="";
    						}
   				}
    
    				<%}%>
    				d[40] = d[11];
    				
    				<%}
				
		}%>
			
			
		if(activeExist ==false){
			dummyActiveProjects();
		} 
		if(closedExist ==false){
			dummyClosedProjects();
		} 
			totalSize=data.length;
			
			var quarterlyTargetMap = {};
			var accrualMap = {};
			for (var cntTotal = 0; cntTotal < 4; cntTotal++) {
				var rowNum = cntTotal + totalSize;
				var d = (data[rowNum] = {});
				d["id"] = "id_" + rowNum;
				d["indent"] = indent;
				d["parent"] = parent;
				for (var j = 0; j < 11; j++) {
					d[j] = "";
				}

				for (var j = 12; j < 25; j++) {
					d[j] = 0.0;
					}
				var trowNum = rowNum % 4;
				
				switch(cntTotal) {
			    case 0:
			    	d[11] = "<%=BudgetConstants.FORECAST%>";
			        break;
			    case 1:
			    	d[11] = "<%=BudgetConstants.ANNUAL_TARGET%>";
			        break;
			    case 2:
			    	d[11] = "<%=BudgetConstants.ACCRUAL%>";
			        break;
			    case 3:
			    	d[11] = "<%=BudgetConstants.FORECAST_LTS%>";
			        break;
			    default:
			    	d[11] = "<%=BudgetConstants.FORECAST%>";
		        	break;
			}
				d[40] = d[11];
				 
			var compareString = "";
			for (var j = 0; j < totalSize ; j++) {
				if(data[j][11] == "<%=BudgetConstants.QUARTERLY_TARGET%>"){
					compareString =  "<%=BudgetConstants.ANNUAL_TARGET%>"
				}else if(data[j][11] == "<%=BudgetConstants.QUARTERLY_LTS%>"){
					compareString =  "<%=BudgetConstants.FORECAST_LTS%>"
				}else{
					compareString = data[j][11];
				}
				
				if( (data[j][37] == false && d[11]==compareString && data[j][0]!= 'undefined' && data[j][27] != "" && (data[j][27].indexOf(".") == -1)) ||
						 (data[j][37] == true && d[11]==compareString && data[j][0]!= 'undefined' && data[j][27] != "" && (data[j][27].indexOf(".") != -1) && ('<%=viewSelected%>' == 'My Brands')) ||
						 (data[j][37] == true && d[11]==compareString && data[j][0]!= 'undefined' && data[j][27] != "" && (data[j][27].indexOf(".") == -1) && ('<%=viewSelected%>' != 'My Brands'))		 
				){
					if(d[11] != "<%=BudgetConstants.QUARTERLY_LTS%>"){
						for(var i = 0; i <= 12; i++){
							d[12 + i] = parseFloat(d[12 + i]) + parseFloat(data[j][12 + i]);
						}
					}
					if(d[11] == "<%=BudgetConstants.QUARTERLY_TARGET%>"){
						for(var i = 0; i <= 12; i++){
							quarterlyTargetMap[12 + i] = d[12 + i];
						}
					} else if(d[11] == "<%=BudgetConstants.ACCRUAL%>"){
						for(var i = 0; i <= 12; i++){
							accrualMap[12 + i] = d[12 + i];
						}
					} else if(d[11] == "<%=BudgetConstants.QUARTERLY_LTS%>"){
						for(var i = 0; i <= 12; i++){
							if(!isNaN(quarterlyTargetMap[12 + i] - accrualMap[12 + i])){
								d[12 + i] =  quarterlyTargetMap[12 + i] - accrualMap[12 + i];
							}else{
								d[12 + i] =  0.0;
							}
						}
					}
				}
			}
			for (var j = 12; j < 25; j++) {
				d[j] = d[j].toFixed(4);
			}
			d[25] = "";
			d[26] = "Total";
			d[27] = "";
			d[28] = "";
			d[29] = "";
			d[30] = "";
			d[31] = "";
			d[32] = "";
			d[33] = "New";
			d[34] = "";
			d[35] = "";
			d[36] = "";
			d[37] = "";
			d[38] = "";
			d[39] = "";
			d[0] ="";

		}

		// initialize the model
		dataView = new Slick.Data.DataView({
			inlineFilters : true
		});
		
		dataView.beginUpdate();
		dataView.setItems(data);
		
		dataView.setFilter(searchProject);
		dataView.endUpdate();
		groupByStatus();
		
		var isSearch = false;
		if(document.URL.toString().indexOf("gMemoriId=") != -1){
			if("<%=request.getParameter("gMemoriId") != null %>" == "true"){
				searchString = "<%=request.getParameter("gMemoriId")%>" ; 
				isSearch = true;
			}
			
			$("#txtSearch").val(searchString);
			window.history.pushState(null, "", "<%=BudgetConstants.APP_URL%>");
			map = {};
			dataView.refresh();
			if(!isMatchPresent){
				alert("No project with gMemori Id " +searchString+ " Found!!!");
				searchString = "";
				$("#txtSearch").val(searchString);
			}else{
				dataView.expandGroup("Active");
				dataView.expandGroup("Closed");
				dataView.expandGroup("New");
			}
			dataView.refresh();
		}
		
		<%if(request.getAttribute("accessreq").toString().equalsIgnoreCase("external") && gtfReports.isEmpty()){%>
		$('#displayGrid').css("align","center");
		$('#displayGrid').html('<div style = "font-size:16px; line-height: 50px; margin-left: auto; margin-right: auto; width: 10%; ">No Project found.</div>');
	<%}else{%>
	    grid = new Slick.Grid("#displayGrid", dataView, hidecolumns, options);	
	<%}%>
	
		if(isSearch && isMatchPresent){
			calculateTotal();
		}
		
		// initialize the grid
		//grid = new Slick.Grid("#displayGrid", dataView, hidecolumns, options);
		//register the group item metadata provider to add expand/collapse group handlers
		grid.registerPlugin(groupItemMetadataProvider);
		grid.setSelectionModel(new Slick.CellSelectionModel());

		grid.onCellChange
				.subscribe(function(e, args) {
					var isValidBrand =false;
					var item = args.item;
					var tempKey = item[27];
					var cell = args.cell;
					var row = args.row;
					var dataLength = 0;
					itemClicked = item; 
					var fixedCell = cell;
					if ($('#hideColumns').is(":checked")) {
						fixedCell = cell + numHideColumns;
					} else {
						fixedCell = cell;
					}
					var itemCell = fixedCell;
					// Code for brand column(dropdown and validation)
					if( args.cell == <%=BudgetConstants.BRAND_CELL%> ){
						for(var i=0;i< availableTags.length;i++){
							if(availableTags[i].toString().trim().toLowerCase()===args.item[6].toString().trim().toLowerCase()){
								args.item[6]=availableTags[i].toString();
								isValidBrand = true;
								grid.invalidate();
								break;
							}
						}
						if(isValidBrand == false){
							var enteredBrand = args.item[6];
							args.item[6]=args.item[46][6];
							columnValiation=true;
							grid.invalidate();
							alert("'" + enteredBrand + "' is not a valid brand. Enter a valid brand.");
							return;
						}
					}
					if(args.cell==<%=BudgetConstants.GMEMORI_ID_CELL%>){
						for(var j = 0; j < data.length ; j++){
							if (data[j]["id"] != args.item.id && args.item[0]==data[j][0] && args.item[0]!='') {
								args.item[0]=args.item[46][0];
								columnValiation=true;
								alert("Duplicate gMemoriId !!!");
								grid.invalidate();
								return;
							}
						}
					}
					
				 	
					
						var temp = 0;
						for (var j = 0; j < data.length - 1; j++) {
							if (data[j]["id"] == args.item.id) {
								temp = j;
								break;
							}
						}
						
						if(item[37]!='undefined' && item[37]==true && fixedCell >=  <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>
						&& item[51]!='undefined' && item[11] == "<%=BudgetConstants.FORECAST%>"){
							var actualPlannedTotal=parseFloat(item[51]).toFixed(4);
							var calculatedPlannedTotal=0.0;
							for (var j = 12; j < 24; j++) {
								if(item[j] == "" || item[j] == "undefined"){
									item[j] = 0.0;
									
								}
								calculatedPlannedTotal= parseFloat(calculatedPlannedTotal) + parseFloat(item[j]);
							}
							/* if(calculatedPlannedTotal > actualPlannedTotal){
								columnValiation=true;
								alert("Sum of the entered budget of months exceeds Total specified for Multi brand project !!!");	
								item[itemCell]=args.item[45][itemCell-12];
								grid.invalidate();
								return;
							}else if(calculatedPlannedTotal < actualPlannedTotal){
								alert("Sum of the entered budget of months is less than Total specified for Multi brand project !!!");	
							} */
							
						}
						<%-- if(item[37]!='undefined' && item[37]==true && fixedCell >=  <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){ 
						var interimTotal=0.0;
						var actualPlannedTotal=parseFloat(data[temp][24]).toFixed();
						var calculatedPlannedTotal=0.0;
						if(data[temp][11] == 'Accrual'){
							for (var j = 0; j < data.length - 1; j++) {
								if (data[j][27] == args.item[27] && data[j][11]=="<%=BudgetConstants.FORECAST%>") {
									actualPlannedTotal=data[j][24];
									break;
								}
							}
						}
						for (var j = 12; j < 24; j++) {
							if(data[temp][j] == "" || data[temp][j] == "undefined"){
								data[temp][j] = 0.0;
							}
							interimTotal = parseFloat(interimTotal)
										+ parseFloat(data[temp][j]);
						}
						if(interimTotal>actualPlannedTotal ){
							alert("Sum of the entered budget of months exceeds Total specified for Multi brand project !!!");
							data[temp][itemCell]=args.item[45][itemCell-12];
							grid.invalidate();
							return;
						}
					} --%>
					grid.invalidate();
			

					if(args.item[6].toString().toLowerCase().indexOf("smart wbs") != -1 && args.item[35] == "NewProjects" && cell == <%=BudgetConstants.BRAND_CELL%> && lastKeyPressed == 9){
						addMultiBrandPopUp();
					}
					
					if(args.item["34"] != "New projects"){
						updateMemCache(e, args, tempKey);
						<%--	for(var counter = 0; counter<data.length; counter++ ){
							if(data[counter][34] != "New projects"){
								dataLength++;
							}
						}
						// Caluculation of total (columnwise)
						var verPlannedTotal=0.0;
						var verBenchmarkTotal=0.0;
						var verAccrualTotal=0.0;
						var verVarianceTotal=0.0;
						var verPlanned=0.0;
						var verBenchmark=0.0;
						var verAccrual=0.0;
						var verVariance=0.0;
						var rowTotal=0.0;
						
						if(fixedCell >=  <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
							
							//if((item[37]=='undefined' || item[37]==false) || (item[37]==true && data[temp][11]!="Forecast")){
								rowTotal = 0.0;
								for (var j = 12; j < 24; j++) {
									if(data[temp][j] == "" || data[temp][j] == "undefined"){
										data[temp][j] = 0.0;
									}
									rowTotal = parseFloat(rowTotal)
												+ parseFloat(data[temp][j]);
								}
								data[temp][24]=parseFloat(rowTotal).toFixed(2);
								if(item[37]==true){
									for (var j = 0; j < data.length ; j++) {
										if( data[j][0].toString().indexOf(".") != -1 && data[j][0].toString().split(".")[0] == item[0] ){
											rowTotal = 0.0;
											for (var k = 12; k < 24; k++) {
												if(data[j][k] == "" || data[j][k] == "undefined"){
													data[j][k] = 0.0;
												}
												rowTotal = parseFloat(rowTotal)
															+ parseFloat(data[j][k]);
											}
											data[j][24]=parseFloat(rowTotal).toFixed(2);
										}
									}
								}
							//}
							
							
							for (var j = 0; j < data.length ; j++) {
								if(data[j][26] != 'Total' && data[j][0] != 'undefined' && data[j]["34"] != "New projects"){
									if( data[j][11] == "<%=BudgetConstants.FORECAST%>"){
										if(data[j][37] == false && data[j][27].toString().indexOf(".") == -1){
											verPlannedTotal= parseFloat(verPlannedTotal) + parseFloat(data[j][itemCell]);
											verPlanned= parseFloat(verPlanned) + parseFloat(data[j][24]);
										}else if(data[j][37] == true && data[j][27].toString().indexOf(".") != -1){
											verPlannedTotal= parseFloat(verPlannedTotal) + parseFloat(data[j][itemCell]);
											verPlanned= parseFloat(verPlanned) + parseFloat(data[j][24]);
										}
									}				
									if(data[j][11]=="<%=BudgetConstants.QUARTERLY_TARGET%>"  && data[j][27].toString().indexOf(".") ==-1){
										verBenchmarkTotal= parseFloat(verBenchmarkTotal) + parseFloat(data[j][itemCell]);
										verBenchmark= parseFloat(verBenchmark) + parseFloat(data[j][24]);
									}
									if(data[j][11]=="<%=BudgetConstants.ACCRUAL%>" && data[j][27].toString().indexOf(".") ==-1 ){
										verAccrualTotal= parseFloat(verAccrualTotal) + parseFloat(data[j][itemCell]);
										verAccrual= parseFloat(verAccrual) + parseFloat(data[j][24]);
									}
									if(data[j][11]=="<%=BudgetConstants.QUARTERLY_LTS%>" && data[j][27].toString().indexOf(".") ==-1 && data[j][26]!='' ){
										verVarianceTotal= parseFloat(verVarianceTotal) + parseFloat(data[j][itemCell]);
										verVariance= parseFloat(verVariance) + parseFloat(data[j][24]);
									}
								}
							}
							data[data.length - 4][itemCell]=verPlannedTotal;
							data[data.length - 3][itemCell]=verBenchmarkTotal;
							data[data.length - 2][itemCell]=verAccrualTotal;
							data[data.length - 1][itemCell]=verBenchmarkTotal-verAccrualTotal;
							data[data.length - 4][24]=verPlanned;
							data[data.length - 3][24]=verBenchmark;
							data[data.length - 2][24]=verAccrual;
							data[data.length - 1][24]=verBenchmark-verAccrual;
							
						}
						grid.invalidate();--%>
						calculateTotal();
						dataView.refresh();
					}
		});
		function do_the_ajax_call(){
			var openPopUp =  false;
			var gMemoriId ;
			$.ajax({
				url : '/initiateProject',
				type : 'GET',
				async: false,
				dataType : 'text',
				data : {ccId: itemClicked[47],
					unixId: itemClicked[48],
					prj_name:itemClicked[2],
					dummyGMemId:itemClicked[0]
				},
				success : function(result) {
					var obj = $.parseJSON(result);
					var statusCode = obj.statusCode;
					if(statusCode == 200){
						
						gMemoriId = obj.newGMemId;
						openPopUp =  true;
						
					}else{
						if(obj!=null && obj.statusMessage!=null){
						alert("Error occured during synchronization with Study : \n"+obj.statusMessage);
						}else{
							alert("Error occured during synchronization with Study : \n Internal error occured.");
						}
					}
				},
				error : function(result){
					alert("Error occured during synchronization with Study : \n Internal error occured.");
				}
			});
			if(openPopUp == true){
			window.open ("https://memori-qa.appspot.com/initiateProject?gMemoriId="+gMemoriId,'gmemori','');
			openPopUp =  false;
			window.location.reload(true);
			//alert("Project successfully created in Budget Tool. Please continue.");
			}
		}
		
		grid.onClick.subscribe(function(e, args) {
				grid.gotoCell(args.row, args.cell, false);
				itemClicked = dataView.getItem(args.row);
				console.log(itemClicked);
				if(args.cell == <%=BudgetConstants.GMEMORI_ID_CELL%> &&
						itemClicked[0].toString().trim != "" && itemClicked[11] == "<%=BudgetConstants.FORECAST%>" && itemClicked[26] != "Total" && 
						itemClicked[2] != "" && itemClicked[0].toString().length==10){
					if(('<%=role%>'=='Admin' || ('<%=role%>'!='Admin' && itemClicked[1]=='<%=user.getUserName()%>'))){
					var myPopup = window.open ("", 'gmemori', '');
					do_the_ajax_call();
					}else{
						alert("You are not authorised to initiate project : "+itemClicked[2]);
						return;
					}
				}else if(args.cell == <%=BudgetConstants.BRAND_CELL%> && itemClicked[6].toString().toLowerCase().indexOf("smart wbs")!=-1){
					if(itemClicked[26] == "New"){
						var userAccepted = confirm("Do you want to convert it in to a single brand?");
						if (!userAccepted) {
							addMultiBrandPopUp();
						}
						else{
							multiBrandToSingle = true;
							itemClicked[37] = "";
							itemClicked[36] = false;
							return;
						}
					}else{
						addMultiBrandPopUp();
				}
					 
				} 
			if ($(e.target).hasClass("toggle")) {
				var item = dataView.getItem(args.row);
				if (item) {
					if (!item._collapsed) {
						item._collapsed = true;
					} else {
						item._collapsed = false;
					}
					dataView.updateItem(item.id, item);
				}
				e.stopImmediatePropagation();
			}
			
			var cell = args.cell;
			var fixedCell;
			if ($('#hideColumns').is(":checked")) {
				fixedCell = cell + numHideColumns;
			} else {
				fixedCell = cell;
			}
		});
		
		var errExist = false;
		grid.onValidationError.subscribe(function(e, args) {
			if(errExist){
				return;
			}
	        var validationResult = args.validationResults;
	        var activeCellNode = args.cellNode;
	        var editor = args.editor;
	        var errorMessage = validationResult.msg;
	        var valid_result = validationResult.valid;
	        if (!valid_result) {
	        	alert(errorMessage);
	        	errExist = true;
	          	$(activeCellNode).attr("title", errorMessage);
	        }
	        else {
	           $(activeCellNode).attr("title", "");
	        }

	    }); 
		
		// Handeler for Create New Project button
		$(document).on('click', '#crtNewProjBtn',
			    function() {
					createIntProjects();
			    }
		);
		
		
		
		
		// Handeler for click on submit and cancel button under new project creation
		$(document).on('click', '#submitProjBtn',
		    function() {
			if(columnValiation==false){
				$('#submitProjBtn').prop("disabled",true);
			 	submitProjects();
			}else{
				columnValiation=false;
			}
		    }
		);
		
		$(document).on('click', '#cnclProjBtn',
			function() {
				cancelProjects();
			}
		);

		
		
		
		
		// brand select using arrow keys
		grid.onKeyDown.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row - 1;
			var fixedCell = cell;
			lastKeyPressed = e.which;
			if ((e.which == 38 || e.which == 40 || e.which == 13) && cell == "<%=BudgetConstants.BRAND_CELL%>") {
				if ($('#hideColumns').is(":checked")) {
					fixedCell = cell + numHideColumns;
				}
				data[row][fixedCell] = 0.0;
				updateTotals(cell, row, fixedCell, args);
				if (!grid.getEditorLock().commitCurrentEdit()) {
					return;
				}
				grid.invalidate();
				e.stopPropagation();
			}
		}); 
		
		// make the current and future month cells editable
		grid.onBeforeEditCell
				.subscribe(function(e, args) {
			var monthArray = ["JAN", "FEB","MAR","APR","MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV","DEC"];
			var cell = args.cell;
			var row = args.row;
			var cols = grid.getColumns();
			args.item[46]=JSON.parse(JSON.stringify(args.item));
			var fixedCell = cell;
			if(args.item["26"]=="Closed"){
				return false;
			}
			if ($('#hideColumns').is(":checked")) {
				fixedCell = cell + numHideColumns;
			} else {
				fixedCell = cell;
			}
			var userName = '<%=user.getUserName()%>';
			var role = '<%=user.getRole()%>';
			if((args.item["27"].toString().indexOf(".") != -1 && args.item["37"] == true && args.item["11"] == "<%=BudgetConstants.ACCRUAL%>") ){
				args.item[50]=args.item[fixedCell];
			}
			if(	(!($('#selectedUserView').val().toLowerCase() == "my projects")) && (args.item["34"] != "New projects") && role != "Admin"){
				return false;
			} 
			if((role!='Admin') && (args.item["26"]=="Active" || args.item["26"]=="New") && 
					(args.item["11"] == "<%=BudgetConstants.ACCRUAL%>" || args.item["11"] == "<%=BudgetConstants.FORECAST%>") && 
					(args.item["48"]!=null && args.item["48"]!='' && args.item["48"] != userName)){
				alert("You are not authorised to edit this project !!!");
				return false;
			}
			if(fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
				var budgetItem=[];
				for(var iBudget=0;iBudget<12;iBudget++){
					budgetItem[iBudget]=args.item[iBudget+12];
				}
				args.item[45]=budgetItem;
			}
			if(args.item["34"]!="New projects" ){
				if((args.item["11"] == "<%=BudgetConstants.ACCRUAL%>" && args.item["26"]=="Active") && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
					args.item["43"] = args.item[fixedCell];
					grid.invalidate();
				}
				if(((args.item["27"].toString().indexOf(".") == -1 && args.item["37"] == true && args.item["11"] == "<%=BudgetConstants.ACCRUAL%>") 
						|| (args.item["27"].toString().indexOf(".") != -1 && args.item["37"] == true && args.item["11"] != "<%=BudgetConstants.ACCRUAL%>"))
						){
					return false;
				}
				if(cell == "<%=BudgetConstants.BRAND_CELL%>" && args.item["11"] == "<%=BudgetConstants.FORECAST%>"  && args.item["26"] =="New" ){
					return true;
				}
				if (args.item["11"] == "<%=BudgetConstants.FORECAST%>"
					&& cols[cell].name == "PO Number" &&  args.item["26"] !="Total" && args.item["26"] =="New") {
					return true;
				}
				var isAnEditableId = false;
				if(args.item["11"] == "<%=BudgetConstants.FORECAST%>" && args.item[0].toString().indexOf(".") == -1 && cell==4 ){
					isAnEditableId = true;
				}
				if (args.item["11"] == "<%=BudgetConstants.FORECAST%>"
									&& (cols[cell].name == "Project Name" || cols[cell].name == "Project WBS" || 
											cols[cell].name == "SubActivity"  || cols[cell].name == "Vendor" || cols[cell].name == "Units" || isAnEditableId ) &&  
											args.item["26"] !="Total" && (args.item["26"] =="New" || args.item["26"] =="Active")) {
					return true;
				}
				var newYear =args.item["39"];
				var createYear = args.item["38"].split("-")[0];
				var quarter = <%=qtr%>;
				var month;
				if(newYear > createYear){
					month = 0;
				}else{
					month='<%=month%>';	
				}
				for (var i = month; i < 12; i++) {
					if (cols[cell].name == monthArray[i]
							&& ((args.item["11"] == "<%=BudgetConstants.FORECAST%>" && args.item["26"] !="Total"))) {
						return true;
					} 
				}
				
				if (args.item["11"] == "<%=BudgetConstants.FORECAST%>"
						&& cols[cell].name == "Comments" &&  args.item["26"] !="Total") {
					return true;
				} 
				
				return false;
			}else{
				if( cols[cell].name == "$ in 1000's" ||  cols[cell].name == "Status" || cols[cell].name == "Allocation %"){
					return false;
				}
				if( args.item["35"]!="Buttons"){
					return true;
				}else{
					return false;
				}
			}
		});

		// wire up model events to drive the grid
		dataView.onRowCountChanged.subscribe(function(e, args) {
			grid.updateRowCount();
			grid.render();
		});

		dataView.onRowsChanged.subscribe(function(e, args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

		// Handeling search textbox 
		$("#txtSearch").keyup(function(e) {
			map = {};
			Slick.GlobalEditorLock.cancelCurrentEdit();
			// clear on Esc
			if (e.which == 27) {
				this.value = "";
			}
			searchString = this.value;
			searchString = searchString.replace(/</g, "&lt;");
			searchString = searchString.replace(/>/g, "&gt;");
			
		    if (searchString != "") {
				dataView.expandGroup("Active");
				dataView.expandGroup("Closed");
				dataView.expandGroup("New");
			} else {
				
				dataView.collapseGroup("New");
				dataView.collapseGroup("Active");
				dataView.collapseGroup("Closed");
			}
			dataView.refresh();
			if(!isMatchPresent && searchString != ""){
				alert("No Search Results Found!");	
			}
			calculateTotal();
		});

		
		// Handeling radio button "Forecast" and "All"
		rdoSelectedmode.change(function(e) {
			Slick.GlobalEditorLock.cancelCurrentEdit();
			var choice = this.value;
			if (choice == 'planned') {
				radioString = 'Forecast'
			} else {
				radioString = "All";
			}
			dataView.refresh();
		});

		// Handeling hide column check box
		chkBoxHideColumns.change(function(e) {
			Slick.GlobalEditorLock.cancelCurrentEdit();
			if (this.checked) {
				grid.setColumns(hidecolumns);
			} else {
				grid.setColumns(columns);
			}
			dataView.refresh();
		});

		// Display details on mouse over a cell while the details exceeds the cell size
		grid.registerPlugin(new Slick.AutoTooltips({
			enableForHeaderCells : true
		}));
		grid.render();
		
		$('input[name=file]').change(function() {
			if($(this).val.toString().trim() != ""){
				$("#fileUploadBtn").prop('disabled', false);
	        }
		});
		
		if($('#selectedUserView').val() == 'My Projects' || '<%=user.getRole()%>'=="Admin"){
			$('#exportButton').show();
		}else{
			$('#exportButton').hide();
		}
		$('#selectedUserView').change(function() {
			if($('#selectedUserView').val() == 'My Projects'  || '<%=user.getRole()%>'=="Admin"){
				$('#exportButton').show();
			}else{
				$('#exportButton').hide();
			}
		});
	})

	// Persist the data to datastore while moving to other page or closing the application
	/* $(window).bind(
			'beforeunload',
			function(e) {
				$('#statusMessage').text("Saving data...").fadeIn(200);
				$.ajax({
					url : '/AutoSaveData',
					type : 'POST',
					dataType : 'text',
					data : {
						key : "",
						cellValue : "",
						celNum : "",
						mapType: ""
					},
					success : function(result) {
						$('#statusMessage').text(
								"All changes saved successfully!").fadeIn(200);
						$("#statusMessage").fadeOut(400);
					}
				});
			}); */
	
	
	  var m_grid;
	  
	  var m_options = {
	    editable: true,
	    enableAddRow: true,
	    enableCellNavigation: true,
	    asyncEditorLoading: false,
	    autoEdit: true,
	    enableColumnReorder: false
	  };
	  var sum = 0.0;
	  var m_columns = [
	 	{
			id : 1,
			name : "",
			field : 8,
			width : 25,
			formatter : Slick.Formatters.checkbox
		},
		/* {
			id : 2,
			name : "Project name",
			field : 4,
			width : 160,
			editor : Slick.Editors.Text
		}, {
			id : 3,
			name : "Project Owner",
			field : 7,
			width : 125,
			editor : Slick.Editors.Auto
		},
		 {
			id : 4,
			name : "gmemori id",
			field : 5,
			width : 100,
			editor : Slick.Editors.Text
		}, */
	    {
			id : 5,
			name : "Brand",
			field : 1,
			width : 160,
			editor : Slick.Editors.Auto
		}, {
			id : 6,
			name : "Total($ in 1000's)",
			field : 3,
			width : 140,
			editor : Slick.Editors.FloatText
		}, {
			id : 7,
			name : "Allocation %",
			field : 2,
			width : 125
		}
	  ];
		

	
	
	
	

	// Multibrand popup window
	function displayMultibrandGrid() {
		m_grid = new Slick.Grid("#multibrandGrid", m_data, m_columns, m_options);
		m_grid.setSelectionModel(new Slick.CellSelectionModel());
		m_grid.registerPlugin(new Slick.AutoTooltips());
		m_grid.getCanvasNode().focus();

		m_grid.onClick.subscribe(function(e, args) {
			
			m_grid.gotoCell(args.row, args.cell, false);
			if (args.cell == <%=BudgetConstants.MB_CHECKBOX_CELL%>) {
				initDeletionCell(args.row);
			}
		})
		
		

		m_grid.onAddNewRow.subscribe(function(e, args) {
			var item = args.item;
			var column = args.column;
			var row = args.row;
			m_grid.invalidateRow(m_data.length);
			m_data.push(item);
			m_grid.updateRowCount();
			m_grid.render();
		});

		m_grid.onValidationError.subscribe(function(e, args) {
			var validationResult = args.validationResults;
			var activeCellNode = args.cellNode;
			var editor = args.editor;
			var errorMessage = validationResult.msg;
			var valid_result = validationResult.valid;

			if (!valid_result) {
				alert(errorMessage);
				$(activeCellNode).attr("title", errorMessage);
			} else {
				$(activeCellNode).attr("title", "");
			}

		});
		
		m_grid.onKeyDown.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row - 1;
			var fixedCell = cell;
			if (e.which == 38 || e.which == 40 || e.which == 13) {
				if ($('#hideColumns').is(":checked")) {
					fixedCell = cell + numHideColumns;
				}
				data[row][fixedCell] = 0.0;
				updateTotals(cell, row, fixedCell, args);
				if (!m_grid.getEditorLock().commitCurrentEdit()) {
					return;
				}
				m_grid.invalidate();
				e.stopPropagation();
			}
		}); 

		m_grid.onBeforeEditCell
				.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row;
			var pRow = row + 1;
			if(itemClicked[26] == "Closed"){
				return false;
			}
			if((m_data[row]["7"] .toString().trim() == "" || m_data[row]["1"] .toString().trim() == "") && cell == <%=BudgetConstants.MB_$_IN_THOUSAND_CELL%>){
				return false;
			}
			if((itemClicked[1]!='<%=user.getUserName()%>' && '<%=user.getRole()%>'!="Admin" )){
				return false ;
			}
			if((itemClicked[1]!='<%=user.getUserName()%>' && '<%=user.getRole()%>'!="Admin" )){
				return false ;
			}
			if ((args.item[0].toString().trim() != "" && itemClicked[26] == "Active")
					|| (itemClicked[26] == "Closed")) {
				return false;
			}
			if (row != 0) {
				<%-- if (cell == <%=BudgetConstants.MB_PROJECT_OWNER_CELL%>) {
					m_data[row]["7"] = m_data[row - 1]["7"];
					m_grid.invalidate();
					return false;
				} --%>
				if (cell == <%=BudgetConstants.MB_BRAND_CELL%>) {
					m_data[row]["4"]=itemClicked[2];
					if (m_data[row]["5"] == "") {
						m_data[row]["5"] = m_data[row - 1]["5"]
								.split(".")[0]
								+ "."
								+ (parseInt(m_data[row - 1]["5"]
										.split(".")[1]) + 1);
						m_grid.invalidate();
					}
				//	return false;
				}
				if ((m_data[row]["7"] == 'undefined' || m_data[row]["7"] == "")
						&& cell == <%=BudgetConstants.MB_BRAND_CELL%>) {
					m_data[row]["7"] = m_data[row - 1]["7"];
					m_grid.invalidate();
					//return false;
				}
			}
			<%-- if (cell == <%=BudgetConstants.MB_BRAND_CELL%>) {
				availableTags = [];
				
				for (var j = 0; j < ccUsersVar.length; j++) {
					if (ccUsersVar[j][0] == m_data[row]["7"]) {
						var res = ccUsersVar[j][1].substring(1,
								ccUsersVar[j][1].length - 1);
						availableTags = res.split(",");
						break;
					}
				}
			} --%>
			<%-- if (row == 0){
				if(cell == <%=BudgetConstants.MB_GMEMORI_ID_CELL%>){
					return false
				}
				return true;
			} --%>
			//alert(row+"::::"+JSON.stringify(m_data));
			
			return true;
		});

		

		m_grid.onCellChange
				.subscribe(function(e, args) {
					var cell = args.cell;
					var row = args.row;
					var isValidBrand = false;
					sum = 0.0;
					if (cell == <%=BudgetConstants.MB_$_IN_THOUSAND_CELL%>) {

						for (var count = 0; count < m_data.length; count++) {
							if(( m_data[count]["3"] != "" )
									&&   m_data[count]["3"] != "undefined"){
								sum = sum + parseFloat(m_data[count]["3"]);
							}
						}
						for (var count = 0; count < m_data.length; count++) {
							if(isNaN(parseFloat(m_data[count]["3"]))){
								m_data[count]["2"]="";
							}
							else if(!isNaN(m_data[count]["3"] / sum * 100)){
								m_data[count]["2"] = (m_data[count]["3"] / sum * 100)
									.toFixed(4);
							}else{
								m_data[count]["2"] = "0";
							}
						}
 						 if (row + 1 >= 5 && m_grid.getDataLength() == row + 1) {
							var initMData = (m_data[m_grid.getDataLength()] = {});
							initMData[0] = "";
							initMData[1] = "";
							initMData[2] = "";
							initMData[3] = "";
							initMData[4] = "";
							initMData[5] = "";
							initMData[6] = "";
							initMData[7] = "";
							initMData[8] = false;
							initMData[9] = "";
							m_grid.invalidate();
							m_grid.invalidateRow(m_grid.getSelectedRows());
							m_grid.updateRowCount();
							m_grid.render();
						} 
						m_grid.invalidate(); 
					}
					<%-- if (cell == <%=BudgetConstants.MB_PROJECT_OWNER_CELL%>
							&& poOwners.toString().indexOf(m_data[row][7]) == -1) {
						for (var i = 0; i < poOwners.length; i++) {
							if (poOwners[i] === m_data[row][1]) {
								isValidBrand = true;
								break;
							}
						}
						if (isValidBrand == false) {
							m_data[row][7] = "";
							alert("Please choose a valid project owner.");
							m_grid.invalidate();
							return;
						}
					} --%>
					if (cell == <%=BudgetConstants.MB_BRAND_CELL%>) {
						for (var i = 0; i < availableTags.length; i++) {
							if (availableTags[i].toString().trim()
									.toLowerCase() === m_data[row][1]
									.toString().trim().toLowerCase()) {
								m_data[row][1] = availableTags[i].toString();
								isValidBrand = true;
								break;
							}
						}
						if (isValidBrand == false) {
							m_data[row][1] = "";
							alert("Enter a valid brand.");
							m_grid.gotoCell(row, <%=BudgetConstants.MB_BRAND_CELL%>, true);
						}
						m_grid.invalidate();
					}

				});

	}
	
	var resized = false;
	$(window).resize(function() {
		grid.resizeCanvas();
		if($(window).width() < 900){
			$('#cautionWindow').show().fadeIn(100);
			$('#back').addClass('black_overlay').fadeIn(100);
			resized = true;
		}else{
			$('#cautionWindow').hide();
			if(resized){
				$('#back').removeClass('black_overlay').fadeIn(100);
				resized = false;
			}
		}
	});
	
	function addMultiBrandPopUp(){
		if(!(itemClicked[37]) && itemClicked[35] != "NewProjects"){
			m_data[0][1]=itemClicked[44];
			m_data[0][3]=itemClicked[24];
			m_data[0][2]=100.0;
			m_data[0][4]=itemClicked[2];
		 	m_data[0][5]=itemClicked[0]+'.1';
		 	m_data[0][7]=itemClicked[1];
		 	singleBrandToMulti=true;
		}
		<%selectedCostCenter = (String)request.getAttribute("getCCValue");
		if(selectedCostCenter==null || "".equals(selectedCostCenter)){
			selectedCostCenter = userInfo.getSelectedCostCenter();
		}
		MemcacheService cacheCC = MemcacheServiceFactory.getMemcacheService();
		Map<String,ArrayList<String>> ccUsers = util.getCCUsersList(selectedCostCenter);%>
		// multi brand click
		
		var usr=0;
		var userCnt=0;
		<%Set<String> userList = ccUsers.keySet();
		for(Map.Entry<String,ArrayList<String>> userMapDetails: ccUsers.entrySet()){%>
		 poOwners[userCnt] = "<%=userMapDetails.getKey()%>";
		 var d = (ccUsersVar[userCnt] = {});
		 d[0]=   poOwners[userCnt];
		 d[1] = "<%=userMapDetails.getValue()%>";
		 
		 userCnt++;
		<%}%>
		
		
		if(itemClicked[34]!="New projects"){
			// Start : For Multibrand projects on click of brand (with mb) display pop-up conatining sub-projects
			var multiBrandCnt = 0 ;	
			<%GtfReport pGtfReport = new GtfReport();
			
			for(GtfReport gtfReport : gtfReports){
				requestor = gtfReport.getRequestor();
				if(requestor.contains(":")){
				requestor = requestor.substring(0,requestor.indexOf(":"));
				}%>
				var contains = '<%=gtfReport.getgMemoryId().contains(".")%>'; 
				var gMemoriId='<%=gtfReport.getgMemoryId()%>';

				if(contains =='true'  && gMemoriId.toLowerCase().indexOf(itemClicked[0])==0 ){ 
					var d = (m_data[multiBrandCnt++] = {});
	 				var parent;
	 				d["0"] = "<%=gtfReport.getId()%>";
					d["1"] = "<%=gtfReport.getBrand()%>";
					d["2"] = "<%=gtfReport.getPercent_Allocation()%>";
					<%Double total = gtfReport.getPlannedMap().get(BudgetConstants.total);%>
					d["3"] = "<%=total%>";
					d["4"] = "<%=gtfReport.getProjectName()%>";
					d["5"] =  "<%=gtfReport.getgMemoryId()%>";
					d["7"] = "<%=requestor%>";
					d["9"] = "preExisting";
				}
					
			<%}%>
			<%-- System.out.println("user.getUserName() = "+user.getUserName());
				System.out.println("requestor = "+requestor);
			if((!user.getUserName().equalsIgnoreCase(requestor) && !"Admin".equalsIgnoreCase(user.getRole()) )){%>
			return;
			<%}else{%> --%>
			if(itemClicked[26]=='Closed'){
				$('#deleteSel').attr("disabled", true);
				$('#saveClose').attr("disabled", true);
			}else{
				$('#deleteSel').attr("disabled", false);
				$('#saveClose').attr("disabled", false);
			}
			$('#multibrandEdit').show().fadeIn(100);
			displayMultibrandGrid();
			$('#back').addClass('black_overlay').fadeIn(100);
			// End : For Multibrand projects on click of brand (with mb) display pop-up conatining sub-projects
			<%-- <%}%> --%>
			var index = availableTags.indexOf("Smart WBS");
			if (index > -1) {
				availableTags.splice(index, 1);
			}
		}
		//code for newly added projects 
		else if(itemClicked[34]=="New projects"){
			// Start : Code for newly added projects
			var error=0;
			var errStrng="";
			//alert("itemClicked = "+JSON.stringify(itemClicked));
			if(itemClicked[2]=='' || itemClicked[0]=='' || itemClicked[1]=='' || 
				itemClicked[2]=='undefined' || itemClicked[0]=='undefined' || itemClicked[1]=='undefined'){
		
				if(itemClicked[2]=='' || itemClicked[2]=='undefined'){
					error=error+1;
				}
				/* if(itemClicked[0]=='' || itemClicked[0]=='undefined'){
					error=error+3;
				} */
				if(itemClicked[1]=='' || itemClicked[1]=='undefined'){
					error=error+5;
				}
				switch(error) {
	    			case 0:
	        			break;
	    			case 1:
	    				errStrng="Project name can not be blank."
	        			break;
	    			/* case 3:
	    				errStrng="gMemoriID can not be blank."
	        			break;
	    			case 4:
	    				errStrng="gMemoriID or Project name can not be blank."
	        			break; */
	    			case 5:
	    				errStrng="Project Owner can not be blank."
	        			break;
	    			case 6:
	    				errStrng="Project name or Project Owner can not be blank."
	        			break;
	    			default:
	        		break;
				}
			}
			if(error==0){
			 	if(itemClicked[37]){
					 m_data = JSON.parse(JSON.stringify(itemClicked[36]));
			 	}else{
				 	m_data[0][4]=itemClicked[2];
				 	m_data[0][5]=itemClicked[0]+'.1';
				 	m_data[0][7]=itemClicked[1];
			 	}
			 	$('#multibrandEdit').show().fadeIn(100);
				displayMultibrandGrid();
				$('#back').addClass('black_overlay').fadeIn(100);
				var index = availableTags.indexOf("Smart WBS");
				if (index > -1) {
					availableTags.splice(index, 1);
				}
			}else{
				alert(errStrng);
				itemClicked[6] = "";
				grid.invalidate();
			}
		}
	}
</script>
	<!-- to be removed after uat 2 -->
	<div width='100%' align=right>
		<button id="exportButton" class="myButton" value=""
			onclick="openDownloadPopUp();"
			style="height: 25px; letter-spacing: 1px;"
			align='right'>Export data as excel</button>
	</div>
	</br>
	<!-- <div>
	<button class="myButton" value="" onclick="onClickAsynch();" style="height: 25px; letter-spacing:1px;" align= 'left'>Download All CostCenters</button>
	</div>
	 -->
	<div id="uploadWindow">
		<div id="header"
			style="width: 100%; height: 20px; background-color: #2271B0; color: white; border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; font-size: 17px; letter-spacing: 3px;"
			align=center>File Upload</div>
		<div align='right' style='padding-right: 100px;'>
			<form action="/userupload" method="post"
				enctype="multipart/form-data">
				<br /> <span style="font-size: 14px; font-weight: bold;">
					Select File to Upload : &nbsp;&nbsp;&nbsp; </span> <input type="file"
					name="file"
					accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel">
				<br /> <input class='myButton' id="fileUploadBtn" type="submit"
					value="Upload" disabled="true"> <input class='myButton'
					type="button" value="Cancel" onclick="closeUploadWindow();">
			</form>

		</div>
	</div>

	<div id="cautionWindow">
		<div id="header"
			style="width: 100%; height: 20px; background-color: red; color: white; border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; font-size: 17px; letter-spacing: 3px;"
			align=center>Caution!</div>
		<div style="font-size: 14px">Window width is not sufficient
			enough for application to be viewed. Please increase the window
			width.</div>
	</div>
	<%@ include file="footer.jsp"%>
</body>
<form id="exportExcel" name="exportExcel" method="post"
	action="/download" target="myIFrm">
	<input type="hidden" name="objarray" id="objArrayId" /> <input
		type="hidden" name="costCenter" id="ccId" /> <input type="hidden"
		name="viewSelected" id="viewSelected" /> <input type="hidden"
		name="brandSelected" id="brandSelected" />
</form>
<iframe id="myIFrm" name="myIFrm" src="" style="visibility: hidden">

</iframe>

</html>