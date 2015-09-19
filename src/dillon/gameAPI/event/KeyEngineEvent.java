package dillon.gameAPI.event;

import java.awt.event.KeyEvent;

/**
 * Event for when a key is pressed. Only metadata: The raw java.awt mouse event,
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

	public Object[] metadata = new Object[2]; // The raw key event and an int
												// representing what happened.

	@Override
	public Object[] getMetadata() {
		return metadata;
	}

	/**
	 * Instantates an key event.
	 * 
	 * @param evt
	 *            The key event.
	 * @param mode
	 *            The type of event.
	 */
	public KeyEngineEvent(KeyEvent evt, int mode) {
		metadata[0] = evt;
		metadata[1] = mode;
	}

	public static final int KEY_PRESS = 0; // Constant: key type, pressed.
	public static final int KEY_RELEASE = 1; // Constant: key type, released.
	public static final int KEY_TYPED = 2; // Constant: key type, typed.
}
