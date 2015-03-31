package dillon.gameEngine.States;

import java.awt.Color;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;

import dillon.gameEngine.core.CanvasController;
import dillon.gameEngine.core.Core;
import dillon.gameEngine.errors.RenderingError;
import dillon.gameEngine.event.EEHandler;
import dillon.gameEngine.event.EventSystem;
import dillon.gameEngine.scroller.Camera;
import dillon.gameEngine.scroller.ScrollManager;
import dillon.gameEngine.world.TileManager;

/**
 * This class holds a snapshot of the engine.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class StateConfig implements Serializable {
	private static final long serialVersionUID = -2971917949105802923L;
	private Image background;
	private Color backColor;
	private int FPS;
	private ArrayList<EEHandler<?>> eventHandlers = new ArrayList<EEHandler<?>>();
	private int CamX, CamY;
	private Image ScrollManTiles;
	private int scrollManDistX, scrollManDistY;
	private Image scrollManMap;
	private Image tileManTiles;
	private int tileManDistX, tileManDistY;
	private Image tileManMap;
	private int CanvasState = -1;

	public synchronized void takeSnapshot() {
		background = CanvasController.getBackgroundImage();
		backColor = Core.getBackColor();
		FPS = CanvasController.getFPS();
		eventHandlers = EventSystem.getHandlers();
		CamX = Camera.getXPos();
		CamY = Camera.getYPos();
		ScrollManTiles = ScrollManager.getTiles();
		scrollManDistX = ScrollManager.getTileWidth();
		scrollManDistY = ScrollManager.getTileHeight();
		scrollManMap = ScrollManager.getMap();
		tileManTiles = TileManager.getTilesheet();
		tileManDistX = TileManager.getTileWidth();
		tileManDistY = TileManager.getTileHeight();
		tileManMap = TileManager.getMap();
		CanvasState = CanvasController.getRenderMethod();
	}

	/**
	 * This will apply the config to the engine.
	 */
	public synchronized void apply() {
		CanvasController.blackout(Color.black, "Switching states...",
				"Calibri", 20, Color.white);
		if (background != null) {
			CanvasController.setBackgroundImage(background);
		}
		if (backColor != null) {
			Core.setBackColor(backColor);
		}
		if (FPS > 0) {
			Core.setFPS(FPS);
		}
		if (eventHandlers != null) {
			EventSystem.setHandlers(eventHandlers);
		}
		if (CamX >= 0) {
			Camera.setX(CamX);
			Camera.setY(CamY);
		}
		if (ScrollManTiles != null) {
			ScrollManager.regsterTiles(ScrollManTiles, scrollManDistX,
					scrollManDistY);
		}
		if (scrollManMap != null) {
			ScrollManager.setLevel(scrollManMap);
		}
		if (tileManTiles != null) {
			TileManager.setTileImage(tileManTiles, tileManDistX, tileManDistY);
		}
		if (tileManMap != null) {
			try {
				TileManager.loadMap(tileManMap);
			} catch (RenderingError e) {
				e.printStackTrace();
			}
		}
		if (CanvasState != -1) {
			CanvasController.setRenderMethod(CanvasState);
		}
		CanvasController.clearBlackout();
	}

	/**
	 * Sets the cached canvas state.
	 * 
	 * @param c
	 *            state
	 */
	public void setCanvasState(int c) {
		CanvasState = c;
	}

	/**
	 * Gets the cached canvas state.
	 * 
	 * @return The state.
	 */
	public int getCanvasState() {
		return CanvasState;
	}

	/**
	 * Adds a handler to the state.
	 * 
	 * @param m
	 *            The handler
	 */
	public void addEventHandler(EEHandler<?> m) {
		eventHandlers.add(m);
	}

	/**
	 * Removes the handler.
	 * 
	 * @param m
	 *            the handler object.
	 */
	public void removeEventHandler(EEHandler<?> m) {
		eventHandlers.remove(m);
	}

	/**
	 * @return the background
	 */
	public Image getBackground() {
		return background;
	}

	/**
	 * @param background
	 *            the background to set
	 */
	public void setBackground(Image background) {
		this.background = background;
	}

	/**
	 * @return the backColor
	 */
	public Color getBackColor() {
		return backColor;
	}

	/**
	 * @param backColor
	 *            the backColor to set
	 */
	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	/**
	 * @return the fPS
	 */
	public int getFPS() {
		return FPS;
	}

	/**
	 * @param fPS
	 *            the fPS to set
	 */
	public void setFPS(int fPS) {
		FPS = fPS;
	}

	/**
	 * @return the camX
	 */
	public int getCamX() {
		return CamX;
	}

	/**
	 * @param camX
	 *            the camX to set
	 */
	public void setCamX(int camX) {
		CamX = camX;
	}

	/**
	 * @return the camY
	 */
	public int getCamY() {
		return CamY;
	}

	/**
	 * @param camY
	 *            the camY to set
	 */
	public void setCamY(int camY) {
		CamY = camY;
	}

	/**
	 * @return the scrollManTiles
	 */
	public Image getScrollManTiles() {
		return ScrollManTiles;
	}

	/**
	 * @param scrollManTiles
	 *            the scrollManTiles to set
	 */
	public void setScrollManTiles(Image scrollManTiles) {
		ScrollManTiles = scrollManTiles;
	}

	/**
	 * @return the scrollManDistX
	 */
	public int getScrollManDistX() {
		return scrollManDistX;
	}

	/**
	 * @param scrollManDistX
	 *            the scrollManDistX to set
	 */
	public void setScrollManDistX(int scrollManDistX) {
		this.scrollManDistX = scrollManDistX;
	}

	/**
	 * @return the scrollManDistY
	 */
	public int getScrollManDistY() {
		return scrollManDistY;
	}

	/**
	 * @param scrollManDistY
	 *            the scrollManDistY to set
	 */
	public void setScrollManDistY(int scrollManDistY) {
		this.scrollManDistY = scrollManDistY;
	}

	/**
	 * @return the scrollManMap
	 */
	public Image getScrollManMap() {
		return scrollManMap;
	}

	/**
	 * @param scrollManMap
	 *            the scrollManMap to set
	 */
	public void setScrollManMap(Image scrollManMap) {
		this.scrollManMap = scrollManMap;
	}

	/**
	 * @return the tileManTiles
	 */
	public Image getTileManTiles() {
		return tileManTiles;
	}

	/**
	 * @param tileManTiles
	 *            the tileManTiles to set
	 */
	public void setTileManTiles(Image tileManTiles) {
		this.tileManTiles = tileManTiles;
	}

	/**
	 * @return the tileManDistX
	 */
	public int getTileManDistX() {
		return tileManDistX;
	}

	/**
	 * @param tileManDistX
	 *            the tileManDistX to set
	 */
	public void setTileManDistX(int tileManDistX) {
		this.tileManDistX = tileManDistX;
	}

	/**
	 * @return the tileManDistY
	 */
	public int getTileManDistY() {
		return tileManDistY;
	}

	/**
	 * @param tileManDistY
	 *            the tileManDistY to set
	 */
	public void setTileManDistY(int tileManDistY) {
		this.tileManDistY = tileManDistY;
	}

	/**
	 * @return the tileManMap
	 */
	public Image getTileManMap() {
		return tileManMap;
	}

	/**
	 * @param tileManMap
	 *            the tileManMap to set
	 */
	public void setTileManMap(Image tileManMap) {
		this.tileManMap = tileManMap;
	}
}
