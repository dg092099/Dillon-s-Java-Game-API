package dillon.gameAPI.core;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.KeyEngineEvent;
import dillon.gameAPI.event.MouseEngineEvent;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.event.TickEvent;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.mapping.MapManager;
import dillon.gameAPI.networking.NetworkConnection;
import dillon.gameAPI.networking.NetworkServer;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;
import dillon.gameAPI.utils.MainUtilities;

/**
 * This class is in control of the JFrame's canvas.
 *
 * @author Dillon - Github dg092099
 */
class CanvasController extends Canvas implements Runnable {
	private static final long serialVersionUID = -3207927320425492600L;
	private static SecurityKey key;

	public CanvasController(SecurityKey k) {
		// Initially sets up the canvas and loads security key.
		this.setSize(new Dimension(Core.getWidth(), Core.getHeight()));
		this.setBackground(Color.BLACK);
		key = k;
	}

	private long startTime, endTime; // Time when loop starts, ends.
	private static int FPS = 30; // The target fps, defaults to 30.
	private boolean running = false; // Tells if the loop should keep running.
	private volatile boolean paused = false; // Tells if the update method
												// should occur.

	/**
	 * Starts the game loop.
	 */
	public synchronized void start() {
		running = true;
	}

	/**
	 * Terminates the game loop.
	 */
	public synchronized void stop() {
		running = false;
	}

	/**
	 * This adjusts the current FPS limit on the game.
	 *
	 * @param newFps
	 *            The new FPS to set it to.
	 */
	public synchronized void setFps(final int newFps) {
		if (newFps <= 0) {
			throw new IllegalArgumentException("The FPS must be more than 0.");
		}
		FPS = newFps;
	}

	/**
	 * This returns the current FPS limit.
	 *
	 * @return current FPS limit
	 */
	public int getFps() {
		return FPS;
	}

	/**
	 * Gives back the canvas to draw on. This should not be used outside of this
	 * package.
	 *
	 * @return The canvas
	 */
	public Graphics2D getDrawingCanvas() {
		return graphics;
	}

	private boolean showingSplash = true; // Determines if a splash screen
											// should still be displayed.

	@Override
	public void run() {
		this.addMouseListener(new MouseListener() { // Mouse listener
			@Override
			public void mouseClicked(final MouseEvent evt) {
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1: // Left mouse
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.CLICK, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2: // Middle button
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.CLICK, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3: // Right mouse
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.CLICK, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mouseEntered(final MouseEvent evt) {
				// When a mouse enters the window.
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.ENTER, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.ENTER, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.ENTER, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mouseExited(final MouseEvent evt) {
				// When the mouse leaves the window.
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.LEAVE, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.LEAVE, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.LEAVE, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mousePressed(final MouseEvent evt) {
				// When someone holds the mouse
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
							MouseEngineEvent.MouseMode.HOLD, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
							MouseEngineEvent.MouseMode.HOLD, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
							MouseEngineEvent.MouseMode.HOLD, evt.getX(), evt.getY(), 0), MouseEngineEvent.class, key);
					break;
				}
			}

			@Override
			public void mouseReleased(final MouseEvent evt) {
				// When someone releases the button.
				switch (evt.getButton()) {
				case MouseEvent.BUTTON1:
					EventSystem
							.broadcastMessage(
									new MouseEngineEvent(MouseEngineEvent.MouseButton.LEFT,
											MouseEngineEvent.MouseMode.RELEASE, evt.getX(), evt.getY(), 0),
									MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON2:
					EventSystem
							.broadcastMessage(
									new MouseEngineEvent(MouseEngineEvent.MouseButton.RIGHT,
											MouseEngineEvent.MouseMode.RELEASE, evt.getX(), evt.getY(), 0),
									MouseEngineEvent.class, key);
					break;
				case MouseEvent.BUTTON3:
					EventSystem.broadcastMessage(
							new MouseEngineEvent(MouseEngineEvent.MouseButton.MIDDLE,
									MouseEngineEvent.MouseMode.RELEASE, evt.getX(), evt.getY(), 0),
							MouseEngineEvent.class, key);
					break;
				}
			}
		});
		this.addKeyListener(new KeyListener() { // Key listener

			@Override
			public void keyPressed(final KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
					if (arg0.isShiftDown()) {
						// Shutdown key combination: Shift+escape
						Core.shutdown(true, key);
					}
				}
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0, KeyEngineEvent.KeyMode.KEY_PRESS),
						KeyEngineEvent.class, key);
			}

			@Override
			public void keyReleased(final KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0, KeyEngineEvent.KeyMode.KEY_RELEASE),
						KeyEngineEvent.class, key);
			}

			@Override
			public void keyTyped(final KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0, KeyEngineEvent.KeyMode.KEY_TYPED),
						KeyEngineEvent.class, key);
			}
		});
		this.requestFocus(); // Get window focus.
		long startSecond;
		startSecond = System.currentTimeMillis();
		int frames = 0;
		while (running) {
			final int framesInSecond = 1000 / FPS; // The amount of frames in a
													// second.
			startTime = System.currentTimeMillis(); // The starting time in the
													// loop.
			sendTick();
			sendRender();
			endTime = System.currentTimeMillis(); // The ending time in the loop
			final long diff = endTime - startTime;
			final long delta = framesInSecond - diff; // The calculated delta in
														// the
			// time.
			if (delta < -70) { // Small threshold.
				Logger.getLogger("Core").warning("The game is behind by " + Math.abs(delta) + " ticks.");
			}
			try {
				Thread.sleep(delta);
			} catch (final Exception e) {
			}
			if (System.currentTimeMillis() - startSecond >= 1000) {
				if (frames < getFPS()) {
					catchUp = getFPS() - frames;
					for (int i = 0; i < catchUp; i++) {
						EventSystem.broadcastMessage(new TickEvent(), TickEvent.class, key);
					}
				}
				frames = 0;
				startSecond = System.currentTimeMillis();
			} else {
				frames++;
			}
		}
	}

	private int catchUp = 0;

	public int getCatchUp() {
		return catchUp;
	}

	/**
	 * This ticks everything that happens.
	 */
	public void sendTick() {
		// Sends the updates to the objects.
		if (paused) {
			return;
		}
		EventSystem.broadcastMessage(new TickEvent(), TickEvent.class, key);
		MainUtilities.executeQueue(key); // Executes things that are to run on
											// engine thread.
	}

	/**
	 * This pauses the updates on the engine.
	 */
	public void pauseUpdate() {
		paused = true;
	}

	/**
	 * This unpauses the game.
	 */
	public void unpauseUpdate() {
		paused = false;
	}

	Graphics2D graphics; // The graphics for the canvas.
	private int splashCounter; // The counter to determine how long the splash
								// was on.
	private Image Splash; // The splash itself.
	private static Image background; // The background image.

	/**
	 * This function has the screen rendered to.
	 */
	public void sendRender() {
		// Causes the render process.
		final BufferStrategy buffer = getBufferStrategy(); // The buffer system
															// in the
															// rendering system.
		if (buffer == null) {
			// Then create the buffer strategy.
			createBufferStrategy(2);
			return;
		}
		graphics = (Graphics2D) getBufferStrategy().getDrawGraphics(); // Get
																		// the
																		// graphics
																		// to
																		// use.
		graphics.setColor(graphics.getBackground());
		graphics.fillRect(0, 0, Core.getWidth(), Core.getHeight()); // Fill
																	// background
		// Start Draw
		if (background != null) {
			graphics.drawImage(background, 0, 0, null);
		}
		EventSystem.broadcastMessage(new RenderEvent(graphics), RenderEvent.class, key); // Render

		if (showingSplash) {
			splashCounter++; // To stop displaying splash after a while.
			if (splashCounter >= FPS * 2) { // Two Seconds.
				showingSplash = false;
			}
			try {
				if (Splash == null) {
					Splash = ImageIO
							.read(getClass().getClassLoader().getResourceAsStream("dillon/gameAPI/res/splash.png"));
				}
				graphics.drawImage(Splash, Core.getWidth() - 100, Core.getHeight() - 50, null);
			} catch (final Exception e) {
			}
		}
		if (NetworkServer.getServerRunning()) {
			try {
				// Show networking icon if on.
				graphics.drawImage(
						ImageIO.read(
								getClass().getClassLoader().getResourceAsStream("dillon/gameAPI/res/ServerImage.png")),
						Core.getWidth() - 30, 5, null);
			} catch (final Exception e) {
				e.printStackTrace();
				Core.crash(e, key);
			}
		}
		// End draw
		getBufferStrategy().show();
		graphics.dispose();
	}

	/**
	 * This sets the background image.
	 *
	 * @param img
	 *            The image to put in the background.
	 */
	public static void setBackgroundImage(final BufferedImage img) {
		background = img;
	}

	/**
	 * Gets the current background image.
	 *
	 * @return The background image.
	 */
	public static BufferedImage getBackgroundImage() {
		return (BufferedImage) background;
	}

	/**
	 * This method crashes the game and produces a stacktrace onto the screen.
	 *
	 * @param e
	 *            This is the exception that will be displayed.
	 */
	public void crash(final Exception e) {
		if (e == null) {
			throw new IllegalArgumentException("The exception must not be null.");
		}
		stop(); // Halts the game loop
		Logger.getLogger("Core").severe("Crashing...");
		graphics = (Graphics2D) getBufferStrategy().getDrawGraphics();
		final Font f = new Font("Courier", Font.BOLD, 18);
		this.setFont(f);
		this.setBackground(Color.WHITE);
		setBackgroundImage(null);
		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, Core.getWidth(), Core.getHeight());
		graphics.setColor(Color.RED);
		graphics.drawString("An error has occured.", 15, 15);
		graphics.drawString(e.getMessage(), 15, 30);
		final StackTraceElement[] lines = e.getStackTrace();
		String formatted; // The formatted version of the stacktrace.
		int counter = 0;
		for (int i = 0; i < lines.length; i++) {// Tries to display the crash
												// details on the screen.
			formatted = lines[i].getClassName() + "#" + lines[i].getMethodName() + " Line: " + lines[i].getLineNumber();
			graphics.drawString(formatted, 30, i * 15 + 45);
			counter = i + 1;
		}
		graphics.drawString("The game has been halted. Please notify the game author of this error.", 15,
				counter * 15 + 45);
		try {
			BufferedImage img = ImageIO
					.read(CanvasController.class.getClassLoader().getResourceAsStream("dillon/gameAPI/res/crash.png"));
			graphics.drawImage(img, 15, (counter + 1) * 15 + 45, null);
		} catch (Exception e1) {
		}
		getBufferStrategy().show();
		graphics.dispose();
		if (JOptionPane.showConfirmDialog(null, "Do you want to dump the engine?") == JOptionPane.YES_OPTION) { // Print
																												// all
																												// debug
																												// information.
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Choose where to put the dump.");
			if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					Files.deleteIfExists(file.toPath());
					PrintWriter pw = new PrintWriter(new File(file.getAbsolutePath() + ".txt"));
					pw.println("The game crashed with the stacktrace:");
					e.printStackTrace(pw);
					pw.println("");
					pw.println(this.toString());
					pw.println(Core.getDebug());
					pw.flush();
					pw.println(EventSystem.getDebug());
					pw.println(GuiSystem.getDebug());
					pw.println(dillon.gameAPI.mapping.Camera.getDebug());
					pw.println(MapManager.getDebug());
					pw.flush();
					pw.println(NetworkConnection.getDebug());
					pw.println(NetworkServer.getDebug());
					pw.flush();
					pw.println(SecuritySystem.getDebug());
					pw.close();
					JOptionPane.showMessageDialog(null, "Dumped successfully.");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "An error occured when saving.");
				}
			}
		}
	}

	/**
	 * Gets the current FPS limit.
	 *
	 * @return FPS
	 */
	public static int getFPS() {
		return FPS;
	}

	/**
	 * Used for debugging a crash.
	 *
	 * @return Debug string.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\ndillon.gameAPI.core.CanvasController Dump:\n");
		String data = "";
		data = String.format("%-20s %-20s\n", "Key", "Value");
		data += String.format("%-20s %-20s\n", "---", "-----");
		data += String.format("%-20s %-20d\n", "Start Time: ", startTime);
		data += String.format("%-20s %-20d\n", "End Time:", endTime);
		data += String.format("%-20s %-20d\n", "FPS:", FPS);
		data += String.format("%-20s %-20s\n", "Running", running ? "Yes" : "No");
		data += String.format("%-20s %-20s\n", "Paused", paused ? "Yes" : "No");
		data += String.format("%-20s %-20s\n", "Showing Splash:", showingSplash ? "Yes" : "No");
		data += String.format("%-20s %-20d\n", "Splash counter:", splashCounter);
		data += String.format("%-20s %-20s\n", "Splash:", Splash != null ? Splash.toString() : "Not set.");
		data += String.format("%-20s %-20s\n", "Background:", background != null ? background.toString() : "Not set.");
		sb.append(data);
		return sb.toString();
	}
}
