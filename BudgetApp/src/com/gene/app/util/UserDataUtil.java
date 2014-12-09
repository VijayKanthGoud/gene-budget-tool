package com.gene.app.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gene.app.bean.BudgetSummary;
import com.gene.app.bean.CostCenter_Brand;
import com.gene.app.bean.UserRoleInfo;
import com.gene.app.server.PMF;
import com.google.apphosting.api.DatastorePb.Cost;

public class UserDataUtil {
	
	// create username array
	//String [] userName = {"mathura2","nellurks","siddagov","goldy","kaviv","sreedhac","makodea","singhb15","chinthb2"};//,"","","",""};
	String [] userName = {"mathura2","nellurks","siddagov","goldy","kaviv","sreedhac","makodea","singhb15","chinthb2","test","challags"};//,"","","",""};
	// create user email array
	String [] userEmail = {"mathura2@gene.com","nellurks@gene.com","siddagov@gene.com","goldy@gene.com","kaviv@gene.com","sreedhac@gene.com",
			"makodea@gene.com","singhb15@gene.com","chinthb2@gene.com","test@example.com","challags@gene.com"};//,"","","",""};
	//String [] userEmail = {"test@example.com"};
	// create brandmap array

	// create role array
	String [] role = {"Project Owner","Project Owner","Brand Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner"};
	//String [] role = {"Project Owner"};
	String [] costCenter = {"307680","235031","307677","235032","307677","307680","307676","307677","307678","307680","307676"};
	public void insertUserRoleInfo(){
		Map<String,Double> brandMap = new LinkedHashMap<String,Double>();
		brandMap.put("Onart", 30000.0);
		brandMap.put("Perjeta", 20000.0);
		brandMap.put("Tarceva", 50000.0);
		
		Map<String,Double> brand1Map = new LinkedHashMap<String,Double>();
		brand1Map.put("Onart",  60000.0);
		brand1Map.put("Xolair", 30000.0);
		brand1Map.put("Perjeta", 20000.0);
		
		
		Map<String,Double> brand2Map = new LinkedHashMap<String,Double>();
		brand2Map.put("Avastin",  60000.0);
		brand2Map.put("Tarceva", 30000.0);
		
		Map<String,Double> brand3Map = new LinkedHashMap<String,Double>();
		brand3Map.put("Avastin",  60000.0);
		brand3Map.put("Tarceva", 30000.0);
		brand3Map.put("Xolair", 30000.0);
		brand3Map.put("Pulmozyme", 30000.0);
		
		Map<String,Double> brand4Map = new LinkedHashMap<String,Double>();
		brand4Map.put("Avastin",  60000.0);
		brand4Map.put("Tarceva", 30000.0);
		brand4Map.put("Etrolizumab",  60000.0);
		brand4Map.put("Bitopertin", 30000.0);
		
		Map<String,Double> brand5Map = new LinkedHashMap<String,Double>();
		brand5Map.put("Avastin",  60000.0);
		brand5Map.put("Tarceva", 30000.0);
		brand5Map.put("Lucentis",  60000.0);
		brand5Map.put("Xolair", 30000.0);
		
	PersistenceManager pm = PMF.get().getPersistenceManager();
	List<UserRoleInfo> userInfoList = new ArrayList<UserRoleInfo>();
	UserRoleInfo userRoleInfo = null;
	for(int i=0;i<userName.length;i++){
	userRoleInfo = new UserRoleInfo();
	userRoleInfo.setEmail(userEmail[i]);
	if("makodea".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand1Map);
	}else if("sreedhac".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brandMap);
	}else if("challags".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand2Map);
	}else if("singhb15".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand3Map);
	}else if("siddagov".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand4Map);
	}else if("kaviv".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand5Map);
	}else{
	userRoleInfo.setBrand(brandMap);
	}
	userRoleInfo.setUserName(userName[i]);
	userRoleInfo.setRole(role[i]);
	userRoleInfo.setCostCenter(costCenter[i]);
	userInfoList.add(userRoleInfo);
	}
	try{
		pm.makePersistentAll(userInfoList);
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		pm.close();
	}
}
	
	public void insertBudgetSummary(){
		Double[] budgetArray = {7000.0,10000.0,30000.0,60000.0,6000.0,2000.0,7000.0,30000.0,6000.0};
		//Double[] budgetArray = {7000.0};
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<BudgetSummary> budgetSummaryList = new ArrayList<BudgetSummary>();
		BudgetSummary budgetSummary = null;
		for(int i=0;i<budgetArray.length;i++){
		budgetSummary = new BudgetSummary();
		budgetSummary.setProjectOwnerEmail(userEmail[i]);
		budgetSummary.setTotalBudget(budgetArray[i]);
		budgetSummaryList.add(budgetSummary);
		}
		try{
			pm.makePersistentAll(budgetSummaryList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			pm.close();
		}
	}
	
	
	
	public void insertCCMapping(){
		Double[] budgetArray = {7000.0,10000.0,30000.0,60000.0,6000.0,2000.0,7000.0,30000.0,6000.0};
		//Double[] budgetArray = {7000.0};
		PersistenceManager pm = PMF.get().getPersistenceManager();
		CostCenter_Brand cc = new CostCenter_Brand();
		//List<BudgetSummary> budgetSummaryList = new ArrayList<BudgetSummary>();
		/*BudgetSummary budgetSummary = null;
		for(int i=0;i<budgetArray.length;i++){
		budgetSummary = new BudgetSummary();
		budgetSummary.setProjectOwnerEmail(userEmail[i]);
		budgetSummary.setTotalBudget(budgetArray[i]);
		budgetSummaryList.add(budgetSummary);
		}*/
		List<CostCenter_Brand> ccList = new ArrayList<CostCenter_Brand>();
		for(int i=0;i<userName.length;i++){
			cc = new CostCenter_Brand();
			cc.setBrandFromDB("Perjeta:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=30000.0;Avastin:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=40000.0;Tarceva:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=50000.0;Onart:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=60000.0;");
			cc.setCostCenter(costCenter[i]);
			ccList.add(cc);
		}
		//cc.setBrandFromDB("Perjeta:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=30000.0;Avastin:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=40000.0;Tarceva:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=50000.0;Onart:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=60000.0;");
		//cc.setCostCenter("307673");
		try{
			pm.makePersistentAll(ccList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			pm.close();
		}
	}
}
