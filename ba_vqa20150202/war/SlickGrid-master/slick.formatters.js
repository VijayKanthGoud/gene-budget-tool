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
	  if(dataContext["35"] != "Buttons"){
		if(dataContext["26"] == "Total" || columnDef["name"] == "Total"){
			return "<span style='color:#339966; height: 25px; width: 120px; font-weight: bold; font-style: italic;'> "+ Number(value).toFixed(2) +"</span>" 
		}else if((dataContext["11"] == "Planned" && dataContext["0"].toString().indexOf(".") == -1) || (dataContext["11"] == "Accrual" && dataContext["26"] == "Active" ) ){
			return "<div width = '100%' style='background:#C0CCED'><span style='color:#005691'> "+ Number(value).toFixed(2) +"</span></div>" ;
		}else{
			return "<span style='color:#005691'> "+ Number(value).toFixed(2) +"</span>"; 
		}
	  }
  }
  
  function DollarFormatterMB(row, cell, value, columnDef, dataContext) {
	  if(dataContext[1].trim() != '' || dataContext[2].trim() != ''){
		  return "<span style='color:#005691'> "+ Number(value).toFixed(2) +"</span>";
	  }
  }
  
  function ButtonFormatter(row, cell, value, columnDef, dataContext) {
	  if(value=="Save" && dataContext["35"] == "Buttons"){
	  return  "<input type='button' value='"+value+"' id='btnForm' value2='"+row+"' value3='"+cell+"/>";
  }
  }
  
  function HyperLinkFormatter(row, cell, value, columnDef, dataContext) {
	  if(typeof value == "string"){
		  if(value.toLowerCase().indexOf("mb") >= 0){
			  return "<div width = '100%' style='background:#C0CCED'><span ><a href='#' style='color:green'>"+value + "</a></span></div>" ;		
		  }else if(value=="Save"   && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='submitProjBtn' style='font-size: 12px; width:80px; height: 20px; background:#005691; color:#FFFFFF'/>";
		  }else if( value=="Cancel"  && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='cnclProjBtn' style='font-size: 12px;  width:80px; height: 20px; background:#005691; color:#FFFFFF' />";
		  }else if(dataContext["11"] == "Planned" && dataContext["0"].toString().indexOf(".") == -1 && dataContext["26"] != "Total" ){
			  return "<div width = '100%' style='background:#C0CCED'>"+value+"&nbsp;</div>" ;		
		  }
	  }
	  return value;
  }
  
  function cancelButtonFormatter(row, cell, value, columnDef, dataContext) {
	  if(typeof value == "string"){
		 if( value=="Cancel"  && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='cnclProjBtn' style='font-size: 12px;  width:80px; height: 20px; background:#005691; color:#FFFFFF' />";
		 }
	  }
	  return value;
  }
  
  function gMemoriHyperLinkFormatter(row, cell, value, columnDef, dataContext) {
	  var val = value.toString();
	  if(val.indexOf(".") != -1){
		  val = val.split(".")[0];  
	  }
	  if(val.length > 0 && val.length <= 6 && dataContext[35]!="NewProjects"){
		  var url = "http://memori-qa.appspot.com/editProject?gMemoriId="+val;
		  if(dataContext["0"].toString().indexOf(".") == -1){
			  return "<div width = '100%' style='background:#C0CCED'><span ><a id='gmem' href="+url+" target='gmemori' style='color:green'>"+value + "</a></span></div>" ;
		  }else{
			  return "<span ><a id='gmem' href="+url+" target='gmemori' style='color:green'>"+value + "</a></span>" ;
		  }
		  
	  }else if(dataContext[35]=="NewProjects" && value.toString().trim != "" && value != 0){
		  return "<div width = '100%' style='background:#C0CCED'>" + value +"</div>";
	  }else if(value.toString().trim != "" && dataContext["11"] == "Planned" && dataContext["26"] != "Total" && dataContext["0"].toString().indexOf(".") == -1 ){
		  return "<div width = '100%' style='background:#C0CCED'>&nbsp;</div>";
	  }else{
		  return "";
	  }
  }
  
  function RemarkFormatter(row, cell, value, columnDef, dataContext) {
		  return "<span style='color:green'>"+ value +"</span>";
  }
  
  function BudgetFormatter(row, cell, value, columnDef, dataContext) {
		if(value!=""){
			return "<span style='color:#005691'>"+ Number(value).toFixed(2) +"</span>" ;
		}else if(dataContext[25]=="Added"){
			return "<span style='color:#005691'>" + (0).toFixed(2) +"</span>";
		}
  }
  
  function CheckBoxFormatter(row, cell, value, columnDef, dataContext) {
	  if(dataContext[7].toString().trim() != ""){
		return "<input type='checkbox' class='checkDeletion' value='true' id = "+ row+"chkBox>";
	  }
  }

  function gMemoriFormatter(row, cell, value, columnDef, dataContext) {
	  if(dataContext[35] == "NewProjects"){
		return "<INPUT type=text class='editor-text' maxlength='6'/>";
	  }
  }
  
  function editableFieldFormatter(row, cell, value, columnDef, dataContext) {
	  if(dataContext[35] == "NewProjects" || (dataContext["11"] == "Planned" && dataContext["0"].toString().indexOf(".") == -1  && dataContext["26"] != "Total" )){
		return "<div width = '100%' style='background:#C0CCED'>"+value+"&nbsp;</div>";
	  }
	  return value;
  }
  
  function poFieldFormatter(row, cell, value, columnDef, dataContext) {
	  if(dataContext[35] == "NewProjects" || (dataContext["11"] == "Planned" && dataContext["0"].toString().indexOf(".") == -1  && dataContext["26"] != "Total"  && dataContext["26"] != "Active" )){
		return "<div width = '100%' style='background:#C0CCED'>"+value+"&nbsp;</div>";
	  }
	  return value;
  }
  
})(jQuery);
