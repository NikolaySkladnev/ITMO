#!/bin/bash

> 4_cpu_burst.out

for pid in $(ps -Ao pid --no-headers)
do
	status="/proc/$pid/status"
    	sched="/proc/$pid/sched"

    	if [[ ! -f "$status" || ! -f "$sched" ]]; then
        	continue
    	fi

	ppid=$(awk '/PPid/ {print $2}' "$status")
    	sum_exec_runtime=$(awk '/sum_exec_runtime/ {print $3}' "$sched")
    	nr_switches=$(awk '/nr_switches/ {print $3}' "$sched")

	art=0
	if [[ $nr_switches -ne 0 ]]; then
		art=$(awk "BEGIN {print $sum_exec_runtime / $nr_switches}")
	fi

	echo "ProcessID="$pid" : Parent_ProcessID="$ppid" : Average_Running_Time="$art"" >> 4_cpu_burst.out
done

sort -t= -nk3 4_cpu_burst.out -o 4_cpu_burst.out


