package dillon.gameAPI.event;

import java.awt.event.KeyEvent;

/**
 * Event for when a key is pressed. Only metadata: The raw java.awt key event,
 * and a mode.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class KeyEngineEvent extends EEvent {
	@Override
	public String getType() {
		return "Key";
	}

	private final KeyEvent evt;
	private final KeyMode mode;

	public KeyEvent getKeyEvent() {
		return evt;
	}

	public static enum KeyMode {
		KEY_PRESS, KEY_RELEASE, KEY_TYPED
	}

	public KeyMode getMode() {
		return mode;
	}

	/**
	 * Instantates an key event.
	 * 
	 * @param evt
	 *            The key event.
	 * @param mode
	 *            The type of event.
	 */
	public KeyEngineEvent(KeyEvent evt, KeyMode mode) {
		this.evt = evt;
		this.mode = mode;
	}
}
