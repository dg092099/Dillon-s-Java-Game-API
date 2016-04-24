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
	private static volatile ArrayList<EEHandler<?>> handlers = new ArrayList<EEHandler<?>>(); // The
	// handlers
	// for
	// the
	// events.
	private static State currentState = null;
	private static transient volatile ArrayList<EEHandler<?>> toAdd = new ArrayList<EEHandler<?>>();
	private static transient volatile ArrayList<EEHandler<?>> toRemove = new ArrayList<EEHandler<?>>();

	/**
	 * Adds a handler to get events.
	 *
	 * @param h
	 *            The handler.
	 * @param k
	 *            The security key.
	 */
	public static void addHandler(EEHandler<? extends EEvent> h, SecurityKey k) {
		toAdd.add(h);
	}

	private static void addHandlerAfterWait(EEHandler<? extends EEvent> h, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.RECEIVE_EVENT);
		if (h == null) {
			throw new IllegalArgumentException("Event handler must not be null.");
		}
		int index = h.getPriority();
		if (handlers.isEmpty()) {
			handlers.add(h);
		} else {
			for (int i = 0; i < handlers.size(); i++) {
				if (handlers.get(i).getPriority() >= index) {
					handlers.add(i, h);
					break;
				}
			}
		}
	}

	/**
	 * Removes the handler.
	 *
	 * @param h
	 *            The handler
	 */
	public static void removeHandler(EEHandler<? extends EEvent> h) {
		if (h == null) {
			throw new IllegalArgumentException("Event handler must not be null.");
		}
		toRemove.add(h);
	}

	private static void removeAfterWait(EEHandler<? extends EEvent> h) {
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
		if (e == null) {
			throw new IllegalArgumentException("The event must not be null.");
		}
		if (c == null) {
			throw new IllegalArgumentException("The class must be specified.");
		}
		if (!toAdd.isEmpty()) {
			for (int i = 0; i < toAdd.size(); i++) {
				if (toAdd.get(i) != null) {
					addHandlerAfterWait(toAdd.get(i), k);
				}
			}
		}
		if (!toRemove.isEmpty()) {
			for (int i = 0; i < toRemove.size(); i++) {
				if (toRemove.get(i) != null) {
					removeAfterWait(toRemove.get(i));
				}
			}
		}
		if (currentState != null) {
			currentState.sendEvent(e);
		}
		for (int i = 0; i < handlers.size(); i++) { // For all handlers
			try {
				if (i < 0 || i > handlers.size() || handlers.get(i) == null) {
					continue;
				}
				EEHandler<? extends EEvent> h = handlers.get(i);
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

	public static void setState(State st) {
		currentState = st;
		st.initiate();
	}

	public static State getState() {
		return currentState;
	}
}
