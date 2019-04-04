package com.company.helpers;

import com.company.helpers.CounterHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParserHelper {
	public static final String OUTPUT_FILE_EXTENSION = ".tsv";
	public static final String DATE_AND_TIME_PATTERN = "E MMM dd yyyy HH:mm:ss";

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

	public static void printResume(CounterHelper counterHelper) {

		int weekDaysOnLog = counterHelper.weekDaysOnLog.get();
		if (weekDaysOnLog == 0) { weekDaysOnLog = 1; }
		int weekendDaysOnLog = counterHelper.weekendDaysOnLog.get();
		if (weekendDaysOnLog == 0) { weekendDaysOnLog = 1; }
		int checkoutCountTotal = counterHelper.checkoutCountTotal.get();
		int deniedCountTotal = counterHelper.deniedCountTotal.get();
		int weekendCheckOuts = counterHelper.weekendCheckOuts.get();
		int weekendDenies = counterHelper.weekendDenies.get();
		int weekDaysLicenseChecked = counterHelper.weekDaysLicenseChecked.get();
		if (weekDaysLicenseChecked == 0) { weekDaysLicenseChecked = 1; }
		int weekendDaysLicenseChecked = counterHelper.weekendDaysLicenseChecked.get();
		if (weekendDaysLicenseChecked == 0) { weekendDaysLicenseChecked = 1; }

		System.out.println("========== Totals ==========");
		System.out.println(" Weekday checkouts: " + checkoutCountTotal);
		System.out.println(" Weekday denies: " + deniedCountTotal);
		System.out.println(" Weekdays: " + weekDaysOnLog);
		System.out.println(" Weekend checkouts: " + weekendCheckOuts);
		System.out.println(" Weekend denies: " + weekendDenies);
		System.out.println(" Weekend days: " + weekendDaysOnLog);
		System.out.println(" Days parsed on log: " + (weekDaysOnLog + weekendDaysOnLog));
		System.out.println(" Days that licenses were used: " + (weekDaysLicenseChecked + weekendDaysLicenseChecked));
		System.out.println("============================");
		DecimalFormat df = new DecimalFormat("0.0");
		System.out.println("========= Averages =========");
		System.out.println(" Weekday checkouts: " + df.format((float) checkoutCountTotal / weekDaysLicenseChecked));
		System.out.println(" Weekday denied: " + df.format((float) deniedCountTotal / weekDaysOnLog));
		System.out.println(" Weekend checkouts: " + df.format((float) weekendCheckOuts / weekendDaysLicenseChecked));
		System.out.println(" Weekend denies: " + df.format((float) weekendDenies / weekendDaysOnLog));
		System.out.println("============================");
	}

	public static File concatAllFilesWhichStartsWith(final File folder, String filenamePattern) throws IOException {
		System.out.println("Concatenating and parsing all files...");
		File[] allFolderFiles = folder.listFiles();
		assert allFolderFiles != null;
		Charset charset = StandardCharsets.UTF_8;
		List<String> allContent = new ArrayList<>();
		File tempFile = new File("tempFile"+System.currentTimeMillis());
		for (final File fileEntry : allFolderFiles) {
			if (fileEntry.getName().startsWith(filenamePattern)) {
				allContent.addAll(Files.readAllLines(fileEntry.toPath(), charset));
			}
		}
		Files.write(tempFile.toPath(), allContent, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		return tempFile;
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
		line = line.trim();
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
