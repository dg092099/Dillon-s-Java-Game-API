package dillon.gameAPI.sprites;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * This class should be used to make sprites to use similar to entities.
 * 
 * @author Dillon - Github dg092099
 */
public class Sprite implements Serializable {
	private static final long serialVersionUID = -7928142513950423682L;
	private BufferedImage img;
	private int x, y, w, h;
	private int dx, dy;

	/**
	 * You should use the sprite manager instead of executing this from the
	 * game.
	 * 
	 * @param Img
	 *            The icon to use as the sprite.
	 */
	public Sprite(BufferedImage Img) {
		img = Img;
		w = img.getWidth();
		h = img.getHeight();
	}

	/**
	 * @return The sprite's width.
	 */
	public int getWidth() {
		return w;
	}

	/**
	 * @return The sprite's height.
	 */
	public int getHeight() {
		return h;
	}

	/**
	 * This sets the image that the sprite should be displayed with.
	 * 
	 * @param Img
	 *            The new icon.
	 */
	public void setImage(Image Img) {
		img = (BufferedImage) Img;
	}

	/**
	 * Returns where the sprite is on the screen (X axis.)
	 * 
	 * @return x value
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets where the sprite should be rendered on the x axis.
	 * 
	 * @param x
	 *            The new x value.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Says where the sprite is on the y axis.
	 * 
	 * @return Where it is on the y axis.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets where the sprite should be on the y axis.
	 * 
	 * @param y
	 *            The new Y value.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns the directional X value.
	 * 
	 * @return directional x
	 */
	public int getDX() {
		return dx;
	}

	/**
	 * Sets the directional x value
	 * 
	 * @param dx
	 *            New directional x.
	 */
	public void setDX(int dx) {
		this.dx = dx;
	}

	/**
	 * Returns the directional Y value.
	 * 
	 * @return directional y.
	 */
	public int getDY() {
		return dy;
	}

	/**
	 * Sets the directional Y value.
	 * 
	 * @param dy
	 *            New directional y.
	 */
	public void setDY(int dy) {
		this.dy = dy;
	}

	/**
	 * This renders the sprite on the screen
	 * 
	 * @param g
	 *            Graphics object given from invoker.
	 */
	public void render(Graphics2D g) {
		if (!visible)
			return;
		g.drawImage(img, x, y, null);
	}

	/**
	 * This tells the sprite to update its positional attributes.
	 */
	public void update() {
		x += dx;
		y += dy;
	}

	boolean visible = true;

	/**
	 * Returns if the sprite is being rendered. NOT if it is on the screen or
	 * not.
	 * 
	 * @return Weather or not it is visible.
	 */
	public boolean getVisible() {
		return visible;
	}

	/**
	 * Sets if the sprite should be rendered.
	 * 
	 * @param b
	 *            Visible or not
	 */
	public void setVisible(boolean b) {
		visible = b;
	}

	private boolean collidable = true;

	/**
	 * This tells if the sprite is to be collidable with.
	 * 
	 * @return collidable
	 */
	public boolean getCollidable() {
		return collidable;
	}

	/**
	 * Sets if the sprite should be collided with.
	 * 
	 * @param b
	 *            collidable
	 */
	public void setCollidable(boolean b) {
		collidable = b;
	}

	public Image getSprite() {
		return img;
	}
}
