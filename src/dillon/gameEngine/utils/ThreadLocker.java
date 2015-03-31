package dillon.gameEngine.utils;

import java.security.SecureRandom;

/**
 * This acts as a mechanic to wait for a response from the engine.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class ThreadLocker {
	@SuppressWarnings("unused")
	private final long lock;

	public ThreadLocker() {
		SecureRandom sr = new SecureRandom();
		lock = sr.nextLong();
	}

	/**
	 * This will lock this object and wait for the engine to unlock it.
	 */
	public synchronized void lock() {
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unlocks this object. Used internally only.
	 */
	public synchronized void unlock() {
		this.notifyAll();
	}
}
