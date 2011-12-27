package com.willmeyer.card;

import com.willmeyer.card.exception.*;

import java.util.*;

public interface AttributeProvider {
	
	public String getAttributeValue(String name) throws AttributeUnavailableException;
	
	public List<AttributeDef> getSupportedAttributes();
	
}
