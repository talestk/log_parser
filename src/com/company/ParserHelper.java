package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ParserHelper {

	public static List<String> getAllLinesFromFile(String arg) {
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

	public static void printResume(int weekDaysOnLog, int checkoutCountTotal, int deniedCountTotal, int weekendCheckOuts, int weekendDenies, int weekendDaysOnLog) {
		System.out.println("========== Totals ==========");
		System.out.println(" weekday checkouts: " + checkoutCountTotal);
		System.out.println(" weekday denies: " + deniedCountTotal);
		System.out.println(" weekdays: " + weekDaysOnLog);
		System.out.println(" weekend checkouts: " + weekendCheckOuts);
		System.out.println(" weekend denies: " + weekendDenies);
		System.out.println(" weekend days: " + weekendDaysOnLog);
		System.out.println(" days on log: " + (weekDaysOnLog + weekendDaysOnLog));
		System.out.println("============================");
		System.out.println("========= Averages =========");
		System.out.println(" weekday checkouts: " + checkoutCountTotal / weekDaysOnLog);
		System.out.println(" weekday denied: " + deniedCountTotal / weekDaysOnLog);
		System.out.println(" weekend checkouts: " + weekendCheckOuts / weekendDaysOnLog);
		System.out.println(" weekend denies: " + weekendDenies / weekendDaysOnLog);
		System.out.println("============================");
	}
}
