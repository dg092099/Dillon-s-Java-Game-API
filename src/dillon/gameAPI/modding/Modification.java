package dillon.gameAPI.modding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A modification annotation for all mods. Must be placed on the mod class.
 * 
 * @author Dillon - Github https://dg092099.github.io/
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Modification {
	/**
	 * Mod name
	 * 
	 * @return Mod name
	 */
	public String name();

	/**
	 * Version
	 * 
	 * @return Version
	 */
	public String version();
}
