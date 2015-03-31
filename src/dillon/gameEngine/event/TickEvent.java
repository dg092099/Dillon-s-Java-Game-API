package dillon.gameEngine.event;

/**
 * Indicates when everything should tick.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class TickEvent extends EEvent {

	@Override
	public String getType() {
		return "Tick";
	}

	@Override
	public Object[] getMetadata() {
		return null;
	}

}
