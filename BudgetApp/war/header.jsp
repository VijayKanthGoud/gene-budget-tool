<%@ page import="java.security.Principal"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.google.appengine.api.memcache.*"%>

<head>
<title>Budget App</title>
<link rel="stylesheet" type="text/css" href="css/mystyle.css"></link>
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

	<p class="topnav">
		<%
			UserService userService = UserServiceFactory.getUserService();
			String requestUri = request.getRequestURI();
			Principal userPrincipal = request.getUserPrincipal();
			if (userPrincipal != null) {
		%>
		<span class="nav-item"> <a href="/">Home</a>
		</span> <span class="nav-item"> <a href="/getreport"> Projects
		</a>
		</span> <span class="nav-item"> <a href="/displayReports"> Reports</a>
		</span>
		<%
			}
			if (userPrincipal == null) {
				String loginLink = userService.createLoginURL(requestUri);
		%>
		<span class="nav-item"> <a href=<%=loginLink%>>Login</a>
		</span>
		<%
			} else {
				if (userPrincipal != null && userService.isUserAdmin()) {
		%>
		<span class="nav-item"> <a href="/developer">Developer</a>
		</span>
		<%
			}
				String logoutLink = userService.createLogoutURL(requestUri);
		%>
		<span class="nav-item"> <a href=<%=logoutLink%>>Logout</a>
		</span>
		<%
			}
		%>
	</p>