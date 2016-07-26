package dillon.gameAPI.mapping;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.errors.MapException;
import dillon.gameAPI.utils.MainUtilities;

/**
 * This class loads the map information and makes a map object.
 *
 * @author Dillon - Github dg092099
 * @since V2.2.0
 */
public class MapLoader {
	private static File extracted;
	private static Map map;
	public static final int VERSION = 2;

	/**
	 * Loads the map from the input stream.
	 *
	 * @param is
	 *            The stream.
	 * @return The map
	 */
	public static Map load(InputStream is) {
		map = new Map();
		extractFiles(is);
		if (!checkVersion()) {
			deleteDirectory(extracted);
			throw new MapException("The map you are trying to load is out of date.");
		}
		loadMusic();
		loadBackground();
		loadTilesheetImages();
		loadTilesheetMeta();
		loadTilesMeta();
		loadEventsMeta();
		loadScripts();
		deleteDirectory(extracted);
		map.render();
		return map;
	}

	/**
	 * Extracts the files for reading.
	 *
	 * @param is
	 *            The input stream.
	 */
	private static void extractFiles(InputStream is) {
		try {
			extracted = Files
					.createTempDirectory(
							"DGAPI-GAME-" + Core.getTitle().toUpperCase() + "-" + Long.toString(System.nanoTime()))
					.toFile();
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				File extractTo = new File(extracted.getAbsolutePath() + File.separator + ze.getName());
				extractTo.getParentFile().mkdirs();
				extractTo.createNewFile();
				byte[] buffer = new byte[1024];
				int len = 0;
				FileOutputStream fos = new FileOutputStream(extractTo);
				while ((len = zis.read(buffer, 0, 1024)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				zis.closeEntry();
			}
			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new MapException("The files could not be extracted.");
		}
	}

	/**
	 * Deletes the directory.
	 *
	 * @param f
	 *            The directory
	 */
	private static void deleteDirectory(File f) {
		if (!f.isDirectory()) {
			f.delete();
		} else {
			for (File f2 : f.listFiles()) {
				deleteDirectory(f2);
			}
			f.delete();
		}
	}

	/**
	 * Checks the version
	 *
	 * @return If it's correct.
	 */
	private static boolean checkVersion() {
		try {
			if (!MainUtilities.isFileInDirectory(extracted, "version.txt")) {
				throw new MapException("The map you have provided is corrupt.");
			}
			Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "version.txt"));
			if (s.hasNext()) {
				int ver = s.nextInt();
				s.close();
				return ver == VERSION;
			} else {
				s.close();
				return false;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			throw new MapException("The map version could not be checked.");
		}
	}

	/**
	 * Loads the music from the map.
	 */
	private static void loadMusic() {
		if (MainUtilities.isFileInDirectory(extracted, "music.au")) {
			File f;
			try {
				f = File.createTempFile(
						"DGAPI-GAME-" + Core.getTitle().toUpperCase() + "-" + Long.toString(System.nanoTime()), ".au");
				f.deleteOnExit();
				byte[] buffer = new byte[1024];
				int len = 0;
				FileInputStream fis = new FileInputStream(
						new File(extracted.getAbsolutePath() + File.separator + "music.au"));
				FileOutputStream fos = new FileOutputStream(f);
				while ((len = fis.read(buffer, 0, 1024)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				fis.close();
				map.setBackgroundMusic(f);
			} catch (IOException e) {
				e.printStackTrace();
				throw new MapException("The music could not be loaded.");
			}
		}
	}

	/**
	 * Loads the background image.
	 */
	private static void loadBackground() {
		if (MainUtilities.isFileInDirectory(extracted, "background.png")) {
			try {
				BufferedImage img = ImageIO
						.read(new File(extracted.getAbsolutePath() + File.separator + "background.png"));
				map.setBackground(img);
			} catch (IOException e) {
				e.printStackTrace();
				throw new MapException("The background image could not be loaded.");
			}
		}
	}

	private static HashMap<String, BufferedImage> tilesheets = new HashMap<String, BufferedImage>();

	/**
	 * Loads the tilesheet's images.
	 */
	private static void loadTilesheetImages() {
		if (MainUtilities.isFileInDirectory(extracted, "tilesheets")) {
			for (File f : new File(extracted.getAbsolutePath() + File.separator + "tilesheets").listFiles()) {
				try {
					String id = f.getName().split("\\Q.\\E")[0];
					BufferedImage img = ImageIO.read(f);
					tilesheets.put(id, img);
				} catch (IOException ex) {
					ex.printStackTrace();
					throw new MapException("Could not load tilesheets.");
				}
			}
		}
	}

	/**
	 * Loads the scripts.
	 */
	private static void loadScripts() {
		if (MainUtilities.isFileInDirectory(extracted, "scripts")) {
			for (File f : new File(extracted.getAbsolutePath() + File.separator + "scripts").listFiles()) {
				try {
					String content = "";
					Scanner s = new Scanner(f);
					while (s.hasNextLine()) {
						content += s.nextLine();
						content += "\n";
					}
					map.putScript(content);
					s.close();
				} catch (IOException ex) {
					ex.printStackTrace();
					throw new MapException("Could not load scripts.");
				}
			}
		}
	}

	/**
	 * Loads tilesheet information.
	 */
	private static void loadTilesheetMeta() {
		if (MainUtilities.isFileInDirectory(extracted, "tilesheets.info")) {
			try {
				Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "tilesheets.info"));
				int amt = s.nextInt();
				s.nextLine();
				for (int i = 0; i < amt; i++) {
					String desc = s.nextLine();
					String id = desc.split(":")[0];
					int width = Integer.parseInt(desc.split(":")[1]);
					int height = Integer.parseInt(desc.split(":")[2]);
					Tilesheet t = new Tilesheet();
					t.setId(id);
					t.setTileWidth(width);
					t.setTileHeight(height);
					t.setParentMap(map);
					if (!tilesheets.containsKey(id)) {
						s.close();
						throw new MapException("Missing tilesheet: " + id);
					} else {
						t.setImg(tilesheets.get(id));
						map.addTilesheet(t);
					}
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new MapException("The tilesheet data could not be loaded.");
			}
		}
	}

	/**
	 * Loads the tiles information.
	 */
	private static void loadTilesMeta() {
		if (MainUtilities.isFileInDirectory(extracted, "tiles.info")) {
			try {
				Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "tiles.info"));
				int amtTiles = s.nextInt();
				s.nextLine();
				for (int i = 0; i < amtTiles; i++) {
					String desc = s.nextLine();
					String[] parts = desc.split(":");
					String tilesheetID = parts[0];
					int xPos = Integer.parseInt(parts[1]);
					int yPos = Integer.parseInt(parts[2]);
					boolean visible = Boolean.parseBoolean(parts[3]);
					boolean solid = Boolean.parseBoolean(parts[4]);
					int sheetX = Integer.parseInt(parts[5]);
					int sheetY = Integer.parseInt(parts[6]);
					Tile t = new Tile();
					t.setTilesheetId(tilesheetID);
					t.setParentTilesheet(map.getTilesheetByName(tilesheetID));
					t.setxPos(xPos);
					t.setyPos(yPos);
					t.setVisible(visible);
					t.setSolid(solid);
					t.setSheetPosX(sheetX);
					t.setSheetPosY(sheetY);
					t.updateImage();
					map.addTile(t);
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new MapException("Unable to load tiles.");
			}
		}
	}

	/**
	 * Loads the tile event information.
	 */
	private static void loadEventsMeta() {
		if (MainUtilities.isFileInDirectory(extracted, "events.info")) {
			try {
				Scanner s = new Scanner(new File(extracted.getAbsolutePath() + File.separator + "events.info"));
				int amt = s.nextInt();
				s.nextLine();
				for (int i = 0; i < amt; i++) {
					String desc = s.nextLine();
					String[] parts = desc.split(":");
					TileEvent evt = new TileEvent();
					evt.setEventType(TileEvent.TileEventType.valueOf(parts[0]));
					int x = Integer.parseInt(parts[1]);
					int y = Integer.parseInt(parts[2]);
					String tilesheet = parts[3];
					map.setInUseTilesheet(tilesheet);
					Tile t = map.getTileAtPosition(x, y);
					evt.setAffectedTile(t);
					String entityType = parts[4];
					evt.setEntityType(entityType);
					String method = parts[5];
					evt.setMethod(method);
					map.addTileEvent(evt);
				}
				s.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new MapException("Could not load events.");
			}
		}
	}
}
