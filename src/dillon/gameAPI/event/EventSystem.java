package dillon.gameAPI.event;

import java.util.ArrayList;

import dillon.gameAPI.modding.ModdingCore;

/**
 * This is the hub for all handlers and event broadcasting.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class EventSystem {
	private static ArrayList<EEHandler<?>> handlers = new ArrayList<EEHandler<?>>();

	/**
	 * Adds a handler to get events.
	 * 
	 * @param h
	 *            The handler.
	 */
	public static void addHandler(EEHandler<? extends EEvent> h) {
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
	 */
	public static void broadcastMessage(EEvent e) {
		for (int i = 0; i < handlers.size(); i++) {
			try {
				handlers.get(i).handle(e);
			} catch (Exception e2) {
			}
		}
		ModdingCore.sendEvent(e);
	}

	/**
	 * This is meant for the API so that if it crashes, it can shut off all
	 * handlers.
	 */
	public static void override() {
		handlers.clear();
	}
}
