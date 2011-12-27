package com.willmeyer.card.commands;

import java.io.PrintStream;

import com.willmeyer.card.*;

import com.willmeyer.commander.*;

public final class StandbySwitchCommand extends OperationalCmdBase {

	public StandbySwitchCommand() {
		super(0,-1);
	}
	
	@Override
	public void executeForHuman(PrintStream out, String[] params)
		throws UsageException, ExecutionException {
		CarD card = CarD.getInstance();
		if (card.inStandby()) {
			out.println("Leaving standby, activating...");
			card.standby(false);
			out.println("Now active.");
		} else {
			out.println("Entering standby...");
			card.standby(true);
			out.println("System standing by");
		}
	}

	@Override
	public CommandResponse executeForMachine(String[] params)
		throws UsageException, ExecutionException {
		return new CommandResponse();
	}

	@Override
	public String getName() {
		return "standby";
	}

	@Override
	public String getReadableUsageString() {
		return ""; 
	}

	@Override
	public void printReadableHelpDetail(PrintStream dest) {
		dest.println("Places the system in our out of standby mode.");
	}

	@Override
	public void validateParams(String[] params) throws UsageException {
	}
}
