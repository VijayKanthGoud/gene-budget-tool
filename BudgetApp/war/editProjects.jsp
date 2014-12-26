<%@page import="com.gene.app.model.*"%>
<%@page import="com.gene.app.dao.DBUtil"%>
<%@page import="com.gene.app.util.BudgetConstants"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="javax.servlet.RequestDispatcher"%>

<%@ include file="header.jsp"%>


<%
	String color ="yellow";
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
%>

<link rel="stylesheet" href="SlickGrid-master/slick.grid.css"
	type="text/css" />
<link rel="stylesheet"
	href="SlickGrid-master/css/smoothness/jquery-ui-1.8.16.custom.css"
	type="text/css" />
<link rel="stylesheet" href="SlickGrid-master/examples/examples.css"
	type="text/css" />
    <html>
<body onload="getBrandTotals();getAvailableTags()">  
	<div align="center">
		<table
			style="border: 1px solid gray; background: #E3E8F3; padding: 6px; width: 100%; font-weight: normal; font-size: 14px; color: #005691; font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif; float: left;">
			<tr>
				<td style="width: 20%;" rowspan="2">
					<table class="summarytable"
						style="color: #005691; white-space: nowrap; height: 117px; width: 220px;">
						<tr>
							<td style="padding-left: 20px;"><input type="radio"
								name="selectedmode" value="planned">Planned <input
								type="radio" name="selectedmode" value="All" checked="checked">All</td>
						</tr>
						<tr>
							<td style="padding-left: 20px;"><input type="checkbox"
								id="hideColumns" name="hideColumns" value="hide" checked>Hide
								Columns</td>
						</tr>
					</table>
				</td>				
				<% UserRoleInfo userInfo = (UserRoleInfo)session.getAttribute("userInfo"); %>
				<td style="width: 50%; height: 55px; text-align: center;"><span
						style="color: #105596; font-family: 'trebuchet ms'; font-size: 22px; font-weight: bold; letter-spacing: 5px; padding-top: 8px;">
						My Projects </span><br/><br/>Cost center : <%=userInfo.getCostCenter()%></td>

				<td style="width: 20%;" rowspan="2">
					<table class="summarytable" width=100%
						style="color: #005691; white-space: nowrap; font-weight: bold;">
						<%
							BudgetSummary summary = (BudgetSummary) session.getAttribute("summary");
							Map<String, BudgetSummary> budgetMap = summary.getBudgetMap();
							BudgetSummary budgetSummary = new BudgetSummary();
							UserRoleInfo user = (UserRoleInfo) request.getAttribute("user");
							Map<String,Double> brandMap = user.getBrand();
							LOGGER.log(Level.INFO, "brandMaps received : " + brandMap);
							Object[] brands = {}; 
							if(brandMap!=null && !brandMap.isEmpty()){
								brands = brandMap.keySet().toArray();
							}
						%>
					<script>
						  var selectedValue = "";
						  var summaryResult = "";
						  var availableTags = [];
						  var poOwners=[];
						  var ccUsersVar=[];
	            		function getAvailableTags(){
		            		availableTags[0] = "Total Products(MB)";
		            		var j;
		            		<%for(int i=0;i<brands.length;i++){%>
		            			j=<%= i+1%>;
		            			availableTags[j] = '<%= brands[i]%>';
		            		<%}%>
		            	} 
						function getBrandTotals(){
							
							selectedValue = document.getElementById("brandType").value; 
							$.ajax({
								url : '/GetSummaryFromCache',
								type : 'POST',
								dataType : 'text',
								data : {costCentre: <%=userInfo.getCostCenter()%>
								},
								success : function(result) {
									summaryResult = result;
									getSummaryValues();
								}
							});
						} 
					</script> 
						
						<tr align='center'> <td colspan=2>Brand Summary &nbsp;($ in 1000's)</td>
						<td> <img alt="" src="images/refresh.png" height="25" width="25" align='left' onclick="getBrandTotals()"> </td> </tr>
						 <tr>
       					<tr>
                            <td >Select Brand:</td>
                            <td colspan="2"><select id="brandType" onchange="getBrandTotals()" style="color: #005691; font: normal 12 Arial, Helvetica, sans-serif;">
                            <%String option = "";
                            if(budgetMap!=null && !budgetMap.isEmpty()){
                            	Object[] budgets = budgetMap.keySet().toArray();
                            for(int i=0;i<budgets.length;i++){ 
                            option = budgets[i].toString();
                            if(i==1){%>
                            <option value="<%=option %>" selected><%=option %></option>
                            <%}else{ %>
                            <option value="<%=option %>"><%=option %></option>
                            <%}}} %>
                            </select></td>
                        </tr>
                                <td>Budget:</td>
                                <td><input id = "totalBudget" style="color: #005691; font: normal 12 Arial, Helvetica, sans-serif;" type=text name=type
                                       maxlength="8" size="8" value="<%=Math.round(budgetSummary.getTotalBudget() * 10.0) / 10.0%>"></td>
                        </tr>

						<tr>
                                <td>Planned:</td><td><span id = "plannedTotal"><%=Math.round(budgetSummary.getPlannedTotal() * 10.0) / 10.0%></span></td>
                        </tr>
                        <tr>
                                <td>Unplanned Total:</td><td><span id = "budgetLeftToSpend"><%=Math.round(((budgetSummary.getTotalBudget() - budgetSummary.getPlannedTotal())*10.0)/10.0) %></span></td>
                        </tr>
                        <tr>
                                <!-- td style="padding-left: 20px;">2017</td> -->
                                <td>Accrual:</td><td><span id = "accrualTotal"><%=Math.round(budgetSummary.getAccrualTotal() * 10.0) / 10.0%></span></td>
                        </tr>
                         <tr>
                               <td><span style="background: <%=color%>;color:black">Variance Total:</span></td>
                               <td><span style="background: <%=color%>;color:black"><span id = "varianceTotal"><%=Math.round(budgetSummary.getVarianceTotal() * 10.0) / 10.0%></span></span>
                               </td>
                         </tr>
					</table>
				</td>
			</tr>
			<tr style="">
				<td style="padding-left: 21.5%"><input type=text
					style="float: left; align: center; width: 140px;" id="txtSearch">
					<img src="images/search.png" height="20" width="20" align="bottom"
					style="float: left;"
					title="Search in Project name, gMemori Id, Brand and Remarks.">
				</td>
			</tr>
		</table>
	</div>
	<div id="statusMessage"></div>
	<div id="displayGrid" style="width: 100%; height: 51%;  min-height: 300px;"></div>
	<div id="multibrandEdit">
		<div id="header"
			style="width: 100%; height: 20px; background-color: #005691; color: white">&nbsp;Multi-brand:
		</div>
		<div id="multibrandGrid" style="width: 100%; height: 230px;"></div>
		<div align='center'>
			<button class="myButton" value="" onclick="deleteSelectedProjects();">
				DELETE SELECTED</button>
			<button id="saveClose" class="myButton" value=""
				onclick="saveAndClose();">SAVE AND CLOSE</button>
			<button class="myButton" value="" onclick="closeWithoutSave();">
				CANCEL</button>
		</div>
	</div>
	<div id="back">	</div>  
  
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
<script>
	
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
	var radioString = "All";
	var totalSize = 0;
	var numHideColumns = <%=BudgetConstants.NUMBER_OF_HDN_COLS%>;
	var columnNames = [ "Status", "Project Name", "Brand", "$ in 1000's", "gMemori Id", "Project Owner",
	        			"Project WBS", "SubActivity", "Allocation %", "PO Number", "Vendor", 
	        			"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
	        			"DEC", "Total", "Remark" ];
	var noOfNew = 0;
	var noOfActive = 0;
	var noOfClosed = 0;
	var newExist=false;
	var activeExist=false;
	var closedExist=false;
	
	// Columns displayed when hide columns is unchecked
	var columns = [ 
		{ id : 1, name : columnNames[0], field : 30, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[1], field : 2, width : 150, editor : Slick.Editors.Text},
		{ id : 3, name : columnNames[2], field : 6, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Auto},
		{ id : 4, name : columnNames[3], field : 11, width : 110, formatter : Slick.Formatters.HyperLink,groupTotalsFormatter : sumTotalsFormatter},
		{ id : 5, name : columnNames[4], field : 0, width : 90, editor : Slick.Editors.GMemoriText },
		{ id : 6, name : columnNames[5], field : 1, width : 90},
		{ id : 7, name : columnNames[6], field : 3, width : 90, editor : Slick.Editors.Text},
		{ id : 8, name : columnNames[7], field : 5, width : 90, editor : Slick.Editors.Text},
		{ id : 9, name : columnNames[8], field : 7, width : 90, editor : Slick.Editors.Text},
		{ id : 10, name : columnNames[9], field : 8, width : 90, editor : Slick.Editors.PONumberText},
		{ id : 11, name : columnNames[10], field : 10, width : 90, editor : Slick.Editors.Text},
		{ id : 12, name : columnNames[11], field : 12, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 13, name : columnNames[12], field : 13, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[13], field : 14, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[14], field : 15, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[15], field : 16, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[16], field : 17, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[17], field : 18, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[18], field : 19, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[19], field : 20, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[20], field : 21, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[21], field : 22, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[22], field : 23, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[23], field : 24, width : 90, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[24], field : 25, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	} ];

	//Columns displayed when hide columns is checked
	var hidecolumns = [ 
		{ id : 1, name : columnNames[0], field : 30, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[1], field : 2, width : 150, editor : Slick.Editors.Text},
		{ id : 3, name : columnNames[2], field : 6, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Auto},
		{ id : 4, name : columnNames[3], field : 11, width : 110, formatter : Slick.Formatters.HyperLink,groupTotalsFormatter : sumTotalsFormatter},
		{ id : 5, name : columnNames[4], field : 0, width : 90, editor : Slick.Editors.GMemoriText },
		{ id : 6, name : columnNames[5], field : 1, width : 90},
		{ id : 12, name : columnNames[11], field : 12, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 13, name : columnNames[12], field : 13, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[13], field : 14, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[14], field : 15, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[15], field : 16, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[16], field : 17, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[17], field : 18, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[18], field : 19, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[19], field : 20, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[20], field : 21, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[21], field : 22, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[22], field : 23, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[23], field : 24, width : 90, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[24], field : 25, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	}]
	var searchString = "";
	
	// Grouping columns acording to status(New, Active, Closed)
	function groupByStatus() {
		dataView
				.setGrouping([{
					getter : 26,
					formatter : function(g) {
						noOfNew = 0;
						noOfActive = 0;
						noOfClosed = 0;
						
						var newArr = [];
						var ActiveArr = [];
						var ClosedArr = [];
						var uniqueNames = [];
						for(var cnt=0; cnt<data.length; cnt++){
							if(data[cnt][26].indexOf("New") != -1 && data[cnt][0].toString().trim() != ""  && data[cnt][34]!="New projects" ){
								newArr.push(data[cnt][34]);
							}
							if(data[cnt][26].indexOf("Active") != -1 && data[cnt][0].toString().trim() != ""  && data[cnt][34]!="New projects"){
								ActiveArr.push(data[cnt][34]);
							}
							if(data[cnt][26].indexOf("Closed") != -1 && data[cnt][0].toString().trim() != ""  && data[cnt][34]!="New projects"){
								ClosedArr.push(data[cnt][34]);
							}
						}
						
						
						$.each(newArr, function(i, el){
						    if($.inArray(el, uniqueNames) === -1) uniqueNames.push(el);
						});
						noOfNew = uniqueNames.length;
						uniqueNames = [];
						$.each(ActiveArr, function(i, el){
						    if($.inArray(el, uniqueNames) === -1) uniqueNames.push(el);
						});
						noOfActive = uniqueNames.length;
						uniqueNames = [];
						$.each(ClosedArr, function(i, el){
						    if($.inArray(el, uniqueNames) === -1) uniqueNames.push(el);
						});
						
						noOfClosed = uniqueNames.length;
						
						if (g.value == "Total") {
							return "<span style='color:green'>"
								+ g.value + "</span>";
						} 
						else if (g.value == "New"){
							return " " + g.value
							+ "<span style='color:green'>("
							+ noOfNew + " items)</span>" 
							+ "&nbsp;&nbsp;<input type='button' style='font-size: 12px; height: 25px; width: 120px; background:#005691; color:#FFFFFF' value='Create Projects' id='crtNewProjBtn'/>";
						} 
						else if (g.value == "Active"){
							return " " + g.value
							+ "  <span style='color:green'>("
							+     noOfActive + " items)</span>";
						}
						else if (g.value == "Closed"){
							return " " + g.value
							+ "<span style='color:green'>("
							+ noOfClosed + " items)</span>";
						}
					},
					aggregators : [ new Slick.Data.Aggregators.Sum("12"),
							new Slick.Data.Aggregators.Sum("13"),
							new Slick.Data.Aggregators.Sum("14"),
							new Slick.Data.Aggregators.Sum("15"),
							new Slick.Data.Aggregators.Sum("16"),
							new Slick.Data.Aggregators.Sum("17"),
							new Slick.Data.Aggregators.Sum("18"),
							new Slick.Data.Aggregators.Sum("19"),
							new Slick.Data.Aggregators.Sum("20"),
							new Slick.Data.Aggregators.Sum("21"),
							new Slick.Data.Aggregators.Sum("22"),
							new Slick.Data.Aggregators.Sum("23"),
							new Slick.Data.Aggregators.Sum("24"), ],
					aggregateCollapsed : true,
					lazyTotalsCalculation : true
				},
			    {
				      getter: 34,
				      formatter :function (g) {
				        return g.value;
				      },
				      lazyTotalsCalculation: true
				    }
				
				
				]);
		 if(newExist == false){
			dataView.collapseGroup("New");
		}
		 dataView.collapseGroup("Active");
		 dataView.collapseGroup("Closed");
	}

	

	var options = {
		editable : true,
		enableAddRow : true,
		enableCellNavigation : true,
		asyncEditorLoading : false,
		autoEdit : false,
		frozenColumn : 3,
		enableColumnReorder: false
	};


	// Display total for active new and closed projects (roll up total)
	function sumTotalsFormatter(totals, columnDef) {
		var val = totals.sum && totals.sum[columnDef.field];
		if(columnDef.field==11 && totals['group']['value'].toLowerCase() != 'total'){
			return "<span style='color:rgb(168, 39, 241)'>" + "Totals (Planned)"
			+ "</span> ";
		}
		if (val != null
				&& totals['group']['value'].toLowerCase() != 'Total'
						.toLowerCase()
						) {
			return "<span style='color:rgb(168, 39, 241)'>" 
					+ ((Math.round(parseFloat(val) * 100) / 100)).toFixed(2)
					+ "</span> ";
		}
		return "";
	}

	// Filter data acording to search field
	function searchProject(item) {
		var status = true;
		if (item[33] != "New") {
			status = false;
		}
		
		if (((searchString != "" && item[27].toLowerCase().indexOf(
				searchString.toLowerCase()) == -1)
				&& (searchString != "" && item[28].toLowerCase().indexOf(
						searchString.toLowerCase()) == -1)
				&& (searchString != "" && item[29].toLowerCase().indexOf(
						searchString.toLowerCase()) == -1)
				&& (searchString != "" && item[32].toLowerCase().indexOf(
						searchString.toLowerCase()) == -1)
				&& (searchString != "" && item[30].toLowerCase().indexOf(
						searchString.toLowerCase()) == -1) && item[26] != "Total")
				|| (radioString != "All" && item[40] !="undefined" && item[40].toLowerCase().indexOf(
						radioString.toLowerCase()) == -1)) {
			return false;
		}

		if (item.parent != null) {
			var parent = data[item.parent];
			while (parent) {
				if (parent._collapsed
						|| ((searchString != "" && parent[27].toLowerCase()
								.indexOf(searchString.toLowerCase()) == -1)
								&& (searchString != "" && parent[28]
										.toLowerCase().indexOf(
												searchString.toLowerCase()) == -1)
								&& (searchString != "" && parent[29]
										.toLowerCase().indexOf(
												searchString.toLowerCase()) == -1)
								&& (searchString != "" && parent[32]
										.toLowerCase().indexOf(
												searchString.toLowerCase()) == -1)
								&& (searchString != "" && parent[30]
										.toLowerCase().indexOf(
												searchString.toLowerCase()) == -1) && (parent[26] != "Total"))
						|| (radioString != "All" && item[11]!="undefined" && item[11].toLowerCase()
								.indexOf(radioString.toLowerCase()) == -1)) {
					return false;
				}
				parent = data[parent.parent];
			}
		}
		return status;
	}



    // Method called to store changed value in to memcache
	function updateMemCache(e, args, tempKey) {
		$('#statusMessage').text("Saving data...").fadeIn(200);
		var cell = args.cell;
		var item = args.item;
		var fixedCell = cell;
		var row = args.row;
		var poNum = 0;
		var projName = "";
		var projWBS = "";
		var subactivity = "";
		
		if ($('#hideColumns').is(":checked")) {
			fixedCell = cell + numHideColumns;
		} else {
			fixedCell = cell;
		}
		
		if(cell <= <%=BudgetConstants.$_IN_THOUSAND_CELL%>){
			fixedCell = cell;
		}
		var itemCell = fixedCell + 1;
		
		if(fixedCell == <%=BudgetConstants.PO_NUMBER_CELL%>){
			var userAccepted = confirm("You have entered PO Number "+ args.item["8"] +". Want to continue?");
			if (!userAccepted) {
				data[row][fixedCell]="";
				grid.invalidate();
		        grid.gotoCell(row, fixedCell, true);
			    return;
			}
			poNum = args.item["8"];
		}
		
		if (fixedCell == <%=BudgetConstants.REMARK_CELL%>) {
			for (var i = 0; i < totalSize; i++) {
				if (data[i][31] == item[31]) {
					data[i][32] = item[itemCell];
				}
			}
		}
		
		var cellValue = item[itemCell];
		var cellNum = fixedCell - 11;
		key = item[34];
		var aSaveData=[];
		var iCnt=0;
		var varTotal = 0.0;
		if( fixedCell == <%=BudgetConstants.REMARK_CELL%>){
 			var aSave = (aSaveData[0] = {});
 			aSave[0] = key;
	 		aSave[1] = cellValue;
 		}else{
	 		for(var i=0;i<data.length;i++){
				var d = data[i];
				if(key== d[34] && fixedCell > 11 && fixedCell< 24 && item[11]=='Accrual'){
					if(d[11]=="Accrual"){
						d[itemCell]=parseFloat( parseFloat(d[41]) * parseFloat(cellValue) /100).toFixed(2);
					}else if(d[11]=="Variance"){
						if( item[43]=='undefined' ||item[43]=="" ){
							item[43]=0.0;
						}
						d[itemCell] = parseFloat(d[itemCell]) +  parseFloat(item[43]) - parseFloat(cellValue);
					}
					if(item[37]== false){
						varTotal = 0.0;
						for (var j = 12; j < 24; j++) {
							if(d[j] == "" || d[j] == "undefined"){
								d[j] = 0.0;
							}
							varTotal = parseFloat(varTotal)
										+ parseFloat(d[j]);
						}
						
						d[24]= parseFloat(varTotal);
					}
				}
		 		if(key== d[34] && d[11]=="Planned" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
			 		var aSave = (aSaveData[iCnt] = {});
			 		aSave[0] = d[27];
			 		if(d[7] == 0.0){
					 	d[7]=100.0;
			 		}
			 		if(item[11]=='Accrual'){
						d[itemCell]=parseFloat(cellValue).toFixed(2);
					}
			 		aSave[1] = parseFloat( parseFloat(d[7]) * parseFloat(cellValue) /100).toFixed(2);
			 		d[itemCell]=aSave[1];
			 		if(item[37]== false){
			 			varTotal = 0.0;
						for (var j = 12; j < 24; j++) {
							if(d[j] == "" || d[j] == "undefined"){
								d[j] = 0.0;
							}
							varTotal = parseFloat(varTotal)	+ parseFloat(d[j]);
						}
						d[24]= parseFloat(varTotal);
			 		}
			 		iCnt++;
		 		}else if(key== d[34] && d[11]=="Planned" && ( fixedCell == <%=BudgetConstants.PROJECT_NAME_CELL%> || fixedCell == <%=BudgetConstants.PO_NUMBER_CELL%> || fixedCell == <%=BudgetConstants.PROJECT_WBS_CELL%> || fixedCell == <%=BudgetConstants.SUBACTIVITY_CELL%>	|| fixedCell == <%=BudgetConstants.VENDOR_CELL%>)){
			 		var aSave = (aSaveData[iCnt] = {});
			 		aSave[0] = d[27];
			 		if(fixedCell == <%=BudgetConstants.VENDOR_CELL%>){
			 			d[fixedCell] = args.item[fixedCell];
			 			aSave[1] = d[fixedCell];
			 		}else if(fixedCell == <%=BudgetConstants.SUBACTIVITY_CELL%>	){
			 			d[fixedCell - 2] = args.item[fixedCell - 2];
			 			aSave[1] = d[fixedCell - 2];
			 		}else if(fixedCell == <%=BudgetConstants.PROJECT_WBS_CELL%>){
			 			d[fixedCell - 3] = args.item[fixedCell - 3];
			 			aSave[1] = d[fixedCell - 3];
			 		}else if(fixedCell == <%=BudgetConstants.PO_NUMBER_CELL%>){
			 			d[fixedCell - 1] = args.item[fixedCell - 1];
			 			aSave[1] = d[fixedCell - 1];
			 		}else if(fixedCell == <%=BudgetConstants.PROJECT_NAME_CELL%>){
			 			d[itemCell] = args.item[itemCell];
			 			aSave[1] = d[itemCell];
			 		}
			 		iCnt++;
		 		}else if(key== d[34] && d[11]=="Benchmark" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && d[26]=="New"){
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
			}
 		}
		
		$.ajax({
			url : '/AutoSaveData',
			type : 'POST',
			dataType : 'text',
			data : {
				celNum : cellNum,
				objarray : JSON.stringify(aSaveData),
				mapType : item[11]
			},
			success : function(result) {
				$('#statusMessage').text("All changes saved successfully!")
						.fadeIn(200);
				$("#statusMessage");
				summaryResult = result;
				getSummaryValues();
				if(cellNum == '-2' || cellNum == '-10'){
					window.location.reload(true);
				}
			}
		});
	
	}

	function getSummaryValues(){
		//alert(selectedValue+"::::"+summaryResult);
		var obj = $.parseJSON(summaryResult);
		//alert("obj"+JSON.stringify(obj));
		var value;
		var varianceTotal;
		if(obj==null){
			getBrandTotals();
		}else{
			selectedValue = document.getElementById("brandType").value;
			for(var key in obj.budgetMap){
				//alert("value"+key);
				if(key==selectedValue){
					//alert("val = "+selectedValue);
					value = obj.budgetMap[key];
					$('#totalBudget').val((value.totalBudget).toFixed(2));
					$('#plannedTotal').text((value.plannedTotal).toFixed(2));
					$('#budgetLeftToSpend').text((value.totalBudget).toFixed(2) - (value.plannedTotal).toFixed(2));
					$('#accrualTotal').text((value.accrualTotal).toFixed(2));
					$('#varianceTotal').text((value.varianceTotal).toFixed(2));
					if((value.varianceTotal).toFixed(2)/(value.totalBudget).toFixed(2) *100 < 5){
						<% color= "yellow"; %>
					}else{
						<% color= "#00FFFF" ;%>
					}
				}
			}
		}
	}
	
	$(function() {
		var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
			groupItemMetadataProvider : groupItemMetadataProvider,
			inlineFilters : true
		});

		var indent = 0;
		var parents = [];
		
		<% 	if(gtfReports == null || gtfReports.isEmpty()) { %>
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
		
		var jsId = -1;
		var dLength= data.length;
		// prepare the data
		<%
		String requestor = "";
		for (int i = 0; i < gtfReports.size(); i++) {
			boolean isFirst = true;
			for (int count = 0; count < 4; count++) {%>
				var d = (data[++jsId + dLength] = {});
			 	var parent;
			 	
    	   		d["id"] = "id_" + (parseInt(jsId) + parseInt(dLength));
    	    	d["indent"] = indent;
    	    	d["parent"] = parent;
    	    	d[0]=" ";
    	    	<%GtfReport gReport = gtfReports.get(i);%>
    	 		d[25]=" ";
       	 		d[26]="<%=gReport.getStatus()%>";
       	 		var gmemoriID = "<%=gReport.getgMemoryId()%>";
        		d[27]=gmemoriID;
        		d[28]="<%=gReport.getBrand()%>";
        		d[29]="<%=gReport.getProjectName()%>";
        		d[30]=" ";	
        		d[31]="<%=gReport.getId()%>";
        		d[32]="<%=gReport.getRemarks()%>";
        		d[33]="New";
        		d[34]=gmemoriID;
        		d[35]=" ";
        		d[37]=<%=gReport.getMultiBrand()%>;
        		d[38]="<%=gReport.getCreateDate()%>";
        		d[39]="<%=gReport.getYear()%>";
				if(gmemoriID.indexOf(".") > -1){
					d[34]=gmemoriID.split(".")[0];
				}
        		<%if(isFirst){
    				isFirst = false;
    				requestor = gReport.getRequestor();
    				if(requestor.contains(":")){
    					requestor = requestor.substring(0,requestor.indexOf(":"));
    				}
    				%>    
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
    				d[11]="Planned";
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
    				if(<%=gReport.getMultiBrand()%> == true){
    					d[24]="<%=new DecimalFormat("#.##").format(gtfReports.get(i).getPlannedMap().get("TOTAL"))%>";
    				}else{
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
    				}
    				d[25]="<%=gtfReports.get(i).getRemarks()%>";
  				<%} else{%>  
  				for(var cnt=1;cnt<11;cnt++){
  						d[cnt]=" "; 
  					}
   				if(jsId % 4 == 1){
   				d[41]="<%=gtfReports.get(i).getPercent_Allocation()%>";
   				if(gmemoriID.indexOf(".") == -1 ){
   				d[11]="Benchmark";
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
				d[11]="Accrual";
				d[41]="<%=gtfReports.get(i).getPercent_Allocation()%>";
				if(d[26]!="New" && gmemoriID.indexOf(".") == -1 ){
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
    					d[33]=" ";
    					}
   
   				} if(jsId % 4 == 3){
				d[11]="Variance";
				d[41]="<%=gtfReports.get(i).getPercent_Allocation()%>";
				if(d[26]!="New" && gmemoriID.indexOf(".") == -1 ){
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
    						d[33]=" ";
    						}
   				}
    
    				<%} %>
    				d[40] = d[11];
    				
    				<%
				}
			}%>
			if(closedExist ==false || activeExist ==false){
				dummyACProjects();
			}
			totalSize=data.length;
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
			    	d[11] = "Planned";
			        break;
			    case 1:
			    	d[11] = "Benchmark";
			        break;
			    case 2:
			    	d[11] = "Accrual";
			        break;
			    case 3:
			    	d[11] = "Variance";
			        break;
			    default:
			    	d[11] = "Planned";
		        	break;
			}
				d[40] = d[11];
			for (var j = 0; j < totalSize ; j++) {
				if( d[11]==data[j][11] && data[j][0]!= 'undefined' && data[j][27] != "" ){
				if((data[j][37] == false || data[j][11] != "Planned") && data[j][0].indexOf(".") == -1 ){
					d[12] = parseFloat(d[12]) + parseFloat(data[j][12]);
					d[13] = parseFloat(d[13]) + parseFloat(data[j][13]);
					d[14] = parseFloat(d[14]) + parseFloat(data[j][14]);
					d[15] = parseFloat(d[15]) + parseFloat(data[j][15]);
					d[16] = parseFloat(d[16]) + parseFloat(data[j][16]);
					d[17] = parseFloat(d[17]) + parseFloat(data[j][17]);
					d[18] = parseFloat(d[18]) + parseFloat(data[j][18]);
					d[19] = parseFloat(d[19]) + parseFloat(data[j][19]);
					d[20] = parseFloat(d[20]) + parseFloat(data[j][20]);
					d[21] = parseFloat(d[21]) + parseFloat(data[j][21]);
					d[22] = parseFloat(d[22]) + parseFloat(data[j][22]);
					d[23] = parseFloat(d[23]) + parseFloat(data[j][23]);
					d[24] = parseFloat(d[24]) + parseFloat(data[j][24]);
					}
				else if(data[j][0].indexOf(".") != -1){
					d[12] = parseFloat(d[12]) + parseFloat(data[j][12]);
					d[13] = parseFloat(d[13]) + parseFloat(data[j][13]);
					d[14] = parseFloat(d[14]) + parseFloat(data[j][14]);
					d[15] = parseFloat(d[15]) + parseFloat(data[j][15]);
					d[16] = parseFloat(d[16]) + parseFloat(data[j][16]);
					d[17] = parseFloat(d[17]) + parseFloat(data[j][17]);
					d[18] = parseFloat(d[18]) + parseFloat(data[j][18]);
					d[19] = parseFloat(d[19]) + parseFloat(data[j][19]);
					d[20] = parseFloat(d[20]) + parseFloat(data[j][20]);
					d[21] = parseFloat(d[21]) + parseFloat(data[j][21]);
					d[22] = parseFloat(d[22]) + parseFloat(data[j][22]);
					d[23] = parseFloat(d[23]) + parseFloat(data[j][23]);
					d[24] = parseFloat(d[24]) + parseFloat(data[j][24]);
					}
				}
			}

			for (var j = 12; j < 25; j++) {
				d[j] = d[j].toFixed(2);
			}
			d[25] = "";
			d[26] = "Total";
			d[27] = " ";
			d[28] = " ";
			d[29] = " ";
			d[30] = " ";
			d[31] = " ";
			d[32] = " ";
			d[33] = "New";
			d[34] = " ";
			d[35]= " ";
			d[36]= " ";
			d[37]= " ";
			d[38]= " ";
			d[39]= " ";
			d[0]=" ";

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
		
		// initialize the grid
		grid = new Slick.Grid("#displayGrid", dataView, hidecolumns, options);
		//register the group item metadata provider to add expand/collapse group handlers
		grid.registerPlugin(groupItemMetadataProvider);
		grid.setSelectionModel(new Slick.CellSelectionModel());

		grid.onCellChange
				.subscribe(function(e, args) {
					var isValidBrand =false;
					// Code for brand column(dropdown and validation)
					if(args.item["34"]=="New projects" && args.cell == <%=BudgetConstants.BRAND_CELL%> ){
						for(var i=0;i< availableTags.length;i++){
							if(availableTags[i].toString().trim().toLowerCase()===args.item[6].toString().trim().toLowerCase()){
								args.item[6]=availableTags[i].toString();
								isValidBrand = true;
								grid.invalidate();
								break;
							}
						}
						if(isValidBrand == false){
							args.item[6]="";
							alert("Enter a valid brand.");
							grid.invalidate();
						}
					}
					var item = args.item;
					var tempKey = item[27];
					
					var cell = args.cell;
					var row = args.row;
					var dataLength = 0;
					if(item[37]=='undefined' || item[37]==false){
						var temp = 0;
						for (var j = 0; j < data.length - 1; j++) {
							if (data[j]["id"] == args.item.id) {
								temp = j;
								break;
							}
						}
						data[temp][24] = 0.0;
						for (var j = 12; j < 24; j++) {
							if(data[temp][j] == "" || data[temp][j] == "undefined"){
								data[temp][j] = 0.0;
							}
							data[temp][24] = parseFloat(data[temp][24])
										+ parseFloat(data[temp][j]);
						}
					}
						
					grid.invalidate();
			
					if(args.item["34"] != "New projects"){
						updateMemCache(e, args, tempKey);
						var fixedCell = cell;
						
						if ($('#hideColumns').is(":checked")) {
							fixedCell = cell + numHideColumns;
						} else {
							fixedCell = cell;
						}
						var itemCell = fixedCell + 1;
						for(var counter = 0; counter<data.length; counter++ ){
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
						if(fixedCell >=  <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
							for (var j = 0; j < data.length ; j++) {
								if(data[j][26] != 'Total' && data[j][0] != 'undefined' && data[j]["34"] != "New projects"){
									if( data[j][11] == "Planned"){
										if(data[j][37] == false && data[j][27].toString().indexOf(".") == -1){
											verPlannedTotal= parseFloat(verPlannedTotal) + parseFloat(data[j][itemCell]);
											verPlanned= parseFloat(verPlanned) + parseFloat(data[j][24]);
										}else if(data[j][37] == true && data[j][27].toString().indexOf(".") != -1){
											verPlannedTotal= parseFloat(verPlannedTotal) + parseFloat(data[j][itemCell]);
											verPlanned= parseFloat(verPlanned) + parseFloat(data[j][24]);
										}
									}				
									if(data[j][11]=="Benchmark"  && data[j][27].toString().indexOf(".") ==-1){
										verBenchmarkTotal= parseFloat(verBenchmarkTotal) + parseFloat(data[j][itemCell]);
										verBenchmark= parseFloat(verBenchmark) + parseFloat(data[j][24]);
									}
									if(data[j][11]=="Accrual" && data[j][27].toString().indexOf(".") ==-1 ){
										verAccrualTotal= parseFloat(verAccrualTotal) + parseFloat(data[j][itemCell]);
										verAccrual= parseFloat(verAccrual) + parseFloat(data[j][24]);
									}
									if(data[j][11]=="Variance" && data[j][27].toString().indexOf(".") ==-1 ){
										verVarianceTotal= parseFloat(verVarianceTotal) + parseFloat(data[j][itemCell]);
										verVariance= parseFloat(verVariance) + parseFloat(data[j][24]);
									}
								}
							}
							data[data.length - 4][itemCell]=verPlannedTotal;
							data[data.length - 3][itemCell]=verBenchmarkTotal;
							data[data.length - 2][itemCell]=verAccrualTotal;
							data[data.length - 1][itemCell]=verVarianceTotal;
							data[data.length - 4][24]=verPlanned;
							data[data.length - 3][24]=verBenchmark;
							data[data.length - 2][24]=verAccrual;
							data[data.length - 1][24]=verVariance;
						
						}
						grid.invalidate();
						dataView.refresh();
					}
		});

		grid.onClick.subscribe(function(e, args) {
				itemClicked = dataView.getItem(args.row);
				
				if(args.cell == <%=BudgetConstants.BRAND_CELL%> && itemClicked[6].toLowerCase().indexOf("mb")!=-1){
					
					<%
					MemcacheService cacheCC = MemcacheServiceFactory.getMemcacheService();
					Map<String,ArrayList<String>> ccUsers = util.getCCUsersList(user.getCostCenter());%>
					// multi brand click
					
					var usr=0;
					var userCnt=0;
					<% 
					
					Set<String> userList = ccUsers.keySet();
					for(Map.Entry<String,ArrayList<String>> userMapDetails: ccUsers.entrySet()){%>
					 poOwners[userCnt] = "<%=userMapDetails.getKey()%>";
					 var d = (ccUsersVar[userCnt] = {});
					 d[0]=   poOwners[userCnt];
					 d[1] = "<%=userMapDetails.getValue()%>";
					 
					 userCnt++;
					<%}%>
					
					var index = availableTags.indexOf("Total Products(MB)");
					if (index > -1) {
						availableTags.splice(index, 1);
					}
					if(itemClicked[34]!="New projects"){
						
						// Start : For Multibrand projects on click of brand (with mb) display pop-up conatining sub-projects
						var multiBrandCnt = 0 ;	
						<% 
						GtfReport pGtfReport = new GtfReport();
						
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
							}
						<%}%>
						
						$('#multibrandEdit').show().fadeIn(100);
						displayMultibrandGrid();
						$('#back').addClass('black_overlay').fadeIn(100);
						// End : For Multibrand projects on click of brand (with mb) display pop-up conatining sub-projects
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
							if(itemClicked[0]=='' || itemClicked[0]=='undefined'){
								error=error+3;
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
				    			case 3:
				    				errStrng="gMemoriID can not be blank."
				        			break;
				    			case 4:
				    				errStrng="gMemoriID or Project name can not be blank."
				        			break;
				    			case 5:
				    				errStrng="Project Owner can not be blank."
				        			break;
				    			case 9:
				    				errStrng="gMemoriID or Project name or Project Owner can not be blank."
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
						}else{
							alert(errStrng);
						}
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
		
		// Handeler for Create New Project button
		$(document).on('click', '#crtNewProjBtn',
			    function() {
					createIntProjects();
			    }
		);
		
		
		function createNewProjects(){
			$('#displayGrid').show();
			$('#topCrtNewProjBtn').hide();
			$('#noData').hide();
			var length= data.length;
			var item ={id:"id_"+length+1,indent:0,0:"",1:"<%=userInfo.getUserName()%>",2:"project_name",3:" ",4:" ",5:"sub_activity",6:" ",7:"100.0",8:"",9:"",10:""
				,11:"Planned",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:" ",33:"New",34:"New projects",35:"NewProjects",37:false,38:"",39:"",40:"Planned"};
			dataView.insertItem(0,item);
		if(addsave ==0){
		    var saveClose ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"Save",7:"",8:"",9:"",10:""
						,11:"Cancel",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
							,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
								,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Planned"};
			var item2 ={id:"id_"+length+6,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
						,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
							,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
								,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Planned"};
			var item3 ={id:"id_"+length+3,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
						,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
							,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
								,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Planned"};
			var item4 ={id:"id_"+length+4,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
					,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Closed",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Planned"};
			var item5 ={id:"id_"+length+5,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
				,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Active",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Planned"};
			dataView.insertItem(1,item3);
		    dataView.insertItem(2,saveClose);
		    dataView.insertItem(3,item2);
		    dataView.insertItem(4,item5);
		    dataView.insertItem(5,item4);
		}
		    addsave=addsave+1;
		    dataView.refresh(); 
		    data=dataView.getItems();
		    activeExist=true;
		    closedExist=true;
		    
		}
		
		// Create a new project
		function createIntProjects(){
			if(newExist == false){
				dataView.deleteItem("id_0");
				newExist = true;
				dataView.expandGroup("New");
			}
			$('#displayGrid').show();
			$('#topCrtNewProjBtn').hide();
			$('#noData').hide();
			var length= data.length;
			var item ={id:"id_"+length+1,indent:0,0:"",1:"<%=userInfo.getUserName()%>",2:"project_name",3:" ",4:" ",5:"sub_activity",6:" ",7:"100.0",8:"",9:"",10:""
				,11:"Planned",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:" ",33:"New",34:"New projects",35:"NewProjects",37:false,38:"",39:"",40:"Planned"};
			dataView.insertItem(0,item);
			if(addsave ==0){
			    var saveClose ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"Save",7:"",8:"",9:"",10:""
							,11:"Cancel",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
								,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
									,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Planned"};
				var item2 ={id:"id_"+length+6,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
							,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
								,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
									,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Planned"};
				var item3 ={id:"id_"+length+3,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
							,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
								,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
									,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Planned"};
			
				dataView.insertItem(1,item3);
			    dataView.insertItem(2,saveClose);
			    dataView.insertItem(3,item2);
			}
		    addsave=addsave+1;
		    dataView.refresh(); 
		    data=dataView.getItems();
		}
		
		// inserts dummy new projects
		function dummyNewProjects(){
			var length= data.length;
			var item ={id:"id_"+length,indent:0,0:"",1:"",2:"",3:" ",4:" ",5:"",6:" ",7:"",8:"",9:"",10:""
				,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:" ",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Planned"};
			dataView.insertItem(0,item);
		    dataView.refresh(); 
		    data=dataView.getItems();
		}
		
		// inserts dummy active or closed projects
		function dummyACProjects(){
			var length= data.length;
			var iPlace=length-1;
			var item4 ={id:"id_"+length+1,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
					,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Closed",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Planned"};
			var item5 ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
				,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Active",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Planned"};
			if(activeExist==false){
				data[++iPlace] = item5;
			}
			if(closedExist==false){
				data[++iPlace] = item4;
			}
		}
		
		// Handeler for click on submit and cancel button under new project creation
		$(document).on('click', '#submitProjBtn',
		    function() {
			 	submitProjects();
		    }
		);
		
		$(document).on('click', '#cnclProjBtn',
			function() {
				cancelProjects();
			}
		);

		function cancelProjects(){
			var result = confirm("Are you sure you want to cancel the projects you created?");
			if(result){
			window.location.reload(true);
			}else{
				return;
			}
		}
		
		function submitProjects(){
			var errStr = 0;
			var storeData=[];
			var flag = false;
			for(var i=0;i<addsave;i++){
	
				if( data[i][0] == 'undefined' || data[i][0].toString().trim() ==""){
					errStr += 1;
				}
				if( data[i][2] == 'undefined' || data[i][2].toString().trim() ==""){
					errStr += 2;
				}
				if( data[i][6] == 'undefined' || data[i][6].toString().trim() ==""){
					errStr += 4;	
				}
				if(data[i][6].toString().toLowerCase().indexOf("mb")!=-1 && data[i][37] == false){
					flag=true;
					break;
				}
				switch(errStr) {
				case 0:
			        break;
			    case 1:
			    	alert('"gMemori ID" can not be blank.');
			        break;
			    case 2:
			    	alert('"Project name" can not be blank.');
			    	break;
			    case 3:
			    	alert('"Project name" and "gMemori ID" can not be blank.');
			        break;
			    case 4:
			    	alert('"Brand" can not be blank.');
			        break;
			    case 5:
			    	alert('"Brand" and "gMemori Id" can not be blank.');
			        break;
			    case 6:
			    	alert('"Project name" and "Brand" can not be blank.');
			        break;
			    case 7:
			    	alert('"Project name", "Brand" and "Gmemori ID" can not be blank.');
			        break;
				}
				storeData[i]=data[i];
			}
			if(flag == true){
				alert("Please add sub-projects to your multibrand project: "+ data[i][2]);
				return;
			}
			if(errStr == 0){
				 $.ajax({
					url : '/storereport',
					type : 'POST',
					dataType : 'json',
					data : {objarray: JSON.stringify(storeData) },
					success : function(result) {
						alert('Data saved successfully');
						storeData=[];
						window.location.reload(true);
					},
					error: function() {
			            alert('gMemori Id exists. Try Different gMemori Id.');
			        }
				});  
			}
		
		}
		
		/* 	// delete cell data on press of delete button
		grid.onKeyDown.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row - 1;
			var fixedCell = cell;
			if (e.which == 46) {
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
		}); */
		
		// make the current and future month cells editable
		grid.onBeforeEditCell
				.subscribe(function(e, args) {
			var monthArray = ["JAN", "FEB","MAR","APR","MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV","DEC"];
			var cell = args.cell;
			var row = args.row;
			var cols = grid.getColumns();
	
			var fixedCell = cell;
			if ($('#hideColumns').is(":checked")) {
				fixedCell = cell + numHideColumns;
			} else {
				fixedCell = cell;
			}
			if(args.item["34"]!="New projects" ){
				if(args.item["11"] == "Accrual" && args.item["26"]=="Active" && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
					args.item["43"] = args.item[fixedCell];
					grid.invalidate();
				}
				if(args.item["27"].toString().indexOf(".") != -1){
					return false;
				}
				
				if (args.item["11"] == "Planned"
					&& cols[cell].name == "PO Number" &&  args.item["26"] !="Total" && args.item["26"] =="New") {
					return true;
				}

				if (args.item["11"] == "Planned"
									&& (cols[cell].name == "Project Name" || cols[cell].name == "Project WBS" || 
											cols[cell].name == "SubActivity"  || cols[cell].name == "Vendor" ) &&  
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
							&& ((args.item["11"] == "Planned" && args.item["26"] !="Total") || (args.item["11"] == "Accrual" && args.item["26"] =="Active"))) {
						return true;
					} 
				}
				
				if (args.item["11"] == "Planned"
						&& cols[cell].name == "Remark" &&  args.item["26"] !="Total") {
					return true;
				} 
				
				return false;
			}else{
				if( cols[cell].name == "$ in 1000's" ||  cols[cell].name == "Status"){
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
			Slick.GlobalEditorLock.cancelCurrentEdit();
			// clear on Esc
			if (e.which == 27) {
				this.value = "";
			}
			searchString = this.value;
			
	    if (searchString != "") {
				dataView.expandGroup("Active");
				dataView.expandGroup("Closed");
				dataView.expandGroup("New");
			} else {
				dataView.collapseGroup("Active");
				dataView.collapseGroup("Closed");
			}
			dataView.refresh();
		});

		
		// Handeling radio button "Planned" and "All"
		rdoSelectedmode.change(function(e) {
			Slick.GlobalEditorLock.cancelCurrentEdit();
			var choice = this.value;
			if (choice == 'planned') {
				radioString = 'Planned'
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
	    autoEdit: false,
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
		{
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
		},
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
		

	function saveAndClose() {
		var errStr = "";
		var i = 0;
		for (i = 0; i < m_data.length; i++) {
			var d = m_data[i];
			errStr = "";
			
			if (m_data[i][4] != "" && m_data[i][4] != "undefined") {
				if (m_data[i][7].trim() == "" || m_data[i][7] == "undefined") {
					errStr = errStr + "Project Owner "
				}
				if (m_data[i][5].toString().trim() == ""
						|| m_data[i][5] == "undefined") {
					if (errStr.length > 0) {
						errStr = errStr + ", "
					}
					errStr = errStr + "gMemori Id "
				}
				if (m_data[i][1].trim() == "" || m_data[i][1] == "undefined") {
					if (errStr.length > 0) {
						errStr = errStr + ", "
					}
					errStr = errStr + "Brand "
				}
				if (m_data[i][3].toString().trim() == ""
						|| m_data[i][3] == "undefined"
						|| m_data[i][3].toString() == "0"
						|| m_data[i][3].toString() == "0.0") {
					if (errStr.length > 0) {
						errStr = errStr + ", "
					}
					errStr = errStr + "Total "
				}
				if (errStr.length > 0) {
					break;
				}
			} else {
				break;
			}
		}
		
		if (errStr.length > 0 || i == 1) {
			if (errStr.length > 0) {
				errStr = errStr + " cannot be blank"
				if (errStr.toString().indexOf("Total") != -1) {
					errStr = errStr + " or zero."
				} else {
					errStr = errStr + ".";
				}
			} else {
				errStr = "Please enter atleast two sub-projects."
			}
			alert(errStr);
			return;
		}

		availableTags = [];
		
		for (var j = 0; j < ccUsersVar.length; j++) {
			if (ccUsersVar[j][0] == itemClicked[1]) {
				var res = ccUsersVar[j][1].substring(1,
						ccUsersVar[j][1].length - 1);
				availableTags = res.split(",");
				break;
			}
		}

		availableTags.splice(0, 0, "Total Products(MB)");

		for (var i = 0; i < m_data.length; i++) {

			if ((m_data[i][4] == "" || m_data[i][4] == "undefined")
					&& m_data[i][1] != "") {
				m_data[i][4] = m_data[0][4];
			}
		}
		$('#multibrandEdit').hide();
		$('#back').removeClass('black_overlay').fadeIn(100);
		var total = 0.0;
		
		for (var i = 0; i < data.length; i++) {
			var d = data[i];

			if (d["id"] != 'undefined' && d["id"] == itemClicked["id"]) {
				itemClicked[36] = JSON.parse(JSON.stringify(m_data));
				itemClicked[37] = true;

				for (var j = 0; j < m_data.length; j++) {
					var d = m_data[j];
					if (d[4] != '') {
						total = total + parseFloat(d[3]);
					} else {
						break;
					}
				}
				break;
			}
		}
		itemClicked[24] = total;
		grid.invalidate();
		
		if (itemClicked["34"] != "New projects") {
			$.ajax({
				url : '/multiBrandServlet',
				type : 'POST',
				dataType : 'json',
				data : {
					objarray : JSON.stringify(m_data),
					sumTotal : total
				},
				success : function(result) {
					alert('Data saved successfully');
					isMultiBrand = false;
					window.location.reload(true);
				}
			});
		}
		
		for (var j = 0; j < m_data.length; j++) {
			var d = (m_data[j] = {});
			d[0] = "";
			d[1] = "";
			d[2] = "";
			d[3] = "";
			d[4] = "";
			d[5] = "";
			d[6] = "";
			d[7] = "";
		}
	}
	
	function closeWithoutSave() {
		availableTags = [];
		for (var j = 0; j < ccUsersVar.length; j++) {
			if (ccUsersVar[j][0] == itemClicked[1]) {
				var res = ccUsersVar[j][1].substring(1,
						ccUsersVar[j][1].length - 1);
				availableTags = res.split(",");
				break;
			}
		}
		availableTags.splice(0, 0, "Total Products(MB)");
		for (var j = 0; j < m_data.length; j++) {
			var d = (m_data[j] = {});
			d[0] = "";
			d[1] = "";
			d[2] = "";
			d[3] = "";
			d[4] = "";
			d[5] = "";
			d[6] = "";
			d[7] = "";
		}
		$('#multibrandEdit').hide();
		$('#back').removeClass('black_overlay').fadeIn(100);
	}

	function deleteSelectedProjects() {
		var userAccepted = confirm("Selected project(s) will be deleted. Want to continue?");
		if (!userAccepted) {
			return false;
		}
		var pLength = m_data.length;
		var noProjToDelete = true;
		for (var count = 0; count < m_data.length; count++) {
			if (m_data[count]["8"] != 'undefined' && m_data[count]["8"] == true) {
				m_data.splice(count--, 1);
				noProjToDelete = false;
			}
		}
		if (noProjToDelete) {
			alert("Please select project(s) to delete.");
		}
		for (var c = 0; c < pLength; c++) {
			if (c >= m_data.length) {
				var d = (m_data[c] = {});
				d[0] = "";
				d[1] = "";
				d[2] = "";
				d[3] = "";
				d[4] = "";
				d[5] = "";
				d[6] = "";
				d[7] = "";
				d[8] = false;
			}
			if (m_data[c][4].toString().trim() != ""
					&& itemClicked[34] == "New projects") {
				m_data[c][5] = itemClicked[0] + '.' + (c + 1);
			}
		}
		sum = 0;
		for (var count = 0; count < m_data.length && m_data[count]["3"] != ""
				&& m_data[count]["3"] != "undefined"; count++) {
			sum = sum + parseFloat(m_data[count]["3"]);
		}
		for (var count = 0; count < m_data.length && m_data[count]["3"] != ""
				&& m_data[count]["3"] != "undefined"; count++) {
			m_data[count]["2"] = (m_data[count]["3"] / sum * 100).toFixed(2);
		}

		if (m_data[0]["3"] == "") {
			m_data[0][4] = itemClicked[2];
			m_data[0][5] = itemClicked[0] + '.1';
			m_data[0][7] = itemClicked[1];
		}
		m_grid.invalidate();
	}

	// Multibrand popup window
	function displayMultibrandGrid() {
		m_grid = new Slick.Grid("#multibrandGrid", m_data, m_columns, m_options);
		m_grid.setSelectionModel(new Slick.CellSelectionModel());
		m_grid.registerPlugin(new Slick.AutoTooltips());
		m_grid.getCanvasNode().focus();

		m_grid.onClick.subscribe(function(e, args) {
			if (args.cell == <%=BudgetConstants.MB_CHECKBOX_CELL%>) {
				initDeletionCell(args.row);
			}
		})
		
		function initDeletionCell(row) {
			for (var count = 0; count < m_data.length; count++) {
				var thisId = "#" + count + "chkBox";
				if ($(thisId).is(':checked')) {
					m_data[count]["8"] = true;
				} else {
					m_data[count]["8"] = false;
				}
			}
		}

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

		m_grid.onBeforeEditCell
				.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row;
			var pRow = row + 1;

			if ((args.item[0].toString().trim() != "" && itemClicked[26] == "Active")
					|| (itemClicked[26] == "Closed")) {
				return false;
			}
			if (cell == <%=BudgetConstants.MB_BRAND_CELL%>) {
				availableTags = [];
				for (var j = 0; j < ccUsersVar.length; j++) {
					if (ccUsersVar[j][0] == m_data[row]["7"]) {
						var res = ccUsersVar[j][1].substring(1,
								ccUsersVar[j][1].length - 1);
						availableTags = res.split(",");
						break;
					}
				}
			}
			if (row == 0){
				if(cell == <%=BudgetConstants.MB_GMEMORI_ID_CELL%>){
					return false
				}
				return true;
			}
			if (row != 0) {
				if (cell == <%=BudgetConstants.MB_PROJECT_NAME_CELL%>) {
					m_data[row]["4"] = m_data[row - 1]["4"];
					m_grid.invalidate();
					return false;
				}
				if (cell == <%=BudgetConstants.MB_GMEMORI_ID_CELL%>) {
					if (m_data[row]["5"] == "") {
						m_data[row]["5"] = m_data[row - 1]["5"]
								.split(".")[0]
								+ "."
								+ (parseInt(m_data[row - 1]["5"]
										.split(".")[1]) + 1);
						m_grid.invalidate();
					}
					return false;
				}
				if ((m_data[row]["7"] == 'undefined' || m_data[row]["7"] == "")
						&& cell == <%=BudgetConstants.MB_PROJECT_OWNER_CELL%>) {
					m_data[row]["7"] = m_data[row - 1]["7"];
					m_grid.invalidate();
					return false;
				}
			}
			return true;
		});

		function removeArrayItem(arr, item) {
			var removeCounter = 0;

			for (var index = 0; index < arr.length; index++) {
				if (arr[index] === item) {
					arr.splice(index, 1);
					removeCounter++;
					index--;
				}
			}
			
			return removeCounter;
		}

		m_grid.onCellChange
				.subscribe(function(e, args) {
					var cell = args.cell;
					var row = args.row;
					var isValidBrand = false;
					sum = 0.0;
					if (cell == <%=BudgetConstants.MB_$_IN_THOUSAND_CELL%>) {

						for (var count = 0; count < m_data.length
								&& m_data[count]["3"] != ""
								&& m_data[count]["3"] != "undefined"; count++) {
							sum = sum + parseFloat(m_data[count]["3"]);
						}
						for (var count = 0; count < m_data.length
								&& m_data[count]["3"] != ""
								&& m_data[count]["3"] != "undefined"; count++) {
							m_data[count]["2"] = (m_data[count]["3"] / sum * 100)
									.toFixed(2);
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
							m_grid.invalidate();
							m_grid.invalidateRow(m_grid.getSelectedRows());
							m_grid.updateRowCount();
							m_grid.render();
						}
						m_grid.invalidate();
					}
					if (cell == <%=BudgetConstants.MB_PROJECT_OWNER_CELL%>
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
					}
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
</script>


<%@ include file="footer.jsp"%>
</body>
</html>