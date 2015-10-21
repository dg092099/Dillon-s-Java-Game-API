package dillon.gameAPI.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import dillon.gameAPI.core.Core;

/**
 * This is a class that makes a dialog similar to the old gui system's show
 * dialog method.
 * 
 * @since 1.11
 * @author Dillon - Github dg092099
 *
 */
public class BasicDialog implements GuiComponent {
	int index = 0;

	@Override
	public int getZIndex() {
		return alwaysAtFront ? -1 : index;
	}

	public Font getFont() {
		return textFont;
	}

	@Override
	public void bringToFront() {
		index = 0;
	}

	@Override
	public void dropBehind() {
		index++;
	}

	protected String prompt;
	private Color borderColor;
	private Color foregroundColor;
	private Color textColor;
	private Font textFont;
	private boolean alwaysAtFront;

	public BasicDialog(String prompt, Font f, Color bor, Color fore, Color txtColor, boolean alwaysAtFront) {
		this.prompt = prompt;
		textFont = f;
		borderColor = bor;
		foregroundColor = fore;
		textColor = txtColor;
		this.alwaysAtFront = alwaysAtFront;
	}

	private int outterP1X, outterP1Y;
	private int outterP2X, outterP2Y;
	private int innerP1X;
	public int innerP1Y;
	private int innerP2X, innerP2Y;
	private int textX, textY;
	private int lineSpace;

	/**
	 * This method finds the dimensions for the two boxes.
	 * 
	 * @param g
	 *            Graphics
	 */
	public void calculateDimensions(Graphics2D g) {
		int CenterX = Math.round(Core.getWidth() / 2);
		int CenterY = Math.round(Core.getHeight() / 2);
		g.setFont(textFont);
		FontMetrics fm = g.getFontMetrics(textFont);
		int longestLine = Integer.MIN_VALUE;
		String[] lines = prompt.split("\n");
		for (String s : lines) {
			int lineWidth = fm.stringWidth(s);
			if (lineWidth > longestLine) {
				longestLine = lineWidth;
			}
		} // Gets the longest line metrically
		lineSpace = (int) (fm.getHeight() * 0.7);

		outterP1X = CenterX; // Outer box, upper left x
		outterP1X -= Math.round(longestLine / 2);
		outterP1X -= 20; // Padding
		outterP1X -= 10; // Border

		outterP2X = CenterX; // outer box, bottom right x
		outterP2X += Math.round(longestLine / 2);
		outterP2X += 20; // Padding
		outterP2X += 10; // Border

		innerP1X = CenterX; // Inner box, upper left x
		innerP1X -= Math.round(longestLine / 2);
		innerP1X -= 20; // Padding
		// No border padding

		innerP2X = CenterX; // Inner box, lower right x
		innerP2X += Math.round(longestLine / 2);
		innerP2X += 20; // Padding

		// Y values:

		int linesHeight = (fm.getHeight() + lineSpace) * lines.length;
		outterP1Y = CenterY;
		outterP1Y -= linesHeight / 2; // Moves up above half way from lines.
		outterP1Y -= 20; // Padding
		outterP1Y -= 10; // Border

		outterP2Y = CenterY;
		outterP2Y += linesHeight / 2; // Moves down halfway from lines.
		outterP2Y += 20; // Padding
		outterP2Y += 10; // Border

		innerP1Y = CenterY;
		innerP1Y -= linesHeight / 2;
		innerP1Y -= 20; // Padding

		innerP2Y = CenterY;
		innerP2Y += linesHeight / 2;
		innerP2Y += 20; // Padding

		textX = innerP1X + 20;
		textY = innerP1Y + 20;
	}

	private boolean calculated = false;
	protected FontMetrics fm;

	@Override
	public void render(Graphics2D g) {
		if (!calculated) {
			calculateDimensions(g);
			calculated = true;
		}
		g.setColor(borderColor);
		g.fillRect(outterP1X, outterP1Y, outterP2X - outterP1X, outterP2Y - outterP1Y);
		g.setColor(foregroundColor);
		g.fillRect(innerP1X, innerP1Y, innerP2X - innerP1X, innerP2Y - innerP1Y);
		g.setColor(textColor);
		g.setFont(textFont);
		int num = 1;
		for (String s : prompt.split("\n")) {
			g.drawString(s, textX, textY + (lineSpace * num));
			num++;
		}
	}

	@Override
	public void onMouseClickRight(double x, double y) {
	}

	@Override
	public void onMouseClickLeft(double x, double y) {
	}

	@Override
	public void onKeyPress(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
			GuiSystem.removeGui(this);
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
