package com.willmeyer.card.devices;

import com.willmeyer.card.Device;
import com.willmeyer.card.exception.*;

import com.willmeyer.util.PropertiesPlusPlus;

import com.willmeyer.jobdii.*;

public class Elm32xObd2ReaderDevice implements Device {

	protected static final String PROP_RS232PORT = "device.com.willmeyer.card.devices.Obd2Reader.rs232port";

	protected Elm32x obd2 = null;
	
	public void stop() {
		obd2.disconnect();
	}

	public String getInterfaceName() {
		return "com.willmeyer.jobdii.Obd2Reader";
	}
	
	public Object getInterfaceImpl() {	
		return obd2;
	}

	public void start(PropertiesPlusPlus props) throws ComponentInitException {
		try {
			obd2 = new Elm32x(props.getProperty(PROP_RS232PORT, "MOCK"));
			obd2.connect();
		} catch (Exception e) {
			throw new ComponentInitException(e.getMessage(), ComponentInitException.FailureMode.UNRECOVERABLE);
		}
	}

}
