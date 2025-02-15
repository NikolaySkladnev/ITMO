#!/usr/bin/bash

if [[ -p pipe ]]; then
    rm -f pipe
fi

mkfifo pipe

./modification_handler.bash &
handler_pid=$!

./modification_producer.bash 

wait $handler_pid

rm -f pipe