#!/bin/bash

ps -Ao pid,command | grep -E "[0-9]+ /sbin" | awk '{print $1}' > 2_from_sbin.out
