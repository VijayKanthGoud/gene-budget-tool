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


noOfNew = 0;
noOfActive = 0;
noOfClosed = 0;
var newArr = [];
var ActiveArr = [];
var ClosedArr = [];
var uniqueNames = [];
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
			    	  uniqueNames = [];
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
					  newArr = [];
					  ActiveArr = [];
					  ClosedArr = [];
					  if((noOfNew > 0 || noOfActive > 0 || noOfClosed > 0) && searchString != ""){
						  isMatchPresent = true;
					  }else{
						  isMatchPresent = false;
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
			      getter: 42,
			      formatter :function (g) {
			        return  g.value;
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

//Filter data according to search field
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
	
	if(cell <= <%=BudgetConstants.PROJECT_OWNER_CELL%>){
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
	 		}else if(key== d[34] && d[11]=="Planned" && ( fixedCell == <%=BudgetConstants.PROJECT_NAME_CELL%> || fixedCell == <%=BudgetConstants.PO_NUMBER_CELL%> || fixedCell == <%=BudgetConstants.PROJECT_WBS_CELL%> || fixedCell == <%=BudgetConstants.SUBACTIVITY_CELL%>	|| fixedCell == <%=BudgetConstants.VENDOR_CELL%> || fixedCell == <%=BudgetConstants.GMEMORI_ID_CELL%>)){
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
		 		}else if(fixedCell == <%=BudgetConstants.GMEMORI_ID_CELL%>){
		 		//alert(d[fixedCell - 4]);
		 		if(d[fixedCell - 4].toString().indexOf(".")!=-1){
		 			d[fixedCell - 4] = args.item[fixedCell - 4] +"." +d[fixedCell - 4].toString().split(".")[1]
		 		}else{
		 			d[fixedCell - 4] = args.item[fixedCell - 4];
		 		}
		 			aSave[1] = d[fixedCell - 4];
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
	//alert("as "+JSON.stringify(aSaveData));	
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
			if(cellNum == '-2' || cellNum == '-10' || cellNum == '-7'){
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

function submitProjects(){
	var errStr = 0;
	var storeData=[];
	var flag = false;
	for(var i=0;i<addsave;i++){

		/*if( data[i][0] == 'undefined' || data[i][0].toString().trim() ==""){
			errStr += 1;
		}*/
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
	  /*  case 1:
	    	alert('"gMemori ID" can not be blank.');
	        break;*/
	    case 2:
	    	alert('"Project name" can not be blank.');
	    	break;
	   /* case 3:
	    	alert('"Project name" and "gMemori ID" can not be blank.');
	        break;*/
	    case 4:
	    	alert('"Brand" can not be blank.');
	        break;
	   /* case 5:
	    	alert('"Brand" and "gMemori Id" can not be blank.');
	        break;*/
	    case 6:
	    	alert('"Project name" and "Brand" can not be blank.');
	        break;
	  /*  case 7:
	    	alert('"Project name", "Brand" and "Gmemori ID" can not be blank.');
	        break;*/
		}
		storeData[i]=data[i];
	}
	if(flag == true){
		alert("Please add sub-projects to your multibrand project: "+ data[i][2]);
		return;
	}
	//alert(JSON.stringify(storeData));
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
