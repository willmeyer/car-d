package com.willmeyer.card.commands;

import java.io.PrintStream;

import com.willmeyer.card.*;

import com.willmeyer.commander.*;

public final class UpdateNetConnectionCommand extends OperationalCmdBase {

	protected AttributeManager attrMgr;
	
	public UpdateNetConnectionCommand(AttributeManager attrMgr) {
		super(0,-1);
		this.attrMgr = attrMgr;
	}
	
	@Override
	public void executeForHuman(PrintStream out, String[] params)
		throws UsageException, ExecutionException {
		
		// Get the IP Manager and see what's what
		NetManager ipMgr = CarD.getNetManager();
		if (ipMgr.haveConnectivity()) {
			out.println("We DO have external connectivity.");
			NetManager.IpInfo info = ipMgr.getAndUpdateIp();
			out.println("Our remote IP is: " + info.remoteIp);
		}
		ipMgr.getAndUpdateIp();
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
		return "net";
	}

	@Override
	public String getReadableUsageString() {
		return ""; 
	}

	@Override
	public void printReadableHelpDetail(PrintStream dest) {
		dest.println("Checks the status of the net connection, and updates our server with our info.");
	}

	@Override
	public void validateParams(String[] params) throws UsageException {
		
	}
}
