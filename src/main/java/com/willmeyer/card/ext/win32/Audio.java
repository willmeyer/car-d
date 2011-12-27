package com.willmeyer.card.ext.win32;

import java.io.File;

import org.slf4j.*;

public class Audio {
	
	protected static final Logger logger = LoggerFactory.getLogger(Audio.class);

	protected static final String soundDir = "sounds";
	
	private Audio(String audioResource) {
	}

	public static void play(String resource) throws Exception{
		if (!resource.startsWith("http://")) {
			String fileName = resource;
			File soundFile = new File(soundDir + "/" + fileName);
			if (!soundFile.exists()) {
				fileName = resource + ".mp3";
				soundFile = new File(soundDir + "/" + fileName);
				if (!soundFile.exists()) {
					fileName = resource + ".wav";
					soundFile = new File(soundDir + "/" + fileName);
					if (!soundFile.exists()) {
						throw new Exception("Could not find a sound file called \"" + resource + "\".");
					}
				}
			}
			assert soundFile.exists();
			if (!soundFile.canRead() || !soundFile.isFile()) {
				throw new Exception("The sound file \"" + soundFile.getAbsolutePath() + "\" is not accessible.");
			}
			resource = soundFile.getAbsolutePath();
		}
		try {
			logger.info("Audio", "Playing: \"" + resource + "\"");
			String cmd = "cmd /C ext\\win32\\playit\\PlayIt.cmd \"" + resource + "\"";
			Process proc = Runtime.getRuntime().exec(cmd);
			proc.waitFor();
			if (proc.exitValue() != 0) {
				throw new Exception ("PlayIt exitCode " + proc.exitValue());
			}
		} catch (Exception e) {
			String msg = "Unable to activate audio: " + e.getMessage();
			logger.error("Audio", msg);
			throw new Exception (msg);
		}
		
	}
}