package dillon.gameEngine.errors;

/**
 * This method is called if there is a problem with the tile or scroll manager.
 * 
 * @author Dillon Geier
 *
 */
public class RenderingError extends Exception {
	private static final long serialVersionUID = -7739829216772644009L;

	public RenderingError() {
		super();
	}

	public RenderingError(String msg) {
		super(msg);
	}
}
