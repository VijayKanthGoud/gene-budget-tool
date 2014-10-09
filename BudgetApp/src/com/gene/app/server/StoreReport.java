package com.gene.app.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.bean.GtfReport;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.labs.repackaged.org.json.XML;

@SuppressWarnings("serial")
public class StoreReport extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		System.out.println("Hello world");
		resp.getWriter().println("Hello, world");
		
		String objarray = req.getParameter("objarray").toString();
		System.out.println(objarray);
		try {
		        JSONObject jsonObj = new JSONObject(objarray);          

		        String xmlString= XML.toString(jsonObj);
		        System.out.println("JSON to XML: " + xmlString);
		    } catch (JSONException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    }
	}
	
	public void generateProjectIdUsingJDOTxn(List<GtfReport> gtfReport){
		
		System.out.println("gtfReport"+gtfReport);
        final PersistenceManagerFactory pmfInstance =
                JDOHelper.getPersistenceManagerFactory("transactions-optional");
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
        try{
               pm.makePersistentAll(gtfReport);
        }catch(Exception e){
               e.printStackTrace();
        }
        finally{
               pm.close();
        }
        
 }

}
