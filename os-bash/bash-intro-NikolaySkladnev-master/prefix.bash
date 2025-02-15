#!/bin/bash

sum=0

for ((i=$1; i <= $2; i++))
do
	let sum=$sum+$i
	echo "$sum"
done
