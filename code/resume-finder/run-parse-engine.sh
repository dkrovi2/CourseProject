#!/bin/sh
if [ "$#" -ne "2" ]; then
  echo "Usage: run-parse-engine.sh <location of resumes> <location to write JSON files for scoring engine>"
  exit 1
fi

input_location="$1"
output_location="$2"

echo "Running the parse engine for resumes at $input_location. The JSON files for scoring engine will be available at $output_location"

java -cp build/libs/resume-finder-all.jar edu.illinois.phantom.parseengine.Main "$input_location" "$output_location"
