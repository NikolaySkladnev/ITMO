#!/bin/bash 

man bash | grep -o -i -E "[a-zA-z]{4,}" | tr "[:upper:]" "[:lower:]" | sort | uniq -c | sort -n -r | awk '{print $2}' | head -3
