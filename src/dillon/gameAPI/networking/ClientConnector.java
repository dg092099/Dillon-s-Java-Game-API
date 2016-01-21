package dillon.gameAPI.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.NetworkEvent;
import dillon.gameAPI.security.SecurityKey;

/**
 * This class bridges the gap between the central server and the client, from
 * the server to client.
 *
 * @author Dillon - Github dg092099
 *
 */
public class ClientConnector {
	private Socket remote; // The client socket.
	private ObjectInputStream ois; // The socket's input stream.
	private ObjectOutputStream oos; // The socket's output stream.
	private SecurityKey key;

	/**
	 * Creates a client connector with the specified socket.
	 *
	 * @param s
	 *            The client's socket.
	 * @param k
	 *            The security key.
	 * @throws IOException
	 *             When socket fails to connect.
	 */
	public ClientConnector(Socket s, SecurityKey k) throws IOException {
		cc = this;
		remote = s;
		ois = new ObjectInputStream(remote.getInputStream());
		oos = new ObjectOutputStream(remote.getOutputStream());
		oos.flush();
		continueListen = true;
		// Start listening
		Thread t = new Thread(new listener());
		t.start();
		key = k;
	}

	/**
	 * Gets the ip of the remote connection.
	 *
	 * @return IP
	 */
	public String getIP() {
		return remote.getRemoteSocketAddress().toString();
	}

	/**
	 * Invokes a shutdown on this controller.
	 */
	public void shutdown() {
		Message msg = new Message("SHUTDOWN", "Server");
		continueListen = false;
		try {
			// Send shutdown signal to client.
			oos.writeObject(msg);
			oos.flush();
			oos.close();
			ois.close();
			remote.close();
		} catch (IOException e) {
		}
		EventSystem.broadcastMessage(new NetworkEvent(NetworkEvent.NetworkMode.DISCONNECT, this, null),
				NetworkEvent.class, key);
	}

	/**
	 * Sends a message to this client.
	 *
	 * @param msg
	 *            The message to send.
	 */
	public void send(Message msg) {
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private volatile boolean continueListen = false; // Tells whether to
														// continue listening.
	private ClientConnector cc; // The instance.

	/**
	 * The class for a separate thread to listen for messages.
	 *
	 * @author Dillon - Github dg092099
	 *
	 */
	class listener implements Runnable {
		@Override
		public void run() {
			while (continueListen) {
				try {
					// Get the message.
					Message rec = (Message) ois.readObject();
					if (rec == null) {
						continue;
					}
					System.out.println("Got message.");
					if (rec.getMessage().equals("SHUTDOWN")) {
						shutdown();
						return;
					}
					rec.setIP(remote.getRemoteSocketAddress().toString());
					EventSystem.broadcastMessage(new NetworkEvent(NetworkEvent.NetworkMode.MESSAGE, cc, rec),
							NetworkEvent.class, key);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
				}
			}
		}
	}

	public String getDebug(String input) {
		input += "dillon.gameAPI.networking.ClientConnector debug:\n";
		input += String.format("%-13s %-5s\n", "Key", "Value");
		input += String.format("%-13s %-5s\n", "---", "-----");
		input += String.format("%-13s %-5s\n", "IP:", remote.getInetAddress().getHostAddress());
		input += String.format("%-13s %-5d\n", "Remote Port:", remote.getPort());
		return input;
	}
}
