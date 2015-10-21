package dillon.gameAPI.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.PromptEvent;

/**
 * This class is a replacement for the old gui system's show prompt method.
 * 
 * @since 1.11
 * @author Dillon - Github dg092099
 *
 */
public class Prompt extends BasicDialog {
	private final long promptNumber;

	public Prompt(String prompt, Font f, Color bor, Color fore, Color txtColor, boolean alwaysAtFront, long pNum,
			Color resColor) {
		super(prompt, f, bor, fore, txtColor, alwaysAtFront);
		promptNumber = pNum;
		responseColor = resColor;
	}

	private String text = "";
	private Color responseColor;
	private boolean skip = true;

	@Override
	public void render(Graphics2D g) {
		try {
			super.render(g);
			if (skip) {
				skip = false;
				return;
			}
			fm = g.getFontMetrics(getFont());
			int textX = (Core.getWidth() / 2) - fm.stringWidth(text + "_") / 2;
			int lineSpace = (int) (fm.getHeight() * 0.7);
			int lines = super.prompt.split("\n").length + 1;
			int textY = super.innerP1Y;
			textY += lines * fm.getHeight();
			textY += lines * lineSpace;
			textY -= lineSpace;
			g.setFont(super.getFont());
			g.setColor(responseColor);
			g.drawString(text + "_", textX, textY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onKeyPress(KeyEvent evt) {
		int keyCode = evt.getKeyCode();
		if (keyCode == KeyEvent.VK_ENTER) {
			GuiSystem.removeGui(this);
			EventSystem.broadcastMessage(new PromptEvent(text, promptNumber), PromptEvent.class);
			return;
		}
		if (keyCode == KeyEvent.VK_BACK_SPACE && text.length() > 0) {
			text = text.substring(0, text.length() - 1);
			return;
		}
		if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CAPS_LOCK || keyCode == KeyEvent.VK_BACK_SPACE)
			return;
		text += evt.getKeyChar();
	}

}
