package dillon.gameAPI.security;

import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.UUID;

import dillon.gameAPI.errors.EngineSecurityError;

/**
 * A security key for allowing a sensitive action.
 *
 * @author Dillon - Github dg092099.github.io
 * @since V1.13
 */
public class SecurityKey {
	private final byte[] key = new byte[1024]; // The random key
	private byte[] signature = new byte[1024]; // A signed checksum
	private String desc;
	private String id;

	/**
	 * Internally used.
	 *
	 * @param d
	 *            The description.
	 * @param signing
	 *            The signing DSA key.
	 */
	public SecurityKey(String d, PrivateKey signing) {
		try {
			desc = d;
			// Generate key
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(key);
			// Sign key
			Signature sig = Signature.getInstance("SHA1withDSA");
			sig.initSign(signing);
			sig.update(key);
			sig.update(desc.getBytes("UTF-8"));
			signature = sig.sign();
			id = UUID.randomUUID().toString();
		} catch (Exception e) {
			throw new EngineSecurityError("Security Key Compromised");
		}
	}

	/**
	 * Gets the data for the key.
	 *
	 * @return Data.
	 */
	public byte[] getKey() {
		return key;
	}

	/**
	 * Gets the signature of the key.
	 *
	 * @return signature
	 */
	public byte[] getSignature() {
		return signature;
	}

	/**
	 * Gets the description of the key.
	 *
	 * @return Description
	 */
	public String getDescription() {
		return desc;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SecurityKey)) {
			return false;
		}
		return id.equals(((SecurityKey) o).id);
	}
}
