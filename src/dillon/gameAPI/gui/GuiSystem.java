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
	 */
	public static void startGui(GuiComponent gc) {
		components.add(gc);
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
	 */
	public static void removeGui(GuiComponent gc) {
		components.remove(gc);
		lowestIndex = Integer.MAX_VALUE;
		highestIndex = Integer.MIN_VALUE;
		for (GuiComponent comp : components) {
			if (comp.getZIndex() > highestIndex) {
				highestIndex = comp.getZIndex();
			}
			if (comp.getZIndex() < lowestIndex) {
				lowestIndex = comp.getZIndex();
			}
		}
	}

	public GuiSystem() {
		EventSystem.addHandler(new EEHandler<RenderEvent>() {
			@Override
			public void handle(RenderEvent evt) {
				Graphics2D g = evt.getGraphics();
				for (GuiComponent comp : components) {
					comp.render(g);
				}
			}
			@Override
			public Class<RenderEvent> getEventType() {
				return RenderEvent.class;
			}
		});
		EventSystem.addHandler(new EEHandler<TickEvent>() {
			@Override
			public void handle(TickEvent evt) {
				for (GuiComponent comp : components) {
					comp.onUpdate();
				}
			}
			@Override
			public Class<TickEvent> getEventType() {
				return TickEvent.class;
			}
		});
		EventSystem.addHandler(new EEHandler<MouseEngineEvent>() {
			@Override
			public void handle(MouseEngineEvent evt) {
				if (evt.getMouseMode() != MouseEngineEvent.MouseMode.CLICK)
					return;
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
							&& p.getX() <= (comp.getTopLeftCorner()[0] + comp.getSize()[0])
							&& p.getY() <= (comp.getTopLeftCorner()[1] + comp.getSize()[1])) {
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

			@Override
			public Class<MouseEngineEvent> getEventType() {
				return MouseEngineEvent.class;
			}
		});
		EventSystem.addHandler(new EEHandler<KeyEngineEvent>() {
			@Override
			public void handle(KeyEngineEvent evt) {
				KeyEvent e = evt.getKeyEvent();
				if (evt.getMode() != KeyEngineEvent.KeyMode.KEY_PRESS)
					return;
				if (activeGuiComponent != -1) {
					components.get(activeGuiComponent).onKeyPress(e);
				}
			}

			@Override
			public Class<KeyEngineEvent> getEventType() {
				return KeyEngineEvent.class;
			}
		});
	}
}
