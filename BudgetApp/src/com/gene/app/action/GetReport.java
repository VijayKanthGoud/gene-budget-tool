package com.gene.app.action;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.gene.app.dao.DBUtil;
import com.gene.app.model.BudgetSummary;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.GtfReport;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class GetReport extends HttpServlet {
	
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	DBUtil util = new DBUtil();
	String costCenter = "";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		HttpSession session = req.getSession();
		resp.setContentType(BudgetConstants.contentType);
		UserRoleInfo user = (UserRoleInfo) session.getAttribute("userInfo");
		UserService userService;
		String email = "";
		Map<String, GtfReport> gtfReports = new LinkedHashMap<String, GtfReport>();
		String selectedBrand = req.getParameter("brandValue");
		String selectedView = req.getParameter("selectedView");
		String selectedCC = req.getParameter("getCCValue");
		String gMemoriId = req.getParameter("gMemoriId");
		if (user == null) {
			userService = UserServiceFactory.getUserService();
			String requestUri = req.getRequestURI();
			Principal userPrincipal = req.getUserPrincipal();
			String loginLink = userService.createLoginURL(requestUri);
			if (userPrincipal == null) {
				resp.sendRedirect(loginLink);
				return;
			}
			costCenter = "";
			email = userService.getCurrentUser().getEmail();
			user = util.readUserRoleInfo(email);
			if (user == null || user.getUserName() == null) {
				resp.sendRedirect("/unautherizedUser.jsp");
				return;
			}
		} else {
			email = user.getEmail();
		}
		user.setSelectedCostCenter((selectedCC == null || ""
				.equalsIgnoreCase(selectedCC.trim())) ? user
				.getSelectedCostCenter() : selectedCC);
		List<GtfReport> gtfReportList = null;
		Map<String, Double> userBrandMap = new LinkedHashMap<String, Double>();
		Object[] myBrands = {};

		gtfReports = util.getAllReportDataFromCache(user
				.getSelectedCostCenter());

		if (user.getRole() != null
				&& !"".equalsIgnoreCase(user.getRole().trim())
				&& user.getRole().contains("Admin")
				&& !(gMemoriId != null && !""
						.equalsIgnoreCase(gMemoriId.trim()))) {
			if (selectedView == null
					|| "".equalsIgnoreCase(selectedView.trim())
					|| selectedCC == null
					|| "".equalsIgnoreCase(selectedCC.trim())) {
				selectedView = "My Brands";
				selectedCC = "7135";
				user.setSelectedCostCenter(selectedCC);
				user.setCostCenter("7527:7034:7035:7121:7712:7135:7713:7428:7512:7574:7136:7138:7004");
			}
			if (!Util.isNullOrEmpty(selectedBrand)) {
				CostCenter_Brand ccBrandMap = new CostCenter_Brand();
				List<CostCenter_Brand> costCenterList = util
						.readCostCenterBrandMappingData();
				for (CostCenter_Brand ccBrand : costCenterList) {
					if (selectedCC.equalsIgnoreCase(ccBrand.getCostCenter()
							.trim())) {
						ccBrandMap = ccBrand;
						break;
					}
				}
				if (ccBrandMap.getBrandFromDB().contains("Avastin")) {
					selectedBrand = "Avastin";
				} else {
					userBrandMap = util
							.getBrandMap(ccBrandMap.getBrandFromDB());
					Map<String, Double> sortedMap = new TreeMap<String, Double>(
							userBrandMap);
					myBrands = sortedMap.keySet().toArray();
					selectedBrand = myBrands[0].toString();
				}
			}
		}
		gtfReportList = getRptListForLoggedInUser(user, selectedView,
				selectedBrand, gtfReports, gMemoriId, req);

		List<GtfReport> queryGtfRptList = new ArrayList<GtfReport>();
		if (gMemoriId != null && !"".equalsIgnoreCase(gMemoriId.trim())) {
			req.setAttribute("accessreq", "external");
			req.setAttribute("selectedView", "My Projects");
			if (costCenter != null && !"".equalsIgnoreCase(costCenter)) {
				req.setAttribute("getCCValue", costCenter);
			}
		}
		req.setAttribute("selectedView", selectedView);
		req.setAttribute("brandValue", selectedBrand);
		req.setAttribute("getCCValue", selectedCC);
		req.setAttribute("accessreq", "internal");
		queryGtfRptList = gtfReportList;

		if (queryGtfRptList != null && !queryGtfRptList.isEmpty()) {
			Collections.sort(queryGtfRptList, new Comparator<GtfReport>() {
				public int compare(GtfReport o1, GtfReport o2) {
					if (o1.getFlag() == o2.getFlag()) {
						if ((o1.getProjectName()).compareTo(o2.getProjectName()) == 0) {
							return (o1.getgMemoryId()).compareTo(o2
									.getgMemoryId());
						}
						return (o1.getProjectName()).compareTo(o2
								.getProjectName());
					}
					return o1.getFlag() - o2.getFlag();
				}
			});
			queryGtfRptList = util.calculateVarianceMap(queryGtfRptList);
		}

		req.setAttribute(BudgetConstants.REQUEST_ATTR_GTFReports,
				queryGtfRptList);
		DBUtil util = new DBUtil();
		BudgetSummary summary = util.readBudgetSummary(user
				.getSelectedCostCenter());
		req.setAttribute("user", user);
		session.setAttribute(BudgetConstants.REQUEST_ATTR_SUMMARY, summary);
		RequestDispatcher rd = req
				.getRequestDispatcher(BudgetConstants.GetReport_REDIRECTURL);
		try {
			rd.forward(req, resp);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	public List<GtfReport> getReportListByBrand(
			Map<String, GtfReport> gtfReports, String userType,
			String selectedBrand) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		if (gtfReports != null) {

			for (Map.Entry<String, GtfReport> gtfEntry : gtfReports.entrySet()) {
				gtfReport = gtfEntry.getValue();
				if ((selectedBrand != null && !"".equals(selectedBrand.trim()))
						&& (gtfReport.getBrand().trim().toLowerCase())
								.equalsIgnoreCase(selectedBrand.toLowerCase()
										.trim())) {
					gtfReportList.add(gtfReport);
				}
			}
		}
		return gtfReportList;
	}

	public List<GtfReport> getReportListCC(Map<String, GtfReport> gtfReports) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;
		if (gtfReports != null) {
			for (Map.Entry<String, GtfReport> gtfEntry : gtfReports.entrySet()) {
				gtfReport = gtfEntry.getValue();
				gtfReportList.add(gtfReport);
			}
		}
		return gtfReportList;
	}

	public List<GtfReport> getRptListForLoggedInUser(UserRoleInfo user,
			String selectedView, String selectedBrand,
			Map<String, GtfReport> gtfReports, String gMemoriId,
			HttpServletRequest req) {
		boolean found = false;
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		if (selectedView == null || "".equalsIgnoreCase(selectedView.trim())) {
			if (selectedBrand != null
					&& !"".equalsIgnoreCase(selectedBrand.trim())) {
				selectedView = "My Brands";
			} else {
				selectedView = "";
			}
		}
		switch (selectedView) {
		case "My Projects":
			gtfReportList = getReportList(gtfReports,
					BudgetConstants.USER_ROLE_PRJ_OWNER, user.getEmail());
			break;
		case "My Brands":
			if (selectedBrand != null
					&& !"".equalsIgnoreCase(selectedBrand.trim())) {
				gtfReportList = getReportListByBrand(gtfReports,
						BudgetConstants.USER_ROLE_BRAND_OWNER, selectedBrand);
			}
			break;
		case "My Cost Center":
			gtfReportList = getReportListCC(gtfReports);
			break;
		case "gMemori":
			if (user.getCostCenter().contains(":")) {
				for (String cc : user.getCostCenter().split(":")) {
					Map<String, GtfReport> gtfReportMap = util
							.getAllReportDataFromCache(cc);
					if (gtfReportMap.containsKey(gMemoriId)) {
						gtfReportList = getReportList(gtfReportMap,
								BudgetConstants.USER_ROLE_PRJ_OWNER,
								user.getEmail());
						costCenter = cc;
						user.setSelectedCostCenter(costCenter);
						found = true;
						break;
					}
				}
			} else {
				gtfReportList = getReportList(gtfReports,
						BudgetConstants.USER_ROLE_PRJ_OWNER, user.getEmail());
				found = true;
			}
			if (!found) {
				costCenter = user.getSelectedCostCenter();
				gtfReportList = getReportList(gtfReports,
						BudgetConstants.USER_ROLE_PRJ_OWNER, user.getEmail());
			}
			break;
		default:
			gtfReportList = getReportList(gtfReports,
					BudgetConstants.USER_ROLE_PRJ_OWNER, user.getEmail());
			selectedView = "My Projects";
		}
		return gtfReportList;
	}

	public List<GtfReport> getReportList(Map<String, GtfReport> gtfReports,
			String userType, String email) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		GtfReport gtfReport = null;

		if (gtfReports != null) {

			for (Map.Entry<String, GtfReport> gtfEntry : gtfReports.entrySet()) {
				gtfReport = gtfEntry.getValue();
				if ((email != null && !"".equals(email.trim()))
						&& (gtfReport.getEmail().trim().toLowerCase())
								.contains(email.toLowerCase())) {
					gtfReportList.add(gtfReport);
				}
			}
		}
		return gtfReportList;
	}

}
