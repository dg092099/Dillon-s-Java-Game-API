package dillon.gameAPI.modding;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * An object to identify mods, based on this loader.
 * 
 * @author Dillon - Github https://dg092099.github.io/
 *
 */
public class SandboxedLoader extends URLClassLoader {
	public SandboxedLoader(URL[] urls) {
		super(urls);
	}
}
