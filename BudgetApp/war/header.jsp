<%@ page import="java.security.Principal"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.google.appengine.api.memcache.*"%>

<head>
<title>Budget App</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/menustyles.css">
   <script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
   <script src="scripts/menuscript.js"></script>
</head>
<html>

<body bgcolor="#F4F6F8">
	<%!private static Logger log = Logger.getLogger("header.jsp");%>
	<table>
		<tr>
			<td style="width: 50%"><div style="float: left;">
					<a id="logo-container" href="/">
						<h1 class="logo" style="padding-left: 10px;">
							<img src="images/logo.png"
								alt="Genentech - A Member of the Roche Group" width="120"
								height="32" class="gene-logo has-high-res">
						</h1>
					</a>
				</div></td>
			<td style="width: 23%">
				<div style="float: right;">
					<h1
						style="color: #105596; font-family: 'trebuchet ms'; font-size: 25px; font-variant: small-caps; letter-spacing: 2px;">Budget
						Management Tool</h1>
				</div>
			</td>
		</tr>
	</table>

	<div id='cssmenu'>
	<ul>
		<%
			UserService userService = UserServiceFactory.getUserService();
			String requestUri = request.getRequestURI();
			Principal userPrincipal = request.getUserPrincipal();
			if (userPrincipal != null) {
		%>
		<li><a href='/'><span>Home</span></a></li>
		<li class='last'><a href='/getreport'><span>Projects</span></a></li>
		<li class='last'><a href='/displayReports'><span>Reports</span></a></li>
		<%
			}
			if (userPrincipal == null) {
				String loginLink = userService.createLoginURL(requestUri);
		%>
		<li class='last'><a href=<%=loginLink%>><span>Login</span></a></li>
		<%
			} else {
				if (userPrincipal != null && userService.isUserAdmin()) {
		%>
		<li class='last'><a href='/developer'><span>Admin</span></a></li>
		<%
			}
				String logoutLink = userService.createLogoutURL(requestUri);
		%>
		<li class='last'><a href=<%=logoutLink%>><span>Logout</span></a></li>
		<%
			}
		%>
	</ul>
	</div>