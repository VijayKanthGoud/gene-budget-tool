package com.gene.app.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.bean.UserRoleInfo;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		readUserRoleInfo();
	}
	public void readUserRoleInfo() {
		System.out.println("results = ");

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(UserRoleInfo.class);
		List results = (List) q.execute();
		Extent<UserRoleInfo> extent = pm.getExtent(UserRoleInfo.class, false);
		for (UserRoleInfo p : extent) {
			System.out.println("UserInfo = " + p.toString());
			
		}
		extent.closeAll();
	}
}
