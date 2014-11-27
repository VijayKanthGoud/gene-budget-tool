<%@page import="java.util.ArrayList"%>
<%@ page import="com.gene.app.bean.GtfReport"%>
<%@ page import="java.util.*"%>
<%-- <%@ include file="header.jsp"%> --%>
<%
	if (true) {
%>


<style>
.slick-cell.copied {
	background: blue;
	background: rgba(0, 0, 255, 0.2);
	-webkit-transition: 0.5s background;
}

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

<link rel="stylesheet" href="SlickGrid-master/slick.grid.css"
	type="text/css" />
<link rel="stylesheet"
	href="SlickGrid-master/css/smoothness/jquery-ui-1.8.16.custom.css"
	type="text/css" />
<link rel="stylesheet" href="SlickGrid-master/examples/examples.css"
	type="text/css" />

<script src="SlickGrid-master/lib/firebugx.js"></script>
<script src="SlickGrid-master/lib/jquery-1.7.min.js"></script>
<script src="SlickGrid-master/lib/jquery-ui-1.8.16.custom.min.js"></script>
<script src="SlickGrid-master/lib/jquery.event.drag-2.2.js"></script>
<script src="SlickGrid-master/slick.core.js"></script>
<!-- <script src="SlickGrid-master/plugins/slick.autotooltips.js"></script> -->
<script src="SlickGrid-master/plugins/slick.cellrangedecorator.js"></script>
<script src="SlickGrid-master/plugins/slick.cellrangeselector.js"></script>
<script src="SlickGrid-master/plugins/slick.cellexternalcopymanager.js"></script>
<script src="SlickGrid-master/plugins/slick.cellselectionmodel.js"></script>
<script src="SlickGrid-master/slick.editors.js"></script>
<script src="SlickGrid-master/slick.formatters.js"></script>
<script src="SlickGrid-master/slick.grid.js"></script>
<script src="SlickGrid-master/slick.core.js"></script>

<script>
	var isMultiBrand = false;

	var m_data = [];
	   for (var i = 0; i < 5; i++) {
		      var d = (m_data[i] = {});
		      d[0] = "";
		      d[1] = "";
		      d[2] = "";
		      d[3] = "";
		    }
	
	var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC", "C",
			"C++", "Clojure", "COBOL", "ColdFusion", "Erlang", "Fortran",
			"Groovy", "Haskell", "Java", "JavaScript", "Lisp", "Perl", "PHP",
			"Python", "Ruby", "Scala", "Scheme" ];
	var options = {
		editable : true,
		enableAddRow : true,
		enableCellNavigation : true,
		asyncEditorLoading : false,
		autoEdit : false
	};

	var undoRedoBuffer = {
		commandQueue : [],
		commandCtr : 0,

		queueAndExecuteCommand : function(editCommand) {
			this.commandQueue[this.commandCtr] = editCommand;
			this.commandCtr++;
			editCommand.execute();
		},

		undo : function() {
			if (this.commandCtr == 0)
				return;

			this.commandCtr--;
			var command = this.commandQueue[this.commandCtr];

			if (command && Slick.GlobalEditorLock.cancelCurrentEdit()) {
				command.undo();
			}
		},
		redo : function() {
			if (this.commandCtr >= this.commandQueue.length)
				return;
			var command = this.commandQueue[this.commandCtr];
			this.commandCtr++;
			if (command && Slick.GlobalEditorLock.cancelCurrentEdit()) {
				command.execute();
			}
		}
	}
	// undo shortcut
	$(document).keydown(function(e) {
		if (e.which == 90 && (e.ctrlKey || e.metaKey)) { // CTRL + (shift) + Z
			if (e.shiftKey) {
				undoRedoBuffer.redo();
			} else {
				undoRedoBuffer.undo();
			}
		}
		/* if (e.which == 46){
			var rows = grid.getSelectedRows();
		    for (var i = 0, l = rows.length; i < l; i++) {
		        var item = dataView.getItem(rows[i]);
		        alert(item);
		        var rowid = item.id;
		        //dataView.deleteItem(rowid);
		    }
		} */
	});

	var pluginOptions = {
		clipboardCommandHandler : function(editCommand) {
			undoRedoBuffer.queueAndExecuteCommand.call(undoRedoBuffer,
					editCommand);
		},
		includeHeaderWhenCopying : false
	};

	var columnNames = [ "gMemori Id", "Project Owner", "Project Name", "Brand", "Allocation %", "Project WBS",
			"PO Number",
			"PO Desc", "Vendor", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
			"JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "Total" ];

	var columns = [ {
		id : 0,
		name : columnNames[0],
		field : 0,
		width : 120,
		editor : Slick.Editors.Text
	}, {
		id : 1,
		name : columnNames[1],
		field : 1,
		width : 120,
		editor : Slick.Editors.Auto
	}, {
		id : 2,
		name : columnNames[2],
		field : 2,
		width : 120,
		editor : Slick.Editors.Text
	}, {
		id : 3,
		name : columnNames[3],
		field : 3,
		width : 120,
		editor : Slick.Editors.Text,
		formatter : Slick.Formatters.HyperLink
	}, {
		id : 4,
		name : columnNames[4],
		field : 4,
		width : 120,
		editor : Slick.Editors.Text
	}, {
		id : 5,
		name : columnNames[5],
		field : 5,
		width : 120,
		editor : Slick.Editors.Text
	}, {
		id : 6,
		name : columnNames[6],
		field : 6,
		width : 120,
		editor : Slick.Editors.Text
	}, {
		id : 7,
		name : columnNames[7],
		field : 7,
		width : 120,
		editor : Slick.Editors.Text
	},  {
		id : 8,
		name : columnNames[8],
		field : 8,
		width : 120,
		editor : Slick.Editors.Text,
		//formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 9,
		name : columnNames[9],
		field : 9,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 10,
		name : columnNames[10],
		field : 10,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 11,
		name : columnNames[11],
		field : 11,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 12,
		name : columnNames[12],
		field : 12,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 13,
		name : columnNames[13],
		field : 13,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 14,
		name : columnNames[14],
		field : 14,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 15,
		name : columnNames[15],
		field : 15,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 16,
		name : columnNames[16],
		field : 16,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 17,
		name : columnNames[17],
		field : 17,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 18,
		name : columnNames[18],
		field : 18,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 20,
		name : columnNames[20],
		field : 20,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	}, {
		id : 21,
		name : columnNames[21],
		field : 21,
		width : 120,
		editor : Slick.Editors.FloatText,
		formatter : Slick.Formatters.DollarSymbol
	} ];

	$(function() {
	/* 	for (var i = 0; i < 11; i++) {
			var d = (c_data[i] = {});
			d["num"] = "";
			d["id"] = "id_" + i;
			for (var j = 0; j < 26; j++) {
				d[j] = "";
			}
			
		} */
		
		//grid = new Slick.Grid("#gridtable", data, columns, options);
		alert(JSON.stringify(c_data, null, 4));
		c_grid = new Slick.Grid("#gridtable", c_data, columns, options);
		c_grid.setSelectionModel(new Slick.CellSelectionModel());
		c_grid.registerPlugin(new Slick.AutoTooltips());
		// set keyboard focus on the grid
		c_grid.getCanvasNode().focus();
		c_grid.registerPlugin(new Slick.CellExternalCopyManager(pluginOptions));
		c_grid.invalidate();
		/* 	grid.onAddNewRow.subscribe(function(e, args) {
				var item = args.item;
				var column = args.column;
				grid.invalidateRow(data.length);
				data.push(item);
				grid.updateRowCount();
				grid.render();
			}); */

		c_grid.onKeyDown.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row;
			if (e.which == 46) {
				c_data[row][cell] = "";
				if (!c_grid.getEditorLock().commitCurrentEdit()) {
					return;
				}
				c_grid.updateRow(row);
				e.stopPropagation();
			}
		});
			
		c_grid.onClick.subscribe(function(e, args) {
			if(args.cell == 3 && c_data[args.row]["3"].toLowerCase().indexOf("mb") >= 0){
				//$('#multibrandDisp').load('multiBrand.jsp').fadeIn(100);
				$('#multibrandDisp').show().fadeIn(100);
				$('#back').addClass('black_overlay').fadeIn(100);
				displayMultibrandGrid();
				isMultiBrand = true;
			}
		});
		c_grid.onCellChange.subscribe(function(e, args) {
			var cell = args.cell;
			var row = args.row;
			var total = row;
			var sum=0.0;
			if(c_data[row][21]!=""){
				for(var cnt=10;cnt<21;cnt++){
					sum=parseFloat(sum)+ parseFloat(c_data[total][cnt]);
				}
				
				if( sum > c_data[total][21]) {
					c_data[row][cell]=0.0;
					alert("Sum of budget for all months exceeding totals.");
			        c_grid.gotoCell(row, cell, true);
				}

			 }
		   if(cell>10 && cell<21){
			while(c_data[row+1][7]!="" && c_data[row+1][7]!="undefined" &&
					c_data[row+1][8]!="" && c_data[row+1][8]!="undefined"){
				c_data[row+1][cell] = (parseFloat(c_data[total][cell])*parseFloat(c_data[row+1][8]/100)).toFixed(2);
				row++;
			}
			c_grid.invalidate();
		}
		});
		 c_grid.onBeforeEditCell
			.subscribe(function(e, args) {
				var row = args.row;
				if(c_data[row][26]=="Added"){
					return false;
				}else{
					return true;
				}
			});
		
	})

	function submitData() {
		var param = 'objarray=' + JSON.stringify(c_data);
		alert(JSON.stringify(param, null, 4));
		$.ajax({
			url : '/storereport',
			type : 'POST',
			dataType : 'json',
			data : {objarray: JSON.stringify(c_data), multibrand: isMultiBrand},
			success : function(result) {
				alert('c_data saved successfully');
				isMultiBrand = false;
			}
		});
	}
</script>

<script>
	function saveAndClose(){
		$('#multibrandDisp').hide();
		$('#back').removeClass('black_overlay').fadeIn(100);
		c_data[0][24]=sum;
		for(var count = 0; count < m_data.length && m_data[count]["1"] != "" && m_data[count]["1"] != "undefined"  && m_data[count]["2"] != "" && m_data[count]["2"] != "undefined"  && m_data[count]["3"] != "" && m_data[count]["3"] != "undefined"; count++){
			c_data[count+1][7]=m_data[count][1];
			c_data[count+1][8]=m_data[count][2];
			var total =count+1;
			var row= total;
			for(var cell=12;cell <24;cell++){
				total=row;
				if(c_data[0][cell]!=0.0){
					 
						 c_data[total][cell] = (parseFloat(c_data[0][cell])*parseFloat(c_data[total][8]/100)).toFixed(2);
					} 
				else{
					c_data[total][cell]=0.00;
					 }
				}
			c_data[count+1][26]="Added";
		}
	c_grid.invalidate();
	}
	
	function saveWithoutClose(){
		$('#multibrandDisp').hide();
		$('#back').removeClass('black_overlay').fadeIn(100);
	}

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
		id : 1,
		name : "Brand",
		field : 1,
		width : 160,
		editor : Slick.Editors.Auto
	}, {
		id : 2,
		name : "Allocation %",
		field : 2,
		width : 125,
	}, {
		id : 3,
		name : "Total",
		field : 3,
		width : 140,
		editor : Slick.Editors.Text
	}
  ];
	
  var availableTags = [ "Rituxan Heme/Onc", "Kadcyla", "Actemra",
            			"Rituxan RA", "Lucentis", "Bitopertin", "Ocrelizumab", "Onart",
            			"Avastin", "BioOnc Pipeline", "Lebrikizumab", "Pulmozyme",
            			"Xolair", "Oral Octreotide", "Etrolizumab", "GDC-0199",
            			"Neuroscience Pipeline", "Tarceva" ];

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
    
    m_grid.onCellChange.subscribe(function(e, args) {
		
		var cell = args.cell+1;
		var row = args.row;
		sum = 0.0;
		if(cell == 3){
		
		for(var count = 0; count < m_data.length && m_data[count]["3"] != "" && m_data[count]["3"] != "undefined"; count++){
			sum = sum + parseFloat(m_data[count]["3"]);
		}
		for(var count = 0; count < m_data.length && m_data[count]["3"] != "" && m_data[count]["3"] != "undefined"; count++){
			m_data[count]["2"] = (m_data[count]["3"] / sum * 100).toFixed(2);
		}
		m_grid.invalidate();
		}
		
		if(cell == 1 && availableTags.indexOf(m_data[row][1]) == -1){
			m_data[row][1]="";
			alert("Enter a valid brand.");
	        m_grid.gotoCell(row, 0, true);
			
		}

	});
    
    $('#multibrandGrid').on('blur', function() {
    	var cell = args.cell+1;
		var row = args.row;
		sum = 0.0;
		if(cell == 3){
		
		for(var count = 0; count < m_data.length && m_data[count]["3"] != "" && m_data[count]["3"] != "undefined"; count++){
			sum = sum + parseFloat(m_data[count]["3"]);
		}
		for(var count = 0; count < m_data.length && m_data[count]["3"] != "" && m_data[count]["3"] != "undefined"; count++){
			m_data[count]["2"] = (m_data[count]["3"] / sum * 100).toFixed(2);
		}
		m_grid.invalidate();
		}
		
		if(cell == 1 && availableTags.indexOf(m_data[row][1]) == -1){
			m_data[row][1]="";
			alert("Enter a valid brand.");
	        m_grid.gotoCell(row, 0, true);
			
		}
    });
    
  }
</script>

<div id="gridtable" style="width: 100%; height: 300px;"></div>
<center>
	<button id="submitButton" class="myButton" value="Submit" onclick="return submitData();">
		Submit</button>
	<button class="myButton" value="Reset" ">
		Reset</button>
</center>

<div id="multibrandDisp"> 
<div id="header" style="width:100%;height:20px; background-color:#005691; color: white">&nbsp;Multi-brand: </div>
<div id="multibrandGrid" style="width:100%;height:230px;"></div>
<center>
<button  id="saveClose" class="myButton" value="" onclick="saveAndClose();">
		Save and close</button>
<button class="myButton" value="" onclick="saveWithoutClose();">
		Cancel</button>
 </center>
 </div>   
<div id="back">	</div>  
<%
	}
%>

<%@ include file="footer.jsp"%>