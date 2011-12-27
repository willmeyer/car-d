package com.willmeyer.card.attrproviders;

import java.util.*;

import com.willmeyer.card.*;
import com.willmeyer.jobdii.*;
import com.willmeyer.card.exception.*;

public class Obd2AttributeProvider implements AttributeProvider {

	protected Obd2Reader obdii;
	protected List<AttributeDef> attrDefs;
	
	public String getAttributeValue(String name) throws AttributeUnavailableException {
		
		// Which Pid?
		String pidTerm = name.substring("/obdii".length() + 1);
		int dot = pidTerm.indexOf(".");
		String setName = pidTerm.substring(0, dot);
		String pidName = pidTerm.substring(dot+1);
		PidSet pidSet = null;
		if (setName.equalsIgnoreCase("SAE")) {
			pidSet = SaePidSet.theSet();
		}
		assert pidSet != null;
		Pid pid = pidSet.getPid(pidName);
		assert pid != null;
		
		// Get it
		try {
			Number val = obdii.getPid(1, pid);
			String valStr = val + " " + pid.getUnits();
			return valStr;
		} catch (Exception e) {
			throw new AttributeUnavailableException(name);
		}
	}

	private void addSetPidsAsAttrs(PidSet pidSet) {
		String setName = pidSet.getName();
		Set<String> pidNames = pidSet.getPidNames();
		for (String pidName : pidNames) {
			String attrName = "/obdii/" + setName.toLowerCase() + "." + pidName.toLowerCase();
			String attrFriendly = pidSet.getPid(pidName).getFriendlyName();
			attrDefs.add(new AttributeDef(attrName, attrFriendly, 1000));
		}
	}
	
	private void initAttrDefs() {
		attrDefs = new LinkedList<AttributeDef>();
		this.addSetPidsAsAttrs(SaePidSet.theSet());
	}
	
	public List<AttributeDef> getSupportedAttributes() {
		return attrDefs;
	}
	
	public Obd2AttributeProvider() throws Exception {
		obdii = (Obd2Reader)CarD.getDeviceManager().getDeviceInterface("com.willmeyer.jobdii.Obd2Reader");
		if (obdii == null) {
			throw new Exception("Missing an OBDII device"); 
		}
		
		// Set up our maps, pids, attrs, etc.
		initAttrDefs();
	}

}
