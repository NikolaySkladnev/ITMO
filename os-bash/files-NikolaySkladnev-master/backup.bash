#!/bin/bash

source_dir="$HOME/source"
date_now=$(date +%Y-%m-%d)
backup_dir="$HOME/Backup-$date_now"
backup_report="$HOME/backup-report"

active_backup=""
for dir in $(find "$HOME" -maxdepth 1 -type d -name "Backup-*"); do
    backup_date=$(basename "$dir" | awk -F'-' '{print $2"-"$3"-"$4}')
    if [[ $(($(date -d "$date_now" +%s) - $(date -d "$backup_date" +%s))) -le $((7 * 86400)) ]]; then
        active_backup="$dir"
        break
    fi
done

if [ -z "$active_backup" ]; then
    mkdir "$backup_dir" || { echo "Ошибка: не удалось создать каталог $backup_dir"; exit 1; }
    echo "Создан резервный каталог копирования: $backup_dir" >> "$backup_report"
    echo "Дата создания каталога: $date_now" >> "$backup_report"
    
    for file in "$source_dir"/*; do
        [ -f "$file" ] || continue  
        cp "$file" "$backup_dir" || { echo "Ошибка: не удалось скопировать $file"; exit 1; }
        echo "Скопирован файл: $(basename "$file")" >> "$backup_report"
    done
else
    echo "Найден резервный каталог копирования: $active_backup"
    echo "Изменения в каталоге $active_backup:" >> "$backup_report"
    
    for file in "$source_dir"/*; do
        [ -f "$file" ] || continue  
        file_name=$(basename "$file")
        old_file="$active_backup/$file_name"
        
        if [ -f "$old_file" ]; then
            source_size=$(stat --printf="%s" "$file")
            old_size=$(stat --printf="%s" "$old_file")
            if [ "$source_size" -ne "$old_size" ]; then
                mv "$old_file" "$active_backup/$file_name.$date_now" || { echo "Ошибка: не удалось переименовать $old_file"; exit 1; }
                cp "$file" "$old_file" || { echo "Ошибка: не удалось скопировать $file"; exit 1; }
                echo "Версионная копия: $file_name -> $file_name.$date_now" >> "$backup_report"
                echo "Обновлен файл: $file_name" >> "$backup_report"
            fi
        else
            cp "$file" "$old_file" || { echo "Ошибка: не удалось скопировать $file"; exit 1; }
            echo "Добавлен файл: $file_name" >> "$backup_report"
        fi
    done
fi

echo "Резервное копирование завершено."
