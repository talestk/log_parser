package com.company;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * This is a simple counter which parses a log file and outputs the results (tab delimited)
 * <p/>
 * 10:41:30 (parteklm) OUT: "base" tangw3@NCI-02034032-ML<br/>
 * 10:42:24 (parteklm) IN: "base" tangw3@NCI-02034032-ML<br/>
 * 13:11:55 (parteklm) DENIED: "base" ciuccit@NCI-01978755-ML  (Licensed number of users already reached. (-4,342:10054 ""))<br/>
 * 11:46:28 (parteklm) UNSUPPORTED: "pathway_base" (PORT_AT_HOST_PLUS   ) nicolaea@NCI-01831285  (License server system does not support this feature. (-18,327))<br/>
 * <p/>
 * User Checkouts Checkins Denies Unsupported <br/>
 * Tales 1 1 0 1 <br/>
 * <p/>
 * ...
 * <p/>
 * At the end it will print the total of all the attempts to check out licenses on the console<br/>
 * Total Outs:<br/>
 * Total Ins:<br/>
 * Total Denies:<br/>
 * Total Unsupported:<br/>
 */
class SimpleUserActionsCounter {
	private static int months = 0;

	static void parse(String filePath, int months) throws IOException, ParseException {
		SimpleUserActionsCounter.months = months;
		parse(filePath);
	}

	static void parse(String filePath) throws IOException, ParseException {
		System.out.println("Starting overall parser ...");
		long firstDayToCount = 0;
		if (months > 0) {
			long lastDayInMills = checkLastDayOnFile(filePath);
			long daysInMonths = months * 30;
			firstDayToCount = lastDayInMills - TimeUnit.DAYS.toMillis(daysInMonths);
		}

		// initialize variables
		List<String> allLines = ParserHelper.getAllLinesFromFile(filePath);
		String outputFileName = "output";
		// check for file name existence
		if (new File(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION).exists()) {
			outputFileName = outputFileName + "_" + System.currentTimeMillis();
			System.out.println("-s output file: " + outputFileName);
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION), "utf-8"))) {
			// print the header
			writer.write("User\tCheckout\tCheckins\tDenies\tUnsupported\n");

			// this set will hold all the users on the log
			HashSet<Object> userSet = new HashSet<>();
			// this will have an object of all UserActions on the log
			List<UserActions> simpleUserActionsCounterList = new ArrayList<>();

			boolean foundDate = false;
			// lets loop through all the lines on the log
			for (String line : allLines) {
				if (line.contains("TIMESTAMP") && !foundDate) {
					if (firstDayToCount > 0 && firstDayToCount <= getTimeStampInMillis(line)) {
						foundDate = true;
					}
				} else if (foundDate) {
					parseLine(userSet, simpleUserActionsCounterList, line);
				}
			}
			writeAndPrintOutput(writer, userSet, simpleUserActionsCounterList);
		}
	}

	/**
	 * Check the last lines of the file for a possible last date seen on logs
	 * @param filePath the file log to be parsed
	 * @return last known date in milliseconds
	 * @throws ParseException if line has the wrong format
	 */
	private static long checkLastDayOnFile(String filePath) throws ParseException {
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
	private static long getTimeStampInMillis(String line) throws ParseException {
		String[] lineSplit = line.split(" ");
		String timeStamp = lineSplit[lineSplit.length - 1].trim();
		Date newDate = new SimpleDateFormat("MM/dd/yyyy").parse(timeStamp);
		return newDate.getTime() + TimeUnit.DAYS.toMillis(1);
	}

	private static void parseLine(Collection<Object> userSet, Collection<UserActions> simpleUserActionsCounterList, String string) {
		string = string.trim();

		// lets find only the lines we are interested on
		if (string.contains("parteklm") &&
				(string.contains("\"flow") || string.contains("base\"") || string.contains("infr\"") || string.contains("pathway_base\""))) {
			String[] user = string.split(" ");

			// 10:41:30 (parteklm) OUT: "base" tangw3@NCI-02034032-ML
			// WARNING: sometimes it can have spaces on the user such as "Jacky Wong@JackyWong-PC"
			// the user is the fourth piece after splitting
			String newUser = user[4];
			if (!newUser.contains("@")) {
				newUser = user[4] + user[5];
			}

			// lets make sure it is a user
			if (newUser.contains("@")) {
				userSet.add(newUser);
				simpleUserActionsCounterList.add(new UserActions(user[2].trim().replace(":", ""), newUser.trim()));
			} else if (string.contains("UNSUPPORTED")) {
				if (string.contains("\"infr\"")) { // this is a special case that looks like
					//22:11:33 (parteklm) UNSUPPORTED: "infr" (08AD 4E27 354C F88F ) zhangs9@NCI-01990683  (License server system does not support this feature. (-18,327:10054 ""))
					simpleUserActionsCounterList.add(new UserActions(user[2].trim().replace(":", ""), user[9].trim()));
				} else if (string.contains("\"pathway_base\"")) {
					//10:24:50 (parteklm) UNSUPPORTED: "pathway_base" (PORT_AT_HOST_PLUS   ) fisherbe@NCI-01874123  (License server system does not support this feature. (-18,327))
					simpleUserActionsCounterList.add(new UserActions(user[2].trim().replace(":", ""), user[8].trim()));
				}
			} else { // here we print the lines left just in case there are new licenses to take care
				System.out.println(string);
			}
		}
	}

	private static void writeAndPrintOutput(Writer writer, Collection<Object> userSet, Collection<UserActions> simpleUserActionsCounterList) throws IOException {
		// these will help us keep a total count
		int totalOut = 0;
		int totalIn = 0;
		int totalDenied = 0;
		int totalUnsupported = 0;

		// lets loop through all distinct users and check how many each checked out/in, got denied or tried an unsupported feature
		for (Object user : userSet.toArray()) {
			int out = Collections.frequency(simpleUserActionsCounterList, new UserActions("OUT", user.toString()));
			totalOut += out;
			int in = Collections.frequency(simpleUserActionsCounterList, new UserActions("IN", user.toString()));
			totalIn += in;
			int denied = Collections.frequency(simpleUserActionsCounterList, new UserActions("DENIED", user.toString()));
			totalDenied += denied;
			int unsupported = Collections.frequency(simpleUserActionsCounterList, new UserActions("UNSUPPORTED", user.toString()));
			totalUnsupported += unsupported;
			writer.write(user + "\t" + out + "\t" + in + "\t" + denied + "\t" + unsupported + "\n");
		}
		System.out.println("Total Out: " + totalOut);
		System.out.println("Total In: " + totalIn);
		System.out.println("Total Denied: " + totalDenied);
		System.out.println("Total Unsupported: " + totalUnsupported);
	}
}
