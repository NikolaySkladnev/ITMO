#!/usr/bin/bash

if [[ -f .handler_pid ]]; then
    rm .handler_pid
fi

./6_handler.bash &
handler_pid=$!

echo $handler_pid > .handler_pid

./6_producer.bash

wait $handler_pid

rm .handler_pid

