package dillon.gameAPI.utils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import dillon.gameAPI.security.RequestedAction;
import dillon.gameAPI.security.SecurityKey;
import dillon.gameAPI.security.SecuritySystem;

/**
 * This class implements a timed execution of runnable objects.
 *
 * @author Dillon - Github dg092099
 *
 */
public class Scheduling {
	/**
	 * This class will do the actual waiting.
	 *
	 * @author Dillon - Github dg092099
	 *
	 */
	static class Waiter implements Runnable {
		private final TimeUnit unit;
		private final long time;
		private final Runnable run;
		private final Thread t;

		/**
		 * This will create the waiter and will set it up.
		 *
		 * @param unit
		 *            The time unit.
		 * @param time
		 *            The time to wait.
		 * @param r
		 *            The runnable to run after the timer.
		 */
		public Waiter(TimeUnit unit, long time, Runnable r) {
			this.unit = unit;
			this.time = time;
			run = r;
			t = new Thread(this);
			t.setName("Scheduler - " + r.hashCode());
			t.start();
		}

		private boolean execute = true;

		@Override
		public void run() {
			try {
				unit.sleep(time);
			} catch (InterruptedException e) {
				System.out.println("Interupted " + run.hashCode());
				execute = false;
			}
			if (execute)
				run.run();
			waiters.remove(this);
		}
	}

	private final static ArrayList<Waiter> waiters = new ArrayList<Waiter>();
	private final static ArrayList<Repeater> repeaters = new ArrayList<Repeater>();

	/**
	 * This method schedules something to happen.
	 *
	 * @param unit
	 *            The unit of time to wait.
	 * @param time
	 *            The time to wait.
	 * @param run
	 *            The runnable to run after the time out.
	 * @param k
	 *            The security key
	 */
	public static void scheduleWaiting(TimeUnit unit, long time, Runnable run, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SCHEDULE_WAIT);
		Waiter w = new Waiter(unit, time, run);
		waiters.add(w);
	}

	/**
	 * Schedules a runnable to repeat.
	 *
	 * @param unit
	 *            The time unit.
	 * @param time
	 *            The time to wait.
	 * @param times
	 *            The amount of times to do it.
	 * @param run
	 *            The runnable to run.
	 * @param k
	 *            The security key
	 */
	public static void scheduleRepeating(TimeUnit unit, long time, int times, Runnable run, SecurityKey k) {
		Repeater r = new Repeater(unit, time, times, run);
		repeaters.add(r);
	}

	/**
	 * Schedules a runnable to repeat infinitely.
	 *
	 * @param unit
	 *            The unit of time.
	 * @param time
	 *            The amount of time.
	 * @param run
	 *            The runnable.
	 * @param k
	 *            The security key
	 */
	public static void scheduleRepeating(TimeUnit unit, long time, Runnable run, SecurityKey k) {
		SecuritySystem.checkPermission(k, RequestedAction.SCHEDULE_REPEAT);
		Repeater r = new Repeater(unit, time, run);
		repeaters.add(r);
	}

	/**
	 * A utility class to repeat runnables.
	 *
	 * @author Dillon - Github dg092099
	 *
	 */
	static class Repeater implements Runnable {
		private final TimeUnit waitUnit;
		private final long waitTime;
		private final int timesToRepeat;
		private final Runnable run;
		private final Thread t;

		public Repeater(TimeUnit unit, long time, int times, Runnable run) {
			waitUnit = unit;
			waitTime = time;
			timesToRepeat = Math.abs(times);
			this.run = run;
			t = new Thread(this);
			t.setName("Scheduler " + run.hashCode());
			t.start();
		}

		public Repeater(TimeUnit unit, long time, Runnable r) {
			waitUnit = unit;
			waitTime = time;
			run = r;
			timesToRepeat = -1;
			t = new Thread(this);
			t.setName("Scheduler " + r.hashCode());
			t.start();
		}

		@Override
		public void run() {
			if (timesToRepeat == -1)
				while (true)
					try {
						waitUnit.sleep(waitTime);
						run.run();
					} catch (InterruptedException e) {
						System.out.println("Schedule interupted " + run.hashCode());
					}
			else
				for (int counter = 0; counter < timesToRepeat; counter++)
					try {
						waitUnit.sleep(waitTime);
						run.run();
					} catch (InterruptedException e) {
						System.out.println("Schedule interupted " + run.hashCode());
						return;
					}
			repeaters.remove(this);
		}
	}
}
