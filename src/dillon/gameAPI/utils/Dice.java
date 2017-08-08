package dillon.gameAPI.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * A utility class to help with random chances.
 *
 * @author Dillon - Github dg092099
 * @since 2.1.0
 */
public class Dice {
	private final Random r;
	private final int[] results;

	/**
	 * Instantiates a dice with the specified outcomes.
	 * 
	 * @param outcomes
	 *            The outcomes.
	 */
	public Dice(int... outcomes) {
		results = outcomes;
		r = new Random();
	}

	/**
	 * Instantiates a dice with the outcomes and weights.
	 * 
	 * @param outcomes
	 *            The outcomes.
	 * @param weights
	 *            The weights.
	 */
	public Dice(int[] outcomes, int[] weights) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		for (int i = 0; i < outcomes.length; i++) {
			for (int k = 0; k < weights[i]; k++) {
				out.add(outcomes[i]);
			}
		}
		results = new int[out.size()];
		for (int i = 0; i < out.size(); i++) {
			results[i] = out.get(i);
		}
		r = new Random();
	}

	private ArrayList<Integer> log = new ArrayList<Integer>();

	/**
	 * Roll a number
	 *
	 * @return The number.
	 */
	public int roll() {
		int res = results[r.nextInt(results.length)];
		log.add(res);
		return res;
	}

	/**
	 * Roll the amount of times specified.
	 *
	 * @param times
	 *            The amount of times.
	 * @return The results.
	 */
	public int[] roll(int times) {
		int[] res = new int[times];
		for (int i = 0; i < times; i++) {
			res[i] = roll();
		}
		return res;
	}

}
