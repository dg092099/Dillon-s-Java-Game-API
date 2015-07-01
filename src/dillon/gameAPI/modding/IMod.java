package dillon.gameAPI.modding;

/**
 * Interface specifying methods that should be in mod class.
 * 
 * @author Dillon - Github https://dg092099.github.io/
 *
 */
public interface IMod {
	/**
	 * Calls immediately when the mod is found and started.
	 */
	public void Instantate();

	/**
	 * Calls when Core.setup() is called.
	 */
	public void init();

	/**
	 * Calls when Core.startGame() is called.
	 */
	public void postStart();
}
