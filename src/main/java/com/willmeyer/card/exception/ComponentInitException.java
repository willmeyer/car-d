package com.willmeyer.card.exception;

/**
 * An exception that indicates an error during component initialization, with additional information 
 * suggesting the recoverability of the exception.
 */
@SuppressWarnings("serial")
public class ComponentInitException extends CodedException {

	public static enum FailureMode {
		UNRECOVERABLE, // failed, and isn't going to start working 
		ABORT_ALL, // failed, and suggesting that we abort execution completely (may not be honored)
		TEMPORARY // a temporary failure, might be recoverable on reinitialization
	}
	
	FailureMode mode;
	
	public ComponentInitException(String description, FailureMode mode) {
		super(StatusCodes.ERR_COMPONENT_INIT, description);
		this.mode = mode;
	}
	
	public FailureMode getFailureMode() {
		return this.mode;
	}
}
