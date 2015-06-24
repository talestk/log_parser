# log_parser
To run this parser you MUST have java installed on your computer and run this from a terminal.
Run the command:
```
$ java -jar log_parser.jar <command> /path/to/log/file
```
```
Usage: log_parser <command> </path/to/logfile>

<commands>
	-o	Overall daily parser, will print a daily resume
	-u	User specific parser, will print a resume based on user usage
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
 weekday checkouts: 14
 weekday denied: 21
 weekend checkouts: 4
 weekend denies: 0
============================
```
