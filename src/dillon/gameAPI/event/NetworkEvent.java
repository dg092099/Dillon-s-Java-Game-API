package dillon.gameAPI.event;

import dillon.gameAPI.networking.ClientConnector;
import dillon.gameAPI.networking.Message;

/**
 * Fires when a network-related event occurs. Metadata: mode, connector, and
 * message
 *
 * @author Dillon - Github dg092099
 *
 */
public class NetworkEvent extends EEvent {
	@Override
	public String getType() {
		return "Network";
	}

	private final NetworkMode mode;
	private final ClientConnector connector;
	private final Message message;

	/**
	 * @return the mode
	 */
	public NetworkMode getMode() {
		return mode;
	}

	/**
	 * @return the connector
	 */
	public ClientConnector getConnector() {
		return connector;
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * Instantiates a network event.
	 *
	 * @param Mode
	 *            What it's doing.
	 * @param cnct
	 *            The client connector
	 * @param msg
	 *            The message
	 */
	public NetworkEvent(NetworkMode Mode, ClientConnector cnct, Message msg) {
		if (Mode == null) {
			throw new IllegalArgumentException("The mode must not be null.");
		}
		if (cnct == null) {
			throw new IllegalArgumentException("The connector must not be null.");
		}
		if (Mode == NetworkMode.MESSAGE && msg == null) {
			throw new IllegalArgumentException("The message cannot be null with the specified mode.");
		}
		mode = Mode;
		connector = cnct;
		message = msg;
	}

	/**
	 * The action that is happening on the network.
	 * 
	 * @author Dillon - Github dg092099
	 *
	 */
	public static enum NetworkMode {
		DISCONNECT, CONNECT, MESSAGE, DEBUG_ENABLE
	}
}
