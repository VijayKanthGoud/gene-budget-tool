<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<head>
<title>gMemori Budgeting Tool</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/menustyles.css">
<script src="SlickGrid-master/lib/jquery-1.7.min.js"
	type="text/javascript"></script>
</head>
<html>

<body style="font-family: arial !important;">
	<table style="width: 100%; height: 20%">
		<tr>

			<td style="width: 15%; float: left"><a id="logo-container"
				href="/"> <img src="images/elephant.png"
					 width="120px"
					height="100px" class="gene-logo has-high-res">
		
			</a></td>

			<td style="width: 50%; align: center">
				<div style="text-align: center; vertical-align: middle; padding-left:30%">
					<h1
						style="color: #105596; font-size: 25px; letter-spacing: 5px; padding-top: 8px;">
						<b>g<span style="font-variant: small-caps;">Memori Budgeting
							Tool</span></b>
					</h1>

				</div>
			</td>
			<td style="width: 35%; float: right">
					<div style="text-align:right; font-size: 13px;" class="">
                Questions? Comments? Issues? Please email <a href="#" data-mailto="gMEMORI_support-d@gene.com" id="mailToSupport">gMEMORI_support-d@gene.com</a>
            </div>
					<div style="float:right;top:5%; margin: 0 auto; position: relative;">
                <img src="/images/masLogo.png">
            </div>
				
			</td>
		</tr>
	</table>
	<hr/>
	<%
		UserService service = UserServiceFactory.getUserService();
		String url=service.createLogoutURL(service.createLoginURL("/"));
	%>
	<div style="background-color: #EAF4FD;width: 100%;height: 75%;font-size: 20px;
  font-family: arial;">
  <br/>
		You are not authorized to access this application.<br>
		<a href="<%= url%>">Click here</a> to logout and login as another user.
	</div>
	<%@ include file="footer.jsp"%>
	</body>
</html>