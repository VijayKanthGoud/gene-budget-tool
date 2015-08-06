package com.gene.app.exception;

public class DuplicatePOException extends Exception {
	private String message = null;

	public DuplicatePOException() {
		super();
	}

	public DuplicatePOException(String message) {
		super(message);
		this.message = message;
	}

	public DuplicatePOException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}