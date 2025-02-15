#!/bin/bash

ps -eu "$USER" --no-headers | wc -l > 1_users_processes.out
ps -eu "$USER" -o pid,command --no-headers | sed "s/^[[:space:]]*//; s/ /:/" >> 1_users_processes.out
