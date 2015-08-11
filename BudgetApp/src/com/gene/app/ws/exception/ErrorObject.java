package com.gene.app.ws.exception;

public class ErrorObject {

	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getNewGMemId() {
		return newGMemId;
	}
	public void setNewGMemId(String newGMemId) {
		this.newGMemId = newGMemId;
	}
	private String statusMessage;
	private int statusCode;
	private String newGMemId;
}
