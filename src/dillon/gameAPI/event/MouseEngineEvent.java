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

	private Object[] metadata = new Object[4];

	@Override
	public Object[] getMetadata() {
		return metadata;
	}

	public static final int MOUSE_CLICK = 0;
	public static final int MOUSE_RELEASE = 1;
	public static final int MOUSE_HOLD = 2;
	public static final int MOUSE_ENTER = 3;
	public static final int MOUSE_LEAVE = 4;

	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int MIDDLE = 2;
	public static final int SCROLL = 3;

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
	public MouseEngineEvent(int button, int mode, int x, int y, String scrollAmt) {
		Point p = new Point(x, y);
		metadata[0] = mode;
		metadata[1] = button;
		metadata[2] = p;
		if (scrollAmt != null)
			metadata[3] = scrollAmt;
	}
}
