package dillon.gameAPI.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.PromptEvent;
import dillon.gameAPI.security.SecurityKey;

/**
 * This class is a replacement for the old gui system's show prompt method.
 *
 * @since 1.11
 * @author Dillon - Github dg092099
 *
 */
public class Prompt extends BasicDialog {
	private final long promptNumber;
	private SecurityKey key;

	public Prompt(String prompt, Font f, Color bor, Color fore, Color txtColor, boolean alwaysAtFront, long pNum,
			Color resColor, SecurityKey k) {
		super(prompt, f, bor, fore, txtColor, alwaysAtFront, k);
		promptNumber = pNum;
		responseColor = resColor;
		key = k;
	}

	private String text = "";
	private Color responseColor;
	private boolean update = true;

	@Override
	public void render(Graphics2D g) {
		try {
			super.render(g);
			if (update) {
				update = false;
				calculateDimensions(g);
			}
			fm = g.getFontMetrics(getFont());
			// Figure out where to put the cursor
			int textX = Core.getWidth() / 2 - fm.stringWidth(text + "_") / 2;
			int lineSpace = (int) (fm.getHeight() * 0.7);
			int lines = super.prompt.split("\n").length + 1;
			int textY = super.innerP1Y;
			textY += lines * fm.getHeight();
			textY += lines * lineSpace;
			textY -= lineSpace;
			g.setFont(super.getFont());
			g.setColor(responseColor);
			// Draw the cursor
			g.drawString(text + "_", textX, textY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onKeyPress(KeyEvent evt) {
		int keyCode = evt.getKeyCode();
		if (keyCode == KeyEvent.VK_ENTER) {
			GuiSystem.removeGui(this, key);
			EventSystem.broadcastMessage(new PromptEvent(text, promptNumber), PromptEvent.class, key);
			return;
		}
		if (keyCode == KeyEvent.VK_ESCAPE) {
			GuiSystem.removeGui(this, key);
			EventSystem.broadcastMessage(new PromptEvent("", promptNumber), PromptEvent.class, key);
			return;
		}
		if (keyCode == KeyEvent.VK_CONTROL) {
			return;
		}
		if (keyCode == KeyEvent.VK_V && evt.isControlDown()) {
			try {
				String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null)
						.getTransferData(DataFlavor.stringFlavor);
				text += data;
				return;
			} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (keyCode == KeyEvent.VK_BACK_SPACE && text.length() > 0) {
			text = text.substring(0, text.length() - 1);
			update = true;
			return;
		}
		if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CAPS_LOCK || keyCode == KeyEvent.VK_BACK_SPACE) {
			return;
		}
		text += evt.getKeyChar();
		update = true;
	}

	@Override
	public void calculateDimensions(Graphics2D g) {
		// Get center of screen.
		int CenterX = Math.round(Core.getWidth() / 2);
		int CenterY = Math.round(Core.getHeight() / 2);
		g.setFont(textFont);
		FontMetrics fm = g.getFontMetrics(textFont);
		// Get longest line to find how far back the dialog should be.
		int longestLine = Integer.MIN_VALUE;
		String[] lines = prompt.split("\n");
		for (String s : lines) {
			int lineWidth = fm.stringWidth(s);
			if (lineWidth > longestLine) {
				longestLine = lineWidth;
			}
		}
		int lineWidth = fm.stringWidth(text + "_");
		if (lineWidth > longestLine) {
			longestLine = lineWidth;
		}
		// Gets the longest line metrically
		lineSpace = (int) (fm.getHeight() * 0.7);

		outterP1X = CenterX; // Outer box, upper left x
		outterP1X -= Math.round(longestLine / 2); // Offset
		outterP1X -= 20; // Padding
		outterP1X -= 10; // Border

		outterP2X = CenterX; // outer box, bottom right x
		outterP2X += Math.round(longestLine / 2); // Offset
		outterP2X += 20; // Padding
		outterP2X += 10; // Border

		innerP1X = CenterX; // Inner box, upper left x
		innerP1X -= Math.round(longestLine / 2); // Offset
		innerP1X -= 20; // Padding
		// No border padding

		innerP2X = CenterX; // Inner box, lower right x
		innerP2X += Math.round(longestLine / 2); // Offset
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
}
