package dillon.gameAPI.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.security.SecurityKey;

/**
 * This is a replacement for the old gui system's blackout method, using text.
 *
 * @since 1.11
 * @author Dillon - Github dg092099
 *
 */
public class BlackoutText implements GuiComponent {
	@Override
	public int getZIndex() {
		return -1;
	}

	private boolean closable;
	private Color background;
	private Color foreground;
	private String text;
	private Font textFont;
	private int textX, textY;
	private SecurityKey key;

	public BlackoutText(boolean cl, Color back, Color fore, String txt, Font f, SecurityKey k) {
		// Initiates variables.
		closable = cl;
		key = k;
		background = back;
		foreground = fore;
		text = txt;
		textFont = f;

	}

	@Override
	public void bringToFront() {

	}

	@Override
	public void dropBehind() {

	}

	private boolean rendered = false;

	@Override
	public void render(Graphics2D g) {
		if (!rendered) {
			rendered = true;
			FontMetrics fm = g.getFontMetrics(textFont);
			// Get where to draw text.
			textX = Core.getWidth() / 2 - fm.stringWidth(text) / 2;
			textY = Core.getHeight() / 2 - fm.getHeight() / 2;
		}
		g.setFont(textFont);
		// Create background.
		g.setColor(background);
		g.fillRect(0, 0, Core.getWidth(), Core.getHeight());
		// Draw text
		g.setColor(foreground);
		g.drawString(text, textX, textY);
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
				GuiSystem.removeGui(this, key);
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

}
