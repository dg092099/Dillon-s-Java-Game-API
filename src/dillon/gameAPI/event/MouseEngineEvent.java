package dillon.gameAPI.event;

import java.awt.Point;

/**
 * Fires when a mouse event occurs. Metadata: mode, button, and point
 * 
 * @author Dillon - Github dg092099
 *
 */
public class MouseEngineEvent extends EEvent {

	@Override
	public String getType() {
		return "Mouse";
	}

	private final MouseButton mouseButton;
	private final MouseMode mouseMode;
	private final Point location;
	private final int scrollAmount;

	/**
	 * @return the mouseButton
	 */
	public MouseButton getMouseButton() {
		return mouseButton;
	}

	/**
	 * @return the mouseMode
	 */
	public MouseMode getMouseMode() {
		return mouseMode;
	}

	/**
	 * @return the location
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * @return the scrollAmount
	 */
	public int getScrollAmount() {
		return scrollAmount;
	}

	/**
	 * Instantates a mouse event.
	 * 
	 * @param button
	 *            The mouse button used.
	 * @param mode
	 *            What it's doing.
	 * @param x
	 *            X coord of mouse.
	 * @param y
	 *            Y coord. of mouse.
	 * @param scrollAmt
	 *            unused.
	 */
	public MouseEngineEvent(MouseButton button, MouseMode mode, int x, int y, int scrollAmt) {
		mouseButton = button;
		mouseMode = mode;
		location = new Point(x, y);
		this.scrollAmount = scrollAmt;
	}

	public static enum MouseButton {
		LEFT, RIGHT, MIDDLE, SCROLL
	}

	public static enum MouseMode {
		CLICK, RELEASE, HOLD, ENTER, LEAVE
	}
}
