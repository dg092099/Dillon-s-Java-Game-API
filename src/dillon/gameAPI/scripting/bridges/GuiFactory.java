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

	/**
	 * Build a basic dialog.
	 * 
	 * @param prompt
	 *            Prompt
	 * @param fontName
	 *            The font.
	 * @param bold
	 *            If it should be bolded.
	 * @param fontSize
	 *            The font size.
	 * @param borderR
	 *            Border red
	 * @param borderG
	 *            Border green
	 * @param borderB
	 *            Border Blue
	 * @param foreR
	 *            Foreground Red
	 * @param foreG
	 *            Foreground green
	 * @param foreB
	 *            Foreground blue
	 * @param txtR
	 *            Text red
	 * @param txtG
	 *            Text green
	 * @param txtB
	 *            Text blue
	 * @param atFront
	 *            If at front.
	 * @param key
	 *            The security key
	 * @return The Basic Dialog.
	 */
	public BasicDialog basicDialog(String prompt, String fontName, boolean bold, int fontSize, int borderR, int borderG,
			int borderB, int foreR, int foreG, int foreB, int txtR, int txtG, int txtB, boolean atFront,
			SecurityKey key) {
		return new BasicDialog(prompt, new Font(fontName, bold ? Font.BOLD : Font.PLAIN, fontSize),
				new Color(borderR, borderG, borderB), new Color(foreR, foreG, foreB), new Color(txtR, txtG, txtB),
				atFront, key);
	}

	/**
	 * Builds a blackout text object.
	 * 
	 * @param closable
	 *            If it should be closable
	 * @param rBack
	 *            Background red
	 * @param gBack
	 *            Background green
	 * @param bBack
	 *            Background blue
	 * @param rFore
	 *            Foreground red
	 * @param gFore
	 *            Foreground green
	 * @param bFore
	 *            Foreground blue
	 * @param text
	 *            Text
	 * @param fontName
	 *            The font.
	 * @param size
	 *            The size of the text.
	 * @param bold
	 *            If it should be bold.
	 * @param key
	 *            The security key.
	 * @return The Blackout text object.
	 */
	public BlackoutText blackoutText(boolean closable, int rBack, int gBack, int bBack, int rFore, int gFore, int bFore,
			String text, String fontName, int size, boolean bold, SecurityKey key) {
		return new BlackoutText(closable, new Color(rBack, gBack, bBack), new Color(rFore, gFore, bFore), text,
				new Font(fontName, bold ? Font.BOLD : Font.PLAIN, size), key);
	}

	/**
	 * Returns the GUI System.
	 * 
	 * @return GUI System.
	 */
	public GuiSystem System() {
		return Core.getGuiSystem();
	}

	/**
	 * Builds a prompt.
	 * 
	 * @param prompt
	 *            The prompt
	 * @param fontName
	 *            The font.
	 * @param bold
	 *            If the text should be bold.
	 * @param fontSize
	 *            The font size.
	 * @param borderR
	 *            Border Red
	 * @param borderG
	 *            Border green
	 * @param borderB
	 *            Border blue
	 * @param foreR
	 *            Foreground red
	 * @param foreG
	 *            Foreground green
	 * @param foreB
	 *            Foreground blue
	 * @param txtR
	 *            Text red
	 * @param txtG
	 *            Text green
	 * @param txtB
	 *            Text blue
	 * @param resR
	 *            Response red
	 * @param resG
	 *            Response green
	 * @param resB
	 *            Response blue
	 * @param key
	 *            Security key
	 * @param functionName
	 *            Function to execute when entered.
	 * @param alwaysOnTop
	 *            If the prompt should be on top.
	 * @return The prompt object.
	 */
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

			@Override
			public int getPriority() {
				return 0;
			}
		}, facKey);
		return p;
	}
}
