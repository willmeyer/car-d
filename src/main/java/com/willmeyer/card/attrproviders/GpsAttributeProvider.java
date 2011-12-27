package com.willmeyer.card.attrproviders;

import java.util.*;

import com.willmeyer.card.*;
import com.willmeyer.jgps.*;

public class GpsAttributeProvider implements AttributeProvider, GpsReceiver.GpsListener {

	public void onPositionUpdate(String pos) {
		currentPos = pos;
		String[] elems = pos.split("/");
		currentLatDegrees = elems[0].trim();
		currentLonDegrees = elems[1].trim();
	}

	protected static final String POS_UNKNOWN = "?? / ??";
	
	protected List<AttributeDef> attrDefs;
	protected GpsReceiver gps;
	protected String currentPos;
	protected String currentLatDegrees;
	protected String currentLonDegrees;
	
	public String getAttributeValue(String name) {
		String val = null; 
			
		// Get it
		if (name.equals("/gps/position")) {
			val = currentPos;
		} else if (name.equals("/gps/lat")) {
			val = currentLatDegrees;
		} else if (name.equals("/gps/lon")) {
			val = currentLonDegrees;
		}
		assert val != null;
		return val;
	}

	public List<AttributeDef> getSupportedAttributes() {
		return attrDefs;
	}
	
	public GpsAttributeProvider() throws Exception {
		gps = (GpsReceiver)CarD.getDeviceManager().getDeviceInterface("com.willmeyer.jgps.GpsReceiver");
		if (gps == null) {
			throw new Exception("Missing a GPS device"); 
		}
		
		gps.setListener(this);
		
		// Current position default
		onPositionUpdate(POS_UNKNOWN);
		
		// Set up defs
		attrDefs = new LinkedList<AttributeDef>();
		attrDefs.add(new AttributeDef("/gps/position", "current position", -1));
		attrDefs.add(new AttributeDef("/gps/lat", "current latitude", -1));
		attrDefs.add(new AttributeDef("/gps/lon", "current longitude", -1));
	}

}
