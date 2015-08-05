package com.gene.app.action;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.util.BudgetConstants;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class RecoverDataServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String filename = req.getParameter("filename");
		Queue queue = QueueFactory.getDefaultQueue();
		System.out.println("filename = "+filename);
		TaskOptions recoveryTask = TaskOptions.Builder.withUrl("/gcs/"+BudgetConstants.BUCKET_NAME+"/"+filename).method(Method.GET);
		queue.add(recoveryTask);
	}
}
