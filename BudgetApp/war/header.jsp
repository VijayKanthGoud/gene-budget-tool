<%@page import="java.util.LinkedHashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.gene.app.model.UserRoleInfo"%>
<%@ page import="java.security.Principal"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="java.util.logging.*"%>
<%@ page import="com.google.appengine.api.memcache.*"%>
<%@ page import="com.gene.app.dao.*" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheService" %>
<%@ page import="com.google.appengine.api.memcache.MemcacheServiceFactory" %>

<head>
<title>gMemori Budgeting Tool</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/menustyles.css">
<script src="SlickGrid-master/lib/jquery-1.7.min.js"
	type="text/javascript"></script>
<script src="scripts/menuscript.js"></script>
</head>
<html>

<body bgcolor="#F4F6F8">
	<%!
		private static Logger LOGGER = Logger.getLogger("header.jsp");
	%>
	<%
		LOGGER.log(Level.INFO, "Inside Header.jsp...");
		UserService userService = UserServiceFactory.getUserService();
		String requestUri = request.getRequestURI();
		Principal userPrincipal = request.getUserPrincipal();
		String loginLink = userService.createLoginURL(requestUri);
		String email = userService.getCurrentUser().getEmail();
		LOGGER.log(Level.INFO, "Logged in user : "+email);
		request.setAttribute("email", email);
		Boolean isAdmin = false;
		String userName = "";
		String logoutLink = "";
		DBUtil util = new DBUtil();
			
		/*  UserDataUtil util1 = new UserDataUtil();
		util1.insertCCMapping();
		util1.insertUserRoleInfo();
		util1.insertBudgetSummary(); */    
		if (userPrincipal != null) {
			User user = userService.getCurrentUser();
			session.setAttribute("loggedInUser",user);
			isAdmin = userService.isUserAdmin();
			boolean isGeneUser = false;//util.readUserRoleInfo(email,costCenter);
			UserRoleInfo userInfo = util.readUserRoleInfo(email);
			String role = "";
			if(userInfo!=null){
				session.setAttribute("userInfo", userInfo);
				if(email.equalsIgnoreCase(userInfo.getEmail())){
					isGeneUser = true;
					role = userInfo.getRole();
					LOGGER.log(Level.INFO, "Logged in user role : "+role);
				}
			}
	%>
				
			<%
			if (userService!= null && userService.isUserLoggedIn()    /* && isGeneUser   */) {
				userName = "Welcome, " + user.getNickname() + " (" + role +") !";
			} else{%>
				alert("You are not authorized to access the application!");
			<%	response.sendRedirect(userService.createLogoutURL(requestUri));
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
						style="color: #105596; font-family: 'trebuchet ms'; font-size: 25px; letter-spacing: 5px; padding-top: 8px;">
						g<span style="font-variant: small-caps;">Memori Budgeting
							Tool</span>
					</h1>

				</div>
			</td>
			<td style="width: 25%; float: right">
				<div style="text-align: right;">
					<span
						style="color: #105596; font-family: 'trebuchet ms'; font-size: 14px; white-space: nowrap;"><i><%=userName%></i>
					</span>
				</div>
			</td>
		</tr>
		<tr>
			<td colspan="5" style="background-color: #e1e2e2; width: 100%"></td>
		</tr>
	</table>
	<div style="height: 5px;"></div>
	<div id='cssmenu'>
		<ul>
			<li><a style = "cursor: hand;" id="homeLink" ><span>Home</span></a></li>
			<li class='last'><a href='/'><span>Reports</span></a></li>
			<%
				if (isAdmin) {
			%>
			<li class='last'><a href='/admin'><span>Admin</span></a></li>
			<%
				}
			%>
			<li class='last'><a href='/logout'><span>Logout</span></a></li>
		</ul>
	</div>
	<%
		}
	%>
