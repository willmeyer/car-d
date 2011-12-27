package com.willmeyer.card.exception;

@SuppressWarnings("serial")
public class WriteOnlyAttributeError extends UsageError {

	public WriteOnlyAttributeError(String attrName) {
		super(StatusCodes.ERR_ATTR_WRITEONLY, attrName + " is write-only");
	}
}
