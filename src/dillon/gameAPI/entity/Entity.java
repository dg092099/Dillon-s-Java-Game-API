package dillon.gameAPI.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;

import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.event.TickEvent;
import dillon.gameAPI.scroller.ScrollManager;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This class stores the position, direction and sprite of each entity.
 *
 * @author Dillon - Github dg092099
 */
public class Entity implements Serializable {
	private static final long serialVersionUID = 1176042239171972455L;
	private BufferedImage spr; // The sprite object for this entity.
	private SecurityKey key;

	/**
	 * This method creates an entity.
	 *
	 * @param sprite
	 *            The image to use.
	 * @param k
	 *            The security key.
	 */
	public Entity(Image sprite, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.INSTANTIATE_ENTITY);
		key = k; // Security key
		spr = (BufferedImage) sprite; // The sprite
		x = 0; // X position
		y = 0; // Y Position
		dx = 0; // Direction X
		dy = 0; // Direction Y
		EventSystem.addHandler(new EEHandler<TickEvent>() { // Update handler
			@Override
			public void handle(TickEvent T) {
				if (!checkCollisionWithPos(x + dx, y + dy)) {
					// Move in direction if it won't cause a collision.
					x += dx;
					y += dy;
				}
				if (gravity) {
					// Gravity calculations.
					if (!checkCollisionWithPos(x, y + fallspeed)) {
						if (!gravityOverride) {
							y += fallspeed;
						}
					} else if (gravityOverride) {
						gravityOverride = false;
					}
				}
				if (jumping) {
					// Jump
					if (jumpPixCount >= jumpHeight) {
						jumping = false;
						jumpPixCount = 0;
						setDirection(dx, 2);
					} else {
						jumpPixCount += 2;
						y += 2;
					}
				}
				if (autoMode == 1) { // Auto pilot mode.
					if (counter == 0) { // Limiter
						counter = autoLimit;
						double diffX = x - target.x; // The difference between
														// the two x values.
						double diffY = y - target.y; // The difference between
														// the two y values.
						// Find angle to go towards.
						double angle = Math.atan2(diffX, diffY);
						dx = Math.sin(angle * autoMultiplier);
						dy = Math.cos(angle * autoMultiplier);
					} else {
						counter--;
					}
				}
				calculateZones();
			}
		}, key);
		EventSystem.addHandler(new EEHandler<RenderEvent>() { // Render the
																// entity.
			@Override
			public void handle(RenderEvent evt) {
				Graphics2D graphics = evt.getGraphics();
				graphics.drawImage(spr, (int) x, (int) y, null);
				if (showHealth) {
					// Show health bar.
					graphics.setColor(Color.RED);
					graphics.fillRect((int) x - 30, (int) y - 20, 100, 5);
					graphics.setColor(Color.GREEN);
					graphics.fillRect((int) x - 30, (int) y - 20, (int) (health / MaxHealth * 100), 5);
				}
			}
		}, key);
	}

	private double x, y; // The entity's position values.
	private transient double dx, dy; // The entity's velocity values.

	/**
	 * Sets the x position of the entity.
	 *
	 * @param X
	 *            the position
	 */
	public void setX(int X) {
		x = X;
		calculateZones();
	}

	/**
	 * Gets the x position.
	 *
	 * @return X
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the Y position.
	 *
	 * @param Y
	 *            The y position
	 */
	public void setY(int Y) {
		y = Y;
		calculateZones();
	}

	private void calculateZones() {
		for (EntityZoneEvent evt : zoneEvents) {
			if (x >= evt.getTopLeft()[0] && y >= evt.getTopLeft()[1]
					&& x <= evt.getTopLeft()[0] + evt.getWidthAndHeight()[0]
					&& y <= evt.getTopLeft()[0] + evt.getWidthAndHeight()[1]) {
				evt.onAction();
			}
		}
	}

	/**
	 * Gets the Y position.
	 *
	 * @return Y
	 */
	public double getY() {
		return y;
	}

	/**
	 * This sets the direction based on a velocity x and y.
	 *
	 * @param DX
	 *            Directional x
	 * @param DY
	 *            Directional Y
	 */
	public void setDirection(double DX, double DY) {
		dx = DX;
		dy = DY;
	}

	/**
	 * This sets the direction based on an angle.
	 *
	 * @param angle
	 *            The angle
	 */
	public void setDirection(double angle) {
		dx = Math.sin(angle);
		dy = Math.cos(angle);
	}

	/**
	 * Gets the current direction.
	 *
	 * @return array: [x direction, y direction]
	 */
	public int[] getDirection() {
		return new int[] { (int) dx, (int) dy };
	}

	/**
	 * Sets the current sprite
	 *
	 * @param img
	 *            The sprite.
	 */
	public void setSprite(Image img) {
		spr = (BufferedImage) img;
	}

	public static final int AUTOPILOT_DIRECT = 1; // Constant: autopilot type
													// direct
	private int autoMode = -1; // The autopilot mode.
	private Entity target; // The target entity for the autopilot.
	private int autoLimit; // The limit to how many frames must pass until path
							// is recalculated.
	private int autoMultiplier; // The speed that it should go.

	/**
	 * Sets the sprite to target and follow an entity
	 *
	 * @param mode
	 *            How it should move
	 * @param target
	 *            The entity it is targeting.
	 * @param limit
	 *            How many updates before recalculating the directions.
	 * @param speedMultiplier
	 *            A value needed to normalize the angles.
	 */
	public void setAutoPilot(int mode, Entity target, int limit, int speedMultiplier) {
		autoMode = mode;
		this.target = target;
		autoLimit = limit;
		autoMultiplier = speedMultiplier;
	}

	/**
	 * Shuts off the autopilot for this entity.
	 */
	public void unsetAutoPilot() {
		autoMode = 0;
		counter = 0;
	}

	private Double health = 0D; // The entity's health

	/**
	 * Sets the current health of an entity.
	 *
	 * @param h
	 *            health.
	 */
	public void setHealth(Double h) {
		health = h;
	}

	/**
	 * Gets the current health.
	 *
	 * @return health
	 */
	public Double getHealth() {
		return health;
	}

	private int MaxHealth = 100; // The entity's maximum health.

	/**
	 * Sets the maximum health.
	 *
	 * @param max
	 *            Maximum health.
	 */
	public void setMaxHealth(int max) {
		MaxHealth = max;
	}

	/**
	 * Gets the maximum health.
	 *
	 * @return The maximum health.
	 */
	public int getMaxHealth() {
		return MaxHealth;
	}

	/**
	 * Makes the entity take damage.
	 *
	 * @param dm
	 *            The damage.
	 */
	public void takeDamage(int dm) {
		health -= dm;
	}

	private int counter = 0; // A counter for the autopilot.

	private boolean showHealth = false; // Whether the health should be
										// displayed.

	/**
	 * Sets if the health bar should be rendered.
	 *
	 * @param b
	 *            Health bar
	 */
	public void setShowHealth(boolean b) {
		showHealth = b;
	}

	/**
	 * This method will check if the player is colliding with anything.
	 *
	 * @return if it is colliding with something
	 */
	public boolean checkCollision() {
		return ScrollManager.getCollisionAny(x, y, spr.getWidth(), spr.getHeight());
	}

	/**
	 * Finds if the entity is colliding with a tile.
	 *
	 * @param posx
	 *            tile's x position
	 * @param posy
	 *            tile's y position
	 * @return colliding
	 */
	private boolean checkCollisionWithPos(double posx, double posy) {
		return ScrollManager.getCollisionAny(posx, posy, spr.getWidth(), spr.getHeight());
	}

	private boolean gravity = false; // If gravity affects this entity.

	/**
	 * This method will set weather gravity should operate on entity.
	 *
	 * @param g
	 *            whether it should or not.
	 */
	public void setGravity(boolean g) {
		gravity = g;
	}

	private int fallspeed = 5; // How quickly the entity should fall.

	/**
	 * This is for the gravity, it sets how fast the entity falls.
	 *
	 * @param speed
	 *            the speed.
	 */
	public void setFallSpeed(int speed) {
		fallspeed = speed;
	}

	private boolean gravityOverride = false; // Temporarily shuts off the
												// gravity for a jump.

	/**
	 * This method will disable the gravity until the entity hits a surface.
	 */
	public void setGravityJumpOverride() {
		gravityOverride = true;
	}

	private boolean jumping = false; // Indicates if the entity is jumping.
	private int jumpHeight = 1; // The entity's jumping height.
	private int jumpPixCount = 0; // How many pixels has it gone.

	/**
	 * This method will send the entity upwards.
	 *
	 * @param height
	 *            How high to move the entity.
	 */
	public void jump(int height) {
		jumpHeight = height;
		jumping = true;
		setGravityJumpOverride();
	}

	/**
	 * Finds the distance between this entity and another entity.
	 *
	 * @param e
	 *            The other entity
	 * @return The distance.
	 */
	public double getDistanceFrom(Entity e) {
		int diffX = (int) Math.abs(getX() - e.getX());
		int diffY = (int) Math.abs(getY() - e.getY());
		// Gets the directional distances between the two to calculate the
		// actual distance.
		double xs = Math.pow(diffX, 2);
		double ys = Math.pow(diffY, 2);
		double sum = xs + ys;
		return Math.sqrt(sum);
	}

	/**
	 * Finds the distance from this entity and a point.
	 *
	 * @param x
	 *            The x
	 * @param y
	 *            The y
	 * @return The distance
	 */
	public double getDistanceFrom(int x, int y) {
		int diffX = (int) Math.abs(getX() - x);
		int diffY = (int) Math.abs(getY() - y);
		// Similar to above, with different sources though.
		double xs = Math.pow(diffX, 2);
		double ys = Math.pow(diffY, 2);
		return Math.sqrt(xs + ys);
	}

	/**
	 * Determines if the two entiites are the same.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Entity)) {
			return false;
		}
		Entity e = (Entity) o;
		if (this.getX() != e.getX()) {
			return false;
		}
		if (this.getY() != e.getY()) {
			return false;
		}
		if (this.getHealth() != e.getHealth()) {
			return false;
		}
		if (this.getMaxHealth() != e.getMaxHealth()) {
			return false;
		}
		if (!this.spr.equals(e.spr)) {
			return false;
		}
		return true;
	}

	/**
	 * Sends out the debugging string.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\ndillon.gameAPI.entity.Entity Dump: Entity code " + hashCode() + "\n");
		String data = "";
		data += String.format("%-25s %-5s\n", "Key", "Value");
		data += String.format("%-25s %-5s\n", "---", "-----");
		data += String.format("%-25s %-5s\n", "Sprite:", spr != null ? spr.toString() : "None");
		data += String.format("%-25s %-5.2f\n", "X Position:", x);
		data += String.format("%-25s %-5.2f\n", "Y Position:", y);
		data += String.format("%-25s %-5.2f\n", "X Direction:", dx);
		data += String.format("%-25s %-5.2f\n", "Y Direction:", dy);
		data += String.format("%-25s %-5d\n", "Autopilot mode:", autoMode);
		data += String.format("%-25s %-5s\n", "Target Code:", target != null ? target.hashCode() : "None");
		data += String.format("%-25s %-5d\n", "Autopilot Limit:", autoLimit);
		data += String.format("%-25s %-5d\n", "Autopilot Multiplier:", autoMultiplier);
		data += String.format("%-25s %-5s\n", "Health:", health + "/" + MaxHealth);
		data += String.format("%-25s %-5s\n", "Showing Health:", showHealth ? "Yes" : "No");
		data += String.format("%-25s %-5s\n", "Gravity:", gravity ? "Yes" : "No");
		data += String.format("%-25s %-5d\n", "Falling speed:", fallspeed);
		data += String.format("%-25s %-5s\n", "Jumping:", jumping ? "Yes" : "No");
		sb.append(data);
		return sb.toString();
	}

	/**
	 * A zone event.
	 *
	 * @author Dillon - Github dg092099
	 *
	 */
	public static abstract class EntityZoneEvent {
		public abstract int[] getTopLeft();

		public abstract int[] getWidthAndHeight();

		public abstract void onAction();
	}

	public ArrayList<EntityZoneEvent> zoneEvents = new ArrayList<>();

	/**
	 * Adds an event handler.
	 *
	 * @param e
	 *            Event handler
	 */
	public void addEvent(EntityZoneEvent e) {
		zoneEvents.add(e);
	}

	/**
	 * Removes an event handler.
	 *
	 * @param e
	 *            Event handler
	 */
	public void removeEvent(EntityZoneEvent e) {
		zoneEvents.remove(e);
	}
}
