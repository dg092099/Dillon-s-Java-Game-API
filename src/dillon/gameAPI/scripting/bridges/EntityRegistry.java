package dillon.gameAPI.scripting.bridges;

import java.util.HashMap;

import dillon.gameAPI.entity.Entity;

/**
 * This allows for transfers of entities between the program and scripts. This
 * is because I haven't implemented a way for the script to load images yet.
 *
 * @author Dillon - Github dg092099
 *
 */
public class EntityRegistry {
	private static final HashMap<String, Entity> entities = new HashMap<String, Entity>();

	/**
	 * Register with the given name.
	 *
	 * @param name
	 *            The entity name.
	 * @param e
	 *            The entity.
	 */
	public static void register(String name, Entity e) {
		entities.put(name, e);
	}

	/**
	 * Remove an entry
	 *
	 * @param name
	 *            The entity name
	 */
	public static void unregister(String name) {
		entities.remove(name);
	}

	/**
	 * Retrieves the entity
	 *
	 * @param name
	 *            The name
	 * @return The entity
	 */
	public Entity get(String name) {
		return entities.get(name);
	}
}
