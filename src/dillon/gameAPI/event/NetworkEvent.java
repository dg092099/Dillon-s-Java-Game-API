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
	 * Instantates a network event.
	 * 
	 * @param Mode
	 *            What it's doing.
	 * @param cnct
	 *            The client connector
	 * @param msg
	 *            The message
	 */
	public NetworkEvent(NetworkMode Mode, ClientConnector cnct, Message msg) {
		mode = Mode;
		connector = cnct;
		message = msg;
	}

	public static enum NetworkMode {
		DISCONNECT, CONNECT, MESSAGE, DEBUG_ENABLE
	}
}
