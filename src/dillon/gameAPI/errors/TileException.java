package dillon.gameAPI.errors;

public class TileException extends RuntimeException {
	private static final long serialVersionUID = 8071017008348453283L;
	private int x, y;
	private String tilesheet;

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
