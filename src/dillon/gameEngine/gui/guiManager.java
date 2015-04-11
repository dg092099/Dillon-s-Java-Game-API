package dillon.gameEngine.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import dillon.gameEngine.core.Core;
import dillon.gameEngine.event.EEHandler;
import dillon.gameEngine.event.EEvent;
import dillon.gameEngine.event.EventSystem;
import dillon.gameEngine.event.KeyEngineEvent;
import dillon.gameEngine.event.RenderEvent;
import dillon.gameEngine.utils.ThreadLocker;

/**
 * This class is designed to show all of the gui set to display.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class guiManager {
	private static boolean guiDisplaying = false;
	private static boolean showDialog = false;
	private static String dialogText;
	private static int dialogFontSize;
	private static Color dialogBorder;
	private static boolean showingPrompt = false;
	private static String typedPrompt;
	private static int blackoutActive = 0;
	private static BufferedImage blackoutImage;
	private static String blackoutText;
	private static Font blackoutFont;
	private static Color blackoutColor;
	private static Image background;
	/**
	 * Returns if the manager is displaying something.
	 * @return if this is displaying something.
	 */
	public static boolean getDisplaying() {
		return guiDisplaying;
	}

	public guiManager() {
		EventSystem.addHandler(new EEHandler<KeyEngineEvent>() {
			@Override
			public void handle(EEvent T) {
				if (T instanceof KeyEngineEvent) {
					KeyEngineEvent evt = (KeyEngineEvent) T;
					KeyEvent evt2 = (KeyEvent) evt.getMetadata()[0];
					if (evt2.getKeyCode() == KeyEvent.VK_ENTER && showDialog
							&& !showingPrompt) {
						showDialog = false;
					} else if (showDialog) {
						if (evt2.getKeyCode() == KeyEvent.VK_ENTER) {
							releaseLocker();
							showingPrompt = false;
							showDialog = false;
						} else if (evt2.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
							if (typedPrompt.length() < 1)
								return;
							typedPrompt = typedPrompt.substring(0,
									typedPrompt.length() - 1);
						} else if (evt2.getKeyCode() == KeyEvent.VK_SHIFT) {
							return;
						} else {
							if((int) evt.getMetadata()[1] == KeyEngineEvent.KEY_TYPED)
								typedPrompt = typedPrompt + evt2.getKeyChar();
						}
					}
				}
			}
		});
		EventSystem.addHandler(new EEHandler<RenderEvent>() {
			@Override
			public void handle(EEvent T) {
				if (T instanceof RenderEvent) {
					Graphics2D graphics = (Graphics2D) (((RenderEvent) T).getMetadata()[0]);
					if (blackoutActive == 1) {
						graphics.drawImage(blackoutImage, 0, 0, null);
						graphics.setFont(blackoutFont);
						FontMetrics metrics = graphics.getFontMetrics();
						graphics.setColor(blackoutColor);
						graphics.drawString(
								blackoutText,
								(blackoutImage.getWidth() / 2)
										- metrics.stringWidth(blackoutText) / 2,
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
						reqHeight = metrics.getHeight() * lines.length + 15; // 10 border; 5
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
							graphics.drawString(lines[i],
									(Core.getWidth() / 2)
											- (metrics.stringWidth(lines[i]) / 2),
									(Core.getHeight() / 2) - (metrics.getHeight() / 2)
											+ (i * (dialogFontSize / 2 + 10)));
						}
						if (showingPrompt) {
							int drx = Core.getWidth() / 2;
							int dry = (Core.getHeight() / 2) - (metrics.getHeight() / 2)
									+ (lines.length * (dialogFontSize / 2 + 10));
							graphics.drawString(typedPrompt + "_", drx, dry);
						}
					}
				}
			}
		});
	}

	public void releaseLocker() {
		locker.unlock();
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
	public static void blackout(Color bck, String text, String fontName,
			int fontSize, Color fontColor) {
		clearBlackout();
		blackoutImage = new BufferedImage(Core.getWidth(), Core.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gph = blackoutImage.createGraphics();
		blackoutColor = fontColor;
		blackoutFont = new Font(fontName, Font.BOLD, fontSize);
		blackoutText = text;
		gph.setColor(bck);
		gph.fillRect(0, 0, blackoutImage.getWidth(), blackoutImage.getHeight());
		blackoutActive = 1;
		System.out.println("Activated Blackout");
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
	 */
	public static void showPrompt(String text, int size, Color border,
			ThreadLocker l) {
		dialogBorder = border;
		dialogFontSize = size;
		dialogText = text;
		showDialog = true;
		showingPrompt = true;
		typedPrompt = "";
		locker = l;
	}

	private static ThreadLocker locker;

	/**
	 * This will say if the engine is yielding on a prompt.
	 * 
	 * @return Yielding
	 */
	public static boolean getShowingPrompt() {
		return showingPrompt;
	}
}
