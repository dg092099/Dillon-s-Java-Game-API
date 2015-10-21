package dillon.gameAPI.event;

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

}
