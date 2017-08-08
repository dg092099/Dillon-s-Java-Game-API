package dillon.gameAPI.mapping;

import java.awt.Rectangle;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.entity.Entity;
import dillon.gameAPI.errors.GeneralRuntimeException;
import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.MouseEngineEvent;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.gui.BlackoutImage;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.sound.PlayableSound;
import dillon.gameAPI.sound.SoundSystem;

/**
 * The class that manages all of the mapping package.
 *
 * @author Dillon - Github dg092099
 * @since V2.0
 *
 */
public class MapManager {
	public static final int VERSION = 2;
	private static Map currentMap = null;

	private static PlayableSound backgroundMusic;

	/**
	 * This will load the map in the input stream.
	 *
	 * @param is
	 *            The input stream.
	 * @return The map object.
	 */
	public static Map loadMap(InputStream is) {
		BlackoutImage blimg = null;
		try {
			blimg = new BlackoutImage(false, ImageIO.read(MapManager.class.getClassLoader()
					.getResourceAsStream("dillon/gameAPI/res/MapTransitionDefault.png")), null);
			GuiSystem.startGui(blimg, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map m = MapLoader.load(is);
		loadMap(m);
		if (blimg != null) {
			GuiSystem.removeGui(blimg, null);
		}
		return m;
	}

	/**
	 * Sets the current map.
	 *
	 * @param m
	 *            The map
	 */
	public static void loadMap(Map m) {
		currentMap = m;
		if (m.getBackgroundImage() != null) {
			Core.setBackgroundImage(m.getBackgroundImage(), key);
		}
		if (backgroundMusic != null) {
			SoundSystem.stopSound(backgroundMusic, key);
		}
		if (m.getBackgroundMusicFile() != null) {
			try {
				backgroundMusic = new PlayableSound(
						new BufferedInputStream(new FileInputStream(m.getBackgroundMusicFile())), true);
				SoundSystem.playSound(backgroundMusic, key);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This causes the game to unload the current map.
	 */
	public static void unloadMap() {
		currentMap = null;
		Core.setBackgroundImage(null, null);
		if (backgroundMusic != null) {
			SoundSystem.stopSound(backgroundMusic, null);
			backgroundMusic = null;
		}
	}

	private static boolean initialized = false;
	private static SecurityKey key;

	/**
	 * Internal use only.
	 *
	 * @param engineKey
	 *            The key
	 */
	public static void initiate(SecurityKey engineKey) {
		if (initialized) {
			throw new GeneralRuntimeException("The mapping system is already initialized.");
		}
		key = engineKey;
		initialized = true;
		EventSystem.addHandlerDirectly(new EEHandler<RenderEvent>() {
			@Override
			public void handle(RenderEvent evt) {
				if (currentMap != null) {
					evt.getGraphics().drawImage(currentMap.getRender(), 0 - Camera.getXPos(), 0 - Camera.getYPos(),
							null);
				}
			}

			@Override
			public int getPriority() {
				return 10;
			}
		});
		EventSystem.addHandlerDirectly(new EEHandler<MouseEngineEvent>() {
			@Override
			public void handle(MouseEngineEvent evt) {
				if (GuiSystem.getActiveComponent() != -1) {
					return;
				}
				if (currentMap == null) {
					return;
				}
				if (evt.getMouseMode().equals(MouseEngineEvent.MouseMode.HOLD)) {
					int locationX = (int) (evt.getLocation().getX() + Camera.getXPos());
					int locationY = (int) (evt.getLocation().getY() + Camera.getYPos());
					for (TileEvent event : currentMap.getTileEvents()) {
						if (event.getEventType().equals(TileEvent.TileEventType.CLICK)) {
							Tilesheet tilesheet = currentMap
									.getTilesheetByName(event.getAffectedTile().getTilesheetId());
							int realX = tilesheet.getTileWidth() * event.getAffectedTile().getxPos();
							int realY = tilesheet.getTileHeight() * event.getAffectedTile().getyPos();
							if (locationX >= realX && locationX <= realX + tilesheet.getTileWidth()) {
								if (locationY >= realY && locationY <= realY + tilesheet.getTileHeight()) {
									currentMap.invokeScriptMethod(event.getMethod());
								}
							}
						}
					}
				}
			}

			@Override
			public int getPriority() {
				return 10;
			}
		});

	}

	/**
	 * Returns the loaded map.
	 *
	 * @return The map.
	 */
	public static Map getLoadedMap() {
		return currentMap;
	}

	/**
	 * Gets a collision with any tile.
	 *
	 * @param e
	 *            The entity
	 * @return If it collides with a tile.
	 */
	public static boolean getCollisionAny(Entity e) {
		if (e == null) {
			throw new IllegalArgumentException("Entity must not be null.");
		}
		int x = (int) e.getX(), y = (int) e.getY();
		int width = e.getWidth(), height = e.getHeight();
		Rectangle r1 = new Rectangle(x, y, width, height);
		for (Tile t : currentMap.getTiles()) {
			if (!t.isSolid()) {
				continue;
			}
			Tilesheet tilesheet = currentMap.getTilesheetByName(t.getTilesheetId());
			int locationX = tilesheet.getTileWidth() * t.getxPos();
			int locationY = tilesheet.getTileHeight() * t.getyPos();
			Rectangle r2 = new Rectangle(locationX, locationY, tilesheet.getTileWidth(), tilesheet.getTileHeight());
			if (r1.intersects(r2)) {
				fireTouch(t, e);
				return true;
			}
		}
		return false;
	}

	/**
	 * Fires the touch method if possible.
	 *
	 * @param t
	 *            The tile
	 * @param e
	 *            The entity
	 */
	private static void fireTouch(Tile t, Entity e) {
		if (t == null) {
			throw new IllegalArgumentException("Tile must not be null.");
		}
		if (e == null) {
			throw new IllegalArgumentException("Entity must not be null.");
		}
		for (TileEvent evt : currentMap.getTileEvents()) {
			if (evt.getAffectedTile().equals(t)) {
				if (e.getType().equals(evt.getEntityType()) || evt.getEntityType().isEmpty()) {
					currentMap.invokeScriptMethod(evt.getMethod());
				}
			}
		}
	}

	/**
	 * Gets relational movement collision.
	 *
	 * @param e
	 *            entity
	 * @param relX
	 *            relative X
	 * @param relY
	 *            relative Y
	 * @return collision
	 */
	public static boolean getCollisionPos(Entity e, double relX, double relY) {
		if (e == null) {
			throw new IllegalArgumentException("Entity must not be null.");
		}
		int x = (int) e.getX() + (int) relX, y = (int) e.getY() + (int) relY;
		int width = e.getWidth(), height = e.getHeight();
		Rectangle r1 = new Rectangle(x, y, width, height);
		for (Tile t : currentMap.getTiles()) {
			if (!t.isSolid()) {
				continue;
			}
			Tilesheet tilesheet = currentMap.getTilesheetByName(t.getTilesheetId());
			int locationX = tilesheet.getTileWidth() * t.getxPos();
			int locationY = tilesheet.getTileHeight() * t.getyPos();
			Rectangle r2 = new Rectangle(locationX, locationY, tilesheet.getTileWidth(), tilesheet.getTileHeight());
			if (r2.intersects(r1)) {
				fireTouch(t, e);
				return true;
			}
		}
		return false;
	}

	public static String getDebug() {
		String str = "\n\ndillon.gameAPI.mapping.MapManager\n";
		str += String.format("%-10s %-5s\n", "Key", "Value");
		str += String.format("%-10s %-5d\n", "Version", VERSION);
		str += String.format("%-10s %-5s\n", "Initialized", initialized ? "Yes" : "No");
		if (currentMap != null) {
			str += "Current Map:\n";
			str += currentMap.toString();
			str += "\n";
		}
		if (backgroundMusic != null) {
			str += "Background music:\n";
			str += backgroundMusic.toString();
		}
		return str;
	}
}
