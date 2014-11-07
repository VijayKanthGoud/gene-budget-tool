<!DOCTYPE HTML>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="SlickGrid-master/slick.grid.css"
	type="text/css" />
<link rel="stylesheet"
	href="SlickGrid-master/css/smoothness/jquery-ui-1.8.16.custom.css"
	type="text/css" />
<link rel="stylesheet" href="SlickGrid-master/examples/examples.css"
	type="text/css" />
  <style>
    .slick-cell.copied {
      background: blue;
      background: rgba(0, 0, 255, 0.2);
      -webkit-transition: 0.5s background;
    }
  </style>
</head>
<body>
<div id="header" style="width:100%;height:20px;background-color:#005691"></div>
<div id="myGrid" style="width:100%;height:230px;"></div>
<center>
<button id= "" class="myButton" value="Submit" onclick="saveAndClose();">
		Save and close</button>
 </center>

<script src="SlickGrid-master/lib/firebugx.js"></script>

<script src="SlickGrid-master/lib/jquery-1.7.min.js"></script>
<script src="SlickGrid-master/lib/jquery-ui-1.8.16.custom.min.js"></script>
<script src="SlickGrid-master/lib/jquery.event.drag-2.2.js"></script>

<script src="SlickGrid-master/slick.core.js"></script>
<script src="SlickGrid-master/plugins/slick.autotooltips.js"></script>
<script src="SlickGrid-master/plugins/slick.cellrangedecorator.js"></script>
<script src="SlickGrid-master/plugins/slick.cellrangeselector.js"></script>
<script src="SlickGrid-master/plugins/slick.cellcopymanager.js"></script>
<script src="SlickGrid-master/plugins/slick.cellselectionmodel.js"></script>
<script src="SlickGrid-master/slick.editors.js"></script>
<script src="SlickGrid-master/slick.grid.js"></script>
<script>
	function saveAndClose(){
		$('#multibrandDisp').hide();
		$('#back').removeClass('black_overlay').fadeIn(100);
	}




  var m_grid;
  var m_data = [];
  var m_options = {
    editable: true,
    enableAddRow: true,
    enableCellNavigation: true,
    asyncEditorLoading: false,
    autoEdit: false
  };
  var m_columns = [
    {
		id : 1,
		name : "Brand",
		field : 1,
		width : 160,
		editor : Slick.Editors.Text
	}, {
		id : 2,
		name : "Allocation %",
		field : 2,
		width : 125,
		editor : Slick.Editors.Text
	}, {
		id : 3,
		name : "Total",
		field : 3,
		width : 140,
		editor : Slick.Editors.Text
	}
  ];
	
 

  $(function () {
    for (var i = 0; i < 5; i++) {
      var d = (m_data[i] = {});
      d[i] = "";
    }
    m_grid = new Slick.Grid("#myGrid", m_data, m_columns, m_options);
    m_grid.setSelectionModel(new Slick.CellSelectionModel());
    m_grid.registerPlugin(new Slick.AutoTooltips());
    // set keyboard focus on the grid
    m_grid.getCanvasNode().focus();
    var copyManager = new Slick.CellCopyManager();
    m_grid.registerPlugin(copyManager);
   
    m_grid.onAddNewRow.subscribe(function (e, args) {
      var item = args.item;
      var column = args.column;
      m_grid.invalidateRow(m_data.length);
      m_data.push(item);
      m_grid.updateRowCount();
      m_grid.render();
    });
    
    m_grid.onClick.subscribe(function(e, args) {
    	/*  alert(JSON.stringify(m_data));  */
    	dataView.addItem({id: "1", name: "", field: "", complete:true});   
    	dataView.refresh();
    	/* alert(JSON.stringify(dataView));
         */
    });
  })
  

</script>
</body>
</html>