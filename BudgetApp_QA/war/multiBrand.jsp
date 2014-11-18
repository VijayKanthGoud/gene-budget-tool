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
<div id="header" style="width:100%;height:20px;background-color:#005691">Enter project Multi Brand</div>
<div id="myGrid" style="width:100%;height:230px;"></div>
<center>
<button  id="saveClose" class="myButton" value="" onclick="saveAndClose();">
		Save and close</button>
<button class="myButton" value="" onclick="saveWithoutClose();">
		Cancel</button>
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
		data[0][24]=sum;
		for(var count = 0; count < m_data.length && m_data[count]["1"] != "" && m_data[count]["1"] != "undefined"; count++){
			data[count+1][7]=m_data[count][1];
			data[count+1][8]=m_data[count][2];
			var total =count+1;
			var row= total;
			for(var cell=12;cell <24;cell++){
				total=row;
				if(data[0][cell]!=0.0){
					 
						 data[total][cell] = (parseFloat(data[0][cell])*parseFloat(data[total][8]/100)).toFixed(2);
					} 
				else{
					data[total][cell]=0.00;
					 }
				}
			data[count+1][25]="Added";
		}
	grid.invalidate();
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

  $(function () {
 
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
		/* localStorage.setItem("data", JSON.stringify(m_data));
		alert(JSON.stringify(localStorage.getItem("data"), null, 4)); */

	});
    
  })
  

</script>
</body>
</html>