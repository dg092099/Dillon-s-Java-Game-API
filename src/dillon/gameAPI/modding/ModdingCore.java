package dillon.gameAPI.modding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dillon.gameAPI.event.EEvent;
import dillon.gameAPI.event.ShutdownEvent;
import dillon.gameAPI.security.SecurityKey;

/**
 * The central class for modding.
 *
 * @author Dillon - Github https://dg092099.github.io/
 *
 */
public final class ModdingCore {
	private static final HashMap<String, SandboxedLoader> loaders = new HashMap<String, SandboxedLoader>(); // The
																											// sandboxed
																											// classloaders.
	private static final HashMap<String, Class<?>> modClasses = new HashMap<String, Class<?>>(); // The
																									// mod
																									// classes.
	private static final HashMap<String, Object> modClassObjects = new HashMap<String, Object>(); // The
																									// instances
																									// of
																									// the
																									// mod
																									// classes.
	public static final ArrayList<Class<?>> eventHandlers = new ArrayList<Class<?>>(); // The
																						// event
																						// handlers
																						// classes.

	/**
	 * Mod classes
	 *
	 * @return The mod classes.
	 */
	public HashMap<String, Class<?>> getModClasses() {
		return modClasses;
	}

	/**
	 * Mod EventHandlers
	 *
	 * @return The event handlers.
	 */
	public ArrayList<Class<?>> getModEventHandlers() {
		return eventHandlers;
	}

	/**
	 * Called to fill all mod lists.
	 *
	 * @param f
	 *            The directory to search for mods in.
	 * @param k
	 *            The security key.
	 */
	public static void instantateMods(File f, SecurityKey k) {
		if (f.isDirectory()) {
			// Create mod policy
			ModPolicy m = new ModPolicy();
			Policy.setPolicy(m);
			System.setSecurityManager(new SecurityManager());
			for (File f2 : f.listFiles()) {
				if (f2.getName().endsWith(".jar")) {
					try {
						// Create sandboxed class loader.
						SandboxedLoader l = new SandboxedLoader(new URL[] { f2.toURI().toURL() });
						ArrayList<String> classNames = new ArrayList<String>();
						ZipInputStream zis = new ZipInputStream(new FileInputStream(f2));
						ZipEntry ze;
						// Get all class names in jar.
						while ((ze = zis.getNextEntry()) != null) {
							if (ze.getName().endsWith(".class") && !ze.getName().contains("$")) {
								classNames.add(ze.getName().replaceAll("/", ".").substring(0,
										ze.getName().length() - ".class".length()));
							}
							zis.closeEntry();
						}
						zis.close();
						if (classNames.size() == 0) {
							System.out.println("There are no files in the jar.");
							continue;
						}
						for (String s : classNames) {
							Class<?> c = l.loadClass(s);
							if (c.getAnnotationsByType(Modification.class)[0] != null) {
								// Found main class.
								String ModName = c.getAnnotationsByType(Modification.class)[0].name();
								loaders.put(ModName, l);
								modClasses.put(ModName, c);
								modClassObjects.put(ModName, c.newInstance());
								Method[] methods = c.getMethods();
								for (Method m2 : methods) {
									if (m2.getName().equals("Instantate")) {
										// Run instantiate
										m2.invoke(modClassObjects.get(ModName));
									}
								}
							}
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Runs the initialize method in all mod classes.
	 */
	public static void sendInit() {
		for (String s : modClasses.keySet()) {
			Class<?> c = modClasses.get(s);
			Method[] methods = c.getMethods();
			for (Method m : methods) {
				if (m.getName().equals("init")) { // Find method init in any mod
													// classes
					try {
						m.invoke(modClassObjects.get(s)); // Invoke it
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Sends the post start message to mods.
	 */
	public static void sendPostStart() {
		for (String s : modClasses.keySet()) {
			Class<?> c = modClasses.get(s);
			Method[] methods = c.getMethods();
			for (Method m : methods) {
				if (m.getName().equals("postStart")) { // Find postStart in mod
														// classes
					try {
						m.invoke(modClassObjects.get(s));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface EventHandler {
		public Class<? extends EEvent> type() default ShutdownEvent.class;
	}

	/**
	 * Sends an event to all mods.
	 *
	 * @param e
	 *            The event.
	 */
	public static void sendEvent(EEvent e) {
		for (Class<?> c : eventHandlers) {
			Method[] methods = c.getMethods();
			for (Method m : methods) {
				if (m.isAnnotationPresent(EventHandler.class)) { // If the
																	// method
																	// has the
																	// annotation
					try {
						Class<? extends EEvent> eventType = m.getAnnotation(EventHandler.class).type();
						if (e.getClass() == eventType) {
							m.invoke(c.newInstance(), e);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| InstantiationException e1) {
					}
				}
			}
		}
	}

	/**
	 * Gets the name of a mod based on the loader.
	 *
	 * @param s
	 *            The loader
	 * @return The mod name.
	 */
	public static String getNameFromLoader(SandboxedLoader s) {
		for (String s2 : loaders.keySet()) {
			if (loaders.get(s2).equals(s)) {
				return s2;
			}
		}
		return null;
	}

	/**
	 * Registers a class to receive events. Methods that take in events must be
	 * annotated with ModdingCore.EventHandler.
	 *
	 * @param c
	 *            The class, not an object.
	 */
	public static void registerHandler(Class<?> c) {
		eventHandlers.add(c);
	}

}
