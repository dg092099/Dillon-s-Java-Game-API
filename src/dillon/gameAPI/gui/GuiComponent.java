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
	public int getZIndex();

	public void bringToFront();

	public void dropBehind();

	public void render(Graphics2D g);

	public void onMouseClickRight(double x, double y);

	public void onMouseClickLeft(double x, double y);

	public void onKeyPress(KeyEvent evt);

	public void onUpdate();

	public int[] getTopLeftCorner();

	public int[] getSize();

	public void slide(int x, int y);
}
