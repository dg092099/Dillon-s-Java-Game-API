package dillon.gameAPI.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import dillon.gameAPI.event.EEHandler;
import dillon.gameAPI.event.EventSystem;
import dillon.gameAPI.event.KeyEngineEvent;
import dillon.gameAPI.event.MouseEngineEvent;
import dillon.gameAPI.event.RenderEvent;
import dillon.gameAPI.event.TickEvent;
import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This class regulates all on-screen objects that are not entities, or a level.
 *
 * @since 1.11
 * @author Dillon - Github dg092099
 *
 */
public class GuiSystem {
	private static ArrayList<GuiComponent> components = new ArrayList<GuiComponent>();
	private static int lowestIndex = Integer.MAX_VALUE, highestIndex = Integer.MIN_VALUE;
	private static int activeGuiComponent = -1;

	/**
	 * This shows a gui on the screen.
	 *
	 * @param gc
	 *            The component.
	 * @param k
	 *            The security key.
	 */
	public static void startGui(GuiComponent gc, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SHOW_GUI); // Security
																		// Check
		components.add(gc);
		// Adjust zIndex.
		int zIndex = gc.getZIndex();
		if (zIndex < lowestIndex) {
			lowestIndex = zIndex;
		}
		if (zIndex > highestIndex) {
			highestIndex = zIndex;
		}
		activeGuiComponent = components.indexOf(gc);
	}

	/**
	 * This removes a gui component from the screen.
	 *
	 * @param gc
	 *            The gui component.
	 * @param k
	 *            The security key.
	 */
	public static void removeGui(GuiComponent gc, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SHOW_GUI);
		components.remove(gc);
		// Adjust zIndex
		lowestIndex = Integer.MAX_VALUE;
		highestIndex = Integer.MIN_VALUE;
		for (GuiComponent comp : components) {
			if (comp.getZIndex() > highestIndex) {
				highestIndex = comp.getZIndex();
				activeGuiComponent = components.indexOf(comp);
			}
		}
		if (components.isEmpty()) {
			activeGuiComponent = -1;
		}
	}

	public static int getActiveComponent() {
		return activeGuiComponent;
	}

	public GuiSystem(SecurityKey k) {
		EventSystem.addHandler(new EEHandler<RenderEvent>() {
			@Override
			public void handle(RenderEvent evt) {
				Graphics2D g = evt.getGraphics();
				for (GuiComponent comp : components) {
					// Render each component
					comp.render(g);
				}
			}
		}, k);
		EventSystem.addHandler(new EEHandler<TickEvent>() {
			@Override
			public void handle(TickEvent evt) {
				for (GuiComponent comp : components) {
					// Update each component.
					comp.onUpdate();
				}
			}
		}, k);
		EventSystem.addHandler(new EEHandler<MouseEngineEvent>() {
			@Override
			public void handle(MouseEngineEvent evt) {
				if (evt.getMouseMode() != MouseEngineEvent.MouseMode.CLICK
						&& evt.getMouseMode() != MouseEngineEvent.MouseMode.HOLD) {
					return;
				}
				if (evt.getMouseButton() == MouseEngineEvent.MouseButton.LEFT) {
					for (GuiComponent comp : components) {
						comp.onMouseClickLeft(evt.getLocation().getX(), evt.getLocation().getY());
					}
				} else if (evt.getMouseButton() == MouseEngineEvent.MouseButton.RIGHT) {
					for (GuiComponent comp : components) {
						comp.onMouseClickRight(evt.getLocation().getX(), evt.getLocation().getY());
					}
				}
				ArrayList<GuiComponent> candidates = new ArrayList<>();
				Point p = evt.getLocation();
				for (GuiComponent comp : components) {
					if (p.getX() >= comp.getTopLeftCorner()[0] && p.getY() >= comp.getTopLeftCorner()[1]
							&& p.getX() <= comp.getTopLeftCorner()[0] + comp.getSize()[0]
							&& p.getY() <= comp.getTopLeftCorner()[1] + comp.getSize()[1]) {
						candidates.add(comp);
					}
				}
				int lowestIndex = Integer.MAX_VALUE;
				for (GuiComponent comp : candidates) {
					if (comp.getZIndex() < lowestIndex) {
						lowestIndex = comp.getZIndex();
						activeGuiComponent = components.indexOf(comp);
						comp.bringToFront();
					}
				}
				if (candidates.size() == 0) {
					activeGuiComponent = -1;
				}
			}
		}, k);
		EventSystem.addHandler(new EEHandler<KeyEngineEvent>() {
			@Override
			public void handle(KeyEngineEvent evt) {
				KeyEvent e = evt.getKeyEvent();
				if (evt.getMode() != KeyEngineEvent.KeyMode.KEY_PRESS) {
					return;
				}
				if (activeGuiComponent != -1) {
					components.get(activeGuiComponent).onKeyPress(e);
				}
			}
		}, k);
	}
}
