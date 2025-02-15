#!/usr/bin/bash

CURRENT_TIME=$(date "+%Y-%m-%d_%H-%M-%S")
CURRENT_DATE=$(echo "$CURRENT_TIME" | awk -F'_' '{print $1}') 

TEST_DIR="$HOME/test"
ARCHIVED_DIR="$TEST_DIR/archived"

mkdir -p "$TEST_DIR"

touch "${TEST_DIR}/${CURRENT_TIME}"

echo "<$(echo "$CURRENT_TIME" | sed 's/_/:/')> test was created successfully" >> "$HOME/report"

FILES_TO_ARCHIVE=$(find "$TEST_DIR" -type f ! -newermt "$CURRENT_DATE")

if [ -n "$FILES_TO_ARCHIVE" ]; then
    mkdir -p "$ARCHIVED_DIR"

    tar -czf "${ARCHIVED_DIR}/${CURRENT_DATE}.tar.gz" $FILES_TO_ARCHIVE 

    echo "$FILES_TO_ARCHIVE" | xargs rm -f
    rm "${TEST_DIR}/${CURRENT_TIME}"
fi
