package dillon.gameAPI.errors;

/**
 * This is thrown when something happens that the system cannot load a
 * tilesheet.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class TileException extends RuntimeException {
	private static final long serialVersionUID = 8071017008348453283L;
	private int x, y;
	private String tilesheet;

	/**
	 * Instantiates a Tile Exception
	 * 
	 * @param msg
	 *            The message
	 * @param x
	 *            The x position of the tile.
	 * @param y
	 *            The y position of the tile.
	 * @param tilesheet
	 *            The tilesheet name.
	 */
	public TileException(String msg, int x, int y, String tilesheet) {
		super(msg);
		this.x = x;
		this.y = y;
		this.tilesheet = tilesheet;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the tilesheet
	 */
	public String getTilesheet() {
		return tilesheet;
	}

}
