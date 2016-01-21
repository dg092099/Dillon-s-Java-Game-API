package dillon.gameAPI.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.logging.Logger;

import dillon.gameAPI.errors.EngineSecurityError;

/**
 * This class keeps track of all sensitive actions and stops them if necessary.
 *
 * @author Dillon - Github dg092099.github.io
 * @since V1.13
 */
public class SecuritySystem {
	private static SecurityKey engineKey;
	private static boolean masterKeyRetrieved = false;
	private static PrivateKey signing;
	private static PublicKey verifying;
	private static SecurityKey gameKey;
	private static boolean gameKeyRetrived = false;

	/**
	 * Used internally to activate module.
	 *
	 * @return The engine master key.
	 */
	public static SecurityKey init() {
		if (masterKeyRetrieved) {
			throw new EngineSecurityError("The security system was already initialized.");
		}
		try {
			// Generate signing keys
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
			kpg.initialize(1024);
			KeyPair kp = kpg.generateKeyPair();
			signing = kp.getPrivate();
			verifying = kp.getPublic();
			// Create engine and game keys, which supersede all checks.
			engineKey = new SecurityKey("Engine Master Key", signing);
			gameKey = new SecurityKey("Game Master Key", signing);
			masterKeyRetrieved = true;
		} catch (Exception e) {
			throw new EngineSecurityError("Security System integrety compromised.");
		}
		return engineKey;
	}

	/**
	 * Gets the game's master key. Can be called only once. Even if you don't
	 * use the security features, still get the key to lockout this method, so
	 * someone else can't.
	 *
	 * @return The key.
	 */
	public static SecurityKey getGameKey() {
		if (gameKeyRetrived) {
			throw new EngineSecurityError("Key already retrieved.");
		}
		gameKeyRetrived = true;
		active = true;
		return gameKey;
	}

	private static boolean active = false;

	/**
	 * Called internally to check the permissions.
	 *
	 * @param k
	 *            The key given.
	 * @param ra
	 *            The action requested to execute.
	 */
	public static void checkPermission(SecurityKey k, RequestedAction ra) {
		if (k == null) {
			if (active) {
				throw new EngineSecurityError("A key must be provided.");
			} else {
				return;
			}
		}
		if (!active) {
			return;
		}
		if (!verify(k)) {
			throw new EngineSecurityError("The key given is forged incorrectly.");
		}
		if (k.equals(engineKey)) {
			return;
		}
		for (engineSecurityHandler h : engineSecurityHandlers) {
			if (!h.allow(k, ra)) {
				Logger.getLogger("Security")
						.severe("Security Violation Key: " + k.getDescription() + " action: " + ra.toString());
				throw new EngineSecurityError("Security violation: Key: " + k.getKey() + " action: " + ra.toString());
			}
		}
	}

	/**
	 * This method verifies the keys.
	 *
	 * @param s
	 *            The key
	 * @return Validity.
	 */
	private static boolean verify(SecurityKey s) {
		try {
			byte[] key = s.getKey();
			byte[] sig = s.getSignature();
			Signature verifier = Signature.getInstance("SHA1withDSA");
			verifier.initVerify(verifying);
			verifier.update(key);
			verifier.update(s.getDescription().getBytes("UTF-8"));
			return verifier.verify(sig);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EngineSecurityError("Security System compromised.");
		}
	}

	/**
	 * The engine security handler, registered engine security handlers would be
	 * called when a sensitive action is requested to run.
	 *
	 * @author Dillon - Github dg092099.github.io
	 *
	 */
	public static interface engineSecurityHandler {
		public boolean allow(SecurityKey k, RequestedAction a);
	}

	private final static ArrayList<engineSecurityHandler> engineSecurityHandlers = new ArrayList<engineSecurityHandler>();

	/**
	 * This adds a engine security handler to the system. The key must be the
	 * engine or game key.
	 *
	 * @param h
	 *            The handler.
	 * @param k
	 *            The key
	 */
	public static void addHandler(engineSecurityHandler h, SecurityKey k) {
		if ((k.equals(engineKey) || k.equals(gameKey)) && verify(k)) {
			engineSecurityHandlers.add(h);
			active = true;
		} else {
			throw new EngineSecurityError("Given key must be the engine key or game key.");
		}
	}

	/**
	 * This disables the security measures. Must use the game key.
	 *
	 * @param k
	 *            The key.
	 */
	public static void disableSecurity(SecurityKey k) {
		if ((k.equals(engineKey) || k.equals(gameKey)) && verify(k)) {
			active = false;
		} else {
			throw new EngineSecurityError("Given key must be the engine or game keys.");
		}
	}

	/**
	 * This enables the security measures. Must use the game key.
	 *
	 * @param k
	 *            The key
	 */
	public static void enableSecurity(SecurityKey k) {
		if ((k.equals(engineKey) || k.equals(gameKey)) && verify(k)) {
			active = true;
		} else {
			throw new EngineSecurityError("Given key must be the engine or game key.");
		}
	}

	/**
	 * This creates a valid security key.
	 *
	 * @param k
	 *            The security key as a security measure.
	 * @param description
	 *            The description.
	 * @return A key
	 */
	public static SecurityKey createSecurityKey(SecurityKey k, String description) {
		checkPermission(k, RequestedAction.CREATE_SECURITY_KEY);
		return new SecurityKey(description, signing);
	}

	public static boolean isEngineKey(SecurityKey k) {
		if (k.equals(engineKey) && verify(k)) {
			return true;
		}
		return false;
	}
}
