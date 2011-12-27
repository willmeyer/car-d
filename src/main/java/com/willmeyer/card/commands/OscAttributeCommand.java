package com.willmeyer.card.commands;

import java.io.PrintStream;

import com.willmeyer.card.AttributeManager;
import com.willmeyer.card.exception.*;
import com.willmeyer.commander.*;

public final class OscAttributeCommand extends OperationalCmdBase {

	protected AttributeManager attrMgr;
	
	public OscAttributeCommand(AttributeManager attrMgr) {
		super(1,-1);
		this.attrMgr = attrMgr;
	}
	
	@Override
	public void executeForHuman(PrintStream out, String[] params)
		throws UsageException, ExecutionException {
		String attrName = params[0];
		try {
			if (params.length > 1) {
	
				// This is a set
				String val = params[1];
				attrMgr.setAttributeValue(attrName, val);
				out.println("Attribute set!");
			} else {
				
				// This is a get
				String val = attrMgr.getAttributeValue(attrName);
				out.println("Attribute value is '" + val + "'");
			}
		} catch (CodedException e) {
			throw new ExecutionException(e.getStatusCode(), e.getMessage());
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
		return "attr";
	}

	@Override
	public String getReadableUsageString() {
		return "[/path/of/attribute]"; 
	}

	@Override
	public void printReadableHelpDetail(PrintStream dest) {
		dest.println("Gets the value of a specific system attribute.");
	}

	@Override
	public void validateParams(String[] params) throws UsageException {
		
	}
}
