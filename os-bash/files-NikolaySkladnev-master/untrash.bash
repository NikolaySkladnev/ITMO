#!/bin/bash

if [ "$#" -lt 1 ]; then
    echo "Ошибка: необходимо указать имя файла для восстановления."
    exit 1
fi

flag_ignore=false
flag_unique=false
flag_overwrite=false

flags_used=false  

while [[ "$1" =~ ^- ]]; do
    flags_used=true
    if [[ "$1" == --* ]]; then
        case "$1" in
            --ignore)
                flag_ignore=true
                ;;
            --unique)
                flag_unique=true
                ;;
            --overwrite)
                flag_overwrite=true
                ;;
            *)
                echo "Ошибка: неизвестный флаг '$1'."
                exit 1
                ;;
        esac
    else
        for (( i=1; i<${#1}; i++ )); do
            case "${1:$i:1}" in
                i)
                    flag_ignore=true
                    ;;
                u)
                    flag_unique=true
                    ;;
                o)
                    flag_overwrite=true
                    ;;
                *)
                    echo "Ошибка: неизвестный флаг '${1:$i:1}'."
                    exit 1
                    ;;
            esac
        done
    fi
    shift
done

if [ "$flags_used" = false ]; then
    flag_ignore=true
fi

filename="$1"
if [ -z "$filename" ]; then
    echo "Ошибка: имя файла не указано."
    exit 1
fi

trash_log="$HOME/.trash.log"
if [ ! -f "$trash_log" ]; then
    echo "Ошибка: файл $trash_log не найден."
    exit 1
fi

files_to_restore=$(grep -v '^$' "$trash_log" | grep -E "/[^/]*$filename[^/]*:")
if [ -z "$files_to_restore" ]; then
    echo "Ошибка: файл $filename не найден в корзине."
    exit 1
fi

if [ "$flag_ignore" = true ]; then
    echo "Найдены следующие файлы для восстановления (режим -i, восстановление пропущено):"
    echo "$files_to_restore"
    exit 0
fi

echo "Найдены следующие файлы для восстановления:"
echo "$files_to_restore"

generate_unique_name() {
    local base_name="$1"
    local target_dir="$2"
    
    if [[ "$base_name" == *.* ]]; then
        local extension="${base_name##*.}"
        local name="${base_name%.*}"
    else
        local extension="" 
        local name="$base_name"
    fi

    version=$(echo "$name" | grep -oP '\(\d+\)' | grep -oP '\d+')

    if [ -z "$version" ]; then
        base="$name"
    else
        base="${name%%\(*}"  
    fi

    count=1
    
    if [ -z "$extension" ]; then
        new_name="${target_dir}/${base}(${count})"
    else
        new_name="${target_dir}/${base}(${count}).${extension}"
    fi

    while [ -f "$new_name" ]; do
        
        count=$((count + 1))

        if [ -z "$extension" ]; then
            new_name="${target_dir}/${base}(${count})"
        else
            new_name="${target_dir}/${base}(${count}).${extension}"
        fi
    
    done

    echo "$new_name"
}


for file in $files_to_restore; do
    full_path=$(echo "$file" | awk -F":" '{print $1}')
    trash_path=$(echo "$file" | awk -F":" '{print $2}')

    echo -n "Восстановить $full_path (y/n)? "
    read -r response

    if [[ "$response" =~ ^[Yy]$ ]]; then
        target_dir=$(dirname "$full_path")

        if [ ! -d "$target_dir" ]; then
            echo "Каталог $target_dir не существует. Восстановление в домашний каталог..."
            target_dir="$HOME"
            full_path="$target_dir/$(basename "$full_path")"
        fi

        new_name="$full_path"

        if [ "$flag_overwrite" = true ]; then
            rm -f "$full_path"
            echo "Файл $full_path был перезаписан."
        fi

        if [ "$flag_unique" = true ]; then
            new_name=$(generate_unique_name "$(basename "$full_path")" "$target_dir")
            echo "Восстановление как $new_name"
        fi

        ln "$trash_path" "$new_name"
        rm "$trash_path"
        echo "Файл $trash_path восстановлен и удален из корзины."
        sed -i "\|$full_path:$trash_path|d" "$trash_log"
    fi
done
