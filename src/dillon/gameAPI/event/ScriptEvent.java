package dillon.gameAPI.event;

public class ScriptEvent extends EEvent {

	@Override
	public String getType() {
		return "Script";
	}

	private final int code;
	private final String[] metadata;

	public ScriptEvent(int code, String[] metadata) {
		this.code = code;
		this.metadata = metadata;
	}

	public int getCode() {
		return code;
	}

	public String[] getMeta() {
		return metadata;
	}
}
