package com.gene.app.model;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class QuarterCutoffDates {

	@Persistent
	private String qtr;
	@Persistent
	private Date dateTime;
	
	public String getQtr() {
		return qtr;
	}
	public void setQtr(String qtr) {
		this.qtr = qtr;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
}
