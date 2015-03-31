package dillon.gameEngine.scroller;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import dillon.gameEngine.core.CanvasController;
import dillon.gameEngine.core.Core;
import dillon.gameEngine.event.EEHandler;
import dillon.gameEngine.event.EEvent;
import dillon.gameEngine.event.EventSystem;
import dillon.gameEngine.event.RenderEvent;
import dillon.gameEngine.utils.MainUtilities;

/**
 * This class should be used to implement side-scroller like games.
 * 
 * @author Dillon - Github dg092099
 */
public class ScrollManager {

	private static BufferedImage[][] tiles;
	private static int width, height;
	private static Image tilesheet;

	/**
	 * This method will register the tile sheet to use.
	 * 
	 * @param img
	 *            The image for the tiles.
	 * @param width
	 *            The width of each tile.
	 * @param height
	 *            The height of each tile.
	 */
	public static void regsterTiles(Image img, int width, int height) {
		tilesheet = img;
		ScrollManager.width = width;
		ScrollManager.height = height;
		if (img == null) {
			System.out.println("image is null.");
			return;
		}
		if (width == 0 || height == 0) {
			System.out.println("dimensions are null.");
			return;
		}
		ScrollManager.width = width;
		ScrollManager.height = height;
		int tilesX = img.getWidth(null) / width;
		int tilesY = img.getHeight(null) / height;
		tiles = new BufferedImage[tilesX][tilesY];
		for (int x = 0; x < tilesX; x++) {
			for (int y = 0; y < tilesY; y++) {
				BufferedImage img2 = (BufferedImage) img;
				if (img2 == null) {
					System.out.println("Unable to cast.");
					return;
				}
				BufferedImage tile = img2.getSubimage(width * x, height * y,
						width, height);
				if (tile == null) {
					System.out.println("Tile is null.");
				}
				System.out.println("Setting tile: " + x + " " + y);
				tiles[x][y] = tile;
			}
		}
	}

	private static BufferedImage fullMap;
	private static BufferedImage bitMap;

	/**
	 * The method to set the level.
	 * 
	 * @param img
	 *            The image that represents the level.
	 * 
	 */
	public static void setLevel(Image img) {
		try {
			if (CanvasController.getRenderMethod() != CanvasController.SIDESCROLLER)
				return;
			bitMap = (BufferedImage) img;
			fullMap = new BufferedImage(bitMap.getWidth() * width,
					bitMap.getHeight() * height, BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x < bitMap.getWidth(); x++) {
				for (int y = 0; y < bitMap.getHeight(); y++) {
					int red, blue;
					red = MainUtilities.getRed(bitMap.getRGB(x, y));
					blue = MainUtilities.getBlue(bitMap.getRGB(x, y));
					if (red >= 255)
						continue;
					fullMap.getGraphics().drawImage(
							getTile(red / 10, blue / 10), x * width,
							y * height, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Core.crash(e);
		}
	}

	private static Image getTile(int i, int j) {
		int x = i - 1;
		int y = j - 1;
		if (tiles == null) {
			System.out.println("Tiles is null.");
		}
		return tiles[x][y];
	}

	/**
	 * This gets the dimensions of the full map.
	 * 
	 * @return The dimensions.
	 */
	public static Dimension getFullLayoutDims() {
		try {
			return new Dimension(fullMap.getWidth(), fullMap.getHeight());
		} catch (Exception e) {
		}
		return null;
	}

	public ScrollManager() {
		EventSystem.addHandler(new EEHandler<RenderEvent>() {
			@Override
			public void handle(EEvent T) {
				if (CanvasController.getRenderMethod() != 2)
					return;
				Graphics2D graphics = (Graphics2D) ((RenderEvent) T)
						.getMetadata()[0];
				graphics.drawImage(fullMap, 0 - Camera.getXPos(),
						0 - Camera.getYPos(), null);
			}
		});
	}

	public static boolean getCollisionAny(double x2, double y2, int width2,
			int height2) {
		boolean colliding = false;
		if (fullMap == null) {
			return false;
		}
		Rectangle rect1 = new Rectangle();
		rect1.setBounds((int) x2, (int) y2, width2, height2);
		for (int x = 0; x < bitMap.getWidth(); x++) {
			for (int y = 0; y < bitMap.getHeight(); y++) {
				if (bitMap.getRGB(x, y) == java.awt.Color.WHITE.getRGB()) {
					continue;
				}
				Rectangle rect2 = new Rectangle();
				int tx = width * x;
				int ty = height * y;
				int tex = tx + width;
				int tey = ty + height;
				rect2.setBounds(tx, ty, tex, tey);
				if (rect1.intersects(rect2)) {
					colliding = true;
					if (MainUtilities.getGreen(bitMap.getRGB(x, y)) > 127) {
						colliding = false;
					}
				}
			}
		}
		return colliding;
	}

	/**
	 * Used internally by state module.
	 * 
	 * @return tilesheet
	 */
	public static Image getTiles() {
		return tilesheet;
	}

	/**
	 * Used internally by state module.
	 * 
	 * @return width
	 */
	public static int getTileWidth() {
		return width;
	}

	public static int getTileHeight() {
		return height;
	}

	public static Image getMap() {
		return bitMap;
	}
}
