#!/bin/bash

ps -Ao pid,stime | tail -n 6 | head -n 1 | awk '{print $1}'

