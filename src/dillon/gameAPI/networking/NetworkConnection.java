package dillon.gameAPI.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import dillon.gameAPI.errors.NetworkingError;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.NetworkEvent;

/**
 * This handles a connection to a server.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class NetworkConnection {
	private static Socket sock;
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;

	/**
	 * Tells the engine to connect to a server.
	 * 
	 * @param host
	 *            The ip address of the server.
	 * @param port
	 *            The port number of the server.
	 * @throws NetworkingError
	 */
	public static void connect(String host, int port) throws NetworkingError {
		try {
			sock = new Socket(host, port);
			oos = new ObjectOutputStream(sock.getOutputStream());
			ois = new ObjectInputStream(sock.getInputStream());
			oos.flush();
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
	 * Tries to disconnect from the server.
	 */
	public static void disconnect() {
		try {
			running = false;
			Message msg = new Message("SHUTDOWN", "Client");
			oos.writeObject(msg);
			oos.flush();
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

	private static volatile boolean running = false;

	static class listener implements Runnable {
		@Override
		public void run() {
			while (running) {
				try {
					Message rec = (Message) ois.readObject();
					if (rec == null)
						continue;
					if (rec.getMessage().equals("SHUTDOWN")) {
						disconnect();
						return;
					}
					rec.setIP(sock.getRemoteSocketAddress().toString());
					EventSystem.broadcastMessage(new NetworkEvent(
							NetworkEvent.MESSAGE, null, rec));
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
}
