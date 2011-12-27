package com.willmeyer.card;

import com.willmeyer.commander.HumanShellProcessor.FallbackProcessor;

import com.willmeyer.card.exception.*;

import java.io.*;

import org.slf4j.*;

/**
 * A FallbackProcessor for command shells, which interprets commands as installed attribute names and just returns the
 * value.  Basically, just easy way to get an attribute value in a shell just by typing part of its name. 
 */
final class AttributeShellHandler implements FallbackProcessor {

	protected AttributeManager attrMgr;

	protected final Logger logger = LoggerFactory.getLogger(AttributeShellHandler.class);
	
	public AttributeShellHandler(AttributeManager attrMgr) {
		this.attrMgr = attrMgr;
	}
	
	public boolean fallback(String commandLine, PrintStream out) {
		
		// See if any attribute short or friendly names are included in the commandline
		boolean handled = false;
		for (AttributeDef def : attrMgr.getAvailableAttributes()) {
			if (handled) break;
			
			// If the command line is a subset of the short attribute name, or if the command line includes the friendly name
			if (def.getShortName().toLowerCase().contains(commandLine.toLowerCase())) {
				handled = true;
				this.getAttribute(def.getShortName(), def.getFriendlyName(), out);
			} else if (commandLine.toLowerCase().contains(def.getFriendlyName().toLowerCase())) {
				handled = true;
				this.getAttribute(def.getShortName(), def.getFriendlyName(), out);
			}
		}
		return handled;
	}

	private void getAttribute(String shortName, String friendlyName, PrintStream out) {
		try {
			String val = attrMgr.getAttributeValue(shortName);
			out.println(friendlyName + " is " + val);
		} catch (CodedException e) {
			out.println(e.getMessage());
		}
	}
}
