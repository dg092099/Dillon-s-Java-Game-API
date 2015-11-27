package dillon.gameAPI.scroller;

import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This object tells the Scroll manager where the camera is so it can render the
 * correct tiles.
 *
 * @author Dillon - Github dg092099
 */
public class Camera {
	private static int xPos = 5; // The x position of the camera.
	private static int yPos = 5; // The y position of the camera.

	/**
	 * This method sets the x value for the camera.
	 *
	 * @param x
	 *            The new x value.
	 * @param k
	 *            The security key.
	 */
	public static void setX(int x, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.MOVE_CAMERA);
		try {
			if (x > 1 && x < ScrollManager.getFullLayoutDims().getWidth())
				xPos = x;
		} catch (Exception e) {
			xPos = 5;
		}
	}

	/**
	 * Sets the new Y position for the camera.
	 *
	 * @param y
	 *            The new y value.
	 * @param k
	 *            The security key.
	 */
	public static void setY(int y, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.MOVE_CAMERA);
		try {
			if (y > 1 && y < ScrollManager.getFullLayoutDims().getHeight())
				yPos = y;
		} catch (Exception e) {
			yPos = 5;
		}
	}

	/**
	 * This retrieves the x position of the camera.
	 *
	 * @return x position
	 */
	public static int getXPos() {
		return xPos;
	}

	/**
	 * This returns the y position of the camera.
	 *
	 * @return y position.
	 */
	public static int getYPos() {
		return yPos;
	}
}
