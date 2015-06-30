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
//			System.out.println("\t-f\tRead all files that starts with the given pattern");
			return;
		}

		// TODO: read many files on a single run
		if (args[0].equals("-f")) {
			ParserHelper.listFilesForFolder(new File("."), args[1]);
		}

		if (args[0].equals("-a")) {
			DayByDayParser.parse(args[1]);
		}

		if (args[0].equals("-u")) {
			UserSpecificParser.parse(args[1]);
		}

	}
}
