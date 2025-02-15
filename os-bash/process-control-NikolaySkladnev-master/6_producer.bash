#!/usr/bin/bash

LINE=""

while true; do
    read LINE
    case "$LINE" in
        "+")
            kill -USR1 $(cat .handler_pid)
            ;;
        "*")
            kill -USR2 $(cat .handler_pid)
            ;;
        "TERM")
            kill -SIGTERM $(cat .handler_pid)
            break
            ;;
        *)
            ;;
    esac
done
