package dillon.gameAPI.event;

import dillon.gameAPI.mapping.Tile;
import dillon.gameAPI.mapping.TileEvent;

/**
 * This is an event that executes when a tile is touched or clicked.
 *
 * @author Dillon - Github dg092099
 * @since V2.0
 */
public class TileEntityEvent extends EEvent {

	@Override
	public String getType() {
		return "Tile Entity Event";
	}

	private final Tile affected;

	/**
	 * Get the tile that was touched/clicked on.
	 *
	 * @return The tile
	 */
	public Tile getTile() {
		return affected;
	}

	private final TileEvent.TileEventType eventSubtype;

	/**
	 * Gets the subtype for this event.
	 *
	 * @return The subtype
	 */
	public TileEvent.TileEventType getSubtype() {
		return eventSubtype;
	}

	public TileEntityEvent(Tile target, TileEvent.TileEventType eventType) {
		if (target == null) {
			throw new IllegalArgumentException("The tile must not be null.");
		}
		if (eventType == null) {
			throw new IllegalArgumentException("The event type must be specified.");
		}
		affected = target;
		eventSubtype = eventType;
	}
}
