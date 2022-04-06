package com.company;

import com.company.helpers.ParserHelper;
import com.company.helpers.UserSpecificPrettifyHolder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class UserSpecificPrettify {

	static void sumTotalHoursPerDay(String fileName) throws IOException {
		List<String> allLines = ParserHelper.getAllLinesFromFile(fileName);
		allLines.remove(0);
		String currentDay = "";
		List<UserSpecificPrettifyHolder> currentUsers = new ArrayList<>();
		String outputFileName = "user_specific_prettify";
		if (new File(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION).exists()) {
			outputFileName = outputFileName + "_" + System.currentTimeMillis();
			System.out.println("User specific output file: " + outputFileName);
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION), StandardCharsets.UTF_8))) {
			writer.write("Date\tUser\tTime using license\n");
			for (String line : allLines) {
				// 0 = Thu Mar 18 2021
				// 1 = zhangl28
				// 2 = 2:13:40
				String[] lineSplit = line.split("\t");
				// create an object of @UserSpecificPrettifyHolder and parse the hours
				Duration hoursToAdd = UserSpecificPrettifyHolder.parseStrDuration(lineSplit[2]);
				UserSpecificPrettifyHolder currentUser = new UserSpecificPrettifyHolder(hoursToAdd, lineSplit[1], lineSplit[0]);

				if (!currentDay.equals(lineSplit[0])) {
//				System.out.println(lineSplit[0]);
					currentDay = lineSplit[0];
					printResultToFile(currentUsers, writer);
					currentUsers.clear();
				}

				if (!currentUsers.contains(currentUser)) {
					currentUsers.add(currentUser);
				} else {
					// we need to check for each element of the list to add the hours correctly
					for (UserSpecificPrettifyHolder user : currentUsers) {
						if (user.equals(currentUser)) {
							user.addHours(hoursToAdd);
						}
					}

				}

//			System.out.println(line);
			}
			printResultToFile(currentUsers, writer);
		}
	}

	private static void printResultToFile(List<UserSpecificPrettifyHolder> currentUsers, Writer writer) throws IOException {
		for (UserSpecificPrettifyHolder user : currentUsers) {
			writer.write(user.getDate() + "\t" + user.getUser() + "\t" + user.getTotalHours() + "\n");
		}

	}
}
