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
<script src="SlickGrid-master/slick.groupitemmetadataprovider.js"></script>
<script>
function requiredFieldValidator(value) {
  if (value == null || value == undefined || !value.length) {
    return {valid: false, msg: "This is a required field"};
  } else {
    return {valid: true, msg: null};
  }
}


/* var TaskNameFormatter = function (row, cell, value, columnDef, dataContext) {
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
}; */

var dataView;
var grid;
var data = [];
var columnNames = [ "Unique Identifier", 
                    "Requestor", 
                    "Project WBS",
                    "WBS Name", 
                    "SubActivity", 
                    "Brand", 
                    "Allocation %", 
                    "PO Number",
					"PO Desc", 
					"Vendor", 
					"JAN", "FEB", "MAR", "APR", "MAY",	"JUN", "JUL", 
					"AUG", "SEP", "OCT", "NOV",	"DEC", "Total" ,"Remark"];

var columns = [
	{   id : 0,name : columnNames[0],field : 0,width : 120,	editor : Slick.Editors.Text },
	{	id : 1,name : columnNames[1],field : 1,width : 120,	editor : Slick.Editors.Text },
	{	id : 2,name : columnNames[2],field : 2,width : 120,	editor : Slick.Editors.Text},
	{	id : 3,name : columnNames[3],field : 3,width : 120,	editor : Slick.Editors.Auto},
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
	{	id : 22,name : columnNames[22],field : 22,width : 120,	editor : Slick.Editors.Text} ,
	{	id : 23,name : columnNames[23],field : 23,width : 160,	editor : Slick.Editors.Text} 
	];

function sumTotalsFormatter(totals, columnDef) {
	  var val = totals.sum && totals.sum[columnDef.field];
	  if (val != null) {
	    return "Total: " + ((Math.round(parseFloat(val)*100)/100));
	  }
	  return "";
	}
	
function groupByProjectWBS() {
	  dataView.setGrouping({
	  getter: 24,
	    formatter: function (g) {
	    	if(g.value=="Total"){
	      return " " + g.value +" "  ;
	    	}else{
	    		return "" + g.value + "  <span style='color:green'>(" + g.count + " items)</span>";
	    	}
	    },
	  /*   aggregators: [
	      new Slick.Data.Aggregators.Sum(10),
	      new Slick.Data.Aggregators.Sum(11),
	      new Slick.Data.Aggregators.Sum(12),
	      new Slick.Data.Aggregators.Sum(13),
	      new Slick.Data.Aggregators.Sum(14),
	      new Slick.Data.Aggregators.Sum(15),
	      new Slick.Data.Aggregators.Sum(16),
	      new Slick.Data.Aggregators.Sum(17),
	      new Slick.Data.Aggregators.Sum(18),
	      new Slick.Data.Aggregators.Sum(19),
	      new Slick.Data.Aggregators.Sum(20),
	      new Slick.Data.Aggregators.Sum(21),
	      new Slick.Data.Aggregators.Sum(22)
	    ], */
	    aggregateCollapsed: false,
	    lazyTotalsCalculation: true
	  });
	}
var availableTags = [ "Rituxan Heme/Onc",
                      "Kadcyla",
                      "Actemra",
                      "Rituxan RA",
                      "Lucentis",
                      "Bitopertin",
                      "Ocrelizumab",
					  "Onart",
					  "Avastin",
					  "BioOnc Pipeline",
					  "Lebrikizumab",
					  "Pulmozyme",
					  "Xolair",
					  "Oral Octreotide",
					  "Etrolizumab",
					  "GDC-0199",
					  "Neuroscience Pipeline",
					  "Tarceva" ];
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
  if (searchString != "" && item[3].toLowerCase().indexOf(searchString.toLowerCase()) == -1) {
    return false;
  }

  if (item.parent != null) {
    var parent = data[item.parent];

    while (parent) {
      if (parent._collapsed || (searchString != "" && parent[3].toLowerCase().indexOf(searchString.toLowerCase()) == -1)) {
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
    <%for (int i = 0; i < gtfReports.size(); i++) {%>
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
   <%--  if ("<%=i%>"==6 && "<%=i%>">0) {
        indent++;
        parents.push("<%=i%>" - 1);
      }
     
     if (parents.length > 0) {
         parent = parents[parents.length - 1];
       } else {
         parent = null;
       } --%>
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
    		gtfReports.get(i).getForecastMap().get("DEC")%>";
    d[24]="<%=gtfReports.get(i).getStatus()%>";
     
  <%}%>

  var d = (data["<%=gtfReports.size()%>"] = {});
  d["id"] = "id_" + "<%=gtfReports.size()%>";
  d["indent"] = indent;
  d["parent"] = parent;
  for (var j = 0; j < 10 ; j++) {
	  d[j]="";
  }
  for (var j = 10; j < 23 ; j++) {
	  d[j]=0.0;
  }
  for (var j = 0; j < "<%=gtfReports.size()%>" ; j++) {
	 d[10]= parseFloat(d[10])+parseFloat(data[j][10]);
	 d[11]= parseFloat(d[11])+parseFloat(data[j][11]);
	 d[12]= parseFloat(d[12])+parseFloat(data[j][12]);
	 d[13]= parseFloat(d[13])+parseFloat(data[j][13]);
	 d[14]= parseFloat(d[14])+parseFloat(data[j][14]);
	 d[15]= parseFloat(d[15])+parseFloat(data[j][15]);
	 d[16]= parseFloat(d[16])+parseFloat(data[j][16]);
	 d[17]= parseFloat(d[17])+parseFloat(data[j][17]);
	 d[18]= parseFloat(d[18])+parseFloat(data[j][18]);
	 d[19]= parseFloat(d[19])+parseFloat(data[j][19]);
	 d[20]= parseFloat(d[20])+parseFloat(data[j][20]);
	 d[21]= parseFloat(d[21])+parseFloat(data[j][21]);
	 d[22]= parseFloat(d[22])+parseFloat(data[j][22]);
 	
  }
  d[24]="Total";
  // initialize the model
  dataView = new Slick.Data.DataView({ inlineFilters: true });
  dataView.beginUpdate();
  dataView.setItems(data);
  dataView.setFilter(myFilter);
  dataView.endUpdate();
  groupByProjectWBS();
  // initialize the grid
  grid = new Slick.Grid("#displayGrid", dataView, columns, options);
  //register the group item metadata provider to add expand/collapse group handlers
  grid.registerPlugin(groupItemMetadataProvider);
  grid.setSelectionModel(new Slick.CellSelectionModel());
  
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
  
  grid.onKeyDown.subscribe(function(e, args) {
	 /*  alert(args.cell);
	  alert(args.row);
	  alert(e.which); */
		var cell = args.cell;
		var row = args.row;
		if (e.which == 46) {
			data[row][cell] = "";
			if (!grid.getEditorLock().commitCurrentEdit()) {
				return;
			}
			grid.updateRow(row);
			e.stopPropagation();
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