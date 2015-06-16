/***
 * Contains basic SlickGrid formatters.
 * 
 * NOTE:  These are merely examples.  You will most likely need to implement something more
 *        robust/extensible/localizable/etc. for your use!
 * 
 * @module Formatters
 * @namespace Slick
 */

(function ($) {
  // register namespace
  $.extend(true, window, {
    "Slick": {
      "Formatters": {
        "PercentComplete": PercentCompleteFormatter,
        "PercentCompleteBar": PercentCompleteBarFormatter,
        "YesNo": YesNoFormatter,
        "Checkmark": CheckmarkFormatter,
        "DollarSymbol" : DollarFormatter,
        "DollarSymbolMB" : DollarFormatterMB,
        "Remark":RemarkFormatter,
        "HyperLink":HyperLinkFormatter,
        "budget":BudgetFormatter,
        "button": ButtonFormatter,
        "checkbox": CheckBoxFormatter,
        "gMemoriHyperLink":gMemoriHyperLinkFormatter,
        "editableField":editableFieldFormatter,
        "cancelButton":cancelButtonFormatter,
        "poField":poFieldFormatter
      }
    }
  });
  
  function PercentCompleteFormatter(row, cell, value, columnDef, dataContext) {
    if (value == null || value === "") {
      return "-";
    } else if (value < 50) {
      return "<span style='color:red;font-weight:bold;'>" + value + "%</span>";
    } else {
      return "<span style='color:green'>" + value + "%</span>";
    }
  }

  function PercentCompleteBarFormatter(row, cell, value, columnDef, dataContext) {
    if (value == null || value === "") {
      return "";
    }

    var color;

    if (value < 30) {
      color = "red";
    } else if (value < 70) {
      color = "silver";
    } else {
      color = "green";
    }

    return "<span class='percent-complete-bar' style='background:" + color + ";width:" + value + "%'></span>";
  }

  function YesNoFormatter(row, cell, value, columnDef, dataContext) {
    return value ? "Yes" : "No";
  }

  function CheckmarkFormatter(row, cell, value, columnDef, dataContext) {
    return value ? "<img src='../images/tick.png'>" : "";
  }
 
  function DollarFormatter(row, cell, value, columnDef, dataContext) {
		 
	//  console.log(dataContext);
	  var d= new Date();
	  var month = d.getMonth();
	  var monthArray = ["JAN", "FEB","MAR","APR","MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV","DEC"];
	 
	  if(dataContext["35"] != "Buttons"){
		if(dataContext[35] == "NewProjects" && columnDef['name'] != "Total"){
			return "<div width = '100%' style='background:#C0CCED'><span style='color:#2271B0'> "+ Number(value).toFixed(2) +"</span></div>" ;
		}
		else if(dataContext["26"] == "Total" || columnDef["name"] == "Total"){
			return "<span style='color:#339966; height: 25px; width: 120px; font-weight: bold; font-style: italic;'> "+ Number(value).toFixed(2) +"</span>" 
		}else if((dataContext["26"] != "Closed") && ((dataContext["11"] == "Forecast" && dataContext["0"].toString().indexOf(".") == -1)))  {
			for(var i=0;i<month;i++){
				if(columnDef["name"]==monthArray[i]){
				return "<span style='color:#2271B0'> "+ Number(value).toFixed(2) +"</span>";
				}
			}
			for(var i=month;i<12;i++){
				if(columnDef["name"]==monthArray[i]){
					return "<div width = '100%' style='background:#C0CCED'><span style='color:#2271B0'> "+ Number(value).toFixed(2) +"</span></div>" ;		
				}
			
		}
			
		}/*else if((dataContext["11"] == "Forecast" && dataContext["0"].toString().indexOf(".") == -1) || (dataContext["11"] == "Accrual" && dataContext["26"] == "Active" ) ){
			return "<div width = '100%' style='background:#C0CCED'><span style='color:#2271B0'> "+ Number(value).toFixed(2) +"</span></div>" ;
		}*/else{
			return "<span style='color:#2271B0'> "+ Number(value).toFixed(2) +"</span>"; 
		}
	  }
  }
  
  function DollarFormatterMB(row, cell, value, columnDef, dataContext) {
	  if(dataContext[1].trim() != '' || dataContext[2].trim() != ''){
		  return "<span style='color:#2271B0'> "+ Number(value).toFixed(2) +"</span>";
	  }
  }
  
  function ButtonFormatter(row, cell, value, columnDef, dataContext) {
	  if(value=="Save" && dataContext["35"] == "Buttons"){
	  return  "<input type='button' value='"+value+"' id='btnForm' value2='"+row+"' value3='"+cell+"/>";
  }
  }
  
  function HyperLinkFormatter(row, cell, value, columnDef, dataContext) {
	  if(typeof value == "string"){
		  if(value.toLowerCase().indexOf("smart wbs") >= 0){
			  return "<div width = '100%' style='background:#C0CCED'><span ><a href='#' style='color:green'>"+value + "</a></span></div>" ;		
		  }else if(value=="Save"   && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='submitProjBtn' style='font-size: 12px; width:80px; height: 20px; background:#2271B0; color:#FFFFFF'/>";
		  }else if( value=="Cancel"  && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='cnclProjBtn' style='font-size: 12px;  width:80px; height: 20px; background:#2271B0; color:#FFFFFF' />";
		  }else if((dataContext["26"] != "Closed") && (dataContext["11"] == "Forecast" && dataContext["0"].toString().indexOf(".") == -1 && dataContext["26"] != "Total") ){
			  return "<div width = '100%' style='background:#C0CCED'>"+value+"&nbsp;</div>" ;		
		  }
	  }
	  return value;
  }
  
  function cancelButtonFormatter(row, cell, value, columnDef, dataContext) {
	  if(typeof value == "string"){
		 if( value=="Cancel"  && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='cnclProjBtn' style='font-size: 12px;  width:80px; height: 20px; background:#2271B0; color:#FFFFFF' />";
		 }else if(value=="Forecast"){
			 return  "<span title='Projected Spend'>Forecast</span>";
		 }else if(value=="Quarterly Target"){
			 return  "<span title='Benchmarks for current quarter'>Quarterly Target</span>";
		 }else if(value=="Annual Target"){
			 return  "<span title='Benchmarks for current quarter'>Annual Target</span>";
		 }else if(value=="Accrual"){
			 return  "<span title='Spend according to milestones of SOW'>Accrual</span>";
		 }else if(value=="Forecast LTS"){
			 return  "<span title='= Quarterly Target - Accrual'>Forecast LTS</span>";
		 }else if(value=="Quarterly LTS"){
			 return  "<span title='= Quarterly Target - Accrual'>Quarterly LTS</span>";
		 }
	  }
	  return value;
  }
  
  function gMemoriHyperLinkFormatter(row, cell, value, columnDef, dataContext) {
	  var val = value.toString();
	  if(val.indexOf(".") != -1){
		  val = val.split(".")[0];  
	  }
	  if(dataContext[35] == "NewProjects"){
			  return "";
	  }else if(val.length > 0 && val.length <= 6 && dataContext[35]!="NewProjects"){
		  var url = "http://memori-qa.appspot.com/editProject?gMemoriId="+val;
		  if(dataContext["0"].toString().indexOf(".") == -1){
			  return "<div width = '100%'><span ><a id='gmem' href="+url+" target='gmemori' style='color:green'>"+value + "</a></span></div>" ;
		  }else{
			  return "<span ><a id='gmem' href="+url+" target='gmemori' style='color:green'>"+value + "</a></span>" ;
		  }
	  }else if(dataContext[35]=="NewProjects" && value.toString().trim != "" && value != 0){
		  return "<div width = '100%'>" + value +"</div>";
	  }else if(value.toString().trim != "" && dataContext["11"] == "Forecast" && dataContext["26"] != "Total" && dataContext["2"] != "" && dataContext["0"].toString().length==10 ){
		 return "<div width = '100%'><span ><a id='gmem' href='#' target='' style='color:green'>Initiate</a></span></div>";
	  }else if(value.toString().trim != "" && dataContext["11"] == "Forecast" && dataContext["26"] != "Total" && dataContext["2"] == "" && dataContext["0"].toString().indexOf(".") == -1 ){
		  return "<div width = '100%'>&nbsp;</div>";
	  }else{
		  return "";
	  }
  }
  
  function RemarkFormatter(row, cell, value, columnDef, dataContext) {
		  return "<span style='color:green'>"+ value +"</span>";
  }
  
  function BudgetFormatter(row, cell, value, columnDef, dataContext) {
		if(value!=""){
			return "<span style='color:#2271B0'>"+ Number(value).toFixed(2) +"</span>" ;
		}else if(dataContext[25]=="Added"){
			return "<span style='color:#2271B0'>" + (0).toFixed(2) +"</span>";
		}
  }
  
  function CheckBoxFormatter(row, cell, value, columnDef, dataContext) {
	  if((dataContext[9] != "preExisting" || itemClicked[26] == 'New') && (dataContext[7].toString().trim() != "" || dataContext[1].toString().trim() != "" || dataContext[3].toString().trim() != "" || dataContext[2].toString().trim() != "")){
		return "<input type='checkbox' class='checkDeletion' value='true' id = "+ row+"chkBox>";
	  }else{
		  return "";
	  }
  }

  function gMemoriFormatter(row, cell, value, columnDef, dataContext) {
	  if(dataContext[35] == "NewProjects"){
		return "<INPUT type=text class='editor-text' maxlength='6'/>";
	  }
  }
  
  function editableFieldFormatter(row, cell, value, columnDef, dataContext) {
	  if(columnDef['name'] == "Project Name" && dataContext[11] == "Quarterly Target" && dataContext[35] != "NewProjects"  && dataContext[26] != "Total" && dataContext[27].indexOf('.') == -1){
		  return "<button class='myButton' style='margin-left:auto;margin-right:auto;display:block;' type='button' id='delPrjBtn' value="+dataContext[27] + "~" + dataContext[54] + "~"+ dataContext[38] + "~"+ dataContext[26] +" onClick = 'deleteCurrentProject(this)'>Delete</button>";
	  }
	  if((dataContext["26"] == "Closed")){
		  if((typeof value != 'undefined' || value != '') && (dataContext["11"] == "Forecast" && dataContext["0"].toString().indexOf(".") == -1 )){
			     return "<div width = '100%'>"+value+"</div>";
		  }else{
		 		return "<div width = '100%'>&nbsp;</div>";
		  }
	  }
	  if((typeof value != 'undefined' && value != '') && (dataContext[35] == "NewProjects" || (dataContext["11"] == "Forecast" && dataContext["0"].toString().indexOf(".") == -1  && dataContext["26"] != "Total" ))){
		return "<div width = '100%' style='background:#C0CCED'>"+value+"&nbsp;</div>";
	  }
	  
	  if((typeof value === 'undefined' || value == '')  && (dataContext[35] == "NewProjects" || (dataContext["11"] == "Forecast" && dataContext["0"].toString().indexOf(".") == -1  && dataContext["26"] != "Total" ))){
	     return "<div width = '100%' style='background:#C0CCED'>&nbsp;</div>";
  	  }
  }
  
  function poFieldFormatter(row, cell, value, columnDef, dataContext) {
	  if((dataContext["26"] != "Closed") && (dataContext[35] == "NewProjects" || (dataContext["11"] == "Forecast" && dataContext["0"].toString().indexOf(".") == -1  && dataContext["26"] != "Total"  && dataContext["26"] != "Active" ))){
		return "<div width = '100%' style='background:#C0CCED'>"+value+"&nbsp;</div>";
	  }
	  return value;
  }
  
})(jQuery);
