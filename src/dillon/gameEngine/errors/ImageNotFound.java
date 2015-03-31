package dillon.gameEngine.errors;

/**
 * This is a custom error that means that an image couldn't be found.
 * 
 * @author Dillon Geier
 */
public class ImageNotFound extends Exception {
	private static final long serialVersionUID = 388190623014234629L;

	public ImageNotFound() {
		super();
	}

	public ImageNotFound(String msg) {
		super(msg);
	}
}
