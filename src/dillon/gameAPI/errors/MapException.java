package dillon.gameAPI.errors;

/**
 * An error when parsing the map file.
 * 
 * @author Dillon - Github dg092099
 * @since V2.2.0
 */
public class MapException extends RuntimeException {
	private static final long serialVersionUID = -2374784948632896464L;

	public MapException(String msg) {
		super(msg);
	}
}
