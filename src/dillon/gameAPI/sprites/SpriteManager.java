package dillon.gameAPI.sprites;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * This allows the program to be able to house many sprites.
 * 
 * @author Dillon - Github dg092099
 * @deprecated No replacement.
 */
public class SpriteManager {
	private static HashMap<String, Sprite> sprites = new HashMap<String, Sprite>(); // The
																					// sprites
																					// in
																					// the
																					// game.

	/**
	 * Creates a sprite.
	 * 
	 * @param name
	 *            The name you want to refer this sprite by.
	 * @param img
	 *            The image that is the sprite.
	 * @return The sprite
	 */
	public static Sprite registerSprite(String name, BufferedImage img) {
		Sprite sp = new Sprite(img);
		sprites.put(name, sp);
		return sp;
	}

	/**
	 * Returns the requested sprite.
	 * 
	 * @param name
	 *            The name of the sprite to use.
	 * @return The sprite.
	 */
	public static Sprite getSprite(String name) {
		return sprites.get(name);
	}

	/**
	 * Tells weather or not the two sprites are colliding. NOTE: Will return
	 * false if either sprite's collidable properties are false.
	 * 
	 * @param s1
	 *            One sprite
	 * @param s2
	 *            The other sprite.
	 * @return If they are colliding.
	 */
	public static boolean isSpritesColliding(Sprite s1, Sprite s2) {
		if (!s1.getCollidable() || !s2.getCollidable())
			return false;
		Rectangle rect1 = new Rectangle();
		rect1.setBounds(s1.getX(), s1.getY(), s1.getWidth(), s1.getHeight());
		Rectangle rect2 = new Rectangle();
		rect2.setBounds(s2.getX(), s2.getY(), s2.getWidth(), s2.getHeight());
		return rect1.intersects(rect2);
	}
}
