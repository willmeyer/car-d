package com.willmeyer.card.commands;

import java.io.PrintStream;

import com.willmeyer.card.*;

import com.willmeyer.commander.*;

public final class RestartCommand extends OperationalCmdBase {

	public RestartCommand() {
		super(0,-1);
	}
	
	@Override
	public void executeForHuman(PrintStream out, String[] params)
		throws UsageException, ExecutionException {
		CarD card = CarD.getInstance();
		out.println("Restarting system...you'll lose us for a minute...");
		card.restart();
	}

	@Override
	public CommandResponse executeForMachine(String[] params)
		throws UsageException, ExecutionException {
		return new CommandResponse();
	}

	@Override
	public String getName() {
		return "restart";
	}

	@Override
	public String getReadableUsageString() {
		return ""; 
	}

	@Override
	public void printReadableHelpDetail(PrintStream dest) {
		dest.println("Restarts the entire system, with the latest configuration.");
	}

	@Override
	public void validateParams(String[] params) throws UsageException {
	}
}
