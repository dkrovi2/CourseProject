#!/bin/sh
if [ "$#" -ne "2" ]; then
  echo "Usage: run-scoring-engine.sh <location to write JSON files for scoring engine> <location of index>"
  exit 1
fi

input_location="$1"
output_location="$2"

echo "Running the scoring engine for JSON files at $input_location. The Lucene Index will be stored at $output_location"

java -cp build/libs/resume-finder-all.jar edu.illinois.phantom.analysisengine.ScoringEngine "$input_location" "$output_location"
