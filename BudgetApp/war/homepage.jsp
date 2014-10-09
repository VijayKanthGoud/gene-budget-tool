<%@page import="java.util.ArrayList"%>
<%@ include file="header.jsp"%>
<%@ page import="com.gene.app.bean.GtfReport"%>
<%@ page import="java.util.*"%>
<%
	if (userPrincipal != null) {
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
	var grid;
	var data = [];
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
	});

	var pluginOptions = {
		clipboardCommandHandler : function(editCommand) {
			undoRedoBuffer.queueAndExecuteCommand.call(undoRedoBuffer,
					editCommand);
		},
		includeHeaderWhenCopying : false
	};

	var columns = [ {
		id : "selector",
		name : "",
		field : "num",
		width : 1
	} ];
	var columnNames = [ "Unique Identifier", "Requestor", "Project WBS",
			"WBS Name", "SubActivity", "Brand", "Allocation %", "PO Number",
			"PO Desc", "Vendor", "JAN", "Feb", " 	 MAR", "  	 APR", "  	 MAY ",
			" 	 JUN", "  	 JUL", "  	 AUG ", " 	 SEP ", " 	 OCT", "  	 NOV",
			"  	 DEC ", " 	 Total " ];
	for (var i = 0; i < columnNames.length; i++) {
		columns.push({
			id : i,
			name : columnNames[i],
			field : i,
			width : 120,
			editor : Slick.Editors.Text
		});
	}

	$(function() {
		for (var i = 0; i < 11; i++) {
			var d = (data[i] = {});
			d["num"] = "";
			for (var j = 0; j < 23; j++) {
				d[j] = "";
			}
		}
		grid = new Slick.Grid("#gridtable", data, columns, options);
		grid.setSelectionModel(new Slick.CellSelectionModel());
		grid.registerPlugin(new Slick.AutoTooltips());
		// set keyboard focus on the grid
		grid.getCanvasNode().focus();
		grid.registerPlugin(new Slick.CellExternalCopyManager(pluginOptions));

		grid.onAddNewRow.subscribe(function(e, args) {
			var item = args.item;
			var column = args.column;
			grid.invalidateRow(data.length);
			data.push(item);
			grid.updateRowCount();
			grid.render();
		});
	})
	
	function submitData(){
		 alert('SUCCESS'+data);
		var param = 'objarray=' +JSON.stringify(data);
alert("param .."+param);
	    $.ajax({
	      url: '/storereport',
	      type: 'POST', 
	      dataType: 'json',  
	      data: param,
	      success: function(result) {
	          alert('SUCCESS');
	      }
	    });
	
	}
</script>

<div id="gridtable" style="width: 1323px; height: 425px;"></div>


<center>
 <button value="Submit" onclick="return submitData();"> Submit</button>
 </center>
 <%
	}
%>

<%@ include file="footer.jsp"%>