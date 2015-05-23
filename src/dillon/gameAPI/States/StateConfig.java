package dillon.gameAPI.States;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.errors.RenderingError;
import dillon.gameAPI.scroller.Camera;
import dillon.gameAPI.scroller.ScrollManager;
import dillon.gameAPI.world.TileManager;

/**
 * This class holds a snapshot of the engine.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class StateConfig implements Serializable {
	private static final long serialVersionUID = -2971917949105802923L;
	private transient BufferedImage background; // Check
	private Color backColor; // Check
	private int FPS; // Check
	private int CamX, CamY; // Check
	private transient BufferedImage ScrollManTiles; // Check
	private int scrollManDistX, scrollManDistY; // Check
	private transient BufferedImage scrollManMap;// Check
	private transient BufferedImage tileManTiles;
	private int tileManDistX, tileManDistY;
	private transient BufferedImage tileManMap;
	private int CanvasState = -1; // Check

	public synchronized void takeSnapshot() {
		background = Core.getBackgroundImage();
		backColor = Core.getBackColor();
		FPS = Core.getFPS();
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
		CanvasState = Core.getRenderMethod();
	}

	/**
	 * This will apply the config to the engine.
	 */
	public synchronized void apply() {
		if (background != null) {
			Core.setBackgroundImage(background);
		}
		if (backColor != null) {
			Core.setBackColor(backColor);
		}
		if (FPS > 0) {
			Core.setFPS(FPS);
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
			Core.setRenderMethod(CanvasState);
		}
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
	 * @return the background
	 */
	public Image getBackground() {
		return background;
	}

	/**
	 * @param background
	 *            the background to set
	 */
	public void setBackground(BufferedImage background) {
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
	public void setScrollManTiles(BufferedImage scrollManTiles) {
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
	public void setScrollManMap(BufferedImage scrollManMap) {
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
	public void setTileManTiles(BufferedImage tileManTiles) {
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
	public void setTileManMap(BufferedImage tileManMap) {
		this.tileManMap = tileManMap;
	}

	private void writeObject(ObjectOutputStream oos) {
		try {
			oos.defaultWriteObject();
			BufferedImage nonImage = new BufferedImage(1, 1,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = nonImage.createGraphics();
			g.setColor(new Color(5, 10, 15));
			g.drawRect(0, 0, 1, 1);
			ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
			images.add(background != null ? background : nonImage);
			images.add(ScrollManTiles != null ? ScrollManTiles : nonImage);
			images.add(scrollManMap != null ? scrollManMap : nonImage);
			images.add(tileManTiles != null ? tileManTiles : nonImage);
			images.add(tileManMap != null ? tileManMap : nonImage);
			oos.writeInt(images.size());
			for (BufferedImage i : images) {
				ImageIO.write(i, "png", oos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException,
			ClassNotFoundException {
		ois.defaultReadObject();
		final int count = ois.readInt();
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
		for (int i = 0; i < count; i++) {
			images.add(ImageIO.read(ois));
		}
		background = images.get(0);
		if (background == null
				|| (background.getHeight() == 1 && background.getWidth() == 1))
			background = null;
		ScrollManTiles = images.get(1);
		if (ScrollManTiles == null
				|| (ScrollManTiles.getHeight() == 1 && ScrollManTiles
						.getWidth() == 1))
			ScrollManTiles = null;
		scrollManMap = images.get(2);
		if (scrollManMap == null
				|| (scrollManMap.getHeight() == 1 && scrollManMap.getWidth() == 1))
			scrollManMap = null;
		tileManTiles = images.get(3);
		if (tileManTiles == null
				|| (tileManTiles.getHeight() == 1 && tileManTiles.getWidth() == 1))
			tileManTiles = null;
		tileManMap = images.get(4);
		if (tileManMap == null
				|| (tileManMap.getHeight() == 1 && tileManMap.getWidth() == 1))
			tileManMap = null;
	}
}
