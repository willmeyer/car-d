package com.willmeyer.card.exception;

@SuppressWarnings("serial")
public class CodedException extends Exception {

	protected int statusCode;

	public CodedException(int statusCode, String description) {
		super (description);
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}
