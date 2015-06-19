package com.gene.app.action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class BackupServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
		    URL url = new URL("https://google.com/");
		    URLConnection URLConnection = url.openConnection();
		    URLConnection.connect();
		} 
		catch (MalformedURLException e) { 
		    System.out.println("Malformed url");
		} 
		catch (IOException e) {   
			System.out.println("IO exception");
		}
	}
}