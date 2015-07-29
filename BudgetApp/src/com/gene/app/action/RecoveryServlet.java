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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gene.app.dao.PMF;
import com.gene.app.model.GtfReport;
import com.gene.app.util.BudgetConstants;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class RecoveryServlet extends HttpServlet {
	 
	 private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
     .initialRetryDelayMillis(10)
     .retryMaxAttempts(10)
     .totalRetryPeriodMillis(480000)
     .build());

 /**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
 private static final int BUFFER_SIZE = 10 * 1024 * 1024;
 
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DatastoreService datastoreService = DatastoreServiceFactory
				.getDatastoreService();

		String[] splits = req.getRequestURI().split("/", 4);
		GcsFilename fileName = getFileName(splits);

		Entity recoveryStatusEntity = new Entity("RecoveryStatus");
		recoveryStatusEntity.setProperty(BudgetConstants.FILENAME,
				fileName.getObjectName());
		recoveryStatusEntity.setProperty(BudgetConstants.STATUS, "initiated");
		recoveryStatusEntity.setProperty(BudgetConstants.COUNT, 0);
		Key recoveryStatusEntityKey = datastoreService
				.put(recoveryStatusEntity);

		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(
				fileName, 0, BUFFER_SIZE);
		InputStream is = Channels.newInputStream(readChannel);
		String gtfRptString = getStringFromInputStream(is);
		System.out.println("gtfRptString = " + gtfRptString);
		Gson gson = new Gson();
		List<GtfReport> gtfRptList = gson.fromJson(gtfRptString,
				new TypeToken<List<GtfReport>>() {
				}.getType());
		saveAllDataToDataStore(gtfRptList);
		copy(Channels.newInputStream(readChannel), resp.getOutputStream());

		try {
			Entity recoveredStatusEntity = datastoreService.get(recoveryStatusEntityKey);
			recoveredStatusEntity.setProperty(BudgetConstants.STATUS, "completed");
			recoveredStatusEntity.setProperty(BudgetConstants.COUNT, gtfRptList.size());
			datastoreService.put(recoveredStatusEntity);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	 
	 private GcsFilename getFileName(String [] splits) {
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
		 GtfReport gtf = null;
			List<GtfReport> gtfRptListFrmCS = new ArrayList<GtfReport>();
			  if(gtfReportList!=null && !gtfReportList.isEmpty()){
			  for(GtfReport gtfRpt:gtfReportList){
				gtf = new GtfReport();
				gtf.setAccrualsMap(gtfRpt.getAccrualsMap());
				gtf.setBenchmarkMap(gtfRpt.getBenchmarkMap());
				gtf.setBrand(gtfRpt.getBrand());
				gtf.setChildProjectList(gtfRpt.getChildProjectList());
				gtf.setCostCenter(gtfRpt.getCostCenter());
				gtf.setCreateDate(gtfRpt.getCreateDate());
				gtf.setDummyGMemoriId(gtfRpt.isDummyGMemoriId());
				gtf.setEmail(gtfRpt.getEmail());
				gtf.setFlag(gtfRpt.getFlag());
				gtf.setgMemoryId(gtfRpt.getgMemoryId());
				//gtf.setId(gtfRpt.getId());
				gtf.setMultiBrand(gtfRpt.getMultiBrand());
				gtf.setPercent_Allocation(gtfRpt.getPercent_Allocation());
				gtf.setPlannedMap(gtfRpt.getPlannedMap());
				gtf.setPoDesc(gtfRpt.getPoDesc());
				gtf.setPoNumber(gtfRpt.getPoNumber());
				gtf.setProject_WBS(gtfRpt.getProject_WBS());
				gtf.setProjectName(gtfRpt.getProjectName());
				gtf.setQual_Quant(gtfRpt.getQual_Quant());
				gtf.setRemarks(gtfRpt.getRemarks());
				gtf.setRequestor(gtfRpt.getRequestor());
				gtf.setStatus(gtfRpt.getStatus());
				gtf.setStudy_Side(gtfRpt.getStudy_Side());
				gtf.setSubActivity(gtfRpt.getSubActivity());
				gtf.setUnits(gtfRpt.getUnits());
				gtf.setVariancesMap(gtfRpt.getVariancesMap());
				gtf.setVendor(gtfRpt.getVendor());
				gtf.setWBS_Name(gtfRpt.getWBS_Name());
				gtf.setYear(gtfRpt.getYear());
				gtfRptListFrmCS.add(gtf);
			}
		}
			PersistenceManager pm = PMF.get().getPersistenceManager();
			//Transaction tx = pm.currentTransaction();
			try {
			//	tx.begin();
				if(gtfRptListFrmCS!=null && !gtfRptListFrmCS.isEmpty()){
					for(GtfReport gtfRpt: gtfRptListFrmCS){
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
