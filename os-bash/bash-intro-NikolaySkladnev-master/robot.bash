#!/bin/bash

let x=$1/2
let y=$2/2

echo "x=$x;y=$y"


while read -n 1 line; [[ "$line" != "q" ]] && [[ $x -le $1 ]] && [[ $y -le $2 ]]
do

case $line in
	"w" | "W")
		let y=$y+1
	;;
	"a" | "A")
		let x=$x-1
	;;
	"s" | "S")
		let y=$y-1
	;;
	"d" | "D")
		let x=$x+1
	;;
esac

echo "x=$x;y=$y"

done
