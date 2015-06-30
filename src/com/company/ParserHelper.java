package com.company;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
		if (weekDaysOnLog == 0) { weekDaysOnLog = 1; }
		if (weekendDaysOnLog == 0) { weekendDaysOnLog = 1; }
		System.out.println("========== Totals ==========");
		System.out.println(" weekday checkouts: " + checkoutCountTotal);
		System.out.println(" weekday denies: " + deniedCountTotal);
		System.out.println(" weekdays: " + weekDaysOnLog);
		System.out.println(" weekend checkouts: " + weekendCheckOuts);
		System.out.println(" weekend denies: " + weekendDenies);
		System.out.println(" weekend days: " + weekendDaysOnLog);
		System.out.println(" days on log: " + (weekDaysOnLog + weekendDaysOnLog));
		System.out.println("============================");
		DecimalFormat df = new DecimalFormat("0.0");
		System.out.println("========= Averages =========");
		System.out.println(" weekday checkouts: " + df.format(Float.valueOf(checkoutCountTotal) / weekDaysOnLog));
		System.out.println(" weekday denied: " + df.format(Float.valueOf(deniedCountTotal)	 / weekDaysOnLog));
		System.out.println(" weekend checkouts: " + df.format(Float.valueOf(weekendCheckOuts) / weekendDaysOnLog));
		System.out.println(" weekend denies: " + df.format(Float.valueOf(weekendDenies) / weekendDaysOnLog));
		System.out.println("============================");
	}

	public static void listFilesForFolder(final File folder, String filenamePattern) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName().startsWith(filenamePattern)) {
				System.out.println(fileEntry.getName());
			}
		}
	}

	// http://stackoverflow.com/questions/1978933/a-quick-and-easy-way-to-join-array-elements-with-a-separator-the-opposite-of-sp
	public static String strJoin(String[] aArr, String sSep) {
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0)
				sbStr.append(sSep);
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}

	public static String[] getDatePiecesFromTimeStamp(String line) throws ParseException {
		String time;
		String[] datePieces;
		String[] lineSplit = line.split(" ");
		String timeStamp = lineSplit[lineSplit.length - 1].trim();
		Date newDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(timeStamp + " " + lineSplit[0].trim());
		// new SimpleDateFormat("E MMM dd yyyy HH:mm:ss")
		// Wed Jan 28 09:31:36 CST 2015 <- Date formats this way so we have to convert to the log standards above
		String[] splitFormattedDate = newDate.toString().split(" ");
		time = splitFormattedDate[0] + " " + splitFormattedDate[1] + " " + splitFormattedDate[2] + " " +
				splitFormattedDate[5] + " " + splitFormattedDate[3];
		datePieces = time.split(" ");
		return datePieces;
	}
}
