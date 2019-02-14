package com.company;

import com.company.helpers.LastDayOnFile;
import com.company.helpers.LicenseRegistrar;
import com.company.helpers.ParserHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

class UserSpecificParser {
	private static int months = 0;

	static void parse(String filePath, int months) throws IOException, ParseException {
		UserSpecificParser.months = months;
		parse(filePath);
	}

	static void parse(String filePath) throws IOException, ParseException {
		System.out.println("Starting user specific parser ...");
		long firstDayToCount = 0;
		if (months > 0) {
			long lastDayInMills = LastDayOnFile.checkLastDayOnFile(filePath);
			long daysInMonths = months * 30;
			firstDayToCount = lastDayInMills - TimeUnit.DAYS.toMillis(daysInMonths);
		}

		List<String> allLines = ParserHelper.getAllLinesFromFile(filePath);
		String outputFileName = "output";
		// check for file name existence
		if (new File(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION).exists()) {
			outputFileName = outputFileName + "_" + System.currentTimeMillis();
			System.out.println("User specific output file: " + outputFileName);
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION), StandardCharsets.UTF_8))) {
			String[] datePieces = {};
			writer.write("Date\tUser\tTime using license\n");
			List<LicenseRegistrar> registrars = new ArrayList<>();
			List<LicenseRegistrar> duplicateRegistry = new ArrayList<>();
			boolean foundDate = months < 1;

			// here we loop through all the lines in the file
			for (String line : allLines) {
				if (line.contains("TIMESTAMP") && !foundDate) {
					if (firstDayToCount > 0 && firstDayToCount <= LastDayOnFile.getTimeStampInMillis(line)) {
						foundDate = true;
						datePieces = ParserHelper.getDatePiecesFromTimeStamp(line);
					}
				} else if (foundDate) {
					datePieces = parseLine(writer, datePieces, registrars, duplicateRegistry, line);
				}
			}
		}
		System.out.println("Done!");
	}

	private static String[] parseLine(Writer writer, String[] datePieces, List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries,
									  String line) throws ParseException, IOException {
		String time;
		line = line.trim();
		// lets get the date
		// this case is only if the first log file does not contain any of the other time patterns
		if (line.contains("(lmgrd) FLEXnet Licensing")) {
			//12:41:24 (lmgrd) FLEXnet Licensing (v11.9.0.0 build 87342 x64_n6) started on ors-dlssrv1.ors.nih.gov (IBM PC) (11/7/2014)
			String[] lineSplit = line.split(" ");
			String timeStamp = lineSplit[lineSplit.length - 1].trim().replace("(", "").replace(")", "");
			Date newDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(timeStamp + " " + lineSplit[0].trim());
			datePieces = newDate.toString().split(" ");
			time = datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[5] + " " + datePieces[3];
			datePieces = time.split(" ");
		}

		// 23:32:37 (parteklm) (@parteklm-SLOG@) Time: Sat Apr 11 2015 23:32:37 Eastern Daylight Time
		if (line.contains("Time:")) {
			time = line.split("Time:")[1].trim();
			datePieces = time.split(" ");
		}
		// 23:32:26 (parteklm) (@parteklm-SLOG@) Time: Tue Apr 07 2015 23:32:26 Eastern Daylight Time
		// 1:17:41 (parteklm) TIMESTAMP 4/8/2015
		// 1:32:49 (parteklm) IN: "base" mikamiy@NIAMS01677357M
		// unfortunately we have to deal with situations like the above where the date changes but we dont get the regular log message
		if (line.contains("TIMESTAMP") && line.contains("lmgrd")) {
			datePieces = ParserHelper.getDatePiecesFromTimeStamp(line);
		}

		// now we can start parsing
		if (line.contains("parteklm") &&
				(line.contains("\"flow") || line.contains("base\"") || line.contains("infr\"") || line.contains("pathway_base\""))) {
			if (!line.contains("DENIED")) {
				formatDateAndPrintLine(line, registrars, duplicateRegistries, datePieces, writer);
			}
		}

		if (line.contains("parteklm exited with status")) {
			clearAllRegistrars(registrars, writer);
		}
		return datePieces;
	}

	private static void clearAllRegistrars(Collection<LicenseRegistrar> registrars, Writer writer) throws IOException {
		for (LicenseRegistrar license : registrars) {
			// new SimpleDateFormat("E MMM dd yyyy HH:mm:ss")
			// Wed Jan 28 09:31:36 CST 2015 <- Date formats this way so we have to convert to the log standards above
			String[] splitFormattedDate = license.getCheckOutTime().toString().split(" ");
			String time = splitFormattedDate[0] + " " + splitFormattedDate[1] + " " + splitFormattedDate[2] + " " +
					splitFormattedDate[5] + " " + splitFormattedDate[3];
			String[] datePieces = time.split(" ");
			writer.write(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + "\t");
			writer.write(license.getUser() + "\t");
			long total = 0;
			printTotalUsageString(total, writer);
		}
		registrars.clear();
	}

	private static void formatDateAndPrintLine(String line, List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries,
											   String[] datePieces, Writer writer) throws ParseException, IOException {
		String[] wordsInLine = line.split(" ");
		if (line.contains("OUT:")) {
			addCheckOutToRegistry(registrars, duplicateRegistries, datePieces, wordsInLine);
		}
		if (line.contains("IN:")) {
			removeRegistry(registrars, duplicateRegistries, datePieces, writer, wordsInLine);
		}
	}

	private static void removeRegistry(List<LicenseRegistrar> registrars, Collection<LicenseRegistrar> duplicateRegistries,
	                                   String[] datePieces, Writer writer, String[] wordsInLine) throws ParseException, IOException {
		String lastWord = wordsInLine[wordsInLine.length - 1];
		if (!lastWord.contains("@")) {
			//  6:20:35 (parteklm) IN: "base" weipingchen@DK8R1A11PC31  (SHUTDOWN)
			lastWord = wordsInLine[wordsInLine.length - 3];
		}
		String[] userHost = lastWord.split("@");
		Date dateCheckIn = new SimpleDateFormat(ParserHelper.DATE_AND_TIME_PATTERN)
				.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + wordsInLine[0] + "\t");
		LicenseRegistrar newCheckIn = new LicenseRegistrar(dateCheckIn, userHost[0], userHost[1], wordsInLine[3]);
		if (duplicateRegistries.contains(newCheckIn)) {
			duplicateRegistries.remove(newCheckIn);
		} else if (registrars.contains(newCheckIn)) {
			removeFromRegistry(registrars, datePieces, writer, userHost[0], newCheckIn);
		}
	}

	private static void removeFromRegistry(List<LicenseRegistrar> registrars, String[] datePieces, Writer writer, String user, LicenseRegistrar newCheckIn) throws IOException, ParseException {
		writer.write(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + "\t");
		writer.write(user + "\t");
		long total = newCheckIn.getCheckOutTime().getTime() - registrars.get(registrars.indexOf(newCheckIn)).getCheckOutTime().getTime();
		// TODO: fix this, if total is less than zero we assume the next day
		if (total < 0) {
			total += 24 * 60 * 60 * 1000;
		}
		printTotalUsageString(total, writer);
		registrars.remove(newCheckIn);
	}

	private static void addCheckOutToRegistry(List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries, String[] datePieces, String[] wordsInLine) throws ParseException {
		// we separate the user from the host tales@superPC
		String[] userHost = wordsInLine[wordsInLine.length - 1].split("@");
		Date dateCheckOut = new SimpleDateFormat(ParserHelper.DATE_AND_TIME_PATTERN)
				.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + wordsInLine[0]);
		LicenseRegistrar newRegistry = new LicenseRegistrar(dateCheckOut, userHost[0], userHost[1], wordsInLine[3]);
		checkForDuplicateEntry(registrars, duplicateRegistries, newRegistry);
	}

	private static void checkForDuplicateEntry(List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries, LicenseRegistrar newRegistry) {
		if (!registrars.contains(newRegistry)) {
			registrars.add(newRegistry);
		} else {
			duplicateRegistries.add(newRegistry);
		}
	}

	private static void printTotalUsageString(long total, Writer writer) throws IOException {
		int seconds = (int) (total / 1000) % 60;
		int minutes = (int) ((total / (1000 * 60)) % 60);
		int hours = (int) ((total / (1000 * 60 * 60)) % 24);
		writer.write(hours + ":" + minutes + ":" + seconds + "\n");
	}
}
