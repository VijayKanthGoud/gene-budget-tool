var selectedValue = "";
var summaryResult = "";
var availableTags = [];
var poOwners=[];
var ccUsersVar=[];
function getAvailableTags(){
	if($('#selectedUserView').val() == 'My Brands'){
		$("#dropdown").show();
	}else{
		$("#dropdown").hide();	
	}
	document.getElementById("myIFrm").style.display="none";
	availableTags[0] = "Smart WBS";
	var j;
	<%for(int i=0;i<myBrands.length;i++){%>
		j=<%= i+1%>;
		availableTags[j] = '<%= myBrands[i]%>';
	<%}%>
} 

function getProjectsBrandwise(){
	getAvailableTags();
	//alert("getProjectsBrandwise()");
	var val = $('#selectedUserView').val();
	var ccVal = $('#getCostCenter').val();
	$('#selectedView3').val(val);
	//alert("$('#selectedUserView').val() = "+$('#selectedUserView').val());
	selectedBrandValue = $('#getBrand1').val(); 
	$('#getBrand3').val(selectedBrandValue);
	 $('#getCostCenter3').val(ccVal);
	//alert("selectedBrandValue()"+selectedBrandValue);
	/*$.ajax({
		url : '/getreport',
		type : 'GET',
		dataType : 'text',
		data : {brandValue: selectedBrandValue
		},
		success : function(result) {
			summaryResult = result;
			getSummaryValues();
		}
	});*/
	
	$('#getBrand').submit();
} 

function OpenInNewTab(url) {
	//alert("OpenInNewTab");
	  var win = window.open(url, 'initiate');
	  win.focus();
	}
function getBrandTotals(){
	selectUserView1();
	/*if($('#selectedUserView').val() == 'My Brands'){
		$("#dropdown").show();
	}else{
		$("#dropdown").hide();	
	}
	
	document.getElementById("myIFrm").style.display="none";
	selectedValue = document.getElementById("brandType").value;
	var ccVal = "";
	if(<%=userInfo.getRole().contains("Admin")%>){
		ccVal = $('#getCostCenter').val();
	}else{
		//ccVal = <%=userInfo.getSelectedCostCenter()%>;
		ccVal = $('#getCostCenter').val();
	}
	
	//alert("ccVal = "+ccVal);
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
	});*/
} 
/*function ValidateGMemoriId(gMemoriId){
	//var gMemoriId = document.getElementById("brandType").value; 
	
	var validationMsg = '';
	$.ajax({
		url : '/ValidateGMemoriId',
		type : 'POST',
		dataType : 'text',
		data : {gMemoriId: gMemoriId,
				costCenter: costCenter
		},
		success : function(result) {
			validationMsg = result;
			alert(validationMsg);
			//getSummaryValues();
		}
	});
	return validationMsg;
} */

function updateUserSession(){
	var costCenter = $('#getCostCenter').val();
	var validationMsg = '';
	$.ajax({
		url : '/updateUserSession',
		type : 'POST',
		dataType : 'text',
		data : {costCenter: costCenter
		},
		success : function(result) {
			validationMsg = result;
			//alert(validationMsg);
			//getSummaryValues();
		}
	});
	return validationMsg;
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
							+ 'Overall' + "</span>";
					  } 
						else if (g.value == "New"){
							var returnStr =  "" + 'Planned'
							+ "<span style='color:green'>("
							+ noOfNew + " items)</span>" ;
						
							if(frmStudy == false){
								returnStr +=  "&nbsp;&nbsp;<input type='button' style='font-size: 12px; height: 25px; width: 120px; background:#2271B0; color:#FFFFFF' value='Create Projects' id='crtNewProjBtn'/>";
							}
						return returnStr;
						} 
						else if (g.value == "Active"){
							return "" + g.value
							+ "  <span style='color:green'>("
							+     noOfActive + " items)</span>";
						}
					else if (g.value == "Closed"){
						return "" + g.value
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
	// if(newExist == false){
		dataView.collapseGroup("New");
	//}
	 dataView.collapseGroup("Active");
	 dataView.collapseGroup("Closed");
}

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

//Filter data according to search field
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

var forecast_cur = 0.0;
var accrual_cur = 0.0;
var quarterly_tar_cur = 0.0;

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
	var costCenter = item["47"];
	//console.log(item);
	//alert("costCenter = "+costCenter);
	if ($('#hideColumns').is(":checked")) {
		fixedCell = cell + numHideColumns;
	} else {
		fixedCell = cell;
	}
	
	if(cell <= <%=BudgetConstants.PROJECT_OWNER_CELL%>){
		fixedCell = cell;
	}
	var itemCell = fixedCell;
	
	if(fixedCell == <%=BudgetConstants.PO_NUMBER_CELL%>){
		var userAccepted = confirm("You have entered PO Number "+ args.item["8"] +". Want to continue?");
		if (!userAccepted) {
			args.item["8"]="";
			grid.invalidate();
	        grid.gotoCell(row, fixedCell, true);
	        $('#statusMessage').text("")
			.fadeOut(100);
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
	
	var qtrEditing =  Math.floor((fixedCell - 12) / 3);
	
	var cellValue ;
	if(cell == <%=BudgetConstants.BRAND_CELL%>){
	cellValue = item[6];
	}else{
		cellValue = item[itemCell];
	}
	var cellNum = fixedCell - 12;
	if(item[37]==true && item["11"] == "<%=BudgetConstants.ACCRUAL%>" && item[27].toString().indexOf(".") != -1){
		key = item[27];
	}else{
		key = item[34];
	}
	var aSaveData=[];
	var iCnt=0;
	var varTotal = 0.0;
	var singleBrandToMulti = false;
	//alert(fixedCell);
	//alert(cell+"::::"+cellValue)
	if( fixedCell == <%=BudgetConstants.REMARK_CELL%>){
			var aSave = (aSaveData[0] = {});
			aSave[0] = key;
			aSave[1] = cellValue;
 		//aSave[2] = d["47"];
		}else if(cell == <%=BudgetConstants.BRAND_CELL%>){
			if(cellValue.toString().toLowerCase().indexOf("smart wbs")!=-1){
				//alert(cellValue+"::::"+args.item[37] +"::::"+cellNum);
				<%
				//MemcacheService cacheCCJs = MemcacheServiceFactory.getMemcacheService();
				Map<String,ArrayList<String>> ccUsersJs = util.getCCUsersList(user.getSelectedCostCenter());%>
				// multi brand click
				
				var usr=0;
				var userCnt=0;
				<% 
				
				//Set<String> userListJs = ccUsersJs.keySet();
				for(Map.Entry<String,ArrayList<String>> userMapDetails: ccUsersJs.entrySet()){%>
				 poOwners[userCnt] = "<%=userMapDetails.getKey()%>";
				 var d = (ccUsersVar[userCnt] = {});
				 d[0]=   poOwners[userCnt];
				 d[1] = "<%=userMapDetails.getValue()%>";
				 
				 userCnt++;
				<%}%>
				if(args.item[37] == false){
					var index = availableTags.indexOf("Smart WBS");
					if (index > -1) {
						availableTags.splice(index, 1);
					}
					m_data[0][1]=args.item[44];
					m_data[0][3]=args.item[24];
					m_data[0][2]=100.0;
					m_data[0][4]=args.item[2];
				 	m_data[0][5]=args.item[0]+'.1';
				 	m_data[0][7]=args.item[1];
				 	singleBrandToMulti=true;
				 	$('#multibrandEdit').show().fadeIn(100);
					displayMultibrandGrid();
					$('#back').addClass('black_overlay').fadeIn(100);
				}
			}else{
				var aSave = (aSaveData[0] = {});
				aSave[0] = key;
	 		aSave[1] = cellValue;
	 		//aSave[2] = d["47"];
			}
			
		}else{
			for(var i=0;i<data.length;i++){
				var d = data[i];
				
				// Commented as Accrual is not editable as per current bussiness case
				/*if((key.toString().indexOf(".") != -1) && 
						key.split(".")[0]== d[27] && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && item[11]=='Accrual'){
					if(d[11]=="<%=BudgetConstants.ACCRUAL%>"){
						var aSave = (aSaveData[iCnt] = {});
						
						d[fixedCell] = parseFloat(d[fixedCell]) + parseFloat(item[fixedCell]) - parseFloat(item[50]);
						aSave[0] = d[27];
						aSave[1] =  d[itemCell]
						iCnt++;
						for(var iVar=0;iVar<data.length;iVar++){
							var kData = data[iVar];
							if(key.toString().indexOf(".") != -1 && 
									key.split(".")[0]== d[27]   && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && kData[11]=="<%=BudgetConstants.QUARTERLY_LTS%>"){
								kData[itemCell] = parseFloat(kData[itemCell])  - parseFloat(item[fixedCell]) + parseFloat(item[50]);
								break;
							}
						}
						var val = 0;
						for(var iVar=0;iVar<data.length;iVar++){
							var kData = data[iVar];
							if(key.toString().indexOf(".") != -1 && 
									key.split(".")[0] == d[27]   && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && kData[11]=="<%=BudgetConstants.ACCRUAL%>"){
								if(kData[27].toString().indexOf(".") != -1){
									val += parseFloat(kData[itemCell]);
								}
							}
						}
						for(var iVar=0;iVar<data.length;iVar++){
							var kData = data[iVar];
							if(key.toString().indexOf(".") != -1 && 
									key.split(".")[0] == d[27]   && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && kData[11]=="<%=BudgetConstants.FORECAST%>" ){
								if (kData[27].toString().indexOf(".") == -1){
									kData[itemCell] = parseFloat(val);
								}else if(kData[27] == key){
									kData[itemCell] = parseFloat(item[fixedCell]);
								}
								
							}
						}
					for(var iVar=0;iVar<data.length;iVar++){
						var kData = data[iVar];
						
						if(key.toString().indexOf(".") != -1 && key == kData[27] && fixedCell >= <%=BudgetConstants.JAN_CELL%> && 
								fixedCell <= <%=BudgetConstants.DEC_CELL%> && kData[11]=="<%=BudgetConstants.QUARTERLY_LTS%>"){
							aSave = (aSaveData[iCnt] = {});
							kData[itemCell] = parseFloat(kData[itemCell])  - parseFloat(item[fixedCell]) + parseFloat(item[50]);
							aSave[0] = kData[27];
							aSave[1] = cellValue;
							break;
						}
					}
					}
				}

				if(key== d[34] && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && item[11]=='Accrual'){
					if(d[11]=="<%=BudgetConstants.ACCRUAL%>"){
						d[itemCell]=parseFloat( parseFloat(d[41]) * parseFloat(cellValue) /100).toFixed(2);
					}else if(d[11]=="<%=BudgetConstants.QUARTERLY_LTS%>"){
						if( item[43]=='undefined' ||item[43]=="" ){
							item[43]=0.0;
						}
						for(var iVar=0;iVar<data.length;iVar++){
							var kData = data[iVar];
							if(key== kData[34] && fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && kData[11]=="<%=BudgetConstants.QUARTERLY_TARGET%>"){
								d[itemCell] = parseFloat(kData[itemCell])  - parseFloat(cellValue);
								break;
							}
						}
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
				} */
				if(key== d[34] && d[11]=="<%=BudgetConstants.FORECAST%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%>){
					forecast_cur =  d[itemCell];
					var aSave = (aSaveData[iCnt] = {});
					aSave[0] = d[27];
					if(d[7] == 0.0){
						d[7]=100.0;
					}
					// Commented as Accrual is not editable as per current bussiness case
					/*if(item[11]=='Accrual'){
						d[itemCell]=parseFloat(cellValue).toFixed(2);
					}*/
					aSave[1] = parseFloat( parseFloat(d[7]) * parseFloat(cellValue) /100).toFixed(2);
					//aSave[2] = d["47"];
					d[itemCell]=aSave[1];
					// Commented to remove restriction of calculating total for multibrand.
					//if(item[37]== false){
						varTotal = 0.0;
						for (var j = 12; j < 24; j++) {
							if(d[j] == "" || d[j] == "undefined"){
								d[j] = 0.0;
							}
							varTotal = parseFloat(varTotal)	+ parseFloat(d[j]);
						}
						d[24]= parseFloat(varTotal);
					//}
					iCnt++;
				}else if(key== d[34] && d[11]=="<%=BudgetConstants.FORECAST%>" && ( fixedCell == <%=BudgetConstants.PROJECT_NAME_CELL%> || fixedCell == <%=BudgetConstants.PO_NUMBER_CELL%> || fixedCell == <%=BudgetConstants.PROJECT_WBS_CELL%> || fixedCell == <%=BudgetConstants.SUBACTIVITY_CELL%>	|| fixedCell == <%=BudgetConstants.VENDOR_CELL%> || fixedCell == <%=BudgetConstants.UNIT_CELL%> || fixedCell == <%=BudgetConstants.GMEMORI_ID_CELL%>)){
					var aSave = (aSaveData[iCnt] = {});
					aSave[0] = d[27];
					if(fixedCell == <%=BudgetConstants.VENDOR_CELL%>){
						d[<%=BudgetConstants.VENDOR_FIELD%>] = args.item[<%=BudgetConstants.VENDOR_FIELD%>];
						aSave[1] = d[<%=BudgetConstants.VENDOR_FIELD%>];
					}else if(fixedCell == <%=BudgetConstants.SUBACTIVITY_CELL%>	){
						d[<%=BudgetConstants.SUBACTIVITY_FIELD%>] = args.item[<%=BudgetConstants.SUBACTIVITY_FIELD%>];
						aSave[1] = d[<%=BudgetConstants.SUBACTIVITY_FIELD%>];
					}else if(fixedCell == <%=BudgetConstants.PROJECT_WBS_CELL%>){
						d[<%=BudgetConstants.PROJECT_WBS_FIELD%>] = args.item[<%=BudgetConstants.PROJECT_WBS_FIELD%>];
						aSave[1] = d[<%=BudgetConstants.PROJECT_WBS_FIELD%>];
					}else if(fixedCell == <%=BudgetConstants.PO_NUMBER_CELL%>){
						d[<%=BudgetConstants.PO_NUMBER_FIELD%>] = args.item[<%=BudgetConstants.PO_NUMBER_FIELD%>];
						aSave[1] = d[<%=BudgetConstants.PO_NUMBER_FIELD%>];
					}else if(fixedCell == <%=BudgetConstants.PROJECT_NAME_CELL%>){
						d[<%=BudgetConstants.PROJECT_NAME_FIELD%>] = args.item[<%=BudgetConstants.PROJECT_NAME_FIELD%>];
						aSave[1] = d[<%=BudgetConstants.PROJECT_NAME_FIELD%>];
					}else if(fixedCell == <%=BudgetConstants.GMEMORI_ID_CELL%>){
						if(d[<%=BudgetConstants.GMEMORI_ID_FIELD%>].toString().indexOf(".")!=-1){
							d[<%=BudgetConstants.GMEMORI_ID_FIELD%>] = args.item[<%=BudgetConstants.GMEMORI_ID_FIELD%>] +"." +d[<%=BudgetConstants.GMEMORI_ID_FIELD%>].toString().split(".")[1]
						}else{
							d[<%=BudgetConstants.GMEMORI_ID_FIELD%>] = args.item[<%=BudgetConstants.GMEMORI_ID_FIELD%>];
						}
						aSave[1] = d[<%=BudgetConstants.GMEMORI_ID_FIELD%>];
					}else if(fixedCell == <%=BudgetConstants.UNIT_CELL%>){
						d[<%=BudgetConstants.UNIT_FIELD%>] = args.item[<%=BudgetConstants.UNIT_FIELD%>];
						aSave[1] = d[<%=BudgetConstants.UNIT_FIELD%>];
					}
					//aSave[2] = d["47"];
					iCnt++;
				}else if(key== d[34] && d[11]=="<%=BudgetConstants.QUARTERLY_TARGET%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active") && 
						((qtrEditing != '<%=qtr%>' ) || ( qtrEditing == '<%=qtr%>' && '<%=cutOfDate.after(new Date()) %>' =='true')  ))){
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
				if(key== d[34] && d[11]=="<%=BudgetConstants.QUARTERLY_TARGET%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active"))){
					quarterly_tar_cur = d[itemCell];
				}
				if(key== d[34] && d[11]=="<%=BudgetConstants.ACCRUAL%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active"))){
					accrual_cur = d[itemCell];
				}
				if(key== d[34] && d[11]=="<%=BudgetConstants.QUARTERLY_LTS%>" &&  fixedCell >= <%=BudgetConstants.JAN_CELL%> && fixedCell <= <%=BudgetConstants.DEC_CELL%> && ((d[26]=="New" || d[26]=="Active") )){
					d[itemCell]= parseFloat(quarterly_tar_cur - accrual_cur);
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
		
	if(singleBrandToMulti!=true){
	$.ajax({
		url : '/AutoSaveData',
		type : 'POST',
		dataType : 'text',
		data : {
			celNum : cellNum,
			objarray : JSON.stringify(aSaveData),
			costCenter : costCenter,
			mapType : item[11]
		},
		success : function(result) {
			//alert("success"+JSON.stringify(result));
			if(JSON.stringify(result).indexOf("<poError>")!=-1){
				alert("PO Number already exists !!!");
				window.location.reload(true);
			}
			$('#statusMessage').text("All changes saved successfully!")
					.fadeIn(200);
			$("#statusMessage");
			summaryResult = result;
			getSummaryValues();
			if(cellNum == '<%=BudgetConstants.CELL_PONUMBER%>' || cellNum == '<%=BudgetConstants.CELL_GMEMORI_ID%>' || cellNum == '<%=BudgetConstants.CELL_BRAND%>' || cellNum == '<%=BudgetConstants.CELL_PNAME%>'){
				window.location.reload(true);
			}
		},
		error : function(result){
			alert("gMemoriId already exists !!!");
			$('#statusMessage').text("")
			.fadeIn(200);
			$('#statusMessage');
			for(var i=0;i<data.length;i++){
				var d = data[i];
				if(key== d[34] && d[11]=="<%=BudgetConstants.FORECAST%>" && ( fixedCell == <%=BudgetConstants.GMEMORI_ID_CELL%>)){
				d["0"] = d["27"];
			}}
			grid.invalidate();
		}
	});
	}
	singleBrandToMulti=false;
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

function createNewProjects(){
	$('#displayGrid').show();
	$('#topCrtNewProjBtn').hide();
	$('#noData').hide();
	var length= data.length;
	var item ={id:"id_"+length+1,indent:0,0:"",1:"<%=userInfo.getUserName()%>",2:"",3:"",4:"",5:"",6:"",7:"100.0",8:"",9:"",10:""
		,11:"<%=BudgetConstants.FORECAST%>",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
			,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"New projects",35:"NewProjects",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	dataView.insertItem(0,item);
if(addsave ==0){
    var saveClose ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"Save",7:"",8:"",9:"",10:""
				,11:"Cancel",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	var item2 ={id:"id_"+length+6,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	var item3 ={id:"id_"+length+3,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
				,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
					,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
						,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	var item4 ={id:"id_"+length+4,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
			,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Closed",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	var item5 ={id:"id_"+length+5,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
		,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Active",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
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
	var item ={id:"id_"+length+1,indent:0,0:"",1:"<%=userInfo.getUserName()%>",2:"",3:"",4:"",5:"",6:"",7:"100.0",8:"",9:"",10:""
		,11:"<%=BudgetConstants.FORECAST%>",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
			,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"New projects",35:"NewProjects",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	dataView.insertItem(0,item);
	if(addsave ==0){
	    var saveClose ={id:"id_"+length+2,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"Save",7:"",8:"",9:"",10:""
					,11:"Cancel",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
						,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
							,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
		var item2 ={id:"id_"+length+6,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
					,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
						,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
							,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
		var item3 ={id:"id_"+length+3,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
					,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
						,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
							,31:"",32:"",33:"New",34:"New projects",35:"Buttons",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	
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
	var item ={id:"id_"+length,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:"",13:"",14:"",15:"",16:"",17:"",18:"",19:"",20:""
			,21:"",22:"",23:"",24:"",25:"",26:"New",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	dataView.insertItem(0,item);
    dataView.refresh(); 
    data=dataView.getItems();
    newExist = true;
}

// inserts dummy active or closed projects
function dummyActiveProjects(){
	var length= data.length;
	var iPlace=length-1;
	
	var item5 ={id:"id_"+length,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
		,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Active",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	if(activeExist==false){
		data[++iPlace] = item5;
	}
	activeExist = true;
}

function dummyClosedProjects(){
	var length= data.length;
	var iPlace=length-1;
	var item4 ={id:"id_"+length,indent:0,0:"",1:"",2:"",3:"",4:"",5:"",6:"",7:"",8:"",9:"",10:""
		,11:"",12:0.0,13:0.0,14:0.0,15:0.0,16:0.0,17:0.0,18:0.0,19:0.0,20:0.0
			,21:0.0,22:0.0,23:0.0,24:0.0,25:"",26:"Closed",27:"",28:"",29:"",30:""
				,31:"",32:"",33:"New",34:"",35:"",37:false,38:"",39:"",40:"<%=BudgetConstants.FORECAST%>"};
	
	if(closedExist==false){
		data[++iPlace] = item4;
	}
	closedExist = true;
}

function saveAndClose() {
	var errStr = "";
	var i = 0;
	for (i = 0; i < m_data.length; i++) {
		var d = m_data[i];
		errStr = "";
		
		if (m_data[i][4] != "" && m_data[i][4] != "undefined") {
			/*if (m_data[i][7].trim() == "" || m_data[i][7] == "undefined") {
				errStr = errStr + "Project Owner "
			}*/
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

	/*availableTags = [];
	
	for (var j = 0; j < ccUsersVar.length; j++) {
		if (ccUsersVar[j][0] == itemClicked[1]) {
			var res = ccUsersVar[j][1].substring(1,
					ccUsersVar[j][1].length - 1);
			availableTags = res.split(",");
			break;
		}
	}*/

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
		itemClicked[i] = 0.0;//parseFloat(total/(12-currentMonth)).toFixed(2);
	}
	grid.invalidate();
	var costCenter = $('#getCostCenter').val();
	//alert(JSON.stringify(m_data));
	if (itemClicked["34"] != "New projects") {
		$.ajax({
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

function deleteSelectedProjects() {
	
	var pLength = m_data.length;
	var noProjToDelete = true;
	if(itemClicked[1]=='<%=user.getUserName()%>' || '<%=user.getRole()%>'=="Admin" ){
		
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

function submitProjects(){
	var errStr = 0;
	var storeData=[];
	var flag = false;
	var msg = "";
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
	  /*  case 1:
	    	alert('"gMemori ID" can not be blank.');
	        break;*/
	    case 2:
	    	 alert('"Project name" can not be blank.');
	    	 grid.gotoCell(i+2, <%=BudgetConstants.PROJECT_NAME_CELL%>, false);
	    	break;
	   /* case 3:
	    	alert('"Project name" and "gMemori ID" can not be blank.');
	        break;*/
	    case 4:
	    	alert('"Brand" can not be blank.');
	    	grid.gotoCell(i+2,  <%=BudgetConstants.BRAND_CELL%>, false);
	        break;
	   /* case 5:
	    	alert('"Brand" and "gMemori Id" can not be blank.');
	        break;*/
	    case 5:
	    	alert('Po Number cannot be same');
	    	grid.gotoCell(i+2, <%=BudgetConstants.PO_NUMBER_CELL%>, false);
	        break;
	    case 6:
	    	alert('"Project name" and "Brand" can not be blank.');
	    	grid.gotoCell(i+2, <%=BudgetConstants.PROJECT_NAME_CELL%>, false);
	        break;
	  /*  case 7:
	    	alert('"Project name", "Brand" and "Gmemori ID" can not be blank.');
	        break;*/
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
	//alert(JSON.stringify(storeData));
	var costCenter = $('#getCostCenter').val();
	if(errStr == 0){
		 $.ajax({
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
				alert(result["responseText"].toString().indexOf("java.lang.Error:"));
				//alert(result["responseText"].toString());
				if(result["responseText"].toString().indexOf("<poError>:")!=-1){
					alert("PO Number already exists !!!");
				}
				else if(result["responseText"].toString().indexOf("java.lang.Error:")!= -1){
					alert(JSON.stringify(result["responseText"].toString().split("java.lang.Error:")[1].substring(1,38)));
				}else{
					alert("Unknow server error occured.");
				}
				$('#submitProjBtn').prop("disabled",false);
	        }
		});  
	}else{
		$('#submitProjBtn').prop("disabled",false);
	}
}


function exportExcelData(){
	var val = $('#selectedUserView').val();
	var ccVal = $('#getCostCenter').val();
	var brandValue = $('#getBrand1').val();
	//alert(val+":::::::::"+ccVal+"::::::"+brandValue);
	//code for server side export
/*	alert($('#getCostCenter').val());*/
	if($('input:radio[name=selectCC]:checked').val() == 1){
		onClickAsynch();
	}
	else{
	//console.log(JSON.stringify(data,null,4));
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

function onClickAsynch(){
	var num=1;
	function async(callback) {
//	    var i;
	    var z;
	    <%
	    HashSet hs = new HashSet();
	    ArrayList<String> costcentreAry = new ArrayList<String>();
	    for(int j=0; j<ccList.size();j++){
	    	costcentreAry.add(ccList.get(j).getCostCenter());
	    }
	    hs.addAll(costcentreAry);
	    costcentreAry.clear();
	    costcentreAry.addAll(hs);
	  /*  for(int j=0; j<costcentreAry.size();j++){%>
	 
	   <% }*/%>
//   alert(<%= costcentreAry.size()%>);
	    <%for (int i=0; i < costcentreAry.size();i++){%>
	    CostCenterApperance(<%=costcentreAry.get(i)%>);
	    <%}%>
	}
	function CostCenterApperance(i) {
		
//		alert("Inside appearance"+num)
//	    var x = speech[i];
	    setTimeout(function() {
	    	ServletCall(i);
	    	callback();
	    }, 3500*num);
	    num++;
//	    }
//	    console.log(speech[i]);
	}
	function ServletCall(i){
		
		console.log("Downloading data...");
		var viewVal = $('#selectedUserView').val();
		//var ccVal = $('#getCostCenter').val();
		var brandValue = $('#getBrand1').val();
		
//			alert('Inside t 	imeout function'+i);
		$('#objArrayId').val('');
		$('#ccId').val(i);
		$('#viewSelected').val(viewVal);
		$('#brandSelected').val(brandValue);
		
		
	    document.getElementById('exportExcel').submit();
	    
		
//		alert('after servlet call');
	}



async(function(){ 

});

}

function modifyData(data){
	var modifiedData = [];
	var modifiedRowData = [];
	
	modifiedRowData.prjWBS = "";
	modifiedRowData.wbsName = "";
	modifiedRowData.subAct = "";
	modifiedRowData.brand = "";
	modifiedRowData.perAllocation = "";
	modifiedRowData.poNum = "";
	modifiedRowData.poDesc = "";
	modifiedRowData.vendor = "";
	modifiedRowData.requestor = "";
	var today = new Date();
    var month = today.getMonth()+1;
    month > 0? modifiedRowData.jan = "Actual": modifiedRowData.jan = "<%=BudgetConstants.FORECAST%>";
    month > 1? modifiedRowData.feb = "Actual": modifiedRowData.feb = "<%=BudgetConstants.FORECAST%>";
    month > 2? modifiedRowData.mar = "Actual": modifiedRowData.mar = "<%=BudgetConstants.FORECAST%>";
    month > 3? modifiedRowData.apr = "Actual": modifiedRowData.apr = "<%=BudgetConstants.FORECAST%>";
    month > 4? modifiedRowData.may = "Actual": modifiedRowData.may = "<%=BudgetConstants.FORECAST%>";
    month > 5? modifiedRowData.jun = "Actual": modifiedRowData.jun = "<%=BudgetConstants.FORECAST%>";
    month > 6? modifiedRowData.jul = "Actual": modifiedRowData.jul = "<%=BudgetConstants.FORECAST%>";
    month > 7? modifiedRowData.aug = "Actual": modifiedRowData.aug = "<%=BudgetConstants.FORECAST%>";
    month > 8? modifiedRowData.sep = "Actual": modifiedRowData.sep = "<%=BudgetConstants.FORECAST%>";
    month > 9? modifiedRowData.oct = "Actual": modifiedRowData.oct = "<%=BudgetConstants.FORECAST%>";
    month > 10? modifiedRowData.nov = "Actual": modifiedRowData.nov = "<%=BudgetConstants.FORECAST%>";
    month > 11? modifiedRowData.dec = "Actual": modifiedRowData.dec = "<%=BudgetConstants.FORECAST%>";
	modifiedRowData.total = "FY";
	modifiedRowData.unit = "FY";
	modifiedRowData.poTotal = "FY";
	modifiedRowData.variance = "Check";
	modifiedRowData.forecast = "Q3-Q4";
	
	modifiedData.push(modifiedRowData);
	
	modifiedRowData = [];
	modifiedRowData.prjWBS = "Project WBS";
	modifiedRowData.wbsName = "WBS Name";
	modifiedRowData.subAct = "Sub Activity";
	modifiedRowData.brand = "Brand";
	modifiedRowData.perAllocation = "Allocation %";
	modifiedRowData.poNum = "PO Number";
	modifiedRowData.poDesc = "PO Description";
	modifiedRowData.vendor = "Vendor";
	modifiedRowData.requestor = "Requestor";
	modifiedRowData.jan = "Jan";
	modifiedRowData.feb = "Feb";
	modifiedRowData.mar = "Mar";
	modifiedRowData.apr = "Apr";
	modifiedRowData.may = "May";
	modifiedRowData.jun = "Jun";
	modifiedRowData.jul = "Jul";
	modifiedRowData.aug = "Aug";
	modifiedRowData.sep = "Sep";
	modifiedRowData.oct = "Oct";
	modifiedRowData.nov = "Nov";
	modifiedRowData.dec = "Dec";
	modifiedRowData.total = "Total";
	modifiedRowData.unit = "Unit";
	modifiedRowData.poTotal = "PO Total";
	modifiedRowData.variance = "LTS";
	modifiedRowData.forecast = "<%=BudgetConstants.FORECAST%>";
	modifiedData.push(modifiedRowData);
	for(var cnt = 0; cnt < data.length; cnt++){
		var rowData = data[cnt];
		modifiedRowData = [];
		
		if(rowData[<%=BudgetConstants.GMEMORI_ID_FIELD%>].toString().trim() != "" &&rowData[<%=BudgetConstants.$_IN_1000_FIELD%>] == "<%=BudgetConstants.FORECAST%>"){
			
			modifiedRowData.prjWBS = rowData[<%=BudgetConstants.PROJECT_WBS_FIELD%>];
			modifiedRowData.wbsName = "";
			modifiedRowData.subAct = rowData[<%=BudgetConstants.SUBACTIVITY_FIELD%>];
			modifiedRowData.brand = rowData[<%=BudgetConstants.BRAND_FIELD%>];
			modifiedRowData.perAllocation = rowData[<%=BudgetConstants.ALLOCATION_PERCENTAGE_FIELD%>];
			modifiedRowData.poNum = rowData[<%=BudgetConstants.PO_NUMBER_FIELD%>];
			modifiedRowData.poDesc = "";
			modifiedRowData.vendor = rowData[<%=BudgetConstants.VENDOR_FIELD%>];
			modifiedRowData.requestor = rowData[<%=BudgetConstants.PROJECT_OWNER_FIELD%>];
			modifiedRowData.jan = rowData[<%=BudgetConstants.JAN_FIELD%>];
			modifiedRowData.feb = rowData[<%=BudgetConstants.FEB_FIELD%>];
			modifiedRowData.mar = rowData[<%=BudgetConstants.MAR_FIELD%>];
			modifiedRowData.apr = rowData[<%=BudgetConstants.APR_FIELD%>];
			modifiedRowData.may = rowData[<%=BudgetConstants.MAY_FIELD%>];
			modifiedRowData.jun = rowData[<%=BudgetConstants.JUN_FIELD%>];
			modifiedRowData.jul = rowData[<%=BudgetConstants.JUL_FIELD%>];
			modifiedRowData.aug = rowData[<%=BudgetConstants.AUG_FIELD%>];
			modifiedRowData.sep = rowData[<%=BudgetConstants.SEP_FIELD%>];
			modifiedRowData.oct = rowData[<%=BudgetConstants.OCT_FIELD%>];
			modifiedRowData.nov = rowData[<%=BudgetConstants.NOV_FIELD%>];
			modifiedRowData.dec = rowData[<%=BudgetConstants.DEC_FIELD%>];
			modifiedRowData.total = rowData[<%=BudgetConstants.TOTAL_FIELD%>];
			modifiedRowData.unit = "";
			modifiedRowData.poTotal = rowData[<%=BudgetConstants.TOTAL_FIELD%>];
			modifiedRowData.variance = "";
			modifiedRowData.forecast = "";
			
			modifiedData.push(modifiedRowData);
		
		}
		
	}
	
	return modifiedData;
}

function openUploadPopUp(){
	$('#uploadWindow').show().fadeIn(100);
	$('#back').addClass('black_overlay').fadeIn(100);
}

function openDownloadPopUp(){
	if(<%=!userInfo.getRole().contains("Admin")%>){
	//	$('#selectthebrand').hide();
		exportExcelData();
	}
	else{
	$('#selectthebrand').show().fadeIn(100);
	$('#back').addClass('black_overlay').fadeIn(100);
	//$('selectCC').val(0,)
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

function closeUploadWindow(){
	$('#uploadWindow').hide();
	$('#back').removeClass('black_overlay').fadeIn(100);
	$('input[name=file]').replaceWith($('input[name=file]').clone(true));
}


function selectUserView(){
	var val = $('#selectedUserView').val();
	var ccVal = $('#getCostCenter').val();
	
	if($('#selectedUserView').val()=='My Brands'){
		$('#selectedView3').val(val);
		$('#getCostCenter3').val(ccVal);
		$("#dropdown").show();
		selectedBrandValue = $('#getBrand1').val(); 
		$('#getBrand3').val(selectedBrandValue);
		$('#getBrand').submit();
		
	}else if($('#selectedUserView').val()=='My Projects'){
		$('#selectedView2').val(val);
		$('#getCostCenter2').val(ccVal);
		$("#dropdown").hide();
		$('#getProjects').submit();
		
	}else if($('#selectedUserView').val()=='My Cost Center'){
		$('#selectedView1').val(val);
		$('#getCostCenter1').val(ccVal);
		$("#dropdown").hide();
		$('#getCostCentre').submit();
	}
}

function selectUserView1(){
	var val = $('#selectedUserView').val();
	var ccVal = $('#getCostCenter').val();
	selectedBrandValue = document.getElementById("brandType").value;
	if($('#selectedUserView').val()=='My Brands'){
		$('#selectedView3').val(val);
		$('#getCostCenter3').val(ccVal);
		$('#dropdown').show();
		
		$('#getBrand3').val(selectedBrandValue);
		$('#getBrand').submit();
		
	}else if($('#selectedUserView').val()=='My Projects'){
		$('#selectedView2').val(val);
		$('#getCostCenter2').val(ccVal);
		$('#getBrand2').val(selectedBrandValue);
		$('#dropdown').hide();
		$('#getProjects').submit();
		
	}else if($('#selectedUserView').val()=='My Cost Center'){
		$('#selectedView1').val(val);
		$('#getCostCenter1').val(ccVal);
		$('#getBrandCC').val(selectedBrandValue);
		$('#dropdown').hide();
		$('#getCostCentre').submit();
	}
}

function closepopup(){
	$('#selectthebrand').hide();
	$('#back').removeClass('black_overlay').fadeIn(100);
}
function toSubmit(){
    alert('I will not submit');
    return false;
 }
function getCostCenterDetails(){
	var ccVal = $('#getCostCenter').val();
	var val = $('#selectedUserView').val();
	
	
	if(val=="My Brands"){
		var brandVal = 	document.getElementById("getBrand").value;
		 $('#selectedView3').val(val);
		 $('#getCostCenter3').val(ccVal);
		$('#getBrand').submit();
	}else if(val=="My Cost Center"){
		$('#getCostCenter1').val(ccVal);
		$('#selectedView1').val(val);
		$('#getCostCentre').submit();
	}else if ("My Projects"){
		$('#getCostCenter2').val(ccVal);
		$('#selectedView2').val(val);
		$('#getProjects').submit();	
	}
	 
	/*alert(brandVal);
	alert(ccVal);
	$('#selectedView1').val("My Brands");
	*/
//	updateUserCostCenter(costCenter);
	
}

function openBrandPopUp(){
	var val = $('#selectedUserView').val();
	var ccVal = $('#getCostCenter').val();
	if($('#selectedUserView').val()=='My Brands'){
		$('#selectedView3').val(val);
		$('#getCostCenter3').val(ccVal);
	$('#selectthebrand').show().fadeIn(100);
	$('#back').addClass('black_overlay').fadeIn(100);
	}
}

// Code for delete or disable project
function deleteCurrentProject(delBtnClicked){
	var gmemId = delBtnClicked.value.split('~')[0];
	var projectOwner = delBtnClicked.value.split('~')[1];
	var projectCreateDate = new Date(delBtnClicked.value.split('~')[2].split("_"));
	var projStatus = delBtnClicked.value.split('~')[3];
	if('<%=userInfo.getRole().contains("Project Owner")%>' == 'true'){
		console.log("Not an admin...");
		if(projStatus != "<%=BudgetConstants.status_New%>" ){
			alert("PO exists and the project cannot be deleted.");
			return;
		}
		var cutOffDate = new  Date('<%=sdf.format(cutOfDate)%>');
		var currQtr = '<%=qtr%>';
		var projCreatedQtr = Math.floor(projectCreateDate.getMonth() / 3);
		// if project created in this quarter compare creation date with
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
		url : '/disableProject',
		type : 'POST',
		dataType : 'text',
		data : {gMem: gmemId, costCenter:ccVal, projectOwner:projectOwner},
		success : function(result) {
			if(result==null){
				alert("Error occured while deleting project!!!");
			}else{
			var obj = $.parseJSON(result);
			//alert(result+"::::::::::"+obj);
			if(obj.statusCode==200){
			alert('Successfully deleted project.');
			window.location.reload(true);
			}else{
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


function calculateTotal(){
	var accrualTotalItem, forecastTotalItem, quarterlyTargetTotalItem, quarterlyLTSTotalItem;
		// get the items for totals, sets values to zero so that new value will be summed up and replace current
	for (var key in map) {
		if(map[key][26] == "Total"){
			if(key.split(":")[1].trim() == "<%=BudgetConstants.FORECAST%>"){
				 forecastTotalItem = map[key];
				 for(var i =0; i<=12; i++){
					 forecastTotalItem[i + 12] = 0.0; 
				 }
			}else if(key.split(":")[1].trim() == "<%=BudgetConstants.ACCRUAL%>"){
				accrualTotalItem = map[key];
				 for(var i =0; i<=12; i++){
					 accrualTotalItem[i + 12] = 0.0; 
				 }
			}else if(key.split(":")[1].trim() == "<%=BudgetConstants.ANNUAL_TARGET%>"){
				quarterlyTargetTotalItem = map[key];
				for(var i =0; i<=12; i++){
					quarterlyTargetTotalItem[i + 12] = 0.0; 
				}
			}else if(key.split(":")[1].trim() == "<%=BudgetConstants.FORECAST_LTS%>"){
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
				if(key.split(":")[1].trim() == "<%=BudgetConstants.FORECAST%>"){
					for(var i =0; i<=12; i++){
						forecastTotalItem[i + 12] = parseFloat(forecastTotalItem[i + 12]) + parseFloat(map[key][i + 12]);
					}
				}
				else if(key.split(":")[1].trim() == "<%=BudgetConstants.ACCRUAL%>" && map[key][26] != 'New'){
					for(var i =0; i<=12; i++){
						accrualTotalItem[i + 12] = parseFloat(accrualTotalItem[i + 12]) + parseFloat(map[key][i + 12]);
					}
				}
				else if(key.split(":")[1].trim() == "<%=BudgetConstants.ANNUAL_TARGET%>"){
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
	
	for(var i =0; i<=12; i++){
		quarterlyLTSTotalItem[i + 12] = parseFloat(quarterlyTargetTotalItem[i + 12]) - parseFloat(accrualTotalItem[i + 12]);
	}
	
	prevKey = "";
	grid.invalidate();
}
