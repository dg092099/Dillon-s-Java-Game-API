package dillon.gameAPI.mapping;

import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This object tells the Scroll manager where the camera is so it can render the
 * correct tiles. <b>We are using this one for the newest system.</b>
 *
 * @author Dillon - Github dg092099
 * @since V2.0
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
		xPos = x;
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
		yPos = y;
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

	public static String getDebug() {
		String data = "\n\ndillon.gameAPI.scroller.Camera Debug:\n";
		data += String.format("%-15s %-5s\n", "Key", "Value");
		data += String.format("%-15s %-5s\n", "---", "-----");
		data += String.format("%-15s %-5s\n", "Position X", xPos);
		data += String.format("%-15s %-5s\n", "Position Y", yPos);
		return data;
	}
}
