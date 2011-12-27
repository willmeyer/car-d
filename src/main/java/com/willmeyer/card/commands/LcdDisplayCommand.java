package com.willmeyer.card.commands;

import java.io.PrintStream;

import com.willmeyer.card.*;
import com.willmeyer.card.lcd.*;
import com.willmeyer.commander.*;

public final class LcdDisplayCommand extends OperationalCmdBase {

	public LcdDisplayCommand() {
		super(1,-1);
	}
	
	@Override
	public void executeForHuman(PrintStream out, String[] params)
		throws UsageException, ExecutionException {
		LcdFeederBacker lcd = CarD.getLcd();
		if (lcd == null) {
			out.println("The LCD isn't currently connected, sorry.");
			return;
		}
		String message = "";
		for (String param : params) {
			message += (param + " ");
		}
		out.println("Writing to display: '" + message + "'");
		lcd.displayNow(message, 5000);
	}

	@Override
	public CommandResponse executeForMachine(String[] params)
		throws UsageException, ExecutionException {
/*
 		String attrName = params[0];
 
		if (params.length > 1) {

			// This is a set
			String val = params[1];
			attrMgr.setAttributeValue(attrName, val);
			responseCode = StatusCodes.ERR_NONE;
			responseDetail = val;
		} else {
			
			// This is a get
			String val = attrMgr.getAttributeValue(attrName);
			responseCode = StatusCodes.ERR_NONE;
			responseDetail = val;
		}
		*/
		return new CommandResponse();
	}

	@Override
	public String getName() {
		return "write";
	}

	@Override
	public String getReadableUsageString() {
		return "<the text to display>"; 
	}

	@Override
	public void printReadableHelpDetail(PrintStream dest) {
		dest.println("Displays a text message to the in-console LCD.");
	}

	@Override
	public void validateParams(String[] params) throws UsageException {
	}
}
