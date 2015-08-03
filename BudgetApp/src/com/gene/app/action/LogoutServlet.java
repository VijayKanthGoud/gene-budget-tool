package com.gene.app.action;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		UserService userService = UserServiceFactory.getUserService();
		String logoutLink = userService.createLogoutURL("/");
		resp.sendRedirect(logoutLink);

	}
}
