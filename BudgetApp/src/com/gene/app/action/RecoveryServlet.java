package com.gene.app.action;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.DBUtil;
import com.gene.app.dao.PMF;
import com.gene.app.model.GtfReport;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class RecoveryServlet extends HttpServlet {
	 public static final boolean SERVE_USING_BLOBSTORE_API = false;
	 
	 private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
     .initialRetryDelayMillis(10)
     .retryMaxAttempts(10)
     .totalRetryPeriodMillis(15000)
     .build());

 /**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
 private static final int BUFFER_SIZE = 2 * 1024 * 1024;
 
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String URI = req.getRequestURI();
		String[] splits = req.getRequestURI().split("/", 4);
		GcsFilename fileName = getFileName(splits);
		if (SERVE_USING_BLOBSTORE_API) {
			BlobstoreService blobstoreService = BlobstoreServiceFactory
					.getBlobstoreService();
			BlobKey blobKey = blobstoreService
					.createGsBlobKey("/gs/" + fileName.getBucketName() + "/"
							+ fileName.getObjectName());
			blobstoreService.serve(blobKey, resp);
		} else {
			GcsInputChannel readChannel = gcsService
					.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
			InputStream is = Channels.newInputStream(readChannel);
			String gtfRptString = getStringFromInputStream(is);
			System.out.println("gtfRptString = " + gtfRptString);
			Gson gson = new Gson();
			List<GtfReport> gtfRptList = gson.fromJson(gtfRptString,
					new TypeToken<List<GtfReport>>() {
					}.getType());
			System.out.println("gtfRptList = " + gtfRptList);
			saveAllDataToDataStore(gtfRptList);
			copy(Channels.newInputStream(readChannel), resp.getOutputStream());
			RequestDispatcher rd = req.getRequestDispatcher("/recovery_conversion.jsp");
			try {
				rd.forward(req, resp);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	 
	 private GcsFilename getFileName(String [] splits) {
		  //System.out.println("req.getRequestURI()"+req.getRequestURI());
	    if (!splits[0].equals("") || !splits[1].equals("gcs")) {
	      throw new IllegalArgumentException("The URL is not formed as expected. " +
	          "Expecting /gcs/<bucket>/<object>");
	    }
	    return new GcsFilename(splits[2], splits[3]);
	  }
	 
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
}
