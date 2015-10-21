package dillon.gameAPI.event;

/**
 * Indicates a shutdown of the engine.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class ShutdownEvent extends EEvent {
	@Override
	public String getType() {
		return "Shutdown";
	}

}
