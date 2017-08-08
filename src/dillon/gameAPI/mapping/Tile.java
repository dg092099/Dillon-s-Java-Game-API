package dillon.gameAPI.mapping;

import java.awt.image.BufferedImage;

/**
 * The tile information.
 *
 * @author Dillon - Github dg092099
 * @since V2.0
 */
public class Tile {
	private int xPos = 0, yPos = 0; // World
	private String tilesheetId;
	private boolean visible = true, solid = true;
	private String varLocationX = "", varLocationY = ""; // World variables
	private String visibleVar = "", solidVar = "";
	private int sheetPosX = -1, sheetPosY = -1; // Tilesheet
	private BufferedImage img;
	private Tilesheet parentTilesheet;

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public String getTilesheetId() {
		return tilesheetId;
	}

	public void setTilesheetId(String tilesheetId) {
		this.tilesheetId = tilesheetId;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	public String getVarLocationX() {
		return varLocationX;
	}

	public void setVarLocationX(String varLocationX) {
		this.varLocationX = varLocationX;
	}

	public String getVarLocationY() {
		return varLocationY;
	}

	public void setVarLocationY(String varLocationY) {
		this.varLocationY = varLocationY;
	}

	public String getVisibleVar() {
		return visibleVar;
	}

	public void setVisibleVar(String visibleVar) {
		this.visibleVar = visibleVar;
	}

	public String getSolidVar() {
		return solidVar;
	}

	public void setSolidVar(String solidVar) {
		this.solidVar = solidVar;
	}

	public int getSheetPosX() {
		return sheetPosX;
	}

	public void setSheetPosX(int sheetPosX) {
		this.sheetPosX = sheetPosX;
	}

	public int getSheetPosY() {
		return sheetPosY;
	}

	public void setSheetPosY(int sheetPosY) {
		this.sheetPosY = sheetPosY;
	}

	/**
	 * Makes a copy of the tile with the specified places.
	 *
	 * @param xPos
	 *            The x position.
	 * @param yPos
	 *            The y position.
	 * @return The new tile.
	 */
	public Tile copy(int xPos, int yPos) {
		Tile copy = new Tile();
		copy.sheetPosX = this.sheetPosX;
		copy.sheetPosY = this.sheetPosY;
		copy.solid = this.solid;
		copy.solidVar = this.solidVar;
		copy.tilesheetId = this.tilesheetId;
		copy.varLocationX = this.varLocationX;
		copy.varLocationY = this.varLocationY;
		copy.visible = this.visible;
		copy.visibleVar = this.visibleVar;
		copy.xPos = xPos;
		copy.yPos = yPos;
		copy.img = img;
		copy.updateImage();
		return copy;
	}

	/**
	 * Returns the tile.
	 *
	 * @return The tile.
	 */
	public BufferedImage getTile() {
		if (img == null) {
			System.out.println("Image null");
		}
		return img;
	}

	/**
	 * Recalculates the image.
	 */
	public void updateImage() {
		try {
			if (sheetPosX > -1 && sheetPosY > -1 && tilesheetId != null) {
				BufferedImage fullImg = parentTilesheet.getImg();
				if (fullImg == null) {
					return;
				}
				BufferedImage tileImg = fullImg.getSubimage(sheetPosX * parentTilesheet.getTileWidth(),
						sheetPosY * parentTilesheet.getTileHeight(), parentTilesheet.getTileWidth(),
						parentTilesheet.getTileHeight());
				img = tileImg;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setParentTilesheet(Tilesheet parentTilesheet) {
		this.parentTilesheet = parentTilesheet;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tile)) {
			return false;
		}
		Tile oth = (Tile) other;
		if (oth.sheetPosX == this.sheetPosX && oth.sheetPosY == this.sheetPosY && oth.xPos == this.xPos
				&& oth.yPos == this.yPos && oth.getTilesheetId() == this.getTilesheetId()) {
			return true;
		}
		return false;
	}
}
