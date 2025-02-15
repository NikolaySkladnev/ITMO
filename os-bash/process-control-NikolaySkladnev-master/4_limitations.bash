#!/usr/bin/bash

infinite() {
  while true; do
    result=$((10 * 10))
  done
}

infinite &   
PID1=$!
infinite &   
PID2=$!
infinite &   
PID3=$!

sleep 2

TOP_OUTPUT=$(top -b -n 1)

FIRST_PID=$(echo "$TOP_OUTPUT" | grep -E "($PID1|$PID2|$PID3)" | sort -k 9 -nr | head -n 1 | awk '{print $1}')
THIRD_PID=$(echo "$TOP_OUTPUT" | grep -E "($PID1|$PID2|$PID3)" | sort -k 9 -nr | tail -n 1 | awk '{print $1}')

kill "$THIRD_PID"

cpulimit -p "$FIRST_PID" -l 10 &
