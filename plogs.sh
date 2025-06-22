#!/bin/bash

# Get the current timestamp in YYYY-MM-DD_HH:MM:SS format
timestamp=$(date +"%Y-%m-%d_%H-%M-%S")

# Run your JAR file and capture its output to a log file with timestamp
java -jar /home/stephan/Git/parseSLF4J/target/SLF4J-parser-1.0.0-jar-with-dependencies.jar | cut -f9,10 | grep app/web.1 | cut -f2 > "/home/stephan/Git/parseSLF4J/plogs-${timestamp}.txt" 2>&1 
