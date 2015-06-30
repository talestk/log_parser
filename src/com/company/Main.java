package com.company;

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
			return;
		}

		if (args[0].equals("-a")) {
			DayByDayParser.parse(args[1]);
		}

		if (args[0].equals("-u")) {
			UserSpecificParser.parse(args[1]);
		}

	}
}
