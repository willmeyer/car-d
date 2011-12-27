package com.willmeyer.card.exception;

@SuppressWarnings("serial")
public class UsageError extends CodedException {

	public UsageError(int statusCode, String description) {
		super (statusCode, description);
	}

}
