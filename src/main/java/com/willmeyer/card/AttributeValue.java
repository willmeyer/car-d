package com.willmeyer.card;

public class AttributeValue {

	protected String value = null;
	protected long fetchedAt = 0;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public long getFetchedTime() {
		return fetchedAt;
	}
	
	public AttributeValue(String value, long fetchedAt) {
		this.value = value;
		this.fetchedAt = fetchedAt;
	}
}
