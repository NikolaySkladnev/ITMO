#!/bin/bash

REPORT_LOG_PATH="$PWD/../logs/report2.log"

> "$REPORT_LOG_PATH"

array=()
counter=0

while true; do
    for i in {1..100}; do
        array+=("$i")    
    done

    counter=$((counter + 1))

    if ((counter % 100000 == 0)); then
        echo "Step: $counter, Array size: ${#array[@]}" >> "$REPORT_LOG_PATH"
    fi
done
