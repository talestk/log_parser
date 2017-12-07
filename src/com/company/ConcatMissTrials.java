package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/*
Email: abaker@tgen.org
(1 record)										(1 record)
Angela	Baker	Translational Genomics Research Institute	6023438828	Apr 20, 2017	Partek Genomics Suite	Other	Trial Request	AZ	United States	Partek Genomics Suite + Pathway	Macintosh	5/2/2017	5/15/2017	Evaluation
 */
public class ConcatMissTrials {
	public static void parse(String filePath) throws IOException, ParseException {
		System.out.println("Starting overall parser ...");
		// initialize variables
		List<String> allLines = ParserHelper.getAllLinesFromFile(filePath);
		String outputFileName = "output";
		// check for file name existence
		if (new File(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION).exists()) {
			outputFileName = outputFileName + "_" + System.currentTimeMillis();
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFileName + ParserHelper.OUTPUT_FILE_EXTENSION), "utf-8"))) {
			// print the header
			writer.write("Email\tFirst Name\tLast Name\tAccount Name\tPhone\tTrial Requested Date\tProduct Interest\tIndustry\tLead Source\tState/Province\tCountry\tAsset Name\tPlatform\tInstall Date\tUsage End Date\tLicense Term\tRecords\n");

			// this set will hold all the users on the log
			HashSet<Object> userSet = new HashSet<>();
			// this will have an object of all UserActions on the log
			List<UserActions> simpleUserActionsCounterList = new ArrayList<>();

			// lets loop through all the lines on the log
			for (int i = 0; i < allLines.size() / 3; i++) {
				if (i%3==0) {
					//make sure to parse the (x record), sometimes in ta new line sometimes at the end of the email
					// if new evaluations add to the end of the row
					System.out.println(allLines.get(i));
					System.out.println(allLines.get(i + 1));
					System.out.println(allLines.get(i + 2));
				}
			}
//			writeAndPrintOutput(writer, userSet, simpleUserActionsCounterList);
		}
	}
}
