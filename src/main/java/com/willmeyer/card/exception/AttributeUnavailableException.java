package com.willmeyer.card.exception;

@SuppressWarnings("serial")
public class AttributeUnavailableException extends CodedException {

	protected String attrName;
	
	public AttributeUnavailableException(String attrName) {
		super(StatusCodes.ERR_ATTR_UNAVAIL, "The attribute " + attrName + " could not be retrieved at this time.");
		assert (attrName != null);
		this.attrName = attrName;
	}
	
	public String getAttribute() {
		return this.attrName;
	}
}
