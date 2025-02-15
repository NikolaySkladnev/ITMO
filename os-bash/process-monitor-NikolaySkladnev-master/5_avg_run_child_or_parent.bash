#!/bin/bash

last_ppid=""
sum=0
count=0

> 5_avg_run_child_or_parent.out

while read -r line
do

	pid=$(echo $line | sed "s/ : /=/g" | awk -F= '{print $2}')
	ppid=$(echo $line | sed "s/ : /=/g" | awk -F= '{print $4}')
	art=$(echo $line | sed "s/ : /=/g" | awk -F= '{print $6}')

	if [[ "$last_ppid" != "$ppid" && "$last_ppid" != "" ]]
	then
		avg_art=$(awk "BEGIN {print $sum / $count}")
		echo "Average_Running_Children_of_ParentID="$last_ppid" is "$avg_art"" >> 5_avg_run_child_or_parent.out
		echo "" >> 5_avg_run_child_or_parent.out

		sum=$art
		count=1
		last_ppid=$ppid

		echo "$line" >> 5_avg_run_child_or_parent.out
		continue
	fi

	sum=$(awk "BEGIN {print $sum + $art}")
    	count=$((count + 1))
	last_ppid=$ppid

	echo "$line" >> 5_avg_run_child_or_parent.out

done < 4_cpu_burst.out

avg_art=$(awk "BEGIN {print $sum / $count}")
echo "Average_Running_Children_of_ParentID="$last_ppid" is "$avg_art"" >> 5_avg_run_child_or_parent.out
