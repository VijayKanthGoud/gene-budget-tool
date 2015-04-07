package com.gene.app.action;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.UserDataUtil;

@SuppressWarnings("serial")
public class UploadUserDataServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserDataUtil util = new UserDataUtil();
		util.insertCCMapping();
		util.insertUserRoleInfo();
		util.insertBudgetSummary();
		util.insertCutOffDates();
		resp.sendRedirect("/logout");
	}
}
