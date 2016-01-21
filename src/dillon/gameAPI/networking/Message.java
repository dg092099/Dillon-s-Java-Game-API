package dillon.gameAPI.networking;

import java.io.Serializable;

/**
 * This object is an object that holds the information for an exchange in the
 * program.
 *
 * @author Dillon - Github dg092099
 *
 */
public class Message implements Serializable {
	private static final long serialVersionUID = -5648651L;
	private String message; // The message.
	private String originator; // Where it came from.
	private transient String originIP; // The ip it came from.

	/**
	 * This creates a message
	 *
	 * @param msg
	 *            The string representing the message.
	 * @param name
	 *            The originator of the message.
	 */
	public Message(String msg, String name) {
		message = msg;
		originator = name;
	}

	/**
	 * Sets the message.
	 *
	 * @param msg
	 *            The message.
	 */
	public void setMessage(String msg) {
		message = msg;
	}

	/**
	 * Gives the message.
	 *
	 * @return The message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * This will return who created the object.
	 *
	 * @return The originator.
	 */
	public String getOriginator() {
		return originator;
	}

	/**
	 * Used to find the ip of the client that created this message.
	 *
	 * @return its IP
	 */
	public String getIP() {
		return originIP;
	}

	/**
	 * Internally used only.
	 *
	 * @param ip
	 *            The ip to set.
	 */
	public void setIP(String ip) {
		originIP = ip;
	}
}
