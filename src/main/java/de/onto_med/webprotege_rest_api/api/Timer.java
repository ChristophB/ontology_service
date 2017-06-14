package de.onto_med.webprotege_rest_api.api;

import java.text.DecimalFormat;

/**
 * This class can be used to evaluate required time for code sequences.
 * Usage:
 * 		Timer timer = new Timer(); 	// (1)
 * 		timer.getDiff();			// (2), returns the time diff to (1)
 * 		timer.getDiff(); 			// returns the time diff to (2)
 * 		timer.getDiffFromStart(); 	// returns the time diff to (1)
 * @author Christoph Beger
 */
public class Timer {
	/* Construction time */
	private long start;
	/* Last stoped time */
	private long current;
	/* DecimalFormat (up to 5 digets before comma. */
	private DecimalFormat format = new DecimalFormat("#####");
	
	/**
	 * Constructs a new Timer and sets  the start time.
	 */
	public Timer() {
		start   = System.nanoTime();
		current = start;
	}
	
	/**
	 * (Re)sets the last stoped time and returns the difference to the last stop in ms.
	 * @return time diff in ms to the last call of getDiff() or constructor
	 */
	public String getDiff() {
		long newCurrent = System.nanoTime();
		String duration = "@" + format.format((newCurrent - current) / Math.pow(10, 6)) + "ms";
		current = newCurrent;
		return duration;
	}
	
	/**
	 * Returns the time difference to construction time of this object in ms.
	 * @return time diff in ms to construction time
	 */
	public String getDiffFromStart() {
		current = System.nanoTime();
		return "@" + format.format((current - start) / Math.pow(10, 6)) + "ms";
	}
}
