package com.willmeyer.card.leds;

import com.willmeyer.jfusionbrain.*;

public final class LedFeederBacker {
	
	public static final int NUM_LEDS = 10;
	
	protected FusionBrainV3 brain;
	protected int deviceIndex;
	protected boolean[] currentStates = new boolean[NUM_LEDS];
	
	protected static LedFeederBacker theInstance = null;
	
	public LedFeederBacker(FusionBrainV3 fbrain) {
		assert (theInstance == null);
		assert (fbrain != null);
		brain = fbrain;
		theInstance = this;
	}

	public void setAllTo(boolean on) throws Exception {
		boolean[] states = new boolean[NUM_LEDS];
		for (int i = 0; i < NUM_LEDS; i++) states[i] = on;
		this.setLedValues(states);
	}
	
	public void setAll(boolean[] states) throws Exception {
		this.setLedValues(states);
	}
	
	private void setLedValues(boolean[] states) throws Exception {
		brain.setAndGet(states);
		System.arraycopy(states, 0, this.currentStates, 0, NUM_LEDS);
	}
	
}
