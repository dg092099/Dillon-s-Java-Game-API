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
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.KeyEngineEvent;
import dillon.gameAPI.event.MouseEngineEvent;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.event.TickEvent;
import dillon.gameAPI.networking.NetworkServer;
import dillon.gameAPI.utils.MainUtilities;

/**
 * This class is in control of the game's canvas.
 * 
 * @author Dillon - Github dg092099
 */
class CanvasController extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public CanvasController() {
		this.setSize(new Dimension(Core.getWidth(), Core.getHeight()));
		this.setBackground(Color.BLACK);
	}

	private long startTime, endTime;
	private static int FPS = 30;
	private boolean running = false;
	private volatile boolean paused = false;

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
					}
				}
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0,
						KeyEngineEvent.KEY_PRESS));
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0,
						KeyEngineEvent.KEY_RELEASE));
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				EventSystem.broadcastMessage(new KeyEngineEvent(arg0,
						KeyEngineEvent.KEY_TYPED));
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
		if (paused)
			return;
		EventSystem.broadcastMessage(new TickEvent());
		MainUtilities.executeQueue();
	}

	/**
	 * This pauses the update on the engine.
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

	Graphics2D graphics;
	private int splashCounter;
	private Image Splash;
	private static Image background;

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
		if (background != null) {
			graphics.drawImage(background, 0, 0, null);
		}
		EventSystem.broadcastMessage(new RenderEvent(graphics));

		if (showingSplash) {
			splashCounter++;
			if (splashCounter >= FPS * 2)
				showingSplash = false;
			try {
				if (Splash == null) {
					Splash = ImageIO.read(getClass().getResourceAsStream(
							"splash.png"));
				}
				graphics.drawImage((Image) Splash, Core.getWidth() - 100,
						Core.getHeight() - 50, null);
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

	private static int renderMethod = 0;

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
