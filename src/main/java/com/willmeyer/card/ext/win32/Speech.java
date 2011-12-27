package com.willmeyer.card.ext.win32;

import org.slf4j.*;

public final class Speech {

	private static final int MAX_LEN = 300;
	private static final Logger logger = LoggerFactory.getLogger(Speech.class);
	
	private Speech() {
	}

	public static void activate(String message) {
		assert message != null;
		String toSpeak = null;
		if (message.length() > MAX_LEN) {
			toSpeak = message.substring(0, MAX_LEN) + " (message truncated)";
		} else
			toSpeak = message;
		try {
			logger.info("Speaking text: \"" + toSpeak + "\"");
			String cmd = "cmd /C ext\\win32\\speakit\\speakit.cmd \"" + message + "\"";
			Process proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();
			if (proc.exitValue() != 0) {
				throw new Exception ("SpeakIt exitCode " + proc.exitValue());
			}
		} catch (Exception e) {
			logger.error("Unable to activate speech: " + e.getMessage());
		}
	}

}
