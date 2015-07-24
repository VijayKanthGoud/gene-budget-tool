package com.gene.app.util;

import java.util.Comparator;
import java.util.Map;

import com.gene.app.model.GtfReport;

public class ReportComparator implements Comparator<String> {

	Map<String, GtfReport> gtfReportFromCache;
    public ReportComparator(Map<String, GtfReport> gtfReportFromCache) {
        this.gtfReportFromCache = gtfReportFromCache;
    }

	@Override
	public int compare(String o1, String o2) {
		if((gtfReportFromCache.get(o1).getProjectName()).compareTo((gtfReportFromCache.get(o2).getProjectName())) ==0){
			return (gtfReportFromCache.get(o1).getgMemoryId()).compareTo((gtfReportFromCache.get(o2).getgMemoryId()));
		}
		return (gtfReportFromCache.get(o1).getProjectName()).compareTo((gtfReportFromCache.get(o2).getProjectName()));
	}
}