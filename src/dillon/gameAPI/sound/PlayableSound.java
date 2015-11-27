package dillon.gameAPI.sound;

import java.io.IOException;
import java.io.InputStream;

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

	public PlayableSound(final InputStream is) {
		try {
			sound = AudioSystem.getAudioInputStream(is);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			sound = null;
		}
	}

	public AudioInputStream getStream() {
		return sound;
	}
}
