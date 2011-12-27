package com.willmeyer.card.exception;

@SuppressWarnings("serial")
public class UnknownAttributeError extends UsageError {

	public UnknownAttributeError(String attrName) {
		super(StatusCodes.ERR_ATTR_UNKNOWN, attrName + " is not a known attribute");
	}
}
