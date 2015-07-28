package com.company;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class Main {

	public static void main(String[] args) throws IOException, ParseException {
		if (args.length < 2) {
			System.out.println("Usage: log_parser <command> </path/to/logfile>");
			System.out.println();
			System.out.println("<commands>");
			System.out.println("\t-a\tOverall daily parser, will print a daily resume");
			System.out.println("\t-u\tUser specific parser, will print a resume based on user usage");
			System.out.println("\t-fo\tParse output files provided by this program");
			System.out.println("\t-fa\tRead all files that starts with the given pattern, overall parser");
			System.out.println("\t-fu\tRead all files that starts with the given pattern, user specific parser");
			return;
		}

		System.out.printf("Starting parse for ");
		if (args[0].equals("-fa")) {
			System.out.println("files that starts with: "+args[1]);
			File tempFile = ParserHelper.concatAllFilesWhichStartsWith(new File("."), args[1]);
			if (tempFile.exists()) {
				DayByDayParser.parse(tempFile.getAbsolutePath());
				tempFile.delete();
			}
		}

		if (args[0].equals("-fu")) {
			System.out.println("files that starts with: "+args[1]);
			File tempFile = ParserHelper.concatAllFilesWhichStartsWith(new File("."), args[1]);
			if (tempFile.exists()) {
				UserSpecificParser.parse(tempFile.getAbsolutePath());
				tempFile.delete();
			}
		}

		if (args[0].equals("-a")) {
			System.out.println("file: "+args[1]);
			DayByDayParser.parse(args[1]);
		}

		if (args[0].equals("-u")) {
			System.out.println("file: "+args[1]);
			UserSpecificParser.parse(args[1]);
		}

		if (args[0].equals("-fo")) {
			System.out.println("file: "+args[1]);
			UserSpecificParser.parse(args[1]);
		}

	}
}
