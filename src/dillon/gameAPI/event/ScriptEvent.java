package dillon.gameAPI.event;

public class ScriptEvent extends EEvent {

	@Override
	public String getType() {
		return "Script";
	}

	private final int code; // The code that the script is sending.
	private final String[] metadata;

	public ScriptEvent(int code, String[] metadata) {
		this.code = code;
		this.metadata = metadata;
	}

	/**
	 * Get the event code.
	 * 
	 * @return The code.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Get any relevant meta data.
	 * 
	 * @return metadata.
	 */
	public String[] getMeta() {
		return metadata;
	}
}
