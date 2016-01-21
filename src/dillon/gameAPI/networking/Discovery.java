package dillon.gameAPI.networking;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.SecureRandom;

import dillon.gameAPI.errors.GeneralRuntimeException;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * A utility class used to find servers running a specific game. Uses port 9518,
 * immutable.
 *
 * @author Dillon - Github dg092099
 *
 */
class Discovery implements Runnable {
	private static boolean run = false; // If the server should be discoverable.
	private static Thread t; // A thread for the discovery server.
	private static String name, version, code; // The game name, version, and if
												// there is, a password.
	private static int port; // The port to say that the game is on.

	/**
	 * Puts this server on for discovery.
	 *
	 * @param gameName
	 *            The game name.
	 * @param versionName
	 *            The game version.
	 * @param useCode
	 *            If a code should be used.
	 * @param port
	 *            The port the game is on.
	 * @param key
	 *            The security key.
	 * @return The code if one is being used, otherwise null.
	 */
	public static String start(String gameName, String versionName, boolean useCode, int port, SecurityKey key) {
		SecuritySystem.checkPermission(key, RequestedAction.ENABLE_DISCOVERY);
		if (run) {
			return "Already started.";
		}
		name = gameName;
		version = versionName;
		run = true;
		Discovery.port = port;
		t = new Thread(new Discovery());
		t.setName("Network Discovery");
		t.start();
		if (useCode) {
			code = new BigInteger(45, new SecureRandom()).toString(32).toUpperCase();
			return code.toUpperCase();
		}
		return null;
	}

	/**
	 * Turns off discovery for the server.
	 *
	 * @param k
	 *            The security key
	 */
	public static void stop(SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.DISABLE_DISCOVERY);
		if (!run) {
			return;
		}
		run = false;
		try {
			t.interrupt();
			t.join();
		} catch (InterruptedException e) {
		}
	}

	private static DatagramSocket socket; // The UDP socket for discovery.

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(9518);
			socket.setBroadcast(true);
		} catch (SocketException e) {
			throw new GeneralRuntimeException("Unable to establish lock on port 9518.");
		}
		while (run) {
			try {
				byte[] packet = new byte[2048];
				DatagramPacket pack = new DatagramPacket(packet, packet.length);
				socket.receive(pack);
				String s = new String(packet, "UTF-8");
				s = s.trim();
				if (!s.split(":")[0].equals(name)) {
					continue;
				}
				if (!s.split(":")[1].equals(version)) {
					continue;
				}
				if (code != null) {
				}
				if (code != null && !s.split(":")[2].equals(code)) {
					continue;
				}
				packet = ("OK-" + InetAddress.getLocalHost().getHostAddress() + ":" + port).getBytes("UTF-8");
				DatagramPacket response = new DatagramPacket(packet, packet.length, pack.getAddress(), pack.getPort());
				socket.send(response);
				if (s.split(":")[0].equals(name) && s.split(":")[1].equals(version) && code != null
						&& s.split(":")[2].equals(code)) {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static String getDebug() {
		String data = "\n\ndillon.gameAPI.networking.Discovery Debug:\n";
		data += String.format("%-15s %-7s\n", "Key", "Value");
		data += String.format("%-15s %-7s\n", "---", "-----");
		data += String.format("%-15s %-7s\n", "Running:", run ? "Yes" : "No");
		data += String.format("%-15s %-7s\n", "Name:", name != null ? name : "None");
		data += String.format("%-15s %-7s\n", "Version:", version != null ? version : "None");
		data += String.format("%-15s %-7s\n", "Code:", code != null ? code : "None");
		data += String.format("%-15s %-7d\n", "Port:", port);
		return data;
	}
}
