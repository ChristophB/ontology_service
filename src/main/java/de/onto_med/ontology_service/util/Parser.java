package de.onto_med.ontology_service.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class Parser {
	/**
	 * A list of supported date patterns.
	 */
	private static final List<String> DATE_PATTERNS = Arrays.asList("dd.MM.yyyy", "yyyy-MM-dd");

	/**
	 * Transforms a string into a java Date object.
	 * See {@link #DATE_PATTERNS} for allowed patterns.
	 * @param string String representation of a date.
	 * @return The parsed Date object.
	 * @throws ParseException If the string could not be parsed to Date.
	 */
	public static Date parseStringToDate(String string) throws ParseException {
		Date date = null;

		for (String pattern : DATE_PATTERNS) {
			DateFormat format = new SimpleDateFormat(pattern);
			try {
				date = format.parse(string);
			} catch (ParseException ignored) { }
		}
		if (date == null) throw new ParseException("Could not parse string '" + string + "' to Date.", 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}
}
