<%@page import="com.gene.app.bean.*"%>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@ include file="header.jsp"%>

<%
	List<GtfReport> gtfReports = (List<GtfReport>)request.getAttribute("gtfreports");
for(GtfReport report : gtfReports){
System.out.println(report.getBrand());
}
Calendar cal = Calendar.getInstance();
int year = cal.get(Calendar.YEAR);
int month = cal.get(Calendar.MONTH);
int qtr = month/3;
%>

<link rel="stylesheet" href="SlickGrid-master/slick.grid.css"
	type="text/css" />
<link rel="stylesheet"
	href="SlickGrid-master/css/smoothness/jquery-ui-1.8.16.custom.css"
	type="text/css" />
<link rel="stylesheet" href="SlickGrid-master/examples/examples.css"
	type="text/css" />
<style>
.cell-title {
	font-weight: bold;
}

.cell-effort-driven {
	text-align: center;
}

.slick-row-total-class {
	background: green !important;
}

.toggle {
	height: 9px;
	width: 9px;
	display: inline-block;
}

.toggle.expand {
	background: url(SlickGrid-master/images/expand.gif) no-repeat center
		center;
}

.toggle.collapse {
	background: url(SlickGrid-master/images/collapse.gif) no-repeat center
		center;
}
</style>
<center>
	<div>
		<table
			style="border: 1px solid gray; background: #E3E8F3; padding: 6px; width: 100%; font-weight: normal; font-size: 14px; color: #005691; font-family: Trebuchet MS, Tahoma, Verdana, Arial, sans-serif; float: left;">
			<tr>
				<td style="width: 20%;" rowspan="2">
					<table class="summarytable"
						style="color: #005691; white-space: nowrap; height: 117px; width: 220px;">
						<th>Display controls:</th>
						<tr>
							<td style="padding-left:20px;"><input type="radio" name="selectedmode"		
						value="planned">Planned &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <input type="radio"		
						name="selectedmode" value="All" checked="checked">All</td>
						</tr>
						<tr>
							<td style="padding-left:20px;"><input type="checkbox" id="hideColumns" name="hideColumns" value="hide"
								checked>Hide Columns</td>
						</tr>
					</table>
				</td>
				<td style="width: 50%; height: 55px;">
					<center>
						<span
							style="color: #105596; font-family: 'trebuchet ms'; font-size: 22px; font-weight: bold; letter-spacing: 5px; padding-top: 8px;">My
							Projects</span>
					</center>

				</td>

				<td style="width: 20%;" rowspan="2">
					<table class="summarytable" width=100%
						style="color: #005691; white-space: nowrap;">
						<%
						BudgetSummary summary = (BudgetSummary)request.getAttribute("summary");
						
						%>
						<tr>
							<td style="padding-left:20px;">2017</td>
							<td>Total budget:</td>
							<td>$<input style="color: #005691" type=text name=type
								maxlength="8" size="8" value="<%= summary.getTotalBudget() %>"></td>
						</tr>
						<tr>
							<td style="padding-left:20px;">2017</td>
							<td>Budget left to spend:</td>
							<td>$<%= Math.round(summary.getBudgetLeftToSpend() * 10.0) / 10.0 %></td>
						</tr>
						<tr>
							<td style="padding-left:20px;">2017</td>
							<td>Benchmark Total:</td>
							<td>$<%=Math.round(summary.getBenchmarkTotal() * 10.0) / 10.0 %></td>
						</tr>
						<tr>
							<td style="padding-left:20px;">2017</td>
							<td>Planned Total:</td>
							<td>$<%=Math.round(summary.getPlannedTotal() * 10.0) / 10.0  %></td>
						</tr>
						<%
						String color=
						summary.getPercentageVarianceTotal() < 5? "yellow":  "#00FFFF"; %>
						<tr >
							<td style="padding-left:20px;"><span style="background: <%=color%>;color:black">2017</span></td>
							<td><span style="background: <%=color%>;color:black">Variance Total:</span></td>
							<td><span style="background: <%=color%>;color:black">$<%= Math.round(summary.getVarianceTotal() * 10.0) / 10.0 %></span> </td>
						</tr>
					</table>
				</td>
			</tr>
			<tr style="">
				<td>
					<center>
						Search String: <input type=text id="txtSearch"
							style="width: 120px;">
					</center>
				</td>
			</tr>
		</table>
	</div>
</center>
<div id="displayGrid" style="width: 100%; height: 400px;"></div>
 
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

function requiredFieldValidator(value) {
  if (value == null || value == undefined || !value.length) {
    return {valid: false, msg: "This is a required field"};
  } else {
    return {valid: true, msg: null};
  }
}

//code for radio button
rdoSelectedmode = $('input[name="selectedmode"]');
chkBoxHideColumns = $('input[name="hideColumns"]');
choice = '';
var dataView;
var grid;
var data = [];
var radioString="All";
var totalSize=0;
var numHideColumns=6;
var columnNames = [ "Unique Identifier", 
                    "Project Owner", 
                    "Project Name",
                    "Project WBS",
                    "WBS Name", 
                    "SubActivity", 
                    "Brand", 
                    "Allocation %", 
                    "PO Number",
					"PO Desc", 
					"Vendor",
					"$ in 1000s", 
					"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", 
					"AUG", "SEP", "OCT", "NOV", "DEC", "Total" ,"Remark"];

var columns = [
            	{   id : 30,name : "Status",field : 0,width : 120,	editor : Slick.Editors.Text },
            	{	id : 2,name : columnNames[2],field : 2,width : 120,	editor : Slick.Editors.Text},
            	{	id : 6,name : columnNames[6],field : 6,width : 120,	editor : Slick.Editors.Text},
            	{	id : 11,name : columnNames[11],field : 11,width : 120,	editor : Slick.Editors.Text},
           		{   id : 0,name : columnNames[0],field : 0,width : 120,	editor : Slick.Editors.Text },
           		{	id : 1,name : columnNames[1],field : 1,width : 120,	editor : Slick.Editors.Text },
           		{	id : 5,name : columnNames[5],field : 5,width : 120,	editor : Slick.Editors.Text},
           		{	id : 7,name : columnNames[7],field : 7,width : 120,	editor : Slick.Editors.Text},
           		{	id : 8,name : columnNames[8],field : 8,width : 120,	editor : Slick.Editors.Text},
           		{	id : 9,name : columnNames[9],field : 9,width : 120,	editor : Slick.Editors.Text},
           		{	id : 10,name : columnNames[10],field : 10,width : 120,	editor : Slick.Editors.Text},
           		{	id : 12,name : columnNames[12],field : 12,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 13,name : columnNames[13],field : 13,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 14,name : columnNames[14],field : 14,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 15,name : columnNames[15],field : 15,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 16,name : columnNames[16],field : 16,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 17,name : columnNames[17],field : 17,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 18,name : columnNames[18],field : 18,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 19,name : columnNames[19],field : 19,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 20,name : columnNames[20],field : 20,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 21,name : columnNames[21],field : 21,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 22,name : columnNames[22],field : 22,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 23,name : columnNames[23],field : 23,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 24,name : columnNames[24],field : 24,width : 100,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
           		{	id : 25,name : columnNames[25],field : 25,width : 200,	editor : Slick.Editors.LongText, formatter: Slick.Formatters.Remark}
           		];


var hidecolumns = [
            	{   id : 30,name : "Status",field : 0,width : 120,	editor : Slick.Editors.Text },
            	{	id : 2,name : columnNames[2],field : 2,width : 120,	editor : Slick.Editors.Text},
            	{	id : 6,name : columnNames[6],field : 6,width : 120,	editor : Slick.Editors.Text},
            	 {	id : 11,name : columnNames[11],field : 11,width : 120,	editor : Slick.Editors.Text},
           		{   id : 0,name : columnNames[0],field : 0,width : 120,	editor : Slick.Editors.Text },
           		{	id : 1,name : columnNames[1],field : 1,width : 120,	editor : Slick.Editors.Text },
               
                {	id : 12,name : columnNames[12],field : 12,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 13,name : columnNames[13],field : 13,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 14,name : columnNames[14],field : 14,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 15,name : columnNames[15],field : 15,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 16,name : columnNames[16],field : 16,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 17,name : columnNames[17],field : 17,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 18,name : columnNames[18],field : 18,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 19,name : columnNames[19],field : 19,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 20,name : columnNames[20],field : 20,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 21,name : columnNames[21],field : 21,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 22,name : columnNames[22],field : 22,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 23,name : columnNames[23],field : 23,width : 90,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 24,name : columnNames[24],field : 24,width : 100,	editor : Slick.Editors.FloatText, formatter: Slick.Formatters.DollarSymbol},
                {	id : 25,name : columnNames[25],field : 25,width : 200,	editor : Slick.Editors.LongText, formatter: Slick.Formatters.Remark} 
                ];


function sumTotalsFormatter(totals, columnDef) {
 	var val = totals.sum && totals.sum[columnDef.field];
 	if (val != null) {
  		 return "Total: " + ((Math.round(parseFloat(val)*100)/100));
	}
 	return "";
}
var searchString = "";
function groupByStatus() {
 	dataView.setGrouping({
	getter: 26,
  	formatter: function (g) {
   	if(g.value !="Total"){ 
   		if(radioString == 'Planned' || searchString != ""){
    		return " "+ g.value +"  <span style='color:green'>(" + (g.count) + " items)</span>"  ;
   		}else{
   			return " "+ g.value +"  <span style='color:green'>(" + (g.count)/4 + " items)</span>"  ; 
   		}
   	
   	}else{
    		return "<span style='color:green'> "+ g.value +"</span> ";
		}
   	},
  	aggregateCollapsed: false,
   	lazyTotalsCalculation: true
 	});
 	
 	dataView.collapseGroup("Active");
 	dataView.collapseGroup("Closed");
}


var availableTags = [ "Rituxan Heme/Onc", "Kadcyla", "Actemra", "Rituxan RA", "Lucentis", "Bitopertin", 
                      "Ocrelizumab", "Onart", "Avastin", "BioOnc Pipeline", "Lebrikizumab", "Pulmozyme", 
                      "Xolair", "Oral Octreotide", "Etrolizumab", "GDC-0199", "Neuroscience Pipeline", "Tarceva" ];
                      
var options = {
  editable: true,
  enableAddRow: true,
  enableCellNavigation: true,
  asyncEditorLoading: false,
  autoEdit : false,
  frozenColumn: 3
};




function myFilter(item) {
  if (((searchString != "" && item[6].toLowerCase().indexOf(searchString.toLowerCase()) == -1)  &&
 (searchString != "" && item[2].toLowerCase().indexOf(searchString.toLowerCase()) == -1) &&
 (searchString != "" && item[25].toLowerCase().indexOf(searchString.toLowerCase()) == -1) )|| 
 (radioString!= "All" && item[11].toLowerCase().indexOf(radioString.toLowerCase())==-1) ) {
    return false;
  }

  if (item.parent != null) {
    var parent = data[item.parent];
    while (parent) {
      if (parent._collapsed ||( (searchString != "" && parent[6].toLowerCase().indexOf(searchString.toLowerCase()) == -1) &&
     (searchString != "" && parent[2].toLowerCase().indexOf(searchString.toLowerCase()) == -1) &&
     (searchString != "" && parent[25].toLowerCase().indexOf(searchString.toLowerCase()) == -1) )||
           (radioString!= "All" && item[11].toLowerCase().indexOf(radioString.toLowerCase())==-1)) {
        return false;
      }
      parent = data[parent.parent];
    }
  }
  return true;
}

$(function () {
  var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
  dataView = new Slick.Data.DataView({
   groupItemMetadataProvider: groupItemMetadataProvider,
   inlineFilters: true
 });

  var indent = 0;
  var parents = [];

  // prepare the data
    <%int idCounter = -1;
    for (int i = 0; i < gtfReports.size(); i++) {
    boolean isFirst = true;
    for (int count = 0; count < 4; count++) {
    if(isFirst){
    isFirst = false;%>
  
    // start looping for four different maps
    var d = (data["<%=++idCounter%>"] = {});
    var parent;
    d["id"] = "id_" + "<%=idCounter%>";
    d["indent"] = indent;
    d["parent"] = parent;
    d[1]="<%=gtfReports.get(i).getRequestor()%>";
    d[2]="<%=gtfReports.get(i).getProjectName()%>";
    d[3]="<%=gtfReports.get(i).getProject_WBS()%>";
    d[4]="<%=gtfReports.get(i).getWBS_Name()%>";
    d[5]="<%=gtfReports.get(i).getSubActivity()%>";
    d[6]="<%=gtfReports.get(i).getBrand()%>";
    d[7]="<%=gtfReports.get(i).getPercent_Allocation()%>";
    d[8]="<%=gtfReports.get(i).getPoNumber()%>";
    d[9]="<%=gtfReports.get(i).getPoDesc()%>";
    d[10]="<%=gtfReports.get(i).getVendor()%>";
    d[11]="Planned";
    d[12]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("JAN"))%>";
    d[13]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("FEB"))%>";
    d[14]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("MAR"))%>";
    d[15]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("APR"))%>";
    d[16]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("MAY"))%>";
    d[17]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("JUN"))%>";
    d[18]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("JUL"))%>";
    d[19]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("AUG"))%>";
    d[20]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("SEP"))%>";
    d[21]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("OCT"))%>";
    d[22]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("NOV"))%>";
    d[23]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("DEC"))%>";
    d[24]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getPlannedMap().get("JAN") + 
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
    d[25]="";<%-- "<%=gtfReports.get(i).getRemarks()%>"; --%>
    d[26]="<%=gtfReports.get(i).getStatus()%>";

  <%} else{%>  
   var d = (data["<%=++idCounter%>"] = {});
   var parent;
   d["id"] = "id_" + "<%=idCounter%>";
   d["indent"] = indent;
   d["parent"] = parent;
   d[1]=""; d[2]=""; d[3]=""; d[4]=""; 
   d[5]=""; d[6]="";  d[7]=""; d[8]=""; d[9]="";d[10]="";
   
   <%if(idCounter%4 == 1){%>
   d[11]="Benchmark";
   d[12]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("JAN"))%>";
   d[13]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("FEB"))%>";
   d[14]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("MAR"))%>";
   d[15]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("APR"))%>";
   d[16]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("MAY"))%>";
   d[17]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("JUN"))%>";
   d[18]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("JUL"))%>";
   d[19]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("AUG"))%>";
   d[20]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("SEP"))%>";
   d[21]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("OCT"))%>";
   d[22]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("NOV"))%>";
   d[23]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("DEC"))%>";
   d[24]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getBenchmarkMap().get("JAN") + 
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
    d[25]="";
   d[26]="<%=gtfReports.get(i).getStatus()%>";
 
   <%} if(idCounter%4 == 2){%>
   d[11]="Accruals";
   d[12]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("JAN"))%>";
   d[13]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("FEB"))%>";
   d[14]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("MAR"))%>";
   d[15]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("APR"))%>";
   d[16]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("MAY"))%>";
   d[17]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("JUN"))%>";
   d[18]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("JUL"))%>";
   d[19]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("AUG"))%>";
   d[20]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("SEP"))%>";
   d[21]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("OCT"))%>";
   d[22]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("NOV"))%>";
   d[23]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("DEC"))%>";
   d[24]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getAccrualsMap().get("JAN") + 
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
    d[25]="";
   d[26]="<%=gtfReports.get(i).getStatus()%>";
   
   <%} if(idCounter%4 == 3){%>
   d[11]="Variances";
   d[12]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("JAN"))%>";
   d[13]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("FEB"))%>";
   d[14]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("MAR"))%>";
   d[15]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("APR"))%>";
   d[16]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("MAY"))%>";
   d[17]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("JUN"))%>";
   d[18]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("JUL"))%>";
   d[19]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("AUG"))%>";
   d[20]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("SEP"))%>";
   d[21]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("OCT"))%>";
   d[22]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("NOV"))%>";
   d[23]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("DEC"))%>";
   d[24]="<%=new DecimalFormat("#.#").format(gtfReports.get(i).getVariancesMap().get("JAN") + 
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
    d[25]="";
   d[26]="<%=gtfReports.get(i).getStatus()%>";
   <%}%>
    
    <%}
    }
  }%>
  totalSize="<%=gtfReports.size()%>" * 4;
  var d = (data[totalSize] = {});
  d["id"] = "id_" + totalSize;
  d["indent"] = indent;
  d["parent"] = parent;
  for (var j = 0; j < 11; j++) {
  	d[j] = "";
  }
  d[11] = "Planned";
  for (var j = 12; j < 25; j++) {
  	d[j] = 0.0;
  }
  for (var j = 0; j < totalSize; j=j+4) {
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
  
  for (var j = 12; j < 25; j++) {
	d[j] = d[j].toFixed(3);
  }
  d[26] = "Total";
  
  for (var j = 0; j < totalSize; ) {
		var plannedAmt = data[j++]["24"];
		var bnchmrkAmt = data[j++]["24"];
		var accrualsAmt = data[j++]["24"];
		var variancesAmt = data[j++]["24"];
		var percentage;
		if(bnchmrkAmt!=0){
			percentage = (bnchmrkAmt - accrualsAmt)/bnchmrkAmt * 100;
		}else{
			percentage = 0;
		}
		data[j-4]["25"] = percentage + "%";
	  }
  for (var j = totalSize-4; j <= totalSize; j++) {
		
		data[j]["25"] = "";
	  }
  // initialize the model
	dataView = new Slick.Data.DataView({
		inlineFilters : true
	});
	dataView.beginUpdate();
	dataView.setItems(data);
	dataView.setFilter(myFilter);
	dataView.endUpdate();
	groupByStatus();
	// initialize the grid
	grid = new Slick.Grid("#displayGrid", dataView, hidecolumns, options);
	//register the group item metadata provider to add expand/collapse group handlers
	grid.registerPlugin(groupItemMetadataProvider);
	grid.setSelectionModel(new Slick.CellSelectionModel());
	
	grid.onCellChange
	.subscribe(function(e, args) {
		var cell = args.cell;
		var row = args.row;
		var delCell=cell+1;
		updateTotals(cell,row,delCell,args);

});
	
	function updateTotals(cell,row,delCell,args) {
		grid.invalidate();
		if ($('#hideColumns').is(":checked")){
			delCell= cell+numHideColumns;
		}
		data[totalSize][delCell] = 0.0;
		for (var j = 0; j < totalSize; j=j+4) {
			data[totalSize][delCell] = parseFloat(data[totalSize][delCell])
			+ parseFloat(data[j][delCell]);
		}
		grid.invalidate();
		var temp = 0;
		for (var j = 0; j < data.length - 1; j++) {
			if (data[j]["id"] == args.item.id) {
			temp = j;
			break;
			}
		}
		grid.invalidate();
	
		data[temp][24] = 0.0;
	for (var j = 12; j < 24; j++) {
		data[temp][24] = parseFloat(data[temp][24])
		+ parseFloat(data[temp][j]);
	}
	grid.invalidate();
	data[data.length - 1][24] = 0.0;
	for (var j = 12; j < 24; j++) {
		data[data.length - 1][24] = parseFloat(data[data.length - 1][24])
		+ parseFloat(data[data.length - 1][j]);
	}
	grid.invalidate();
	dataView.updateItem(args.item.id, args.item);
		  }

grid.onClick.subscribe(function(e, args) {
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

grid.onKeyDown.subscribe(function(e, args) {
var cell = args.cell;
var row = args.row-1;
var delCell=cell+1;
if (e.which == 46) {
	if ($('#hideColumns').is(":checked")){
		delCell= cell+numHideColumns;
	}
	data[row ][delCell] = 0.0;
	updateTotals(cell,row,delCell,args);
	 if (!grid.getEditorLock().commitCurrentEdit()) {
		return;
	} 
	grid.invalidate();
	e.stopPropagation();
}
});

grid.onBeforeEditCell.subscribe(function(e,args) {
var newYear = <%=year+1%>;
var quarter = <%=qtr%>;
var month = <%=month%>;
var monthArray =  [{"m0":"JAN"},{"m0":"FEB"},{"m0":"MAR"},{"m0":"APR"} ,{"m0":"MAY"} ,{"m0":"JUN"} ,{"m0":"JUL"} ,{"m0":"AUG"} ,{"m0":"SEP"},{"m0":"OCT"},{"m0":"NOV"},{"m0":"DEC"}];
var cell = args.cell;
var row = args.row;
var cols = grid.getColumns();
var result=false;
var result1=false;
for(var i=month;i<12;i++){
if((cols[cell].name == monthArray[i].m0) && (args.item["11"]=="Planned")){
	result = true;
	break;
}
else{
  result = false;
}
}
if((args.item["11"]=="Planned") && (cols[cell].name=="Remark")){
	result1 = true;
}else{
	result1 = false;
}
if(result || result1){
	return true;
}else{
	return false;
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


// wire up the search textbox to apply the filter to the model
$("#txtSearch").keyup(function(e) {
	Slick.GlobalEditorLock.cancelCurrentEdit();
	// clear on Esc
	if (e.which == 27) {
		this.value = "";
	}
	searchString = this.value;
	dataView.refresh();
});
 

rdoSelectedmode.change(function(e) {
	Slick.GlobalEditorLock.cancelCurrentEdit();
    choice = this.value;
   if (choice == 'planned') {
	   radioString='Planned'
   }else {
	   radioString="All";
    } 
   dataView.refresh();
});

chkBoxHideColumns.change(function(e) {
	Slick.GlobalEditorLock.cancelCurrentEdit();
	if(this.checked){
    	grid.setColumns(hidecolumns);
   }else{
    	grid.setColumns(columns);
   }
   dataView.refresh();
});

grid.registerPlugin( new Slick.AutoTooltips({ enableForHeaderCells: true }) );
grid.render();
 
})
</script>


<%@ include file="footer.jsp"%>