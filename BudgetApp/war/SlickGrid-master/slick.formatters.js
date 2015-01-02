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
        "gMemori": gMemoriFormatter,
        "gMemoriHyperLink":gMemoriHyperLinkFormatter
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
		}else{
			return "<span style='color:#005691'> "+ Number(value).toFixed(2) +"</span>" 
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
			  return "<span ><a href='#' style='color:green'>"+value + "</a></span>" ;		
		  }else if(value=="Save"   && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='submitProjBtn' style='font-size: 12px; width:80px; height: 20px; background:#005691; color:#FFFFFF'/>";
		  }else if( value=="Cancel"  && dataContext["35"] == "Buttons"){
			  return  "<input type='button' value='"+value+"' id='cnclProjBtn' style='font-size: 12px;  width:80px; height: 20px; background:#005691; color:#FFFFFF' />";
		  }
	  }
	  return value;
  }
  
  function gMemoriHyperLinkFormatter(row, cell, value, columnDef, dataContext) {
	  var val = value.toString();
	  if(val.length>0 && dataContext[35]!="NewProjects"){
		  var url = "https://25-dot-budget-mgmt-tool.appspot.com/?gMemoriId="+val;
		  return "<span ><a href="+url+" target='_blank' style='color:green'>"+value + "</a></span>" ;
	  }else{
		  return val;
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
	  if(dataContext[4].toString().trim() != ""){
		return "<input type='checkbox' class='checkDeletion' value='true' id = "+ row+"chkBox>";
	  }
  }

  function gMemoriFormatter(row, cell, value, columnDef, dataContext) {
	 // console.log("dataContext = "+JSON.stringify(dataContext,null,4));
	 // alert("columnDef "+JSON.stringify(columnDef));
	  if(dataContext[35] == "NewProjects"){
		return "<INPUT type=text class='editor-text' maxlength='6' placeholder="+columnDef["name"]+" />";
	  }
  }
  
})(jQuery);
