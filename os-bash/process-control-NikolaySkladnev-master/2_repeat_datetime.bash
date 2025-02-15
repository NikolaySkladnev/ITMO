#!/usr/bin/bash

echo "$PWD/1_datetime.bash" | at now + 2 minutes &

tail -n 0 -f "$HOME/report" &


