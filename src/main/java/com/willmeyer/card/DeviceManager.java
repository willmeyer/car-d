package com.willmeyer.card;

import java.util.*;

import org.slf4j.*;

import com.willmeyer.util.PropertiesPlusPlus;
import com.willmeyer.card.exception.*;

/**
 * The DeviceManager manages the set of all available devices.  It loads Device implementations as 
 * specified in the application properties file, according to the following scheme:
 * 
 * device.#.enabled=true|false
 * device.#.class=com.company.device.MyDevice
 * 
 * This manager maintains the set of available devices and the interfaces they expose, and other 
 * components can query it for these.
 * 
 * Devices that are loaded are treated as "installed", devices that are also successfully started
 * are treated as "started".  When the manager looks for available devices, it tries to start them.  
 * If they fail to start, they indicate whether the failure is such that they should still be left 
 * in the installed state and an attempt to start them later might work (device in use by some other
 * app, for example), or that the failure is complete.  Depending on what the device says about its
 * failure, the manager will either put it in the installed state or completely forget about it.
 * Devices that are installed but not started can be retried later through other manager methods.
 */
public class DeviceManager {

	protected final Logger logger = LoggerFactory.getLogger(DeviceManager.class);

	protected HashMap<String, Device> installedDevices = new HashMap<String, Device>();
	protected HashMap<String, Device> startedDevices = new HashMap<String, Device>();
	protected PropertiesPlusPlus initProps = null;
	
	/**
	 * Init the device manager and install/start the devices.
	 */
	DeviceManager(PropertiesPlusPlus props) {
		this.initProps = props;
	}
	
/*
 	public void resetAllDevices() {
 
		for (Device device : installedDevices.values()) {
			logger.info("Shutting down device {}", device.getClass().getCanonicalName());
			device.stop();
			logger.info("Reinitializing device {}", device.getClass().getCanonicalName());
			try {
				device.start(initProps);
			} catch (Exception e) {
				logger.error("Shit, a previously initialized device is now no longer workable: {}", e.getMessage());
			}
		}
	}
*/
	
	public void stopAndUnloadDevices() {
		for (Device device : startedDevices.values()) {
			device.stop();
		}
		this.startedDevices.clear();
		this.installedDevices.clear();
	}
	
	/**
	 * Load and start all available devices, if possible.  
	 */
	public void installAndStartDevices() {
	    	
		// Device connectivity
		logger.info("Loading devices...");
		int deviceNum = 1;
		boolean foundDevice = true;
		while (foundDevice) {
			foundDevice = false;
			logger.debug("Checking device {}", deviceNum);
			String propPrefix = "device." + deviceNum;
			String deviceEnabledProp = propPrefix + ".enabled";
			if (initProps.getProperty(deviceEnabledProp) != null) {
				foundDevice = true;
				boolean deviceEnabled = initProps.getBooleanProperty(deviceEnabledProp, false);
				if (deviceEnabled) {
					String deviceClassProp = propPrefix + ".class";
					String deviceClassName = initProps.getProperty(deviceClassProp);
					String deviceInterfaceName = null;
					Device device = null;
					try {
						device = (Device)Class.forName(deviceClassName).newInstance();
						device.start(initProps);
						logger.info("Device {} loaded and started.", deviceClassName);
						deviceInterfaceName = device.getInterfaceName();
						installedDevices.put(deviceInterfaceName, device);
						startedDevices.put(deviceInterfaceName, device);
					} catch (ComponentInitException e) {
						logger.warn("Device '{}' failed to start, error: {}", deviceClassName, e.getMessage());
						if (e.getFailureMode() == ComponentInitException.FailureMode.UNRECOVERABLE) {
							logger.warn("Device '{}' failure was permanent, not installing device.", deviceClassName);
						} else {
							logger.warn("Device '{}' failure might be recoverable, installing but leaving un-started.", deviceClassName);
							installedDevices.put(deviceInterfaceName, device);
						}
					} catch (Exception e) {
						logger.error("Class '{}' could not be loaded as a Device, ignoring (error: {})", deviceClassName, e.getMessage());
					}
				}
			}
			deviceNum++;
		}
	}
	
	/**
	 * @return the interface, or null if unavailable
	 */
	public Object getDeviceInterface(String interfaceName) {
		Device theDevice = startedDevices.get(interfaceName);
		if (theDevice != null) {
			return theDevice.getInterfaceImpl();
		} else {
			return null;
		}
	}
	
	/**
	 * A simple data object for representing state.
	 */
	public static class DeviceState {
		
		public String interfaceName;
		public boolean started;
		
		public DeviceState(String interfaceName, boolean started) {
			this.interfaceName = interfaceName;
			this.started = started;
		}
	}
	
	/**
	 * Gets a summary of the current state of all installed devices.
	 */
	public List<DeviceState> getDeviceSummary() {
		List<DeviceState> summary = new LinkedList<DeviceState>();
		Set<String> interfaces = installedDevices.keySet();
		for (String i : interfaces) {
			boolean started = (this.startedDevices.get(i) != null);
			summary.add(new DeviceState(i, started));
		}
		return summary;
	}

	public boolean haveDeviceInterface(String interfaceName) {
		Device theDevice = startedDevices.get(interfaceName);
		return (theDevice != null);
	}

}
