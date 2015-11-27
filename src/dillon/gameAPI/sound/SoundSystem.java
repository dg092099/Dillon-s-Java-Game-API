package dillon.gameAPI.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import dillon.gameAPI.errors.EngineSecurityError;
import dillon.gameAPI.errors.GeneralRuntimeException;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * The class that handles the sounds.
 *
 * @author Dillon - Github dg092099.github.io
 * @since V1.13
 */
public class SoundSystem {
	private static ArrayList<PlayableSound> sounds = new ArrayList<PlayableSound>();
	private static ArrayList<Clip> clips = new ArrayList<Clip>();

	/**
	 * This plays a sound.
	 *
	 * @param ps
	 *            The playable sound.
	 * @param k
	 *            The security key
	 */
	public static void playSound(PlayableSound ps, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.PLAY_SOUND);
		try {
			sounds.add(ps);
			Clip c = AudioSystem.getClip();
			if (c.isOpen())
				throw new GeneralRuntimeException("The audio system is already in use.");
			c.open(ps.getStream());
			c.setFramePosition(0);
			c.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent arg0) {
					if (arg0.getType().equals(LineEvent.Type.STOP)) {
						c.removeLineListener(this);
						clips.remove(c);
						c.drain();
						c.close();
						sounds.remove(ps);
					}
				}
			});
			c.start();
			clips.add(c);
		} catch (IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playAndBlock(PlayableSound ps, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.PLAY_SOUND);
		try {
			sounds.add(ps);
			Clip c = AudioSystem.getClip();
			if (c.isOpen())
				throw new GeneralRuntimeException("The audio system is already in use.");
			c.open(ps.getStream());
			c.setFramePosition(0);
			LineListener ll = new LineListener() {

				@Override
				public void update(LineEvent arg0) {
					if (arg0.getType().equals(LineEvent.Type.STOP)) {
						c.removeLineListener(this);
						clips.remove(c);
						c.drain();
						c.close();
						sounds.remove(ps);
					}
				}
			};
			c.addLineListener(ll);
			c.start();
			clips.add(c);
			while (clips.contains(c))
				TimeUnit.MILLISECONDS.sleep(500);
		} catch (IOException | LineUnavailableException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void stopSound(PlayableSound ps, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.STOP_SOUND);
		int index = sounds.indexOf(ps);
		clips.get(index).stop();
	}

	public static void shutdown(SecurityKey k) {
		if (!SecuritySystem.isEngineKey(k))
			throw new EngineSecurityError("Invalid key for operation.");
		for (Clip c : clips)
			c.stop();
	}
}
