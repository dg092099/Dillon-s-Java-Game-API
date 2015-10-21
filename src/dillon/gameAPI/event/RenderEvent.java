package dillon.gameAPI.event;

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

	private final Graphics2D graphics;

	/**
	 * @return the graphics
	 */
	public Graphics2D getGraphics() {
		return graphics;
	}

	/**
	 * Instantates the event.
	 * 
	 * @param g2
	 *            The graphics object
	 */
	public RenderEvent(Graphics2D g2) {
		graphics = g2;
	}

}
