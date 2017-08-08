package dillon.gameAPI.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

/**
 * This class represents an animation within the game.
 *
 * @author Dillon - Github dg092099
 * @since V2.1.0
 *
 */
public class Animation {
	private final BufferedImage[] frames;
	private final int animationSpeed;
	private int loops;
	private int animationIndex;
	private int ticksTaken;
	private boolean finished;

	/**
	 * Instantiate an animation object.
	 *
	 * @param frames
	 *            The frames (Must be more than 2.)
	 * @param animationSpeed
	 *            The amount of updates that must execute to proceed the animation.
	 * @param loops
	 *            The amount of times that the animation loops. 1 For doesn't loop.
	 *            -1 for always loops. 0 causes issues with the animation finishing
	 *            before it happens (Don't set it to that).
	 */
	public Animation(BufferedImage[] frames, int animationSpeed, int loops) {
		if (frames.length < 2) {
			this.frames = null;
			this.animationSpeed = 0;
			this.animationIndex = 0;
			ticksTaken = 0;
			finished = true;
			this.loops = 0;
			throw new IllegalArgumentException("There must be at least two frames to have an animation.");
		}
		if (loops == 0) {
			throw new IllegalArgumentException("Loops must not be 0. Use 1 to make the animation play once.");
		}
		this.frames = frames;
		this.animationSpeed = animationSpeed;
		animationIndex = 0;
		ticksTaken = 0;
		finished = false;
		this.loops = loops;
	}

	/**
	 * Causes the animation to progress if able.
	 */
	public void tick() {
		if (finished) {
			return;
		}
		ticksTaken++;
		if (ticksTaken == animationSpeed) {
			animationIndex++;
			if (animationIndex == frames.length) {
				if (loops == 0) { // Animation done playing
					finished = true;
					return;
				}
				if (loops != -1) { // Not infinite
					loops--;
				}
				animationIndex = 0;
			}
		}
	}

	/**
	 * Get the current animation frame.
	 *
	 * @return The frame.
	 */
	public BufferedImage getCurrentFrame() {
		return frames[animationIndex];
	}

	/**
	 * Gets if the animation is at the end. It will not progress further.
	 *
	 * @return At the end.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Get the frames of the animation.
	 *
	 * @return The frames.
	 */
	public Image[] getFrames() {
		return frames;
	}

	static class Frame {
		private BufferedImage img;
		private String name;

		public BufferedImage getImg() {
			return img;
		}

		public void setImg(BufferedImage img) {
			this.img = img;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Frame(String name, BufferedImage img) {
			this.img = img;
			this.name = name;
		}
	}

	/**
	 * Create an animation from a zip file. The frames should be numbered.
	 *
	 * @param is
	 *            The input stream.
	 *
	 * @return The animation
	 */
	public static Animation createFromZip(InputStream is) {
		ArrayList<Frame> frames = new ArrayList<Frame>();
		// Load images
		BufferedImage[] imgs = null;
		try {
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry e = null;
			while ((e = zis.getNextEntry()) != null) {
				String name = e.getName();
				BufferedImage img = ImageIO.read(zis);
				zis.closeEntry();
				Frame f = new Animation.Frame(name, img);
				if (frames.isEmpty()) {
					frames.add(f);
				} else {
					boolean put = false;
					for (int i = 0; i < frames.size(); i++) {
						if (frames.get(i).name.compareTo(name) > 0) {
							frames.add(i, f);
							put = true;
							break;
						}
					}
					if (!put) {
						frames.add(f);
					}
				}
			}
			zis.close();
			imgs = new BufferedImage[frames.size()];
			for (int i = 0; i < imgs.length; i++) {
				imgs[i] = frames.get(i).img;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return new Animation(imgs, 2, 1);
	}

	public static Animation createFromZip(URL url) {
		try {
			return createFromZip(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return The amount of times that the animation will loop.
	 */
	public int getLoops() {
		return loops;
	}

	/**
	 * @param loops
	 *            times the animation should loop.
	 */
	public void setLoops(int loops) {
		this.loops = loops;
	}

	/**
	 * @return How fast the animation should play.
	 */
	public int getAnimationSpeed() {
		return animationSpeed;
	}
}
