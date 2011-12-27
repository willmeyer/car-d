package com.willmeyer.card.lcd;

import java.util.*;
import java.io.*;

import org.slf4j.LoggerFactory;

import com.willmeyer.jlcd.*;
import com.willmeyer.card.*;
import com.willmeyer.card.exception.*;

import org.slf4j.*;

import java.util.concurrent.atomic.*;

/**
 * Manages the LCD display.  It has a queue of FeedbackGetter objects, where each one is capable of
 * doing something to come up with a message to display.  They are managed in a queue, where each
 * one can indicate a time it wants to stay up for.  Callers can also submit feedback messages
 * directly.
 */
public final class LcdFeederBacker {

	protected SparkFunLcd lcd = null;
	protected AttributeManager attrMgr;
	protected List<FeedbackGetter> getters;
	protected final Logger logger = LoggerFactory.getLogger(LcdFeederBacker.class);
	protected int nextToGet = 0;
	protected AtomicLong nextGetTimeMs;

	protected static LcdFeederBacker theInstance = null;
	
	protected abstract class FeedbackGetter {
		
		public abstract String getFeedbackMessage();
		
		public int getMinDisplayTime() {
			return 5000;
		}
	}
	
	/**
	 * A FeedbackGetter that can get the value of a specific attribute and display it in a custom
	 * format string.
	 */
	protected class AttributeGetter extends FeedbackGetter {
	
		protected String attrName;
		protected String feedbackFormatString;
		
		public AttributeGetter(String attrName, String feedbackFormatString) {
			this.attrName = attrName;
			this.feedbackFormatString = feedbackFormatString;
		}
		
		public String getFeedbackMessage() {
			String feedbackString = null;
			try {
				String val = attrMgr.getAttributeValue(this.attrName);
				feedbackString = this.feedbackFormatString.replace("{}", val);
			} catch (Exception e) {
				logger.error("Couldn't get feedback message for attribute {}, {}", this.attrName, e.getMessage());
			}
			return feedbackString;
		}
	}
	
	public LcdFeederBacker(AttributeManager attrMgr) throws ComponentInitException {
		assert (theInstance == null);
		assert attrMgr != null;
		SparkFunLcd lcd = null;
		if (CarD.getDeviceManager().haveDeviceInterface("com.willmeyer.jlcd.SparkFunLcd")) {
			lcd = (SparkFunLcd)CarD.getDeviceManager().getDeviceInterface("com.willmeyer.jlcd.SparkFunLcd");
		}
		if (lcd == null) {
			throw new ComponentInitException("Missing necessary LCD device", ComponentInitException.FailureMode.TEMPORARY); 
		}
		try {
			this.lcd = lcd;
			this.attrMgr = attrMgr;
			getters = new LinkedList<FeedbackGetter>();
			
			// Init the display
			lcd.clearDisplay();
			lcd.backlightPercent(70);
			lcd.send1LineMessage("I'm online");
			
			// Install all the getters
			getters.add(new AttributeGetter("/obdii/sae.mph", "Current speed is {}"));
			getters.add(new AttributeGetter("/gps/position", "Currently at {}"));
			getters.add(new AttributeGetter("/obdii/sae.ect", "Coolant temp is {}"));
			getters.add(new AttributeGetter("/obdii/sae.fuel", "Fuel at {}"));
			
			// Initially not started
			this.nextGetTimeMs = new AtomicLong();
			this.nextGetTimeMs.set(System.currentTimeMillis());
		} catch (IOException e) {
			throw new ComponentInitException("Unable to communicate with LCD: " + e.getMessage(), ComponentInitException.FailureMode.UNRECOVERABLE);
		}
		theInstance = this;
	}

	/**
	 * Displays a message right now, and makes sure that it stays up for AT LEAST a certain amount
	 * of time.
	 */
	public void displayNow(String msg, int holdForMs) {
		
		// Set the wakeup time for the standard cycle
		this.delayNextCycledUpdate(holdForMs);
		
		// Send the message
		try {
			lcd.clearDisplay();
			lcd.sendWrappedMessage(msg);
		} catch (Exception e) {
			logger.error("Unable to send message to LCD: {}", e.getMessage());
		}
	}
	
	private void delayNextCycledUpdate(int delayByMs) {
		long nextTime = nextGetTimeMs.addAndGet(delayByMs);
		logger.debug("Delaying next cycle pdate, next time is {}", nextTime);
	}
	
	public void startUpdateCycle() {
		if (cycleRunning) 
			throw new IllegalStateException("Update cycle already consoleRunning");
		this.cycleRunning = true;
		logger.info("Starting LCD update cycle...");
		UpdateCycler cycler = new UpdateCycler();
		cycler.setDaemon(true);
		cycler.start();
		logger.debug("Update cycle is consoleRunning in thread {}", cycler.getName());
	}
	
	public void stopUpdateCycle() {
		logger.info("Stopping LCD update cycle...");
		if (cycleRunning) {
			cycleRunning = false;
		}
	}
	
	protected boolean cycleRunning = false;

	protected class UpdateCycler extends Thread {

		@Override
		public void run() {
			while (cycleRunning) {
				logger.debug("Update cycle loop hit, checking for updates...");
				
				// Grab the first feedback item
				long now = System.currentTimeMillis();
				long nextGet = nextGetTimeMs.get();
				if (now >= nextGet) {
					logger.debug("Found an update, consoleRunning it");

					// Ok do it
					FeedbackGetter getter = getters.get(nextToGet);
					try {
						String message = getter.getFeedbackMessage();
						if (message != null) {
							logger.debug("Update message is '{}'", message);
							lcd.clearDisplay();
							lcd.sendWrappedMessage(message);
						} else {
							logger.warn("Trying to display a message from a FeedbackGetter, but its null...");
						}

						// Set the next time appropriately
						nextGetTimeMs.addAndGet(getter.getMinDisplayTime());
					} catch (Exception e) { 
						logger.warn("An error ocurred while trying to get feedback for display: {}", e.getMessage());
					}
					
					// Try the next one next time
					if (nextToGet == (getters.size()-1)) nextToGet = 0;
					else nextToGet++;
				} else {
					long timeLeft = nextGet - now;
					logger.debug("Not ready yet, re-sleeping ({} more ms left)", timeLeft);
					
					// Not ready yet
					try {
						sleep(timeLeft);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			logger.debug("Update cycler exiting...");
		}
	}
	
}
