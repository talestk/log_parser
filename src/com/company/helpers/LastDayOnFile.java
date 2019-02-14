package com.company.helpers;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LastDayOnFile {
	/**
	 * Check the last lines of the file for a possible last date seen on logs
	 * @param filePath the file log to be parsed
	 * @return last known date in milliseconds
	 * @throws ParseException if line has the wrong format
	 */
	public static long checkLastDayOnFile(String filePath) throws ParseException {
		String last200Lines = Tail.tail(new File(filePath), 50);
		long timeStampInMillis = 0;
		if (last200Lines != null && last200Lines.contains("TIMESTAMP")) {
			String[] lines = last200Lines.split(System.getProperty("line.separator"));
			for (String line : lines) {
				if (line.contains("TIMESTAMP")) {
					timeStampInMillis = getTimeStampInMillis(line);
				}
			}
		}
		return timeStampInMillis;
	}

	/**
	 * Converts a date on the format MM/dd/yyyy to milliseconds
	 * @param line line to be parsed
	 * @return milliseconds since 1970
	 * @throws ParseException if can't parse the line
	 */
	public static long getTimeStampInMillis(String line) throws ParseException {
		line = line.trim();
		String[] lineSplit = line.split(" ");
		String timeStamp = lineSplit[lineSplit.length - 1].trim();
		Date newDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(timeStamp + " " + lineSplit[0].trim());
		return newDate.getTime();

	}
}
