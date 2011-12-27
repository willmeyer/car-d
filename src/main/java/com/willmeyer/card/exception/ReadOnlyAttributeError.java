package com.willmeyer.card.exception;

@SuppressWarnings("serial")
public class ReadOnlyAttributeError extends UsageError {

	public ReadOnlyAttributeError(String attrName) {
		super(StatusCodes.ERR_ATTR_READONLY, attrName + " is read-only");
	}
}
