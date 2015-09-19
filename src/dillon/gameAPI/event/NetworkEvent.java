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

	private Object[] metadata = new Object[3]; // What happened, the connector,
												// the message.

	@Override
	public Object[] getMetadata() {
		return metadata;
	}

	public static final int DISCONNECT = 0;
	public static final int CONNECT = 1;
	public static final int MESSAGE = 2;

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
	public NetworkEvent(int Mode, ClientConnector cnct, Message msg) {
		metadata[0] = Mode;
		metadata[1] = cnct;
		metadata[2] = msg;
	}
}
