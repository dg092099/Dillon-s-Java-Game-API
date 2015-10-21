package dillon.gameAPI.errors;

/**
 * Occurs when a general runtime exception occurs.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class GeneralRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -8653662429249877175L;

	public GeneralRuntimeException(String msg) {
		super(msg);
	}

}
