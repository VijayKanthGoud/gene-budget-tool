<%@page import="com.gene.app.util.BudgetConstants"%>
<%@page import="com.gene.app.util.Util"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link rel="stylesheet" href="SlickGrid-master/slick.grid.css"
	type="text/css" />
<link rel="stylesheet"
	href="SlickGrid-master/css/smoothness/jquery-ui-1.8.16.custom.css"
	type="text/css" />
<link rel="stylesheet" href="SlickGrid-master/examples/examples.css"
	type="text/css" />
<%@ include file="header.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>

<body onload="setDownloadDefaults();">
<br/><br/>
<%
  List<String> fileNameList = new ArrayList<String>();%>
  <table>
    <tr>
      <td>
        <form name="getFile">
          <div>
            <b>Bucket:</b> <span id="bucket" style="font: 14"><%=BudgetConstants.BUCKET_NAME %></span>
            <br/>
            <br/>
            <b>File Name:</b> <select name="fileName" id="fileName" >
            <% fileNameList = Util.getFileNamesFromCS(BudgetConstants.BUCKET_NAME);
            for(String fileName:fileNameList){ %>
            <option value=<%= fileName%>><%= fileName%></option>
            <%} %> 
            </select> 
          </div>
          <br/><br/>
        </form>
        <form action="/recovery_conversion.jsp" method="get" name="submitGet">
          <div>
            <input id='submtButton' type="submit" onclick='changeGetPath(this)' value="Restore Data" />
          </div>
        </form>
      </td>
    </tr>
  </table>
  <script>

      function setDownloadDefaults() {
        var url = location.search;
        var bucketArg = url.match(/bucket=[^&]*&/);
        if (bucketArg !== null) {
          document.getElementById("bucket").value = bucketArg.shift().slice(7, -1);
        }
        var fileArg = url.match(/fileName=[^&]*&/);
        if (fileArg !== null) {
          document.getElementById("fileName").value = fileArg.shift().slice(9, -1);
        }
      }

      function changeGetPath() {
    	  var bucket = '<%= BudgetConstants.BUCKET_NAME%>';
          var filename =  document.getElementById("fileName").value;
    	  $('#submtButton').prop("disabled",true);
    	  $("#submtButton").attr('value', 'Restoring...');
          if (bucket == null || bucket == "" || filename == null || filename == "") {
              alert("FileName is required");
              return false;
          } else {
		  $.ajax({
				url : "/gcs/" + bucket + "/" + filename,
				type : 'GET',
				async: true,
				dataType : 'text',
				success : function(result) {
					alert("Success");
					$('#submtButton').prop("disabled",false);
					$("#submtButton").attr('value', 'Restore Data');
				},
				error : function(result){
					alert("Error");
					$('#submtButton').prop("disabled",false);
					$("#submtButton").attr('value', 'Restore Data');
				}
			});
      	}
      }

    </script>
</body>
</html>