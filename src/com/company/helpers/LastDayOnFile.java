package com.company.helpers;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LastDayOnFile {
	/**
	 * Check the last lines of the file for a possible last date seen on logs
	 * @param filePath the file log to be parsed
	 * @return last known date in milliseconds
	 * @throws ParseException if line has the wrong format
	 */
	public static long checkLastDayOnFile(String filePath) throws ParseException {
		String last200Lines = Tail.tail(new File(filePath), 200);
		if (last200Lines != null && last200Lines.contains("TIMESTAMP")) {
			String[] lines = last200Lines.split(System.getProperty("line.separator"));
			for (String line : lines) {
				if (line.contains("TIMESTAMP")) {
					return getTimeStampInMillis(line);
				}
			}
		} else {
			System.out.println("Could not find any occurrence of TIMESTAMP in the last 200 lines");
			return 0;
		}
		return 0;
	}

	/**
	 * Converts a date on the format MM/dd/yyyy to milliseconds
	 * @param line line to be parsed
	 * @return milliseconds since 1970
	 * @throws ParseException if can't parse the line
	 */
	public static long getTimeStampInMillis(String line) throws ParseException {
		String[] lineSplit = line.split(" ");
		String timeStamp = lineSplit[lineSplit.length - 1].trim();
		Date newDate = new SimpleDateFormat("MM/dd/yyyy").parse(timeStamp);
		return newDate.getTime() + TimeUnit.DAYS.toMillis(1);
	}
}
