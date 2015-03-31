package dillon.gameEngine.event;

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

	@Override
	public Object[] getMetadata() {
		return null;
	}

}
