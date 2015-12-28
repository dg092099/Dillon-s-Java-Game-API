package dillon.gameAPI.scripting.bridges;

import dillon.gameAPI.scroller.Camera;
import dillon.gameAPI.security.SecurityKey;

public class CameraBridge {
	public void setX(int x, SecurityKey k) {
		Camera.setX(x, k);
	}

	public int getX() {
		return Camera.getXPos();
	}

	public void setY(int y, SecurityKey k) {
		Camera.setY(y, k);
	}

	public int getY() {
		return Camera.getYPos();
	}
}
