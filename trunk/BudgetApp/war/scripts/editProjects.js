// DECLARATIONS FOR CONSTANTS
	// cell numbers edit project multiple brand pop up
	var MB_CHECKBOX_CELL = "0";
	var MB_PROJECT_OWNER_CELL = "4";
	var MB_BRAND_CELL = "1";
	var MB_$_IN_THOUSAND_CELL = "2";
	var MB_ALLOCATION_PERCENTAGE_CELL = "3";

	
	STATUS_CELL = "0";
	PROJECT_NAME_CELL = "1";
	BRAND_CELL = "2";
	$_IN_THOUSAND_CELL = "3";
	GMEMORI_ID_CELL = "4";
	PROJECT_OWNER_CELL = "5";
	PROJECT_WBS_CELL = "6";
	SUBACTIVITY_CELL = "7";
	ALLOCATION_PERCENTAGE_CELL = "8";
	PO_NUMBER_CELL = "9";
	VENDOR_CELL = "10";
	UNIT_CELL = "11";
	JAN_CELL = "12";
	FEB_CELL = "13";
	MAR_CELL = "14";
	APR_CELL = "15";
	MAY_CELL = "16";
	JUN_CELL = "17";
	JUL_CELL = "18";
	AUG_CELL = "19";
	SEP_CELL = "20";
	OCT_CELL = "21";
	NOV_CELL = "22";
	DEC_CELL = "23";
	TOTAL_CELL = "24";
	REMARK_CELL = "25";
	
	
	var searchString = "";
	
	var rdoSelectedmode = $('input[name="selectedmode"]');
	var chkBoxHideColumns = $('input[name="hideColumns"]');

// ON PAGE LOAD
$(function() {
	
		// Blur page on resizing below acceptable grid size on initial load
		if($(window).width() < 900){
			$('#cautionWindow').show().fadeIn(100);
			$('#back').addClass('black_overlay').fadeIn(100);
		}else{
			$('#cautionWindow').hide();
			$('#back').removeClass('black_overlay').fadeIn(100);
		}
		
		// Export is visible for admin in all views and for user in My Projects view
		if($('#selectedUserView').val() == 'My Projects' || USER_ROLE=="Admin"){
			$('#exportButton').show();
		}else{
			$('#exportButton').hide();
		}
		
		
		
});

// OPEN POPUP FOR EXPORT EXCEL TO SELECT BETWEEN CURRENT VIEW OR TOTAL MA&S FOR ADMIN
function openDownloadPopUp(){
	if(USER_ROLE.toLowerCase() != "admin"){
		exportExcelData();
	}
	else{
		$('#selectthebrand').show().fadeIn(100);
		$('#back').addClass('black_overlay').fadeIn(100);
		var ccVal = $('#getCostCenter').val();
		var val = $('#selectedUserView').val();
		if(val == 'My Brands'){
			$('#brandVal').show();
		selectedBrandValue = $('#getBrand1').val(); 
		$('#selectedBrandValue').text(selectedBrandValue);
		}else{
			$('#brandVal').hide();
		}
		$('#selectedCCValue').val(ccVal);
		$('input:radio[name=selectCC]')[0].checked = true;
		$('#back').addClass('black_overlay').fadeIn(100);
	}
}

//Handler for Create New Project button
$(document).on('click', '#crtNewProjBtn',
	    function() {
			createIntProjects();
	    }
);

// Handler for click on submit button under new project creation
$(document).on('click', '#submitProjBtn',
    function() {
		if(columnValiation==false){
			$('#submitProjBtn').prop("disabled",true);
		 	createNewProject();
		}else{
			columnValiation=false;
		}
    }
);

// Handler for click on cancel button under new project creation
$(document).on('click', '#cnclProjBtn',
	function() {
		cancelProjects();
	}
);

// Handler for search box 
$("#txtSearch").keyup(function(e) {
	map = {};
	Slick.GlobalEditorLock.cancelCurrentEdit();
	
	if (e.which == 27) {
		this.value = "";
	}
	searchString = this.value;
	searchString = searchString.replace(/</g, "&lt;");
	searchString = searchString.replace(/>/g, "&gt;");
	
    if (searchString != "") {
		dataView.expandGroup("Active");
		dataView.expandGroup("Closed");
		dataView.expandGroup("New");
	} else {
		
		dataView.collapseGroup("New");
		dataView.collapseGroup("Active");
		dataView.collapseGroup("Closed");
	}
	dataView.refresh();
	if(!isMatchPresent && searchString != ""){
		alert("No Search Results Found!");	
	}
	calculateTotal();
});

//Handler for radio button "Forecast" and "All"
rdoSelectedmode.change(function(e) {
	Slick.GlobalEditorLock.cancelCurrentEdit();
	var choice = this.value;
	if (choice == 'planned') {
		radioString = 'Forecast'
	} else {
		radioString = "All";
	}
	dataView.refresh();
});

//Handler for hide column check box
chkBoxHideColumns.change(function(e) {
	Slick.GlobalEditorLock.cancelCurrentEdit();
	if (this.checked) {
		grid.setColumns(hidecolumns);
	} else {
		grid.setColumns(columns);
	}
	dataView.refresh();
});

//On change of selected views Export is visible for admin in all views and for user in My Projects view
$('#selectedUserView').change(function() {
	if($('#selectedUserView').val() == 'My Projects'  || USER_ROLE =="Admin"){
		$('#exportButton').show();
	}else{
		$('#exportButton').hide();
	}
});

/** Start : EDIT PROJECT GRID SETTINGS **/
	var map = {};
	var idBrandMap = {};
	var selectedValue = "";
	
	var dataView;
	var grid;
	
	var addsave=0;
	
	var data = [];
	var m_data = [];
	
	var itemClicked;
	var popUpWindow;

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
	
	var multiBrandToSingle = false;
	var radioString = "All";
	var totalSize = 0;
	var numHideColumns = 6;
	var columnNames = [ "Status", "Project Name", "Brand", "$ in 1000's", "gMemori Id", "Project Owner",
	        			"Project WBS", "SubActivity", "Allocation %", "PO Number", "Vendor", "Units",
	        			"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
	        			"DEC", "Total", "Comments"];
	
	var noOfNew = 0;
	var noOfActive = 0;
	var noOfClosed = 0;
	
	var newExist=false;
	var activeExist=false;
	var closedExist=false;
	
	var frmStudy=false;
	var columnValiation=false;
	var lastKeyPressed;
	
	// Validate special character entry
	function specialCharValidator(value) {
		  if ((!/[^a-zA-Z0-9. -]/.test(value))) {
			  return {valid: true, msg: null};
		  }
		  else {
			  return {valid: false, msg: "Please enter valid characters."};
		  }
	}
	
	// Validate special character entry in Project WBS
	function projectWBSValidator(value) {
		  if ((!/[^a-zA-Z0-9.]/.test(value))) {
			  return {valid: true, msg: null};
		  }
		  else {
			  return {valid: false, msg: "Please enter valid characters."};
		  }
	}
	
	// Columns displayed when hide columns is unchecked
	var columns = [ 
		{ id : 1, name : columnNames[0], field : 30, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[1], field : 2, width : 150, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField},
		{ id : 3, name : columnNames[2], field : 6, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Auto},
		{ id : 4, name : columnNames[3], field : 11, width : 110, formatter : Slick.Formatters.cancelButton, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 5, name : columnNames[4], field : 0, width : 90, formatter : Slick.Formatters.gMemoriHyperLink },
		{ id : 6, name : columnNames[5], field : 1, width : 90},
		{ id : 7, name : columnNames[6], field : 3, width : 90, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField, validator: projectWBSValidator},
		{ id : 8, name : columnNames[7], field : 5, width : 90, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField, validator: specialCharValidator},
		{ id : 9, name : columnNames[8], field : 7, width : 90, editor : Slick.Editors.Text},
		{ id : 10, name : columnNames[9], field : 8, width : 90, editor : Slick.Editors.PONumberText, formatter : Slick.Formatters.poField},
		{ id : 11, name : columnNames[10], field : 10, width : 90, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField, validator: specialCharValidator},
		{ id : 12, name : columnNames[11], field : 49, width : 90, editor : Slick.Editors.Integer, formatter : Slick.Formatters.editableField},
		{ id : 13, name : columnNames[12], field : 12, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[13], field : 13, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[14], field : 14, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[15], field : 15, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[16], field : 16, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[17], field : 17, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[18], field : 18, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[19], field : 19, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[20], field : 20, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[21], field : 21, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[22], field : 22, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[23], field : 23, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[24], field : 24, width : 90, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 26, name : columnNames[25], field : 25, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	}];

	//Columns displayed when hide columns is checked
	var hidecolumns = [ 
		{ id : 1, name : columnNames[0], field : 30, width : 120, editor : Slick.Editors.Text}, 
		{ id : 2, name : columnNames[1], field : 2, width : 150, editor : Slick.Editors.Text, formatter : Slick.Formatters.editableField},
		{ id : 3, name : columnNames[2], field : 6, width : 90, formatter : Slick.Formatters.HyperLink, editor : Slick.Editors.Auto},
		{ id : 4, name : columnNames[3], field : 11, width : 110, formatter : Slick.Formatters.cancelButton, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 5, name : columnNames[4], field : 0, width : 90, formatter : Slick.Formatters.gMemoriHyperLink },
		{ id : 6, name : columnNames[5], field : 1, width : 90},
		{ id : 13, name : columnNames[12], field : 12, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 14, name : columnNames[13], field : 13, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 15, name : columnNames[14], field : 14, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 16, name : columnNames[15], field : 15, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 17, name : columnNames[16], field : 16, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 18, name : columnNames[17], field : 17, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 19, name : columnNames[18], field : 18, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 20, name : columnNames[19], field : 19, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 21, name : columnNames[20], field : 20, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 22, name : columnNames[21], field : 21, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 23, name : columnNames[22], field : 22, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 24, name : columnNames[23], field : 23, width : 90, editor : Slick.Editors.FloatText, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 25, name : columnNames[24], field : 24, width : 90, formatter : Slick.Formatters.DollarSymbol, groupTotalsFormatter : sumTotalsFormatter},
		{ id : 26, name : columnNames[25], field : 25, width : 200, editor : Slick.Editors.LongText, formatter : Slick.Formatters.Remark
	}]
	
	var options = {
		editable : true,
		enableAddRow : true,
		enableCellNavigation : true,
		asyncEditorLoading : false,
		autoEdit : true,
		frozenColumn : 3,
		enableColumnReorder: false
	};

	/** GRID SETTINGS **/

	/** MULTI BRAND GRID SETTINGS **/
	  var m_grid;
	  
	  var m_options = {
	    editable: true,
	    enableAddRow: true,
	    enableCellNavigation: true,
	    asyncEditorLoading: false,
	    autoEdit: true,
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
	  
	/** MULTI BRAND GRID SETTINGS **/
	  function refreshSummary(){
			var val = $('#selectedUserView').val();
			var ccVal = $('#getCostCenter').val();
			selectedBrandValue = document.getElementById("brandType").value;
			$.ajax({
				url : '/GetSummaryFromCache',
				type : 'POST',
				dataType : 'text',
				data : {costCentre: ccVal,
					brand : selectedBrandValue},
				success : function(result) {
					summaryResult = result;
					getSummaryValues();
				}
			});
		}
	  
	  
	function getBrandTotals(){
		selectedValue = document.getElementById("brandType").value;
		var ccVal = $('#getCostCenter').val();
		$.ajax({
			url : '/GetSummaryFromCache',
			type : 'POST',
			dataType : 'text',
			data : {costCentre: ccVal,
				brand : selectedValue},
			success : function(result) {
				summaryResult = result;
				getSummaryValues();
			}
		});
	}  
  
/************** START : GROUP THE PROJECTS BY PLANNED, ACTIVE, CLOSED AND TOTAL *************/
noOfNew = 0;
noOfActive = 0;
noOfClosed = 0;
var newArr = [];
var ActiveArr = [];
var ClosedArr = [];

// Used to keep track of number of projects in each group
var uniqueProjects = [];

// If searched project is presEnt
var isMatchPresent = false;

function groupByStatus() {
	dataView
			.setGrouping([{
				getter : 26,
				formatter : function(g) {
			    	  for(var cnt = 0; cnt < g.rows.length; cnt++){
			    		  var gmemID = "";
			    		  if(g.rows[cnt][0] != 'undefined'){
			    			  gmemID = g.rows[cnt][0];
			    		  }
			    		  if(gmemID.toString().trim() != ""){
			    			  var value = g.rows[cnt][0];
			    			  if(value.toString().indexOf(".") > -1){
			    				  value = value.split(".")[0];
			    			  }
			    			  if(g.rows[cnt][26] == "New"){
			    				  newArr.push(value);
			    			  }
			    			  if(g.rows[cnt][26] == "Active"){
			    				  ActiveArr.push(value);
			    			  }
			    			  if(g.rows[cnt][26] == "Closed"){
			    				  ClosedArr.push(value);
			    			  }
			    		  }
			    	  }
			    	  
			    	  // Count unique projects
			    	  uniqueProjects = [];
			    	  $.each(newArr, function(i, el){
						    if($.inArray(el, uniqueProjects) === -1) uniqueProjects.push(el);
					  });
			    	  noOfNew = uniqueProjects.length;
			    	  
			    	  uniqueProjects = [];
						$.each(ActiveArr, function(i, el){
						    if($.inArray(el, uniqueProjects) === -1) uniqueProjects.push(el);
						});
			    	  noOfActive = uniqueProjects.length;
					
			    	  uniqueProjects = [];
						$.each(ClosedArr, function(i, el){
						    if($.inArray(el, uniqueProjects) === -1) uniqueProjects.push(el);
						});
					  noOfClosed = uniqueProjects.length;
					  
					  newArr = [];
					  ActiveArr = [];
					  ClosedArr = [];
					  
					  if((noOfNew > 0 || noOfActive > 0 || noOfClosed > 0) && searchString != ""){
						  isMatchPresent = true;
					  }else{
						  isMatchPresent = false;
					  }
					  
					  
					  if (g.value == "Total") {
						return "<span style='color:green'>"	+ 'Overall' + "</span>";
					  } 
						else if (g.value == "New"){
							var returnStr =  "" + 'Planned' + "<span style='color:green'>(" + noOfNew + " items)</span>" ;
							if(frmStudy == false){
								returnStr +=  "&nbsp;&nbsp;<input type='button' style='font-size: 12px; height: 25px; width: 120px; background:#2271B0; color:#FFFFFF' value='Create Projects' id='crtNewProjBtn'/>";
							}
							return returnStr;
						} 
						else if (g.value == "Active"){
							return "" + g.value	+ "  <span style='color:green'>(" +     noOfActive + " items)</span>";
						}
					else if (g.value == "Closed"){
						return "" + g.value + "<span style='color:green'>(" + noOfClosed + " items)</span>";
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
								new Slick.Data.Aggregators.Sum("24") ],
								aggregateCollapsed : true,
								lazyTotalsCalculation : true
				},
				
				{
			      getter: 42,
			      formatter :function (g) {
			      if(typeof g.value === 'undefined'){
			    	  return "";
			      }
			      else if( g.value.toString().split("::")[1].trim().length == 6){
			        return  g.value;
			      }else{
			    	  return g.value.toString().split("::")[0].trim();
			      }
			      },
			      lazyTotalsCalculation: true
			    }
			
			
			]);
			dataView.collapseGroup("New");
			dataView.collapseGroup("Active");
			dataView.collapseGroup("Closed");
}
/************** END : GROUP THE PROJECTS BY PLANNED, ACTIVE, CLOSED AND TOTAL *************/


/***************************** START: GROUPED TOTAL ACCORDING TO STATUS *********************************/
function sumTotalsFormatter(totals, columnDef) {
	var val = totals.sum && totals.sum[columnDef.field];
	if(columnDef.field==11 && totals['group']['value'].toLowerCase() != 'total'){
		return "<span style='color:rgb(168, 39, 241)'>" + "Totals (Forecast)"
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
/***************************** END: GROUPED TOTAL ACCORDING TO STATUS **********************************/




/*********************************** START: SEARCH PROJECT ************************************/
function searchProject(item) {
      var status = true;
      if (item[33] != "New") {
              status = false;
      }
      var arr = [];
      if(item[37] == true && item[35] != 'NewProjects'){
      	arr =  item[53].slice(0);;
      	var limit = arr.length;
      	for (var i = 0; i < limit; i++) {
      		arr[i] = idBrandMap[arr[i]];
      	}
      }
      if ((((item[27].toString().length > 9) || searchString != "" && item[27].toLowerCase().indexOf(
                      searchString.toLowerCase()) == -1)
                      && (searchString != "" && ((item[37] == true && arr.join().toLowerCase().indexOf(
                              searchString.toLowerCase()) == -1) || ((item[37] != true  && item[28].toLowerCase().indexOf(
                                      searchString.toLowerCase()) == -1))))
                      && (searchString != "" && item[29].toLowerCase().indexOf(
                                      searchString.toLowerCase()) == -1)
                      && (searchString != "" && item[32].toLowerCase().indexOf(
                                      searchString.toLowerCase()) == -1)
                      && (searchString != "" && item[30].toLowerCase().indexOf(
                                      searchString.toLowerCase()) == -1) && item[26] != "Total")
                      || (radioString != "All" && item[40] !="undefined" && item[40].toLowerCase() != 
                                      radioString.toLowerCase())) {
              return false;
      }else{
      	map[item[27] + ":" + item[11]] = item;
      }
      if (item.parent != null) {
              var parent = data[item.parent];
              while (parent) {
                      if (parent._collapsed
                                      || (((item[27].toString().length > 9) || searchString != "" && parent[27].toLowerCase()
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
/*********************************** END: SEARCH PROJECT ************************************/

/*********************************** START: INITIATE A PROJECT ****************************/
function do_the_ajax_call(){
	var gMemoriId ;
	$.ajax({
		beforeSend: function(msg){
			$('#back').addClass('black_overlay').fadeIn(100);
			$('#loader_image').show().fadeIn(100);
		},
		url : '/initiateProject',
		type : 'GET',
		async: true,
		dataType : 'text',
		data : {ccId: itemClicked[47],
			unixId: itemClicked[48],
			prj_name:itemClicked[2],
			dummyGMemId:itemClicked[0]
		},
		success : function(result) {
			var obj = $.parseJSON(result);
			var statusCode = obj.statusCode;
			if(statusCode == 200){
				
				gMemoriId = obj.newGMemId;
				openPopUp =  true;
				window.location.reload(true);
				window.open ("https://memori-dev.appspot.com/initiateProject?gMemoriId="+gMemoriId,'gmemori','');
				
			}else{
				if(obj!=null && obj.statusMessage!=null){
				alert("Error occured during synchronization with Study : \n"+obj.statusMessage);
				}else{
					alert("Error occured during synchronization with Study : \n Internal error occured.");
				}
				$('#back').removeClass('black_overlay').fadeIn(100);
				$('#loader_image').hide();
			}
			
		},
		error : function(result){
			alert("Error occured during synchronization with Study : \n Internal error occured.");
			$('#back').removeClass('black_overlay').fadeIn(100);
			$('#loader_image').hide();
		}
	});
			
}
/*********************************** END: INITIATE A PROJECT **********************************/

/*********************************** START: BLUR DISPLAY ON WINDOW RESIZE BELOW MINIMUM ****************************/
var resized = false;
$(window).resize(function() {
	grid.resizeCanvas();
	if($(window).width() < 900){
		$('#cautionWindow').show().fadeIn(100);
		$('#back').addClass('black_overlay').fadeIn(100);
		resized = true;
	}else{
		$('#cautionWindow').hide();
		if(resized){
			$('#back').removeClass('black_overlay').fadeIn(100);
			resized = false;
		}
	}
});
/*********************************** END: BLUR DISPLAY ON WINDOW RESIZE BELOW MINIMUM****************************/




function getSummaryValues(){
	var obj = $.parseJSON(summaryResult);
	var value;
	var varianceTotal;
	if(obj==null){
		getBrandTotals();
	}else{
		selectedValue = document.getElementById("brandType").value;
		for(var key in obj.budgetMap){
			if(key==selectedValue){
				value = obj.budgetMap[key];
				$('#totalBudget').text((value.totalBudget).toFixed(2));
				$('#plannedTotal').text((value.plannedTotal).toFixed(2));
				$('#budgetLeftToSpend').text(((value.totalBudget).toFixed(2) - (value.plannedTotal).toFixed(2)).toFixed(2));
				$('#accrualTotal').text((value.accrualTotal).toFixed(2));
				$('#varianceTotal').text((value.budgetLeftToSpend).toFixed(2));
				if((value.varianceTotal).toFixed(2)/(value.totalBudget).toFixed(2) *100 == 0){
					$(varTotalLabel).css('background-color', '#FFFFFF');
					$(varTotalText).css('background-color', '#FFFFFF');
				}
				else if((value.varianceTotal).toFixed(2)/(value.totalBudget).toFixed(2) *100 < 5){
					$(varTotalLabel).css('background-color', 'yellow');
					$(varTotalText).css('background-color', 'yellow');
				}else{
					$(varTotalLabel).css('background-color', '#00FFFF');
					$(varTotalText).css('background-color', '#00FFFF');
				}
			}
		}
	}
}



/*********************************** START: CALCULATING TOTAL ****************************/

function calculateTotal(){
	var accrualTotalItem, forecastTotalItem, quarterlyTargetTotalItem, quarterlyLTSTotalItem;
	for (var key in map) {
		if(map[key][26] == "Total"){
			if(key.split(":")[1].trim() == "Forecast"){
				 forecastTotalItem = map[key];
				 for(var i =0; i<=12; i++){
					 forecastTotalItem[i + 12] = 0.0; 
				 }
			}else if(key.split(":")[1].trim() == "Accrual"){
				accrualTotalItem = map[key];
				 for(var i =0; i<=12; i++){
					 accrualTotalItem[i + 12] = 0.0; 
				 }
			}else if(key.split(":")[1].trim() == "Quarterly Target"){
				quarterlyTargetTotalItem = map[key];
				for(var i =0; i<=12; i++){
					quarterlyTargetTotalItem[i + 12] = 0.0; 
				}
			}else if(key.split(":")[1].trim() == "Quarterly LTS"){
				quarterlyLTSTotalItem = map[key];
				for(var i =0; i<=12; i++){
					quarterlyLTSTotalItem[i + 12] = 0.0; 
				}
			}
		}
     }
			
	// Recalculation of total according to search criteria
	var prevKey = "";
	var forecastTotalItem, accrualTotalItem, quarterlyLTSTotalItem, quarterlyLTSTotalItem ;
	for (var key in map) {
		if((prevKey=='') || (key.split(":")[0].trim() == prevKey) || (key.split(":")[0].trim() != prevKey && key.split(":")[0].trim().indexOf(prevKey) == -1 )){// match with previous gmemId
		if(map[key][26] != "Total"){
				if(key.split(":")[1].trim() == "Forecast"){
					for(var i =0; i<=12; i++){
						forecastTotalItem[i + 12] = parseFloat(forecastTotalItem[i + 12]) + parseFloat(map[key][i + 12]);
					}
				}
				else if(key.split(":")[1].trim() == "Accrual" && map[key][26] != 'New'){
					for(var i =0; i<=12; i++){
						accrualTotalItem[i + 12] = parseFloat(accrualTotalItem[i + 12]) + parseFloat(map[key][i + 12]);
					}
				}
				else if(key.split(":")[1].trim() == "Quarterly Target"){
					for(var i =0; i<=12; i++){
						quarterlyTargetTotalItem[i + 12] = parseFloat(quarterlyTargetTotalItem[i + 12]) + parseFloat(map[key][i + 12]);
					}
				}
				if(key.split(":")[0].trim().indexOf(".") == -1){// doesnt contain dot
					prevKey = key.split(":")[0].trim();
				}  
			}
		}
   	}
	if(radioString == 'All'){
		for(var i =0; i<=12; i++){
			quarterlyLTSTotalItem[i + 12] = parseFloat(quarterlyTargetTotalItem[i + 12]) - parseFloat(accrualTotalItem[i + 12]);
		}
	}
	prevKey = "";
	grid.invalidate();
}
/*********************************** END: Calculating total ****************************/

/*********************************** START: GETTING DATA ON CHANGING COSTCENTER, BRAND AND VIEW  ****************************/

//on change of select view
function selectUserView(){
	$('#getDataForm').submit();
}

// on change of cost center
function getCostCenterDetails(){
	$('#getDataForm').submit();
}

//on change of brand
function getProjectsBrandwise(){
	getAvailableTags();
	$('#getDataForm').submit();
} 
/*********************************** END: GETTING DATA ON CHANGING COSTCENTER, BRAND AND VIEW  ****************************/



//Create a new project
function createIntProjects(){
	searchString = "" ;
	$("#txtSearch").val(searchString);
	dataView.expandAllGroups();
	dataView.collapseGroup("Active");
	dataView.collapseGroup("Closed");
	if(newExist == false){
		dataView.deleteItem("id_0");
		newExist = true;
		dataView.expandGroup("New");
	}
	$('#displayGrid').show();
	$('#topCrtNewProjBtn').hide();
	$('#noData').hide();
	var length= data.length;
	var item ={id:"id_"+length+1,indent:0,0:"",1:USER_NAME,2:"",3:"",4:"",5:"",6:"",7:"100.0",8:"",9:"",10:""
		,11:"Forecast",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:"",21:"",22:"",23:"",24:"",25:"",26:"New"
			,27:"",28:"",29:"",30:"",31:"",32:"",33:"New",34:"New projects",35:"NewProjects",37:false,38:"",39:"",40:"Forecast"};
	
	dataView.insertItem(0,item);

	//Project Name Field highlighted when user clicks on Create a Project Button
	grid.gotoCell(2, "1", false);
	
	if(addsave ==0){
	    var saveClose ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"Save",7:"",8:"",9:"",10:""
					,11:"Cancel",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
						,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
							,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Forecast"};
		var item2 ={id:"id_"+length+6,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
					,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
						,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
							,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Forecast"};
		var item3 ={id:"id_"+length+3,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
					,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
						,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
							,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Forecast"};
	
		dataView.insertItem(1,item3);
	    dataView.insertItem(2,saveClose);
	    dataView.insertItem(3,item2);
	}
    addsave=addsave+1;
    dataView.refresh(); 
    data=dataView.getItems();
}

//inserts dummy closed projects
function dummyClosedProjects(){
	var length= data.length;
	var iPlace=length-1;
	var item4 ={id:"id_"+length,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
			,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Closed",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Forecast"};
	
	if(closedExist==false){
		data[++iPlace] = item4;
	}
	closedExist = true;
}

//inserts dummy active projects
function dummyActiveProjects(){
	var length= data.length;
	var iPlace=length-1;
	
	var item5 ={id:"id_"+length,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
		,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Active",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Forecast"};
	if(activeExist==false){
		data[++iPlace] = item5;
	}
	activeExist = true;
}

//inserts dummy new projects
function dummyNewProjects(){
	var length= data.length;
	var item ={id:"id_"+length,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
			,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Forecast"};
	dataView.insertItem(0,item);
    dataView.refresh(); 
    data=dataView.getItems();
    newExist = true;
}

////////////////////////////////////////////// END: MULTI BRAND //////////////////////////////////////////////

// Save and close multi brand projects
function saveAndClose() {
	var errStr = "";
	var i = 0;
	for (i = 0; i < m_data.length; i++) {
		var d = m_data[i];
		errStr = "";
		
		if (m_data[i][4] != "" && m_data[i][4] != "undefined") {
			m_data[i][7]=itemClicked[1];
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

	availableTags.splice(0, 0, "Smart WBS");

	for (var i = 0; i < m_data.length; i++) {

		if ((m_data[i][4] == "" || m_data[i][4] == "undefined")
				&& m_data[i][1] != "") {
			m_data[i][4] = m_data[0][4];
		}
	}
	var isValidData = validateUserAndBrand();
	var isUserAndBrandAlreadyExists = validateUserAndBrandExists();
	if(isValidData == true){
		return;
	}if(isUserAndBrandAlreadyExists == true){
		return;
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
					total = parseFloat(total) + parseFloat(d[3]);
				} else {
					break;
				}
			}
			break;
		}
	}
	var d = new Date();
	var currentMonth = d.getMonth();
	
	itemClicked[51] = total;
	for(i = 12 + currentMonth; i<24; i++){
		itemClicked[i] = 0.0;
	}
	grid.invalidate();
	var costCenter = $('#getCostCenter').val();
	if (itemClicked["34"] != "New projects") {
		$.ajax({
			beforeSend: function(msg){
				if(disableGrid){
					$('#back').addClass('black_overlay').fadeIn(100);
					$('#loader_image').show().fadeIn(100);
				}
		    },
			url : '/multiBrandServlet',
			type : 'POST',
			dataType : 'json',
			data : {
				objarray : JSON.stringify(m_data),
				costCenter : costCenter,
				sumTotal : total
			},
			success : function(result) {
				alert('Project(s) created successfully!!!');
				isMultiBrand = false;
				window.location.reload(true);
			},
			error: function(result) {
				$('#back').removeClass('black_overlay').fadeIn(100);
				$('#loader_image').hide();
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


// Validates Brands for particular user
function validateUserAndBrand(){
	var prj_owner = "";
	var brand = "";
	var res = "";
	var flag = false;
	for(var i=0;i<m_data.length;i++){
		prj_owner = m_data[i]["7"];
		brand = m_data[i]["1"];
		res ="";
		for (var j = 0; j < ccUsersVar.length; j++) {
			if (ccUsersVar[j][0] == prj_owner) {
				res = ccUsersVar[j][1].substring(1,
						ccUsersVar[j][1].length - 1);
				break;
			}
		}
		if(res.toString().indexOf(brand) == -1){
			alert("User : "+ m_data[i]["7"] +" is not assigned to the brand : "+m_data[i]["1"]);
			flag = true;
			return flag;
		}
	}
	return flag;
}

//Validates Brands for particular user
function validateUserAndBrandExists(){
	var prj_owner = "";
	var brand = "";
	var res = "";
	var flag = false;
	var arr = [];
	for(var i=0;i<m_data.length;i++){
		prj_owner = m_data[i]["7"];
		brand = m_data[i]["1"];
		if(prj_owner!='' && brand!=''){
		res =prj_owner.trim()+":"+brand.trim();
		}else{
			break;
		}
		arr[i] = res;
	}
	var sortedarr = arr.sort();
	var results = [];
	var isDuplicate = false;
	for(var i=0;i<m_data.length;i++){
		if( (!(typeof sortedarr[i] === 'undefined')) && (!(typeof sortedarr[i+1] === 'undefined'))){
			if( sortedarr[i].trim() !='' && sortedarr[i+1].trim() != '' && sortedarr[i].trim() == sortedarr[i+1].trim() ){
				results.push(sortedarr[i]);
				isDuplicate = true;
			}
		}
	}
	if(isDuplicate == true){
		alert(results+": Duplicate Project owner and Brand combination.");
	}
	return isDuplicate;
}


// Close multibrand grid without saving
function closeWithoutSave() {
	var userAccepted = confirm(" Warning: The brand allocations if entered will not be saved by the system. \n\nDo you want to continue?");
	if (!userAccepted) {
		return false;
	}
	
	if(itemClicked[37] == false){
		itemClicked[6] = itemClicked[44];
	}
	
	if(itemClicked[34]=="New projects"){
		itemClicked[6] = "";
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
	availableTags.splice(0, 0, "Smart WBS");
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
	grid.invalidate();
}

// Add new row to multi brand popup
function addNewRow(){
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
	initMData[9] = "";
	m_grid.invalidate();
	m_grid.invalidateRow(m_grid.getSelectedRows());
	m_grid.updateRowCount();
	m_grid.render();
	m_grid.invalidate();
			
}
////////////////////////////////////////////// START : MULTI BRAND //////////////////////////////////////////////

function closepopup(){
	$('#selectthebrand').hide();
	$('#back').removeClass('black_overlay').fadeIn(100);
}




function exportExcelData(){
	var val = $('#selectedUserView').val();
	var ccVal = $('#getCostCenter').val();
	var brandValue = $('#getBrand1').val();
	if($('input:radio[name=selectCC]:checked').val() == 1){
		onClickAsynch();
	}
	else{
		var multiBrandCnt=true;
		for(var cntData = 0; cntData < data.length; cntData++){
			var cntD = data[cntData];
			if(cntD[0]!=null && cntD[0]!=''){
				multiBrandCnt=false;
				break;
				}
			}
		if(multiBrandCnt==true){
			alert("No data to export !!!");
			return;
		}else{
			$('#objArrayId').val(JSON.stringify(data,null,4));
			$('#ccId').val($('#getCostCenter').val());
		    document.getElementById('exportExcel').submit();
		}
	}
	closepopup();
}

/********************************* START: CREATE NEW PROJECT IF NO PROJECT PRESENT *********************************/
function createNewProjects(){
	$('#displayGrid').show();
	$('#topCrtNewProjBtn').hide();
	$('#noData').hide();
	var length= data.length;
	var item ={id:"id_"+length+1,indent:0,0:"",1:USER_NAME,2:"",3:"",4:"",5:"",6:"",7:"100.0",8:"",9:"",10:""
		,11:"Forecast",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
			,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"New projects",35:"NewProjects",37:false,38:"",39:"",40:"Forecast"};
	dataView.insertItem(0,item);
if(addsave ==0){
    var saveClose ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"Save",7:"",8:"",9:"",10:""
				,11:"Cancel",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Forecast"};
	var item2 ={id:"id_"+length+6,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Forecast"};
	var item3 ={id:"id_"+length+3,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"Forecast"};
	var item4 ={id:"id_"+length+4,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
			,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Closed",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Forecast"};
	var item5 ={id:"id_"+length+5,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
		,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Active",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"Forecast"};
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
/********************************* END: CREATE NEW PROJECT IF NO PROJECT PRESENT *********************************/

/********************************* START:  DISABLE PROJECT *********************************/

function deleteSelectedProjects() {
	
	var pLength = m_data.length;
	var noProjToDelete = true;
	
	if(itemClicked[1]== USER_NAME || USER_ROLE =="Admin" ){
		for (var count = 0; count < m_data.length; count++) {
			if (m_data[count]["8"] != 'undefined' && m_data[count]["8"] == true) {
				var userAccepted = confirm("Selected project(s) will be deleted. Want to continue?");
				if (!userAccepted) {
					return false;
				}
				m_data.splice(count--, 1);
				noProjToDelete = false;
			}
		}
		 if (noProjToDelete) {
			alert("Please select project(s) to delete.");
		}
	}else{
		alert("You are not Authorised to delete this Project");
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
/********************************* END:  DELETE PROJECT *********************************/
function initDeletionCell(row) {
	
	for (var count = 0; count < m_data.length; count++) {
		var thisId = "#" + count + "chkBox";
		if ($(thisId).is(':checked')) {
			m_data[count]["8"] = true;
		}
	
		else {
			m_data[count]["8"] = false;
		}
	}
}

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

function cancelProjects(){
	var result = confirm("Are you sure you want to cancel the projects you created?");
	if(result){
	window.location.reload(true);
	}else{
		return;
	}
}


/********************************* START:  CREATE A NEW PROJECT *********************************/
function createNewProject(){
	var errStr = 0;
	var storeData=[];
	var flag = false;
	var msg = "";
	for(var i=0;i<addsave;i++){

		if( data[i][2] == 'undefined' || data[i][2].toString().trim() ==""){
			errStr += 2;
		}
		if( data[i][6] == 'undefined' || data[i][6].toString().trim() ==""){
			errStr += 4;	
		}
		if(data[i][8] != 'undefined' && data[i][8]!=""){
			for(var j=0; j<i;j++){
				if(data[j][8] == data[i][8]){
					errStr += 5;
				}
			}
		}
		if(data[i][6].toString().toLowerCase().indexOf("smart wbs")!=-1 && data[i][37] == false){
			flag=true;
			break;
		}
		if(data[i][6].toString().toLowerCase().indexOf("smart wbs")!=-1 && data[i][37] == true){
			var total = 0;
			var totalMonthly = 0;
			for(var count = 0; count < data[i][36].length; count++){
				if(data[i][36][count][7] != ""){
					if(data[i][36][count][3] == "" || data[i][36][count][3] == "undefined"){
						data[i][36][count][3] = 0.0;
					}
					total = parseFloat(total) + parseFloat(data[i][36][count][3]); 
				}
			}
			
			for(var count = 12; count < 24; count++){
				if(data[i][count] == "" || data[i][count] == "undefined"){
					data[i][count] = 0.0;
				}
				totalMonthly = parseFloat(totalMonthly) + parseFloat(data[i][count]);
			}
			
			if(totalMonthly < total){
				var apnd = ", ";
				if(msg == ""){
					apnd = "";
				}
				msg += apnd + data[i][2].toString();
			}
			
		}
		
		switch(errStr) {
			case 0:
		        break;
		    case 2:
		    	 alert('"Project name" can not be blank.');
		    	 grid.gotoCell(i+2, PROJECT_NAME_CELL, false);
		    	break;
		    case 4:
		    	alert('"Brand" can not be blank.');
		    	grid.gotoCell(i+2,  BRAND_CELL, false);
		        break;
		    case 5:
		    	alert('Po Number cannot be same');
		    	grid.gotoCell(i+2, PO_NUMBER_CELL, false);
		        break;
		    case 6:
		    	alert('"Project name" and "Brand" can not be blank.');
		    	grid.gotoCell(i+2, PROJECT_NAME_CELL, false);
		        break;
		}
		storeData[i]=data[i];
		if(errStr != 0){
			break;
		}
	}

	if(msg != ""){
		alert("For project(s) \""+ msg + "\" entered forecast total is less than allocated total.");
	}
	
	if(flag == true){
		alert("Please add sub-projects to your multibrand project: "+ data[i][2]);
		return;
	}
	
	var costCenter = $('#getCostCenter').val();
	
	if(errStr == 0){
		 $.ajax({
			 beforeSend: function(msg){
				$('#back').addClass('black_overlay').fadeIn(100);
				$('#loader_image').show().fadeIn(100);
			},
			url : '/storereport',
			type : 'POST',
			dataType : 'json',
			data : {objarray: JSON.stringify(storeData),
				costCenter : costCenter},
			success : function(result) {
				alert('Project(s) created successfully!!!');
				storeData=[];
				window.location.reload(true);
			},
			error: function(result) {
				if(result["responseText"].toString().indexOf("<poError>:")!=-1){
					alert("PO Number already exists !!!");
				}
				else if(result["responseText"].toString().indexOf("java.lang.Error:")!= -1){
					alert(JSON.stringify(result["responseText"].toString().split("java.lang.Error:")[1].substring(1,38)));
				}else{
					alert("Unknow server error occured.");
				}
				$('#submitProjBtn').prop("disabled",false);
				$('#back').removeClass('black_overlay').fadeIn(100);
				$('#loader_image').hide();
	        }
		}); 
		
	}else{
		$('#submitProjBtn').prop("disabled",false);
	}
}
/********************************* END:  CREATE A NEW PROJECT *********************************/

/********************************* START: DISABLE PROJECT *********************************/
function deleteCurrentProject(delBtnClicked){
	var gmemId = delBtnClicked.value.split('~')[0];
	var projectOwner = delBtnClicked.value.split('~')[1];
	var projectCreateDate = new Date(delBtnClicked.value.split('~')[2].split("_"));
	var projStatus = delBtnClicked.value.split('~')[3];
	var project_Name = delBtnClicked.value.split('~')[4];
	if(USER_ROLE.toLowerCase() == "project owner"){
		console.log("Not an admin...");
		if(projectOwner != USER_NAME){
			console.log("ProjectOwner : " + projectOwner + ". Logged in user :" + USER_NAME);
			alert("You are not authorized to delete the project.");
			return;
		}
		if(projStatus != "New" ){
			alert("PO exists and the project cannot be deleted.");
			return;
		}
		
		var projCreatedQtr = Math.floor(projectCreateDate.getMonth() / 3);
		if(projectCreateDate < cutOffDate){
			alert('Benchmark exists and the project cannot be deleted.');
			console.log( "Cut Off date is "+cutOffDate+", and  project create date is "+projectCreateDate+". You can not delete the project, as the project has locked benchmark.");
			return;			
		}
		else{
			console.log( "Cut Off date is "+cutOffDate+", and  project create date is "+projectCreateDate+". You can proceed to delete the project.");
		}
	}
	var userAccepted = confirm("Please, confirm: delete project?");
	if (!userAccepted) {
		return;
	}
	var ccVal = $('#getCostCenter').val();
	$.ajax({
		beforeSend: function(msg){
			$('#back').addClass('black_overlay').fadeIn(100);
			$('#loader_image').show().fadeIn(100);
	    },
		url : '/disableProject',
		type : 'POST',
		dataType : 'text',
		data : {gMem: gmemId, costCenter:ccVal, projectOwner:projectOwner},
		success : function(result) {
			if(result==null){
				alert("Error occured while deleting project!!!");
			}else{
			var obj = $.parseJSON(result);
			if(obj.statusCode==200){
				if(gmemId.length>=10){
					gmemId="#";
				}
			alert('Project ' + gmemId + ' ('+ project_Name +') successfully deleted.');
			window.location.reload(true);
			}else{
				$('#back').removeClass('black_overlay').fadeIn(100);
				$('#loader_image').hide();
				alert(obj.statusMessage);
			}
			}
		},		
		error : function(result){
			alert("Error occured while deleting project!!!");
			window.location.reload(true);
		}
	});
}
/********************************* END: DISABLE PROJECT *********************************/

/********************************* START: MULTI BRAND GRID EDIT *********************************/

function displayMultibrandGrid() {
		
	m_grid = new Slick.Grid("#multibrandGrid", m_data, m_columns, m_options);
	m_grid.setSelectionModel(new Slick.CellSelectionModel());
	m_grid.registerPlugin(new Slick.AutoTooltips());
	m_grid.getCanvasNode().focus();
	
	m_grid.onClick.subscribe(function(e, args) {
		m_grid.gotoCell(args.row, args.cell, false);
		if (args.cell == MB_CHECKBOX_CELL) {
			initDeletionCell(args.row);
		}
	});
	
	

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
	
	m_grid.onKeyDown.subscribe(function(e, args) {
		var cell = args.cell;
		var row = args.row - 1;
		var fixedCell = cell;
		if (e.which == 38 || e.which == 40 || e.which == 13) {
			if ($('#hideColumns').is(":checked")) {
				fixedCell = cell + numHideColumns;
			}
			data[row][fixedCell] = 0.0;
			updateTotals(cell, row, fixedCell, args);
			if (!m_grid.getEditorLock().commitCurrentEdit()) {
				return;
			}
			m_grid.invalidate();
			e.stopPropagation();
		}
	}); 

	m_grid.onBeforeEditCell.subscribe(function(e, args) {
		var cell = args.cell;
		var row = args.row;
		var pRow = row + 1;
		if(itemClicked[26] == "Closed"){
			return false;
		}
		if((m_data[row]["7"] .toString().trim() == "" || m_data[row]["1"] .toString().trim() == "") && cell == MB_$_IN_THOUSAND_CELL){
			return false;
		}
		if ((args.item[0].toString().trim() != "" && itemClicked[26] == "Active")
				|| (itemClicked[26] == "Closed")) {
			return false;
		}
		if (row != 0) {
			if (cell == MB_BRAND_CELL) {
				m_data[row]["4"]=itemClicked[2];
				if (m_data[row]["5"] == "") {
					m_data[row]["5"] = m_data[row - 1]["5"]
							.split(".")[0]
							+ "."
							+ (parseInt(m_data[row - 1]["5"]
									.split(".")[1]) + 1);
					m_grid.invalidate();
				}
			}
			if ((m_data[row]["7"] == 'undefined' || m_data[row]["7"] == "")
					&& cell == MB_BRAND_CELL) {
				m_data[row]["7"] = m_data[row - 1]["7"];
				m_grid.invalidate();
			}
		}
		return true;
	});

	

	m_grid.onCellChange.subscribe(function(e, args) {
		var cell = args.cell;
		var row = args.row;
		var isValidBrand = false;
		sum = 0.0;
		index = 0;
		percentSum = 0.0;
		var numOfBrands = 0;
		for (var count = 0; count < m_data.length; count++) {
			if(( m_data[count]["1"] != "" )
					&&   m_data[count]["1"] != "undefined"){
				numOfBrands=numOfBrands+1;
			}
		}
		if (cell == MB_$_IN_THOUSAND_CELL) {
				if(( args.item[3] != "" )
						&&   args.item[3] != "undefined"){
					if(parseFloat(args.item[3]) <=0 ){
						if(args.item[10]=="" || args.item[10] == undefined){
							args.item[3] = 0;
							args.item[2] = 0.0;
						}else{
						args.item[3] = args.item[10];
						args.item[2] = args.item[11];
						}
						alert("MB Total cannot be less than or equal to zero.");
						m_grid.invalidate();
						return;
					}
				}
			for (var count = 0; count < m_data.length; count++) {
				if(( m_data[count]["3"] != "" )
						&&   m_data[count]["3"] != "undefined"){
					sum = sum + parseFloat(m_data[count]["3"]);
					index = count;
				}
			}
			for (var count = 0; count < m_data.length; count++) {
				if(isNaN(parseFloat(m_data[count]["3"]))){
					m_data[count]["2"]="";
				}
				else if(!isNaN(m_data[count]["3"] / sum * 100)){
					if(count < numOfBrands-1){
					m_data[count]["2"] = parseFloat((m_data[count]["3"] / sum * 100)).toFixed(2);
														;
					}else{
						for(var count = 0;count < numOfBrands-1; count++){
							if(( m_data[count]["3"] != "" )
									&&   m_data[count]["3"] != "undefined"){
								percentSum = parseFloat(percentSum) + parseFloat(m_data[count]["2"]);
							}
						}
						m_data[count]["2"] = (100 - parseFloat(percentSum)).toFixed(2);
					}
				}else{
					m_data[count]["2"] = "0";
				}
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
				initMData[9] = "";
				m_grid.invalidate();
				m_grid.invalidateRow(m_grid.getSelectedRows());
				m_grid.updateRowCount();
				m_grid.render();
			} 
			m_grid.invalidate(); 
		}
		if (cell == MB_BRAND_CELL) {
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
				m_grid.gotoCell(row, MB_BRAND_CELL, true);
			}
			m_grid.invalidate();
		}

	});
}
/********************************* END: MULTI BRAND GRID EDIT *********************************/

