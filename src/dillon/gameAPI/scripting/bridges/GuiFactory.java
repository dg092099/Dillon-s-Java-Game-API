package dillon.gameAPI.scripting.bridges;

import java.awt.Color;
import java.awt.Font;
import java.security.SecureRandom;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.PromptEvent;
import dillon.gameAPI.gui.BasicDialog;
import dillon.gameAPI.gui.BlackoutText;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.gui.Prompt;
import dillon.gameAPI.scripting.ScriptSystem;
import dillon.gameAPI.security.SecurityKey;

/**
 * This is a class implementing a controller for making a GUI. This is used only
 * in scripting, it should be invoked as GuiFactory. Ex.
 * GuiFactory.basicDialog(...);
 *
 * @author Dillon - Github dg092099
 *
 */
public class GuiFactory {
	private SecurityKey facKey;

	public GuiFactory(SecurityKey key) {
		facKey = key;
	}

	public BasicDialog basicDialog(String prompt, String fontName, boolean bold, int fontSize, int borderR, int borderG,
			int borderB, int foreR, int foreG, int foreB, int txtR, int txtG, int txtB, boolean atFront,
			SecurityKey key) {
		return new BasicDialog(prompt, new Font(fontName, bold ? Font.BOLD : Font.PLAIN, fontSize),
				new Color(borderR, borderG, borderB), new Color(foreR, foreG, foreB), new Color(txtR, txtG, txtB),
				atFront, key);
	}

	public BlackoutText blackoutText(boolean closable, int rBack, int gBack, int bBack, int rFore, int gFore, int bFore,
			String text, String fontName, int size, boolean bold, SecurityKey key) {
		return new BlackoutText(closable, new Color(rBack, gBack, bBack), new Color(rFore, gFore, bFore), text,
				new Font(fontName, bold ? Font.BOLD : Font.PLAIN, size), key);
	}

	public GuiSystem System() {
		return Core.getGuiSystem();
	}

	public Prompt prompt(String prompt, String fontName, boolean bold, int fontSize, int borderR, int borderG,
			int borderB, int foreR, int foreG, int foreB, int txtR, int txtG, int txtB, int resR, int resG, int resB,
			SecurityKey key, final String functionName, boolean alwaysOnTop) {
		final long id = new SecureRandom().nextLong();
		Prompt p = new Prompt(prompt, new Font(fontName, bold ? Font.BOLD : Font.PLAIN, fontSize),
				new Color(borderR, borderG, borderB), new Color(foreR, foreG, foreB), new Color(txtR, txtG, txtB),
				alwaysOnTop, id, new Color(resR, resG, resB), key);
		EventSystem.addHandler(new EEHandler<PromptEvent>() {
			@Override
			public void handle(PromptEvent evt) {
				if (evt.getId() == id) {
					ScriptSystem.invokeFunction(functionName, evt.getMsg());
					EventSystem.removeHandler(this);
				}
			}
		}, facKey);
		return p;
	}
}
