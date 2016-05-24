package com.company;

import java.io.*;
import java.text.ParseException;
import java.util.*;

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
public class SimpleUserActionsCounter {
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
            writer.write("Date\tCheckout\tCheckins\tDenies\tUnsupported\n");

            // this set will hold all the users on the log
            HashSet<Object> userSet = new HashSet<>();
            // this will have an object of all UserActions on the log
            List<UserActions> simpleUserActionsCounterList = new ArrayList<>();

            // lets loop through all the lines on the log
            for (String line : allLines) {
                parseLine(userSet, simpleUserActionsCounterList, line);
            }
            writeAndPrintOutput(writer, userSet, simpleUserActionsCounterList);


        }
    }

    private static void parseLine(HashSet<Object> userSet, List<UserActions> simpleUserActionsCounterList, String string) {
        string = string.trim();

        // lets find only the lines we are interested on
        if (string.contains("parteklm") &&
                (string.contains("\"base\"") || string.contains("\"infr\"") || string.contains("\"pathway_base\"") || string.contains("\"matk\""))) {
            String[] user = string.split(" ");

            // 10:41:30 (parteklm) OUT: "base" tangw3@NCI-02034032-ML
            // the user is the fourth piece after splitting
            String newUser = user[4];

            // lets make sure it is a user
            if (newUser.contains("@")) {
                userSet.add(newUser);
                simpleUserActionsCounterList.add(new UserActions(user[2].trim().replace(":", ""), newUser.trim()));
            } else if (string.contains("UNSUPPORTED")){
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

    private static void writeAndPrintOutput(Writer writer, HashSet<Object> userSet, List<UserActions> simpleUserActionsCounterList) throws IOException {
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
