<%@page import="com.gene.app.bean.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="javax.servlet.RequestDispatcher"%>

<%@ include file="header.jsp"%>


<%
	List<GtfReport> gtfReports = (List<GtfReport>) request
			.getAttribute("gtfreports");
	for (GtfReport report : gtfReports) {
		System.out.println(report.getgMemoryId());
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
<body onload="getBrandTotals()">  
     
   
<center>
	<div>
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
				<td style="width: 50%; height: 55px; text-align: center;"><span
						style="color: #105596; font-family: 'trebuchet ms'; font-size: 22px; font-weight: bold; letter-spacing: 5px; padding-top: 8px;">
						My Projects </span><br/>(<%=BudgetConstants.costCenter%>)</td>

				<td style="width: 20%;" rowspan="2">
					<table class="summarytable" width=100%
						style="color: #005691; white-space: nowrap; font-weight: bold;">
						<%
							BudgetSummary summary = (BudgetSummary) session.getAttribute("summary");
							Map<String, BudgetSummary> budgetMap = summary.getBudgetMap();
							BudgetSummary budgetSummary = new BudgetSummary();
							UserRoleInfo user = (UserRoleInfo) request.getAttribute("user");
							Map<String,Double> brandMap = user.getBrand();
							Object[] brands = brandMap.keySet().toArray();
						%>
						 <script>
						  var selectedValue = "";
						  var summaryResult = "";
						function getBrandTotals(){
							
							selectedValue = document.getElementById("brandType").value; 
							 <%
							 System.out.println("budgetMap = "+budgetMap);
							 
							for (Map.Entry<String, BudgetSummary> entry : budgetMap.entrySet())
							{
								budgetSummary = new BudgetSummary();
								String keyV = entry.getKey();
								/* int index = keyV.indexOf(" ");
								keyV = keyV.substring(0,index-1); */
								keyV = keyV.replaceAll("\\s+","");
								%>
								
								if(selectedValue == '<%= keyV.trim()%>'){
									
									 <%budgetSummary = entry.getValue();%>
									<%-- document.getElementById("totalBudget").value = <%= budgetSummary.getTotalBudget()%>; --%>
									$('#totalBudget').val("<%= budgetSummary.getTotalBudget()%>");
									$('#plannedTotal').text("<%= Math.round(budgetSummary.getPlannedTotal() * 10.0) / 10.0%>");
									$('#budgetLeftToSpend').text("<%= Math.round(budgetSummary.getBudgetLeftToSpend() * 10.0) / 10.0%>");
									$('#accrualTotal').text("<%= Math.round(budgetSummary.getAccrualTotal() * 10.0) / 10.0%>");
									$('#varianceTotal').text("<%= Math.round(budgetSummary.getVarianceTotal() * 10.0) / 10.0%>"); 
								}else{
									<%budgetSummary = new BudgetSummary();%>
								}
								<%}
							%> 
						} 
						</script> 
						
						
       					<tr>
                            <!-- td style="padding-left: 20px;">2017</td> -->
                            <td>Select Brand:</td>
                            <td><select id="brandType" onchange="getSummaryValues()">
                            <%String option = "";
                            for(int i=0;i<brands.length;i++){ 
                            option = brands[i].toString();
                            if(i==1){%>
                            <option value=<%=option %> selected><%=option %></option>
                            <%}else{ %>
                            <option value=<%=option %>><%=option %></option>
                            <%}} %>
                            </select></td>
                        </tr>
						
						 <tr>
                                <!-- td style="padding-left: 20px;">2017</td> -->
                                <td>Budget:</td>
                                <td>$<input id = "totalBudget" style="color: #005691" type=text name=type
                                       maxlength="8" size="8" value="<%=Math.round(budgetSummary.getTotalBudget() * 10.0) / 10.0%>"></td>
                         </tr>

						 <tr>
                                 <!-- td style="padding-left: 20px;">2017</td> -->
                                 <td>Planned:</td><td>$<span id = "plannedTotal"><%=Math.round(budgetSummary.getPlannedTotal() * 10.0) / 10.0%></span></td>
                          </tr>
                          <tr>
                                 <!-- td style="padding-left: 20px;">2017</td> -->
                                 <td>Unplanned Total:</td><td>$<span id = "budgetLeftToSpend"><%=Math.round(budgetSummary.getBudgetLeftToSpend() * 10.0) / 10.0%></span></td>
                          </tr>
                          <tr>
                                 <!-- td style="padding-left: 20px;">2017</td> -->
                                 <td>Accrual:</td><td>$<span id = "accrualTotal"><%=Math.round(budgetSummary.getAccrualTotal() * 10.0) / 10.0%></span></td>
                          </tr>
                          
                          <%
                                 String color=budgetSummary.getPercentageVarianceTotal() < 5?"yellow":  "#00FFFF";
                          %>
                           <tr>
                                 <!-- td style="padding-left: 20px;"><span
                                        style="background: <%=color%>;color:black">2017</span></td> -->
                                 <td><span style="background: <%=color%>;color:black">2017 Variance Total:</span></td>
                                 <td><span style="background: <%=color%>;color:black">$<span id = "varianceTotal"><%=Math.round(budgetSummary.getVarianceTotal() * 10.0) / 10.0%></span></span>
                                 </td>
                          </tr>

					</table>
				</td>
			</tr>
			<tr style="">
				<td style="padding-left: 21.5%"><input type=text
					style="float: left; align: center; width: 140px;" id="txtSearch">
					<img src="images/search.png" height="25" width="25" align="bottom"
					style="float: left;"
					title="Search in Project name, gMemori Id, Brand and Remarks.">
				</td>
			</tr>
		</table>
	</div>
	<div id="statusMessage"></div>
</center>
<% 	if(gtfReports == null || gtfReports.isEmpty()) { %>
 		<span style="  margin: auto;  position: absolute;  top: 350px; left: 48%; bottom: 0; right; font-size:12px; font-weight:bold; color: #005691" 
 		id="noData">(No data available!)</span>
 		<input type='button' style='font-size: 12px; height: 25px; width: 160px; background:#005691; color:#FFFFFF' 
 		value='Click to Create Project' id='topCrtNewProjBtn'/>  
	<%}%>
<div id="displayGrid" style="width: 100%; height: 58%;  min-height: 300px;"></div>

	<div id="multibrandEdit">
		<div id="header"
			style="width: 100%; height: 20px; background-color: #005691; color: white">&nbsp;Multi-brand:
		</div>
		<div id="multibrandGrid" style="width: 100%; height: 230px;"></div>
		<center>
			<button id="saveClose" class="myButton" value=""
				onclick="saveAndClose();">Save and close</button>
			<button class="myButton" value="" onclick="saveWithoutClose();">
				Cancel</button>
		</center>
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
	
    
	rdoSelectedmode = $('input[name="selectedmode"]');
	chkBoxHideColumns = $('input[name="hideColumns"]');

	choice = '';
	var dataView;
	var grid;
	var addsave=0;
	var data = [];
	var m_data = [];
	var itemClicked;
	 for (var i = 0; i < 5; i++) {
		var d = (m_data[i] = {});
		d[0] = "";
		d[1] = "";
		d[2] = "";
		d[3] = "";
		d[4] = "";
		d[5] = "";
		d[6] = "";
 	} 
	var radioString = "All";
	var totalSize = 0;
	var numHideColumns = 6;
	var columnNames = [ "gMemori Id", "Project Owner", "Project Name",
			"Project WBS", "WBS Name", "SubActivity", "Brand", "Allocation %",
			"PO Number", "PO Desc", "Vendor", "$ in 1000s", "JAN", "FEB",
			"MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
			"DEC", "Total", "Remark" ];

	// Columns displayed when hide columns is unchecked
	var columns = [ 
		{ id : 30, name : "Status", field : 30, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[2], field : 2, width : 150, editor : Slick.Editors.Text},
		{ id : 6, name : columnNames[6], field : 6, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Text},
		{ id : 11, name : columnNames[11], field : 11, width : 110, editor : Slick.Editors.Text, formatter : Slick.Formatters.HyperLink},
		{ id : 0, name : columnNames[0], field : 0, width : 90, editor : Slick.Editors.Text},
		{ id : 1, name : columnNames[1], field : 1, width : 90, editor : Slick.Editors.Text},
		{ id : 3, name : columnNames[3], field : 3, width : 90, editor : Slick.Editors.Text},
		{ id : 5, name : columnNames[5], field : 5, width : 90, editor : Slick.Editors.Text},
		{ id : 7, name : columnNames[7], field : 7, width : 90, editor : Slick.Editors.Text},
		{ id : 8, name : columnNames[8], field : 8, width : 90, editor : Slick.Editors.Text},
		{ id : 10, name : columnNames[10], field : 10, width : 90, editor : Slick.Editors.Text},
		{ id : 12, name : columnNames[12], field : 12, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 13, name : columnNames[13], field : 13, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[14], field : 14, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[15], field : 15, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[16], field : 16, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[17], field : 17, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[18], field : 18, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[19], field : 19, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[20], field : 20, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[21], field : 21, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[22], field : 22, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[23], field : 23, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[24], field : 24, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[25], field : 25, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	} ];

	//Columns displayed when hide columns is checked
	var hidecolumns = [ 
		{ id : 30, name : "Status", field : 30, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[2], field : 2, width : 150, editor : Slick.Editors.Text},
		{ id : 6, name : columnNames[6], field : 6, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Text},
		{ id : 11, name : columnNames[11], field : 11, width : 110, editor : Slick.Editors.Text, formatter : Slick.Formatters.HyperLink},
		{ id : 0, name : columnNames[0], field : 0, width : 90, editor : Slick.Editors.Text},
		{ id : 1, name : columnNames[1], field : 1, width : 90, editor : Slick.Editors.Text},
		{ id : 12, name : columnNames[12], field : 12, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 13, name : columnNames[13], field : 13, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[14], field : 14, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[15], field : 15, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[16], field : 16, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[17], field : 17, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[18], field : 18, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[19], field : 19, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[20], field : 20, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[21], field : 21, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[22], field : 22, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[23], field : 23, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[24], field : 24, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[25], field : 25, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	}]
	var searchString = "";
	// Grouping columns acording to status(New, Active, Closed)
	function groupByStatus() {
		dataView
				.setGrouping([{
					getter : 26,
					formatter : function(g) {
						var noOfNew = 0;
						var noOfActive = 0;
						var noOfClosed = 0;
						for(var cnt=0; cnt<data.length; cnt++){
							if(data[cnt][27].indexOf(".") ==-1 && data[cnt][26].indexOf("New") != -1 && data[cnt][1] != "" && data[cnt][1] != " "){
								noOfNew++;
							}
						}
						for(var cnt=0; cnt<data.length; cnt++){
							if(data[cnt][27].indexOf(".") ==-1 && data[cnt][26].indexOf("Active") != -1 && data[cnt][1] != "" && data[cnt][1] != " "){
								noOfActive++;
							}
						}
						for(var cnt=0; cnt<data.length; cnt++){
							if(data[cnt][27].indexOf(".") ==-1 && data[cnt][26].indexOf("Closed") != -1 && data[cnt][1] != "" && data[cnt][1] != " "){
								noOfClosed++;
							}
						}
						
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

		dataView.collapseGroup("Active");
		dataView.collapseGroup("Closed");
	}

	

	var options = {
		editable : true,
		enableAddRow : true,
		enableCellNavigation : true,
		asyncEditorLoading : false,
		autoEdit : false,
		frozenColumn : 3
	};

	var sortcol = "29";

	// Display total for active new and closed projects (roll up total)
	function sumTotalsFormatter(totals, columnDef) {
		var val = totals.sum && totals.sum[columnDef.field];
		if(columnDef.field==11 && totals['group']['value'].toLowerCase() != 'Total'
			.toLowerCase()){
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
				|| (radioString != "All" && item[11].toLowerCase().indexOf(
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
						|| (radioString != "All" && item[11].toLowerCase()
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
		var delCell = cell + 1;
		var row = args.row;
		if ($('#hideColumns').is(":checked")) {
			delCell = cell + numHideColumns;
		} else {
			delCell = cell + 2;
		}

		if (delCell == 25) {
			for (var i = 0; i < totalSize; i++) {
				if (data[i][31] == item[31]) {
					data[i][32] = item[delCell];
				}
			}
		}
		var cellValue = item[delCell];
		var cellNum = delCell - 12;

		console.log(args.item);
		key = item[0];
var aSaveData=[[]];
	var iCnt=0;
for(var i=0;i<data.length;i++){
	var d = data[i];
	 if(key== d[34] && d[11]=="Planned"){
		 var aSave = (aSaveData[iCnt] = {});
		 aSave[0] = d[0];
		 if(d[7] == 0.0){
			 d[7]=100.0;
		 }
		 aSave[1] = parseFloat( parseFloat(d[7]) * parseFloat(cellValue) /100).toFixed(2);
		 d[delCell]=aSave[1];
		 iCnt++;
	 }
}
		$.ajax({
			url : '/AutoSaveData',
			type : 'POST',
			dataType : 'text',
			data : {
				celNum : cellNum,
				objarray : JSON.stringify(aSaveData)
			},
			success : function(result) {
				$('#statusMessage').text("All changes saved successfully!")
						.fadeIn(200);
				$("#statusMessage");
				summaryResult = result;
				getSummaryValues();
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
				$('#totalBudget').val(value.totalBudget);
				$('#plannedTotal').text(value.plannedTotal);
				$('#budgetLeftToSpend').text(value.budgetLeftToSpend);
				$('#accrualTotal').text(value.accrualTotal);
				$('#varianceTotal').text(value.varianceTotal);
			}
		}
		}
		//Data data = new Gson().fromJson(json, Data.class);
		<%--summary = new Gson().fromJson(%>+obj+<%,BudgetSummary.class);--%>
	}
	
	$(function() {
		<% if(gtfReports == null || gtfReports.isEmpty()) { %>
		$('#displayGrid').hide();
		<% } %>
		var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
			groupItemMetadataProvider : groupItemMetadataProvider,
			inlineFilters : true
		});

		var indent = 0;
		var parents = [];
		
		// prepare the data
		<%int idCounter = -1;
		for (int i = 0; i < gtfReports.size(); i++) {
			boolean isFirst = true;
			for (int count = 0; count < 4; count++) {%>
				var d = (data["<%=++idCounter%>"] = {});
			 	var parent;
    	   		d["id"] = "id_" + "<%=idCounter%>";
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
				if(gmemoriID.indexOf(".") > -1){
					d[34]=gmemoriID.split(".")[0];
				}
        		<%if(isFirst){
    				isFirst = false;%>    
   			 		d[0]=gmemoriID;
    				d[1]="<%=gReport.getRequestor()%>";
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
   				<%if(idCounter%4 == 1){%>
   				if(gmemoriID.indexOf(".") == -1){
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
   					for (var j = 12; j < 25; j++) {
						d[j] = 0.0;
						}
					d[33]="";
   				}
   				<%} if(idCounter%4 == 2){%>
				d[11]="Accrual";
				if(d[26]!="New"){
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
   
   				<%} if(idCounter%4 == 3){%>
				d[11]="Variance";
				if(d[26]!="New"){
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
   				<%}%>
    
    				<%}
				}
			}%>
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
			    	d[11] = "Accurals";
			        break;
			    case 3:
			    	d[11] = "Variance";
			        break;
			    default:
			    	d[11] = "Planned";
		        	break;
			}
			
			for (var j = 0; j < totalSize ; j++) {
				if( d[11]==data[j][11] && data[j][0]!= 'undefined' && data[j][0].indexOf(".") ==-1){
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

			for (var j = 12; j < 25; j++) {
				d[j] = d[j].toFixed(2);
			}
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
			d[0]=" ";

		}

		// Calculation of remark field(To be calculated on server side)
		for (var j = 0; j < totalSize;) {
			var plannedAmt = data[j++]["24"];
			var bnchmrkAmt = data[j++]["24"];
			var accrualsAmt = data[j++]["24"];
			var variancesAmt = data[j++]["24"];
			var percentage;
			if (bnchmrkAmt != 0) {
				percentage = (bnchmrkAmt - accrualsAmt) / bnchmrkAmt * 100;
			} else {
				percentage = 0;
			}
			var remarks = data[j - 4]["25"];
			if ($.trim(remarks).length <= 0) {
				data[j - 4]["25"] = percentage + "%";
				data[j - 4][32] = percentage + "%";
			}
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

		// Caluculation of total (row and columnwise)
		grid.onCellChange
				.subscribe(function(e, args) {
			if(args.item["34"]!="New projects"){
				updateMemCache(e, args, tempKey);
			}
				var item = args.item;
				var tempKey = item[27];
				
				var cell = args.cell;
				var row = args.row;
				var dataLength = 0;
				
				var delCell = cell + 1;
				if ($('#hideColumns').is(":checked")) {
					delCell = cell + numHideColumns;
				} else {
					delCell = cell + 2;
				}
				
				
				for(var counter = 0; counter<data.length; counter++ ){
					if(data[counter][34] != "New projects"){
						dataLength++;
					}
				}
				if(item[37]=='undefined' || item[37]==false){
				grid.invalidate();
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
				
				switch(item[11]) {
			    case "Planned":
			    	dataLength = data.length - 4;
			        break;
			    case "Benchmark":
			    	dataLength = data.length - 3;
			        break;
			    case "Accurals":
			    	dataLength = data.length - 2;
			        break;
			    case "Variance":
			    	dataLength = data.length - 1;3
			        break;
			    default :
			    	dataLength = data.length - 4;
		        	break;
			}
				
				data[dataLength][delCell]=0.0;
				for (var j = 0; j < totalSize ; j++) {
					if( data[dataLength][11]==item[11] && data[j][0]!= 'undefined' && data[j][0].indexOf(".") ==-1){
						data[dataLength][delCell] = parseFloat(data[dataLength][delCell]) + parseFloat(data[j][delCell]);
					}
				
				}
				
				grid.invalidate();
			
		});

		grid.onClick.subscribe(function(e, args) {
			
				itemClicked = dataView.getItem(args.row);
				// multi brand click
				if(args.cell==2 && itemClicked[6].toLowerCase().indexOf("mb")!=-1){
					
					if(itemClicked[34]!="New projects"){
						// Start : For Multibrand projects on click of brand (with mb) display pop-up conatining sub-projects
						var multiBrandCnt = 0 ;	
						<% 
						GtfReport pGtfReport = new GtfReport();
						for(GtfReport gtfReport : gtfReports){%>
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
    							d["5"] =   itemClicked[0]+"."+multiBrandCnt;
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
				
						if(itemClicked[2]=='' || itemClicked[0]=='' || 
							itemClicked[2]=='undefined' || itemClicked[0]=='undefined'){
					
							if(itemClicked[2]=='' || itemClicked[2]=='undefined'){
								error=error+1;
							}
							if(itemClicked[0]=='' || itemClicked[0]=='undefined'){
								error=error+3;
							}
							switch(error) {
				    			case 0:
				        			break;
				    			case 1:
				    				errStrng="Project name cannot be blank."
				        			break;
				    			case 3:
				    				errStrng="gMemoriID cannot be blank."
				        			break;
				    			case 4:
				    				errStrng="gMemoriID or Project name cannot be blank."
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
						 	}
						 	$('#multibrandEdit').show().fadeIn(100);
							displayMultibrandGrid();
							$('#back').addClass('black_overlay').fadeIn(100);
						}else{
							alert(errStrng);
						}
					}
				} 
				/* // Handele click on Save button
				if(args.cell==2 && itemClicked[6].toLowerCase().indexOf("save")!=-1){
					submitProjects();
				} */
				// Handele click on Cancel button
				/* if(args.cell==3 && itemClicked[11].toLowerCase().indexOf("cancel")!=-1){
					cancelProjects();
				} */
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
		
		// Handeler for Create New Project button
		$(document).on('click', '#crtNewProjBtn',
			    function() {
				 	createNewProjects();
			    }
		);
		$(document).on('click', '#topCrtNewProjBtn',
			    function() {
				 	createNewProjects();
			    }
		);
		
		function createNewProjects(){
			$('#displayGrid').show();
			$('#topCrtNewProjBtn').hide();
			$('#noData').hide();
			var length= data.length;
			var item ={id:"id_"+length,indent:0,0:"",1:"project_owner_name",2:"project_name",3:"vijay",4:"",5:"sub_activity",6:"brand",7:"allocation_percentage",8:"",9:"",10:""
				,11:"Planned",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:"vijay",33:"New",34:"New projects",35:"NewProjects",37:false};
			dataView.insertItem(0,item);
		if(addsave ==0){
		    var saveClose ={id:"id_"+length+1,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"Save",7:"",8:"",9:"",10:""
						,11:"Cancel",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
							,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
								,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false};
			var item2 ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
						,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
							,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
								,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false};
			var item3 ={id:"id_"+length+3,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
						,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
							,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
								,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false};
			dataView.insertItem(1,item3);
		    dataView.insertItem(2,saveClose);
		    dataView.insertItem(3,item2);
		}
		    addsave=addsave+1;
		    dataView.refresh(); 
		
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
			window.location.reload(true);
		}
		
		function submitProjects(){
			var storeData=[];
			var flag = true;
			for(var i=0;i<addsave;i++){
				
				if(data[i][0]=="" || data[i][0]=='undefined'){
					alert("gMemoriID cannot be blank.");
					return;
				}
				storeData[i]=data[i];
				//alert(JSON.stringify(data[i], null, 4));
			}
			
			 $.ajax({
				url : '/storereport',
				type : 'POST',
				dataType : 'json',
				data : {objarray: JSON.stringify(storeData) },
				success : function(result) {
					console.log("Result::::"+result);
					alert('Data saved successfully');
					storeData=[];
					window.location.reload(true);
				},
				error: function() {
		            alert('gMemori Id exists. Try Different gMemori Id.');
		        }
			});  
		
		}
		
		// delete cell data on press of delete button
		grid.onKeyDown.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row - 1;
			var delCell = cell + 1;
			if (e.which == 46) {
				if ($('#hideColumns').is(":checked")) {
					delCell = cell + numHideColumns;
				}
				data[row][delCell] = 0.0;
				updateTotals(cell, row, delCell, args);
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
					if(args.item["34"]!="New projects" ){
					var newYear = <%=year+1%>;
					var quarter = <%=qtr%>;
					var month = <%=month%>;
					var monthArray = ["JAN", "FEB","MAR","APR","MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV","DEC"];
					var cell = args.cell;
					var row = args.row;
					var cols = grid.getColumns();
					var result = false;
					var result1 = false;
					for (var i = month; i < 12; i++) {
						if (cols[cell].name == monthArray[i]
								&& args.item["11"] == "Planned" && args.item["26"] !="Total") {
							result = true;
							break;
						} else {
							result = false;
						}
					}
					if (args.item["11"] == "Planned"
							&& cols[cell].name == "Remark" &&  args.item["26"] !="Total") {
						result1 = true;
					} else {
						result1 = false;
					}
					if (result || result1) {
						return true;
					} else {
						return false;
					}
					}else{
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
			} else {
				dataView.collapseGroup("Active");
				dataView.collapseGroup("Closed");
			}
			dataView.refresh();
		});

		
		// Handeling radio button "Planned" and "All"
		rdoSelectedmode.change(function(e) {
			Slick.GlobalEditorLock.cancelCurrentEdit();
			choice = this.value;
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
	$(window).bind(
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
						celNum : ""
					},
					success : function(result) {
						$('#statusMessage').text(
								"All changes saved successfully!").fadeIn(200);
						$("#statusMessage").fadeOut(400);
					}
				});
			});
	
	
	  var m_grid;
	  
	  var m_options = {
	    editable: true,
	    enableAddRow: true,
	    enableCellNavigation: true,
	    asyncEditorLoading: false,
	    autoEdit: false
	  };
	  var sum = 0.0;
	  var m_columns = [
		{
			id : 0,
			name : "Project name",
			field : 4,
			width : 160,
			editor : Slick.Editors.Text
		},
		{
			id : 4,
			name : "gmemori id",
			field : 5,
			width : 100,
			editor : Slick.Editors.Text
		},
	    {
			id : 1,
			name : "Brand",
			field : 1,
			width : 160,
			editor : Slick.Editors.Auto
		}, {
			id : 3,
			name : "Total",
			field : 3,
			width : 140,
			editor : Slick.Editors.Text
		}, {
			id : 2,
			name : "Allocation %",
			field : 2,
			width : 125
		}
	  ];
		
	  var availableTags = [ "Rituxan Heme/Onc", "Kadcyla", "Actemra",
	            			"Rituxan RA", "Lucentis", "Bitopertin", "Ocrelizumab", "Onart",
	            			"Avastin", "BioOnc Pipeline", "Lebrikizumab", "Pulmozyme",
	            			"Xolair", "Oral Octreotide", "Etrolizumab", "GDC-0199",
	            			"Neuroscience Pipeline", "Tarceva" ];
	  function saveAndClose(){
		
		  for(var i=0;i<m_data.length;i++){
			  if((m_data[i][4]=="" || m_data[i][4]=="undefined") && m_data[i][1]!=""){
				  m_data[i][4] = m_data[0][4];
			  }
		  }
			$('#multibrandEdit').hide();
			$('#back').removeClass('black_overlay').fadeIn(100);
			 var total =0.0;
			for(var i=0;i<data.length;i++){
				var d = data[i];
				
				 if(d["id"]!='undefined' && d["id"]== itemClicked["id"]){
					 itemClicked[36] = JSON.parse(JSON.stringify(m_data));
					 itemClicked[37] = true;
					
					 for (var j = 0; j < m_data.length; j++) {
						 var d = m_data[j];
						 if(d[4]!='' ){
							total=total + parseFloat(d[3]);
						 }else{
							 break;
						 }
					 	} 
					 
					 
					 break;
				} 
			}
			itemClicked[24]=total;
		grid.invalidate();
		  if(itemClicked["34"]!="New projects" ){
				 $.ajax({
						url : '/multiBrandServlet',
						type : 'POST',
						dataType : 'json',
						data : {objarray: JSON.stringify(m_data),sumTotal:total},
						success : function(result) {
							alert('Data saved successfully');
							isMultiBrand = false;
							window.location.reload(true);
						}
					});
		  }
			  for (var j = 0; j < 5; j++) {
					var d = (m_data[j] = {});
					d[0] = "";
					d[1] = "";
					d[2] = "";
					d[3] = "";
					d[4] = "";
					d[5] = "";
					d[6] = "";
			 	} 
		}
		
		function saveWithoutClose(){
			 for (var j = 0; j < 5; j++) {
					var d = (m_data[j] = {});
					d[0] = "";
					d[1] = "";
					d[2] = "";
					d[3] = "";
					d[4] = "";
					d[5] = "";
					d[6] = "";
			 	} 
			$('#multibrandEdit').hide();
			$('#back').removeClass('black_overlay').fadeIn(100);
		}
	function displayMultibrandGrid() {
	    m_grid = new Slick.Grid("#multibrandGrid", m_data, m_columns, m_options);
	    m_grid.setSelectionModel(new Slick.CellSelectionModel());
	    m_grid.registerPlugin(new Slick.AutoTooltips());
	    // set keyboard focus on the grid
	    m_grid.getCanvasNode().focus();
	    //var copyManager = new Slick.CellCopyManager();
	    //m_grid.registerPlugin(copyManager);
	   
	    m_grid.onAddNewRow.subscribe(function (e, args) {
	      var item = args.item;
	      var column = args.column;
	      var row = args.row;
	      m_grid.invalidateRow(m_data.length);
	      m_data.push(item);
	      m_grid.updateRowCount();
	      m_grid.render();
	    });
	    
	    
	    m_grid.onBeforeEditCell
		.subscribe(function(e, args) {
			if(args.row!=0){
			var cell = args.cell+1;
			var row = args.row;
			var pRow=row+1;
			
			if(m_data[row]["4"] == "" && cell==1){
				m_data[row]["4"] = m_data[row-1]["4"];
				m_grid.invalidate();
			}
			
			if(m_data[row]["5"] == "" && cell==2){
				m_data[row]["5"] = m_data[row-1]["5"].split(".")[0]+"."+pRow;
				m_grid.invalidate();
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
	    m_grid.onCellChange.subscribe(function(e, args) {
			
			var cell = args.cell+1;
			var row = args.row;
			sum = 0.0;
			
			
			
			if(cell == 4){
				
			for(var count = 0; count < m_data.length && m_data[count]["3"] != "" && m_data[count]["3"] != "undefined"; count++){
				sum = sum + parseFloat(m_data[count]["3"]);
			}
			for(var count = 0; count < m_data.length && m_data[count]["3"] != "" && m_data[count]["3"] != "undefined"; count++){
				m_data[count]["2"] = (m_data[count]["3"] / sum * 100).toFixed(2);
			}
			
			m_grid.invalidate();
			}
			if(cell == 3 && availableTags.indexOf(m_data[row][1]) == -1){
				m_data[row][1]="";
				alert("Enter a valid brand.");
		        m_grid.gotoCell(row, 0, true);
				
			}
			
			

		});
	    

	    
	  }
</script>


<%@ include file="footer.jsp"%>
</body>
</html>