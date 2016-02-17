package dillon.gameAPI.mapping;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.entity.Entity;
import dillon.gameAPI.errors.GeneralRuntimeException;
import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.MouseEngineEvent;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.event.TileEntityEvent;
import dillon.gameAPI.gui.GuiSystem;
import dillon.gameAPI.mapping.TileEvent.TileEventType;
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
	public static final int VERSION = 1;
	private static Map currentMap = null;

	/**
	 * Creates a map object from the file input stream.
	 *
	 * @param is
	 *            The input stream.
	 * @return The map
	 */
	public static Map derriveMapFromFile(InputStream is) {
		ZipInputStream zis = null;
		Scanner input = null;
		Map m = new Map();
		try {
			zis = new ZipInputStream(is);
			input = new Scanner(zis);
			ZipEntry ze = null;
			HashMap<String, BufferedImage> tilesheetsMaps = new HashMap<String, BufferedImage>();
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().equalsIgnoreCase("version.txt")) {
					int version = input.nextInt();
					if (version != VERSION) {
						input.close();
						throw new Exception("Invalid file.");
					}
				} else if (ze.getName().equalsIgnoreCase("music.au")) {
					File temp = File.createTempFile(Long.toString(new SecureRandom().nextLong()), ".au");
					temp.deleteOnExit();
					byte[] buffer = new byte[1024];
					int len = 0;
					FileOutputStream fos = new FileOutputStream(temp);
					while ((len = zis.read(buffer, 0, 1024)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					m.setBackgroundMusic(temp);
				} else if (ze.getName().equalsIgnoreCase("background.png")) {
					BufferedImage img = ImageIO.read(zis);
					m.setBackground(img);
					Core.setBackgroundImage(img, null);
				} else if (ze.getName().startsWith("tilesheets")) {
					String id = ze.getName().split("/")[1].split("\\Q.\\E")[0];
					BufferedImage img = ImageIO.read(zis);
					tilesheetsMaps.put(id, img);
				} else if (ze.getName().equals("data.txt")) {
					int numOfTilesheets = input.nextInt();
					input.nextLine();
					for (int i = 0; i < numOfTilesheets; i++) {
						int width = input.nextInt();
						int height = input.nextInt();
						input.nextLine();
						String id = input.nextLine();
						Tilesheet t = new Tilesheet();
						t.setId(id);
						t.setTileHeight(height);
						t.setTileWidth(width);
						t.setParentMap(m);
						m.addTilesheet(t);
					}

					int numTiles = input.nextInt();
					input.nextLine();
					for (int i = 0; i < numTiles; i++) {
						String tilesheetId = input.nextLine();
						int worldX = input.nextInt(), worldY = input.nextInt();
						boolean visible = input.nextBoolean(), solid = input.nextBoolean();
						input.nextLine();
						String varLocX = input.nextLine();
						String varLocY = input.nextLine();
						String varVisible = input.nextLine();
						String varSolid = input.nextLine();
						int sheetX = input.nextInt(), sheetY = input.nextInt();
						input.nextLine();
						Tile t = new Tile();
						t.setSheetPosX(sheetX);
						t.setSheetPosY(sheetY);
						t.setSolid(solid);
						if (varSolid.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setSolidVar(id);
							m.setGlobalFlagVariable(id, solid, false);
						} else {
							t.setSolidVar(varSolid);
							m.setGlobalFlagVariable(varLocX, solid, false);
						}
						t.setTilesheetId(tilesheetId);
						if (varLocX.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setVarLocationX(id);
							m.setGlobalPositionVariable(id, worldX, false);
						} else {
							t.setVarLocationX(varLocX);
							m.setGlobalPositionVariable(varLocX, worldX, false);
						}
						if (varLocY.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setVarLocationY(id);
							m.setGlobalPositionVariable(id, worldY, false);
						} else {
							t.setVarLocationY(varLocY);
							m.setGlobalPositionVariable(varLocY, worldY, false);
						}
						t.setVisible(visible);
						if (varVisible.isEmpty()) {
							String id = UUID.randomUUID().toString();
							t.setVisibleVar(id);
							m.setGlobalFlagVariable(id, visible, false);
						} else {
							t.setVisibleVar(varVisible);
							m.setGlobalFlagVariable(varVisible, visible, false);
						}
						t.setxPos(worldX);
						t.setyPos(worldY);
						m.addTile(t);
					}
					// Tile events
					int numEvents = input.nextInt();
					input.nextLine();
					for (int i = 0; i < numEvents; i++) {
						TileEvent.TileEventType type = TileEvent.TileEventType.valueOf(input.nextLine());
						int x = input.nextInt();
						int y = input.nextInt();
						input.nextLine();
						String id = input.nextLine();
						String entType = input.nextLine();
						TileEvent evt = new TileEvent();
						evt.setEntityType(entType);
						evt.setEventType(type);
						m.setInUseTilesheet(id);
						evt.setAffectedTile(m.getTileAtPosition(x, y));
						m.addTileEvent(evt);
					}
				}
				zis.closeEntry();
			}
			for (Tilesheet t : m.getTilesheets()) {
				if (!tilesheetsMaps.containsKey(t.getId())) {
					System.out.println("Broken tilesheet: " + t.getId());
				} else {
					t.setImg(tilesheetsMaps.get(t.getId()));
				}
			}
			for (Tile t : m.getTiles()) {
				t.setParentTilesheet(m.getTilesheetByName(t.getTilesheetId()));
				t.updateImage();
			}
			m.render();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (zis != null) {
					zis.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return m;
	}

	private static PlayableSound backgroundMusic;

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
		EventSystem.addHandler(new EEHandler<RenderEvent>() {
			@Override
			public void handle(RenderEvent evt) {
				if (currentMap != null) {
					evt.getGraphics().drawImage(currentMap.getRender(), 0 - Camera.getXPos(), 0 - Camera.getYPos(),
							null);
				}
			}
		}, key);
		EventSystem.addHandler(new EEHandler<MouseEngineEvent>() {
			@Override
			public void handle(MouseEngineEvent evt) {
				if (GuiSystem.getActiveComponent() != -1) {
					return;
				}
				if (currentMap == null) {
					return;
				}
				if (evt.getMouseMode().equals(MouseEngineEvent.MouseMode.CLICK)
						|| evt.getMouseMode().equals(MouseEngineEvent.MouseMode.HOLD)) {
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
									EventSystem.broadcastMessage(
											new TileEntityEvent(event.getAffectedTile(), TileEventType.CLICK),
											TileEntityEvent.class, null);
								}
							}
						}
					}
				}
			}
		}, null);

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
		for (TileEvent evt : currentMap.getTileEvents()) {
			if (evt.getAffectedTile().equals(t)) {
				if (e.getType().equals(evt.getEntityType()) || evt.getEntityType().isEmpty()) {
					EventSystem.broadcastMessage(new TileEntityEvent(t, TileEventType.TOUCH), TileEntityEvent.class,
							null);
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

}
