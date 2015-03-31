package dillon.gameEngine.core;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import dillon.gameEngine.event.EventSystem;
import dillon.gameEngine.event.KeyEngineEvent;
import dillon.gameEngine.event.MouseEngineEvent;
import dillon.gameEngine.event.RenderEvent;
import dillon.gameEngine.event.TickEvent;
import dillon.gameEngine.networking.NetworkServer;
import dillon.gameEngine.utils.MainUtilities;
import dillon.gameEngine.utils.ThreadLocker;

/**
 * This class is in control of the game's canvas.
 * 
 * @author Dillon - Github dg092099
 */
public class CanvasController extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public CanvasController() {
		this.setSize(new Dimension(Core.getWidth(), Core.getHeight()));
		this.setBackground(Color.BLACK);
	}

	private long startTime, endTime;
	private static int FPS = 30;
	private boolean running = false;

	/**
	 * Preps the loop.
	 */
	public synchronized void start() {
		running = true;
	}

	/**
	 * Terminates the loop
	 */
	public synchronized void stop() {
		running = false;
	}

	/**
	 * This adjusts the current fps limit on the game.
	 * 
	 * @param newFps
	 *            The new fps to set it to.
	 */
	public synchronized void setFps(int newFps) {
		FPS = newFps;
	}

	/**
	 * This returns the current fps limit.
	 * 
	 * @return current fps limit
	 */
	public int getFps() {
		return FPS;
	}

	/**
	 * Gives back the canvas to draw on.
	 * 
	 * @return The canvas
	 */
	public Graphics2D getDrawingCanvas() {
		return graphics;
	}

	private boolean showingSplash = true;

	@Override
	public void run() {
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				MouseEngineEvent e = new MouseEngineEvent(evt.getButton(),
						MouseEngineEvent.MOUSE_CLICK, evt.getX(), evt.getY(),
						null);
				EventSystem.broadcastMessage(e);
			}

			@Override
			public void mouseEntered(MouseEvent evt) {
				MouseEngineEvent e = new MouseEngineEvent(evt.getButton(),
						MouseEngineEvent.MOUSE_ENTER, evt.getX(), evt.getY(),
						null);
				EventSystem.broadcastMessage(e);
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				MouseEngineEvent e = new MouseEngineEvent(evt.getButton(),
						MouseEngineEvent.MOUSE_LEAVE, evt.getX(), evt.getY(),
						null);
				EventSystem.broadcastMessage(e);
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				MouseEngineEvent e = new MouseEngineEvent(evt.getButton(),
						MouseEngineEvent.MOUSE_HOLD, evt.getX(), evt.getY(),
						null);
				EventSystem.broadcastMessage(e);
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				MouseEngineEvent e = new MouseEngineEvent(evt.getButton(),
						MouseEngineEvent.MOUSE_RELEASE, evt.getX(), evt.getY(),
						null);
				EventSystem.broadcastMessage(e);
			}
		});
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (arg0.isShiftDown()) {
						Core.shutdown(true);
					} else {
						EventSystem.broadcastMessage(new KeyEngineEvent(arg0));
					}
				} else if (arg0.getKeyCode() == KeyEvent.VK_ENTER && showDialog
						&& !showingPrompt) {
					showDialog = false;
					EventSystem.broadcastMessage(new KeyEngineEvent(arg0));
				} else if (showingPrompt) {
					if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
						releaseLocker();
						showingPrompt = false;
						showDialog = false;
					} else if (arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
						if (typedPrompt.length() < 1)
							return;
						typedPrompt = typedPrompt.substring(0,
								typedPrompt.length() - 1);
					} else if (arg0.getKeyCode() == KeyEvent.VK_SHIFT) {
						return;
					} else {
						typedPrompt = typedPrompt + arg0.getKeyChar();
					}
				} else {
					if (showDialog)
						return;
					EventSystem.broadcastMessage(new KeyEngineEvent(arg0));
				}
			}

			private void releaseLocker() {
				locker.unlock();
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0));
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0));
			}
		});
		this.requestFocus();
		while (running) {
			int framesInSecond = 1000 / FPS;
			startTime = System.currentTimeMillis();
			sendTick();
			sendRender();
			endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			long delta = framesInSecond - diff;
			if (delta < -50) {
				Logger.getLogger("Core").warning(
						"The game is behind by " + Math.abs(delta) + " ticks.");
			}
			try {
				Thread.sleep(delta);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * This ticks everything that happens.
	 */
	public void sendTick() {
		if (!showDialog) {
			EventSystem.broadcastMessage(new TickEvent());
			MainUtilities.executeQueue();
		}
	}

	Graphics2D graphics;
	private static ThreadLocker locker;

	/**
	 * This function renders everything.
	 */
	public void sendRender() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}
		graphics = (Graphics2D) getBufferStrategy().getDrawGraphics();
		graphics.setColor(graphics.getBackground());
		graphics.fillRect(0, 0, Core.getWidth(), Core.getHeight());
		// Start Draw
		if (blackoutActive == 1) {
			graphics.drawImage(blackoutImage, 0, 0, null);
			graphics.setFont(blackoutFont);
			FontMetrics metrics = graphics.getFontMetrics();
			graphics.setColor(blackoutColor);
			graphics.drawString(blackoutText, (blackoutImage.getWidth() / 2)
					- metrics.stringWidth(blackoutText) / 2,
					blackoutImage.getHeight() / 2);
			getBufferStrategy().show();
			graphics.dispose();
			return;
		} else if (blackoutActive == 2) {
			graphics.drawImage(blackoutImage, 0, 0, null);
			getBufferStrategy().show();
			graphics.dispose();
			return;
		}
		if (background != null) {
			graphics.drawImage(background, 0, 0, null);
		}
		EventSystem.broadcastMessage(new RenderEvent(graphics));
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
		if (showingSplash) {
			splashCounter++;
			if (splashCounter >= FPS * 2)
				showingSplash = false;
			try {
				if (Splash == null) {
					Splash = ImageIO.read(getClass().getResourceAsStream(
							"splash.png"));
				}
				graphics.drawImage(Splash, Core.getWidth() - 110,
						Core.getHeight() - 80, null);
			} catch (Exception e) {
			}
		}
		if (NetworkServer.getServerRunning()) {
			try {
				graphics.drawImage(
						ImageIO.read(getClass().getResourceAsStream(
								"ServerImage.png")), Core.getWidth() - 30, 5,
						null);
			} catch (Exception e) {
				e.printStackTrace();
				Core.crash(e);
			}
		}
		// End draw
		getBufferStrategy().show();
		graphics.dispose();
	}

	private static String typedPrompt;
	private int splashCounter = 0;
	private BufferedImage Splash;

	private static int blackoutActive = 0;
	private static BufferedImage blackoutImage;
	private static String blackoutText;
	private static Font blackoutFont;
	private static Color blackoutColor;
	private static Image background;

	/**
	 * This sets the new background image.
	 * 
	 * @param img
	 *            The new image to put in the background.
	 */
	public static void setBackgroundImage(Image img) {
		background = img;
	}

	/**
	 * Gets the current background image.
	 * 
	 * @return The background image.
	 */
	public static Image getBackgroundImage() {
		return background;
	}

	/**
	 * This method crashes the game and produces a stacktrace onto the screen.
	 * 
	 * @param e
	 *            This is the exception that will be displayed.
	 */
	public void crash(Exception e) {
		Logger.getLogger("Core").severe("Crashing...");
		Font f = new Font("Courier", Font.BOLD, 18);
		this.setFont(f);
		this.setBackground(Color.WHITE);
		setBackgroundImage(null);
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, Core.getWidth(), Core.getHeight());
		graphics.setColor(Color.RED);
		this.getGraphics().drawString("An error has occured.", 15, 15);
		this.getGraphics().drawString(e.getMessage(), 15, 30);
		StackTraceElement[] lines = e.getStackTrace();
		String formatted;
		for (int i = 0; i < lines.length; i++) {
			formatted = lines[i].getClassName() + "#"
					+ lines[i].getMethodName() + " Line: "
					+ lines[i].getLineNumber();
			this.getGraphics().drawString(formatted, 15, i * 15 + 45);
		}
		stop();
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

	private static boolean showDialog = false;
	private static String dialogText;
	private static int dialogFontSize;
	private static Color dialogBorder;

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

	private static boolean showingPrompt = false;

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

	/**
	 * This will say if the engine is yielding on a prompt.
	 * 
	 * @return Yielding
	 */
	public static boolean getShowingPrompt() {
		return showingPrompt;
	}

	private static int renderMethod = 0;
	public static final int TILES = 1;
	public static final int SIDESCROLLER = 2;

	/**
	 * This returns the games rendering method.
	 * 
	 * @return The rendering method.
	 */
	public static int getRenderMethod() {
		return renderMethod;
	}

	/**
	 * This sets the game's rendering method.
	 * 
	 * @param i
	 *            The rendering method
	 */
	public static void setRenderMethod(int i) {
		renderMethod = i;
	}

	public static int getFPS() {
		return FPS;
	}
}
