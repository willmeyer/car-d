package com.willmeyer.card;

/**
 * A simple definition of an attribute.  Attributes are always treated as all-lowercase names.
 */
public final class AttributeDef {

	private String shortName = null;
	private String friendlyName = null;
	private int cacheTime = -1;
	
	public String getShortName() {
		return this.shortName;
	}
	
	public String getFriendlyName() {
		return this.friendlyName;
	}

	public boolean isReadable() {
		return true; 
	}
	
	public int cacheableFor() {
		return cacheTime; 
	}
	
	public AttributeDef(String shortName, String friendlyName, int cacheableFor) {
		this.shortName = shortName.toLowerCase();
		this.friendlyName = friendlyName.toLowerCase();
		this.cacheTime = cacheableFor;
	}
	
	public AttributeDef(String shortName, String friendlyName) {
		this.shortName = shortName.toLowerCase();
		this.friendlyName = friendlyName.toLowerCase();
	}
}
