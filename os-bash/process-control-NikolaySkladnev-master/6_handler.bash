#!/usr/bin/bash

RESULT=1
MODE="add"  

add() {
    RESULT=$((RESULT + 2))
    echo "Result: $RESULT"
}

mul() {
    RESULT=$((RESULT * 2))
    echo "Result: $RESULT"
}

terminate() {
    echo "Job finished."
	rm -f handler_pid
    exit 0
}

trap add USR1
trap mul USR2
trap terminate SIGTERM

while true; do
    sleep 1  
done
