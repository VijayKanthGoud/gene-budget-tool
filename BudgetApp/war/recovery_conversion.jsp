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
<h1>Hello Google Cloud Storage!</h1>
<%
	  List<String> fileNameList = new ArrayList<String>();%>
  <table>
  <tr>
      <td style="font-weight: bold;">Download a file from Google Cloud Storage:</td>
    </tr>
    <tr>
      <td>
        <form name="getFile">
          <div>
            Bucket: <span id="bucket" style="font: 14"><%=BudgetConstants.BUCKET_NAME %></span>
            File Name: <select name="fileName" id="fileName" >
            <% fileNameList = Util.getFileNamesFromCS(BudgetConstants.BUCKET_NAME);
            for(String fileName:fileNameList){ %>
            <option value=<%= fileName%>><%= fileName%></option>
            <%} %> 
            </select> 
            <!-- <input type="text" name="fileName" id="fileName" /> -->
          </div>
        </form>
        <form action="/recovery_conversion.jsp" method="get" name="submitGet">
          <div>
            <input type="submit" onclick='changeGetPath(this)' value="Download Content" />
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
        if (bucket == null || bucket == "" || filename == null || filename == "") {
          alert("Both Bucket and FileName are required");
          return false;
        } else {
          document.submitGet.action = "/gcs/" + bucket + "/" + filename;
        }
      }

    </script>
</body>
</html>