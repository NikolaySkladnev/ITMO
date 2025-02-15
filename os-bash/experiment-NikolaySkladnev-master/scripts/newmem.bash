#!/bin/bash

N=$1
count=0
arr=()

while [[ $count -le $N ]]; do
  arr+=(10)
  ((count++))
done

echo "done"
