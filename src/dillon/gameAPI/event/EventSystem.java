package dillon.gameAPI.event;

import java.lang.reflect.Method;
import java.util.ArrayList;

import dillon.gameAPI.modding.ModdingCore;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This is the hub for all handlers and event broadcasting.
 *
 * @author Dillon - Github dg092099
 *
 */
public class EventSystem {
	private static ArrayList<EEHandler<?>> handlers = new ArrayList<EEHandler<?>>(); // The
																						// handlers
																						// for
																						// the
																						// events.

	/**
	 * Adds a handler to get events.
	 *
	 * @param h
	 *            The handler.
	 * @param k
	 *            The security key.
	 */
	public static void addHandler(EEHandler<? extends EEvent> h, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.RECEIVE_EVENT);
		handlers.add(h);
	}

	/**
	 * Removes the handler.
	 *
	 * @param h
	 *            The handler
	 */
	public static void removeHandler(EEHandler<? extends EEvent> h) {
		handlers.remove(h);
	}

	/**
	 * Sets the array of handlers. Internal use only.
	 *
	 * @param s
	 *            Handler arraylist
	 */
	public static void setHandlers(ArrayList<EEHandler<?>> s) {
		handlers = s;
	}

	/**
	 * Gets the array list of handlers.
	 *
	 * @return The handlers.
	 */
	public static ArrayList<EEHandler<?>> getHandlers() {
		return handlers;
	}

	/**
	 * This will broadcast a message through all handlers.
	 *
	 * @param e
	 *            The event.
	 * @param c
	 *            The event class.
	 * @param k
	 *            The security key.
	 */
	public static void broadcastMessage(EEvent e, Class<? extends EEvent> c, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.POST_EVENT); // Security
																		// check.
		for (EEHandler<?> h : handlers) { // For all handlers
			try {
				// Get method to call.
				Method m = h.getClass().getMethod("handle", e.getClass());
				m.setAccessible(true);
				// Invoke it.
				m.invoke(h, e);
			} catch (Exception ex) {
			}
		}
		ModdingCore.sendEvent(e); // Send modding module event.
	}

	/**
	 * This is meant for the API so that if it crashes, it can shut off all
	 * handlers.
	 */
	public static void override() {
		handlers.clear();
	}

	/**
	 * Returns the Debugging string for the event system.
	 * 
	 * @return The debug information
	 */
	public static String getDebug() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n dillon.gameAPI.event.EventSystem Dump:\n");
		String data = "";
		data += String.format("%-15s %-15s\n", "Key", "Value");
		data += String.format("%-15s %-15s\n", "---", "-----");
		for (EEHandler<?> h : handlers) {
			data += String.format("%-15s %-15s\n", "Handler code:", h.hashCode());
			data += String.format("%-15s %-15s\n", "Handled event:", h.getClass().getName());
			data += "-------\n";
		}
		sb.append(data);
		return sb.toString();
	}
}
