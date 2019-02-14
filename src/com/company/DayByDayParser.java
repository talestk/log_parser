package com.company;

import com.company.helpers.CounterHelper;
import com.company.helpers.LastDayOnFile;
import com.company.helpers.ParserHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
class DayByDayParser {
	private static int months = 0;

	static void parse(String filePath, int months) throws IOException, ParseException {
		DayByDayParser.months = months;
		parse(filePath);
	}

	static void parse(String filePath) throws IOException, ParseException {
		System.out.println("Starting overall parser ...");
		long firstDayToCount = 0;
		if (months > 0) {
			long lastDayInMills = LastDayOnFile.checkLastDayOnFile(filePath);
			long daysInMonths = months * 30;
			firstDayToCount = lastDayInMills - TimeUnit.DAYS.toMillis(daysInMonths);
		}

		// initialize variables
		List<String> allLines = ParserHelper.getAllLinesFromFile(filePath);
		String currentDay = "";
		CounterHelper counterHelper = new CounterHelper();

		boolean firstLoop = true;

		String outputFileName = "output";
		// check for file name existence
		if (new File(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION).exists()) {
			outputFileName = outputFileName + "_" + System.currentTimeMillis();
			System.out.println("Overall output file: " + outputFileName);
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION), "utf-8"))) {

			writer.write("Date\tCheckout count\tDenied count\n");
			String time = "";
			String[] datePieces;
			Date currentDate = new Date(0);
			Date newDate = null;
			boolean isNewDay = false;
			boolean foundDate = months < 1;

			// here we loop through all the lines in the file
			for (String line : allLines) {
				line = line.trim();
				if (line.contains("TIMESTAMP") && !foundDate) {
					if (firstDayToCount > 0 && firstDayToCount <= LastDayOnFile.getTimeStampInMillis(line)) {
						foundDate = true;
					}
				} else if (foundDate) {
					// lets get the date
					// this case is only if the first log file does not contain any of the other time patterns
					if (line.contains("(lmgrd) FLEXnet Licensing")) {
						//12:41:24 (lmgrd) FLEXnet Licensing (v11.9.0.0 build 87342 x64_n6) started on ors-dlssrv1.ors.nih.gov (IBM PC) (11/7/2014)
						String[] lineSplit = line.split(" ");
						String timeStamp = lineSplit[lineSplit.length - 1].trim().replace("(", "").replace(")", "");
						newDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(timeStamp + " " + lineSplit[0].trim());
						datePieces = newDate.toString().split(" ");
						String weekDay = datePieces[0];
						if (!weekDay.equals(currentDay) && newDate.getTime() > currentDate.getTime()) {
							time = datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[5] + " " + datePieces[3];
							currentDate = new SimpleDateFormat(ParserHelper.DATE_AND_TIME_PATTERN).parse(time);
							datePieces = time.split(" ");
							currentDay = getNewDay(counterHelper, firstLoop, writer, time, datePieces, weekDay);
							isNewDay = true;
						}
					}

					// 23:32:26 (parteklm) (@parteklm-SLOG@) Time: Tue Apr 07 2015 23:32:26 Eastern Daylight Time
					if (line.contains("Time:")) {
						time = line.split("Time:")[1].trim();
						datePieces = time.split(" ");
						String weekDay = datePieces[0];
						// weird bug on the logs where the line is broken like:
						// parteklm-SLOG@) Time: Sun Nov 18 2018 16:23:46 AUS Eastern Daylight Time
						if (line.split(" ")[0].startsWith("parteklm")) {
							continue;
						}
						newDate = new SimpleDateFormat(ParserHelper.DATE_AND_TIME_PATTERN)
								.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + line.split(" ")[0]);
						if (!weekDay.equals(currentDay) && newDate != null && newDate.getTime() > currentDate.getTime()) {
							currentDate = new SimpleDateFormat(ParserHelper.DATE_AND_TIME_PATTERN)
									.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + line.split(" ")[0]);
							currentDay = getNewDay(counterHelper, firstLoop, writer, time, datePieces, weekDay);
							isNewDay = true;
						}
					}

					// 23:32:26 (parteklm) (@parteklm-SLOG@) Time: Tue Apr 07 2015 23:32:26 Eastern Daylight Time
					// 1:17:41 (parteklm) TIMESTAMP 4/8/2015
					// 1:32:49 (parteklm) IN: "base" mikamiy@NIAMS01677357M
					// unfortunately we have to deal with situations like the above where the date changes but we dont get the regular log message
					if (line.contains("TIMESTAMP") && (line.contains("parteklm") || line.contains("lmgrd") || line.contains("infr\"") || line.contains("pathway_base\""))) {
						datePieces = ParserHelper.getDatePiecesFromTimeStamp(line);
						time = ParserHelper.strJoin(datePieces, " ");
						String weekDay = datePieces[0];
						newDate = new SimpleDateFormat(ParserHelper.DATE_AND_TIME_PATTERN)
								.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + line.split(" ")[0]);
						if (!weekDay.equals(currentDay) && newDate.getTime() > currentDate.getTime()) {
							currentDate = new SimpleDateFormat(ParserHelper.DATE_AND_TIME_PATTERN)
									.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + line.split(" ")[0]);
							currentDay = getNewDay(counterHelper, firstLoop, writer, time, datePieces, weekDay);
							isNewDay = true;
						}
					}

					// if it is from the license we want we start counting the features checked out and denied
					if (line.contains("parteklm") && newDate != null && newDate.getTime() > currentDate.getTime() &&
							(line.contains("base") || line.contains("infr") || line.contains("pathway_base"))) {
						isNewDay = incrementCountersForNewDay(counterHelper, time, isNewDay);
						firstLoop = featureLineFound(counterHelper, time, line);
					}
				}
			}
			// last print of the counts
			writer.write("\t" + counterHelper.checkoutCount.get() + "\t" + counterHelper.deniedCount.get() + "\n");
		}

		ParserHelper.printResume(counterHelper);
		System.out.println("Done!");
	}

	private static boolean incrementCountersForNewDay(CounterHelper counterHelper, String time, boolean isNewDay) {
		if (isNewDay) {
			if (time.startsWith("Sat") || time.startsWith("Sun")) {
				counterHelper.weekendDaysLicenseChecked.incrementAndGet();
			} else {
				counterHelper.weekDaysLicenseChecked.incrementAndGet();
			}
		}
		return false;
	}

	private static boolean featureLineFound(CounterHelper counterHelper, String time, String line) {
		// here we separate weekends from weekdays
		if (time.startsWith("Sat") || time.startsWith("Sun")) {
			if (line.contains("OUT:")) {
				counterHelper.checkoutCount.incrementAndGet();
				counterHelper.weekendCheckOuts.incrementAndGet();
			}
			if (line.contains("DENIED:")) {
				counterHelper.deniedCount.incrementAndGet();
				counterHelper.weekendDenies.incrementAndGet();
			}
		} else {
			if (line.contains("OUT:")) {
				counterHelper.checkoutCount.incrementAndGet();
				counterHelper.checkoutCountTotal.incrementAndGet();
			}
			if (line.contains("DENIED:")) {
				counterHelper.deniedCount.incrementAndGet();
				counterHelper.deniedCountTotal.incrementAndGet();
			}
		}
		return false;
	}

	private static String getNewDay(CounterHelper counterHelper, boolean firstLoop, Writer writer, String time, String[] datePieces, String weekDay) throws IOException {
		String currentDay;
		if (time.startsWith("Sat") || time.startsWith("Sun")) {
			counterHelper.weekendDaysOnLog.incrementAndGet();
		} else {
			counterHelper.weekDaysOnLog.incrementAndGet();
		}
		// for formatting purposes we skip the first loop for the counts
		if (!firstLoop) {
			writer.write("\t" + counterHelper.checkoutCount.get() + "\t" + counterHelper.deniedCount.get() + "\n");
		}
		// but still prints the date
		writer.write(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + "\t");
		currentDay = weekDay;
		counterHelper.checkoutCount.set(0);
		counterHelper.deniedCount.set(0);
		return currentDay;
	}
}
