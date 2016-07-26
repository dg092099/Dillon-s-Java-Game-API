package dillon.gameAPI.mapping;

/**
 * This class moderates the events in regards to the mapping package.
 *
 * @author Dillon - Github dg092099
 * @since V2.0
 *
 */
public class TileEvent {
	/**
	 * The type of tile event.
	 *
	 * @author Dillon - Github dg092099
	 * @since V2.0
	 *
	 */
	public static enum TileEventType {
		TOUCH, CLICK
	}

	private TileEventType eventType;
	private Tile affectedTile;
	private String entityType;
	private String method;

	/**
	 * Copy the event.
	 *
	 * @return The copy
	 */
	public TileEvent copy() {
		TileEvent te = new TileEvent();
		te.eventType = this.eventType;
		te.affectedTile = this.affectedTile;
		te.entityType = this.entityType;
		te.method = this.method;
		return te;
	}

	public TileEventType getEventType() {
		return eventType;
	}

	public void setEventType(TileEventType eventType) {
		this.eventType = eventType;
	}

	public Tile getAffectedTile() {
		return affectedTile;
	}

	public void setAffectedTile(Tile affectedTile) {
		this.affectedTile = affectedTile;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
}
