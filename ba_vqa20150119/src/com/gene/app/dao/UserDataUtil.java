package com.gene.app.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gene.app.model.BudgetSummary;
import com.gene.app.model.CostCenter_Brand;
import com.gene.app.model.UserRoleInfo;
import com.google.apphosting.api.DatastorePb.Cost;

public class UserDataUtil {
	
	// create username array
	//String [] userName = {"mathura2","nellurks","siddagov","goldy","kaviv","sreedhac","makodea","singhb15","chinthb2"};//,"","","",""};
	//String [] userName = {"mathura2","nellurks","siddagov","goldy","kaviv","sreedhac","makodea","singhb15","chinthb2","test","challags"};//,"","","",""};
	String [] userName = {"mathura2","nellurks","siddagov","goldy","kaviv","sreedhac","makodea","singhb15","chinthb2","test","challags",
			"Michael Savitzky","Srihari Narasimhan","Melissa Chen","Kim Basurto","Jamieson Sheffield", "suchockw", "grabowsa","shwetims","subramb1","kameckip"};
	// create user email array
	/*String [] userEmail = {"mathura2@gene.com","nellurks@gene.com","siddagov@gene.com","goldy@gene.com","kaviv@gene.com","sreedhac@gene.com",
			"makodea@gene.com","singhb15@gene.com","chinthb2@gene.com","test@example.com","challags@gene.com"};*///,"","","",""};
	String [] userEmail = {"mathura2@gene.com","nellurks@gene.com","siddagov@gene.com","goldy@gene.com","kaviv@gene.com","sreedhac@gene.com",
			"makodea@gene.com","singhb15@gene.com","chinthb2@gene.com","test@example.com","challags@gene.com","savitzky.michael@gene.com",
			"narasimhan.srihari@gene.com","chen.melissa@gene.com","basurto.kimberly@gene.com","sheffield.jamieson@gene.com", "suchockw@gene.com", "grabowsa@gene.com",
			"shwetims@gene.com","subramb1@gene.com","kameckip@gene.com"};
	//String [] userEmail = {"test@example.com"};
	// create brandmap array

	// create role array
	String [] role = {"Project Owner","Project Owner","Brand Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner"};
	//String [] role = {"Project Owner"};
	String [] costCenter = {"307680","235031","307677","235032","307677","307676","307676","307677","307678","307680","307676","7034","7035","7004","7035","7004","7034","7034"
			,"307676","307676","307676"};
	public void insertUserRoleInfo(){
		Map<String,Double> brandMap = new LinkedHashMap<String,Double>();
		brandMap.put("Onart", 30000.0);
		brandMap.put("Perjeta", 20000.0);
		brandMap.put("Tarceva", 50000.0);
		brandMap.put("Rituxan Heme/Onc", 60000.0);
		brandMap.put("Kadcyla", 40000.0);
		brandMap.put("Actemra", 50000.0);
		brandMap.put("Rituxan RA", 60000.0);
		brandMap.put("Lucentis", 30000.0);
		brandMap.put("Bitopertin", 40000.0);
		brandMap.put("Ocrelizumab", 50000.0);
		brandMap.put("Avastin", 40000.0);
		brandMap.put("BioOnc Pipeline", 60000.0);
		brandMap.put("Lebrikizumab", 30000.0);
		brandMap.put("Pulmozyme", 40000.0);
		brandMap.put("Xolair", 50000.0);
		brandMap.put("Oral Octreotide", 60000.0);
		brandMap.put("Etrolizumab", 30000.0);
		brandMap.put("GDC-0199", 40000.0);
		brandMap.put("Neuroscience Pipeline", 50000.0);
		
		Map<String,Double> brand1Map = new LinkedHashMap<String,Double>();
		brand1Map.put("Onart",  60000.0);
		brand1Map.put("Xolair", 30000.0);
		brand1Map.put("Perjeta", 20000.0);
		brand1Map.put("Oral Octreotide", 60000.0);
		brand1Map.put("Etrolizumab", 30000.0);
		brand1Map.put("GDC-0199", 40000.0);
		brand1Map.put("Neuroscience Pipeline", 50000.0);
		brand1Map.put("BioOnc Pipeline", 60000.0);
		brand1Map.put("Ocrelizumab", 50000.0);
		
		Map<String,Double> brand2Map = new LinkedHashMap<String,Double>();
		brand2Map.put("Avastin",  60000.0);
		brand2Map.put("Tarceva", 30000.0);
		brand2Map.put("Pulmozyme", 40000.0);
		brand2Map.put("Lucentis", 30000.0);
		brand2Map.put("Bitopertin", 40000.0);
		brand2Map.put("Kadcyla", 40000.0);
		brand2Map.put("Actemra", 50000.0);
		brand2Map.put("Lebrikizumab", 30000.0);
		brand2Map.put("Rituxan Heme/Onc", 60000.0);
		brand2Map.put("Rituxan RA", 60000.0);
		brand2Map.put("Etrolizumab", 30000.0);
		
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

		
		Map<String,Double> brand6Map = new LinkedHashMap<String,Double>();
		brand6Map.put("Avastin", 30000.0);
		brand6Map.put("Tarceva", 20000.0);
		brand6Map.put("Pulmozyme", 50000.0);
		brand6Map.put("Lucentis", 60000.0);
		brand6Map.put("Bitopertin", 40000.0);
		brand6Map.put("Kadcyla", 50000.0);
		brand6Map.put("Actemra", 60000.0);
		brand6Map.put("Lebrikizumab", 30000.0);
		brand6Map.put("Rituxan Heme/Onc", 40000.0);
		brand6Map.put("Rituxan RA", 50000.0);
		brand6Map.put("Etrolizumab", 40000.0);
		
		Map<String,Double> brand7Map = new LinkedHashMap<String,Double>();
		brand7Map.put("Avastin", 30000.0);
		brand7Map.put("Tarceva", 20000.0);
		brand7Map.put("Pulmozyme", 50000.0);
		brand7Map.put("Lucentis", 60000.0);
		brand7Map.put("Bitopertin", 40000.0);
		brand7Map.put("Kadcyla", 50000.0);
		brand7Map.put("Actemra", 60000.0);
		brand7Map.put("Lebrikizumab", 30000.0);
		
		Map<String,Double> brand8Map = new LinkedHashMap<String,Double>();
		brand8Map.put("Avastin", 30000.0);
		brand8Map.put("Tarceva", 20000.0);
		brand8Map.put("Pulmozyme", 50000.0);
		brand8Map.put("Lucentis", 60000.0);
		brand8Map.put("Lebrikizumab", 30000.0);
		brand8Map.put("Rituxan Heme/Onc", 40000.0);
		brand8Map.put("Rituxan RA", 50000.0);
		brand8Map.put("Etrolizumab", 40000.0);
		
		Map<String,Double> brand9Map = new LinkedHashMap<String,Double>();
		brand9Map.put("Avastin", 30000.0);
		brand9Map.put("Tarceva", 20000.0);
		brand9Map.put("Pulmozyme", 50000.0);
		brand9Map.put("Lucentis", 60000.0);
		brand9Map.put("Bitopertin", 40000.0);
		brand9Map.put("Kadcyla", 50000.0);
		brand9Map.put("Actemra", 60000.0);
		
	PersistenceManager pm = PMF.get().getPersistenceManager();
	List<UserRoleInfo> userInfoList = new ArrayList<UserRoleInfo>();
	UserRoleInfo userRoleInfo = null;
	for(int i=0;i<userName.length;i++){
	userRoleInfo = new UserRoleInfo();
	userRoleInfo.setEmail(userEmail[i]);
	if("makodea".equalsIgnoreCase( userName[i]) || "sreedhac".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand6Map);
	}else if("challags".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand7Map);
	}else if("shwetims".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand8Map);
	}else if("subramb1".equalsIgnoreCase( userName[i]) || "kameckip".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand9Map);
	}else if("singhb15".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand3Map);
	}else if("siddagov".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brandMap);
	}else if("kaviv".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brandMap);
	}else if("Michael Savitzky".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brandMap);
	}else if("Srihari Narasimhan".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand1Map);
	}else if("Melissa Chen".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand1Map);
	}else if("Kim Basurto".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand2Map);
	}else if("Jamieson Sheffield".equalsIgnoreCase( userName[i])){
		userRoleInfo.setBrand(brand2Map);
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
			//cc.setBrandFromDB("Perjeta:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=30000.0;Avastin:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=40000.0;Tarceva:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=50000.0;Onart:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=60000.0;");
			cc.setBrandFromDB("Perjeta:total=30000.0;Avastin:total=40000.0;Tarceva:total=50000.0;Onart:total=60000.0;Rituxan Heme/Onc:total=30000.0;Kadcyla:total=40000.0;Actemra:total=50000.0;Rituxan RA:total=60000.0;Lucentis:total=30000.0;Bitopertin:total=40000.0;Ocrelizumab:total=50000.0;BioOnc Pipeline:total=60000.0;Lebrikizumab:total=30000.0;Pulmozyme:total=40000.0;Xolair:total=50000.0;Oral Octreotide:total=60000.0;Etrolizumab:total=30000.0;GDC-0199:total=40000.0;Neuroscience Pipeline:total=50000.0;");
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
