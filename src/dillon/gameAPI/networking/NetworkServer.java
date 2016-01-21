package dillon.gameAPI.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
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
 * This manages the networking server and clients.
 *
 * @author Dillon - Github dg092099
 *
 */
public class NetworkServer {
	private static volatile boolean runServer = false; // Whether if the server
														// should be running.
	private static ServerSocket server; // The server socket itself.
	private static SecurityKey key;

	/**
	 * Starts a server.
	 *
	 * @param port
	 *            The port number to use.
	 * @param k
	 *            The security key.
	 * @return The host's IP
	 * @throws NetworkingError
	 *             Thrown when it cannot connect to the port.
	 */
	public static String startServer(int port, SecurityKey k) throws NetworkingError {
		SecuritySystem.checkPermission(k, RequestedAction.START_NET_SERVER);
		key = k;
		try {
			// Instantiate server
			server = new ServerSocket(port, 100);
			runServer = true;
			Thread t = new Thread(new server());
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw new NetworkingError("Error when connecting port.");
		}
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * If the server is running.
	 *
	 * @return running
	 */
	public static boolean getServerRunning() {
		return runServer;
	}

	/**
	 * Class for running the server.
	 *
	 * @author Dillon - Github dg092099
	 *
	 */
	static class server implements Runnable {
		@Override
		public void run() {
			while (runServer) {
				try {
					// Get remote socket
					Socket s = server.accept();
					Logger.getLogger("Networking").info("Got client, " + s.getRemoteSocketAddress().toString());
					// Pass control to client connectors.
					ClientConnector cc = new ClientConnector(s, key);
					connectors.add(cc);
					EventSystem.broadcastMessage(new NetworkEvent(NetworkEvent.NetworkMode.CONNECT, cc, null),
							NetworkEvent.class, key);
				} catch (IOException e) {
				}
			}
		}
	}

	private static ArrayList<ClientConnector> connectors = new ArrayList<ClientConnector>(); // The
																								// connected
																								// clients.

	/**
	 * Gets the arraylist of connectors.
	 *
	 * @return The arraylist
	 */
	public static ArrayList<ClientConnector> getConnectors() {
		return connectors;
	}

	/**
	 * Shuts down the server. Internal use only.
	 *
	 * @param k
	 *            The security key
	 */
	private static void shutdown(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.STOP_NET_SERVER);
		try {
			// Shutdown each connector.
			for (int i = 0; i < connectors.size(); i++) {
				connectors.get(i).shutdown();
			}
			server.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Attempts to shutdown the server. Called automatically while game shuts
	 * down.
	 *
	 * @param k
	 *            The security key
	 */
	public static void stopServer(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.STOP_NET_SERVER);
		runServer = false;
		shutdown(k);
	}

	/**
	 * Enables server discovery.
	 *
	 * @param name
	 *            Game name
	 * @param version
	 *            Game version
	 * @param useCode
	 *            If the server should use a code.
	 * @param port
	 *            The port the game's on. Not what port it uses to allow
	 *            discovery.
	 * @return The code if one is asked for, if not, null is returned.
	 */
	public static String enableDiscovery(String name, String version, boolean useCode, int port) {
		String code = Discovery.start(name, version, useCode, port, key);
		if (code != null) {
			return code;
		}
		return null;
	}

	/**
	 * Stops server discovery.
	 *
	 * @param key
	 *            The security key.
	 */
	public static void disableDiscovery(SecurityKey key) {
		Discovery.stop(key);
	}

	public static String getDebug() {
		String data = "\n\ndillon.gameAPI.networking.NetworkServer\n";
		data += String.format("%-15s %-5s\n", "Key", "Value");
		data += String.format("%-15s %-5s\n", "---", "-----");
		data += String.format("%-15s %-5s\n", "Running:", runServer ? "Yes" : "No");
		data += "Connectors:\n";
		for (ClientConnector cc : connectors) {
			data = cc.getDebug(data);
		}
		return data;
	}
}
