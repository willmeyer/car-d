package com.willmeyer.card.devices;

import com.willmeyer.card.Device;
import com.willmeyer.card.exception.*;

import com.willmeyer.util.PropertiesPlusPlus;

import com.willmeyer.jfusionbrain.*;

public class FusionBrainDevice implements Device {

	protected static final String PROP_DEVICEINDEX = "device.com.willmeyer.card.devices.FusionBrainDevice.usbindex";

	protected FusionBrainV3 fbrain = null;
	
	public void stop() {
		fbrain.shutdownDevice();
	}

	public String getInterfaceName() {
		return "com.willmeyer.jfusionbrain.FusionBrainV3";
	}
	
	public Object getInterfaceImpl() {	
		return fbrain;
	}

	public void start(PropertiesPlusPlus props) throws ComponentInitException {
		fbrain = new FusionBrainV3();
		try {
			fbrain.initDevice(props.getIntProperty(PROP_DEVICEINDEX, 1));
		} catch (Exception e) {
			throw new ComponentInitException(e.getMessage(), ComponentInitException.FailureMode.UNRECOVERABLE);
		}
	}

}
