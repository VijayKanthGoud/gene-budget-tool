package com.gene.app.action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*@SuppressWarnings("serial")
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
}*/

/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.PMF;
import com.gene.app.model.GtfReport;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


/**
 * A simple servlet that proxies reads and writes to its Google Cloud Storage bucket.
 */
@SuppressWarnings("serial")
public class BackupServlet extends HttpServlet {

  public static final boolean SERVE_USING_BLOBSTORE_API = false;
  MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
  /**
   * This is where backoff parameters are configured. Here it is aggressively retrying with
   * backoff, up to 10 times but taking no more that 15 seconds total to do so.
   */
  private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
      .initialRetryDelayMillis(10)
      .retryMaxAttempts(10)
      .totalRetryPeriodMillis(15000)
      .build());

  /**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
  private static final int BUFFER_SIZE = 2 * 1024 * 1024;

  /**
   * Retrieves a file from GCS and returns it in the http response.
   * If the request path is /gcs/Foo/Bar this will be interpreted as
   * a request to read the GCS file named Bar in the bucket Foo.
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		  doPost(req, resp);
  }
  public void removeExistingProject() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query q = pm.newQuery(GtfReport.class);
			List<GtfReport> gtfReports = (List<GtfReport>) q.execute();
			pm.deletePersistentAll(gtfReports);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("closing all datastore call.");
			pm.close();
		}
	}
  public void saveAllDataToDataStore(List<GtfReport> gtfReportList){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		//Transaction tx = pm.currentTransaction();
		try {
		//	tx.begin();
			if(gtfReportList!=null && !gtfReportList.isEmpty()){
				for(GtfReport gtfRpt: gtfReportList){
					pm.makePersistent(gtfRpt);
				}
			}
			//pm.makePersistentAll(gtfReportList);
		//	tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			if (tx.isActive())
		    {
		        tx.rollback();
		    }*/
			pm.close();
		//}
	}
  private static String getStringFromInputStream(InputStream is) {
	  
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
  /**
   * Writes the payload of the incoming post as the contents of a file to GCS.
   * If the request path is /gcs/Foo/Bar this will be interpreted as
   * a request to create a GCS file named Bar in bucket Foo.
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	  /*String requestURI = "/gcs/budget-mgmt-tool-bucket/GtfReport";
	  String[] splits = requestURI.split("/", 4);//req.getRequestURI().split("/", 4);
*/
	  String requestURI = req.getRequestURI();
	  String[] splits = requestURI.split("/", 4);
	  Date datetime = new Date();
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
	  String date = sdf.format(datetime);
	  System.out.println("datetime = "+date);
	  splits[3] = splits[3]+"_"+date; 
    GcsOutputChannel outputChannel =
        gcsService.createOrReplace(getFileName(splits), GcsFileOptions.getDefaultInstance());
    Gson gson = new Gson();
    List<GtfReport> gtfList = getAllReportDataFromCache("37673");
    System.out.println("gtfList:::::::"+gtfList);
	String g = gson.toJson(gtfList);
	System.out.println("g:::::::"+g);
	InputStream is = new ByteArrayInputStream(g.getBytes());
    copy(is, Channels.newOutputStream(outputChannel));
  }

  private GcsFilename getFileName(String [] splits) {
	  //System.out.println("req.getRequestURI()"+req.getRequestURI());
    if (!splits[0].equals("") || !splits[1].equals("gcs")) {
      throw new IllegalArgumentException("The URL is not formed as expected. " +
          "Expecting /gcs/<bucket>/<object>");
    }
    return new GcsFilename(splits[2], splits[3]);
  }
  
  public List<GtfReport> getAllReportData() {
	  PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		List<GtfReport> gtfList = new ArrayList<GtfReport>();
		try{
			List<GtfReport> results = (List<GtfReport>) q.execute();
			if(!results.isEmpty()){
			for(GtfReport gtfReport : results){
					gtfList.add(gtfReport);
					System.out.println("gftReport plannedMap = "+gtfReport.getPlannedMap());
					System.out.println("gftReport getBenchmarkMap = "+gtfReport.getBenchmarkMap());
					System.out.println("gftReport getAccrualsMap = "+gtfReport.getAccrualsMap());
					System.out.println("gftReport getVariancesMap = "+gtfReport.getVariancesMap());
			}
			}
		//saveAllReportDataToCache("307673",gtfList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			q.closeAll();
			pm.close();
		}
		return gtfList;
	}
  
  public void saveAllReportDataToCache(String costCenter,List<GtfReport> gtfReportList){
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		cache.put(costCenter, gtfReportList);
		//cache.put(BudgetConstants.GMEMORI_COLLECTION, gtfReportList);
	}

  /**
   * Transfer the data from the inputStream to the outputStream. Then close both streams.
   */
  private void copy(InputStream input, OutputStream output) throws IOException {
    try {
      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead = input.read(buffer);
      while (bytesRead != -1) {
        output.write(buffer, 0, bytesRead);
        bytesRead = input.read(buffer);
      }
    } finally {
      input.close();
      output.close();
    }
  }
  
  public List<GtfReport> getAllReportDataFromCache(String costCenter){
	  List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		gtfReportList = (List<GtfReport>)cache.get(costCenter);
		//gtfReportList = getReportDataByStatus(gtfReportList);
		if(gtfReportList==null || gtfReportList.size()==0){
			System.out.println("gtfReportList getAllReportDataFromCache = "+gtfReportList);
			gtfReportList = getAllReportData();
			}
		//cache.clearAll();
		return gtfReportList;
	}
  public Map<String,GtfReport> getReportDataByStatus(Map<String,GtfReport> gtfReportList){
		Map<String,GtfReport> gtfRptMap = new HashMap<String,GtfReport>();
		if(gtfReportList!=null && !gtfReportList.isEmpty()){
			for(Map.Entry<String, GtfReport> gtfEntry:gtfReportList.entrySet()){
				if(!"Disabled".equals(gtfEntry.getValue().getStatus())){
					gtfRptMap.put(gtfEntry.getKey(), gtfEntry.getValue());
				}
			}
		}
		return gtfRptMap;
	}
}
