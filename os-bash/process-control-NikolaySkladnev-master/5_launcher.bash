#!/usr/bin/bash

if [[ -p pipe ]]; then
    rm -f pipe
fi

mkfifo pipe

./5_handler.bash &
handler_pid=$!

./5_producer.bash 

wait $handler_pid

rm -f pipe