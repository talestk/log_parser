package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSpecificParser {
	private static int LINE_COUNTER_LIMIT = 0;

	public static void parse(String filePath) throws IOException, ParseException {
		List<String> allLines = ParserHelper.getAllLinesFromFile(filePath);
		String outputFileName = "output.csv";
		// check for file name existence
		if (new File(outputFileName).exists()) {
			outputFileName = outputFileName + "_" + System.currentTimeMillis();
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFileName), "utf-8"))) {
			String[] datePieces = {};
			writer.write("Date\tUser\tTime using license\n");
			List<LicenseRegistrar> registrars = new ArrayList<>();
			List<LicenseRegistrar> duplicateRegistry = new ArrayList<>();
			// here we loop through all the lines in the file
			for (String line : allLines) {
				datePieces = parseLine(writer, datePieces, registrars, duplicateRegistry, line, allLines.indexOf(line)-1, allLines);
			}
		}
	}

	private static String[] parseLine(Writer writer, String[] datePieces, List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries,
									  String line, int lineIndex, List<String> allLines) throws ParseException, IOException {
		String time;
		line = line.trim();
		// lets get the date
		// 23:32:37 (parteklm) (@parteklm-SLOG@) Time: Sat Apr 11 2015 23:32:37 Eastern Daylight Time
		if (line.contains("Time:")) {
			time = line.split("Time:")[1].trim();
			datePieces = time.split(" ");
		}
		// 23:32:26 (parteklm) (@parteklm-SLOG@) Time: Tue Apr 07 2015 23:32:26 Eastern Daylight Time
		// 1:17:41 (parteklm) TIMESTAMP 4/8/2015
		// 1:32:49 (parteklm) IN: "base" mikamiy@NIAMS01677357M
		// unfortunately we have to deal with situations like the above where the date changes but we dont get the regular log message
		if (line.contains("TIMESTAMP") && line.contains("parteklm")) {
			datePieces = ParserHelper.getDatePiecesFromTimeStamp(line);
		}

		// now we can start parsing
		if (line.contains("parteklm") && line.contains("base")) {
			formatDateAndPrintLine(line, registrars, duplicateRegistries, datePieces, writer, lineIndex, allLines);
		}
		return datePieces;
	}

	private static void formatDateAndPrintLine(String line, List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries,
											   String[] datePieces, Writer writer, int lineIndex, List<String> allLines) throws ParseException, IOException {
		String[] wordsInLine = line.split(" ");
		if (line.contains("OUT:")) {
			addCheckOutToRegistry(registrars, duplicateRegistries, datePieces, wordsInLine);
		}
		if (line.contains("IN:")) {
			removeRegistry(line, registrars, duplicateRegistries, datePieces, writer, lineIndex, allLines, wordsInLine);
		}
	}

	private static void removeRegistry(String line, List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries,
									   String[] datePieces, Writer writer, int lineIndex, List<String> allLines, String[] wordsInLine) throws ParseException, IOException {
		String[] userHost = wordsInLine[wordsInLine.length - 1].split("@");
		Date dateCheckIn = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss")
				.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + wordsInLine[0] + "\t");
		LicenseRegistrar newCheckIn = new LicenseRegistrar(dateCheckIn, userHost[0], userHost[1]);
		if (duplicateRegistries.contains(newCheckIn)) {
			duplicateRegistries.remove(newCheckIn);
		} else if (registrars.contains(newCheckIn)) {
			removeFromRegistry(line, registrars, duplicateRegistries, datePieces, writer, lineIndex, allLines, userHost[0], newCheckIn);
		}
	}

	private static void removeFromRegistry(String line, List<LicenseRegistrar> registrars, List<LicenseRegistrar> duplicateRegistries, String[] datePieces, Writer writer, int lineIndex, List<String> allLines, String user, LicenseRegistrar newCheckIn) throws IOException, ParseException {
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
		Date dateCheckOut = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss")
				.parse(datePieces[0] + " " + datePieces[1] + " " + datePieces[2] + " " + datePieces[3] + " " + wordsInLine[0]);
		LicenseRegistrar newRegistry = new LicenseRegistrar(dateCheckOut, userHost[0], userHost[1]);
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
