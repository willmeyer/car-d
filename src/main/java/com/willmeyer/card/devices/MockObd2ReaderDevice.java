package com.willmeyer.card.devices;

import com.willmeyer.card.Device;
import com.willmeyer.util.PropertiesPlusPlus;

import com.willmeyer.jobdii.*;

public class MockObd2ReaderDevice implements Device {

	protected MockObd2Reader obd2 = null;
	
	public void stop() {
	}

	public String getInterfaceName() {
		return "com.willmeyer.jobdii.Obd2Reader";
	}
	
	public Object getInterfaceImpl() {	
		return obd2;
	}

	public void start(PropertiesPlusPlus props) {
		obd2 = new MockObd2Reader();
	}

}
