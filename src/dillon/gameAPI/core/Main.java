package dillon.gameAPI.core;

public class Main {

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		try {
			Core.setup(650, 500, "V2.02", null, null);
			Core.startGame(30, null, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
