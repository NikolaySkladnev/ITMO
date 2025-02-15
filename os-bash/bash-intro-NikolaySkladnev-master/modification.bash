#!/bin/bash

awk -F: '{
	check = substr($2,2,2)

	if (check == "41")
		course = 5
	else if (check == "42")
		course = 6
	else
		course = substr($2,3,1)

	printf "%s:%s\n",$3,course
}' table > courses.lst
