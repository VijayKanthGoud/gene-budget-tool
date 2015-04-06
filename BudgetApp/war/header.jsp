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

<body style="font-family: arial !important;">

<script>
	$(function() {
		$('#homeLink').click(function() {
			if(typeof popUpWindow === 'undefined'){
				openUrl('https://memori-qa.appspot.com');
	    	}else{
	    		var userAccepted = confirm("gMemori app is already opened. Want to reload?");
				if (!userAccepted) {
					if (window.focus) {
						popUpWindow.focus()
					}
			    	return;
				}else{
					openUrl('https://memori-qa.appspot.com');
					if (window.focus) {
						popUpWindow.focus()
					}
				}
	    	}
		});
	});
	function openUrl(url){
		popUpWindow = window.open(url,'gmemori','');
	}
</script>
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
			if (userService!= null && userService.isUserLoggedIn()  && isGeneUser) {
				userName = "Welcome,   " + user.getNickname() +" !";
			} else{%>
				alert("You are not authorized to access the application!");
			<%	response.sendRedirect(userService.createLogoutURL(requestUri));
			} 
			%>

	<table style="width: 100%;">
	
	
		<tr>

			<td style="width: 15%; float: left"><a id="logo-container"
				href="/"> <img src="images/elephant.png"
					 width="120px"
					height="100px" class="gene-logo has-high-res">
		
			</a></td>

			<td style="width: 50%; align: center">
				<div style="text-align: center; vertical-align: middle;">
					<h1
						style="color: #105596; font-size: 25px; letter-spacing: 5px; padding-top: 8px;">
						<b>g<span style="font-variant: small-caps;">Memori Budgeting
							Tool</span></b>
					</h1>

				</div>
			</td>
			<td style="width: 35%; float: right">
					<div style="text-align:right; font-size: 13px;" class="">
                Questions? Comments? Issues? Please email <a href="#" data-mailto="gMEMORI_support-d@gene.com" id="mailToSupport">gBMT_Support@gene.com</a>
            </div>
					<div style="float:right;top:5%; margin: 0 auto; position: relative;">
                <img src="/images/masLogo.png">
            </div>
				
			</td>
		</tr>
		<tr>
			<td colspan="5" style="background-color: #e1e2e2; width: 100%; font:13px/1.231 arial, helvetica, clean, sans-serif"></td>
		</tr>
	</table>
	<div style="height: 5px;"></div>
	<div id='cssmenu'>
		<ul>
			<li><a style = "cursor: hand;" id="homeLink" ><span>Study</span></a></li>
			<!-- <li class='last'><a href='/'><span>Reports</span></a></li> -->
			<%
				if (role!=null && !"".equalsIgnoreCase(role) && ("Admin".equals(role))) {
			%>
			<li class='last'><a href='#'><span>Imports</span></a>
			<ul>
                        <li class='last'> <a href ="/admin?tab_sel=1" id="tabs-1">FACT</a></li>
                        <li class='last'><a href ="/admin?tab_sel=2" id="#tabs-2">PO Details</a></li>
                       <%
				if (email!=null && !"".equalsIgnoreCase(email) && ("kaviv@gene.com".equals(email) || "siddagov@gene.com".equals(email))) {
			%> 
                        <li class='last'><a href ="/admin?tab_sel=3" id="#tabs-2">User Details</a></li>
                        <% } %>
                    </ul>  
                    </li>  
			<%
				}
			%>
			<!-- <li class='last'><a href='/logout'><span>Logout</span></a></li> -->
			<div style="text-align: right;">
					<span
						style="color: #FFFFFF; font-size: 16px; white-space: nowrap; font-weight: normal;font-style: normal !important;"><i><%=userName%>&nbsp;&nbsp;&nbsp;&nbsp;</i>
					</span>
				</div>
		</ul>
	</div>
	<%
		}
	%>
