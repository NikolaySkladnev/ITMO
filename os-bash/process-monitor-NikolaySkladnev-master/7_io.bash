#!/bin/bash

> temp_io_1.data
> temp_io_2.data

for pid in $(ps -Ao pid --no-headers)
do
	file="/proc/$pid/io"

	if [[ ! -f "$file" ]]; then
		continue
	fi

	memory=$(awk '/read_bytes/ {print $2}' "$file" 2>/dev/null)

	if [[ -z "$memory" ]]; then
		continue
	fi

	cmd=$(ps -p $pid -o cmd=)
	echo "$pid:$cmd:$memory" >> temp_io_1.data
done

sleep 60

while read -r line
do
	pid=$(echo "$line" | awk -F: '{print $1}')
	cmd=$(echo "$line" | awk -F: '{print $2}')
	start_value=$(echo "$line" | awk -F: '{print $3}')

	file="/proc/$pid/io"

	if [[ ! -f "$file" ]]; then
		continue
	fi

	end_value=$(awk '/read_bytes/ {print $2}' "$file" 2>/dev/null)
        diff=$(echo "$start_value $end_value" | awk '{print $2 - $1}')

	echo "$pid:$cmd:$diff" >> temp_io_2.data

done < temp_io_1.data

sort -t: -nr -k3 temp_io_2.data | head -n 3

rm temp_io_1.data
rm temp_io_2.data

