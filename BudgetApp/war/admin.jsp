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
</head>

<body>



</br>
</br>
<table border="#FFFFFF" align="center" width="250px" >
<!-- <tr style="padding-left:100px" bgcolor="">
<td> -->
<div align="center">
<button type="button" class="form-control-header">Upload project data: </button>
</div>
<!-- </td>
</tr>

<tr >
<td > -->
<!-- <div style="padding-left: 250px; padding-bottom: 100px;" > -->
	<div style="padding-left: 400px; padding-top: 30px;" ng-app="App" class="container ng-scope">
		<div ng-controller="PreviewController" class="ng-scope">
			<div class="form-group">
				<span style="font-size: 14px; font-weight: bold">Excel File :</span> <input class="form-control-button" type="file"
					name="excel_file" accept=".xlsx"
					onchange="angular.element(this).scope().fileChanged(this.files);"
					required="true">
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
			
			<div class="form-group">
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
			<span style="font-size: 14px; font-weight: bold"> From Line number: </span><input  class="form-control-textbox" type="number" name="frmLine" id="From"><br>
  			<span style="font-size: 14px; font-weight: bold">To Line number: </span> <input class="form-control-textbox" type="number" name="toLine" id="To"><br>
			
			</div>
			</br>
			<div style="padding-left: 100px;">
			<button type="button" class="form-control-submit" onclick="uploadData()">Upload data</button>
			</div>
		</div>
	</div>
	<!-- </div> -->
<!-- </td>
</tr> -->

</table>
	<script>
	function uploadData(){
		console.log(JSON.stringify(excelValue.sheets["Main"].data));
		  $.ajax({
				url : '/adminjsonupload',
				type : 'POST',
				dataType : 'text',
				data : {objarray: JSON.stringify(excelValue.sheets["Main"].data),
					costCenter : $('#getCostCenter').val(),
					inputFrom :  $('#From').val(),
					inputTo :  $('#To').val()},
				success : function(result) {
					alert("Uploaded successfully");
					console.log('Data saved successfully');
				},
				error: function(result) {
					alert("fail");
					console.log(result);
		        }
			});   
	}
	</script>

	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/angularjs/1.0.7/angular.min.js"></script>
	<script type="text/javascript"
		src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
	<script type="text/javascript"
		src="http://netdna.bootstrapcdn.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
	<script type="text/javascript"
		src="http://cdnjs.cloudflare.com/ajax/libs/lodash.js/2.4.1/lodash.min.js"></script>
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

</body>
</html>
<%@ include file="footer.jsp"%>