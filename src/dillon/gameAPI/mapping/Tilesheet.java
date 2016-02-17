package dillon.gameAPI.mapping;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * The holder of information for any tilesheet.
 *
 * @author Dillon - Github dg092099
 * @since V2.0
 *
 */
public class Tilesheet {
	private String id;
	private int tileWidth;
	private int tileHeight;
	private BufferedImage img;
	private Map parentMap;

	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage b) {
		img = b;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

	public int getTilesLeftToRight() {
		return img.getWidth() / tileWidth;
	}

	public int getTilesTopToBottom() {
		return img.getHeight() / tileHeight;
	}

	BufferedImage render;

	public BufferedImage render() {
		int width = 0;
		int height = 0;
		for (Tile t : parentMap.getTiles()) {
			if (t.getTilesheetId().equals(this.getId())) {
				if (parentMap.getGlobalLocationValue(t.getVarLocationX()) > width) {
					width = t.getxPos();
				}
				if (parentMap.getGlobalLocationValue(t.getVarLocationY()) > height) {
					height = t.getyPos();
				}
			}
		}
		width *= getTileWidth();
		height *= getTileHeight();
		width += getTileWidth();
		height += getTileHeight();
		render = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = render.createGraphics();
		for (Tile t : parentMap.getTiles()) {
			if (t.getTilesheetId().equals(this.getId())) {
				int realPosX = parentMap.getGlobalLocationValue(t.getVarLocationX()) * getTileWidth();
				int realPosY = parentMap.getGlobalLocationValue(t.getVarLocationY()) * getTileHeight();
				if (parentMap.getGlobalFlagValue(t.getVisibleVar())) {
					g.drawImage(t.getTile(), realPosX, realPosY, null);
				}
			}
		}
		g.dispose();
		return render;
	}

	public void setParentMap(Map m) {
		parentMap = m;
	}
}
