package com.willmeyer.card;

import com.willmeyer.commander.HumanShellProcessor.FallbackProcessor;
import com.willmeyer.card.exception.*;

import java.io.*;

import org.slf4j.*;

/**
 * Super-Ghetto!  Maybe someday this could do some real NL parsing, have the shakespearean insult 
 * generator, etc.
 */
final class NaturalLanguageHandler implements FallbackProcessor {

	protected AttributeManager attrMgr;

	protected final Logger logger = LoggerFactory.getLogger(NaturalLanguageHandler.class);
	
	public NaturalLanguageHandler(AttributeManager attrMgr) {
		this.attrMgr = attrMgr;
	}
	
	public boolean fallback(String commandLine, PrintStream out) {
		String msg = null;
		commandLine = commandLine.toLowerCase();
		try {
			if (
				(commandLine.contains("fast ") || commandLine.contains("speed")) && (commandLine.contains("?"))
				) {
				
				// get speed
				String speed = attrMgr.getAttributeValue("/obdii/sae.mph");
				msg = "I'm currently going " + speed;
			} else if (
					(commandLine.contains("where") || commandLine.contains("position")) && (commandLine.contains("?"))
					) {
	
				// get position
				String lat = attrMgr.getAttributeValue("/gps/lat");
				String lon = attrMgr.getAttributeValue("/gps/lon");
				String googleMapsUrl = "http://maps.google.com/maps?q=" + lat + ",+" + lon;
				msg = "Right now I'm at " + lat + ", " + lon + " (" + googleMapsUrl + ")";
			} else if (
					commandLine.contains("hello") || commandLine.contains("hi ")
					) {
	
				// Hi!
				msg = "Hi there!";
			} else if (
					commandLine.contains("doing") || commandLine.contains("up to") || commandLine.contains("wassup?")
					) {
	
				// what are you doing?
				msg = "Chillin.";
			} else if (
					(commandLine.contains("who")) && (commandLine.contains("?"))
					) {
	
				// get position
				msg = "I'm REFACTR, Will's car.";
			} else {
				logger.warn("We tried to interpret '{}' as natural language, but no dice.", commandLine);
			}
		} catch (CodedException e) {
			logger.error("Couldn't do what we wanted to: {}", e.getMessage());
		}
		if (msg != null) {
			out.println(msg);
			return true;
		} else {
			return false;
		}
	}

}
