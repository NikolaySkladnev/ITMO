#!/usr/bin/bash

(crontab -l 2>/dev/null; echo "5 * * * 5 bash $PWD/1_datetime.bash") | crontab -

