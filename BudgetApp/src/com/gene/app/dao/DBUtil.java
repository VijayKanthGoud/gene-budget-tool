package com.gene.app.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.gene.app.model.BudgetSummary;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.GtfReport;
import com.gene.app.model.QuarterCutoffDates;
import com.gene.app.model.UserRoleInfo;
import com.gene.app.util.BudgetConstants;
import com.gene.app.util.Util;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class DBUtil {
	MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
	URLFetchService urlFetchService = URLFetchServiceFactory
			.getURLFetchService();
	CostCenterCacheUtil costCenterCacheUtil = new CostCenterCacheUtil();
	private final static Logger LOGGER = Logger.getLogger(DBUtil.class
			.getName());

	public UserRoleInfo readUserRoleInfo(String email) {
		Map<String, UserRoleInfo> userMap = new LinkedHashMap<String, UserRoleInfo>();
		userMap = readAllUserInfo();
		UserRoleInfo user = new UserRoleInfo();
		if (userMap != null && !userMap.isEmpty()) {
			user = userMap.get(email);
			if (user != null) {
				return user;
			} else {
				user = readUserInfoFromDB(email);
			}
		}
		return user;
	}

	public UserRoleInfo readUserRoleInfoByName(String userName) {
		Map<String, UserRoleInfo> userMap = new LinkedHashMap<String, UserRoleInfo>();
		userMap = readAllUserInfo();
		UserRoleInfo user = new UserRoleInfo();
		if (userMap != null && !userMap.isEmpty()) {
			user = getUserByName(userMap, userName);
		}
		System.out.println("readUserRoleInfoByName:::" + userName);
		return user;
	}

	public UserRoleInfo readUserRoleInfoByFName(String fullName) {
		Map<String, UserRoleInfo> userMap = new LinkedHashMap<String, UserRoleInfo>();
		userMap = readAllUserInfo();
		UserRoleInfo user = new UserRoleInfo();
		if (userMap != null && !userMap.isEmpty()) {
			user = getUserByFullName(userMap, fullName);
		}
		System.out.println("readUserRoleInfoByName:::" + fullName);
		return user;
	}

	public UserRoleInfo getUserByName(Map<String, UserRoleInfo> userMap,
			String userName) {
		UserRoleInfo user = new UserRoleInfo();
		UserRoleInfo tempUser;
		for (Map.Entry<String, UserRoleInfo> userEntry : userMap.entrySet()) {
			tempUser = userEntry.getValue();
			if (userName != null && !"".equalsIgnoreCase(userName.trim())
					&& userName.equalsIgnoreCase(tempUser.getUserName())) {
				user = tempUser;
				break;
			}
		}
		return user;
	}

	public UserRoleInfo getUserByFullName(Map<String, UserRoleInfo> userMap,
			String fullName) {
		UserRoleInfo user = new UserRoleInfo();
		UserRoleInfo tempUser;
		for (Map.Entry<String, UserRoleInfo> userEntry : userMap.entrySet()) {
			tempUser = userEntry.getValue();
			if (fullName != null && !"".equalsIgnoreCase(fullName.trim())
					&& fullName.equalsIgnoreCase(tempUser.getFullName())) {
				user = tempUser;
				break;
			}
		}
		return user;
	}

	public Map<String, Double> getBrandMap(String brands) {
		Map<String, Double> brandMap = new HashMap<String, Double>();
		String[] brandsArray = brands.split(";");
		Arrays.sort(brandsArray);
		String[] budgetArray = null;
		String value = "";
		String[] emptyArray = null;
		String brandValue = "";
		for (int i = 0; i < brandsArray.length; i++) {
			value = brandsArray[i];
			budgetArray = value.split(":");
			emptyArray = budgetArray[0].split(" ");
			brandValue = "";
			if ("".equalsIgnoreCase(emptyArray[0].trim())) {
				for (int j = 1; j < emptyArray.length; j++) {
					brandValue += emptyArray[j];
				}
			} else {
				brandValue = budgetArray[0];
			}
			brandMap.put(brandValue, 50000.0);
		}
		Map<String, Double> sortedMap = Util.sortCaseInsensitive(brandMap);
		return sortedMap;
	}

	public Map<String, ArrayList<String>> getCCUsersList(String costCenter) {
		Map<String, UserRoleInfo> userMap = new LinkedHashMap<String, UserRoleInfo>();
		Map<String, ArrayList<String>> ccUsers = new LinkedHashMap<String, ArrayList<String>>();

		List<CostCenter_Brand> costCenterList = readCostCenterBrandMappingData();
		CostCenter_Brand ccBrandMap = new CostCenter_Brand();
		userMap = readAllUserInfo();

		for (Map.Entry<String, UserRoleInfo> ccMap : userMap.entrySet()) {
			if (ccMap.getValue().getCostCenter().contains(costCenter)) {
				ArrayList<String> validBrands = new ArrayList<String>();
				for (CostCenter_Brand ccBrand : costCenterList) {
					if (ccBrand.getCostCenter().equalsIgnoreCase(costCenter)) {
						ccBrandMap = ccBrand;
						break;
					}
				}
				Map<String, Double> brandMap = getBrandMap(ccBrandMap
						.getBrandFromDB());
				for (Map.Entry<String, Double> brandName : brandMap.entrySet()) {
					validBrands.add(brandName.getKey());
				}
				ccUsers.put(ccMap.getValue().getUserName(), validBrands);
				ccBrandMap = new CostCenter_Brand();
			}
		}
		return ccUsers;
	}

	public boolean validategMemoriId(String gMemoriId) {
		Map<String, GtfReport> completeGtfRptMap = new LinkedHashMap<String, GtfReport>();
		boolean isGMemIdExists = false;
		if (gMemoriId != null && !"".equals(gMemoriId.trim())) {
			isGMemIdExists = completeGtfRptMap.containsKey(gMemoriId);
		}
		return isGMemIdExists;
	}

	public UserRoleInfo readUserInfoFromDB(String email) {
		UserRoleInfo user = new UserRoleInfo();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(UserRoleInfo.class);
		try {
			q.setFilter("email==emailParam");
			q.declareParameters("String emailParam");
			List<UserRoleInfo> results = (List<UserRoleInfo>) q.execute(email);

			if (!results.isEmpty()) {
				for (UserRoleInfo p : results) {
					user = p;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
		return user;
	}

	public Map<String, UserRoleInfo> readAllUserInfo() {
		String key = UserRoleInfo.class.getName();
		Map<String, UserRoleInfo> userMap = new LinkedHashMap<String, UserRoleInfo>();
		userMap = (Map<String, UserRoleInfo>) cache.get(key);
		if (userMap == null || userMap.isEmpty()) {
			userMap = new LinkedHashMap<String, UserRoleInfo>();
			UserRoleInfo userInfo = new UserRoleInfo();
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query q = pm.newQuery(UserRoleInfo.class);
			try {
				List<UserRoleInfo> results = (List<UserRoleInfo>) q.execute();
				if (results != null && !results.isEmpty()) {
					for (UserRoleInfo role : results) {
						userInfo = role;
						userMap.put(userInfo.getEmail(), userInfo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				q.closeAll();
				pm.close();
			}
			cache.put(key, userMap);
		}
		return userMap;

	}

	public void putUserInfoToCache(Map<String, UserRoleInfo> userMap) {
		String key = UserRoleInfo.class.getName();
		cache.put(key, userMap);
	}

	public String getPrjEmailByName(String prj_owner) {
		String email = "";
		Map<String, UserRoleInfo> userMap = readAllUserInfo();
		if (userMap != null && !userMap.isEmpty()) {
			for (Map.Entry<String, UserRoleInfo> user : userMap.entrySet()) {
				if (prj_owner == null) {
					email = "";
				} else {
					if (!"".equals(prj_owner.trim())
							&& prj_owner.equalsIgnoreCase(user.getValue()
									.getUserName())) {
						email = user.getValue().getEmail();
						break;
					}
				}
			}
		}
		return email;
	}

	public Map<String, BudgetSummary> readAllBudgetSummary(String costCenter) {
		String key = costCenter + BudgetConstants.seperator
				+ BudgetSummary.class.getName();
		Map<String, BudgetSummary> budgetMap = new LinkedHashMap<String, BudgetSummary>();
		budgetMap = (Map<String, BudgetSummary>) cache.get(key);
		if (budgetMap == null || budgetMap.isEmpty()) {
			budgetMap = new LinkedHashMap<String, BudgetSummary>();
			BudgetSummary budgetInfo = new BudgetSummary();
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query q = pm.newQuery(BudgetSummary.class);
			try {
				List<BudgetSummary> results = (List<BudgetSummary>) q.execute();
				if (!results.isEmpty()) {
					for (BudgetSummary budget : results) {
						budgetInfo = budget;
						budgetMap.put(budgetInfo.getProjectOwnerEmail(),
								budgetInfo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				q.closeAll();
				pm.close();
			}
			cache.put(key, budgetMap);
		}
		return budgetMap;

	}

	public BudgetSummary readBudgetSummaryFromDB(String email, String costCenter) {
		boolean isGeneUser = false;
		String key = costCenter + BudgetConstants.seperator
				+ BudgetSummary.class.getName();
		Map<String, BudgetSummary> budgetMap = new LinkedHashMap<String, BudgetSummary>();
		budgetMap = readAllBudgetSummary(costCenter);
		BudgetSummary summary = new BudgetSummary();
		if (budgetMap != null && !budgetMap.isEmpty()) {
			summary = budgetMap.get(email);
		}
		return summary;
	}

	/*
	 * public void saveAllReportDataToCache(String
	 * costCenter,Map<String,GtfReport> gtfReportList){
	 * cache.setErrorHandler(ErrorHandlers
	 * .getConsistentLogAndContinue(Level.INFO)); // cache.put(costCenter,
	 * gtfReportList); CostCenterCacheUtil.putCostCenterDataToCache(costCenter,
	 * gtfReportList); //cache.put(BudgetConstants.GMEMORI_COLLECTION,
	 * gtfReportList); }
	 */

	public GtfReport readReportDataFromCache(String key, String costCenter) {
		GtfReport gtfReportObj = new GtfReport();
		Map<String, GtfReport> gtfReports = new LinkedHashMap<String, GtfReport>();
		gtfReports = getAllReportDataFromCache(costCenter);
		cache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		if (key != null && !"".equals(key.trim())) {

			gtfReportObj = (GtfReport) gtfReports.get(key);
		}
		return gtfReportObj;
	}

	/*
	 * public void saveDataToDataStore(GtfReport gtfReport){ PersistenceManager
	 * pm = PMF.get().getPersistenceManager(); try {
	 * pm.makePersistent(gtfReport); } catch (Exception e) {
	 * e.printStackTrace(); } finally { pm.close(); } }
	 */

	/*
	 * public void saveAllDataToDataStore(List<GtfReport> gtfReportList){
	 * PersistenceManager pm = PMF.get().getPersistenceManager(); try {
	 * pm.makePersistentAll(gtfReportList); } catch (Exception e) {
	 * e.printStackTrace(); } finally { pm.close(); } }
	 */
	public Map<String, GtfReport> getReport(String costCenter,
			boolean resetCache) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setOrdering(BudgetConstants.GTFReportOrderingParameters_getReport);
		q.setFilter("costCenter==costCenterParam");
		q.declareParameters("String costCenterParam");
		Map<String, GtfReport> gtfList = new LinkedHashMap<String, GtfReport>();
		try {
			List<GtfReport> results = (List<GtfReport>) q.execute(costCenter);
			if (!results.isEmpty()) {
				for (GtfReport gtfReport : results) {
					if (!gtfReport.getStatus().equalsIgnoreCase("Disabled")) {
						gtfList.put(gtfReport.getgMemoryId(), gtfReport);
					}
				}
			}
			if (resetCache) {
				costCenterCacheUtil.putCostCenterDataToCache(costCenter,
						gtfList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
		return gtfList;
	}

	public Map<String, Date> getCutOffDates() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(QuarterCutoffDates.class);
		Map<String, Date> cutOffDateList = new LinkedHashMap<String, Date>();
		try {
			List<QuarterCutoffDates> results = (List<QuarterCutoffDates>) q
					.execute();
			if (!results.isEmpty()) {
				for (QuarterCutoffDates p : results) {
					cutOffDateList.put(p.getQtr(), p.getDateTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
		return cutOffDateList;
	}

	public Map<String, GtfReport> getAllReportData() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		// q.setOrdering(BudgetConstants.GTFReportOrderingParameters_getReport);
		// q.setFilter("costCenter==costCenterParam");
		// q.declareParameters("String costCenterParam");
		Map<String, GtfReport> gtfList = new LinkedHashMap<String, GtfReport>();
		try {
			List<GtfReport> results = (List<GtfReport>) q.execute();
			if (!results.isEmpty()) {
				for (GtfReport gtfReport : results) {
					if (!gtfReport.getStatus().equalsIgnoreCase("Disabled")) {
						gtfList.put(gtfReport.getgMemoryId(), gtfReport);
					}
				}
			}
			// if(resetCache){
			// saveAllReportDataToCache(BudgetConstants.GMEMORI_COLLECTION,gtfList);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
		return gtfList;
	}

	public List<GtfReport> calculateVarianceMap(List<GtfReport> gtfReports) {
		List<GtfReport> rptList = new ArrayList<GtfReport>();
		Calendar cal = Calendar.getInstance();
		double benchMark = 0.0;
		double accrual = 0.0;
		double variance = 0.0;
		Map<String, Double> benchMarkMap;
		Map<String, Double> accrualsMap;
		Map<String, Double> varianceMap;
		GtfReport report = new GtfReport();
		for (int i = 0; i < gtfReports.size(); i++) {
			report = gtfReports.get(i);
			benchMarkMap = report.getBenchmarkMap();
			accrualsMap = report.getAccrualsMap();
			varianceMap = report.getVariancesMap();
			for (int j = 0; j < BudgetConstants.months.length - 1; j++) {
				if (benchMarkMap != null) {
					benchMark = benchMarkMap.get(BudgetConstants.months[j]);
				}
				if (accrualsMap != null) {
					accrual = accrualsMap.get(BudgetConstants.months[j]);
				}
				variance = benchMark - accrual;
				if (varianceMap != null) {
					varianceMap.put(BudgetConstants.months[j], variance);
				}
			}
			report.setVariancesMap(varianceMap);
			rptList.add(report);
		}
		return rptList;
	}

	public Map<String, GtfReport> getAllReportDataFromCache(String costCenter) {
		Map<String, GtfReport> gtfReportList = new LinkedHashMap<String, GtfReport>();
		cache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		gtfReportList = CostCenterCacheUtil
				.getCostCenterDataFromCache(costCenter);
		gtfReportList = getReportDataByStatus(gtfReportList);
		if (gtfReportList == null || gtfReportList.size() == 0) {
			gtfReportList = getReport(costCenter, true);
		}
		return gtfReportList;
	}

	public Map<String, GtfReport> getReportDataByStatus(
			Map<String, GtfReport> gtfReportList) {
		Map<String, GtfReport> gtfRptMap = new HashMap<String, GtfReport>();
		if (gtfReportList != null && !gtfReportList.isEmpty()) {
			for (Map.Entry<String, GtfReport> gtfEntry : gtfReportList
					.entrySet()) {
				if (!"Disabled".equals(gtfEntry.getValue().getStatus())) {
					gtfRptMap.put(gtfEntry.getKey(), gtfEntry.getValue());
				}
			}
		}
		return gtfRptMap;
	}

	public Map<String, GtfReport> getAllReportDataCollectionFromCache(
			String gMemoriCollection) {
		Map<String, GtfReport> gtfReportList = new LinkedHashMap<String, GtfReport>();
		cache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		gtfReportList = (Map<String, GtfReport>) cache.get(gMemoriCollection);
		if (gtfReportList == null || gtfReportList.size() == 0) {
			gtfReportList = getAllReportData();
		}
		return gtfReportList;
	}

	public Map<String, GtfReport> getAllReportsByPrjName(String costCenter,
			String prjName, String email, String gMemoriId) {
		Map<String, GtfReport> rptList = getAllReportDataFromCache(costCenter);
		Map<String, GtfReport> newRptList = new LinkedHashMap<String, GtfReport>();
		GtfReport gtfRpt = new GtfReport();
		List<GtfReport> listReports = new ArrayList<GtfReport>();
		if (rptList == null || rptList.isEmpty()) {
			listReports = readProjectDataByProjectName(email, prjName,
					costCenter);
			for (GtfReport nReport : listReports) {
				if (nReport.getgMemoryId().contains(gMemoriId)) {
					newRptList.put(nReport.getgMemoryId(), nReport);
				}
			}
		} else {
			for (Map.Entry<String, GtfReport> rptMap : rptList.entrySet()) {
				gtfRpt = rptMap.getValue();
				if (prjName != null && !"".equals(prjName)
						&& prjName.equalsIgnoreCase(gtfRpt.getProjectName())
						&& rptMap.getKey().contains(gMemoriId)) {
					newRptList.put(rptMap.getKey(), gtfRpt);
				}
			}
		}
		return newRptList;
	}

	// methods for multibrand
	public List<GtfReport> readProjectDataByProjectName(String email,
			String projectName, String costCenter) {
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Query q = pm.newQuery(GtfReport.class);
		if ("".equals(projectName)) {
			q.setFilter("email==emailParam");
			q.declareParameters("String emailParam");
			q.setFilter("costCenter==costCenterParam");
			q.declareParameters("String costCenterParam");
		} else {
			q.setFilter("projectName == projectNameParam && email==emailParam && costCenter==costCenterParam");
			q.declareParameters("String projectNameParam,String emailParam,String costCenterParam");
		}

		try {
			List<GtfReport> results = (List<GtfReport>) q.execute(projectName,
					email);
			if (!results.isEmpty()) {
				for (GtfReport p : results) {
					if (!"Disabled".equals(p.getStatus())) {
						gtfReportList.add(p);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
		return gtfReportList;
	}

	public List<GtfReport> readProjectDataByGMemId(String gMemoriId) {
		GtfReport gtfRpt = new GtfReport();
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		long GMemIdStartTime = System.currentTimeMillis();
		for (String cc : readAllCostCenters()) {

			if (getAllReportDataFromCache(cc).get(gMemoriId) != null) {
				gtfRpt = getAllReportDataFromCache(cc).get(gMemoriId);
				gtfReportList.add(gtfRpt);
				break;
			}
		}
		return gtfReportList;
	}

	public List<GtfReport> readProjectDataById(String gMemoriId,
			UserRoleInfo user) {
		GtfReport gtfRpt = new GtfReport();
		List<GtfReport> results = new ArrayList<GtfReport>();
		Map<String, GtfReport> gtfRptMap = new HashMap<String, GtfReport>();
		for (String cc : readAllCostCenters()) {
			gtfRptMap = getAllReportDataFromCache(cc);
			if (gtfRptMap.get(gMemoriId) != null) {
				for (Map.Entry<String, GtfReport> gtfRptEntry : gtfRptMap
						.entrySet()) {
					if (gtfRptEntry.getKey().startsWith(gMemoriId)) {
						gtfRpt = gtfRptEntry.getValue();
						results.add(gtfRpt);
					}
				}
				break;
			}
		}
		if (Util.isNullOrEmpty(gtfRpt.getCostCenter())
				&& Util.isNullOrEmpty(user.getCostCenter())
				&& !user.getCostCenter().contains(gtfRpt.getCostCenter())) {
			results = new ArrayList<GtfReport>();
			return results;
		}
		if (gtfRpt == null || !Util.isNullOrEmpty(gtfRpt.getCostCenter())) {
			List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
			PersistenceManager pm = PMF.get().getPersistenceManager();

			Query q = pm.newQuery(GtfReport.class);
			if (gMemoriId != null && !"".equals(gMemoriId)) {
				q.setFilter("gMemoryId==gMemoryIdParam");
				q.declareParameters("String gMemoryIdParam");
			}
			try {
				results = (List<GtfReport>) q.execute(gMemoriId);
				results.size();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				q.closeAll();
				pm.close();
			}
		}
		return results;
	}

	public void generateProjectIdUsingJDOTxn(List<GtfReport> gtfReports,
			String gMemoriId, String baseURL, String cCenter) {
		System.out.println("baseURL : " + baseURL);
		try {
			ArrayList<String> scopes = new ArrayList<>();
			scopes.add("https://www.googleapis.com/auth/userinfo.email");
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();
			AppIdentityService.GetAccessTokenResult accessToken = appIdentity
					.getAccessToken(scopes);
			FetchOptions fetchOptions = FetchOptions.Builder
					.doNotFollowRedirects();
			HTTPRequest httpRequest = new HTTPRequest(new URL(baseURL
					+ "ansycStoreGtfReports"), HTTPMethod.POST, fetchOptions);
			httpRequest.setHeader(new HTTPHeader("Authorization", "OAuth "
					+ accessToken.getAccessToken()));
			httpRequest.setHeader(new HTTPHeader("option", "append"));
			httpRequest.setHeader(new HTTPHeader("cCenter", cCenter));
			httpRequest.setHeader(new HTTPHeader("gMemoriId", gMemoriId));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(gtfReports);
			objectOutputStream.close();
			httpRequest.setPayload(byteArrayOutputStream.toByteArray());
			urlFetchService.fetchAsync(httpRequest);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}

	public void storeUserData(List<UserRoleInfo> userInfoList) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistentAll(userInfoList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("closing all datastore call.");
			pm.close();
		}
	}

	public void storeCC_BrandData(CostCenter_Brand cc_brand) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(cc_brand);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("closing all datastore call.");
			pm.close();
		}
	}

	public void removeExistingProject(List<GtfReport> gtfReports,
			String baseURL, String cCenter) {
		try {
			ArrayList<String> scopes = new ArrayList<>();
			scopes.add("https://www.googleapis.com/auth/userinfo.email");
			AppIdentityService appIdentity = AppIdentityServiceFactory
					.getAppIdentityService();
			AppIdentityService.GetAccessTokenResult accessToken = appIdentity
					.getAccessToken(scopes);
			FetchOptions fetchOptions = FetchOptions.Builder
					.doNotFollowRedirects();
			HTTPRequest httpRequest = new HTTPRequest(new URL(baseURL
					+ "ansycStoreGtfReports"), HTTPMethod.POST, fetchOptions);
			httpRequest.setHeader(new HTTPHeader("Authorization", "OAuth "
					+ accessToken.getAccessToken()));
			httpRequest.setHeader(new HTTPHeader("option", "delete"));
			httpRequest.setHeader(new HTTPHeader("cCenter", cCenter));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					byteArrayOutputStream);
			objectOutputStream.writeObject(gtfReports);
			objectOutputStream.close();
			httpRequest.setPayload(byteArrayOutputStream.toByteArray());
			urlFetchService.fetchAsync(httpRequest);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}

	public BudgetSummary readBudgetSummary(String costCenter) {
		BudgetSummary summary = new BudgetSummary();
		List<CostCenter_Brand> costCenterList = readCostCenterBrandMappingData();
		Map<String, Map<String, BudgetSummary>> brandlevelBudgetMap = prepareBrandData(costCenterList);
		Map<String, GtfReport> gtfRptList = getAllReportDataFromCache(costCenter);
		Map<String, BudgetSummary> brandMap = null;
		brandMap = getProjectListByBrand(gtfRptList, brandlevelBudgetMap,
				costCenter);
		if (brandMap == null) {
			brandMap = new LinkedHashMap<String, BudgetSummary>();
		}
		summary.setBudgetMap(brandMap);
		cache.put(BudgetConstants.GMBT_SUMMARY + costCenter, summary);
		return summary;
	}

	public BudgetSummary getSummaryFromCache(String costCenter) {
		BudgetSummary summary = (BudgetSummary) cache
				.get(BudgetConstants.GMBT_SUMMARY + costCenter);
		if (summary == null) {
			summary = readBudgetSummary(costCenter);
		}
		return summary;
	}

	public void putSummaryToCache(BudgetSummary summary, String costCenter) {
		cache.put(BudgetConstants.GMBT_SUMMARY + costCenter, summary);
	}

	// reading costcenter_brand table data and put it in cache
	public void putCCDataToCache(List<CostCenter_Brand> ccList) {
		String key = BudgetConstants.costCenter + BudgetConstants.seperator
				+ CostCenter_Brand.class.getName()
				+ "readCostCenterBrandMappingData";
		cache.put(key, ccList);
	}

	public List<CostCenter_Brand> readCostCenterBrandMappingData() {
		String key = BudgetConstants.costCenter + BudgetConstants.seperator
				+ CostCenter_Brand.class.getName()
				+ "readCostCenterBrandMappingData";
		cache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));
		CostCenter_Brand cost_brand = new CostCenter_Brand();
		List<CostCenter_Brand> costCenterList = new ArrayList<CostCenter_Brand>();

		if (cache.get(key) != null) {
			costCenterList = (List<CostCenter_Brand>) cache.get(key);
		} else {
			PersistenceManager pm = PMF.get().getPersistenceManager();

			Query q = pm.newQuery(CostCenter_Brand.class);
			q.setOrdering("costCenter asc");
			// q.declareParameters("String emailParam");
			try {
				List<CostCenter_Brand> results = (List<CostCenter_Brand>) q
						.execute();
				if (!results.isEmpty()) {
					for (CostCenter_Brand p : results) {
						cost_brand = p;
						costCenterList.add(cost_brand);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				q.closeAll();
				pm.close();
			}
			cache.put(key, costCenterList);
		}
		return costCenterList;
	}

	public List<String> readAllCostCenters() {
		String key = BudgetConstants.costCenter + BudgetConstants.seperator
				+ CostCenter_Brand.class.getName();
		CostCenter_Brand cost_brand = new CostCenter_Brand();
		List<String> costCenterList = new ArrayList<String>();
		List<CostCenter_Brand> results = new ArrayList<>();
			results = readCostCenterBrandMappingData();
		if (!results.isEmpty()) {
			for (CostCenter_Brand p : results) {
				cost_brand = p;
				costCenterList.add(cost_brand.getCostCenter());
			}
		}
		return costCenterList;
	}

	public Map<String, BudgetSummary> prepareBrandMap(String brands) {
		Map<String, BudgetSummary> brandMap = new HashMap<String, BudgetSummary>();
		String[] brandsArray = brands.split(";");
		Arrays.sort(brandsArray);
		String[] budgetArray = null;
		String[] valueArray = null;
		String value = "";
		String total = "";
		BudgetSummary summary = new BudgetSummary();
		for (int i = 0; i < brandsArray.length; i++) {
			summary = new BudgetSummary();
			value = brandsArray[i];
			budgetArray = value.split(":");
			for (int j = 0; j < budgetArray.length; j++) {
				valueArray = budgetArray[j].split("=");
				if (j == 1) {
					total = valueArray[0].replace(",", "");
					summary.setTotalBudget(Double.parseDouble(total));
					summary.setAccrualTotal(0.0);
					summary.setBenchmarkTotal(0.0);
					summary.setVarianceTotal(0.0);
					summary.setPlannedTotal(0.0);
				}
			}
			brandMap.put(budgetArray[0], summary);
		}
		Map<String, BudgetSummary> sortedMap = new TreeMap<String, BudgetSummary>(
				brandMap);
		return sortedMap;
	}

	public Map<String, Map<String, BudgetSummary>> prepareBrandData(
			List<CostCenter_Brand> costCenterList) {
		CostCenter_Brand costCenter_Brand = null;
		Map<String, BudgetSummary> brandMap = null;
		Map<String, Map<String, BudgetSummary>> costCenterBrandMap = new LinkedHashMap<String, Map<String, BudgetSummary>>();
		if (costCenterList != null && !costCenterList.isEmpty()) {
			for (int i = 0; i < costCenterList.size(); i++) {
				costCenter_Brand = (CostCenter_Brand) costCenterList.get(i);
				String brands = costCenter_Brand.getBrandFromDB();
				brandMap = prepareBrandMap(brands);
				costCenterBrandMap.put(costCenter_Brand.getCostCenter(),
						brandMap);
			}
		}
		return costCenterBrandMap;
	}

	public Map<String, BudgetSummary> getProjectListByBrand(
			Map<String, GtfReport> gtfRptList,
			Map<String, Map<String, BudgetSummary>> brandDataMap,
			String costCenter) {
		Map<String, BudgetSummary> brandMap = new LinkedHashMap<String, BudgetSummary>();
		GtfReport gtfReport = new GtfReport();
		String brand = "";
		Double plannedTotal = 0.0;
		Double benchMarkTotal = 0.0;
		Double accrualTotal = 0.0;
		Double varianceTotal = 0.0;
		BudgetSummary summary = new BudgetSummary();
		for (Entry<String, Map<String, BudgetSummary>> brandEntry : brandDataMap
				.entrySet()) {
			if (costCenter.equalsIgnoreCase(brandEntry.getKey())) {
				brandMap = brandEntry.getValue();
				for (Entry<String, BudgetSummary> budgetEntry : brandMap
						.entrySet()) {
					brand = budgetEntry.getKey();
					summary = budgetEntry.getValue();
					plannedTotal = 0.0;
					benchMarkTotal = 0.0;
					accrualTotal = 0.0;
					varianceTotal = 0.0;
					if (gtfRptList != null && !gtfRptList.isEmpty()) {
						for (Entry<String, GtfReport> gtfRptEnty : gtfRptList
								.entrySet()) {
							gtfReport = gtfRptEnty.getValue();
							if (brand.trim().equalsIgnoreCase(
									gtfReport.getBrand().trim())) {
								for (int j = 0; j < BudgetConstants.months.length - 1; j++) {
									if (gtfReport.getPlannedMap() != null) {
										plannedTotal = plannedTotal
												+ gtfReport
														.getPlannedMap()
														.get(BudgetConstants.months[j]);
									}
									if (gtfReport.getBenchmarkMap() != null
											&& !"New"
													.equalsIgnoreCase(gtfReport
															.getStatus())) {
										benchMarkTotal = benchMarkTotal
												+ gtfReport
														.getBenchmarkMap()
														.get(BudgetConstants.months[j]);
									}
									if (gtfReport.getAccrualsMap() != null
											&& !"New"
													.equalsIgnoreCase(gtfReport
															.getStatus())) {
										accrualTotal = accrualTotal
												+ gtfReport
														.getAccrualsMap()
														.get(BudgetConstants.months[j]);
									}
									if (gtfReport.getVariancesMap() != null) {
										varianceTotal = varianceTotal
												+ gtfReport
														.getVariancesMap()
														.get(BudgetConstants.months[j]);
									}
								}
							}
						}
					}
					summary.setAccrualTotal(accrualTotal);
					summary.setVarianceTotal(benchMarkTotal - accrualTotal);
					summary.setPlannedTotal(plannedTotal);
					summary.setBenchmarkTotal(benchMarkTotal);
					summary.setBudgetLeftToSpend(summary.getTotalBudget()
							- summary.getAccrualTotal());
					brandMap.put(brand, summary);
				}
				break;
			}
		}
		return brandMap;
	}

	public void storeProjectsToCache(List<GtfReport> OldGtfReports,
			List<GtfReport> gtfReports, String costCenter) {
		Map<String, GtfReport> gtfReportFromCache = getAllReportDataFromCache(costCenter);
		// Map<String, GtfReport> gtfAllReportFromCache =
		// getAllReportDataCollectionFromCache(BudgetConstants.GMEMORI_COLLECTION);
		Map<String, GtfReport> uniqueGtfReportMap = prepareUniqueGtfRptMap(costCenter);
		GtfReport report = new GtfReport();
		String uniqueGtfRptKey = "";
		if (OldGtfReports != null && !OldGtfReports.isEmpty()) {
			for (int i = 0; i < OldGtfReports.size(); i++) {
				report = OldGtfReports.get(i);
				gtfReportFromCache.remove(report.getgMemoryId());
				// gtfAllReportFromCache.remove(report.getgMemoryId());
				uniqueGtfRptKey = createKeyForXlPrjUpload(report);
				uniqueGtfReportMap.remove(uniqueGtfRptKey);
			}
		}
		if (gtfReports != null && !gtfReports.isEmpty()) {
			for (int i = 0; i < gtfReports.size(); i++) {
				report = gtfReports.get(i);
				gtfReportFromCache.put(report.getgMemoryId(), report);
				// gtfAllReportFromCache.put(report.getgMemoryId(), report);
				uniqueGtfRptKey = createKeyForXlPrjUpload(report);
				uniqueGtfReportMap.put(uniqueGtfRptKey, report);
			}
		}
		// cache.put(costCenter, gtfReportFromCache);
		CostCenterCacheUtil.putCostCenterDataToCache(costCenter,
				gtfReportFromCache);
		// cache.put(BudgetConstants.GMEMORI_COLLECTION, gtfAllReportFromCache);
		// updateUniqueGtfMap(costCenter, uniqueGtfReportMap);

	}

	public void addProjectsToCache(List<GtfReport> gtfReports, String costCenter) {
		// Map<String, GtfReport> gtfReportFromCache =
		// getAllReportDataFromCache(costCenter);
		// Map<String, GtfReport> gtfAllReportFromCache =
		// getAllReportDataCollectionFromCache(BudgetConstants.GMEMORI_COLLECTION);
		Map<String, GtfReport> uniqueGtfReportMap = prepareUniqueGtfRptMap(costCenter);
		GtfReport report = new GtfReport();
		String uniqueGtfRptKey = "";
		for (int i = 0; i < gtfReports.size(); i++) {
			report = gtfReports.get(i);
			// gtfReportFromCache.put(report.getgMemoryId(), report);
			uniqueGtfRptKey = createKeyForXlPrjUpload(report);
			uniqueGtfReportMap.put(uniqueGtfRptKey, report);
		}
		// cache.put(costCenter, gtfReportFromCache);
		CostCenterCacheUtil.addProjectToCostCenterCache(costCenter, gtfReports);
		// cache.put(BudgetConstants.GMEMORI_COLLECTION, gtfReportFromCache);
		// updateUniqueGtfMap(costCenter, uniqueGtfReportMap);

	}

	public String createKeyForXlPrjUpload(GtfReport report) {
		String brand = Util.isNullOrEmpty(report.getBrand()) ? report
				.getBrand() : "";
		String requestor = Util.isNullOrEmpty(report.getRequestor()) ? report
				.getRequestor() : "";
		// String poDesc =
		// Util.isNullOrEmpty(report.getPoDesc())?report.getPoDesc():"";
		String projectName = Util.isNullOrEmpty(report.getProjectName()) ? report
				.getProjectName() : "";
		return brand + ":" + requestor + /* ":"+poDesc+ */":" + projectName;
	}

	@SuppressWarnings("unchecked")
	public void updateReports() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(GtfReport.class);
		q.setFilter("status==statusParam");
		q.declareParameters("String statusParam");
		List<GtfReport> gtfReports = new ArrayList<GtfReport>();
		Map<String, GtfReport> gtfMap = new LinkedHashMap<String, GtfReport>();
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		try {
			gtfReports = (List<GtfReport>) q.execute("Active");
			// Map<String, GtfReport> completeGtfRptList =
			// getAllReportDataCollectionFromCache(BudgetConstants.GMEMORI_COLLECTION);
			if (!gtfReports.isEmpty()) {
				for (GtfReport gtfReport : gtfReports) {
					Map<String, Double> benchMarkMap = new LinkedHashMap<String, Double>();
					Map<String, Double> accrualMap = new LinkedHashMap<String, Double>();
					Map<String, Double> varianceMap = new LinkedHashMap<String, Double>();
					benchMarkMap = gtfReport.getBenchmarkMap();
					varianceMap = gtfReport.getVariancesMap();
					accrualMap = gtfReport.getAccrualsMap();
					for (int iMonths = 0; iMonths < BudgetConstants.months.length - 1; iMonths++) {
						if (iMonths + 1 > month) {
							benchMarkMap.put(
									BudgetConstants.months[iMonths],
									gtfReport.getPlannedMap().get(
											BudgetConstants.months[iMonths]));
							varianceMap
									.put(BudgetConstants.months[iMonths],
											benchMarkMap
													.get(BudgetConstants.months[iMonths])
													- accrualMap
															.get(BudgetConstants.months[iMonths]));
						}

					}
					gtfReport.setBenchmarkMap(benchMarkMap);
					gtfReport.setVariancesMap(varianceMap);
					gtfMap = getAllReportDataFromCache(gtfReport
							.getCostCenter());
					if (gtfMap == null) {
						gtfMap = new LinkedHashMap<String, GtfReport>();
					}/*
					 * if(completeGtfRptList == null){ completeGtfRptList = new
					 * LinkedHashMap<String, GtfReport>(); }
					 */
					gtfMap.put(gtfReport.getgMemoryId(), gtfReport);
					// completeGtfRptList.put(gtfReport.getgMemoryId(),
					// gtfReport);
					// saveAllReportDataToCache(gtfReport.getCostCenter(),
					// gtfMap);
					// saveAllReportDataToCache(BudgetConstants.GMEMORI_COLLECTION,
					// completeGtfRptList);
				}
			}
			pm.makePersistentAll(gtfReports);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			q.closeAll();
			pm.close();
		}
	}

	public Map<String, GtfReport> prepareUniqueGtfRptMap(String costCentre) {
		Map<String, GtfReport> gtfRptMap = getAllReportDataFromCache(costCentre);
		String uniqueGtfRptKey = "";
		Map<String, GtfReport> gtfReportMap = new HashMap<>();
		GtfReport gtfReport = null;
		if (gtfRptMap != null && !gtfRptMap.isEmpty()) {
			for (Map.Entry<String, GtfReport> gtfMapEntry : gtfRptMap
					.entrySet()) {
				gtfReport = gtfMapEntry.getValue();
				if (Util.isNullOrEmpty(gtfReport.getBrand())) {
					uniqueGtfRptKey = gtfReport.getBrand() + ":";
				}
				if (Util.isNullOrEmpty(gtfReport.getRequestor())) {
					uniqueGtfRptKey = uniqueGtfRptKey
							+ gtfReport.getRequestor() + ":";
				}/*
				 * if(Util.isNullOrEmpty(gtfReport.getPoDesc())){
				 * uniqueGtfRptKey = uniqueGtfRptKey + gtfReport.getPoDesc() +
				 * ":"; }
				 */
				if (Util.isNullOrEmpty(gtfReport.getProjectName())) {
					uniqueGtfRptKey = uniqueGtfRptKey
							+ gtfReport.getProjectName();
				}
				gtfReportMap.put(uniqueGtfRptKey, gtfReport);
			}
		}
		return gtfReportMap;
	}

	/*
	 * public void updateUniqueGtfMap(String costCenter, Map<String,GtfReport>
	 * uniqueGtfReportMap){ cache.put("UniqueProjectUpload"+costCenter,
	 * uniqueGtfReportMap); }
	 */
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

	public Map<String, GtfReport> preparePOMap(
			Map<String, GtfReport> costCenterWiseGtfRptMap) {
		Map<String, GtfReport> poMap = new HashMap<String, GtfReport>();
		for (Map.Entry<String, GtfReport> gtfEntry : costCenterWiseGtfRptMap
				.entrySet()) {
			if (!gtfEntry.getValue().getStatus().equalsIgnoreCase("new")) {
				poMap.put(gtfEntry.getValue().getPoNumber(),
						gtfEntry.getValue());
			}
		}
		return poMap;
	}

	public Map<String, GtfReport> prepareProjectNameMap(
			Map<String, GtfReport> costCenterWiseGtfRptMap) {
		Map<String, GtfReport> poMap = new HashMap<String, GtfReport>();
		for (Map.Entry<String, GtfReport> gtfEntry : costCenterWiseGtfRptMap
				.entrySet()) {
			poMap.put(gtfEntry.getValue().getProjectName(), gtfEntry.getValue());
		}
		return poMap;
	}

	public boolean validatePONum(String PONumber) {
		System.out.println("PO Number::::::::");
		List<CostCenter_Brand> ccList = readCostCenterBrandMappingData();
		Map<String, GtfReport> costCenterWiseGtfRptMap = null;
		Map<String, GtfReport> poMap = null;
		boolean poExists = false;
		if (ccList != null && !ccList.isEmpty()) {
			for (CostCenter_Brand cc : ccList) {
				costCenterWiseGtfRptMap = getAllReportDataFromCache(cc
						.getCostCenter());
				poMap = preparePOMap(costCenterWiseGtfRptMap);
				if (poMap != null && !poMap.isEmpty()) {
					if (poMap.get(PONumber) != null) {
						poExists = true;
						break;
					}
				}
			}
		}
		// System.out.println("ccList.contains(7121)"+ccList.contains("7121"));
		return poExists;
	}

	/**
	 * Disable project.
	 * 
	 * @param gMemoriId  the gMemoriId of the project you want to delete.
	 * @param costCenter the cost center where the project exists
	 */
	public void disableProject(String gMemoriId, String costCenter,
			String baseURL) {
		Map<String, GtfReport> gtfReportMap = getAllReportDataFromCache(costCenter);
		List<GtfReport> gtfReportList = new ArrayList<GtfReport>();
		for (Map.Entry<String, GtfReport> e : gtfReportMap.entrySet()) {
			if (e.getKey().contains(gMemoriId)) {
				e.getValue().setStatus("Disabled");
				gtfReportList.add(e.getValue());
			}
		}
		generateProjectIdUsingJDOTxn(gtfReportList, "", baseURL, costCenter);
	}

}
