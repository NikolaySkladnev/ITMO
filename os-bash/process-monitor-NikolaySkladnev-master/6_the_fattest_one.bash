#!/bin/bash

max_memory=0
max_pid=0

for pid in $(ps -Ao pid --no-headers)
do
	file="/proc/"$pid"/status"

	if [[ ! -f "$file" ]]; then
		continue
	fi

	curr_memory=$(grep "VmRSS" "$file" | awk -F: '{print $2}' | sed 's/ kB//; s/ //g')

	if [[ "$curr_memory" -gt "$max_memory" ]]
	then
		max_memory=$curr_memory
		max_pid=$pid
	fi
done

echo "PID: "$max_pid" memory:"$max_memory" kB"
top -b -n 1 -o RES | head -n 8 | tail -n 1 | awk '{print "PID: "$1" memory: "$6" kB -top"}'
