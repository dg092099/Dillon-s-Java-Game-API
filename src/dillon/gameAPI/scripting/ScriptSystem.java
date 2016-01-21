package dillon.gameAPI.scripting;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.scripting.bridges.CameraBridge;
import dillon.gameAPI.scripting.bridges.CoreBridge;
import dillon.gameAPI.scripting.bridges.EntityRegistry;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This class works with the scripts directly.
 *
 * @author Dillon - Github dg092099
 *
 */
public class ScriptSystem {
	private static boolean started = false;
	private static ScriptEngine scriptEngine;

	private static void initiate() {
		// Setup engine
		scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
		setup(null, null);
		Logger.getLogger("Scripting").severe("Javascript Engine active...");
		started = true;
	}

	private static void setup(SecurityKey key, HashMap<String, Object> obj) {
		// Put some variables for the API.
		scriptEngine.put("Core", new CoreBridge());
		scriptEngine.put("EntityRegistry", new EntityRegistry());
		scriptEngine.put("SecKey", key);
		if (obj != null) {
			for (String k : obj.keySet()) {
				scriptEngine.put(k, obj.get(k));
			}
		}
		scriptEngine.put("GuiFactory", Core.getGuiFactory());
		scriptEngine.put("Camera", new CameraBridge());
		scriptEngine.put("GuiSystem", Core.getGuiSystem());
		scriptEngine.put("RemoteCall", Core.getRemoteBridge());
	}

	public static void load(String code, SecurityKey runKey, SecurityKey providedKey,
			HashMap<String, Object> environment) {
		SecuritySystem.checkPermission(runKey, RequestedAction.RUN_SCRIPT);
		if (!started) {
			initiate();
		}
		setup(providedKey, environment);
		try {
			scriptEngine.eval(code);
		} catch (ScriptException e) {
			handleScriptEx(e);
		}
	}

	public static void invokeFunction(String name, Object... objs) {
		Invocable in = (Invocable) scriptEngine;
		try {
			in.invokeFunction(name, objs);
		} catch (NoSuchMethodException e) {
			Logger.getLogger("Scripting").severe("The called function: " + name + " doesn't exist.");
		} catch (ScriptException e) {
			handleScriptEx(e);
		}
	}

	private static void handleScriptEx(ScriptException ex) {
		Logger.getLogger("Scripting").severe("A script has errored out: " + ex.getFileName() + " on line: "
				+ ex.getLineNumber() + " with error: " + ex.getMessage());
	}
}
