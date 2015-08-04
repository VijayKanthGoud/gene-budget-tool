<%@page import="com.gene.app.model.*"%>
<%@page import="com.gene.app.dao.DBUtil"%>
<%@page import="com.gene.app.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="javax.servlet.RequestDispatcher"%>

<%@ include file="header.jsp"%>

<%
	String prjView = "";
	String brandView = "";
	String ccView = "";
	String color = "";
	ArrayList<Object> userlist;

	List<GtfReport> gtfReports = (List<GtfReport>) request.getAttribute("gtfreports");
	
	for (GtfReport report : gtfReports) {
		LOGGER.log(Level.INFO, "Reports received : " + report.getgMemoryId());
	}
	
	Calendar cal = Calendar.getInstance();
	
	int year = cal.get(Calendar.YEAR);
	int month = cal.get(Calendar.MONTH);
	int qtr = month / 3;
	
	Map<String, Date> cutofDates = util.getCutOffDates();
	Date cutOfDate = cutofDates.get(qtr+"");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	String viewSelected = (String)request.getAttribute("selectedView");
	System.out.println("View Selected : "+viewSelected);
	
	if(viewSelected==null || "".equalsIgnoreCase(viewSelected.trim())){
		viewSelected = "My Projects";
	}if("My Projects".equalsIgnoreCase(viewSelected)){
		prjView = "selected";
	}else if("My Brands".equalsIgnoreCase(viewSelected)){
		brandView = "selected";
	}else{
		ccView = "selected";
	}
	
	String ccSelected = (String)request.getAttribute("getCCValue");
	UserRoleInfo userInfo = (UserRoleInfo)session.getAttribute("userInfo");
	String[] costcenter_list = userInfo.getCostCenter().split(":");
	Arrays.sort(costcenter_list);
	String costcenter_selected;
	
	String selectedView = (String)request.getAttribute("selectedView");
	String selectedBrand = (String)request.getAttribute("brandValue");
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
		
	Object[] myBrands = {}; 
	String brandValue1="";
	String brandValue=(String)request.getAttribute("brandValue");
	
	BudgetSummary summary = (BudgetSummary) session.getAttribute("summary");
	Map<String, BudgetSummary> budgetMap = Util.sortCaseInsensitive(summary.getBudgetMap());
	BudgetSummary budgetSummary = new BudgetSummary();
	UserRoleInfo user = (UserRoleInfo) request.getAttribute("user");
	String cc = user.getSelectedCostCenter();
	Map<String,Double> brandMap= new LinkedHashMap<String,Double>();
	for(CostCenter_Brand cc1: ccList){
		if(cc1!=null && cc1.getCostCenter()!=null && !"".equalsIgnoreCase(cc1.getCostCenter()) && selectedCostCenter.equalsIgnoreCase(cc1.getCostCenter())){
			brandMap = util.getBrandMap(cc1.getBrandFromDB());
		}
	}
	LOGGER.log(Level.INFO, "brandMaps received : " + brandMap);
	Object[] brands = {}; 
	if(brandMap!=null && !brandMap.isEmpty()){
		brands = brandMap.keySet().toArray();
	}
	
	String option = "";
    if(brandValue==null || brandValue==""){
		brandValue = "Avastin";
	} 
    budgetSummary = budgetMap.get(brandValue);
%>

<link rel="stylesheet" href="SlickGrid-master/slick.grid.css"
	type="text/css" />
<link rel="stylesheet"
	href="SlickGrid-master/css/smoothness/jquery-ui-1.8.16.custom.css"
	type="text/css" />
<link rel="stylesheet" href="SlickGrid-master/examples/examples.css"
	type="text/css" />

<html>
<body onload="getAvailableTags(); refreshSummary();">
	<div align="center">
		<table style="border: 1px solid gray; background: #EAF4FD; width: 100%; font-weight: normal; color: #2271B0; float: left;">
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
				<td style="padding-left: 1.5%; width: 50%; text-align: center;">
				 <form method="GET" id="getDataForm" action="/getreport">
					<table align="center" Style="border-top-left-radius: 3px;background-color: white;width: 351px;padding-left: 3px;border-radius: 5px; border: 1px solid #105596; padding-top: 2%">
						<tr>
							<td width="100px">
								<span style="font-size: 12px; font-weight: bold; color: #105596; float: right">Select View : </span>
							</td>
							<td>
							   <select id="selectedUserView" name="selectedView" onchange="selectUserView()" autofocus style="width: 120px; color: #105596;">
									<option <%=prjView%>>My Projects</options>
									<option <%=brandView%>>My Brands</options>
									<option <%=ccView%>>My Cost Center</options>
								</select>
							</td>
						</tr>
						<tr>
							<td>
								<span style="font-size: 12px; font-weight: bold;  color: #105596; float: right">
									Select Cost Center :
								</span>
							</td>
							<td>
								<select id="getCostCenter" name="getCCValue" style="width: 55px; height: 23px; color: #105596;" onchange="getCostCenterDetails()">
									<%
									   	if(costcenter_list != null){
											for(int k=0; k < costcenter_list.length; k++){
												costcenter_selected = costcenter_list[k];
												if(costcenter_selected != null && !"".equals(costcenter_selected) && ccSelected!=null && !"".equals(ccSelected) && ccSelected.equalsIgnoreCase(costcenter_selected)){
									%>
													<option value="<%=costcenter_selected%>" selected><%=costcenter_selected%></option>
														<%
															} else if((costcenter_selected)!=null && !"".equals(costcenter_selected)){
														%>
														<option value="<%=costcenter_selected%>"><%=costcenter_selected%></option>
									<%
															}}}
									%>
								</select>
							</td>
						</tr>
						<tr id="dropdown">
							<td><span style="font-size: 12px; font-weight: bold; color: #105596; float: right">
									Select Brand : </span></td>
							<td>
								<select id="getBrand" name="brandValue"
									onchange="getProjectsBrandwise()"
									style="width: 120px; color: #105596;">
										<%
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
											}}}
										%>
								</select>
							</td>
						</tr>
						<tr> 
							<td style="padding-left: 21.5%; padding-top: 20px;" colspan='2'>
								<input placeholder="Search" type=text autocomplete = "off" style="float: left; width: 150px;  background: url('images/search30-30px.png') top right no-repeat; background-size: 19px 19px; height:21px; padding-right:2px;border: 1px solid #105596;width: 180px;  color: #333333;  margin-bottom: 8px;  border-radius: 2px;"
								id="txtSearch" title="Search in Project name, gMemori Id, Brand and Comments.">
							</td>
						</tr>
				</table>
			</form>
		</td>
			

	 	<td style="width: 1%;" rowspan="2">
					<table class="summarytable"
						style="color: #2271B0; white-space: nowrap; font-weight: bold;">
						<tr align='center'>
							<td colspan=2>Annual Brand Summary &nbsp;($ in 1000's) <img alt="" src="images/refresh.png" height="25"
								width="25" align='left' onclick="refreshSummary()"></td>
						</tr>
						<tr>
						<tr>
							<td>Select Brand:</td>
							<td><select id="brandType" name="brandValue"
								onchange="getBrandTotals()"
								style="color: #2271B0;">
									<%
										if(budgetMap!=null && !budgetMap.isEmpty()){
								    		Object[] budgets = budgetMap.keySet().toArray();
									    	if(budgetSummary == null){
									        	budgetSummary = budgetMap.get(budgets[0]);
									        }
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
							<td style="text-align: right;"><span id="totalBudget"  > <%=new DecimalFormat("0.00").format(Math.round(budgetSummary.getTotalBudget() * 100.0) / 100.0)%></span></td>
						</tr>

						<tr>
							<td><span  title="Total Overall Forecast">Total Forecast:</span></td>
							<td style="text-align: right;"><span id="plannedTotal"  ><%=new DecimalFormat("0.00").format(Math.round(budgetSummary.getPlannedTotal() * 100.0) / 100.0)%></span></td>
						</tr>
						<tr>
							<td><span title="= Budget - Total Forecast">Unallocated Forecast:</span></td>
							<td style="text-align: right;"><span id="budgetLeftToSpend"><%=new DecimalFormat("0.00").format(Math.round(((budgetSummary.getTotalBudget() - budgetSummary.getPlannedTotal())*100.0)/100.0))%></span></td>
						</tr>
						<tr>
							<td><span title = "Total Dollars Spent">Total Accrual:</td>
							<td style="text-align: right;"><span id="accrualTotal"><%=new DecimalFormat("0.00").format(Math.round(budgetSummary.getAccrualTotal() * 100.0) / 100.0)%></span></td>
						</tr>
						<tr>
							<td><span id = "varTotalLabel" title = "= Budget - Total Accrual" >Budget LTS:</span></td>
							<td style="text-align: right;"> <span id="varTotalText" ><span
									id="varianceTotal"><%=new DecimalFormat("#.00").format(Math.round(budgetSummary.getBudgetLeftToSpend() * 100.0) / 100.0)%></span></span>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</div>
	
	<div id="statusMessage"></div>
	
	<div id="displayGrid" style="width: 100%; height: 44.5%; min-height: 200px;"></div>


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
	<script src="scripts/editProjects.js"></script>
	
	<script>
	
	
	function getAvailableTags(){
		if($('#selectedUserView').val() == 'My Brands'){
			$("#dropdown").show();
		}else{
			$("#dropdown").hide();	
		}
		availableTags[0] = "Smart WBS";
		var j;
		<%for(int i=0;i<myBrands.length;i++){%>
			j=<%= i+1%>;
			availableTags[j] = '<%= myBrands[i]%>';
		<%}%>
	} 
	
	
	// ON PAGE LOAD
	$(function() {
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
    				d[12]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("JAN"))%>";
    				d[13]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("FEB"))%>";
    				d[14]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("MAR"))%>";
    				d[15]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("APR"))%>";
    				d[16]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("MAY"))%>";
    				d[17]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("JUN"))%>";
    				d[18]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("JUL"))%>";
    				d[19]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("AUG"))%>";
    				d[20]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("SEP"))%>";
    				d[21]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("OCT"))%>";
    				d[22]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("NOV"))%>";
    				d[23]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("DEC"))%>";
    				d[41]="<%=gReport.getPercent_Allocation()%>";
    				d[51]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("TOTAL"))%>";
    				<%-- if(<%=gReport.getMultiBrand()%> == true){
    					d[24]="<%=new DecimalFormat("#.####").format(gtfReports.get(i).getPlannedMap().get("TOTAL"))%>";
    				}else{ --%>
    					d[24]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("JAN") + 
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
				d[12]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("JAN"))%>";
				d[13]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("FEB"))%>";
				d[14]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("MAR"))%>";
				d[15]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("APR"))%>";
				d[16]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("MAY"))%>";
				d[17]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("JUN"))%>";
				d[18]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("JUL"))%>";
				d[19]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("AUG"))%>";
				d[20]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("SEP"))%>";
				d[21]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("OCT"))%>";
				d[22]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("NOV"))%>";
				d[23]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("DEC"))%>";
				d[24]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getBenchmarkMap().get("JAN") + 
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
					d[12]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("JAN"))%>";
					d[13]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("FEB"))%>";
					d[14]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("MAR"))%>";
					d[15]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("APR"))%>";
					d[16]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("MAY"))%>";
					d[17]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("JUN"))%>";
					d[18]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("JUL"))%>";
					d[19]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("AUG"))%>";
					d[20]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("SEP"))%>";
					d[21]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("OCT"))%>";
					d[22]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("NOV"))%>";
					d[23]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("DEC"))%>";
					d[24]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getAccrualsMap().get("JAN") + 
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
					d[12]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("JAN"))%>";
					d[13]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("FEB"))%>";
					d[14]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("MAR"))%>";
					d[15]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("APR"))%>";
					d[16]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("MAY"))%>";
					d[17]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("JUN"))%>";
					d[18]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("JUL"))%>";
					d[19]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("AUG"))%>";
					d[20]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("SEP"))%>";
					d[21]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("OCT"))%>";
					d[22]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("NOV"))%>";
					d[23]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("DEC"))%>";
					d[24]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getVariancesMap().get("JAN") + 
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
				d[j] = d[j].toFixed(2);
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
			var url_string = window.location.href.split("?")[0];
			window.history.pushState(null, "", url_string);
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
							var actualPlannedTotal=parseFloat(item[51]).toFixed(2);
							var calculatedPlannedTotal=0.0;
							for (var j = 12; j < 24; j++) {
								if(item[j] == "" || item[j] == "undefined"){
									item[j] = 0.0;
									
								}
								calculatedPlannedTotal= parseFloat(calculatedPlannedTotal) + parseFloat(item[j]);
							}
							 if(calculatedPlannedTotal > actualPlannedTotal){
								columnValiation=true;
								alert("Sum of the entered budget of months exceeds Total specified for Multi brand project !!!");	
								item[itemCell]=args.item[45][itemCell-12];
								grid.invalidate();
								return;
							}
							
						}
					grid.invalidate();
			

					if(args.item[6].toString().toLowerCase().indexOf("smart wbs") != -1 && args.item[35] == "NewProjects" && cell == <%=BudgetConstants.BRAND_CELL%> && lastKeyPressed == 9){
						addMultiBrandPopUp();
					}
					
					if(args.item["34"] != "New projects"){
						updateMemCache(e, args, tempKey);
						calculateTotal();
						dataView.refresh();
					}
		});
		
		
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
		
		grid.onValidationError.subscribe(function(e, args) {
	        var validationResult = args.validationResults;
	        var activeCellNode = args.cellNode;
	        var editor = args.editor;
	        var errorMessage = validationResult.msg;
	        var valid_result = validationResult.valid;
	        if (!valid_result) {
	        	alert(errorMessage);
	          	$(activeCellNode).attr("title", errorMessage);
	        }
	        else {
	           $(activeCellNode).attr("title", "");
	        }

	    }); 
		
		
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
				//data[row][fixedCell] = 0.0;
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
			//Stop auto saving data until completed
			if($('#statusMessage').text() == "Saving data..."){
				return false;
			}
			//Stop editing while screen is disabled
			if($('#back').attr('class') != 'undefined' && $('#back').attr('class') == 'black_overlay'){
				return false;
			}
			var monthArray = ["JAN", "FEB","MAR","APR","MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV","DEC"];
			var cell = args.cell;
			var row = args.row;
			var cols = grid.getColumns();
			args.item[46]=JSON.parse(JSON.stringify(args.item));
			var fixedCell = cell;
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
			if((args.item["26"]=="Active" || args.item["26"]=="New" || args.item["26"]=="Closed") && 
					(args.item["11"] == "<%=BudgetConstants.ACCRUAL%>" <%-- || args.item["11"] == "<%=BudgetConstants.FORECAST%>" --%>) && 
					(args.item["48"]!=null && args.item["48"]!=''/*   && args.item["48"] != userName*/ ) ){
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
					&& cols[cell].name == "PO Number" &&  args.item["26"] !="Total" && (args.item["26"] == "New" || args.item["8"].toString().indexOf('1') == 0)) {
					return true;
				}
				var isAnEditableId = false;
				if(args.item["11"] == "<%=BudgetConstants.FORECAST%>" && args.item[0].toString().indexOf(".") == -1 && cell==4 ){
					isAnEditableId = true;
				}
				if (args.item["11"] == "<%=BudgetConstants.FORECAST%>"
									&& (cols[cell].name == "Project Name" || cols[cell].name == "Project WBS" || 
											cols[cell].name == "SubActivity"  || cols[cell].name == "Vendor" || cols[cell].name == "Units" || isAnEditableId ) &&  
											args.item["26"] !="Total" && (args.item["26"] =="New" || args.item["26"] =="Active" || args.item["26"] == "Closed")) {
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
		
		// Display details on mouse over a cell while the details exceeds the cell size
		grid.registerPlugin(new Slick.AutoTooltips({
			enableForHeaderCells : true
		}));
		grid.render();
	})

	var disableGrid = false;
	
	function addMultiBrandPopUp(){
		if(itemClicked[35] != "NewProjects"){
			disableGrid = true;
		}
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
					d["10"] = "<%=total%>";
					d["11"] = "<%=gtfReport.getPercent_Allocation()%>";
				}
					
			<%}%>
			
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

			var index = availableTags.indexOf("Smart WBS");
			if (index > -1) {
				availableTags.splice(index, 1);
			}
		}
		//code for newly added projects 
		else if(itemClicked[34]=="New projects"){
			var error=0;
			var errStrng="";
			if(itemClicked[2]=='' || itemClicked[0]=='' || itemClicked[1]=='' || 
				itemClicked[2]=='undefined' || itemClicked[0]=='undefined' || itemClicked[1]=='undefined'){
		
				if(itemClicked[2]=='' || itemClicked[2]=='undefined'){
					error=error+1;
				}
				if(itemClicked[1]=='' || itemClicked[1]=='undefined'){
					error=error+5;
				}
				switch(error) {
	    			case 0:
	        			break;
	    			case 1:
	    				errStrng="Project name can not be blank."
	        			break;
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
	
	var USER_ROLE = '<%=userInfo.getRole()%>';
	var USER_NAME = '<%=userInfo.getUserName()%>';

	var cutOffDate = new  Date('<%=sdf.format(cutOfDate)%>');
	
	
	var summaryResult = "";
	var availableTags = [];
	var poOwners=[];
	var ccUsersVar=[];

	// USED FOR SAVING DATA ON CHANGE OF GRID CELLS (AUTO SAVE)

	var forecast_cur = 0.0;
	var accrual_cur = 0.0;
	var quarterly_tar_cur = 0.0;

	function updateMemCache(e, args, tempKey) {
		
		var cell = args.cell;
		var item = args.item;
		var fixedCell = cell;
		var row = args.row;
		var poNum = 0;
		var projName = "";
		var projWBS = "";
		var subactivity = "";
		var costCenter = item["47"];

		if ($('#hideColumns').is(":checked")) {
			fixedCell = cell + numHideColumns;
		} else {
			fixedCell = cell;
		}
		
		if(cell <= PROJECT_OWNER_CELL){
			fixedCell = cell;
		}
		var itemCell = fixedCell;
		
		if(fixedCell == PO_NUMBER_CELL){
			var userAccepted = confirm("You have entered PO Number "+ args.item["8"] +". Want to continue?");
			if (!userAccepted) {
				args.item["8"]="";
				grid.invalidate();
		        grid.gotoCell(row, fixedCell, true);
		        $('#statusMessage').text("")
				.fadeOut(100);
			    return;
			}
			poNum = args.item["8"];
		}
		
		if (fixedCell == REMARK_CELL) {
			for (var i = 0; i < totalSize; i++) {
				if (data[i][31] == item[31]) {
					data[i][32] = item[itemCell];
				}
			}
		}
		
		var qtrEditing =  Math.floor((fixedCell - 12) / 3);
		
		var cellValue ;
		if(cell == BRAND_CELL){
		cellValue = item[6];
		}else{
			cellValue = item[itemCell];
		}
		var cellNum = fixedCell - 12;
		if(item[37]==true && item["11"] == "Accrual" && item[27].toString().indexOf(".") != -1){
			key = item[27];
		}else{
			key = item[34];
		}
		var aSaveData=[];
		var iCnt=0;
		var varTotal = 0.0;
		var singleBrandToMulti = false;
		if( fixedCell == REMARK_CELL){
				var aSave = (aSaveData[0] = {});
				aSave[0] = key;
				aSave[1] = cellValue;
			}else if(cell == BRAND_CELL){
				if(cellValue.toString().toLowerCase().indexOf("smart wbs")!=-1 && lastKeyPressed == 9){
					<%
					Map<String,ArrayList<String>> ccUsersJs = util.getCCUsersList(user.getSelectedCostCenter());%>
					
					var usr=0;
					var userCnt=0;
					<% 
					
					for(Map.Entry<String,ArrayList<String>> userMapDetails: ccUsersJs.entrySet()){%>
					 poOwners[userCnt] = "<%=userMapDetails.getKey()%>";
					 var d = (ccUsersVar[userCnt] = {});
					 d[0]=   poOwners[userCnt];
					 d[1] = "<%=userMapDetails.getValue()%>";
					 
					 userCnt++;
					<%}%>
					if(args.item[37] == false){
						var index = availableTags.indexOf("Smart WBS");
						if (index > -1) {
							availableTags.splice(index, 1);
						}
						m_data[0][1]=itemClicked[44];
						m_data[0][3]=itemClicked[24];
						m_data[0][2]=100.0;
						m_data[0][4]=itemClicked[2];
					 	m_data[0][5]=itemClicked[0]+'.1';
					 	m_data[0][7]=itemClicked[1];
					 	singleBrandToMulti=true;
					 	$('#multibrandEdit').show().fadeIn(100);
						displayMultibrandGrid();
						$('#back').addClass('black_overlay').fadeIn(100);
					}
				}else if(cellValue.toString().toLowerCase().indexOf("smart wbs")!=-1){
					alert('Click on "Smart WBS" to add sub projects.');
					return;
				}else{
					var aSave = (aSaveData[0] = {});
					aSave[0] = key;
					aSave[1] = cellValue;
				}
				for(var i=0;i<data.length;i++){
					var d = data[i];
					if(key== d[34] && !singleBrandToMulti && (fixedCell ==  BRAND_CELL) && ((d[26]=="New" || d[26]=="Active") )){
						d[<%=BudgetConstants.BRAND_SEARCH_FIELD%>] = args.item[<%=BudgetConstants.BRAND_FIELD%>];
						d[<%=BudgetConstants.BRAND_DUPLICATE_FIELD%>] = args.item[<%=BudgetConstants.BRAND_FIELD%>]; 
					}
				}
			}else{
				for(var i=0;i<data.length;i++){
					var d = data[i];
					
					if(key== d[34] && d[11]=="Forecast" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
						forecast_cur =  d[itemCell];
						var aSave = (aSaveData[iCnt] = {});
						aSave[0] = d[27];
						if(d[7] == 0.0){
							d[7]=100.0;
						}
						aSave[1] = parseFloat( parseFloat(d[7]) * parseFloat(cellValue) /100).toFixed(2);
						d[itemCell]=aSave[1];
							varTotal = 0.0;
							for (var j = 12; j < 24; j++) {
								if(d[j] == "" || d[j] == "undefined"){
									d[j] = 0.0;
								}
								varTotal = parseFloat(varTotal)	+ parseFloat(d[j]);
							}
							d[24]= parseFloat(varTotal);
						iCnt++;
					}else if(key== d[34] && d[11]=="Forecast" && ( fixedCell == PROJECT_NAME_CELL || fixedCell == PO_NUMBER_CELL || fixedCell == <%=BudgetConstants.PROJECT_WBS_CELL%> || fixedCell == <%=BudgetConstants.SUBACTIVITY_CELL%>	|| fixedCell == <%=BudgetConstants.VENDOR_CELL%> || fixedCell == <%=BudgetConstants.UNIT_CELL%> /*|| fixedCell == BRAND_CELL*/)){
						var aSave = (aSaveData[iCnt] = {});
						aSave[0] = d[27];
						if(fixedCell == <%=BudgetConstants.VENDOR_CELL%>){
							d[<%=BudgetConstants.VENDOR_FIELD%>] = args.item[<%=BudgetConstants.VENDOR_FIELD%>];
							aSave[1] = d[<%=BudgetConstants.VENDOR_FIELD%>];
						}else if(fixedCell == <%=BudgetConstants.SUBACTIVITY_CELL%>	){
							d[<%=BudgetConstants.SUBACTIVITY_FIELD%>] = args.item[<%=BudgetConstants.SUBACTIVITY_FIELD%>];
							aSave[1] = d[<%=BudgetConstants.SUBACTIVITY_FIELD%>];
						}else if(fixedCell == <%=BudgetConstants.PROJECT_WBS_CELL%>){
							d[<%=BudgetConstants.PROJECT_WBS_FIELD%>] = args.item[<%=BudgetConstants.PROJECT_WBS_FIELD%>];
							aSave[1] = d[<%=BudgetConstants.PROJECT_WBS_FIELD%>];
						}else if(fixedCell == PO_NUMBER_CELL){
							d[<%=BudgetConstants.PO_NUMBER_FIELD%>] = args.item[<%=BudgetConstants.PO_NUMBER_FIELD%>];
							aSave[1] = d[<%=BudgetConstants.PO_NUMBER_FIELD%>];
						}else if(fixedCell == PROJECT_NAME_CELL){
							d[<%=BudgetConstants.PROJECT_NAME_FIELD%>] = args.item[<%=BudgetConstants.PROJECT_NAME_FIELD%>];
							if(d[<%=BudgetConstants.PROJECT_NAME_FIELD%>] != 'undefined' && d[<%=BudgetConstants.PROJECT_NAME_FIELD%>].trim() != ""){
								aSave[1] = d[<%=BudgetConstants.PROJECT_NAME_FIELD%>];
							}else{
								alert("Project name can not be blank!!!");
								d[<%=BudgetConstants.PROJECT_NAME_FIELD%>] = d[<%=BudgetConstants.PROJECT_NAME_SEARCH_FIELD%>];
								return;
							}
							aSave[1] = d[<%=BudgetConstants.PROJECT_NAME_FIELD%>];
						}else if(fixedCell == <%=BudgetConstants.GMEMORI_ID_CELL%>){
							if(d[<%=BudgetConstants.GMEMORI_ID_FIELD%>].toString().indexOf(".")!=-1){
								d[<%=BudgetConstants.GMEMORI_ID_FIELD%>] = args.item[<%=BudgetConstants.GMEMORI_ID_FIELD%>] +"." +d[<%=BudgetConstants.GMEMORI_ID_FIELD%>].toString().split(".")[1]
							}else{
								d[<%=BudgetConstants.GMEMORI_ID_FIELD%>] = args.item[<%=BudgetConstants.GMEMORI_ID_FIELD%>];
							}
							aSave[1] = d[<%=BudgetConstants.GMEMORI_ID_FIELD%>];
						}else if(fixedCell == <%=BudgetConstants.UNIT_CELL%>){
							d[<%=BudgetConstants.UNIT_FIELD%>] = args.item[<%=BudgetConstants.UNIT_FIELD%>];
							aSave[1] = d[<%=BudgetConstants.UNIT_FIELD%>];
						}
						iCnt++;
					}else if(key== d[34] && d[11]=="<%=BudgetConstants.QUARTERLY_TARGET%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active" || d[26]=="Closed") && 
							((qtrEditing != '<%=qtr%>' ) || ( qtrEditing == '<%=qtr%>' && '<%=cutOfDate.after(new Date()) %>' =='true')  ))){
						d[itemCell]=parseFloat(cellValue).toFixed(2);
						varTotal = 0.0;
						for (var j = 12; j < 24; j++) {
							if(d[j] == "" || d[j] == "undefined"){
								d[j] = 0.0;
							}
							varTotal = parseFloat(varTotal)	+ parseFloat(d[j]);
						}	
						d[24]= parseFloat(varTotal);
					}
					if(key== d[34] && d[11]=="<%=BudgetConstants.QUARTERLY_TARGET%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active" || d[26]=="Closed"))){
						quarterly_tar_cur = d[itemCell];
					}
					if(key== d[34] && d[11]=="Accrual" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active"))){
						accrual_cur = d[itemCell];
					}
					if(key== d[34] && (fixedCell ==  PROJECT_NAME_CELL) && ((d[26]=="New" || d[26]=="Active") )){
						d[<%=BudgetConstants.PROJECT_NAME_SEARCH_FIELD%>] = args.item[<%=BudgetConstants.PROJECT_NAME_FIELD%>];
						d[<%=BudgetConstants.PROJECT_NAME_GID_FIELD%>] = args.item[<%=BudgetConstants.PROJECT_NAME_FIELD%>] + " :: " + d[<%=BudgetConstants.PROJECT_NAME_GID_FIELD%>].split("::")[1]; 
					}								
					if(key== d[34] && d[11]=="<%=BudgetConstants.QUARTERLY_LTS%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active"  || d[26]=="Closed") )){
						d[itemCell]= parseFloat(quarterly_tar_cur - accrual_cur);
						varTotal = 0.0;
						for (var j = 12; j < 24; j++) {
							if(d[j] == "" || d[j] == "undefined"){
								d[j] = 0.0;
							}
							varTotal = parseFloat(varTotal)	+ parseFloat(d[j]);
						}	
						d[24]= parseFloat(varTotal);
						
					}
				}
			}
			
		if(singleBrandToMulti!=true){
			if(item[37] == true && item[11]=="Forecast" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
				var mTotal =0.0;
				var iTotal =0.0;
				for(var j=0;j<aSaveData.length;j++){
					var mSave = aSaveData[j];
					if(mSave[0].toString().indexOf(".") == -1){
						mTotal = parseFloat(mSave[1]).toFixed(2);
					}else if(j < aSaveData.length-1){
						iTotal= (parseFloat(iTotal)+ parseFloat(mSave[1])).toFixed(2);
					}else if(j==aSaveData.length-1){
						mSave[1] = (parseFloat(mTotal) - parseFloat(iTotal)).toFixed(2);
						for(var i=0;i<data.length;i++){
							var d = data[i];
							if(d[27] == mSave[0]){
								d[itemCell] = mSave[1];
							}
						}
					}

				}
			}

		$.ajax({
			beforeSend: function(msg){
				 $('#statusMessage').text("Saving data...").fadeIn(200);
				 if(cellNum == '<%=BudgetConstants.CELL_PONUMBER%>'){
					$('#back').addClass('black_overlay').fadeIn(100);
					$('#loader_image').show().fadeIn(100);
				 }
			},
			url : '/AutoSaveData',
			type : 'POST',
			dataType : 'text',
			data : {
				celNum : cellNum,
				objarray : JSON.stringify(aSaveData),
				costCenter : costCenter,
				mapType : item[11]
			},
			success : function(result) {
				if(JSON.stringify(result).indexOf("<poError>")!=-1){
					alert("PO Number already exists !!!");
					window.location.reload(true);
				}
				$('#statusMessage').text("All changes saved successfully!")
						.fadeIn(200);
				$("#statusMessage");
				summaryResult = result;
				getSummaryValues();
				if(cellNum == '<%=BudgetConstants.CELL_PONUMBER%>' || (cellNum == '<%=BudgetConstants.CELL_BRAND%>' && (singleBrandToMulti || multiBrandToSingle) /*|| cellNum == '<%=BudgetConstants.CELL_PNAME%>'*/)){
					window.location.reload(true);
				}
			},
			error : function(result){
				$('#statusMessage').text("")
				.fadeIn(200);
				$('#statusMessage');
				for(var i=0;i<data.length;i++){
					var d = data[i];
					if(key== d[34] && d[11]=="Forecast" && ( fixedCell == <%=BudgetConstants.GMEMORI_ID_CELL%>)){
					d["0"] = d["27"];
				}}
				grid.invalidate();
			}
		});
		}
		singleBrandToMulti=false;
	}

	// USED FOR EXPORTING PROJECT DATA / DOWNLOAD DATA IN EXCEL FORMAT

	function onClickAsynch(){
		var num=1;
		function async(callback) {
		    var z;
		    <%
		    	HashSet hs = new HashSet();
		    	ArrayList<String> costcentreAry = new ArrayList<String>();
		    	for(int j=0; j<ccList.size();j++){
		    		costcentreAry.add(ccList.get(j).getCostCenter());
		    	}
		    	hs.addAll(costcentreAry);
		    	costcentreAry.clear();
		    	costcentreAry.addAll(hs);
		    %>
		    <%
		    	for (int i=0; i < costcentreAry.size();i++){%>
		    	CostCenterApperance(<%=costcentreAry.get(i)%>);
		    <%
		    	}
		    %>
		}

		function CostCenterApperance(i) {
		    setTimeout(function() {
		    	ServletCall(i);
		    	callback();
		    }, 3500*num);
		    num++;
		}

		function ServletCall(i){
			console.log("Downloading data...");
			var viewVal = $('#selectedUserView').val();
			var brandValue = $('#getBrand').val();
			$('#objArrayId').val('');
			$('#ccId').val(i);
			$('#viewSelected').val(viewVal);
			$('#brandSelected').val(brandValue);
		    document.getElementById('exportExcel').submit();
		}	
		async(function(){ 

		});

	}
</script>
<%-- *****************************  EXPORT BUTTON ***************************** --%>

<div width='100%' align=right>
		<button id="exportButton" class="myButton" value=""
			onclick="openDownloadPopUp();"
			style="height: 25px; letter-spacing: 1px;"
			align='right'>Export data as excel</button>
</div>
<br>
	
<%@ include file="footer.jsp"%>

<%-- *****************************  MULTIBRAND DIV ***************************** --%>

	<div id="multibrandEdit">
	
		<div id="header"
			style="width: 100%; height: 26px; background-color: #2271B0; color: white; 
			border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; font-size: 20px; 
			letter-spacing: 5px; padding-top: 8px;"	align=center>Multi-brand</div>
		
		<div id="multibrandGrid" style="width: 100%; height: 200px;"></div>
	
		<div align='center'>
			<button id="addRow" class="myButton1" value="" onclick="addNewRow();"
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
	
<%-- ***************************** EXPORT COSTCENTER DIV ***************************** --%>

	<form id="exportExcel" name="exportExcel" method="post"
		action="/download" target="myIFrm">
		<input type="hidden" name="objarray" id="objArrayId" /> <input
			type="hidden" name="costCenter" id="ccId" /> <input type="hidden"
			name="viewSelected" id="viewSelected" /> <input type="hidden"
			name="brandSelected" id="brandSelected" />
	</form>
	
	<iframe id="myIFrm" name="myIFrm" src="" style="visibility: hidden; display:none">
	
	</iframe>

	<div id="selectthebrand">
			
		<div id="header" style="width: 100%; height: 26px; background-color: #005691; 
		color: white; border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; 
		font-size: 20px; letter-spacing: 2px; padding-top: 4px;"  align = center>
			Export CostCenter  
	    </div>
	    <br>
	    <div align="center">
	     <span id="brandVal"  style="font-size:15;">Brand : <span id="selectedBrandValue"> </span>
	     	&nbsp;&nbsp; <br>
	     </span>
	     <br>
			<input type="radio" value="0" id="selectCC" name="selectCC" > 
			<span id = "selectedCCValue" style="font-size:15;">Current View(<%=(String)request.getAttribute("getCCValue")%>)</span>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="radio" value="1" id="selectCC" name="selectCC" > 
			<span style="font-size:15;">Total MA&S Cost Centers</span>
			<br><br>
			<button class="myButton" value="" onclick="exportExcelData();" style="height: 25px; letter-spacing:1px;" align= 'right'> Ok</button>&nbsp;
			<button class="myButton" value="" onclick="closepopup();" style="height: 25px; letter-spacing:1px;" align= 'right'> Cancel</button>
		</div>
	</div>

<%-- ***************************** CAUTION WINDOW DIV *****************************  --%>

	<div id="cautionWindow">
		<div id="header"
			style="width: 100%; height: 20px; background-color: red; color: white; border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; font-size: 17px; letter-spacing: 3px;"
			align=center>Caution!</div>
		<div style="font-size: 12px">Window width is not sufficient
			enough for application to be viewed. Please increase the window
			width.</div>
	</div>

<%-- ***************************** LOADER DIV *****************************  --%>

	<div id="loader_image" style="vertical-align: middle;">
            <img src="images/loading.gif" width="35px" height='35px'/>
	</div>
	
</body>
</html>