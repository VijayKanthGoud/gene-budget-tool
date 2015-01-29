<%@ include file="header.jsp"%>

<html>
<head>
<link rel="stylesheet" type="text/css"
	href="http://netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"
	href="http://cdnjs.cloudflare.com/ajax/libs/prism/0.0.1/prism.min.css">
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
	<div ng-app="App" class="container ng-scope">
		<div ng-controller="PreviewController" class="ng-scope">
			<div class="form-group">
				<label for="excel_file">Excel File</label> <input type="file"
					name="excel_file" accept=".xlsx"
					onchange="angular.element(this).scope().fileChanged(this.files);"
					required="true">
			</div>

			<div ng-show="isProcessing" style="display: none;">
				<span>Processing ...</span>
			</div>

			<div class="form-group">
				<label for="sheet_name">Sheet Name</label> <select id="sheet_name"
					class="form-control ng-pristine ng-invalid ng-invalid-required"
					ng-change="showPreviewChanged()" ng-model="selectedSheetName"
					required="required" ng-required="true"
					ng-options="sheetName as sheetName for (sheetName, sheetData) in sheets"><option
						value="" class="">---- Select a sheet ----</option></select>
			</div>
			<button type="button" onclick="uploadData()">Upload data</button>
		</div>
	</div>


	<script>
	function uploadData(){
		console.log(JSON.stringify(excelValue.sheets["Main"].data));
		  $.ajax({
				url : '/adminjsonupload',
				type : 'POST',
				dataType : 'text',
				data : {objarray: JSON.stringify(excelValue.sheets["Main"].data) },
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