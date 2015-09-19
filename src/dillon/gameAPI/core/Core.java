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

import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.ShutdownEvent;
import dillon.gameAPI.gui.guiManager;
import dillon.gameAPI.modding.ModdingCore;
import dillon.gameAPI.networking.NetworkConnection;
import dillon.gameAPI.networking.NetworkServer;
import dillon.gameAPI.scroller.Camera;
import dillon.gameAPI.scroller.ScrollManager;

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
	public static final String ENGINE_VERSION = "v1.10"; // The engine's
															// version.
	public static final int TILES = 1; // Constant: Render method, tile.
	public static final int SIDESCROLLER = 2; // Constant: render method,
												// sidescroller.

	/**
	 * This method starts the game with the specified background and fps.
	 * 
	 * @param FPS
	 *            The maximum frames per second to use.
	 * @param background
	 *            The default background image. to change it.
	 * @param mode
	 *            This sets the rendering mode.
	 */
	public static void startGame(int FPS, BufferedImage background, int mode) {
		Logger.getLogger("Core").info("Starting game.");
		controller.start();
		controller.setFps(FPS);
		Thread t = new Thread(controller); // So that the game loop can't
											// interfere with other programming.
		t.setName("Canvas Controller");
		t.start();
		if (background != null) {
			CanvasController.setBackgroundImage(background);
		}
		new ScrollManager();
		new Camera();
		new guiManager();
		CanvasController.setRenderMethod(mode);
		ModdingCore.sendPostStart();
	}

	/**
	 * Pauses the game.
	 */
	public static void pauseUpdate() {
		controller.pauseUpdate();
	}

	/**
	 * Unpauses the game.
	 */
	public static void unpauseUpdate() {
		controller.unpauseUpdate();
	}

	/**
	 * The background color.
	 * 
	 * @return The background color.
	 */
	public static Color getBackColor() {
		return controller.getBackground();
	}

	/**
	 * This returns the title you specified for the game.
	 * 
	 * @return Name
	 */
	public String getTitle() {
		return TITLE;
	}

	/**
	 * This returns the icon on the taskbar that you requested to be used.
	 * 
	 * @return taskbar icon
	 */
	public Image getIcon() {
		return ICON;
	}

	/**
	 * This method sets up the jframe.
	 * 
	 * @param width
	 *            width of jframe.
	 * @param height
	 *            height of jframe.
	 * @param title
	 *            title of game.
	 * @param icon
	 *            Icon to use in the corner of the program - optional, the
	 *            engine has its own, but it's ugly so use your own.
	 * @throws IOException
	 *             Results because the icon that you specified couldn't be
	 *             found.
	 */
	public static void setup(int width, int height, String title, Image icon) throws IOException {
		TITLE = title;
		ICON = icon;
		Logger.getLogger("Core").info("Setting up...");
		frame = new JFrame(title);
		controller = new CanvasController();
		frame.getContentPane().setSize(new Dimension(width, height));
		frame.setTitle(title);
		frame.setResizable(false);
		if (icon != null) {
			frame.setIconImage(icon);
		} else {
			frame.setIconImage(ImageIO.read(Core.class.getResourceAsStream("logo.png")));
		}
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				shutdown(false);
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});
		controller = new CanvasController();
		frame.add(controller);
		frame.pack();
		frame.setLocationRelativeTo(null);
		ModdingCore.sendInit();
		frame.setVisible(true);
	}

	private static boolean fullscreen = false;

	/**
	 * Sets if the screen should be fullscreen.
	 * 
	 * @param b
	 *            Weather or not it should be fullscreen.
	 */
	public static void setFullScreen(boolean b) {
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if (devices[0].isFullScreenSupported()) {
			fullscreen = b;
			devices[0].setFullScreenWindow(fullscreen ? frame : null);
		}

	}

	/**
	 * Returns jframe width
	 * 
	 * @return width
	 */
	public static int getWidth() {
		return frame.getContentPane().getWidth();
	}

	/**
	 * Returns jframe height
	 * 
	 * @return height
	 */
	public static int getHeight() {
		return frame.getContentPane().getHeight();
	}

	static CanvasController controller;

	/**
	 * Gets the canvas
	 * 
	 * @return canvas
	 */
	public static CanvasController getController() {
		return controller;
	}

	/**
	 * This method shuts off the event system and activates CanvasController's
	 * Crash to stop the engine.
	 * 
	 * @param e
	 *            The exception that was thrown.
	 */
	public static void crash(Exception e) {
		try {
			NetworkServer.stopServer();
			NetworkConnection.disconnect();
			EventSystem.override();
			controller.crash(e);
		} catch (Exception e2) {
			e2.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This method will shutdown the game. This occurs when the x is clicked,
	 * the key combination shift + escape is used, or the game crashes.
	 * 
	 * @param hard
	 *            Determines if the api would yield to the event system for
	 *            shutdown, or if it will shutdown directly.
	 */
	public static void shutdown(boolean hard) {
		if (hard) {
			Logger.getLogger("Core").severe("Engine Shutting down...");
			NetworkServer.stopServer();
			NetworkConnection.disconnect();
			NetworkServer.disableDiscovery();
			System.exit(0);
		} else {
			Logger.getLogger("Core").severe("Engine Shutting down...");
			EventSystem.broadcastMessage(new ShutdownEvent(), ShutdownEvent.class);
			controller.stop();
			Logger.getLogger("Core").severe("Stopping server...");
			NetworkServer.stopServer();
			NetworkConnection.disconnect();
			NetworkServer.disableDiscovery();
			System.exit(0);
		}
	}

	/**
	 * This method sets the maximum frames per second that this game should run
	 * on.
	 * 
	 * @param fps
	 *            The new fps limit.
	 */
	public static void setFPS(int fps) {
		controller.setFps(fps);
	}

	/**
	 * This method will change the color of the background on the game.
	 * 
	 * @param c
	 *            The new color.
	 */
	public static synchronized void setBackColor(Color c) {
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
	 * Gets the render method.
	 * 
	 * @return The render method
	 */
	public static int getRenderMethod() {
		return CanvasController.getRenderMethod();
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
	 * Gets the current fps.
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
	 */
	public static void setBackgroundImage(BufferedImage background) {
		CanvasController.setBackgroundImage(background);
	}

	/**
	 * Sets the render method.
	 * 
	 * @param canvasState
	 *            The method number.
	 */
	public static void setRenderMethod(int canvasState) {
		CanvasController.setRenderMethod(canvasState);
	}
}
