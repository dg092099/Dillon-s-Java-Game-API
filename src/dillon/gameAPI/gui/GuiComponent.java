package dillon.gameAPI.gui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

/**
 * An interface that all gui components need to follow.
 *
 * @since 1.11
 * @author Dillon - Github dg092099
 *
 */
public interface GuiComponent {
	/**
	 * Get the order in which the gui should be rendered.
	 * 
	 * @return Z index.
	 */
	public int getZIndex();

	/**
	 * Utility function to bring the gui to the front.
	 */
	public void bringToFront();

	/**
	 * Utility function to increment z index.
	 */
	public void dropBehind();

	/**
	 * Causes the GUI to render.
	 * 
	 * @param g
	 *            The graphics to use.
	 */
	public void render(Graphics2D g);

	/**
	 * A function to execute when the mouse is right clicked on the GUI
	 * 
	 * @param x
	 *            The x position
	 * @param y
	 *            The y position
	 */
	public void onMouseClickRight(double x, double y);

	/**
	 * A function to execute when the mouse is left clicked on the GUI.
	 * 
	 * @param x
	 *            The x position
	 * @param y
	 *            The y position
	 */
	public void onMouseClickLeft(double x, double y);

	/**
	 * A function to execute when a key is pressed.
	 * 
	 * @param evt
	 *            The key event.
	 */
	public void onKeyPress(KeyEvent evt);

	/**
	 * A function to execute to update the GUI.
	 */
	public void onUpdate();

	/**
	 * A function to get the top left corner of the GUI.
	 * 
	 * @return {x, y}
	 */
	public int[] getTopLeftCorner();

	/**
	 * A fucntion to get the size of the GUI.
	 * 
	 * @return {w, h}
	 */
	public int[] getSize();

	/**
	 * A function when the GUI is slid to another location.
	 * 
	 * @param x
	 *            The new x position
	 * @param y
	 *            The new y position.
	 */
	public void slide(int x, int y);

	/**
	 * Debug function
	 * 
	 * @return Formatted GUI parameters.
	 */
	public String getDebug();
}
