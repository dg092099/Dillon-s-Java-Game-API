package dillon.gameAPI.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

/**
 * The State class can be used to target handlers for use of game states.
 *
 * @author Dillon - Github dg092099
 * @since V2.1.0
 */
public abstract class State {
	private volatile ArrayList<EEHandler<? extends EEvent>> handlers = new ArrayList<EEHandler<? extends EEvent>>();
	private final String identifier;

	public State() {
		identifier = UUID.randomUUID().toString();
	}

	/**
	 * To check if two states are the same. Only uses identifier.
	 *
	 * @param other
	 *            The other state object.
	 * @return Equality
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof State) {
			return identifier.equals(((State) other).identifier);
		} else {
			return false;
		}
	}

	private boolean ready = false;

	public void setReady(boolean r) {
		ready = r;
	}

	/**
	 * Sends an event to subscribed handlers
	 *
	 * @param e
	 *            The events.
	 */
	public void sendEvent(EEvent e) {
		if (!ready) {
			return;
		}
		if (e == null) {
			throw new IllegalArgumentException("The event must not be null.");
		}
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
	}

	/**
	 * Subscribe a handler to the state.
	 *
	 * @param h
	 *            The handler
	 */
	public void addHandler(EEHandler<? extends EEvent> h) {
		if (h == null) {
			throw new IllegalArgumentException("The handler must not be null.");
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
	 * Remove a handler from the state.
	 *
	 * @param h
	 *            The handler
	 */
	public void removeHandler(EEHandler<? extends EEvent> h) {
		if (h == null) {
			throw new IllegalArgumentException("The handler must not be null.");
		}
		handlers.remove(h);
	}

	/**
	 * Sets up the state.
	 */
	public abstract void initiate();
}
