package com.willmeyer.card.devices;

import com.willmeyer.card.Device;
import com.willmeyer.card.exception.*;

import com.willmeyer.util.PropertiesPlusPlus;

import com.willmeyer.jlcd.*;

public class SparkFunLcdDevice implements Device {

	protected static final String PROP_RS232PORT = "device.com.willmeyer.card.devices.SparkFunLcdDevice.rs232port";

	protected SparkFunLcd lcd = null;
	
	public SparkFunLcdDevice() {
	}
	
	public void stop() {
		try {
			lcd.clearDisplay();
		} catch (Exception e) {}
		lcd.disconnect();
		lcd = null;
	}

	public String getInterfaceName() {
		return "com.willmeyer.jlcd.SparkFunLcd";
	}
	
	public Object getInterfaceImpl() {	
		return lcd;
	}

	public void start(PropertiesPlusPlus props) throws ComponentInitException {
		try {
			lcd = new SparkFunLcd(props.getProperty(PROP_RS232PORT, "MOCK"));
			lcd.connect();
			Thread.sleep(200);
			lcd.backlightPercent(60);
			Thread.sleep(200);
			lcd.clearDisplay();
		} catch (Exception e) {
			throw new ComponentInitException(e.getMessage(), ComponentInitException.FailureMode.UNRECOVERABLE);
		}
	}
}
