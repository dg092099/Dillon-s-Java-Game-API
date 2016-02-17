package dillon.gameAPI.sound;

import java.io.BufferedInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class holds the sound information.
 *
 * @author Dillon - Github dg092099.github.io
 * @since V1.13
 */
public class PlayableSound {
	private AudioInputStream sound;
	private boolean loop;

	/**
	 * Instantiates a playable sound.
	 *
	 * @param is
	 *            The input stream.
	 * @param loop
	 *            If it should loop.
	 */
	public PlayableSound(final BufferedInputStream is, boolean loop) {
		try {
			sound = AudioSystem.getAudioInputStream(is);
			this.loop = loop;
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			sound = null;
		}
	}

	public AudioInputStream getStream() {
		return sound;
	}

	public boolean getLooping() {
		return loop;
	}
}
