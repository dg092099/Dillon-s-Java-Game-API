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

	public Object[] metadata = new Object[1];

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
	public KeyEngineEvent(KeyEvent evt) {
		metadata[0] = evt;
	}
}
