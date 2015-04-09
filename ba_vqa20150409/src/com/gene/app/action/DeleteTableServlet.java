package com.gene.app.action;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.PMF;
import com.gene.app.dao.UserDataUtil;
import com.gene.app.model.BudgetSummary;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@SuppressWarnings("serial")
public class DeleteTableServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String tableName = req.getParameter("name");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List listToBeDeleted = null;
		Query q;
		MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
		switch (tableName) {
		case "gtfreport":
			q = pm.newQuery(GtfReport.class);
			listToBeDeleted = (List) q.execute();
			break;
		case "budgetsummary":
			q = pm.newQuery(BudgetSummary.class);
			listToBeDeleted = (List) q.execute();
			break;
		case "costcenterbrand":
			q = pm.newQuery(CostCenter_Brand.class);
			listToBeDeleted = (List) q.execute();
			break;
		case "userroleinfo":
			q = pm.newQuery(UserRoleInfo.class);
			listToBeDeleted = (List) q.execute();
			break;
		default:
			break;
		}
		if(listToBeDeleted != null && !listToBeDeleted.isEmpty()){
			pm.deletePersistentAll(listToBeDeleted);
			cache.clearAll();
		}
		resp.sendRedirect("/deleteproject.jsp");
	}
}
