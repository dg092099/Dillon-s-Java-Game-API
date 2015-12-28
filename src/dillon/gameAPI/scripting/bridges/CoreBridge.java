package dillon.gameAPI.scripting.bridges;

import dillon.gameAPI.core.Core;
import dillon.gameAPI.security.SecurityKey;

/**
 * This class is for script use only. It's referenced by the script as "Core"
 * ex. Pause game: Core.pause(security key);
 *
 * @author Dillon - Github dg092099
 *
 */
public class CoreBridge {
	public void pause(SecurityKey k) {
		Core.pauseUpdate(k);
	}

	public void unpause(SecurityKey k) {
		Core.unpauseUpdate(k);
	}

	public void fullScreen(boolean fullscreen, SecurityKey k) {
		Core.setFullScreen(fullscreen, k);
	}

	public int getWidth() {
		return Core.getWidth();
	}

	public int getHeight() {
		return Core.getHeight();
	}

	public void shutdown(boolean hard, SecurityKey k) {
		Core.shutdown(hard, k);
	}

	public void setFPS(int FPS, SecurityKey k) {
		Core.setFPS(FPS, k);
	}
}
