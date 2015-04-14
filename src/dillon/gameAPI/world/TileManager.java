package dillon.gameAPI.world;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import dillon.gameAPI.core.CanvasController;
import dillon.gameAPI.core.Core;
import dillon.gameAPI.errors.RenderingError;
import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EEvent;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.utils.MainUtilities;

/**
 * This class assists with the tiling on the game.
 * 
 * @author Dillon - Github dg092099
 */
public class TileManager {
	private static BufferedImage tileSheet;
	private static BufferedImage[][] imgRegistry;

	/**
	 * This method is to set the image to retrieve the tiles from.
	 * 
	 * @param img
	 *            The tiles image.
	 * @param tileWidth
	 *            The width of each tile.
	 * @param tileHeight
	 *            The Height of each tile.
	 */
	public static void setTileImage(Image img, int tileWidth, int tileHeight) {
		tileSheet = (BufferedImage) img;
		fillRegistry(tileWidth, tileHeight);
	}

	private static int wdth, hgth;

	private static void fillRegistry(int tileWidth, int tileHeight) {
		wdth = tileWidth;
		hgth = tileHeight;
		int tilesX = tileSheet.getWidth(null) / tileWidth;
		int tilesY = tileSheet.getHeight(null) / tileHeight;
		imgRegistry = new BufferedImage[tilesX][tilesY];
		for (int x = 0; x < tilesX; x++) {
			for (int y = 0; y < tilesY; y++) {
				int aX = x * tileWidth;
				int aY = y * tileHeight;
				imgRegistry[x][y] = tileSheet.getSubimage(aX, aY, tileWidth,
						tileHeight);
			}
		}
	}

	/**
	 * This gets the tile at the specified coordinates.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 * @return The tile.
	 */
	public static Image getTile(int x, int y) {
		return imgRegistry[x - 1][y - 1];
	}

	private static BufferedImage map;
	private static BufferedImage mapOverlay;

	/**
	 * This loads a bitmap for the level. The system regarding which tile to
	 * choose is as follows: X coordinate (according to tile registry) = Amount
	 * of red in pixel / 10 Y coordinate ("                        ") = Amount
	 * of blue in pixel / 10 Collidable = Amount of green in pixel (0-127 =
	 * collide; 128-255 = No collide.) When both red and blue in a pixel are at
	 * a value of 255, the program assumes that nothing should be there.
	 * 
	 * @param img
	 *            The bitmap
	 * @throws RenderingError
	 *             Occurs when the engine is in the wrong mode.
	 */
	public static void loadMap(Image img) throws RenderingError {
		if (CanvasController.getRenderMethod() != 1) {
			throw new RenderingError(
					"The render method isn't appropriate for this, use the sidescroller module "
							+ "for this.");
		}
		if (img == null) {
			mapOverlay = new BufferedImage(Core.getWidth(), Core.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			return;
		}
		map = null;
		mapOverlay = null;
		map = (BufferedImage) img;
		mapOverlay = new BufferedImage(wdth * map.getWidth(null), hgth
				* map.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				int copy = map.getRGB(x, y);
				int red = MainUtilities.getRed(copy);
				int blue = MainUtilities.getBlue(copy);
				if (red >= 255 && blue >= 255) {
					red = 0;
					blue = 0;
				}
				int coordX = red / 10;
				int coordY = blue / 10;
				if (coordX > 0 && coordY > 0) {
					Image img2 = getTile(coordX, coordY);
					mapOverlay.getGraphics().drawImage(img2, wdth * x,
							hgth * y, null);
				}
			}
		}
	}

	public TileManager() {
		EventSystem.addHandler(new EEHandler<RenderEvent>() {
			@Override
			public void handle(EEvent T) {
				Graphics2D graphics = (Graphics2D) ((RenderEvent) T)
						.getMetadata()[0];
				graphics.drawImage(mapOverlay, 0, 0, null);
			}
		});
	}

	/**
	 * This method detects if an object is colliding with a tile.
	 * 
	 * @param posx
	 *            Object's X coordinate (top-right)
	 * @param posy
	 *            Object's Y coordinate.
	 * @param w
	 *            Object's width
	 * @param h
	 *            Object's height
	 * @return If it is colliding with something.
	 */
	public static boolean getCollisionAny(double posx, double posy, int w, int h) {
		if (map == null) {
			return false;
		}
		boolean colliding = false;
		Rectangle rect1 = new Rectangle();
		rect1.setBounds((int) posx, (int) posy, w, h);
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (map.getRGB(x, y) == java.awt.Color.WHITE.getRGB()) {
					continue;
				}
				Rectangle rect2 = new Rectangle();
				int tx = wdth * x;
				int ty = hgth * y;
				int tex = tx + wdth;
				int tey = ty + hgth;
				rect2.setBounds(tx, ty, tex, tey);
				if (rect1.intersects(rect2)) {
					colliding = true;
					if (MainUtilities.getGreen(map.getRGB(x, y)) > 127) {
						colliding = false;
					}
				}
			}
		}
		return colliding;
	}

	/**
	 * This method will detect if the object is colliding with a specific type
	 * of square.
	 * 
	 * @param tileX
	 *            The tile's X value in tilesheet
	 * @param tileY
	 *            The tile's Y value in tilehsheet.
	 * @param ox
	 *            The object's Position x.
	 * @param oy
	 *            The object's Position y.
	 * @param w
	 *            The object's width
	 * @param h
	 *            The object's height
	 * @return Whether it does collide or not.
	 */
	public static boolean getCollisionByType(int tileX, int tileY, int ox,
			int oy, int w, int h) {
		boolean colliding = false;
		Rectangle rect1 = new Rectangle();
		rect1.setBounds(ox, oy, w, h);
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				int rgb = map.getRGB(x, y);
				if (MainUtilities.getGreen(rgb) > 128)
					continue;
				if ((MainUtilities.getBlue(rgb) / 10 == tileY)
						&& (MainUtilities.getRed(rgb) / 10 == tileX)) {
					Rectangle rect2 = new Rectangle();
					rect2.setBounds(wdth * x, hgth * y, (wdth * x) + wdth,
							(wdth * x) + hgth);
					if (rect1.intersects(rect2)) {
						colliding = true;
						if (MainUtilities.getGreen(map.getRGB(x, y)) > 127) {
							colliding = false;
						}
					}
				}
			}
		}
		return colliding;
	}

	public static Image getTilesheet() {
		return tileSheet;
	}

	public static int getTileWidth() {
		return wdth;
	}

	public static int getTileHeight() {
		return hgth;
	}

	public static Image getMap() {
		return map;
	}
}
