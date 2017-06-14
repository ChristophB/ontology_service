package de.onto_med.webprotege_rest_api.api;

import java.text.DecimalFormat;

public class Timer {
	private long start;
	private long current;
	private DecimalFormat format = new DecimalFormat("#####");
	
	public Timer() {
		start   = System.nanoTime();
		current = start;
	}
	
	public String getDiff() {
		long newCurrent = System.nanoTime();
		String duration = "@" + format.format((newCurrent - current) / Math.pow(10, 6)) + "ms";
		current = newCurrent;
		return duration;
	}
	
	public String getDiffFromStart() {
		current = System.nanoTime();
		return "@" + format.format((current - start) / Math.pow(10, 6)) + "ms";
	}
}
