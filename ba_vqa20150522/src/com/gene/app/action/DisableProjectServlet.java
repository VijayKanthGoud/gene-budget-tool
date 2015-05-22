package com.gene.app.action;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.DBUtil;
import com.gene.app.util.Util;


/**
 * The Class DisableProjectServlet.
 */
@SuppressWarnings("serial")
public class DisableProjectServlet extends HttpServlet {
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger
			.getLogger(DisableProjectServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String gMemoriId = "";
		String costCenter = "";
		if(Util.isNullOrEmpty(req.getParameter("gMem")) && Util.isNullOrEmpty(req.getParameter("costCenter"))){
			gMemoriId = req.getParameter("gMem");
			costCenter = req.getParameter("costCenter");
		}
		LOGGER.log(Level.INFO, "Request received to disable: " + gMemoriId);
		DBUtil util = new DBUtil();
		util.disableProject(gMemoriId, costCenter);
	}
}
