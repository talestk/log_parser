package com.company;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Usage: log_parser <command> </path/to/logfile>");
			System.out.println();
			System.out.println("<commands>");
			System.out.println("\t-o\tOverall daily parser, will print a daily resume");
			System.out.println("\t-u\tUser specific parser, will print a resume based on user usage");
			return;
		}

		if (args[0].equals("-o")) {
			DayByDayParser.parse(args[1]);
		}

		if (args[0].equals("-u")) {
			UserSpecificParser.parse(args[1]);
		}

	}
}
