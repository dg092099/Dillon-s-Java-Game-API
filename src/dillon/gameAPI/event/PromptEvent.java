package dillon.gameAPI.event;

/**
 * Fires when a prompt is answered. Metadata: message, id
 * 
 * @author Dillon - Github dg092099
 *
 */
public class PromptEvent extends EEvent {
	@Override
	public String getType() {
		return "Prompt finished";
	}

	private final String msg; // The data given.
	private final long id; // The id of the prompt.

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Initializes a prompt event.
	 * 
	 * @param msg
	 *            The message
	 * @param id
	 *            The id number.
	 */
	public PromptEvent(String msg, long id) {
		this.msg = msg;
		this.id = id;
	}

}
