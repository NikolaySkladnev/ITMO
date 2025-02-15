#!/bin/bash

while read -r line; [[ "$line" != "q" ]]
do
	echo "${#line}"

	if [[ "$line" =~ ^[a-zA-Z]+$ ]]; then
		echo "true"
	else
		echo "false"
	fi
done
