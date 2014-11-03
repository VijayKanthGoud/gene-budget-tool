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
        "Remark":RemarkFormatter
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
	if(dataContext["26"] == "Total" || columnDef["name"] == "Total"){
		return "<span style='color:#339966; height: 25px; width: 120px; font-weight: bold; font-style: italic;'>"+ Number(value).toFixed(2) +"</span>" 
	}else{
		return "<span style='color:#005691'>"+ Number(value).toFixed(2) +"</span>" 
	}
  }
  
  function RemarkFormatter(row, cell, value, columnDef, dataContext) {
	 
	  if(dataContext[11]!='Planned'){
		  
	  }
	  if(value != "" && typeof(value) != "undefined"){
		  if (value < 50) {
			  color = "red";
		  } else {
			  color = "green";
		  }
		  return "<span style='color:"+color+"'>"+ value +"</span>";
	  }
  }

})(jQuery);
