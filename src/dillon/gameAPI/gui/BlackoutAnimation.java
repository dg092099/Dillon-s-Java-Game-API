package dillon.gameAPI.gui;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.utils.Animation;

/**
 * This allows a blackout object that supports animation.
 *
 * @author Dillon - Github dg092099
 * @since V2.1.0
 */
public class BlackoutAnimation implements GuiComponent {
	private final Animation anim;
	private final boolean autoDispose;
	private final boolean closable;

	public BlackoutAnimation(Animation a, boolean autoDispose, boolean c) {
		if (a == null) {
			throw new IllegalArgumentException("The animation must not be null.");
		}
		anim = a;
		this.autoDispose = autoDispose;
		closable = c;
	}

	@Override
	public int getZIndex() {
		return -1;
	}

	@Override
	public void bringToFront() {
	}

	@Override
	public void dropBehind() {
	}

	@Override
	public void render(Graphics2D g) {
		g.drawImage(anim.getCurrentFrame(), 0, 0, null);
	}

	@Override
	public void onMouseClickRight(double x, double y) {
	}

	@Override
	public void onMouseClickLeft(double x, double y) {
	}

	@Override
	public void onKeyPress(KeyEvent evt) {
		if ((evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_ESCAPE) && closable) {
			GuiSystem.removeGui(this, null);
		}
	}

	@Override
	public void onUpdate() {
		anim.tick();
		if (anim.isFinished() && autoDispose) {
			GuiSystem.removeGui(this, null);
		}
	}

	@Override
	public int[] getTopLeftCorner() {
		return new int[] { 0, 0 };
	}

	@Override
	public int[] getSize() {
		return new int[] { Core.getWidth(), Core.getHeight() };
	}

	@Override
	public void slide(int x, int y) {
	}

	@Override
	public String getDebug() {
		return "TO DO";
	}

	public boolean isAnimationFinished() {
		return anim.isFinished();
	}
}
