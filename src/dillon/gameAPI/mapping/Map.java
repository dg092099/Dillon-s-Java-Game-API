package dillon.gameAPI.mapping;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class contains all of the information regarding how a map should be.
 *
 * @author Dillon - Github dg092099
 * @since V2.0
 *
 */
public class Map {
	private ArrayList<Tilesheet> tilesheets = new ArrayList<Tilesheet>();
	private BufferedImage backgroundImage;
	private File backgroundMusicFile;
	private ArrayList<Tile> tiles = new ArrayList<Tile>();
	private ArrayList<TileEvent> tileEvents = new ArrayList<TileEvent>();
	private Tilesheet inUse;

	/**
	 * Sets the background music.
	 *
	 * @param temp
	 *            The background music.
	 */
	public void setBackgroundMusic(File temp) {
		backgroundMusicFile = temp;
	}

	/**
	 * Sets the background.
	 *
	 * @param img
	 *            The background
	 */
	public void setBackground(BufferedImage img) {
		backgroundImage = img;
	}

	/**
	 * Adds a tilesheet.
	 *
	 * @param t
	 *            The tilesheet.
	 */
	public void addTilesheet(Tilesheet t) {
		if (t == null) {
			throw new IllegalArgumentException("The tilesheet must not be null.");
		}
		tilesheets.add(t);
	}

	/**
	 * Adds a tile to the map.
	 *
	 * @param t
	 *            The Tile
	 */
	public void addTile(Tile t) {
		if (t == null) {
			throw new IllegalArgumentException("The tile must not be null.");
		}
		tiles.add(t);
	}

	/**
	 * Auxiliary method for setting up other methods.
	 *
	 * @param id
	 *            The id
	 */
	public void setInUseTilesheet(String id) {
		for (Tilesheet t : tilesheets) {
			if (t.getId().equals(id)) {
				inUse = t;
				return;
			}
		}
		throw new RuntimeException("Missing tilesheet: " + id);
	}

	/**
	 * Gets the tile at the position using the tilesheet specified by
	 * setInUseTilesheet.
	 *
	 * @param x
	 *            X position
	 * @param y
	 *            Y position
	 * @return The tile.
	 */
	public Tile getTileAtPosition(int x, int y) {
		for (Tile t : tiles) {
			if (t.getxPos() == x && t.getyPos() == y && inUse.getId().equals(t.getTilesheetId())) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Adds a tile event to the map.
	 *
	 * @param evt
	 *            The event
	 */
	public void addTileEvent(TileEvent evt) {
		tileEvents.add(evt);
	}

	/**
	 * Retrieves the tilesheets.
	 *
	 * @return The tilesheet arraylist.
	 */
	public ArrayList<Tilesheet> getTilesheets() {
		return tilesheets;
	}

	/**
	 * Gets the tiles.
	 *
	 * @return The tiles arraylist.
	 */
	public ArrayList<Tile> getTiles() {
		return tiles;
	}

	/**
	 * Returns the background image.
	 *
	 * @return The image.
	 */
	public BufferedImage getBackgroundImage() {
		return backgroundImage;
	}

	/**
	 * Gets the background music file.
	 *
	 * @return The background music.
	 */
	public File getBackgroundMusicFile() {
		return backgroundMusicFile;
	}

	/**
	 * Gets the tilesheet by name.
	 *
	 * @param tilesheetId
	 *            The name
	 * @return The tilesheet.
	 */
	public Tilesheet getTilesheetByName(String tilesheetId) {
		for (Tilesheet t : tilesheets) {
			if (t.getId().equals(tilesheetId)) {
				return t;
			}
		}
		throw new RuntimeException("Missing tilesheet: " + tilesheetId);
	}

	HashMap<String, Integer> positionVariables = new HashMap<String, Integer>();
	HashMap<String, Boolean> flagVariables = new HashMap<String, Boolean>();

	/**
	 * Sets the global position variable to move tiles.
	 *
	 * @param name
	 *            The variable.
	 * @param value
	 *            The new value
	 * @param update
	 *            When true, causes re-render of map.
	 */
	public void setGlobalPositionVariable(String name, int value, boolean update) {
		if (name == null) {
			throw new IllegalArgumentException("The name can't be null.");
		}
		if (positionVariables.containsKey(name)) {
			positionVariables.remove(name);
		}
		positionVariables.put(name, value);
		if (update) {
			render();
		}
	}

	/**
	 * Similar to setGlobalPositionVariable, but uses the solid and visible
	 * flags.
	 *
	 * @param name
	 *            The name
	 * @param value
	 *            The new value
	 * @param update
	 *            Re-renders the map if true.
	 */
	public void setGlobalFlagVariable(String name, boolean value, boolean update) {
		if (name == null) {
			throw new IllegalArgumentException("The name cannot be null.");
		}
		if (flagVariables.containsKey(name)) {
			flagVariables.remove(name);
		}
		flagVariables.put(name, value);
		if (update) {
			render();
		}
	}

	private BufferedImage img;

	/**
	 * Causes the map to render.
	 */
	public void render() {
		ArrayList<BufferedImage> tilesheetImgs = new ArrayList<BufferedImage>();
		for (Tilesheet t : tilesheets) {
			BufferedImage render = t.render();
			tilesheetImgs.add(render);
		}
		int width = 0;
		int height = 0;
		for (BufferedImage img : tilesheetImgs) {
			if (img.getWidth() > width) {
				width = img.getWidth();
			}
			if (img.getHeight() > height) {
				height = img.getHeight();
			}
		}
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		for (BufferedImage img : tilesheetImgs) {
			g.drawImage(img, 0, 0, null);
		}
		g.dispose();
	}

	/**
	 * Gets the global location value.
	 *
	 * @param name
	 *            The name.
	 * @return The value
	 */
	public int getGlobalLocationValue(String name) {
		return positionVariables.get(name);
	}

	/**
	 * Gets the global flag value.
	 *
	 * @param name
	 *            The name.
	 * @return The value
	 */
	public boolean getGlobalFlagValue(String name) {
		return flagVariables.get(name);
	}

	/**
	 * Gets the rendering of the map.
	 *
	 * @return The render.
	 */
	public BufferedImage getRender() {
		return img;
	}

	/**
	 * Gets the tile events of the map.
	 *
	 * @return The tile events arraylist.
	 */
	public ArrayList<TileEvent> getTileEvents() {
		return tileEvents;
	}
}
