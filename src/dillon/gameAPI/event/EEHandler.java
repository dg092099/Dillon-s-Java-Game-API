package dillon.gameAPI.event;

/**
 * This is a handler for any engine event that comes by.
 *
 * @author Dillon - Github dg092099
 *
 * @param <T>
 *            The event to catch
 */
public interface EEHandler<T extends EEvent> {
	/**
	 * Fires when an event occurs depending on your situation.
	 *
	 * @param evt
	 *            Event
	 */
	public void handle(T evt);
}
