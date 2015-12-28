package dillon.gameAPI.scripting.bridges;

import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.ScriptEvent;
import dillon.gameAPI.security.SecurityKey;

/**
 * Scripting: Call "RemoteCall"
 * 
 * @author Dillon
 *
 */
public class RemoteCallBridge {
	private SecurityKey key;

	public RemoteCallBridge(SecurityKey k) {
		key = k;
	}

	public void call(int code, String... metadata) {
		EventSystem.broadcastMessage(new ScriptEvent(code, metadata), ScriptEvent.class, key);
	}
}
