package dillon.gameAPI.core;

public class Main {

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		try {
			Core.setup(650, 500, "test", null);
			Core.startGame(30, null, Core.SIDESCROLLER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
