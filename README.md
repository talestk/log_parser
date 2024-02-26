# log_parser
To run this parser you MUST have java installed on your computer and run this from a terminal.
Run the command:
```
$ java -jar log_parser.jar <command> /path/to/log/file
```
```
Usage: log_parser <command> </path/to/logfile>

<commands>
	-a	Overall daily parser, will print a daily resume
	-u	User specific parser, will print a resume based on user usage
	-s	Simple user actions
	-d	<months>	Define the number of months to parse. eg. -d 12
	-fo	Parse output files provided by this program
	-fa	Read all files that starts with the given pattern, overall parser
	-fu	Read all files that starts with the given pattern, user specific parser
```
A tab delimited file -output.csv- will be created and the program will output a resume of the run, which looks like:
```
========== Totals ==========
 weekday checkouts: 464
 weekday denies: 671
 weekdays: 31
 weekend checkouts: 54
 weekend denies: 0
 weekend days: 12
 days on log: 43
============================
========= Averages =========
 weekday checkouts: 15.0
 weekday denied: 21.6
 weekend checkouts: 4.5
 weekend denies: 0.0
============================
```
You can also run from the tests if managed to get the project correctly setup on your prefferred IDE