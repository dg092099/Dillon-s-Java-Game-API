package dillon.gameAPI.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.KeyEngineEvent;
import dillon.gameAPI.event.PromptEvent;
import dillon.gameAPI.event.RenderEvent;

/**
 * Use the GuiSystem class instead.
 * 
 * @author Dillon - Github dg092099
 * @deprecated
 */
public class guiManager {
	private static boolean guiDisplaying = false; // If anything is on screen.
	private static boolean showDialog = false; // If a dialog is showing.
	private static String dialogText; // The dialog text.
	private static int dialogFontSize; // The dialog font size
	private static Color dialogBorder; // The color of the dialog border.
	private static boolean showingPrompt = false; // If a prompt is being shown
	private static String typedPrompt; // The data entered so far in a prompt.
	private static int blackoutActive = 0; // If a blackout is active.
	private static BufferedImage blackoutImage; // The blackout image.
	private static String blackoutText; // The blackout text.
	private static Font blackoutFont; // The blackout font.
	private static Color blackoutColor; // The blackout background color
	private static Image background; // The blackout background.

	/**
	 * Returns if the manager is displaying something.
	 * 
	 * @return if this is displaying something.
	 */
	public static boolean getDisplaying() {
		return guiDisplaying;
	}

	public guiManager() {
		EventSystem.addHandler(new EEHandler<KeyEngineEvent>() {
			@Override
			public void handle(KeyEngineEvent evt) {
				KeyEvent evt2 = evt.getKeyEvent();
				if (evt.getMode() != KeyEngineEvent.KeyMode.KEY_PRESS)
					return;
				if (evt2.getKeyCode() == KeyEvent.VK_ENTER && showDialog && !showingPrompt) {
					showDialog = false;
				} else if (showingPrompt) {
					if (evt2.getKeyCode() == KeyEvent.VK_ENTER) {
						PromptEvent evt3 = new PromptEvent(getPromptResponse(), lockerID);
						EventSystem.broadcastMessage(evt3, PromptEvent.class);
						showingPrompt = false;
						showDialog = false;
					} else if (evt2.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
						if (typedPrompt.length() < 1)
							return;
						typedPrompt = typedPrompt.substring(0, typedPrompt.length() - 1);
					} else if (evt2.getKeyCode() == KeyEvent.VK_SHIFT) {
						return;
					} else {
						if (evt.getMode() == KeyEngineEvent.KeyMode.KEY_PRESS)
							typedPrompt = typedPrompt + evt2.getKeyChar();
					}
				}
			}
		});
		EventSystem.addHandler(new EEHandler<RenderEvent>() {
			@Override
			public void handle(RenderEvent evt) {
				Graphics2D graphics = evt.getGraphics();
				if (blackoutActive == 1) {
					graphics.drawImage(blackoutImage, 0, 0, null);
					graphics.setFont(blackoutFont);
					FontMetrics metrics = graphics.getFontMetrics();
					graphics.setColor(blackoutColor);
					graphics.drawString(blackoutText,
							(blackoutImage.getWidth() / 2) - metrics.stringWidth(blackoutText) / 2,
							blackoutImage.getHeight() / 2);
					return;
				} else if (blackoutActive == 2) {
					graphics.drawImage(blackoutImage, 0, 0, null);
					return;
				}
				if (background != null) {
					graphics.drawImage(background, 0, 0, null);
				}

				if (showDialog) {
					Font f = new Font("Courier", Font.PLAIN, dialogFontSize);
					String[] lines = dialogText.split("\n");
					int longest = 0;
					int id = 0;
					for (int i = 0; i < lines.length; i++) {
						if (lines[i].length() > longest) {
							longest = lines[i].length();
							id = i;
						}
					}
					graphics.setFont(f);
					FontMetrics metrics = graphics.getFontMetrics(f);
					graphics.setColor(dialogBorder);
					int reqWidth, reqHeight;
					reqWidth = metrics.stringWidth(lines[id]) + 15;
					reqHeight = metrics.getHeight() * lines.length + 15; // 10
																			// border;
																			// 5
																			// padding
					Stroke brushSize = graphics.getStroke();
					graphics.setStroke(new BasicStroke(5));
					int mx = (Core.getWidth() / 2) - (reqWidth / 2);
					int my = (Core.getHeight() / 2) - (reqHeight / 2);
					graphics.drawRect(mx, my, reqWidth, reqHeight);
					graphics.setStroke(brushSize);
					graphics.setColor(Color.WHITE);
					graphics.fillRect(mx + 3, my + 3, reqWidth - 5, reqHeight - 5);
					graphics.setColor(Color.BLACK);
					for (int i = 0; i < lines.length; i++) {
						graphics.drawString(lines[i], (Core.getWidth() / 2) - (metrics.stringWidth(lines[i]) / 2),
								(Core.getHeight() / 2) - (metrics.getHeight() / 2) + (i * (dialogFontSize / 2 + 10))
										+ metrics.getHeight() / 2);
					}
					if (showingPrompt) {
						int drx = Core.getWidth() / 2;
						int dry = (Core.getHeight() / 2) - (metrics.getHeight() / 2)
								+ (lines.length * (dialogFontSize / 2 + 10)) + metrics.getHeight() / 2;
						graphics.drawString(typedPrompt + "_", drx, dry);
					}
				}
			}
		});
	}

	/**
	 * This blacks out the screen with many parameters. The text can be
	 * dismissed by using enter. The engine will not broadcast event ticks while
	 * this is active.
	 * 
	 * @param bck
	 *            The background color
	 * @param text
	 *            The text to display
	 * @param fontName
	 *            The name of the font to use.
	 * @param fontSize
	 *            The Size of the font to use.
	 * @param fontColor
	 *            The color of the text.
	 */
	public static void blackout(Color bck, String text, String fontName, int fontSize, Color fontColor) {
		clearBlackout();
		blackoutImage = new BufferedImage(Core.getWidth(), Core.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D gph = blackoutImage.createGraphics();
		blackoutColor = fontColor;
		blackoutFont = new Font(fontName, Font.BOLD, fontSize);
		blackoutText = text;
		gph.setColor(bck);
		gph.fillRect(0, 0, blackoutImage.getWidth(), blackoutImage.getHeight());
		blackoutActive = 1;
		System.out.println("Activated BlackoutText");
	}

	/**
	 * Tells if there is a blackout.
	 * 
	 * @return blackout
	 */
	public static boolean getOnBlackout() {
		return blackoutActive > 0;
	}

	/**
	 * Tells if there is a showing dialog.
	 * 
	 * @return if there is
	 */
	public static boolean getOnDialog() {
		return showDialog;
	}

	/**
	 * Gets response from the user.
	 * 
	 * @return The response.
	 */
	public static String getPromptResponse() {
		return typedPrompt;
	}

	/**
	 * This does the same as the other blackout function however, you can
	 * specify your own image.
	 * 
	 * @param img
	 *            The image to display.
	 */
	public static void blackout(Image img) {
		clearBlackout();
		blackoutImage = (BufferedImage) img;
		blackoutActive = 2;
	}

	/**
	 * This cancels the blackout.
	 */
	public static void clearBlackout() {
		blackoutActive = 0;
		blackoutColor = null;
		blackoutFont = null;
		blackoutImage = null;
		blackoutText = null;
	}

	/**
	 * This will display a dialog on the screen for the user.
	 * 
	 * @param text
	 *            The text to display.
	 * @param size
	 *            The Font size to use.
	 * @param border
	 *            The color of the border that should be used.
	 */
	public static void showDialog(String text, int size, Color border) {
		dialogBorder = border;
		dialogFontSize = size;
		dialogText = text;
		showDialog = true;
	}

	/**
	 * Sets a dialog to be displayed.
	 * 
	 * @param text
	 *            The prompt.
	 * @param size
	 *            The size of the border.
	 * @param border
	 *            The color of the border.
	 * @param LockId
	 *            The number to use when the event fires.
	 */
	public static void showPrompt(String text, int size, Color border, long LockId) {
		dialogBorder = border;
		dialogFontSize = size;
		dialogText = text;
		showDialog = true;
		showingPrompt = true;
		typedPrompt = "";
		lockerID = LockId;
	}

	private static long lockerID; // The locking object.

	/**
	 * This will say if the engine is yielding on a prompt.
	 * 
	 * @return Yielding
	 */
	public static boolean getShowingPrompt() {
		return showingPrompt;
	}
}
