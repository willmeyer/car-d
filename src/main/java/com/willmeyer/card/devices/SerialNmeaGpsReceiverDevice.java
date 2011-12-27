package com.willmeyer.card.devices;

import com.willmeyer.card.Device;
import com.willmeyer.card.exception.*;

import com.willmeyer.util.PropertiesPlusPlus;

import com.willmeyer.jgps.*;

public class SerialNmeaGpsReceiverDevice implements Device {

	protected static final String PROP_RS232PORT = "device.com.willmeyer.card.devices.GpsReceiver.rs232port";

	protected NmeaSerialGpsDeviceV3 gps = null;
	
	public void stop() {
		gps.disconnect();
	}

	public String getInterfaceName() {
		return "com.willmeyer.jgps.GpsReceiver";
	}
	
	public Object getInterfaceImpl() {	
		return gps;
	}

	public void start(PropertiesPlusPlus props) throws ComponentInitException {
		try {
			gps = new NmeaSerialGpsDeviceV3(props.getProperty(PROP_RS232PORT, "MOCK"));
			gps.connect();
		} catch (Exception e) {
			throw new ComponentInitException(e.getMessage(), ComponentInitException.FailureMode.UNRECOVERABLE);
		}
	}

}
