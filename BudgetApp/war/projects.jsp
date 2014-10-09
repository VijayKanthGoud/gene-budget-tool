<%@page import="com.gene.app.bean.*"%>
<%@page import="java.util.*"%>
<%@ include file="header.jsp"%>

<%
      List<GtfReport> gtfReports = (List<GtfReport>)request.getAttribute("gtfreports");
	for(GtfReport report : gtfReports){
		System.out.println(report.getBrand());
	} 
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
<table>
	<tr>
		<td valign="top" width="100%">
			<div
				style="border: 1px solid gray; background: #E3E8F3; padding: 6px;">
				<label>WBS Name:</label> <input type=text id="txtSearch">
			</div> <br />
		</td>
	</tr>
</table>
</center>

<div id="displayGrid" style="width: 1323px; height: 400px;"></div>

<script src="SlickGrid-master/lib/firebugx.js"></script>
<script src="SlickGrid-master/lib/jquery-1.7.min.js"></script>
<script src="SlickGrid-master/lib/jquery-ui-1.8.16.custom.min.js"></script>
<script src="SlickGrid-master/lib/jquery.event.drag-2.2.js"></script>
<script src="SlickGrid-master/slick.core.js"></script>
<script src="SlickGrid-master/plugins/slick.autotooltips.js"></script>
<script src="SlickGrid-master/plugins/slick.cellrangedecorator.js"></script>
<script src="SlickGrid-master/plugins/slick.cellrangeselector.js"></script>
<script src="SlickGrid-master/plugins/slick.cellexternalcopymanager.js"></script>
<script src="SlickGrid-master/plugins/slick.cellselectionmodel.js"></script>
<script src="SlickGrid-master/slick.editors.js"></script>
<script src="SlickGrid-master/slick.formatters.js"></script>
<script src="SlickGrid-master/slick.grid.js"></script>
<script src="SlickGrid-master/slick.dataview.js"></script>
<script src="SlickGrid-master/slick.core.js"></script>
<script>
function requiredFieldValidator(value) {
  if (value == null || value == undefined || !value.length) {
    return {valid: false, msg: "This is a required field"};
  } else {
    return {valid: true, msg: null};
  }
}


var TaskNameFormatter = function (row, cell, value, columnDef, dataContext) {
  value = value.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;");
  var spacer = "<span style='display:inline-block;height:1px;width:" + (15 * dataContext["indent"]) + "px'></span>";
  var idx = dataView.getIdxById(dataContext.id);
  if (data[idx + 1] && data[idx + 1].indent > data[idx].indent) {
    if (dataContext._collapsed) {
      return spacer + " <span class='toggle expand'></span>&nbsp;" + value;
    } else {
      return spacer + " <span class='toggle collapse'></span>&nbsp;" + value;
    }
  } else {
    return spacer + " <span class='toggle'></span>&nbsp;" + value;
  }
};

var dataView;
var grid;
var data = [];
 /* var columns = [
  {id: "title", name: "Title", field: "title", width: 220, cssClass: "cell-title", formatter: TaskNameFormatter, editor: Slick.Editors.Text, validator: requiredFieldValidator},
  {id: "duration", name: "Duration", field: "duration", editor: Slick.Editors.Text},
  {id: "%", name: "% Complete", field: "percentComplete", width: 80, resizable: false, formatter: Slick.Formatters.PercentCompleteBar, editor: Slick.Editors.PercentComplete},
  {id: "start", name: "Start", field: "start", minWidth: 60, editor: Slick.Editors.Date},
  {id: "finish", name: "Finish", field: "finish", minWidth: 60, editor: Slick.Editors.Date},
  {id: "effort-driven", name: "Effort Driven", width: 80, minWidth: 20, maxWidth: 80, cssClass: "cell-effort-driven", field: "effortDriven", formatter: Slick.Formatters.Checkmark, editor: Slick.Editors.Checkbox, cannotTriggerInsert: true}
];  */

/* var columns = [ {
	id : "selector",
	name : "",
	field : "num",
	width : 1
} ]; */
var columnNames = [ "Unique Identifier", "Requestor", "Project WBS",
		"WBS Name", "SubActivity", "Brand", "Allocation %", "PO Number",
		"PO Desc", "Vendor", "JAN", "Feb", " 	 MAR", "  	 APR", "  	 MAY ",
		" 	 JUN", "  	 JUL", "  	 AUG ", " 	 SEP ", " 	 OCT", "  	 NOV",
		"  	 DEC ", " 	 Total " ];
/* for (var i = 0; i < columnNames.length; i++) { */
	var columns = [
	{   id : 0,name : columnNames[0],field : 0,width : 120,	editor : Slick.Editors.Text },
	{	id : 1,name : columnNames[1],field : 1,width : 120,	editor : Slick.Editors.Text , formatter: TaskNameFormatter},
	{	id : 2,name : columnNames[2],field : 2,width : 120,	editor : Slick.Editors.Text},
	{	id : 3,name : columnNames[3],field : 3,width : 120,	editor : Slick.Editors.Text},
	{	id : 4,name : columnNames[4],field : 4,width : 120,	editor : Slick.Editors.Text},
	{	id : 5,name : columnNames[5],field : 5,width : 120,	editor : Slick.Editors.Text},
	{	id : 6,name : columnNames[6],field : 6,width : 120,	editor : Slick.Editors.Text},
	{	id : 7,name : columnNames[7],field : 7,width : 120,	editor : Slick.Editors.Text},
	{	id : 8,name : columnNames[8],field : 8,width : 120,	editor : Slick.Editors.Text},
	{	id : 9,name : columnNames[9],field : 9,width : 120,	editor : Slick.Editors.Text},
	{	id : 10,name : columnNames[10],field : 10,width : 120,	editor : Slick.Editors.Text},
	{	id : 11,name : columnNames[11],field : 11,width : 120,	editor : Slick.Editors.Text},
	{	id : 12,name : columnNames[12],field : 12,width : 120,	editor : Slick.Editors.Text},
	{	id : 13,name : columnNames[13],field : 13,width : 120,	editor : Slick.Editors.Text},
	{	id : 14,name : columnNames[14],field : 14,width : 120,	editor : Slick.Editors.Text},
	{	id : 15,name : columnNames[15],field : 15,width : 120,	editor : Slick.Editors.Text},
	{	id : 16,name : columnNames[16],field : 16,width : 120,	editor : Slick.Editors.Text},
	{	id : 17,name : columnNames[17],field : 17,width : 120,	editor : Slick.Editors.Text},
	{	id : 18,name : columnNames[18],field : 18,width : 120,	editor : Slick.Editors.Text},
	{	id : 19,name : columnNames[19],field : 19,width : 120,	editor : Slick.Editors.Text},
	{	id : 20,name : columnNames[20],field : 20,width : 120,	editor : Slick.Editors.Text},
	{	id : 21,name : columnNames[21],field : 21,width : 120,	editor : Slick.Editors.Text},
	{	id : 22,name : columnNames[22],field : 22,width : 120,	editor : Slick.Editors.Text}
	];
/* } */


var options = {
  editable: true,
  enableAddRow: true,
  enableCellNavigation: true,
  asyncEditorLoading: false,
  autoEdit : false
};

var percentCompleteThreshold = 0;
var searchString = "";

function myFilter(item) {
  if (searchString != "" && item[3].indexOf(searchString) == -1) {
    return false;
  }

  if (item.parent != null) {
    var parent = data[item.parent];

    while (parent) {
      if (parent._collapsed || (searchString != "" && parent[3].indexOf(searchString) == -1)) {
        return false;
      }

      parent = data[parent.parent];
    }
  }

  return true;
}

$(function () {
  var indent = 0;
  var parents = [];

  // prepare the data

    <%for (int i = 0; i < gtfReports.size() ; i++) {%>
    var d = (data["<%=i%>"] = {});
    var parent;

   /*  if (Math.random() > 0.8 && i > 0) {
      indent++;
      parents.push(i - 1);
    } else if (Math.random() < 0.3 && indent > 0) {
      indent--;
      parents.pop();
    } 
	
     */
    if ("<%=i%>"==6 && "<%=i%>">0) {
        indent++;
        parents.push("<%=i%>" - 1);
      }
     
     if (parents.length > 0) {
         parent = parents[parents.length - 1];
       } else {
         parent = null;
       }
     d["id"] = "id_" + "<%=i%>";
     d["indent"] = indent;
     d["parent"] = parent;
    d[1]="<%=gtfReports.get(i).getRequestor()%>";
    d[2]="<%=gtfReports.get(i).getProject_WBS()%>";
    d[3]="<%=gtfReports.get(i).getWBS_Name()%>";
    d[4]="<%=gtfReports.get(i).getSubActivity()%>";
    d[5]="<%=gtfReports.get(i).getBrand()%>";
    d[6]="<%=gtfReports.get(i).getPercent_Allocation()%>";
    d[7]="<%=gtfReports.get(i).getPoNumber()%>";
    d[8]="<%=gtfReports.get(i).getPoDesc()%>";
    d[9]="<%=gtfReports.get(i).getVendor()%>";
    d[10]="<%=gtfReports.get(i).getForecastMap().get("JAN")%>";
    d[11]="<%=gtfReports.get(i).getForecastMap().get("FEB")%>";
    d[12]="<%=gtfReports.get(i).getForecastMap().get("MAR")%>";
    d[13]="<%=gtfReports.get(i).getForecastMap().get("APR")%>";
    d[14]="<%=gtfReports.get(i).getForecastMap().get("MAY")%>";
    d[15]="<%=gtfReports.get(i).getForecastMap().get("JUN")%>";
    d[16]="<%=gtfReports.get(i).getForecastMap().get("JUL")%>";
    d[17]="<%=gtfReports.get(i).getForecastMap().get("AUG")%>";
    d[18]="<%=gtfReports.get(i).getForecastMap().get("SEP")%>";
    d[19]="<%=gtfReports.get(i).getForecastMap().get("OCT")%>";
    d[20]="<%=gtfReports.get(i).getForecastMap().get("NOV")%>";
    d[21]="<%=gtfReports.get(i).getForecastMap().get("DEC")%>";
    d[22]="<%=gtfReports.get(i).getForecastMap().get("JAN") + 
    		gtfReports.get(i).getForecastMap().get("FEB") + 
    		gtfReports.get(i).getForecastMap().get("MAR") + 
    		gtfReports.get(i).getForecastMap().get("APR") + 
    		gtfReports.get(i).getForecastMap().get("MAY") + 
    		gtfReports.get(i).getForecastMap().get("JUN") + 
    		gtfReports.get(i).getForecastMap().get("JUL") + 
    		gtfReports.get(i).getForecastMap().get("AUG") + 
    		gtfReports.get(i).getForecastMap().get("SEP") + 
    		gtfReports.get(i).getForecastMap().get("OCT") + 
    		gtfReports.get(i).getForecastMap().get("NOV") + 
    		gtfReports.get(i).getForecastMap().get("DEC")
    %>";
   /*  d[0]="1";
    d[1]="2";
    d[2]="3";
    d[3]="4";
    d[4]="5";
    d[5]="6";
    d[6]="";
    d[7]="";
    d[8]="";
    d[9]="";
    d[10]="";
    d[11]="";
    d[12]="";
    d[13]="";
    d[14]="";
     d[15]="";
    d[16]="";
    d[17]="";
    d[18]="";
    d[19]="";
    d[20]="";
    d[21]="";
    d[22]="";  */
   <%--   alert("gtfReports"+<%=gtfReports.size()%> +columnNames[0]);  --%>
     
     /* alert("d is "+d[i]) */
  /*   d["indent"] = indent;
    d["parent"] = parent;
    d["title"] = "Task " + i;
    d["duration"] = "5 days";
    d["percentComplete"] = Math.round(Math.random() * 100);
    d["start"] = "01/01/2009";
    d["finish"] = "01/05/2009";
    d["effortDriven"] = (i % 5 == 0);  */
  <% }%>
 
 /* 
		for (var i = 0; i < 10; i++) {
			var d = (data[i] = {});
			d["num"] = "";
			for (var j = 0; j < 10; j++) {
				d[j] = "";
			} */

  // initialize the model
  dataView = new Slick.Data.DataView({ inlineFilters: true });
  dataView.beginUpdate();
  dataView.setItems(data);
  dataView.setFilter(myFilter);
  dataView.endUpdate();


  // initialize the grid
  grid = new Slick.Grid("#displayGrid", dataView, columns, options);

  grid.onCellChange.subscribe(function (e, args) {
    dataView.updateItem(args.item.id, args.item);
  });

  grid.onAddNewRow.subscribe(function (e, args) {
    var item = {
      "id": "new_" + (Math.round(Math.random() * 10000)),
      "indent": 0,
      "title": "New task",
      "duration": "1 day",
      "percentComplete": 0,
      "start": "01/01/2009",
      "finish": "01/01/2009",
      "effortDriven": false};
    $.extend(item, args.item);
    dataView.addItem(item);
  });

  grid.onClick.subscribe(function (e, args) {
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


  // wire up model events to drive the grid
  dataView.onRowCountChanged.subscribe(function (e, args) {
    grid.updateRowCount();
    grid.render();
  });

  dataView.onRowsChanged.subscribe(function (e, args) {
    grid.invalidateRows(args.rows);
    grid.render();
  });


  var h_runfilters = null;

  // wire up the slider to apply the filter to the model
  $("#pcSlider").slider({
    "range": "min",
    "slide": function (event, ui) {
      Slick.GlobalEditorLock.cancelCurrentEdit();

      if (percentCompleteThreshold != ui.value) {
        window.clearTimeout(h_runfilters);
        h_runfilters = window.setTimeout(dataView.refresh, 10);
        percentCompleteThreshold = ui.value;
      }
    }
  });


  // wire up the search textbox to apply the filter to the model
  $("#txtSearch").keyup(function (e) {
    Slick.GlobalEditorLock.cancelCurrentEdit();

    // clear on Esc
    if (e.which == 27) {
      this.value = "";
    }

    searchString = this.value;
    dataView.refresh();
  })
})
</script>


<%@ include file="footer.jsp"%>