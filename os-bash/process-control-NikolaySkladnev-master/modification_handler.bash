#!/bin/bash

STACK=()
producer_pid=$(pgrep -f modification_producer.bash)

pop_two_elements() {
    if [ ${#STACK[@]} -lt 2 ]; then
        echo "Error: недостаточно аргументов для операции "$1""
        return 1
    fi
    a=${STACK[-1]}
    unset STACK[-1]
    b=${STACK[-1]}
    unset STACK[-1]
    return 0
}

pop_one_element() {
    if [ ${#STACK[@]} -lt 1 ]; then
        echo "Error: недостаточно аргументов для операции "$1""
        return 1
    fi
    a=${STACK[-1]}
    unset STACK[-1]
    return 0
}

process_operation() {
    MODE=$1
    case "$MODE" in
        "+")
            pop_two_elements || return
            STACK+=($(($a + $b)))
            ;;
        "-")
            pop_two_elements || return
            STACK+=($(($a - $b)))
            ;;
        "/")
            if [ "${STACK[-2]}" -eq 0 ]; then
                echo "Error: деление на ноль"
                return
            fi
            pop_two_elements || return
            STACK+=($(($a / $b)))
            ;;
        "*")
            pop_two_elements || return
            STACK+=($(($a * $b)))
            ;;
        "max2")
            pop_two_elements || return
            STACK+=($((a > b ? a : b)))
            ;;
        "log10")
            if [ "${STACK[-1]}" -le 0 ]; then
                echo "Error: значение для log10 должно быть больше 0"
                return
            fi
            pop_one_element || return
            result=$(echo "scale=2; l($a)/l(10)" | bc -l 2>/dev/null)
            STACK+=($result)
            ;;
        "set")
            pop_two_elements || return
            result=$((a | (1 << b)))
            STACK+=($result)
            ;;
        *)
            echo "Error: неизвестная операция '$MODE'"
            ;;
    esac
}

while read LINE < pipe; do
    case "$LINE" in
        "QUIT")
            kill "$producer_pid"
            echo "Работа завершенна."
            break
            ;;
        *)
            if [[ "$LINE" =~ ^-?[0-9]+$ ]]; then
                if [ ${#STACK[@]} -ge 5 ]; then
                    STACK=("${STACK[@]:1}")
                fi
                STACK+=("$LINE")
            else
                process_operation "$LINE"
            fi
            ;;
    esac
    echo "Текущий стек: ${STACK[@]}"
done
