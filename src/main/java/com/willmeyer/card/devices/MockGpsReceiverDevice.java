package com.willmeyer.card.devices;

import com.willmeyer.card.Device;
import com.willmeyer.util.PropertiesPlusPlus;

import com.willmeyer.jgps.*;

public class MockGpsReceiverDevice implements Device {

	protected MockGpsDevice gps = null;
	
	public void stop() {
	}

	public String getInterfaceName() {
		return "com.willmeyer.jgps.GpsReceiver";
	}
	
	public Object getInterfaceImpl() {	
		return gps;
	}

	public void start(PropertiesPlusPlus props) {
		gps = new MockGpsDevice();
	}

}
