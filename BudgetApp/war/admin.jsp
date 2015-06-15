<%@page import="java.util.*"%>
<%@page import="com.gene.app.dao.DBUtil"%>
<%@page import="com.gene.app.util.BudgetConstants"%>
<%@page import="com.gene.app.model.*"%>
<%@ include file="header.jsp"%>
<html>
<head>
 <link rel="stylesheet" type="text/css"
	href="css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"
	href="css/prism.min.css">
<style type="text/css">
@charset "UTF-8";

[ng\:cloak],[ng-cloak],[data-ng-cloak],[x-ng-cloak],.ng-cloak,.x-ng-cloak
	{
	display: none;
}

ng\:form {
	display: block;
}
</style> 
  <script>
  var selectedTab=0;
  $(function() {
	 
	  if(document.URL.split("?")[1].toString().split("=")[1] == 1){
		  selectedTab=1;
		  document.getElementById("headerid").innerHTML="FACT";
		}else if(document.URL.split("?")[1].toString().split("=")[1] == 2){
			  selectedTab=2;
			  document.getElementById("headerid").innerHTML="PO Details";
		}else{
			selectedTab=3;
			document.getElementById("costCenter").style.display = "none";
			document.getElementById("headerid").innerHTML="User Details";
		}
  });
  
	

  </script>

</head>

<body>
<% if(userRole.equalsIgnoreCase("admin")){ %>



</br>
</br>

 <table border="1.5" align="center" width="700px" height="500px" autofocus>

<tr>
<td id='cssmenu'>
<div align="center">
<button id = "headerid" type="button" class="form-control-header" disabled>FACT: </button>
</div>
</td>
</tr>

<tr>
<td id='cssmenu1'>
<div style="padding-left: 100px; padding-top: 30px;" ng-app="App"  >
		<div ng-controller="PreviewController" class="ng-scope">
			<div class="form-group">
 					<span style="font-size: 14px; font-weight: bold" >Excel File :</span> <input class="form-control-button" type="file"
					name="excel_file" accept=".xlsx" onchange="angular.element(this).scope().fileChanged(this.files);"
					required="true" style=" color: black; "> 					
 
			</div>

			<div ng-show="isProcessing" style="display: none;">
				<span style="color: blue">Please wait while processing the sheet ...<img alt="" src=""></span>
			</div>

			<div class="form-group">
				<span style="font-size: 14px; font-weight: bold">Sheet Name :</span> <select id="sheet_name"
					class="form-control ng-pristine ng-invalid ng-invalid-required"
					ng-change="showPreviewChanged()" ng-model="selectedSheetName"
					required="required" ng-required="true"
					ng-options="sheetName as sheetName for (sheetName, sheetData) in sheets"><option
						value="" class="">---- Select a sheet ----</option></select>
			</div>
			
			<div id = "costCenter" class="form-group">
			<span style="font-size: 14px; font-weight: bold">Cost  centre :</span><select id="getCostCenter" class="form-control ng-pristine ng-invalid ng-invalid-required" name="ccValue" style="width: 100px;">
						<%	List<CostCenter_Brand> cc_brandList = util.readCostCenterBrandMappingData();
						String ccSelected = "7135";
						CostCenter_Brand cc_brand = new CostCenter_Brand();
							if(cc_brandList!=null && !cc_brandList.isEmpty()){
								for(int i=0;i<cc_brandList.size();i++){
									cc_brand = cc_brandList.get(i);
									if(cc_brand.getCostCenter()!=null 
											&& !"".equals(cc_brand.getCostCenter()) 
											&& ccSelected.equalsIgnoreCase(cc_brand.getCostCenter())){
						%>
						<option value="<%= cc_brand.getCostCenter()%>" selected><%= cc_brand.getCostCenter()%></option>
						<%} else if(cc_brand.getCostCenter()!=null 
								&& !"".equals(cc_brand.getCostCenter())){%>
						<option value="<%= cc_brand.getCostCenter()%>"><%= cc_brand.getCostCenter()%></option>
						<%} } }%>
						</select>
			</div>
			<!-- </br></br> -->
			<div class="form-group">
			<span style="font-size: 14px; font-weight: bold"> From Line number: </span><input  class="form-control-textbox" type="number" min="0" onkeypress='return event.charCode >= 48 && event.charCode <= 57' name="frmLine" id="From"><br>
  			<span style="font-size: 14px; font-weight: bold">To Line number: </span> <input class="form-control-textbox" type="number" min="0" onkeypress='return event.charCode >= 48 && event.charCode <= 57' name="toLine" id="To"><br>
			
			</div>
			
			<div style="padding-left: 180px; padding-bottom: 20px ">
			<div id="loadImg" style="visibility: hidden;"><img  alt="Loading..." height='25px' width='25px' src="images/loading.gif"> Loading data...</div>
			<button id="loadBtn" type="button" class="form-control-submit" onclick="uploadData()" autofocus>Upload data</button>
			</div>
		</div>
	</div>

</td>
</tr>
</table>


<%-- <%@ include file="uploadpopUp.jsp" %>
 --%><script>
 
	function uploadData(){
		//console.log(JSON.stringify(excelValue.sheets["Main"].data));
		$('#loadBtn').hide();
		 $('#loadImg').removeAttr( "style" )
		if(selectedTab==1){
			
			  $.ajax({
					url : '/factsheetupload',
					type : 'POST',
					dataType : 'text',
					data : {objarray: JSON.stringify(excelValue.sheets[$('#sheet_name').val()].data),
						costCenter : $('#getCostCenter').val(),
						inputFrom :  $('#From').val(),
						inputTo :  $('#To').val()},
					success : function(result) {
						alert("Uploaded successfully");
						console.log('Data saved successfully');
						window.location.reload(true);
					},
					error: function(result) {
						alert("fail");
						console.log(result);
						window.location.reload(true);
			        }
				}); 
		}else if(selectedTab==2){
		  $.ajax({
				url : '/podetailsupload',
				type : 'POST',
				dataType : 'text',
				data : {objarray: JSON.stringify(excelValue.sheets[$('#sheet_name').val()].data),
					costCenter : $('#getCostCenter').val(),
					inputFrom :  $('#From').val(),
					inputTo :  $('#To').val()},
				success : function(result) {
					alert("Uploaded successfully");
					console.log('Data saved successfully');
					window.location.reload(true);
				},
				error: function(result) {
					alert("fail");
					console.log(result);
					window.location.reload(true);
		        }
			}); 
		}else{
			alert("sheet name = "+$('#sheet_name').val());
			 	  $.ajax({
						url : '/userdetailsupload',
						type : 'POST',
						dataType : 'text',
						data : {objarray: JSON.stringify(excelValue.sheets[$('#sheet_name').val()].data),
							sheetName : $('#sheet_name').val(),
							inputFrom :  $('#From').val(),
							inputTo :  $('#To').val()},
						success : function(result) {
							alert("Uploaded successfully");
							console.log('Data saved successfully');
							window.location.reload(true);
						},
						error: function(result) {
							alert("fail");
							console.log(result);
							window.location.reload(true);
				        }
					});  
				}
	}
	</script>

	<script type="text/javascript"
		src="js/angular.min.js"></script>
	<script type="text/javascript"
		src="js/jquery.min.js"></script>
	<script type="text/javascript"
		src="js/bootstrap.min.js"></script>
	<script type="text/javascript"
		src="js/lodash.min.js"></script>
	<script type="text/javascript" src="js/jszip.js"></script>
	<script type="text/javascript" src="js/xlsx.js"></script>
	<script type="text/javascript" src="js/xlsx-reader.js"></script>

	<script type="text/javascript">
	
		var excelValue;
		var app = angular.module("App", []);

		app.factory("XLSXReaderService", [ '$q', '$rootScope',
				function($q, $rootScope) {
					var service = function(data) {
						angular.extend(this, data);
					}

					service.readFile = function(file, readCells, toJSON) {
						var deferred = $q.defer();

						XLSXReader(file, readCells, toJSON, function(data) {
							$rootScope.$apply(function() {
								deferred.resolve(data);
							});
						});

						return deferred.promise;
					}

					return service;
				} ]);

		app.controller('PreviewController',
				function($scope, XLSXReaderService) {
					$scope.showPreview = true;
					$scope.showJSONPreview = false;
					$scope.json_string = "";

					$scope.fileChanged = function(files) {
						$scope.isProcessing = true;
						$scope.sheets = [];
						$scope.excelFile = files[0];
						XLSXReaderService.readFile($scope.excelFile,
								$scope.showPreview, $scope.showJSONPreview)
								.then(function(xlsxData) {
									$scope.sheets = xlsxData.sheets;
									$scope.isProcessing = false;
								});
					}

					$scope.updateJSONString = function() {
						alert(JSON.stringify(
								$scope.sheets[$scope.selectedSheetName], null,
								2));
						$scope.json_string = JSON.stringify(
								$scope.sheets[$scope.selectedSheetName], null,
								2);
						value = $scope.json_string ;
					}

					$scope.showPreviewChanged = function() {
						if ($scope.showPreview) {
							$scope.showJSONPreview = false;
							$scope.isProcessing = true;
							XLSXReaderService.readFile($scope.excelFile,
									$scope.showPreview, $scope.showJSONPreview)
									.then(function(xlsxData) {
										excelValue = xlsxData;
										$scope.sheets = xlsxData.sheets;
										$scope.isProcessing = false;
									});
						}
					}
				});
	</script>
<%}else{
	response.sendRedirect("/getreport");
}%>

</body>
</html>
<%@ include file="footer.jsp"%>