package com.willmeyer.card.commands;

import java.io.PrintStream;
import java.util.*;

import com.willmeyer.card.*;
import com.willmeyer.commander.*;

public final class AttributeListCommand extends OperationalCmdBase {

	protected AttributeManager attrMgr;
	
	public AttributeListCommand(AttributeManager attrMgr) {
		super(0,0);
		this.attrMgr = attrMgr;
	}
	
	@Override
	public void executeForHuman(PrintStream out, String[] params)
		throws UsageException, ExecutionException {
		out.println("All available attributes:");
		Collection<AttributeDef> attrs = attrMgr.getAvailableAttributes();
		for (AttributeDef def : attrs) {
			out.println("  " + def.getShortName() + ": " + def.getFriendlyName());
		}
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
		return "attrs";
	}

	@Override
	public String getReadableUsageString() {
		return ""; 
	}

	@Override
	public void printReadableHelpDetail(PrintStream dest) {
		dest.println("Shows a list of all available attributes in the system.");
	}

	@Override
	public void validateParams(String[] params) throws UsageException {
		
	}
}
