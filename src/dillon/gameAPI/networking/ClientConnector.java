package dillon.gameAPI.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.NetworkEvent;

/**
 * This class bridges the gap between the central server and the client.
 * 
 * @author Dillon - Github dg092099
 *
 */
public class ClientConnector {
	private Socket remote;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	/**
	 * Creates a client connector with the specified socket.
	 * 
	 * @param s
	 *            The client's socket.
	 * @throws IOException
	 * 			  When socket fails to connect.
	 */
	public ClientConnector(Socket s) throws IOException {
		cc = this;
		remote = s;
		ois = new ObjectInputStream(remote.getInputStream());
		oos = new ObjectOutputStream(remote.getOutputStream());
		oos.flush();
		continueListen = true;
		Thread t = new Thread(new listener());
		t.start();
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
			oos.writeObject(msg);
			oos.flush();
			oos.close();
			ois.close();
			remote.close();
		} catch (IOException e) {
		}
		EventSystem.broadcastMessage(new NetworkEvent(NetworkEvent.DISCONNECT,
				this, null));
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
		} catch (IOException e) {
			e.printStackTrace();
			Core.crash(e);
		}
	}

	private volatile boolean continueListen = false;
	private ClientConnector cc;

	class listener implements Runnable {
		@Override
		public void run() {
			while (continueListen) {
				try {
					Message rec = (Message) ois.readObject();
					if (rec == null)
						continue;
					System.out.println("Got message.");
					if (rec.getMessage().equals("SHUTDOWN")) {
						shutdown();
						return;
					}
					rec.setIP(remote.getRemoteSocketAddress().toString());
					EventSystem.broadcastMessage(new NetworkEvent(
							NetworkEvent.MESSAGE, cc, rec));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
				}
			}
		}
	}
}
