package com.company;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * This small code is to parse a lgo file for a specific request from Partek
 * <p/>
 * The input has a lot of noise but we are looking for only these:
 * 15:34:04 (parteklm) (@parteklm-SLOG@) Time: Thu Apr 30 2015 15:34:04 Eastern Daylight Time
 * 17:08:46 (parteklm) OUT: "base" asdsad@ASDASD376
 * 15:36:36 (parteklm) DENIED: "base" asdsd@NSDNA78  (Licensed number of users already reached. (-4,342))
 * <p/>
 * Output:
 * A CSV file:
 * Date	Checkout count	Denied count
 * Fri Mar 27 2015		13	3
 * Sat Mar 28 2015		0	0
 * Sun Mar 29 2015		1	0
 * Mon Mar 30 2015		7	1
 *s
 * And a run resume:
 * ========== Totals ==========
 *  weekday checkouts: 464
 *  weekday denies: 671
 *  weekdays: 31
 *  weekend checkouts: 54
 *  weekend denies: 0
 *  weekend days: 12
 *  days on log: 43
 * ============================
 * ========= Averages =========
 *  weekday checkouts: 14
 *  weekday denied: 21
 *  weekend checkouts: 4
 *  weekend denies: 0
 * ============================
 */
public class DayByDayParser {
	public static void parse(String filePath) throws IOException {
		// initialize variables
		List<String> allLines = ParserHelper.getAllLinesFromFile(filePath);
		String currentDay = "";
		CounterHelper counterHelper = new CounterHelper();

		boolean firstLoop = true;

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("output.csv"), "utf-8"))) {

			writer.write("Date\tCheckout count\tDenied count\n");
			String time = "";
			// here we loop through all the lines in the file
			for (String line : allLines) {
				line = line.trim();
				// lets get the date

				if (line.contains("Time:")) {
					time = line.split("Time:")[1].trim();
					String[] datePieces = time.split(" ");
					String weekDay = datePieces[0];

					if (!weekDay.equals(currentDay)) {
						currentDay = getNewDay(counterHelper, firstLoop, writer, time, datePieces, weekDay);
					}
				}

				// if it is from the license we want we start counting the features checked out and denied
				if (line.contains("parteklm") && line.contains("base")) {
					firstLoop = featureLineFound(counterHelper, time, line);
				}
			}
			// last print of the counts
			writer.write("\t" + counterHelper.getCheckoutCount() + "\t" + counterHelper.getDeniedCount() + "\n");
		}

		ParserHelper.printResume(counterHelper.getWeekDaysOnLog(), counterHelper.getCheckoutCountTotal(),
				counterHelper.getDeniedCountTotal(), counterHelper.getWeekendCheckOuts(),
				counterHelper.getWeekendDenies(), counterHelper.getWeekendDaysOnLog());
	}

	private static boolean featureLineFound(CounterHelper counterHelper, String time, String line) {
		// here we separate weekends from weekdays
		if (time.startsWith("Sat") || time.startsWith("Sun")) {
			if (line.contains("OUT:")) {
				counterHelper.incrementCheckoutCount();
				counterHelper.incrementWeekendCheckOuts();
			}
			if (line.contains("DENIED:")) {
				counterHelper.incrementDeniedCount();
				counterHelper.incrementWeekendDenies();
			}
		} else {
			if (line.contains("OUT:")) {
				counterHelper.incrementCheckoutCount();
				counterHelper.incrementCheckoutCountTotal();
			}
			if (line.contains("DENIED:")) {
				counterHelper.incrementDeniedCount();
				counterHelper.incrementDeniedCountTotal();
			}
		}
		return false;
	}

	private static String getNewDay(CounterHelper counterHelper, boolean firstLoop, Writer writer, String time, String[] datePieces, String weekDay) throws IOException {
		String currentDay;
		if (time.startsWith("Sat") || time.startsWith("Sun")) {
			counterHelper.incrementWeekendDaysOnLog();
		} else {
			counterHelper.incrementWeekDaysOnLog();
		}
		// for formatting purposes we skip the first loop for the counts
		if (!firstLoop) {
			writer.write("\t" + counterHelper.getCheckoutCount() + "\t" + counterHelper.getDeniedCount() + "\n");
		}
		// but still prints the date
		writer.write(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + "\t");
		currentDay = weekDay;
		counterHelper.resetCheckoutCount();
		counterHelper.resetDeniedCount();
		return currentDay;
	}
}
