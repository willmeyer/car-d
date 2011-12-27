package com.willmeyer.card.commands;

import java.io.PrintStream;

import com.willmeyer.commander.*;

import com.willmeyer.card.ext.win32.*;

public final class SpeakCommand extends OperationalCmdBase {

	public SpeakCommand() {
		super(1,-1);
	}
	
	@Override
	public void executeForHuman(PrintStream out, String[] params)
		throws UsageException, ExecutionException {
		String message = "";
		for (String param : params) {
			message += (param + " ");
		}
		out.println("Speaking: '" + message + "'");
		Speech.activate(message);
	}

	@Override
	public CommandResponse executeForMachine(String[] params)
		throws UsageException, ExecutionException {
		return new CommandResponse();
	}

	@Override
	public String getName() {
		return "say";
	}

	@Override
	public String getReadableUsageString() {
		return "<the text to speak>"; 
	}

	@Override
	public void printReadableHelpDetail(PrintStream dest) {
		dest.println("Speaks a message.");
	}

	@Override
	public void validateParams(String[] params) throws UsageException {
	}
}
