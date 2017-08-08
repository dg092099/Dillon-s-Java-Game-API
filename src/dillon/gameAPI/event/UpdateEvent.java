package dillon.gameAPI.event;

/**
 * Indicates when everything should update.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class UpdateEvent extends EEvent {

	@Override
	public String getType() {
		return "Tick";
	}

}
