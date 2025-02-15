#!/bin/bash 

[[ "$PWD" = "$HOME"* ]] && echo "$HOME" || { echo "Error: current directory is not in home" >&2; exit 1; }
