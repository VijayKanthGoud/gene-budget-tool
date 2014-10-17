<%@ page import="java.security.Principal"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.google.appengine.api.memcache.*"%>

<head>
<title>Budget App</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/menustyles.css">
<script src="http://code.jquery.com/jquery-latest.min.js"
	type="text/javascript"></script>
<script src="scripts/menuscript.js"></script>
</head>
<html>

<body bgcolor="#F4F6F8">
	<%!private static Logger log = Logger.getLogger("header.jsp");%>

	<%
		UserService userService = UserServiceFactory.getUserService();
		String requestUri = request.getRequestURI();
		Principal userPrincipal = request.getUserPrincipal();
		String loginLink = userService.createLoginURL(requestUri);
		Boolean isAdmin = false;
		String userName = "";
		String logoutLink = "";

		if (userPrincipal != null) {
			logoutLink = userService.createLogoutURL(requestUri);
			User user = userService.getCurrentUser();
			isAdmin = userService.isUserAdmin();
			//Set username message
			if (isAdmin) {
				userName = "Welcome, " + user.getNickname() + "!";
			} else {
				userName = "Welcome, " + user.getNickname() + "(Admin) !";
			}
	%>

	<table style="width: 100%;">
		<tr>
			<td style="width: 25%; float: left"><a id="logo-container"
				href="/"> <img src="images/logo.png"
					alt="Genentech - A Member of the Roche Group" width="120"
					height="32" class="gene-logo has-high-res">
			</a></td>

			<td style="width: 50%; align: center">
				<div style="text-align: center; vertical-align: middle;">
					<h1
						style="color: #105596; font-family: 'trebuchet ms'; font-size: 18px; font-variant: small-caps; letter-spacing: 5px; padding-top: 8px;">Gmemory Budget
						Management Tool</h1>

				</div>
			</td>
			<td style="width: 25%; float: right">
				<div style="text-align: right;">
					<span
						style="color: #105596; font-family: 'trebuchet ms'; font-size: 14px;"><i><%=userName%></i>
					</span>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="5" style="background-color: #e1e2e2; width: 100%"></td>
		</tr>
	</table>
<div style="height:5px;"></div>
	<div id='cssmenu'>
		<ul>
			<li><a href='/'><span>Home</span></a></li>
			<li class='last'><a href='/getreport'><span>Projects</span></a></li>
			<li class='last'><a href='/displayReports'><span>Reports</span></a></li>
			<%
				if (isAdmin) {
			%>
			<li class='last'><a href='/developer'><span>Admin</span></a></li>
			<%
				}
			%>
			<li class='last'><a href=<%=logoutLink%>><span>Logout</span></a></li>
		</ul>
	</div>
	<%
		}
	%>