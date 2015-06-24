package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
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
 * ============================
 *  total checkouts: 464
 *  total denied: 671
 *  total weekend checkouts: 54
 *  total weekend denies: 0
 *  total days on log: 43
 * ============================
 */
public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Usage: log_parser /path/to/logfile");
			return;
		}

		// initialize variables
		List<String> allLines = getAllLinesFromFile(args[0]);
		String currentDay = "";
		int daysOnLog = 0;
		int checkoutCount = 0;
		int deniedCount = 0;
		int checkoutCountTotal = 0;
		int deniedCountTotal = 0;
		int weekendCheckOuts = 0;
		int weekendDenies = 0;
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
						daysOnLog++;
						// for formatting purposes we skip the first loop for the counts
						if (!firstLoop) {
							writer.write("\t" + checkoutCount + "\t" + deniedCount + "\n");
						}
						// but still prints the date
						writer.write(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + "\t");
						currentDay = weekDay;
						checkoutCount = 0;
						deniedCount = 0;
					}
				}

				// if it is from the license we want we start counting the features checked out and denied
				if (line.contains("parteklm") && line.contains("base")) {
					firstLoop = false;
					// here we separate weekends from weekdays
					if (time.startsWith("Sat") || time.startsWith("Sun")) {
						if (line.contains("OUT:")) {
							checkoutCount++;
							weekendCheckOuts++;
						}
						if (line.contains("DENIED:")) {
							deniedCount++;
							weekendDenies++;
						}
					} else {
						if (line.contains("OUT:")) {
							checkoutCount++;
							checkoutCountTotal++;
						}
						if (line.contains("DENIED:")) {
							deniedCount++;
							deniedCountTotal++;
						}
					}
				}
			}
			// last print of the counts
			writer.write("\t" + checkoutCount + "\t" + deniedCount + "\n");
		}

		printResume(daysOnLog, checkoutCountTotal, deniedCountTotal, weekendCheckOuts, weekendDenies);
	}

	private static List<String> getAllLinesFromFile(String arg) {
		File logFile = new File(arg);
		List<String> allLines = new ArrayList<>();
		if (logFile.canRead()) {
			try {
				allLines = Files.readAllLines(logFile.toPath(), Charset.defaultCharset());
			} catch (IOException e) {
				System.out.println(e.getCause());
			}
		}
		if (allLines.size() < 1) {
			System.out.println("File does not contain anything");
		}
		return allLines;
	}

	private static void printResume(int daysOnLog, int checkoutCountTotal, int deniedCountTotal, int weekendCheckOuts, int weekendDenies) {
		System.out.println("============================");
		System.out.println(" total checkouts: " + checkoutCountTotal);
		System.out.println(" total denied: " + deniedCountTotal);
		System.out.println(" total weekend checkouts: " + weekendCheckOuts);
		System.out.println(" total weekend denies: " + weekendDenies);
		System.out.println(" total days on log: " + daysOnLog);
		System.out.println("============================");
	}
}
