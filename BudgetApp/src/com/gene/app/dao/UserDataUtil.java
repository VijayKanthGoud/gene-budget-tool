package com.gene.app.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
			"michasav","narasims","chenm30","basurtok","jamiesos", "suchockw", "grabowsa","shwetims",
			"subramb1","kameckip","lagardal","micoleh","willim58","armett","haoz","jasonso","choul6","savinn","rybusinb",
			"carlyl","manansaj","liug14","abreum4","ditmorek","kewleya","woochans","harivadp","assangm","douglaas","pateln31","santossa","melissdm"};
	
	String [] fullName = {"Anup Mathur","Sreejith NellurKorachanvittil","Vijay Kanth Goud Siddagowni Balram",
			"Yelena Gold","Vasuda Gayathri Kavi","Sreedhar Challapalli","Akansha Makode","Babajyoti Singh","Bhaswanth Reddy Chinthala","test",
			"Sunil Guptha Challagandla",
			"Michael Savitzky","Srihari Narasimhan","Melissa Chen","Kim Basurto","Jamieson Sheffield", "Witold Suchocki", "Adam Grabowski","Shwetima Shwetima",
			"Balaji Subramaniam","Piotr Kamecki","Lluis Lagarda","Micole Doyle","Melissa Williams","Tom Armet","Hao Zhou","Jason Sole","Lily Chou"
			,"Natalie Savin","Bartosz Rybusinski","Loeb	Carly","Manasala Jehrus","Liu Gary","Abreu Melissa","Sinaikin Katherine",
			"Alison Kewley","Chris Sung","Hari Patel","Milton Assang","Doug Saulsbury","Nitin Patel","Santosh Sastry","Melissa Malloy"};
	// create user email array
	/*String [] userEmail = {"mathura2@gene.com","nellurks@gene.com","siddagov@gene.com","goldy@gene.com","kaviv@gene.com","sreedhac@gene.com",
			"makodea@gene.com","singhb15@gene.com","chinthb2@gene.com","test@example.com","challags@gene.com"};*///,"","","",""};
	String [] userEmail = {"mathura2@gene.com","nellurks@gene.com","siddagov@gene.com","goldy@gene.com","kaviv@gene.com","sreedhac@gene.com",
			"makodea@gene.com","singhb15@gene.com","chinthb2@gene.com","test@example.com","challags@gene.com","michasav@gene.com",
			"narasims@gene.com","chenm30@gene.com","basurtok@gene.com","jamiesos@gene.com", "suchockw@gene.com", 
			"grabowsa@gene.com","shwetims@gene.com","subramb1@gene.com","kameckip@gene.com","lagardal@gene.com","micoleh@gene.com",
			"willim58@gene.com","armett@gene.com","haoz@gene.com","jasonso@gene.com","choul6@gene.com","savinn@gene.com","rybusinb@gene.com",
			"carlyl@gene.com","manansaj@gene.com","liug14@gene.com","abreum4@gene.com","ditmorek@gene.com","kewleya@gene.com","woochans@gene.com","harivadp@gene.com",
			"assangm@gene.com","douglaas@gene.com","pateln31@gene.com","santossa@gene.com","melissdm@gene.com"};
	//String [] userEmail = {"test@example.com"};
	// create brandmap array

	// create role array
	String [] role = {"Project Owner","Admin","Admin","Project Owner","Admin","Admin","Project Owner",
			"Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner","Project Owner","Project Owner",
			"Project Owner","Project Owner","Project Owner"};
	//String [] role = {"Project Owner"};
	String [] costCenter = {"7034",
			"7527:7034:7035:7121:7712:7135:7713:7428:7512:7574:7136:7138",
			"7527:7034:7035:7121:7712:7135:7713:7428:7512:7574:7136:7138",
			"7004",
			"7527:7034:7035:7121:7712:7135:7713:7428:7512:7574:7136:7138",
			"7527:7034:7035:7121:7712:7135:7713:7428:7512:7574:7136:7138",
			"7034:7035",
			"7034:7035",
			"7121:7428",
			"7527:7034:7035:7121:7712:7135:7713:7428:7512:7574:7136:7138",
			"7121:7428",
			"7034",
			"7035",
			"7512",
			"7035",
			"7574",
			"7034",
			"7034",
			"7034",
			"7034",
			"7034",
			"7135",
			"7034",
			"7034",
			"7034",
			"7034",
			"7034",
			"7034",
			"7034",
			"7034",
			"7512",
			"7135",
			"7574",
			"7035",
			"7034",
			"7136",
			"7136",
			"7136",
			"7527",
			"7138",
			"7138",
			"7138",
			"7713"};
	
	//String [] costCenter1 = {"307680","235031","307677","235032","307676","307678","7034","7035","7004","7135"};
	String [] costCenter1 = {"7527","7034","7035","7121","7712","7135","7713","7428","7512","7574","7136","7138"};//"7004","7512","7138","7136"};
	String []costCenter2 = {"Indirect Product:total=3033.20;",
			"Actemra:total=1565.0;Rituxan RA:total=948.0;Esbriet:total=1800.0;Lucentis:total=1315.0;Pulmozyme:total=815.0;Xolair:total=1560.0;Lampalizumab:total=680.0;Etrolizumab:total=150.0;General Immun Pipeline:total=500.0;Lebrikizumab:total=1200.0;Indirect Product:total=100.0;",
			"Indirect Product:total=300.0;Avastin:total=4278.0;Tarceva:total=1125.0;Alectinib:total=1034.0;anti-PDL1:total=2250.0;Rituxan Heme/Onc:total=969.0;Gazyva:total=2339.0;GDC-0199:total=689.0;Herceptin:total=850.0;Kadcyla:total=1600.0;Perjeta:total=1340.0;Erivedge:total=1250.0;Zelboraf:total=350.0;Cobimetinib:total=1075.0;BioOnc Pipeline:total=840.0;GDC0941:total=800.0;",
			"Indirect Product:total=100.0;Nutropin:total=300.0;Tamiflu:total=310.0;Lytics:total=945.0;Ocrelizumab:total=2000.0;Gantenerumab:total=450.0;Crenezumab:total=300.0;Alzheimers:total=200.0;LptD:total=250.0;ACE-910:total=1255.0;IMPACT Pipeline General:total=850.0;Neuroscience Pipeline:total=250.0;",
			"Indirect Product:total=869.83;",
			"Indirect Product:total=100.0;Nutropin:total=300.0;Tamiflu:total=310.0;Lytics:total=945.0;Ocrelizumab:total=2000.0;Gantenerumab:total=450.0;Crenezumab:total=300.0;Alzheimers:total=200.0;LptD:total=250.0;ACE-910:total=1255.0;IMPACT Pipeline General:total=850.0;Neuroscience Pipeline:total=250.0;",
			"Indirect Product:total=1507.83;",
			//"Indirect Product:total=1825.6; Avastin:total=400.0; Tarceva:total=140.0; Alectinib:total=65.0; anti-PDL1:total=1040.0; Rituxan Heme/Onc:total=275.0; Gazyva:total=125.0; GDC-0199:total=205.0; Herceptin:total=555.0; Kadcyla:total=302.5; Perjeta:total=307.5; Erivedge:total=100.0; Cobimetinib:total=50.0; BioOnc Pipeline:total=100.0; Actemra:total=102.5; Rituxan RA:total=190.0; Esbriet:total=800.0; Lucentis:total=280.0; Pulmozyme:total=4705; Xolair:total=1550.; Lampalizumab:total=50.0; General Immun Pipeline:total=50.0; Lebrikizumab:total=100.0; Valcyte:total=30.0; Nutropin:total=30.0; Tamiflu:total=20.0; Lytics:total=53.5;",
			"Avastin:total=75.0;Alectinib:total=605.0;GDC-0199:total=100.0;Herceptin:total=20.0;Kadcyla:total=140.0;Perjeta:total=140.0;Erivedge:total=100.0;Cobimetinib:total=170.0;Actemra:total=250.0;Lucentis:total=150.0;Pulmozyme:total=350.0;Lampalizumab:total=185.0;General Immun Pipeline:total=25.0;Tamiflu:total=806.0;Ocrelizumab:total=1215.0;Gantenerumab:total=250.0;Crenezumab:total=150.0;ACE-910:total=650.0;",
			"Avastin:total=75.0;Alectinib:total=605.0;GDC-0199:total=100.0;Herceptin:total=20.0;Kadcyla:total=140.0;Perjeta:total=140.0;Erivedge:total=100.0;Cobimetinib:total=170.0;Actemra:total=250.0;Lucentis:total=150.0;Pulmozyme:total=350.0;Lampalizumab:total=185.0;General Immun Pipeline:total=25.0;Tamiflu:total=806.0;Ocrelizumab:total=1215.0;Gantenerumab:total=250.0;Crenezumab:total=150.0;ACE-910:total=650.0;",
			"Esbriet:total=1800.0;Lucentis:total=1315.0;Pulmozyme:total=815.0;Xolair:total=1560.0;Lampalizumab:total=680.0;Etrolizumab:total=150.0;General Immun Pipeline:total=500.0;Lebrikizumab:total=1200.0;Indirect Product:total=100.0;",
			"Actemra:total=1565.0;Rituxan RA:total=948.0;Pulmozyme:total=815.0;Xolair:total=1560.0;Lampalizumab:total=680.0;Etrolizumab:total=150.0;General Immun Pipeline:total=500.0;Lebrikizumab:total=1200.0;Indirect Product:total=100.0;",
			"Actemra:total=1223.0;Rituxan RA:total=128.0;Esbriet:total=1300.0;Lucentis:total=1325.0;Pulmozyme:total=315.0;Xolair:total=560.0;Lampalizumab:total=580.0;Etrolizumab:total=450.0;General Immun Pipeline:total=500.0;Lebrikizumab:total=1700.0;Indirect Product:total=100.0;"
			//"Avastin:total=276.00; Tarceva:total=85.98; Alectinib:total=170.56; anti-PDL1:total=630.56; Rituxan Heme/Onc:total=158.84; Gazyva:total=444.90; GDC-0199:total=300.00; Herceptin:total=110.11; Kadcyla:total=677.22; Perjeta:total=112.22; Erivedge:total=92.72; Zelboraf:total=41.00; Cobimetinib:total=260.00; BioOnc Pipeline:total=350.00; Actemra:total=574.00; Rituxan RA:total=207.00; Esbriet:total=250.00; Lucentis:total=304.09; Pulmozyme:total=115.00;Xolair:total=707.57; Etrolizumab:total=50.00; General Immun Pipeline:total=50.00; Lebrikizumab:total=108.00; Valcyte:total=120.00; Ocrelizumab:total=1017.64; Gantenerumab:total=201.00; Crenezumab:total=175.00; Alzheimers:total=174.00; LptD:total=350.00; ACE-910:total=490.00;IMPACT Pipeline General:total=300.00; Neuroscience Pipeline:total=150.00;",
			//"Avastin:total=1235.0;Tarceva:total=270.0;Alectinib:total=200.0;anti-PDL1:total=605.0;Rituxan Heme/Onc:total=305.0;Gazyva:total=570.0;GDC-0199:total=200.0;Herceptin:total=100.0;Kadcyla:total=550.0;Perjeta:total=550.0;Erivedge:total=290.0;Zelboraf:total=152.0;Cobimetinib:total=168.0;BioOnc Pipeline:total=150.0;Actemra:total=600.0;Rituxan RA:total=238.0;Esbriet:total=400.0;Lucentis:total=135.0;Pulmozyme:total=135.0;Xolair:total=850.0; General Immun Pipeline:total=100.0;Lebrikizumab:total=50.0;Valcyte:total=235.0;Nutropin:total=100.0; Tamiflu:total=225.0;Lytics:total=180.0;Ocrelizumab:total=800.0;Gantenerumab:total=295.0; Crenezumab:total=65.0; Alzheimers:total=90.0;LptD:total=165.0;ACE-910:total=265.0;IMPACT Pipeline General:total=275.0;Neuroscience Pipeline:total=115.0;Indirect Product:total=587.07;",
			//"Avastin:total=511.0;Tarceva:total=152.0;Alectinib:total=105.0;anti-PDL1:total=650.0;Rituxan Heme/Onc:total=200.0;Gazyva:total=183.0;GDC-0199:total=160.0;Herceptin:total=50.0;Kadcyla:total=110.0;Perjeta:total=115.0;Erivedge:total=50.0;Zelboraf:total=75.0;Cobimetinib:total=100.0;BioOnc Pipeline:total=400.0;Actemra:total=500.0;Rituxan RA:total=150.0;Esbriet:total=400.0;Lucentis:total=275.0;Pulmozyme:total=225.0;Xolair:total=200.0;Lampalizumab:total=275.0;Etrolizumab:total=50.0;General Immun Pipeline:total=20.0;Lebrikizumab:total=325.0;Valcyte:total=200.0;Tamiflu:total=110.0;Lytics:total=85.0;Ocrelizumab:total=440.0;Alzheimers:total=170.0;LptD:total=125.0;ACE-910:total=175.0;IMPACT Pipeline General:total=220.0;Neuroscience Pipeline:total=97.0; Xenical:total=67.5;Invirase:total=67.5;Indirect Product:total=489.22;"
			};
	public void insertUserRoleInfo(){
		String selectedCostCenter = "";
/*		Map<String,Double> brandMap = new LinkedHashMap<String,Double>();
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
		
		Map<String,Double> brand7138Map = new LinkedHashMap<String,Double>();
		
		brand7138Map.put("Avastin", 30000.0);
		brand7138Map.put("Tarceva", 30000.0);
		brand7138Map.put("Alectinib", 30000.0);
		brand7138Map.put("anti-PDL1", 30000.0);
		brand7138Map.put("Rituxan Heme/Onc", 30000.0);
		brand7138Map.put("Gazyva", 30000.0);
		brand7138Map.put("GDC-0199", 30000.0);
		brand7138Map.put("Herceptin", 30000.0);
		brand7138Map.put("Kadcyla", 30000.0);
		brand7138Map.put("Perjeta", 30000.0);
		brand7138Map.put("Erivedge", 30000.0);
		brand7138Map.put("Zelboraf", 30000.0);
		brand7138Map.put("Cobimetinib", 30000.0);
		brand7138Map.put("BioOnc Pipeline", 30000.0);
		brand7138Map.put("Actemra", 30000.0);
		brand7138Map.put("Rituxan RA", 30000.0);
		brand7138Map.put("Esbriet", 30000.0);
		brand7138Map.put("Lucentis", 30000.0);
		brand7138Map.put("Pulmozyme", 30000.0);
		brand7138Map.put("Xolair", 30000.0);
		brand7138Map.put("General Immun Pipeline", 30000.0);
		brand7138Map.put("Lebrikizumab", 30000.0);
		brand7138Map.put("Valcyte", 30000.0);
		brand7138Map.put("Nutropin", 30000.0);
		brand7138Map.put("Tamiflu", 30000.0);
		brand7138Map.put("Lytics", 30000.0);
		brand7138Map.put("Ocrelizumab", 30000.0);
		brand7138Map.put("Gantenerumab", 30000.0);
		brand7138Map.put("Crenezumab", 30000.0);
		brand7138Map.put("Alzheimers", 30000.0);
		brand7138Map.put("LptD", 30000.0);
		brand7138Map.put("ACE-910", 30000.0);
		brand7138Map.put("IMPACT Pipeline General", 30000.0);
		brand7138Map.put("Neuroscience Pipeline", 30000.0);
		brand7138Map.put("Indirect Product", 30000.0);

		Map<String,Double> brand7136Map = new LinkedHashMap<String,Double>();
		
		brand7136Map.put("Avastin", 30000.0);
		brand7136Map.put("Tarceva", 30000.0);
		brand7136Map.put("Alectinib", 30000.0);
		brand7136Map.put("anti-PDL1", 30000.0);
		brand7136Map.put("Rituxan Heme/Onc", 30000.0);
		brand7136Map.put("Gazyva", 30000.0);
		brand7136Map.put("GDC-0199", 30000.0);
		brand7136Map.put("Herceptin", 30000.0);
		brand7136Map.put("Kadcyla", 30000.0);
		brand7136Map.put("Perjeta", 30000.0);
		brand7136Map.put("Erivedge", 30000.0);
		brand7136Map.put("Zelboraf", 30000.0);
		brand7136Map.put("Cobimetinib", 30000.0);
		brand7136Map.put("BioOnc Pipeline", 30000.0);
		brand7136Map.put("Actemra", 30000.0);
		brand7136Map.put("Rituxan RA", 30000.0);
		brand7136Map.put("Esbriet", 30000.0);
		brand7136Map.put("Lucentis", 30000.0);
		brand7136Map.put("Pulmozyme", 30000.0);
		brand7136Map.put("Xolair", 30000.0);
		brand7136Map.put("Lampalizumab", 30000.0);
		brand7136Map.put("Etrolizumab", 30000.0);
		brand7136Map.put("General Immun Pipeline", 30000.0);
		brand7136Map.put("Lebrikizumab", 30000.0);
		brand7136Map.put("Valcyte", 30000.0);
		brand7136Map.put("Tamiflu", 30000.0);
		brand7136Map.put("Lytics", 30000.0);
		brand7136Map.put("Ocrelizumab", 30000.0);
		brand7136Map.put("Alzheimers", 30000.0);
		brand7136Map.put("LptD", 30000.0);
		brand7136Map.put("ACE-910", 30000.0);
		brand7136Map.put("IMPACT Pipeline General", 30000.0);
		brand7136Map.put("Neuroscience Pipeline", 30000.0);
		brand7136Map.put("Xenical", 30000.0);
		brand7136Map.put("Invirase", 30000.0);
		brand7136Map.put("Indirect Product", 30000.0);
		
		Map<String,Double> brand7512Map = new LinkedHashMap<String,Double>();
		
		brand7512Map.put("Avastin", 30000.0);
		brand7512Map.put("Tarceva", 30000.0);
		brand7512Map.put("Alectinib", 30000.0);
		brand7512Map.put("anti-PDL1", 30000.0);
		brand7512Map.put("Rituxan Heme/Onc", 30000.0);
		brand7512Map.put("Gazyva", 30000.0);
		brand7512Map.put("GDC-0199", 30000.0);
		brand7512Map.put("Herceptin", 30000.0);
		brand7512Map.put("Kadcyla", 30000.0);
		brand7512Map.put("Perjeta", 30000.0);
		brand7512Map.put("Erivedge", 30000.0);
		brand7512Map.put("Zelboraf", 30000.0);
		brand7512Map.put("Cobimetinib", 30000.0);
		brand7512Map.put("BioOnc Pipeline", 30000.0);
		brand7512Map.put("Actemra", 30000.0);
		brand7512Map.put("Rituxan RA", 30000.0);
		brand7512Map.put("Esbriet", 30000.0);
		brand7512Map.put("Lucentis", 30000.0);
		brand7512Map.put("Pulmozyme", 30000.0);
		brand7512Map.put("Xolair", 30000.0);
		brand7512Map.put("Etrolizumab", 30000.0);
		brand7512Map.put("General Immun Pipeline", 30000.0);
		brand7512Map.put("Lebrikizumab", 30000.0);
		brand7512Map.put("Valcyte", 30000.0);
		brand7512Map.put("Ocrelizumab", 30000.0);
		brand7512Map.put("Gantenerumab", 30000.0);
		brand7512Map.put("Crenezumab", 30000.0);
		brand7512Map.put("Alzheimers", 30000.0);
		brand7512Map.put("LptD", 30000.0);
		brand7512Map.put("ACE-910", 30000.0);
		brand7512Map.put("IMPACT Pipeline General", 30000.0);
		brand7512Map.put("Neuroscience Pipeline", 30000.0);

		Map<String,Double> brand7428Map = new LinkedHashMap<String,Double>();
		
		brand7428Map.put("Avastin", 30000.0);
		brand7428Map.put("Alectinib", 30000.0);
		brand7428Map.put("anti-PDL1", 30000.0);
		brand7428Map.put("GDC-0199", 30000.0);
		brand7428Map.put("Herceptin", 30000.0);
		brand7428Map.put("Kadcyla", 30000.0);
		brand7428Map.put("Perjeta", 30000.0);
		brand7428Map.put("Erivedge", 30000.0);
		brand7428Map.put("Cobimetinib", 30000.0);
		brand7428Map.put("Actemra", 30000.0);
		brand7428Map.put("Lucentis", 30000.0);
		brand7428Map.put("Pulmozyme", 30000.0);
		brand7428Map.put("Lampalizumab", 30000.0);
		brand7428Map.put("General Immun Pipeline", 30000.0);
		brand7428Map.put("Tamiflu", 30000.0);
		brand7428Map.put("Ocrelizumab", 30000.0);
		brand7428Map.put("Gantenerumab", 30000.0);
		brand7428Map.put("Crenezumab", 30000.0);
		brand7428Map.put("ACE-910", 30000.0);

		Map<String,Double> brand7004Map = new LinkedHashMap<String,Double>();
		
		brand7004Map.put("Avastin", 30000.0);
		brand7004Map.put("Tarceva", 30000.0);
		brand7004Map.put("Alectinib", 30000.0);
		brand7004Map.put("anti-PDL1", 30000.0);
		brand7004Map.put("Rituxan Heme/Onc", 30000.0);
		brand7004Map.put("Gazyva", 30000.0);
		brand7004Map.put("GDC-0199", 30000.0);
		brand7004Map.put("Herceptin", 30000.0);
		brand7004Map.put("Kadcyla", 30000.0);
		brand7004Map.put("Perjeta", 30000.0);
		brand7004Map.put("Erivedge", 30000.0);
		brand7004Map.put("Cobimetinib", 30000.0);
		brand7004Map.put("BioOnc Pipeline", 30000.0);
		brand7004Map.put("Actemra", 30000.0);
		brand7004Map.put("Rituxan RA", 30000.0);
		brand7004Map.put("Esbriet", 30000.0);
		brand7004Map.put("Lucentis", 30000.0);
		brand7004Map.put("Pulmozyme", 30000.0);
		brand7004Map.put("Xolair", 30000.0);
		brand7004Map.put("Lampalizumab", 30000.0);
		brand7004Map.put("General Immun Pipeline", 30000.0);
		brand7004Map.put("Lebrikizumab", 30000.0);
		brand7004Map.put("Valcyte", 30000.0);
		brand7004Map.put("Nutropin", 30000.0);
		brand7004Map.put("Tamiflu", 30000.0);
		brand7004Map.put("Lytics", 30000.0);
		brand7004Map.put("Indirect Product", 30000.0);
		
		Map<String,Double> brand7713Map = new LinkedHashMap<String,Double>();
		
		brand7713Map.put("Indirect Product", 30000.0);
		
		Map<String,Double> brand7135Map = new LinkedHashMap<String,Double>();
		
		brand7135Map.put("Indirect Product", 30000.0);
		
		Map<String,Double> brand7712Map = new LinkedHashMap<String,Double>();
		
		brand7712Map.put("Indirect Product", 30000.0);
		
Map<String,Double> brand7121Map = new LinkedHashMap<String,Double>();
		
		brand7121Map.put("Indirect Product", 30000.0);
		brand7121Map.put("Nutropin", 30000.0);
		brand7121Map.put("Tamiflu", 30000.0);
		brand7121Map.put("Lytics", 30000.0);
		brand7121Map.put("Ocrelizumab", 30000.0);
		brand7121Map.put("Gantenerumab", 30000.0);
		brand7121Map.put("Crenezumab", 30000.0);
		brand7121Map.put("Alzheimers", 30000.0);
		brand7121Map.put("LptD", 30000.0);
		brand7121Map.put("ACE-910", 30000.0);
		brand7121Map.put("IMPACT Pipeline General", 30000.0);
		brand7121Map.put("Neuroscience Pipeline", 30000.0);

		
Map<String,Double> brand7035Map = new LinkedHashMap<String,Double>();
		
		brand7035Map.put("Indirect Product", 30000.0);
		brand7035Map.put("Avastin", 30000.0);
		brand7035Map.put("Tarceva", 30000.0);
		brand7035Map.put("Alectinib", 30000.0);
		brand7035Map.put("anti-PDL1", 30000.0);
		brand7035Map.put("Rituxan Heme/Onc", 30000.0);
		brand7035Map.put("Gazyva", 30000.0);
		brand7035Map.put("GDC-0199", 30000.0);
		brand7035Map.put("Herceptin", 30000.0);
		brand7035Map.put("Kadcyla", 30000.0);
		brand7035Map.put("Perjeta", 30000.0);
		brand7035Map.put("Erivedge", 30000.0);
		brand7035Map.put("Zelboraf", 30000.0);
		brand7035Map.put("Cobimetinib", 30000.0);
		brand7035Map.put("BioOnc Pipeline", 30000.0);
		brand7035Map.put("GDC0941", 30000.0);

		
Map<String,Double> brand7034Map = new LinkedHashMap<String,Double>();
		
		brand7034Map.put("Indirect Product", 30000.0);
		brand7034Map.put("Actemra", 30000.0);
		brand7034Map.put("Rituxan RA", 30000.0);
		brand7034Map.put("Esbriet", 30000.0);
		brand7034Map.put("Lucentis", 30000.0);
		brand7034Map.put("Pulmozyme", 30000.0);
		brand7034Map.put("Xolair", 30000.0);
		brand7034Map.put("Lampalizumab", 30000.0);
		brand7034Map.put("Etrolizumab", 30000.0);
		brand7034Map.put("General Immun Pipeline", 30000.0);
		brand7034Map.put("Lebrikizumab", 30000.0);

Map<String,Double> brand7527Map = new LinkedHashMap<String,Double>();
		
		brand7527Map.put("Indirect Product", 30000.0);
		*/
	PersistenceManager pm = PMF.get().getPersistenceManager();
	List<UserRoleInfo> userInfoList = new ArrayList<UserRoleInfo>();
	UserRoleInfo userRoleInfo = null;
	for(int i=0;i<userName.length;i++){
	userRoleInfo = new UserRoleInfo();
	userRoleInfo.setEmail(userEmail[i]);
	/*if("makodea".equalsIgnoreCase( userName[i])) {
		Map<String,Map<String,Double>> map1 = new LinkedHashMap<String,Map<String,Double>>();
		map1.put("7034", brand7034Map);
		map1.put("7035", brand7035Map);
		//map1.put("7004", brand7004Map);
		//map1.put("7136", brand7136Map);
		userRoleInfo.setCCBrandMap(map1);
	}else if("sreedhac".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map2 = new LinkedHashMap<String,Map<String,Double>>();
		map2.put("7527", brand7527Map);
		map2.put("7034", brand7034Map);

		map2.put("7035", brand7035Map);

		map2.put("7121", brand7121Map);
		map2.put("7712", brand7712Map);
		map2.put("7135", brand7135Map);
		map2.put("7713", brand7713Map);
		//map2.put("7004", brand7004Map);
		map2.put("7428", brand7428Map);
		//map2.put("7512", brand7512Map);
		//map2.put("7138", brand7138Map);
		//map2.put("7136", brand7136Map);
		userRoleInfo.setCCBrandMap(map2);
	}else if("challags".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map3 = new LinkedHashMap<String,Map<String,Double>>();
		map3.put("7121",brand7121Map);
		map3.put("7428",brand7428Map);
		//map3.put("7512",brand7512Map);
		//map3.put("7138",brand7138Map);
		userRoleInfo.setCCBrandMap(map3);
	}else if("shwetims".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map4 = new LinkedHashMap<String,Map<String,Double>>();
		//map4.put("7136", brand7136Map);
		userRoleInfo.setCCBrandMap(map4);
	}else if("subramb1".equalsIgnoreCase( userName[i]) || "kameckip".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map5 = new LinkedHashMap<String,Map<String,Double>>();
		//map5.put("7138", brand7138Map);
		userRoleInfo.setCCBrandMap(map5);
	}else if( "Lluis Lagarda".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map6 = new LinkedHashMap<String,Map<String,Double>>();
		map6.put("7135", brand7135Map);
		userRoleInfo.setCCBrandMap(map6);
	}else if("singhb15".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map7 = new LinkedHashMap<String,Map<String,Double>>();
		map7.put("7034", brand7034Map);
		map7.put("7035", brand7035Map);
		//map7.put("7004", brand7004Map);
		//map7.put("7136", brand7136Map);
		userRoleInfo.setCCBrandMap(map7);
	}else if("siddagov".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map8 = new LinkedHashMap<String,Map<String,Double>>();
		map8.put("7527", brand7527Map);
		map8.put("7034", brand7034Map);

		map8.put("7035", brand7035Map);

		map8.put("7121", brand7121Map);
		map8.put("7712", brand7712Map);
		map8.put("7135", brand7135Map);
		map8.put("7713", brand7713Map);
		//map8.put("7004", brand7004Map);
		map8.put("7428", brand7428Map);
		//map8.put("7512", brand7512Map);
		//map8.put("7138", brand7138Map);
		//map8.put("7136", brand7136Map);
		userRoleInfo.setCCBrandMap(map8);
	}else if("kaviv".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map9 = new LinkedHashMap<String,Map<String,Double>>();
		map9.put("7527", brand7527Map);
		map9.put("7034", brand7034Map);
		map9.put("7035", brand7035Map);
		map9.put("7121", brand7121Map);
		map9.put("7712", brand7712Map);
		map9.put("7135", brand7135Map);
		map9.put("7713", brand7713Map);
		//map9.put("7004", brand7004Map);
		map9.put("7428", brand7428Map);
		//map9.put("7512", brand7512Map);
		//map9.put("7138", brand7138Map);
		//map9.put("7136", brand7136Map);
		userRoleInfo.setCCBrandMap(map9);
	}else if("michasav".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map10 = new LinkedHashMap<String,Map<String,Double>>();
		map10.put("7034", brand7034Map);
		userRoleInfo.setCCBrandMap(map10);
	}else if("narasims".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map11 = new LinkedHashMap<String,Map<String,Double>>();
		map11.put("7035", brand7035Map);
		userRoleInfo.setCCBrandMap(map11);
	}else if("Melissa Chen".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map12 = new LinkedHashMap<String,Map<String,Double>>();
		//map12.put("7004", brand7004Map);
		userRoleInfo.setCCBrandMap(map12);
	}else if("Kim Basurto".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map13 = new LinkedHashMap<String,Map<String,Double>>();
		map13.put("7035", brand7035Map);
		userRoleInfo.setCCBrandMap(map13);
	}else if("Jamieson Sheffield".equalsIgnoreCase( userName[i])){
		Map<String,Map<String,Double>> map14 = new LinkedHashMap<String,Map<String,Double>>();
		//map14.put("7004", brand7004Map);
		userRoleInfo.setCCBrandMap(map14);
	}else{
		Map<String,Map<String,Double>> map15 = new LinkedHashMap<String,Map<String,Double>>();
		map15.put("7034", brand7034Map);
		userRoleInfo.setCCBrandMap(map15);
	}*/
	selectedCostCenter = costCenter[i];
	userRoleInfo.setUserName(userName[i]);
	userRoleInfo.setRole(role[i]);
	userRoleInfo.setCostCenter(costCenter[i]);
	selectedCostCenter = (selectedCostCenter.indexOf(":")==-1)?selectedCostCenter:selectedCostCenter.substring(0,selectedCostCenter.indexOf(":"));
	userRoleInfo.setSelectedCostCenter(selectedCostCenter);
	userRoleInfo.setFullName(fullName[i]);
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
		for(int i=0;i<costCenter1.length;i++){
			cc = new CostCenter_Brand();
			//cc.setBrandFromDB("Perjeta:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=30000.0;Avastin:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=40000.0;Tarceva:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=50000.0;Onart:planned=0.0:accrual=0.0:benchMark=0.0:variance=0.0:total=60000.0;");
			cc.setBrandFromDB(costCenter2[i]);
			cc.setCostCenter(costCenter1[i]);
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
