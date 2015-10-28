package dillon.gameAPI.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

import dillon.gameAPI.core.Core;

/**
 * This is a replacement for the old gui system's blackout method using an
 * image.
 * 
 * @since 1.11
 * @author Dillon - Github dg092099
 *
 */
public class BlackoutImage implements GuiComponent {
	@Override
	public int getZIndex() {
		return -1;
	}

	private boolean closable;
	private Image img;

	public BlackoutImage(boolean cl, Image i) {
		closable = cl;
		img = i;
	}

	@Override
	public void bringToFront() {

	}

	@Override
	public void dropBehind() {

	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(img, 0, 0, null);
	}

	@Override
	public void onMouseClickRight(double x, double y) {

	}

	@Override
	public void onMouseClickLeft(double x, double y) {

	}

	@Override
	public void onKeyPress(KeyEvent evt) {
		int keyCode = evt.getKeyCode();
		if (closable) {
			if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_ESCAPE) {
				GuiSystem.removeGui(this);
			}
		}
	}

	@Override
	public void onUpdate() {

	}

	@Override
	public int[] getTopLeftCorner() {
		return new int[] { 0, 0 };
	}

	@Override
	public int[] getSize() {
		return new int[] { Core.getWidth(), Core.getHeight() };
	}

	@Override
	public void slide(int x, int y) {

	}

	@Override
	public String toString() {
		return "Closable: " + closable + "\nImage: " + img.toString();
	}
}
