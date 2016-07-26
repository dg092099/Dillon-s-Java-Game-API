package dillon.gameAPI.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import dillon.gameAPI.errors.GeneralRuntimeException;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.ShutdownEvent;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.mapping.Camera;
import dillon.gameAPI.mapping.MapManager;
import dillon.gameAPI.modding.ModdingCore;
import dillon.gameAPI.networking.NetworkConnection;
import dillon.gameAPI.networking.NetworkServer;
import dillon.gameAPI.scripting.bridges.GuiFactory;
import dillon.gameAPI.scripting.bridges.RemoteCallBridge;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * Core file that has starting and stopping methods. A policy file should be
 * used to isolate the game to a certain directory.
 *
 * @author Dillon - Github dg092099
 */
public class Core {
	private static String TITLE; // The game's title.
	private static Image ICON; // The icon for the game.
	private static JFrame frame; // The JFrame window.
	public static final String ENGINE_VERSION = "v2.2.0"; // The engine's
															// version.
	public static int WIDTH, HEIGHT;

	/**
	 * This method starts the game with the specified background and FPS. <b>Use
	 * setup method before this method.</b>
	 *
	 * @param FPS
	 *            The maximum frames per second to use.
	 * @param background
	 *            The default background image. to change it.
	 * @param key
	 *            The security key for the security module.
	 */
	public static void startGame(final int FPS, final BufferedImage background, final SecurityKey key) {
		if (frame == null) { // Frame must exist to use it.
			throw new GeneralRuntimeException("Invalid state: Use setup method first.");
		}
		SecuritySystem.checkPermission(key, RequestedAction.START_GAME); // Security
																			// check.
		if (FPS <= 0) {
			throw new IllegalArgumentException("FPS must be more than 0.");
		}
		Logger.getLogger("Core").info("Starting game.");
		controller.start(); // Initiate the canvas controller.
		controller.setFps(FPS);
		final Thread t = new Thread(controller); // So that the game loop can't
		// interfere with other programming.
		t.setName("Canvas Controller");
		t.start();
		if (background != null) {
			// Set background image.
			CanvasController.setBackgroundImage(background);
		}
		// Setup auxiliary systems.
		new Camera();
		guiSystem = new GuiSystem(engineKey);
		MapManager.initiate(engineKey);
		ModdingCore.sendPostStart();
	}

	/**
	 * Pauses the game.
	 *
	 * @param k
	 *            The security key.
	 */
	public static void pauseUpdate(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.PAUSE);
		controller.pauseUpdate();
	}

	/**
	 * Sets the title of the frame.
	 *
	 * @param newTitle
	 *            The title.
	 */
	public static void setTitle(String newTitle) {
		if (frame == null) {
			throw new IllegalArgumentException("You didn't start the game. You can't set the title.");
		}
		if (newTitle == null) {
			throw new IllegalArgumentException("newTitle must not be null. If needed, use an empty string.");
		}
		if (frame != null && newTitle != null) {
			frame.setTitle(newTitle);
		}
	}

	/**
	 * Unpauses the game.
	 *
	 * @param k
	 *            The security key.
	 */
	public static void unpauseUpdate(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.UNPAUSE);
		controller.unpauseUpdate();
	}

	/**
	 * Returns the background color of the canvas.
	 *
	 * @return The background color.
	 */
	public static Color getBackColor() {
		return controller.getBackground();
	}

	/**
	 * This returns the title you specified for the game.
	 *
	 * @return Title
	 */
	public static String getTitle() {
		return TITLE;
	}

	/**
	 * This returns the icon on the taskbar that you requested to be used.
	 *
	 * @return taskbar icon
	 */
	public static Image getIcon() {
		return ICON;
	}

	/**
	 * Sets the program icon.
	 *
	 * @param img
	 *            The icon
	 */
	public static void setIcon(BufferedImage img) {
		frame.setIconImage(img);
	}

	private static SecurityKey engineKey;

	/**
	 * This method sets up the jframe.
	 *
	 * @param width
	 *            Width of jframe.
	 * @param height
	 *            Height of jframe.
	 * @param title
	 *            Title of game.
	 * @param icon
	 *            Icon to use in the corner of the program - optional, the
	 *            engine has its own, but it's ugly so use your own.
	 * @param k
	 *            The security key.
	 * @throws IOException
	 *             Results because the icon that you specified couldn't be
	 *             found.
	 */
	public static void setup(final int width, final int height, final String title, final Image icon, SecurityKey k)
			throws IOException {
		SecuritySystem.checkPermission(k, RequestedAction.SETUP_GAME); // Security
																		// check.
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Width and height must be larger than 0.");
		}
		if (title == null) {
			throw new IllegalArgumentException("Title must not be null, use an empty string if necesary.");
		}
		WIDTH = width;
		HEIGHT = height;
		engineKey = SecuritySystem.init(); // Obtain engine key
		// Set important variables.
		TITLE = title;
		ICON = icon;
		Logger.getLogger("Core").info("Setting up...");
		frame = new JFrame(TITLE);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// Setup canvas controller and window.
		frame.getContentPane().setSize(new Dimension(width, height));
		controller = new CanvasController(engineKey);
		frame.setTitle(title);
		frame.setResizable(false);
		if (icon != null) {
			frame.setIconImage(icon);
		} else {
			frame.setIconImage(
					ImageIO.read(Core.class.getClassLoader().getResourceAsStream("dillon/gameAPI/res/logo.png"))); // Default
		}
		// Add listeners
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(final WindowEvent arg0) {
			}

			@Override
			public void windowClosed(final WindowEvent arg0) {
			}

			@Override
			public void windowClosing(final WindowEvent arg0) { // Shutdown
				shutdown(false, engineKey);
			}

			@Override
			public void windowDeactivated(final WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(final WindowEvent arg0) {
			}

			@Override
			public void windowIconified(final WindowEvent arg0) {
			}

			@Override
			public void windowOpened(final WindowEvent arg0) {
			}
		});
		// Setup canvas controller.
		controller = new CanvasController(engineKey);
		// Touch up on window.
		frame.add(controller);
		frame.pack();
		frame.setLocationRelativeTo(null);
		ModdingCore.sendInit();
		frame.setVisible(true); // Show window
		guiFactory = new GuiFactory(engineKey);
		scriptRemote = new RemoteCallBridge(engineKey);
	}

	/**
	 * Gets the canvas controller. This can only be used in this package.
	 *
	 * @return The canvas controller.
	 */
	public static CanvasController getController() {
		return controller;
	}

	private static GuiFactory guiFactory;
	private static boolean fullscreen = false;
	private static RemoteCallBridge scriptRemote;

	/**
	 * Gets the remote bridge for the scripting module.
	 *
	 * @return Bridge
	 */
	public static RemoteCallBridge getRemoteBridge() {
		return scriptRemote;
	}

	/**
	 * Gets the GUI factory bridge
	 *
	 * @return The GuiFactory bridge.
	 */
	public static GuiFactory getGuiFactory() {
		return guiFactory;
	}

	/**
	 * Sets if the screen should be fullscreen.
	 *
	 * @param b
	 *            Weather or not it should be fullscreen.
	 * @param k
	 *            The security key.
	 */
	public static void setFullScreen(final boolean b, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_FULLSCREEN);
		final GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices(); // Gets
																												// devices
		if (devices[0].isFullScreenSupported()) {
			fullscreen = b;
			devices[0].setFullScreenWindow(fullscreen ? frame : null);
		}

	}

	/**
	 * Returns JFrame width
	 *
	 * @return width
	 */
	public static int getWidth() {
		return frame.getContentPane().getWidth();
	}

	/**
	 * Returns JFrame height
	 *
	 * @return height
	 */
	public static int getHeight() {
		return frame.getContentPane().getHeight();
	}

	static CanvasController controller;

	/**
	 * This method shuts off the event system and activates CanvasController's
	 * Crash to stop the engine.
	 *
	 * @param e
	 *            The exception that was thrown.
	 * @param k
	 *            The security key.
	 */
	public static void crash(final Exception e, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.CRASH_GAME);
		if (e == null) {
			throw new IllegalArgumentException("e must not be null.");
		}
		try {
			// Crash game.
			NetworkServer.stopServer(engineKey);
			NetworkConnection.disconnect(engineKey);
			EventSystem.override(); // Shutdown system
			controller.crash(e);
		} catch (final Exception e2) {
			e2.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Gets how far behind the program is.
	 *
	 * @return The amount of updates.
	 */
	public static int getCatchUp() {
		return controller.getCatchUp();
	}

	/**
	 * This method will shutdown the game. This occurs when the x is clicked,
	 * the key combination shift + escape is used, or the game crashes.
	 *
	 * @param hard
	 *            Determines if the API would yield to the event system for
	 *            shutdown, or if it will shutdown immediately.
	 * @param k
	 *            The security key.
	 */
	public static void shutdown(final boolean hard, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SHUTDOWN);
		if (hard) { // If the game should immediately shutdown.
			Logger.getLogger("Core").severe("Engine Shutting down...");
			NetworkServer.stopServer(engineKey); // Shutdown server.
			NetworkConnection.disconnect(engineKey); // Cut connection if used.
			NetworkServer.disableDiscovery(engineKey); // Shuts off discovery if
														// active.
			System.exit(0);
		} else {
			Logger.getLogger("Core").severe("Engine Shutting down...");
			// Send message on event system.
			EventSystem.broadcastMessage(new ShutdownEvent(), ShutdownEvent.class, engineKey);
			controller.stop();
			Logger.getLogger("Core").severe("Stopping server...");
			NetworkServer.stopServer(engineKey);
			NetworkConnection.disconnect(engineKey);
			NetworkServer.disableDiscovery(engineKey);
			System.exit(0);
		}
	}

	/**
	 * This method sets the maximum frames per second that this game should run
	 * on.
	 *
	 * @param fps
	 *            The new FPS limit.
	 * @param k
	 *            The security key.
	 */
	public static void setFPS(final int fps, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_FPS);
		if (fps <= 0) {
			throw new IllegalArgumentException("FPS must be more than 0.");
		}
		controller.setFps(fps);
	}

	/**
	 * This method will change the color of the background on the game.
	 *
	 * @param c
	 *            The new color.
	 * @param k
	 *            The security key
	 */
	public static synchronized void setBackColor(final Color c, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_BACKGROUND_COLOR);
		if (c == null) {
			throw new IllegalArgumentException(
					"Background color must not be null. Use Color.black for a blank background.");
		}
		controller.setBackground(c);
	}

	/**
	 * This function will return the current version of the engine.
	 *
	 * @return Version string
	 */
	public static String getVersion() {
		return ENGINE_VERSION;
	}

	/**
	 * Gets the background image.
	 *
	 * @return The background image.
	 */
	public static BufferedImage getBackgroundImage() {
		return CanvasController.getBackgroundImage();
	}

	/**
	 * Gets the current FPS.
	 *
	 * @return FPS
	 */
	public static int getFPS() {
		return CanvasController.getFPS();
	}

	/**
	 * Sets the background image
	 *
	 * @param background
	 *            The image
	 * @param k
	 *            The security key.
	 */
	public static void setBackgroundImage(final BufferedImage background, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SET_BACKGROUND_IMAGE);
		CanvasController.setBackgroundImage(background);
	}

	/**
	 * Used for debugging a crash.
	 *
	 * @return Debugging string
	 */
	public static String getDebug() {
		final StringBuilder sb = new StringBuilder();
		sb.append("\n\ndillon.gameAPI.core.Core Dump:\n");
		String data = "";
		data += String.format("%-13s %-15s\n", "Key", "Value");
		data += String.format("%-13s %-15s\n", "---", "-----");
		data += String.format("%-13s %-15s\n", "Title:", TITLE);
		data += String.format("%-13s %-15s\n", "Icon:", ICON != null ? ICON.toString() : "None");
		data += String.format("%-13s %-15s\n", "Frame:", frame.toString());
		data += String.format("%-13s %-15s\n", "Fullscreen", fullscreen ? "Yes" : "No");
		sb.append(data);
		return sb.toString();
	}

	private static GuiSystem guiSystem;

	/**
	 * Gets the GUI system object.
	 *
	 * @return The object.
	 */
	public static GuiSystem getGuiSystem() {
		return guiSystem;
	}
}
