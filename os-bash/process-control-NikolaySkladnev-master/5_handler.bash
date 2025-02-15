#!/usr/bin/bash

RESULT=1
MODE="+"  
producer_pid=$(pgrep -f 5_producer.bash)

while read LINE < pipe; do
    case "$LINE" in
        "+")
            MODE="+"
            echo "Mode: add."
            ;;
        "*")
            MODE="*"
            echo "Mode: multiply."
            ;;
        "QUIT")
            kill "$producer_pid"
            echo "Job finished." 
            break
            ;;
        *)
            if [[ $LINE =~ ^-?[0-9]+$ ]]; then
                if [[ $MODE == "+" ]]; then
                    RESULT=$((RESULT + LINE))
                    echo "Result: $RESULT."
                elif [[ $MODE == "*" ]]; then
                    RESULT=$((RESULT * LINE))
                    echo "Result: $RESULT."
                fi
            else
                kill "$producer_pid" 
                echo "Invalid input: $LINE"
				echo "Job finished."
                exit 1
            fi
            ;;
    esac
done
