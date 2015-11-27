package dillon.gameAPI.errors;

/**
 * Fires when an error with the API's security module occurs.
 *
 * @author Dillon - Github dg092099.github.io
 * @since V1.13
 *
 */
public class EngineSecurityError extends RuntimeException {
	private static final long serialVersionUID = 1843442884036558984L;

	public EngineSecurityError(String msg) {
		super(msg);
	}
}
