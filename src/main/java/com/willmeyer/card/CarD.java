package com.willmeyer.card;

import java.io.*;

import org.slf4j.*;

import com.willmeyer.commander.*;
import com.willmeyer.commander.input.*;
import com.willmeyer.commander.input.gtalk.*;

import com.willmeyer.card.commands.*;
import com.willmeyer.card.exception.*;
import com.willmeyer.card.ext.win32.Speech;
import com.willmeyer.card.lcd.*;
import com.willmeyer.card.leds.*;
import com.willmeyer.card.web.*;

import com.willmeyer.util.*;

/**
 * The main application class, and also the interface exposed to modules (which can be used to get other interfaces).
 */
public class CarD implements LocalHumanConsoleInterface.QuitHandler
{

	/**
	 * Called when our local console requests a quit...we actually shut the system down int his case
	 */
	public void quit() {
		logger.info("Responding to local user quit command...shutting down...");
		this.shutdown();
	}

	protected CommandSet commands = null;
	protected CmdInterfaceManager inputs = null;
	protected LedFeederBacker ledFb = null;
	protected WebInterface web = null;
	protected PropertiesPlusPlus props = null;
	protected File propsFile = null;
	
	protected final Logger logger = LoggerFactory.getLogger(CarD.class);

	protected static AttributeManager attrMgr = null;
	protected static DeviceManager devices = null;
	protected static NetManager netMgr = null;
	protected static CarD theInstance = null;
	protected static LcdFeederBacker lcdFb = null;

	public static AttributeManager getAttributeManager() {
		return attrMgr;
	}
	
	public static DeviceManager getDeviceManager() {
		return devices;
	}

	public static LcdFeederBacker getLcd() {
		return lcdFb;
	}

	public static NetManager getNetManager() {
		return netMgr;
	}

	public static CarD getInstance() {
		return theInstance;
	}
	
	/**
	 * CTOR doesn't do much
	 */
	CarD(File propsFile) {
		theInstance = this;
		this.propsFile = propsFile;
	}
	
	/**
	 * Core component shutdown, the reverse of initCore. 
	 */
	protected void shutdownCore() {
		logger.info("Shutting down core...");
		if (commands != null) {
			commands.clearCommands();
			commands = null;
		}
		if (inputs != null) {
			inputs.stopInterfaces();
			inputs = null;
		}
		if (netMgr != null) {
			netMgr = null;
		}
		if (web != null) {
			web.stop();
			web = null;
		}
		logger.info("Shut down.");
	}

	/**
	 * Initializes core components.  If any of it fails, we return false.  If we do so, the assumption
	 * is that shutdown will be called.  Shutdown therefore assumes that things can be in incomplete init state.
	 * 
	 * @return boolean indicating overall success; false means a fatal error and we can't continue.
	 */
	protected boolean initCore() {
    	long startMs = System.currentTimeMillis();
		logger.info("Starting system (@ {} ms)", startMs);
		try {
			
			// Misc props stuff for common code
			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "error");
	    	
			// Attribute manager, not yet with any attributes
			logger.debug("Starting attribute manager...");
			attrMgr = new AttributeManager(props);

			// Device manager, not yet with any devices
			logger.info("Starting device manager...");
			devices = new DeviceManager(props);
			
			// Web interface, if its enabled
			if (props.getBooleanProperty("webinterface.enabled", false)) {
				web = new WebInterface(props.getIntPropertyRequired("webinterface.port"));
			}

			// Net manager
			netMgr = new NetManager(props);
			
			// Set up our commands
			System.out.println("Setting up commands...");
			commands = new CommandSet();
			commands.installCommand(new RestartCommand());
			commands.installCommand(new StandbySwitchCommand());
			commands.installCommand(new UpdateNetConnectionCommand(attrMgr));
			commands.installCommand(new LcdDisplayCommand());
			commands.installCommand(new SpeakCommand());
			commands.installCommand(new OscAttributeCommand(attrMgr));
			commands.installCommand(new AttributeListCommand(attrMgr));
	
			// Set up logger
			CommandLogger cmdLogger = new CommandLogger();
			
			// Set up the processors our different interfaces can use, including our human language processor and the attribute manager's processor
			NaturalLanguageHandler nl = new NaturalLanguageHandler(attrMgr);
			AttributeShellHandler attr = new AttributeShellHandler(attrMgr);
			HumanShellProcessor humanShell = new HumanShellProcessor(commands, cmdLogger);
			humanShell.addFallback(nl);
			humanShell.addFallback(attr);
			//MachineCommandProcessor machine = new MachineCommandProcessor(commands, cmdLogger);
			
			// Set up all the command input interfaces
			logger.debug("Setting up interfaces...");
			inputs = new CmdInterfaceManager();
			CmdInputInterface localConsole = new LocalHumanConsoleInterface(new BufferedReader(new InputStreamReader(System.in)), System.out, humanShell, this);
			inputs.installInterface(localConsole);
			if (props.getBooleanProperty("console.net.enabled", false)) {
				CmdInputInterface networkedConsole = new NetworkedHumanConsoleInterface(props.getIntPropertyRequired("console.net.port"), humanShell);
				inputs.installInterface(networkedConsole);
			}
			/*
			if (props.getBooleanProperty("console.gtalk.enabled", false)) {
				CmdInputInterface jabberShell = new GTalkShell(props.getPropertyRequired("console.gtalk.username"), props.getPropertyRequired("console.gtalk.password"), "Yeah, I'm a car, so?", humanShell);
				inputs.installInterface(jabberShell);
			}
			*/
			
			// Start the input interfaces...
			logger.debug("Starting interfaces...");
			inputs.startInterfaces();
		} catch (ComponentInitException e) {
			logger.error("System initialization failed: {}", e.getMessage());
			return false;
		} catch (PropertiesPlusPlus.PropertyException e) {
			logger.error("System initialization failed: {}", e.getMessage());
			return false;
		}
		return true;
	}
	
	protected void shutdown() {
		shutdownDeviceDependent();
		this.loadDevicesAndAttributes(false);
		shutdownCore();
	}
	
	private boolean inStandby = false;
	
	public boolean inStandby() {
		return inStandby;
	}

	public void restart() {
  		logger.info("Excuting full system restart...");
  		Speech.activate("Initiating full system restart.");
		this.shutdown();
		this.initAll();
	}
	
	public void standby(boolean enter) {
	  	if (enter) {
	  		logger.info("Entering standby state...");
	  		this.shutdownDeviceDependent();
	  		this.loadDevicesAndAttributes(false);
	  		this.inStandby = true;
	  		Speech.activate("I'm going to sleep...");
	  		logger.info("Now in standby.");
	  	} else {
	  		logger.info("Resuming from standby state...");
	  		this.loadDevicesAndAttributes(true);
	  		this.initDeviceDependent();
	  		this.inStandby = false;
	  		logger.info("Back online.");
	  		Speech.activate("I'm awake.");
	  	}
	}

	/**
	 * Given an existing DeviceManager and AttributeManager, clears or loads all devices and attributes. 
	 */
	protected void loadDevicesAndAttributes(boolean load) {
	  	long startMs = System.currentTimeMillis();
		if (load) {
		  	logger.info("Starting device/attr init (@ {} ms)", startMs);
			logger.info("Starting devices...");
			devices.installAndStartDevices();
			logger.info("Devices started.");
			logger.info("Loading attributes...");
			attrMgr.loadAttributes();
			logger.info("Attributes loaded.");
		} else {
		  	logger.info("Unloading devices and attributes.");
			devices.stopAndUnloadDevices();
			attrMgr.clearAttributes();
			logger.info("Unloaded");
		}
	}
	
	protected boolean initAll() {
		try {
			this.props = new PropertiesPlusPlus ();
			props.load(new FileInputStream(this.propsFile));
		} catch (Exception e) {
			logger.error("Configuration unavailable: {}", e.getMessage());
			return false;
		}
		boolean ok = this.initCore();
		if (!ok) return false;
		this.loadDevicesAndAttributes(true);
		ok = this.initDeviceDependent();
		return ok;
	}
	
	protected boolean initDeviceDependent() {
    	long startMs = System.currentTimeMillis();
		logger.info("Starting device-dependent init (@ {} ms)", startMs);
	    	
		// Tell the attribute manager to initialize attributes
		logger.debug("Initializing attributes...");
		attrMgr.loadAttributes();

		// LCD feedback manager, if enabled
		try {
			if (props.getBooleanProperty("lcdmonitor.enabled", false)) {
				logger.debug("Starting LCD feedback...");
				lcdFb = new LcdFeederBacker(attrMgr);
				lcdFb.startUpdateCycle();
			}
		} catch (ComponentInitException e) {
			logger.warn("LCD feedback loop couldn't be started: {}", e.getMessage());
		} finally {
			if (lcdFb == null) {
				logger.info("LCD feedback loop is disabled...");
			}
		}
		/**	
			// LED control, if we have a FusionBrain
			if (devices.haveDeviceInterface("com.willmeyer.jfusionbrain.FusionBrainV3")) {
				FusionBrainV3 fbrain = (FusionBrainV3)devices.getDeviceInterface("com.willmeyer.jfusionbrain.FusionBrainV3");
				ledFb = new LedFeederBacker(fbrain);
			}
			
		} catch (ComponentInitException e) {
			logger.error("System initialization failed: {}", e.getMessage());
			return false;
		}
		*/
		return true;
	}
	
	protected void shutdownDeviceDependent() {
		logger.info("Shutting down device-dependent components...");
		if (lcdFb != null) {
			lcdFb.stopUpdateCycle();
			lcdFb = null;
		}
		if (ledFb != null) {
			ledFb = null;
		}
		if (attrMgr != null) {
			attrMgr.clearAttributes();
		}
	}

	/**
	 * Handles command-line args then starts up the engine.
	 * 
	 * CarD <config-file>
	 */
	public static void main( String[] args )
    {
    	try {
	        
    		// confirm the props file exists
    		String propsFile = "card.conf";
    		if (args.length > 0) {
	        	propsFile = args[0];
	        }
    		File theFile = new File(propsFile);
    		if (!(theFile.exists() && theFile.canRead())) {
    			throw new Exception ("Invalid or missing properties file '" + propsFile + "'");
    		}
	    	CarD card = new CarD(theFile);
	    	if (!card.initAll()) {
	    		card.shutdown();
	    		throw new Exception();
	    	}
	  		Speech.activate("I'm alive.");
    	} catch (Exception e) {
    		if (e.getMessage() != null) {
    			System.out.println("Sorry, the system couldn't start: " + e.getMessage());
    		} else {
        		System.out.println("Sorry, the system couldn't start.");
    		}
    		usage();
    	}
    }
	
	private static void usage() {
		System.out.println("Usage: <propsFile>");
	}
}
