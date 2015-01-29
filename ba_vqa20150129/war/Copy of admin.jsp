<%@ include file="header.jsp"%>

 <div height = 20px>&nbsp;</div>
 <button class="myButton" value="" onclick="openUploadPopUp();" style="height: 25px; font-size: 12px; letter-spacing:1px;  align:center" > Upload excel data</button>

<script>
	<%@ include file="scripts/admin.js"%>
</script>
<div id="uploadWindow">
		<div id="header"
			style="width: 100%; height: 20px; background-color: #005691; color: white; border-top-left-radius: 0.7em; border-top-right-radius: 0.7em; font-size: 17px; letter-spacing: 3px;"  align = center> File Upload
		</div>
		<div align='right' style='padding-right: 100px;'>
			<form action="/adminupload" method="post" enctype="multipart/form-data">
				<br/>
				<span style="font-size: 14px;font-weight: bold;">
					Select File to Upload : &nbsp;&nbsp;&nbsp;
				</span>
				<input type="file" name="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"> <br/>
				<input class='myButton' id="fileUploadBtn" type="submit" value="Upload">
				<input class='myButton' type="button" value="Cancel" onclick="closeUploadWindow();">
			</form>
			
		</div>
	</div>
	<div id="back">	</div>  

<%@ include file="footer.jsp"%>