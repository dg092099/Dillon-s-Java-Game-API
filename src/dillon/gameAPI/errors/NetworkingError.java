package dillon.gameAPI.errors;

/**
 * Occurs when there is a problem connecting to or setting up a network.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class NetworkingError extends Exception {
	private static final long serialVersionUID = 1223582718401179763L;

	public NetworkingError() {
		super();
	}

	public NetworkingError(String msg) {
		super(msg);
	}
}
