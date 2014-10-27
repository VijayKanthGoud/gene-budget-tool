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
		return "<span style='color:#339966; height: 25px;width: 120px;'>"+ Number(value).toFixed(3) +"</span>" 
	}else{
		return "<span style='color:#005691'>"+ Number(value).toFixed(3) +"</span>" 
	}
  }
	  function RemarkFormatter(row, cell, value, columnDef, dataContext) {
		if (Number(dataContext["id"].split("_")[1]) + 3 <= totalSize && Number(dataContext["id"].split("_")[1]) % 4 == 0) {
			var plannedAmt = data[(dataContext["id"].split("_")[1])]["24"];
			var bnchmrkAmt = data[Number(dataContext["id"].split("_")[1]) + 1]["24"];
			var accrualsAmt = data[Number(dataContext["id"].split("_")[1]) + 2]["24"];
			var variancesAmt = data[Number(dataContext["id"].split("_")[1]) + 3]["24"];
			if(bnchmrkAmt!=0){
				var percentage = (bnchmrkAmt - accrualsAmt)/bnchmrkAmt * 100;
				if(percentage < 50){
					return "<span style='color:red';>"+percentage + "% </span>";
				}else{
					return "<span style='color:green';>"+percentage + "% </span>";
				}
			}else{
				return "<span style='color:red';>0</span>";
			}
		}else{
			return "";
		}
	}

})(jQuery);
