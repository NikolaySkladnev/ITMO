#!/bin/bash

ps_data_collect() {
  ps -eo stat,uid,rss,command --no-headers | grep -E "^[SR]" | awk '{ print $2 "|" $3 "|" $4 }'
}

ps_data=()

for i in {1..3}; do
  ps_data+=("$(ps_data_collect)")
  sleep 60
done

declare -A memory
declare -A count

for data_page in "${ps_data[@]}"; do
    while IFS="|" read -r uid rss command; do

        key="$uid|$command"
        
        memory["$key"]=$(( ${memory["$key"]} + rss ))
        count["$key"]=$(( ${count["$key"]} + 1 ))

    done <<< "$data_page"
done

current_ps_data="$(ps_data_collect)"
output_count=0

while IFS="|" read -r uid rss command; do

    key="$uid|$command"

    if ! echo "$current_ps_data" | fgrep -q "$uid" | fgrep -q "$command"; then
        output_count=$((output_count + 1))

        avg_memory=$(( ${memory["$key"]} * 1024 / 3 ))
        echo "UID: $uid, Command: $command, Avg Memory: $avg_memory bytes"

        if [[ $output_count -eq 5 ]]; then
            break
        fi
    fi
done <<< "${ps_data[0]}"