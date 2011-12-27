package com.willmeyer.card;

import java.util.*;

import org.slf4j.*;

import com.willmeyer.card.exception.*;

import com.willmeyer.util.*;

/**
 * Tracks and manages all of the attributes available within the system, by different installed AttributeProviders.
 * Attributes can be read and set, as well as enumerated, via this interface.
 * 
 * The AttributeManager loads AttributeProvider implementations as specified in the application properties file, 
 * according to the following scheme:
 * 
 * attrprovider.#.enabled=true|false
 * attrprovider.#.class=com.company.card.MyAttributeProvider
 *
 * The AttributeManager maintains a cache of attributes, according to their caching rules.
 */
public final class AttributeManager {

	protected HashMap<String, AttributeMap> attrMaps; // attribute names to the AttributeMap for those attributes
	protected HashMap<String, AttributeValue> attrCache; // cached values
	protected PropertiesPlusPlus initProps;
	
	protected final Logger logger = LoggerFactory.getLogger(AttributeManager.class);
	
	/**
	 * Install an AttributeProvider by instantiating the specified class.  If it fails, its just a NOOP.
	 */
	protected void installProvider(String providerClassName) {
		AttributeProvider provider = null;
		try {
			provider = (AttributeProvider)Class.forName(providerClassName).newInstance();
			logger.debug("Loaded AttributeProvider '{}', getting attributes", providerClassName);
			List<AttributeDef> defs = provider.getSupportedAttributes();
			for (AttributeDef def : defs) {
				AttributeMap map = new AttributeMap(def, provider);
				logger.debug("Registering attribute '{}'", def.getShortName());
				attrMaps.put(def.getShortName(), map);
			}
		} catch (Exception e) {
			logger.warn("Unable to install attribute provider '{}', skipping it.  Error was '{}'", providerClassName, e.getMessage());
		}
	}
	
	public void loadAttributes() {
		logger.info("Loading attribute providers...");
		int num = 1;
		boolean found = true;
		while (found) {
			found = false;
			logger.debug("Checking provider {}", num);
			String propPrefix = "attrprovider." + num;
			String enabledProp = propPrefix + ".enabled";
			if (initProps.getProperty(enabledProp) != null) {
				found = true;
				boolean enabled = initProps.getBooleanProperty(enabledProp, false);
				if (enabled) {
					String classProp = propPrefix + ".class";
					String className = initProps.getProperty(classProp);
					try {
						this.installProvider(className);
						logger.info("Attribute provider {} successfully installed ({})", num, className);
					} catch (Exception e) {
						logger.warn("Unable to load AttributeProvider '{}', skipping it.  Error was '{}'", className, e.getMessage());
					}
				}
			}
			num++;
		}
	}
	
	public void clearAttributes() {
		this.attrCache.clear();
		this.attrMaps.clear();
	}

	AttributeManager(PropertiesPlusPlus props) {
		attrMaps = new HashMap<String, AttributeMap>();
		attrCache = new HashMap<String, AttributeValue>();
		initProps = props;
	}
	
	/**
	 * The attribute definition, mapped to the provider that provides it.
	 */
	protected class AttributeMap {
		
		protected AttributeDef def;
		protected AttributeProvider provider;
		
		public AttributeMap(AttributeDef def, AttributeProvider provider) {
			this.def = def;
			this.provider = provider;
		}
	}
	
	/**
	 * Sets the value of an attribute.  The attribute must be writeable, or an exception gets thrown.
	 */
	public void setAttributeValue(String attrName, String attrVal) throws UsageError {
		throw new RuntimeException("not implemented");
	}

	/**
	 * Returns the set of all available attributes -- full definitions.
	 */
	public Collection<AttributeDef> getAvailableAttributes() {
		LinkedList<AttributeDef> defs = new LinkedList<AttributeDef>();
		for (AttributeMap map : attrMaps.values()) {
			defs.add(map.def);
		}
		return defs;
	}
	
	/**
	 * Returns the set of all available attributes -- names only.
	 */
	public Set<String> getAvailableAttributeNames() {
		return attrMaps.keySet();
	}

	/**
	 * Gets the value of an attribute, without throwing any exceptions ("N/A" is returned instead).
	 */
	public String getAttributeValueSafe(String attrName) {
		String attrVal; 
		try {
			attrVal = this.getAttributeValue(attrName);
		} catch (Exception e) {
			attrVal = "N/A";
		}
		return attrVal;
	}
	
	/**
	 * Gets the value of an attribute.  The attribute must be readable, or an exception gets thrown.
	 * 
	 * @return The value of the attribute (won't be null, any errors cause exceptions)
	 */
	public String getAttributeValue(String attrName) throws UsageError, AttributeUnavailableException {
		attrName = attrName.toLowerCase();
		
		// Get the definition of this attribute, and its provider
		AttributeMap map = attrMaps.get(attrName);
		if (map == null) {
			throw new UnknownAttributeError(attrName);
		}
		
		// Is this really readable?
		if (!map.def.isReadable()) {
			throw new WriteOnlyAttributeError(attrName);
		}
		
		// Do we already have a valid value in the cache?
		AttributeValue val;
		if ((val = attrCache.get(attrName)) != null) {
			long age = System.currentTimeMillis() - val.getFetchedTime();
			if (age < map.def.cacheableFor()) {
				
				// Yeah, cool
				return val.getValue();
			}
		}
		
		// Ok, fetch the attribute from the provider, and optionally cache it
		try {
			String strVal = map.provider.getAttributeValue(attrName); 
			val = new AttributeValue(strVal, System.currentTimeMillis()); 
		} catch (AttributeUnavailableException e) {
			logger.error("Failed to get an attribute that we thought would be available: '{}'", e.getAttribute());
			throw e;
		}
		if (map.def.cacheableFor() > 0) {
			attrCache.put(attrName, val);
		}
		return val.getValue();
	}
}
