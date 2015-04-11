package dillon.gameEngine.event;

import java.awt.event.KeyEvent;

/**
 * Event for when a key is pressed. Only metadata: The raw java.awt mouse event.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class KeyEngineEvent extends EEvent {
	@Override
	public String getType() {
		return "Key";
	}

	public Object[] metadata = new Object[2];

	@Override
	public Object[] getMetadata() {
		return metadata;
	}

	/**
	 * Instantates an key event.
	 * 
	 * @param evt
	 *            The key event.
	 */
	public KeyEngineEvent(KeyEvent evt, int mode) {
		metadata[0] = evt;
		metadata[1] = mode;
	}
	public static final int KEY_PRESS = 0;
	public static final int KEY_RELEASE = 1;
	public static final int KEY_TYPED = 2;
}
