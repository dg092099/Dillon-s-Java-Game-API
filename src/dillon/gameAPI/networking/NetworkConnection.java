package dillon.gameAPI.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

import dillon.gameAPI.errors.NetworkingError;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.NetworkEvent;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This handles a connection to a server, from client to server.
 *
 * @author Dillon - Github dg092099
 *
 */
public class NetworkConnection {
	private static Socket sock; // The connection.
	private static ObjectInputStream ois; // Input stream.
	private static ObjectOutputStream oos; // output stream.
	private static SecurityKey key;

	/**
	 * Tells the engine to connect to a server.
	 *
	 * @param host
	 *            The ip address of the server.
	 * @param port
	 *            The port number of the server.
	 * @param k
	 *            The security key.
	 * @throws NetworkingError
	 *             Problems connecting
	 */
	public static void connect(String host, int port, SecurityKey k) throws NetworkingError {
		SecuritySystem.checkPermission(k, RequestedAction.CONNECT);
		key = k;
		try {
			// Open socket
			sock = new Socket(host, port);
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			oos.flush();
			// Start listener
			Thread t = new Thread(new listener());
			running = true;
			t.start();
			Logger.getLogger("Networking").info("Connection successful");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetworkingError("Trouble when connecting to server.");
		}
	}

	/**
	 * Connects to a server, using the Inet socket address.
	 *
	 * @param add
	 *            The socket address.
	 * @param k
	 *            The security key
	 * @throws NetworkingError
	 *             If connecting fails.
	 */
	public static void connect(InetSocketAddress add, SecurityKey k) throws NetworkingError {
		connect(add.getHostName(), add.getPort(), k);
	}

	static boolean keepSearching = false; // If the system should keep looking
											// for a server.

	/**
	 * A utility class to search for servers using discovery.
	 *
	 * @author Dillon - Github dg092099
	 *
	 */
	static class timer implements Runnable {
		@Override
		public void run() {
			System.out.println("Now listening");
			while (keepSearching) {
				try {
					// Receive datagram
					byte[] data = new byte[2048];
					pack = new DatagramPacket(data, data.length);
					socket.receive(pack);
					String ip = new String(data, "UTF-8").split("-")[1].trim();
					addresses.add(new InetSocketAddress(ip.split(":")[0], Integer.parseInt(ip.split(":")[1])));
				} catch (Exception e) {
				}
			}
		}

		DatagramSocket socket; // The socket to look on.
		DatagramPacket pack; // The packet to transfer game information.

		public timer(DatagramSocket sock, DatagramPacket p) {
			socket = sock;
			pack = p;
		}
	}

	volatile static ArrayList<InetSocketAddress> addresses; // The addresses
															// found using
															// discovery.

	/**
	 * Finds all of the servers using the given information.
	 *
	 * @param gameName
	 *            The game's name.
	 * @param version
	 *            The game's version.
	 * @param code
	 *            If there's a code, the code. If there is no code, use null.
	 * @return An array list of InetSocketAddresses with all of the servers that
	 *         responded.
	 */
	public static ArrayList<InetSocketAddress> discoverServers(String gameName, String version, String code) {
		try {
			// Reset results
			addresses = new ArrayList<>();
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] data = (gameName + ":" + version + ":" + code).getBytes("UTF-8");
			// Send to all computers on LAN
			DatagramPacket pack = new DatagramPacket(data, data.length, InetAddress.getAllByName("192.168.1.255")[0],
					9518);
			socket.send(pack);
			keepSearching = true;
			// Begin search.
			Thread t = new Thread(new timer(socket, pack), "Searching...");
			t.start();
			Thread.sleep(3000);
			keepSearching = false;
			socket.close();
			t.join();
			return addresses;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Tries to disconnect from the server. Automatically executes on game
	 * shutdown.
	 *
	 * @param k
	 *            The security key.
	 */
	public static void disconnect(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.DISCONNECT);
		try {
			running = false;
			// Send shutdown message
			Message msg = new Message("SHUTDOWN", "Client");
			oos.writeObject(msg);
			oos.flush();
			// Close socket
			oos.close();
			ois.close();
			sock.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Returns if the connection is alive.
	 *
	 * @return alive.
	 */
	public static boolean getRunning() {
		return running;
	}

	private static volatile boolean running = false; // If it should continue
														// listening.

	/**
	 * A class to listen for messages.
	 *
	 * @author Dillon - Github dg092099
	 *
	 */
	static class listener implements Runnable {
		@Override
		public void run() {
			while (running) {
				try {
					Message rec = (Message) ois.readObject();// Gets message
					if (rec == null) {
						continue;
					}
					if (rec.getMessage().equals("SHUTDOWN")) {
						disconnect(key);
						return;
					}
					rec.setIP(sock.getRemoteSocketAddress().toString());
					EventSystem.broadcastMessage(new NetworkEvent(NetworkEvent.NetworkMode.MESSAGE, null, rec),
							NetworkEvent.class, key);
				} catch (ClassNotFoundException | IOException e) {
				}
			}
		}
	}

	/**
	 * Sends a message to the server.
	 *
	 * @param msg
	 *            The message to be sent.
	 */
	public static void sendMessage(Message msg) {
		try {
			oos.writeObject(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getDebug() {
		String data = "\n\ndillon.gameAPI.networking.NetworkConnection Debug:\n";
		data += String.format("%-15s %-5s\n", "Key", "Value");
		data += String.format("%-15s %-5s\n", "---", "-----");
		data += String.format("%-15s %-5s\n", "IP:",
				sock != null ? sock.getInetAddress().getHostAddress() : "Disconnected");
		data += String.format("%-15s %-5s\n", "Searching:", keepSearching ? "Yes" : "No");
		data += String.format("%-15s %-5s\n", "Listening:", running ? "Yes" : "No");
		return data;
	}
}
