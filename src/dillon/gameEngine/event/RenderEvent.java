package dillon.gameEngine.event;

import java.awt.Graphics2D;

/**
 * Fires to indicate that a rendering event should take place.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class RenderEvent extends EEvent {

	@Override
	public String getType() {
		return "Render";
	}

	private Graphics2D g;

	@Override
	public Object[] getMetadata() {
		return new Object[] { g };
	}

	/**
	 * Instantates the event.
	 * 
	 * @param g2
	 *            The graphics object
	 */
	public RenderEvent(Graphics2D g2) {
		g = g2;
	}

}
